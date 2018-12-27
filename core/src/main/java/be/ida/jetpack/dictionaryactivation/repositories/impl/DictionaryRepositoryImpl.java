package be.ida.jetpack.dictionaryactivation.repositories.impl;

import be.ida.jetpack.dictionaryactivation.repositories.DictionaryRepository;
import org.apache.log4j.Logger;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.osgi.service.component.annotations.Component;

import javax.jcr.query.Query;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

@Component(
        name = "Jetpack - Dictionary Repository",
        service = DictionaryRepository.class
)
public class DictionaryRepositoryImpl implements DictionaryRepository {

    private static final Logger LOG = Logger.getLogger(DictionaryRepositoryImpl.class);
    private static final String QUERY = "//element(*, mix:language)[@jcr:language]";

    @Override
    public Iterator<Resource> getDictionaries(ResourceResolver resourceResolver) {
        Map<String, Resource> dictionaryMap = new HashMap<>();

        Iterator<Resource> childRes = resourceResolver.findResources(QUERY, Query.XPATH);

        while (childRes.hasNext()) {
            try {
                Resource resource = childRes.next();
                if (resource.getParent() != null) {
                    dictionaryMap.put(resource.getParent().getPath(), resource.getParent());
                }
            } catch (Exception e) {
                LOG.error("Could not resolve to dictionary node", e);
            }
        }

        return dictionaryMap.values().iterator();
    }
}
