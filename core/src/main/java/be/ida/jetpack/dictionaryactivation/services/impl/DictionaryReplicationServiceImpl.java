package be.ida.jetpack.dictionaryactivation.services.impl;

import be.ida.jetpack.dictionaryactivation.services.DictionaryReplicationService;
import com.day.cq.replication.ReplicationActionType;
import com.day.cq.replication.ReplicationException;
import com.day.cq.replication.ReplicationOptions;
import com.day.cq.replication.Replicator;
import org.apache.commons.lang3.StringUtils;
import org.apache.jackrabbit.vault.fs.api.PathFilterSet;
import org.apache.jackrabbit.vault.fs.config.DefaultWorkspaceFilter;
import org.apache.jackrabbit.vault.packaging.*;
import org.apache.jackrabbit.vault.util.DefaultProgressListener;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import java.io.IOException;
import java.util.*;

/**
 * Dictionary service from mainly activated a dictionary using package replication
 */
@Component(
        name = "Jetpack - Dictionary Service",
        service = DictionaryReplicationService.class
)
public class DictionaryReplicationServiceImpl implements DictionaryReplicationService {

    private static final Logger LOG = LoggerFactory.getLogger(DictionaryReplicationServiceImpl.class);

    private static final String GROUP = "Jetpack-Dictionaries";

    @Reference
    private Replicator replicator;

    @Reference
    private ResourceResolverFactory resourceResolverFactory;

    @Override
    public boolean replicateDictionary(String path) {
        ResourceResolver resourceResolver = resourceResolverFactory.getThreadResourceResolver();
        Session session = resourceResolver.adaptTo(Session.class);
        JcrPackageManager packageManager = PackagingService.getPackageManager(session);

        try {
            Resource dictionary = resourceResolver.resolve(path);
            String name = getDictionaryName(path);

            //create package
            JcrPackage contentPackage = createOrGetContentPackage(session, packageManager, dictionary, name);

            //replicate package
            replicatePackage(session, contentPackage);

            //update last replicated property on dictionary level
            updatePublishStateDictionary(dictionary);
        } catch (Exception e) {
            LOG.error("Error when publishing dictionary", e);
            return false;
        }

        return true;
    }

    /**
     * Add replication information on a dictionary level
     *
     * @param dictionary dictionary to update
     * @throws Exception
     */
    private void updatePublishStateDictionary(Resource dictionary) throws Exception {
        Node node = dictionary.adaptTo(Node.class);
        node.addMixin("cq:ReplicationStatus");
        node.setProperty("cq:lastReplicated", Calendar.getInstance());
        node.setProperty("cq:lastReplicatedBy", node.getSession().getUserID());
        node.setProperty("cq:lastReplicationAction", "Activate");

        node.getSession().save();
    }

    /**
     * Build the dictionary name from the path, to use as package name.
     *
     * @param path dictionary paths name
     * @return formatted dictionary name
     */
    private String getDictionaryName(String path) {
        String name = path.replace("/libs/", StringUtils.EMPTY)
                          .replace("/apps/", StringUtils.EMPTY)
                          .replace("/i18n", StringUtils.EMPTY)
                          .replace("/", "-");
        return name.concat("-dictionary");
    }

    /**
     * Create a new package oor get the existing package.
     *
     * @param session session
     * @param jcrPackageManager package manager
     * @param resourceToPackage resource that need to be included in the package.
     * @param name name of the package
     * @return
     * @throws IOException
     * @throws RepositoryException
     * @throws PackageException
     */
    private JcrPackage createOrGetContentPackage(Session session, JcrPackageManager jcrPackageManager, Resource resourceToPackage, String name) throws IOException, RepositoryException, PackageException {
        //create content package
        JcrPackage jcrPackage = jcrPackageManager.open(new PackageId(GROUP, name, (String)null));
        if (jcrPackage == null) {
            jcrPackage = jcrPackageManager.create(GROUP, name, null);
        }
        final JcrPackageDefinition jcrPackageDefinition = jcrPackage.getDefinition();

        final DefaultWorkspaceFilter workspaceFilter = new DefaultWorkspaceFilter();
        workspaceFilter.getFilterSets().clear();
        workspaceFilter.add(new PathFilterSet(resourceToPackage.getPath()));

        if (jcrPackageDefinition != null) {
            jcrPackageDefinition.setFilter(workspaceFilter, true);
        }

        session.save();

        jcrPackageManager.assemble(jcrPackage, new DefaultProgressListener());
        return jcrPackage;

    }

    /**
     * Replicate the package.
     *
     * @param session session
     * @param contentPackage package
     * @throws ReplicationException exception in case of replicating
     * @throws RepositoryException exception in case of Repository access
     */
    private void replicatePackage(Session session, JcrPackage contentPackage) throws ReplicationException, RepositoryException {
        ReplicationOptions replicationOptions = new ReplicationOptions();
        replicationOptions.setSynchronous(true);
        replicationOptions.setUpdateAlias(true);

        replicator.replicate(session, ReplicationActionType.ACTIVATE, contentPackage.getNode().getPath(), replicationOptions);
    }
}
