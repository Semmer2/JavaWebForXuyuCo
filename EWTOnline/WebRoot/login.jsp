<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page contentType="text/html; charset=GBK" %>
<%@ page import="com.jl.util.*" %>
<%
String serverip=pageContext.getRequest().getServerName();
serverip=StringUtils.replaceString(serverip,".","");
serverip=StringUtils.escape(serverip);
if(request.getMethod().equalsIgnoreCase("post"))
{
	String islogout=request.getParameter("logoutinput");
	if(islogout==null) islogout="";
	if(islogout.equals("logout"))
	{
		request.getSession().putValue("currentuserid",null);
	}
}
%>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=gb2312" />
<META content=IE=EmulateIE7 http-equiv=X-UA-Compatible>
<SCRIPT language=javascript src="scripts/login.js"></SCRIPT>
<title>EWT Login</title>
<script onreadystatechange="if(this.readyState=='complete'){loadstart('<%=serverip%>');}" src="//:" defer="defer"></script>
</head>
<body>
</body>
</html>