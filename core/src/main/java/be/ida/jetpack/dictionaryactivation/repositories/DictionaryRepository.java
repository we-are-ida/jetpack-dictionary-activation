package be.ida.jetpack.dictionaryactivation.repositories;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;

import java.util.Iterator;

/**
 * Repository For accessing the Dictionary on repository level.
 */
public interface DictionaryRepository {

    /**
     * Get dictionaries.
     *
     * @param resourceResolver resource resolver for logged in user.
     * @return Iterator of resources.
     * @throws Exception
     */
    Iterator<Resource> getDictionaries(ResourceResolver resourceResolver);

}
