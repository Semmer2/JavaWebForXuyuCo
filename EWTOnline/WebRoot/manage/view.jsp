<%@ page contentType="text/html;charset=utf-8" pageEncoding="utf-8"%>
<%@ page import="java.util.*"%>
<%@ taglib uri="/WEB-INF/tld/datagrid.tld" prefix="grid"%>
<%@ page import="com.jl.action.*"%>
<%@ page import="com.jl.util.StringUtils"%>
<%@ page import="java.net.*"%>
<%@include file="../main/checksession.jsp"%>
<%
response.setHeader("Cache-Control", "no-cache");
response.setHeader("Pragma", "no-cache");
response.setDateHeader("Expires", 0);
String bulletincontent=request.getParameter("bulletincontent");
if(bulletincontent==null) bulletincontent="";
bulletincontent=StringUtils.replaceString(bulletincontent,"+"," ");
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
  <META content=IE=EmulateIE7 http-equiv=X-UA-Compatible>
</head>
<body>
<%=bulletincontent%>
</body>
</html>