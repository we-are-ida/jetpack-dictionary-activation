package be.ida.jetpack.dictionaryactivation.repositories.impl;

import be.ida.jetpack.dictionaryactivation.repositories.DictionaryRepository;
import com.day.cq.wcm.commons.ResourceIterator;
import org.apache.commons.collections.IteratorUtils;
import org.apache.log4j.Logger;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import javax.jcr.query.Query;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

@Component(
        name = "Jetpack - Dictionary Repository",
        service = DictionaryRepository.class
)
public class DictionaryRepositoryImpl implements DictionaryRepository {

    private static final Logger LOG = Logger.getLogger(DictionaryRepositoryImpl.class);
    private static final String QUERY = "select * from [mix:language] as a where [jcr:language] is not null";

    @Reference
    private ResourceResolverFactory resourceResolverFactory;

    @Override
    public Iterator<Resource> getDictionaries() {
        Map<String, Resource> dictionaryMap = new HashMap<>();
        Iterator<Resource> childRes = getLanguageResources();
        while (childRes.hasNext()) {
            Resource resource = childRes.next();
            if (resource.getParent() != null) {
                dictionaryMap.put(resource.getParent().getPath(), resource.getParent());
            }
        }
        return dictionaryMap.values().iterator();
    }

    public Iterator<Resource> getLanguageResources() {
        try (ResourceResolver resourceresolver = resourceResolverFactory.getResourceResolver(null)) {
            return resourceresolver.findResources(QUERY, Query.JCR_SQL2);
        } catch (LoginException e) {
            e.printStackTrace();
            return IteratorUtils.emptyIterator();
        }
    }

}
