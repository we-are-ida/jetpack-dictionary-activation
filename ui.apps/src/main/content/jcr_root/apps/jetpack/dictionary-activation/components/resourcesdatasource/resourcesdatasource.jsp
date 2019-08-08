<%
%><%@include file="/libs/granite/ui/global.jsp"%><%
%><%@page session="false"
          import="be.ida_mediafoundry.jetpack.dictionaryactivation.services.DictionaryDataSourceService,
                  com.adobe.granite.ui.components.ds.DataSource" %>
<%
    DictionaryDataSourceService service = (DictionaryDataSourceService)sling.getService(DictionaryDataSourceService.class);
    DataSource dataSource = service.getDataSource(request, cmp, resource);
    request.setAttribute(DataSource.class.getName(), dataSource);
%>
