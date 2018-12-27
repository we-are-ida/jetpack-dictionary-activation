package be.ida.jetpack.dictionaryactivation.servlets;

import be.ida.jetpack.dictionaryactivation.services.DictionaryReplicationService;
import com.day.cq.commons.servlets.HtmlStatusResponseHelper;
import com.day.cq.wcm.api.PageManager;
import com.day.cq.wcm.api.commands.WCMCommand;
import com.day.cq.wcm.api.commands.WCMCommandContext;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.request.RequestParameter;
import org.apache.sling.api.servlets.HtmlResponse;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * Activate the Dictionary.
 * The dictionary to activate will be provided in the "path" request parameter.
 */
@Component(service= WCMCommand.class,
        property={
                Constants.SERVICE_DESCRIPTION + "=Jetpack - Publish Dictionary WcmCommand"

        })
public class PublishDictionaryWcmCommand implements WCMCommand {

    @Reference
    private DictionaryReplicationService dictionaryReplicationService;

    @Override
    public String getCommandName() {
        return "publishDictionary";
    }

    @Override
    public HtmlResponse performCommand(WCMCommandContext wcmCommandContext,
                                       SlingHttpServletRequest slingHttpServletRequest,
                                       SlingHttpServletResponse slingHttpServletResponse,
                                       PageManager pageManager) {

        RequestParameter path = slingHttpServletRequest.getRequestParameter(PATH_PARAM);

        HtmlResponse resp = null;

        try {
            boolean success = dictionaryReplicationService.replicateDictionary(path.getString());

            resp = HtmlStatusResponseHelper.createStatusResponse(success, "published",
                    path.getString());
        } catch (Exception e) {
            resp = HtmlStatusResponseHelper.createStatusResponse(false, e.getMessage());
        }

        return resp;
    }
}
