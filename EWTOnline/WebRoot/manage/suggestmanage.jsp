<%@ page contentType="text/html;charset=utf-8" pageEncoding="utf-8"%>
<%@ page import="java.util.*"%>
<%@ taglib uri="/WEB-INF/tld/datagrid.tld" prefix="grid"%>
<%@ page import="com.jl.action.*"%>
<%@ page import="java.net.*"%>
<%@include file="../main/checksession.jsp"%>
<%
response.setHeader("Cache-Control", "no-cache");
response.setHeader("Pragma", "no-cache");
response.setDateHeader("Expires", 0);
String viewdetail="";
int pagesize=2000;//每页记录数量,可修改
int width=100;//表格宽度 ,可修改
int DataWidth=100;//数据宽度,可修改
List userprivmodenames=null;
UserAction useraction=new UserAction(request);	
userprivmodenames=useraction.queryUserPriv(request.getSession().getAttribute("currentuserid").toString());
String usermodenames="";
for(int i=0;i<userprivmodenames.size();i++)
{
	Hashtable ht=(Hashtable)userprivmodenames.get(i);
	String m=ht.get("modename").toString();
	usermodenames=usermodenames+","+m;
}
usermodenames=usermodenames+",";
//if(usermodenames.indexOf(",modename11,")==-1)
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
    <title>Suggest View</title>
    <link REL="StyleSheet" HREF="../css/gridstyle.css">
    <link REL="StyleSheet" HREF="../css/show.css">
    <link REL="StyleSheet" HREF="../css/globle.css">
    <link REL="StyleSheet" HREF="css/smpartmanage.css">
    <script Charset="GBK" src="../scripts/datagrid/datagrid.js" type="text/javascript"></script>	
	<script Charset="GBK" type='text/javascript' src='../scripts/common.js'></script>
	<script Charset="GBK" src="../scripts/base64.js" type='text/javascript'> </script>	
	<script Charset="GBK" src="../scripts/ValidInput.js" type='text/javascript'> </script>
	<script Charset="UTF-8" type="text/javascript" language="javascript" src="../scripts/jquery/json2.js" ></script>  
	<script Charset="UTF-8" type="text/javascript" language="javascript" src="../scripts/jquery/jquery-1.7.min.js" ></script>
	<script Charset="UTF-8" type='text/javascript' src='../scripts/ajaxUtil.js'></script>
    <script language="javascript">
    var callbackresult;
    var editor=null;
    var vehid="";
    var imageid="";
    function initpage()
    {
    	document.all.dataframe.height=document.body.clientHeight;
    	document.all.suggestcontent.style.height=window.screen.height-window.screenTop-78;
    	document.all.suggestcontent.style.width=document.all.suggestpath.offsetWidth;
    }
    function onError(XMLResponse)
    {
    	alert(XMLResponse.responseText);
    }   
    function showpath(id)
    {    	
    	document.all.suggestid.value=id;
    	var url="do.json?action=suggestmanage&method=getpath&id="+id;
		$.ajax({
		type : "GET",//使用post方法访问后台  
		dataType : "json",//返回json格式的数据  
		url : url,//要访问的后台地址 
		cache: false,//默认: true,设置为 false 将不会从浏览器缓存中加载请求信息。  
		contentType : "application/json;charset=utf-8",	
		beforeSend:onBeforeGetPath,
		complete:onComplete,
		success:onAfterGetPath,
		error:onError 
		});
    }
    function onBeforeGetPath()
    {
    	
    }
    function onComplete()
    {
    	
    }
    function onAfterGetPath(data)
    {
    	var path=getvalue(data.path);
    	var content=getvalue(data.suggestcontent);
    	vehid=getvalue(data.vehid);
    	imageid=getvalue(data.imageid);
    	document.all.suggestpath.innerText="Path:"+path;
    	document.all.suggestcontent.value=content;
    }
 	function onError(XMLResponse)
    {
    	alert(XMLResponse.responseText);
    }
 	function openhotimage()
 	{
 		if(vehid!="")
 		{
 			window.open("../main/vehhot.jsp?vehid="+vehid+"&imageid="+imageid,"_blank");
 		}
 		else
 		{
 			alert("对不起,请先选择某一图册反馈!");	
 		}
 	}
    </script>
  </head>  
  <body style="margin:0px" scroll=no onload="initpage();">
  <input type="hidden" name="suggestid" id="suggestid"/>
  <table width="100%" cellSpacing="0" cellPadding="0" border="0">	   
		<tr>
		<td colspan="2">
			<table width="100%" cellSpacing="0" cellPadding="0" border="0">
				<tr>
					<td valign="top" id="gridtd" style="padding-right:15pt">
						<iframe src="suggestlist.jsp" name="dataframe" id="dataframe" frameborder=0 width="500"></iframe>
					</td>
					<td width="80%" valign="top">
					    <table width="100%" cellSpacing="0" cellPadding="0" border="0">
					    <thead>
						    <td id="suggestpath" style="font-size:10pt;padding-right:10pt;cursor:hand" align="center" onclick="openhotimage();">
								Path:
							</td>							
						</thead>
					    <tr>
						    <td>
							   <textarea name="suggestcontent" id="suggestcontent"></textarea>							
							</td>
						</tr>
						</table>									
					</td>
				</tr>
			</table>
		</td>
		</tr>
		</table>
  </body>
</html>