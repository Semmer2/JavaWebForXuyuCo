<%@ page contentType="text/html;charset=utf-8" pageEncoding="utf-8"%>
<%@ page import="java.util.*"%>
<%@ taglib uri="/WEB-INF/tld/datagrid.tld" prefix="grid"%>
<%@ page import="com.jl.action.*"%>
<%@include file="../main/checksession.jsp"%>
<%
response.setHeader("Cache-Control", "no-cache");
response.setHeader("Pragma", "no-cache");
response.setDateHeader("Expires", 0);
String viewdetail="";
int pagesize=20;//每页记录数量,可修改
int width=100;//表格宽度 ,可修改
int DataWidth=100;//数据宽度,可修改
List ls=new ArrayList();
String sessionuserflag=request.getSession().getAttribute("userflag").toString();
if(sessionuserflag.equalsIgnoreCase("3")==false)
{
	out.write("对不起,您没有权限使用此模块!");
	return;
}
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
  	<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
  	<meta http-equiv="pragma" content="no-cache" />
  	<META content=IE=EmulateIE7 http-equiv=X-UA-Compatible>  	
    <title>权限管理</title>
    <link REL="StyleSheet" HREF="../css/gridstyle.css">
    <link REL="StyleSheet" HREF="../css/show.css">
    <link REL="StyleSheet" HREF="../css/globle.css">
    <link REL="StyleSheet" HREF="css/smpartmanage.css">
    <script Charset="gbk" src="../scripts/datagrid/datagrid.js" type="text/javascript"></script>	
	<script Charset="gbk" type='text/javascript' src='../scripts/common.js'></script>	
	<script Charset="gbk" src="../scripts/ValidInput.js" type='text/javascript'> </script>
	<script type="text/javascript" language="javascript" src="../scripts/jquery/json2.js" ></script>  
	<script type="text/javascript" language="javascript" src="../scripts/jquery/jquery-1.7.min.js" ></script>	
    <script language="javascript">
    var currentuserid=-1;;
    function pagesize()
    {
      	document.all.dataframe.height=window.screen.height-window.screenTop;    	
    }   
    function queryusername()
    {	
    	formquery.submit();
    }
    function changeuserpriv()
	{
		var modename=event.srcElement.id;
		var checked=event.srcElement.checked;		
		if(currentuserid!="-1")
		{		
			var url="../servlet.jsp?action=usermanage&method=manageuserpriv&modename="+modename+"&userid="+currentuserid+"&checked="+checked;
			callbackresult=send_request(url,onCallBack);
		}
		else
		{
			alert("请选择某一用户!");	
		}
	}
	function onCallBack(callbacktext)
	{
		
	}
    </script>
  </head>  
  <body style="margin:0px" scroll=no onload="pagesize();" onresize="pagesize();">
    <table width="100%" border="0" cellSpacing="0" cellPadding="0">
    <tr>
    <td valign="top">	     
	     <P class="r_l_3"></P>
		 <P class="r_l_2"></P>
		 <P class="r_l_1"></P>
	     <DIV class="w_l">		    
			 <H4>
			    <form action="userprivlist.jsp" target="dataframe" method="post" name="formquery"> 
					<table width="100%" cellSpacing="0" cellPadding="0" border=0>
						<tr>
							<td width="50%">
								用户名:
								<input type="text" name="username" id="username" size="30"/>
								<input type="button" value="GO" onclick="queryusername();"/>
							</td>
							<td align="right">
								&nbsp;
							</td>
						</tr>
					</table>
				</form>
			 </H4>		
			 <DIV class="body">
			 	<iframe src="userprivlist.jsp" name="dataframe" id="dataframe" frameborder=0 width="100%"></iframe> 		
			 </DIV>
		 </DIV>	
		 <P class="r_l_1"></P>
		 <P class="r_l_2"></P>
		 <P class="r_l_3"></P>	    
    </td>
    </tr>
    </table> 
    <iframe name="hiddenframe" id="hiddenframe" width="1" height="1" style="display:none"/>   
  </body>
</html>
