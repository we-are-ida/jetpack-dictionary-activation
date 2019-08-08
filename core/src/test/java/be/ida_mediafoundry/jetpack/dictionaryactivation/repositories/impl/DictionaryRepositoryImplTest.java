package be.ida_mediafoundry.jetpack.dictionaryactivation.repositories.impl;

import be.ida_mediafoundry.jetpack.dictionaryactivation.repositories.DictionaryRepository;
import io.wcm.testing.mock.aem.junit.AemContext;
import org.apache.sling.api.resource.Resource;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;

@RunWith(MockitoJUnitRunner.class)
public class DictionaryRepositoryImplTest {

    @Rule
    public final AemContext context = new AemContext();


    @Before
    public void setUp() throws Exception {
        context.addModelsForPackage("be.ida_mediafoundry.jetpack.dictionaryactivation");
        context.load().json("/mocks/dictionaries.json", "/apps");
    }


    @Test
    public void testGetDictionaries() {
        DictionaryRepositoryImpl dictionaryRepositorySpy = spy(DictionaryRepositoryImpl.class);
        doReturn(getTestResources()).when(dictionaryRepositorySpy).getLanguageResources(any());

        context.registerInjectActivateService(dictionaryRepositorySpy);

        DictionaryRepository dictionaryRepository = context.getService(DictionaryRepository.class);
        Iterator<Resource> iterator = dictionaryRepository.getDictionaries();

        assertThat(iterator).isNotNull();
        Resource r1 = iterator.next();
        assertThat(r1).isNotNull();
        assertThat(r1.getPath()).isEqualTo("/apps/dict-A");
        Resource r2 = iterator.next();
        assertThat(r2).isNotNull();
        assertThat(r2.getPath()).isEqualTo("/apps/dict-B");
    }

    private Iterator<Resource> getTestResources() {
        List<Resource> list = new ArrayList<>();
        Resource appsResource = context.resourceResolver().getResource("/apps");
        appsResource.getChildren().forEach(dictionaryResource -> dictionaryResource.getChildren().forEach(list::add));
        return list.iterator();
    }
}
