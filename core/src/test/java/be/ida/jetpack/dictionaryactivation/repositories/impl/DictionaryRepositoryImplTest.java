package be.ida.jetpack.dictionaryactivation.repositories.impl;

import be.ida.jetpack.dictionaryactivation.repositories.DictionaryRepository;
import io.wcm.testing.mock.aem.junit.AemContext;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import javax.jcr.query.Query;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

@RunWith(MockitoJUnitRunner.class)
public class DictionaryRepositoryImplTest {

    @Rule
    public final AemContext context = new AemContext();

    DictionaryRepository dictionaryRepository = new DictionaryRepositoryImpl();

    @Before
    public void setUp() throws Exception {
        context.load().json("/mocks/dictionaries.json", "/apps");
    }

    @Test
    public void testGetDictionaries() {

        ResourceResolver resourceResolver = mock(ResourceResolver.class);
        given(resourceResolver.findResources("//element(*, mix:language)[@jcr:language]", Query.XPATH))
                .willReturn(getTestResources());

        Iterator<Resource> iterator = dictionaryRepository.getDictionaries(resourceResolver);

        assertThat(iterator).isNotNull();

        Resource r1 = iterator.next();
        assertThat(r1).isNotNull();
        assertThat(r1.getPath()).isEqualTo("/apps/dict-A");

        Resource r2 = iterator.next();
        assertThat(r2).isNotNull();
        assertThat(r2.getPath()).isEqualTo("/apps/dict-B");
    }

    private Iterator<Resource> getTestResources() {
        Resource appsResource = context.resourceResolver().getResource("/apps");

        List<Resource> list = new ArrayList<>();

        appsResource.getChildren().forEach(new Consumer<Resource>() {
            @Override
            public void accept(Resource dictionaryResource) {
                dictionaryResource.getChildren().forEach(new Consumer<Resource>() {
                    @Override
                    public void accept(Resource languageResource) {
                        list.add(languageResource);
                    }
                });
            }
        });

        return list.iterator();
    }
}
