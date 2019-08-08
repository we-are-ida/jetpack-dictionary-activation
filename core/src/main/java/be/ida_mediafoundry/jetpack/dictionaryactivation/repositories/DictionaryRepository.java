package be.ida_mediafoundry.jetpack.dictionaryactivation.repositories;

import org.apache.sling.api.resource.Resource;

import java.util.Iterator;

/**
 * Repository For accessing the Dictionary on repository level.
 */
public interface DictionaryRepository {

    /**
     * Get dictionaries.
     *
     * @return Iterator of resources.
     * @throws Exception
     */
    Iterator<Resource> getDictionaries();

}
