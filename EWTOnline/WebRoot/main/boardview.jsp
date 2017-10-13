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
NotifyAction notifyaction=new NotifyAction(request);
ls=notifyaction.getNotifys();
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
  	<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
  	<meta http-equiv="pragma" content="no-cache" />
  	<meta http-equiv="X-UA-Compatible" content="IE=EmulateIE7" />  	
    <title>Suggest</title>
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
	<script Charset="UTF-8" type="text/javascript" src="../ueditor/editor_config.js"></script>
	<script Charset="UTF-8" type="text/javascript" src="../ueditor/editor_all.js"></script>
	<link rel="stylesheet" href="../ueditor/themes/default/ueditor.css"/>
    <script language="javascript">
    var mode="new";
    var callbackresult;
    var editor=null;
    function onLight()
    {        
        var selectnotifyid=currentlightvalues[0];//checkbox
        var vctitle=currentlightvalues[1];
        var vcsendcode=currentlightvalues[2];
        var vcsenddate=currentlightvalues[3];
        var isshow=currentlightvalues[4];
        var ishigh=currentlightvalues[5];
        frmNotifyNanage.notifyid.value=selectnotifyid;
        frmNotifyNanage.notifytitle.value=vctitle;
        getContent(selectnotifyid);        
    }    
    function getContent(id)
    {
    	var url="do.json?action=notifymanage&method=query&notifyid="+id;
		$.ajax({
		type : "GET",//使用post方法访问后台  
		dataType : "json",//返回json格式的数据  
		url : url,//要访问的后台地址 
		cache: false,//默认: true,设置为 false 将不会从浏览器缓存中加载请求信息。  
		contentType : "application/json;charset=utf-8",	
		beforeSend:onBeforeGetContent,
		complete:onComplete,
		success:onAfterGetContent,
		error:onError 
		});
    }
    function onBeforeGetContent()
    {
    	
    }
    function onAfterGetContent(data)
    {
    	var content=getvalue(data.content);
    	content=content.replace('+',' ');
    	viewform.bulletincontent.value=content;
    	viewform.submit();
    	
    }   
    function initpage()
    {
    	
    }  
    function getNotify()
    {    	
    	var url="do.json?action=usermanage&method=query";
		$.ajax({
		type : "GET",//使用post方法访问后台  
		dataType : "json",//返回json格式的数据  
		url : url,//要访问的后台地址 
		cache: false,//默认: true,设置为 false 将不会从浏览器缓存中加载请求信息。  
		contentType : "application/json;charset=utf-8",	
		beforeSend:onBeforeGetUsers,
		complete:onComplete,
		success:onAfterGetUsers,
		error:onError 
		});
    }  
    function onComplete()
    {
    	
    }
    function onError(XMLResponse)
    {
    	alert(XMLResponse.responseText);
    }
    </script>
  </head>  
  <body style="margin:0px" scroll=no">
    <table width="100%" border="0" cellSpacing="0" cellPadding="0">
    <tr>
    <td valign="top">	     
	     <DIV class="w_l">	
		 <DIV class="body">
				<table width="100%" cellSpacing="0" cellPadding="0" border="0">
					<tr>					
						<td width="100%" valign="top">
						<form action="" method="post" name="frmNotifyNanage" id="frmNotifyNanage">
						    <input type="hidden" name="hidden1"/>
						    <input type="hidden" name="hidden2"/>
						    <input type="hidden" name="hidden3"/>
						    <input type="hidden" name="hidden4"/>
						    <input type="hidden" name="notifyid" value="-1"/>
						    <table width="100%" cellSpacing="0" cellPadding="0" border="0">
						    <tr>
						    <td>
							主题*:<input type="text" name="notifytitle" size="60" tag="true,char,60,标题" value="style->T1->T2->T3"/>
							</td>							
							</tr>
						    <tr>
						    <td>
							<textarea rows=35 cols="90"></textarea>
							</td>
							</tr>
							</table>
						</form>				
						</td>
					</tr>
				</table>
			</DIV>
		 </DIV>	    
    </td>
    </tr>
    </table>
  </body>
</html>
