package be.ida.jetpack.dictionaryactivation.models;

import com.day.cq.commons.date.RelativeTimeFormat;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.Self;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.*;

/**
 * Sling model that converts dictionary resource into a dictionary object.
 */
@Model(adaptables = SlingHttpServletRequest.class)
public class Dictionary {

    @Self
    private SlingHttpServletRequest request;

    @Inject
    private Resource resource;

    private String title;
    private String description;

    private int numberOfTranslationKeys;
    private int numberOfNewTranslations;

    private String replicationDate;
    private String replicationBy;

    private String path;

    @PostConstruct
    private void init() {
        title = resource.getPath();
        path = resource.getPath();

        ValueMap properties = resource.getValueMap();

        Calendar lastReplicated = properties.get("cq:lastReplicated", Calendar.class);
        replicationDate = formatDateRDF(lastReplicated, null);
        replicationBy = properties.get("cq:lastReplicatedBy", String.class);

        Set<String> uniqueKeys = new HashSet<>();
        numberOfNewTranslations = 0;
        List<String> languageCodes = new ArrayList<>();

        Iterator<Resource> languages = resource.listChildren();

        while (languages.hasNext()) {
            Resource language = languages.next();

            String languageCode = language.getValueMap().get("jcr:language", String.class);
            languageCodes.add(languageCode);

            Iterator<Resource> dictionaryKeys = language.listChildren();
            while (dictionaryKeys.hasNext()) {
                Resource dictionaryKey = dictionaryKeys.next();

                //unique keys
                String key = dictionaryKey.getValueMap().get("sling:key", String.class);
                if (key == null) {
                    key = dictionaryKey.getName();
                }
                uniqueKeys.add(key);

                //Created
                Calendar created = dictionaryKey.getValueMap().get("jcr:created", Calendar.class);

                if (created != null && lastReplicated != null) {
                    boolean isCreatedAfterPublish = lastReplicated.before(created);
                    if (isCreatedAfterPublish) {
                        numberOfNewTranslations++;
                    }
                } else if (lastReplicated == null) {
                    numberOfNewTranslations++;
                }
            }
        }

        numberOfTranslationKeys = uniqueKeys.size();

        if (languageCodes.size() < 10) {
            description = String.join(", ", languageCodes);
        } else {
            description = languageCodes.size() + " languages";
        }
    }

    public String getTitle() {
        return title;
    }

    public String getThumbnail() {
        return request.getContextPath() + getThumbnailUrl();
    }

    public String getPath() {
        return path;
    }

    public int getNumberOfTranslationKeys() {
        return numberOfTranslationKeys;
    }

    public int getNumberOfNewTranslations() {
        return numberOfNewTranslations;
    }

    public String getDescription() {
        return description;
    }

    public String getReplicationDate() {
        return replicationDate;
    }

    public String getReplicationBy() {
        return replicationBy;
    }

    private String formatDateRDF(Calendar cal, String defaultValue) {
        if (cal == null) {
            return defaultValue;
        }
        RelativeTimeFormat rtf = new RelativeTimeFormat("r");
        return rtf.format(cal.getTimeInMillis(), true);
    }

    private String getThumbnailUrl() {
        return "/apps/jetpack/dictionary-activation/components/thumb.png";
    }
}
