<%@ page contentType="application/vnd.ms-excel;charset=gbk"%>
<%@ page import="java.util.*"%>
<%@ taglib uri="/WEB-INF/tld/datagrid.tld" prefix="grid"%>
<%@ page import="com.jl.action.*"%>
<%@ page import="com.jl.entity.*"%>
<%@ page import="java.net.*"%>
<%@include file="../main/checksession.jsp"%>
<%
	response.setContentType("application/vnd.ms-excel;charset=gbk");
	String fileName="Suggestions.xls";
	response.setHeader("Content-disposition","attachment;filename="+fileName);
	SuggestAction action=new SuggestAction(request);
	List ls=action.getSuggests();
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
		<meta http-equiv="pragma" content="no-cache" />
		<meta http-equiv="X-UA-Compatible" content="IE=EmulateIE7" />		
		<title>Suggest</title>		
	</head>
	<body>
	<table width="100%" border="0" cellSpacing="0" cellPadding="0">
			<tr>
				<td>
					Path
				</td>
				<td>
					FromWho
				</td>
				<td>
					Date
				</td>
				<td>
					Suggest
				</td>
			</tr>
	    <%for(int i=0;i<ls.size();i++)
	    {
	    	Hashtable ht=(Hashtable)ls.get(i);
	    	String path=ht.get("path").toString();
	    	String uservccode=ht.get("uservccode").toString();
	    	String sdate=ht.get("screatedate").toString();
	    	String content=ht.get("suggestcontent").toString();	    	
	    %>		
			<tr>
				<td>
					<%=path%>
				</td>
				<td>
					<%=uservccode%>
				</td>
				<td>
					<%=sdate%>
				</td>
				<td>
					<%=content%>
				</td>
			</tr>				
		<%}%>
		</table>
	</body>
</html>