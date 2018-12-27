package be.ida.jetpack.dictionaryactivation.services.impl;

import be.ida.jetpack.dictionaryactivation.repositories.DictionaryRepository;
import be.ida.jetpack.dictionaryactivation.services.DictionaryDataSourceService;
import com.adobe.granite.ui.components.ComponentHelper;
import com.adobe.granite.ui.components.Config;
import com.adobe.granite.ui.components.ExpressionHelper;
import com.adobe.granite.ui.components.PagingIterator;
import com.adobe.granite.ui.components.ds.AbstractDataSource;
import com.adobe.granite.ui.components.ds.DataSource;
import com.adobe.granite.ui.components.ds.EmptyDataSource;
import org.apache.commons.collections.Transformer;
import org.apache.commons.collections.iterators.TransformIterator;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceWrapper;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

@Component(
        name = "Jetpack - Dictionaries DataSource Service",
        service = DictionaryDataSourceService.class
)
public class DictionaryDataSourceServiceImpl implements DictionaryDataSourceService {

    private static final Logger LOG = LoggerFactory.getLogger(DictionaryDataSourceService.class);

    @Reference
    private DictionaryRepository dictionaryRepository;

    @Override
    public DataSource getDataSource(HttpServletRequest request, Object cmp, Resource resource) {
        ExpressionHelper ex = ((ComponentHelper)cmp).getExpressionHelper();
        Config dsCfg = new Config(resource.getChild(Config.DATASOURCE));

        final String itemRT = dsCfg.get("itemResourceType", String.class);
        final Integer offset = ex.get(dsCfg.get("offset", String.class), Integer.class);
        final Integer limit = ex.get(dsCfg.get("limit", String.class), Integer.class);

        ResourceResolver resourceResolver = resource.getResourceResolver();

        try {
            final Iterator<Resource> iterator = dictionaryRepository.getDictionaries(resourceResolver);

            @SuppressWarnings("unchecked")
            DataSource datasource = new AbstractDataSource() {
                public Iterator<Resource> iterator() {
                    Iterator<Resource> it = new PagingIterator<Resource>(iterator, offset, limit);

                    return new TransformIterator(it, new Transformer() {
                        public Object transform(Object o) {
                            Resource r = ((Resource) o);

                            return new ResourceWrapper(r) {
                                public String getResourceType() {
                                    return itemRT;
                                }
                            };
                        }
                    });
                }
            };

            return datasource;
        } catch (Exception e) {
            LOG.error("Error while reading dictionaries list. " + e);
        }

        return EmptyDataSource.instance();
    }
}
