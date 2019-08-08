package be.ida-mediafoundry.jetpack.dictionaryactivation.services;

import com.adobe.granite.ui.components.ds.DataSource;
import org.apache.sling.api.resource.Resource;

import javax.servlet.http.HttpServletRequest;

/**
 * Providing a Datasource to the JSP file to render the resource list (dictionaries in this case)
 */
public interface DictionaryDataSourceService {

    /**
     * To be called from the JSP with:
     *
     * <%
     *     DictionaryDataSourceService service = (DictionaryDataSourceService)sling.getService(DictionaryDataSourceService.class);
     *     DataSource dataSource = service.getDataSource(request, cmp, resource);
     *     request.setAttribute(DataSource.class.getName(), dataSource);
     * %>
     *
     * @param request request from JSP
     * @param cmp component object from JSP
     * @param resource from JSP
     * @return
     */
    DataSource getDataSource(HttpServletRequest request, Object cmp, Resource resource);

}
