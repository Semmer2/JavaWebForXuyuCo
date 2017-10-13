<%@ page contentType="text/html;charset=utf-8" pageEncoding="utf-8"%>
<%@ page import="java.util.*"%>
<%@ page import="java.net.*"%>
<%@include file="../main/checksession.jsp"%>
<%
response.setHeader("Cache-Control", "no-cache");
response.setHeader("Pragma", "no-cache");
response.setDateHeader("Expires", 0);
String pdfid=request.getParameter("pdfid");
if(pdfid==null) pdfid="";
String url="../pdf/"+pdfid+".pdf#toolbar=0&page=1&FitView=true&navpanes=0";
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
  	<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
  	<meta http-equiv="pragma" content="no-cache" />
  	<META content=IE=EmulateIE7 http-equiv=X-UA-Compatible>  	
    <title>pdfView</title>
  </head>  
  <body style="margin:0px" scroll=no>  
	   <object classid="clsid:CA8A9780-280D-11CF-A24D-444553540000" width="100%" height="600" border="0" top="0" name="pdf" id="pdf"> 
			<param name="_Version" value="65539">
			<param name="_ExtentX" value="20108">
			<param name="_ExtentY" value="10866">
			<param name="_StockProps" value="0">
			<param name="SRC" value="<%=url%>"> 
	   </object>			
  </body>
</html>