package be.ida-mediafoundry.jetpack.dictionaryactivation.servlets;

import be.ida-mediafoundry.jetpack.dictionaryactivation.services.DictionaryReplicationService;
import com.day.cq.wcm.api.PageManager;
import com.day.cq.wcm.api.commands.WCMCommandContext;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.request.RequestParameter;
import org.apache.sling.api.servlets.HtmlResponse;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

@RunWith(MockitoJUnitRunner.class)
public class PublishDictionaryWcmCommandTest {

    @InjectMocks
    private PublishDictionaryWcmCommand command;

    @Mock
    private DictionaryReplicationService dictionaryReplicationService;

    @Test
    public void testGetCommandName() {
        assertThat(command.getCommandName()).isEqualTo("publishDictionary");
    }

    @Test
    public void testPerformCommand_success() {
        RequestParameter requestParameter = mock(RequestParameter.class);
        given(requestParameter.getString()).willReturn("/apps/dictionary-A");

        SlingHttpServletRequest slingHttpServletRequest = mock(SlingHttpServletRequest.class);
        given(slingHttpServletRequest.getRequestParameter("path")).willReturn(requestParameter);

        given(dictionaryReplicationService.replicateDictionary("/apps/dictionary-A")).willReturn(true);


        HtmlResponse htmlResponse = command.performCommand(mock(WCMCommandContext.class),
                slingHttpServletRequest,
                mock(SlingHttpServletResponse.class),
                mock(PageManager.class));

        assertThat(htmlResponse).isNotNull();
        assertThat(htmlResponse.getStatusCode()).isEqualTo(200);
    }

    @Test
    public void testPerformCommand_fail() {
        RequestParameter requestParameter = mock(RequestParameter.class);
        given(requestParameter.getString()).willReturn("/apps/dictionary-A");

        SlingHttpServletRequest slingHttpServletRequest = mock(SlingHttpServletRequest.class);
        given(slingHttpServletRequest.getRequestParameter("path")).willReturn(requestParameter);

        given(dictionaryReplicationService.replicateDictionary("/apps/dictionary-A")).willReturn(false);


        HtmlResponse htmlResponse = command.performCommand(mock(WCMCommandContext.class),
                slingHttpServletRequest,
                mock(SlingHttpServletResponse.class),
                mock(PageManager.class));

        assertThat(htmlResponse).isNotNull();
        assertThat(htmlResponse.getStatusCode()).isEqualTo(500);
    }
}
