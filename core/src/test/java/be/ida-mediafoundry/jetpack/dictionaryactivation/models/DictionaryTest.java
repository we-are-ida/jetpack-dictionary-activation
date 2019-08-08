package be.ida-mediafoundry.jetpack.dictionaryactivation.models;

import io.wcm.testing.mock.aem.junit.AemContext;
import org.apache.sling.api.resource.Resource;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class DictionaryTest {

    @Rule
    public final AemContext context = new AemContext();

    @Before
    public void setUp() {
        context.load().json("/mocks/dictionary.json", "/apps/test-dictionary");
        context.load().json("/mocks/activated-dictionary.json", "/apps/test-dictionary-2");
        context.load().json("/mocks/activated-dictionary-more-languages.json", "/apps/test-dictionary-3");
        context.addModelsForClasses(Dictionary.class);
    }

    @Test
    public void testUnActivatedDictionary() {
        //given
        Resource resource = context.resourceResolver().getResource("/apps/test-dictionary");
        context.request().setResource(resource);

        context.request().setContextPath("/content/path");

        Dictionary dictionary = context.request().adaptTo(Dictionary.class);

        assertThat(dictionary).isNotNull();
        assertThat(dictionary.getTitle()).isEqualTo("/apps/test-dictionary");
        assertThat(dictionary.getDescription()).isEqualTo("en, en-us");
        assertThat(dictionary.getNumberOfNewTranslations()).isEqualTo(6);
        assertThat(dictionary.getNumberOfTranslationKeys()).isEqualTo(3);
        assertThat(dictionary.getThumbnail()).isEqualTo("/content/path/apps/jetpack/dictionary-activation/components/thumb.png");
        assertThat(dictionary.getPath()).isEqualTo("/apps/test-dictionary");

        assertThat(dictionary.getReplicationDate()).isNull();
        assertThat(dictionary.getReplicationBy()).isNull();
    }

    @Test
    public void testActivatedDictionary() {
        //given
        Resource resource = context.resourceResolver().getResource("/apps/test-dictionary-2");
        context.request().setResource(resource);

        Dictionary dictionary = context.request().adaptTo(Dictionary.class);

        assertThat(dictionary).isNotNull();
        assertThat(dictionary.getTitle()).isEqualTo("/apps/test-dictionary-2");
        assertThat(dictionary.getDescription()).isEqualTo("en, en-us");
        assertThat(dictionary.getNumberOfNewTranslations()).isEqualTo(0);
        assertThat(dictionary.getNumberOfTranslationKeys()).isEqualTo(3);

        assertThat(dictionary.getReplicationDate()).isNotBlank();
        assertThat(dictionary.getReplicationBy()).isNotBlank();
    }

    @Test
    public void testActivatedDictionary_moreLanguages() {
        //given
        Resource resource = context.resourceResolver().getResource("/apps/test-dictionary-3");
        context.request().setResource(resource);

        Dictionary dictionary = context.request().adaptTo(Dictionary.class);

        assertThat(dictionary).isNotNull();
        assertThat(dictionary.getTitle()).isEqualTo("/apps/test-dictionary-3");
        assertThat(dictionary.getDescription()).isEqualTo("10 languages");
        assertThat(dictionary.getNumberOfNewTranslations()).isEqualTo(0);
        assertThat(dictionary.getNumberOfTranslationKeys()).isEqualTo(3);

        assertThat(dictionary.getReplicationDate()).isNotBlank();
        assertThat(dictionary.getReplicationBy()).isNotBlank();
    }
}
