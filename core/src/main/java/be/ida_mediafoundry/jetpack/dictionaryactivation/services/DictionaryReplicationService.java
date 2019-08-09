package be.ida_mediafoundry.jetpack.dictionaryactivation.services;

/**
 * Dictionary service from mainly activated a dictionary using package replication
 */
public interface DictionaryReplicationService {

    /**
     * Publish Dictionary on the provided path.
     *
     * A package will be created and replicated to get the dictionary activated.
     * If successful, the dictionary will be updates with the replication information.
     *
     *
     * @param path Path of the dictionary
     * @return true if successful, false otherwise
     */
    boolean replicateDictionary(String path);
}
