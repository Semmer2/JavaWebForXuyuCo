<%@ page contentType="text/html;charset=utf-8" pageEncoding="utf-8"%>
<%@ page import="java.util.*"%>
<%@ taglib uri="/WEB-INF/tld/datagrid.tld" prefix="grid"%>
<%@ page import="com.jl.action.*"%>
<%@ page import="java.net.*"%>
<%@include file="checksession.jsp"%>

<%
response.setHeader("Cache-Control", "no-cache");
response.setHeader("Pragma", "no-cache");
response.setDateHeader("Expires", 0);
String viewdetail="";
int pagesize=2000;//每页记录数量,可修改
int width=100;//表格宽度 ,可修改
int DataWidth=100;//数据宽度,可修改
List ls=new ArrayList();
String mastercode=request.getParameter("mastercode");
String vehid=request.getParameter("vehid");
String title="Parts Search";
for(int i=0;i<9999;i++)
{
	title=title+"&nbsp;";
}
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
  	<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
  	<meta http-equiv="pragma" content="no-cache" />
  	<meta http-equiv="X-UA-Compatible" content="IE=EmulateIE7" />  	
    <title><%=title%></title>
    <link REL="StyleSheet" HREF="../css/gridstyle.css">
    <link REL="StyleSheet" HREF="../css/show.css">
    <link REL="StyleSheet" HREF="../css/globle.css">
    <link href="../images/login.css" rel="stylesheet" type="text/css" />
    <script Charset="GBK" src="../scripts/datagrid/datagrid.js" type="text/javascript"></script>	
	<script Charset="GBK" type='text/javascript' src='../scripts/common.js'></script>
	<script Charset="GBK" src="../scripts/base64.js" type='text/javascript'> </script>	
	<script Charset="GBK" src="../scripts/ValidInput.js" type='text/javascript'> </script>
	<script Charset="UTF-8" type="text/javascript" language="javascript" src="../scripts/jquery/json2.js" ></script>  
	<script Charset="UTF-8" type="text/javascript" language="javascript" src="../scripts/jquery/jquery-1.7.min.js" ></script>
	<script Charset="UTF-8" type='text/javascript' src='../scripts/ajaxUtil.js'></script>
	<script Charset="UTF-8" type="text/javascript" src="../ueditor/editor_config.js"></script>
	<script Charset="UTF-8" type="text/javascript" src="../ueditor/editor_all.js"></script>
	<link rel="stylesheet" href="../ueditor/themes/default/ueditor.css"/>
    <script language="javascript">
    var mode="new";
    var callbackresult;
    var editor=null;
    var i=0;
	var j=0;
    function onInputFocus()
	{			
		
		if(event.srcElement.name=="pn")
		{	
			if(i==0) 
			{
				if(event.srcElement.value=="P/N") event.srcElement.value="";
			}
			i=i+1;
		}
		if(event.srcElement.name=="pd")
		{
			if(j==0) 
			{
				if(event.srcElement.value=="P/D") event.srcElement.value="";
			}
			j=j+1;
		}
	}
    function go()
    {
    	if(i==0 && frmquery.pn.value=="P/N") frmquery.pn.value="";
    	if(j==0 && frmquery.pd.value=="P/D") frmquery.pd.value="";
    	frmquery.submit();
    }
    function showimage(imageid)
    {
    	var parentwindow=window.dialogArguments;
    	parentwindow.openhotinfobyimageid(imageid);
    	
    }
   
    </script>
  </head>  
  <body style="margin:0px" scroll=no> 
	<form action="queryhotpartlist.jsp" method="post" name="frmquery" id="frmquery" target="partframe">
	    <input type="hidden" name="vehid" id="vehid" value="<%=vehid%>">												   
	    <table width="100%" cellSpacing="0" cellPadding="0" border="0">
	    <tr>
		    <td style="border-bottom:1px groove #000000">			
				<img src="../images/blue.png" width="16" height="16"/>
			</td>		
		    <td style="font-size:12px;border-bottom:1px groove #000000;padding:0px">
				<b>Only search this model:<%=mastercode%></b>
			</td>
		    <td valign="middle" style="border-bottom:1px groove #000000" align="right">
			    <table style="padding:0px">
			    <tr>
			    <td valign="top">
				<input class="short_input_text1" name="pn" value="P/N" type="text" onclick="onInputFocus();"/>
				</td>
				<td valign="top">
				<input class="short_input_text1" name="pd" value="P/D" type="text" onclick="onInputFocus();"/>
				</td>
				<td valign="baseline">
				<img src="../images/smallquery.jpg" onclick="go();" style="cursor:pointer"/>
				</td>
				</tr>
				</table>		
			</td>			
		</tr>
		<tr>
		<td colspan="3">
		<iframe src="queryhotpartlist.jsp?vehid=<%=-1%>" name="partframe" id="partframe" frameborder=0 width="900" height="420"></iframe>
		</td>
		</tr>
		</table>
	</form>			
  </body>
</html>
