package be.ida.jetpack.dictionaryactivation.models;

import com.day.cq.commons.date.RelativeTimeFormat;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

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

    @ValueMapValue(name = "cq:lastReplicated", optional = true)
    private Calendar lastReplicated;

    @ValueMapValue(name = "cq:lastReplicatedBy", optional = true)
    private String lastReplicationBy;

    private String path;
    private String description;
    private int numberOfTranslationKeys;
    private int numberOfNewTranslations;

    @PostConstruct
    private void init() {
        path = resource.getPath();

        numberOfNewTranslations = 0;
        Set<String> translationKeys = new HashSet<>();
        List<String> languageCodes = new ArrayList<>();

        resource.listChildren().forEachRemaining(languageResource -> {
            addLanguageCode(languageCodes, languageResource);
            languageResource.listChildren().forEachRemaining(dictionaryKeyResource -> {
                addTranslationKeys(translationKeys, dictionaryKeyResource);
                if(isNewTranslationSinceLastReplication(dictionaryKeyResource)){
                    numberOfNewTranslations++;
                }
            });
        });

        numberOfTranslationKeys = translationKeys.size();
        setDescription(languageCodes);
    }

    private void setDescription(List<String> languageCodes) {
        if (languageCodes.size() < 10) {
            description = String.join(", ", languageCodes);
        } else {
            description = languageCodes.size() + " languages";
        }
    }

    private boolean isNewTranslationSinceLastReplication(Resource dictionaryKeyResource) {
        Calendar created = dictionaryKeyResource.getValueMap().get("jcr:created", Calendar.class);
        return lastReplicated == null || lastReplicated.before(created);
    }

    private void addLanguageCode(List<String> languageCodes, Resource languageResource) {
        String languageCode = languageResource.getValueMap().get("jcr:language", String.class);
        languageCodes.add(languageCode);
    }

    private void addTranslationKeys(Set<String> uniqueKeys, Resource dictionaryKey) {
        String key = dictionaryKey.getValueMap().get("sling:key", String.class);
        if (key == null) {
            key = dictionaryKey.getName();
        }
        uniqueKeys.add(key);
    }

    public String getTitle() {
        return getPath();
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
        return formatDateRDF(lastReplicated, null);
    }

    public String getReplicationBy() {
        return lastReplicationBy;
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
