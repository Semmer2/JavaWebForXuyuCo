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
String imageid=request.getParameter("imageid");
String vehid=request.getParameter("vehid");
if(imageid==null) imageid="";
CommonAction commonaction=new CommonAction(request);
String path=commonaction.getDomMenuPath(vehid,imageid);
if(path==null) path="";
String title="Error or Suggestions";
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
    <script language="javascript">
    var callbackresult;
    var i=0;
	var j=0;   
    function sendcontent()
    {
    	if(trim(frmquery.subject.value)=="")
    	{
    		alert("Please Enter Suggestion Subject!");
    		return;
    	}
    	if(trim(frmquery.suggestcontent.value)=="")
    	{
    		alert("Please Enter Suggestion Content!");
    		return;
    	}
    	frmquery.submit();
    }   
    </script>
  </head>  
  <body style="margin:0px" scroll=no> 
	<form action="../servlet.jsp?action=suggest&method=publish" method="post" name="frmquery" id="frmquery" target="dataframe">
	<input type="hidden" name="vehid" value="<%=vehid%>"/>
	<input type="hidden" name="imageid" value="<%=imageid%>"/>
	<input type="hidden" name="path" value="<%=path%>"/>
	    <table width="100%" cellSpacing="0" cellPadding="0" border="0">
	    <tr>
		    <td style="border-bottom:1px groove #000000">			
				<table cellSpacing="0" cellPadding="0" border="0" width="200">
				<tr>
				<td>
					<img src="../images/blue.png" width="16" height="16"/>
				</td>
				<td style="font-size:10pt">
					<b>Errors or Suggestions</b>
				</td>
				</tr>
				</table>
			</td>			
		    <td valign="middle" style="border-bottom:1px groove #000000;font-size:10pt" align="center" width="100%">
			    Path:<%=path%>
			</td>			
		</tr>
		<tr>
		<td colspan="2">
			<table cellSpacing="0" cellPadding="0" border="0">
		    <tr>
			    <td style="border-bottom:1px groove #000000;font-size:10pt;background: url('/EWTOnline/images/datagrid/grid_head_bg.gif') repeat-x;">			
					Subject*:
				</td>		
			    <td style="font-size:12px;border-bottom:1px groove #000000">
					<input type="text" name="subject" id="subject" size="95" />
				</td>
			    <td valign="middle" style="border-bottom:1px groove #000000" align="right">
				    <input type="button" name="sendout" id="sendout" value="Send Out" onclick="sendcontent();"/>
				</td>			
			</tr>
			 <tr>
			    <td colspan="3">			
					<textarea rows=34 cols="91" name="suggestcontent" id="suggestcontent"></textarea>
				</td>	
			</tr>
			</table>
		</td>
		</tr>
		</table>
	</form>
	<iframe name="dataframe" id="dataframe" style="display:none"/>			
  </body>
</html>
