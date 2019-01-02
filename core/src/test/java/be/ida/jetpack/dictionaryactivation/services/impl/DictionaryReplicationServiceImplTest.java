package be.ida.jetpack.dictionaryactivation.services.impl;

import be.ida.jetpack.dictionaryactivation.services.DictionaryReplicationService;
import io.wcm.testing.mock.aem.junit.AemContext;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertTrue;

@RunWith(MockitoJUnitRunner.class)
public class DictionaryReplicationServiceImplTest {

    @Rule
    public final AemContext context = new AemContext();

    @Before
    public void setUp() throws Exception {
        context.addModelsForPackage("be.ida.jetpack.dictionaryactivation");
        context.load().json("/mocks/dictionary.json", "/apps");
    }

    @Test
    @Ignore
    public void replicateDictionary() {
        context.registerInjectActivateService(new DictionaryReplicationServiceImpl());
        DictionaryReplicationService dictionaryReplicationService = context.getService(DictionaryReplicationService.class);
        assertTrue(dictionaryReplicationService.replicateDictionary("/apps"));

    }
}