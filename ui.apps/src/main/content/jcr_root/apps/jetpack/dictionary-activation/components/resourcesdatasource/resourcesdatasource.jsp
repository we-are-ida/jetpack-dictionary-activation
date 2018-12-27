<%
%><%@include file="/libs/granite/ui/global.jsp"%><%
%><%@page session="false"
          import="com.adobe.granite.ui.components.ds.DataSource,
          be.ida.jetpack.dictionaryactivation.services.DictionaryDataSourceService"%>
<%
    DictionaryDataSourceService service = (DictionaryDataSourceService)sling.getService(DictionaryDataSourceService.class);
    DataSource dataSource = service.getDataSource(request, cmp, resource);
    request.setAttribute(DataSource.class.getName(), dataSource);
%>
