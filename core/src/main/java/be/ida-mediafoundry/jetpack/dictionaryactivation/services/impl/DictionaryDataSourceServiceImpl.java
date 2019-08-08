package be.ida-mediafoundry.jetpack.dictionaryactivation.services.impl;

import be.ida-mediafoundry.jetpack.dictionaryactivation.models.Config;
import be.ida-mediafoundry.jetpack.dictionaryactivation.repositories.DictionaryRepository;
import be.ida-mediafoundry.jetpack.dictionaryactivation.services.DictionaryDataSourceService;
import com.adobe.granite.ui.components.ComponentHelper;
import com.adobe.granite.ui.components.ExpressionHelper;
import com.adobe.granite.ui.components.PagingIterator;
import com.adobe.granite.ui.components.ds.AbstractDataSource;
import com.adobe.granite.ui.components.ds.DataSource;
import org.apache.commons.collections.iterators.TransformIterator;
import org.apache.sling.api.resource.*;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

@Component(
        name = "Jetpack - Dictionaries DataSource Service",
        service = DictionaryDataSourceService.class
)
public class DictionaryDataSourceServiceImpl implements DictionaryDataSourceService {

    @Reference
    private DictionaryRepository dictionaryRepository;

    @Override
    public DataSource getDataSource(HttpServletRequest request, Object cmp, Resource resource) {
        Config config = buildConfig((ComponentHelper) cmp, resource);

        final Iterator<Resource> iterator = dictionaryRepository.getDictionaries();
        return new AbstractDataSource() {
            public Iterator<Resource> iterator() {
                Iterator<Resource> it = new PagingIterator<Resource>(iterator, config.getOffset(), config.getLimit());

                return new TransformIterator(it, o -> {
                    Resource r = ((Resource) o);

                    return new ResourceWrapper(r) {
                        public String getResourceType() {
                            return config.getItemResourceType();
                        }
                    };
                });
            }
        };
    }

    protected Config buildConfig(ComponentHelper cmp, Resource resource) {
        ExpressionHelper ex = cmp.getExpressionHelper();
        ValueMap valueMap = resource.getChild("datasource").getValueMap();

        final String itemResourceType = valueMap.get("itemResourceType", String.class);
        final Integer offset = ex.get(valueMap.get("offset", String.class), Integer.class);
        final Integer limit = ex.get(valueMap.get("limit", String.class), Integer.class);

        return new Config(itemResourceType, offset, limit);
    }
}
