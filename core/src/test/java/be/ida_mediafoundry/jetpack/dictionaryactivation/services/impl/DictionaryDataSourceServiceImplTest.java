package be.ida_mediafoundry.jetpack.dictionaryactivation.services.impl;

import be.ida_mediafoundry.jetpack.dictionaryactivation.models.Config;
import be.ida_mediafoundry.jetpack.dictionaryactivation.repositories.DictionaryRepository;
import com.adobe.granite.ui.components.ds.DataSource;
import io.wcm.testing.mock.aem.junit.AemContext;
import org.apache.commons.collections.IteratorUtils;
import org.apache.sling.api.resource.Resource;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DictionaryDataSourceServiceImplTest {


    @InjectMocks
    @Spy
    private DictionaryDataSourceServiceImpl ddss;

    @Mock
    private DictionaryRepository dictionaryRepository;

    @Rule
    public final AemContext context = new AemContext();

    @Before
    public void setUp() throws Exception {
        context.addModelsForPackage("be.ida_mediafoundry.jetpack.dictionaryactivation");
        context.load().json("/mocks/dictionary.json", "/apps");
        context.load().json("/mocks/dictionary-datasource.json", "/config");
    }

    @Test
    public void getDataSourceLimit2() {
        doReturn(new Config("Type", 1,2)).when(ddss).buildConfig(any(),any());
        when(dictionaryRepository.getDictionaries()).thenReturn(getTestResources());
        Resource resource = context.resourceResolver().getResource("/config");

        DataSource dataSource = ddss.getDataSource(null,null,resource);

        List<Resource> list = IteratorUtils.toList(dataSource.iterator());

        assertEquals(2, list.size());
        assertEquals("Type", list.get(0).getResourceType());
    }


    @Test
    public void getDataSourceLimit3() {
        doReturn(new Config("Type", 1,3)).when(ddss).buildConfig(any(),any());
        when(dictionaryRepository.getDictionaries()).thenReturn(getTestResources());
        Resource resource = context.resourceResolver().getResource("/config");

        DataSource dataSource = ddss.getDataSource(null,null,resource);

        List<Resource> list = IteratorUtils.toList(dataSource.iterator());

        assertEquals(3, list.size());
        assertEquals("Type", list.get(0).getResourceType());
    }

    private Iterator<Resource> getTestResources() {
        List<Resource> list = new ArrayList<>();
        Resource appsResource = context.resourceResolver().getResource("/apps");
        appsResource.getChildren().forEach(dictionaryResource -> dictionaryResource.getChildren().forEach(list::add));
        return list.iterator();
    }
}
