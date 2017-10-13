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
List ls=new ArrayList();
NotifyAction notifyaction=new NotifyAction(request);
if(request.getMethod().equalsIgnoreCase("post"))
{
	String querytitle=request.getParameter("querytitle");
	querytitle=URLDecoder.decode(querytitle,"UTF-8");
	querytitle=querytitle.trim();
	ls=notifyaction.getNotifys(querytitle);
}
else
{
	ls=notifyaction.getNotifys();
}
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
  	<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
  	<meta http-equiv="pragma" content="no-cache" />
  	<META content=IE=EmulateIE7 http-equiv=X-UA-Compatible>  	
    <title>公告管理</title>
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
        frmNotifyNanage.notifysender.value=vcsendcode;
        if(isshow=="1")
        	frmNotifyNanage.notifystatus.checked=true;
        else
        	frmNotifyNanage.notifystatus.checked=false;
         if(ishigh=="1")
        	frmNotifyNanage.notifyhigh.checked=true;
        else
        	frmNotifyNanage.notifyhigh.checked=false;        
        mode="edit";
        getContent(selectnotifyid);        
    }
    function delNotify()
    {
    	var ids=getSelectIds();
		var url="do.json?action=notifymanage&method=delete&ids="+ids;
		$.ajax({
		type : "GET",//使用post方法访问后台  
		dataType : "json",//返回json格式的数据  
		url : url,//要访问的后台地址 
		cache: false,//默认: true,设置为 false 将不会从浏览器缓存中加载请求信息。  
		contentType : "application/json;charset=utf-8",	
		beforeSend:onBeforeDeleteNotify,
		complete:onComplete,
		success:onAfterDeleteNotify,
		error:onError 
		});
    }
    function onBeforeDeleteNotify()
    {
    	
    }
    function onAfterDeleteNotify(returnobject)
	{	
		if(returnobject.result=="")
		{
			alert("删除失败("+getvalue(returnobject.error)+")!");
		}
		else
		{			
			alert("删除成功!");
			onDeleteRow();
			mode="";
			frmNotifyNanage.notifytitle.value="";
	    	frmNotifyNanage.notifysender.selectedIndex=0;
	    	frmNotifyNanage.notifystatus.checked=true;
	    	frmNotifyNanage.notifyhigh.checked=false;
	    	setEditContent("");
	    	
		}
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
    	setEditContent(content);
    }
    function setEditContent(value)
    {
    	editor.setContent(value,true);
    }
    function getEditContent()
    {
    	return editor.getContent();
    }
   
    function initpage()
    {
    	 editor = new baidu.editor.ui.Editor();
    	 editor.render("myEditor");
    	 getusers();
    }    
    function getusers()
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
    function onBeforeGetUsers()
    {
    	
    }
    function onComplete()
    {
    	
    }
    function onError(XMLResponse)
    {
    	alert(XMLResponse.responseText);
    } 
    function onAfterGetUsers(data)
    {	
    	if(data.result!="0")
		{	
    		var totalcount=getvalue(data.totalcount);
    		for(var i=0;i<totalcount;i++)
    		{
    			var key="key"+i;
    			var codeexpress="data."+key+"_code";
    			var nameexpress="data."+key+"_name";
    			var vccode=eval(codeexpress);
    			var vccname=getvalue(eval(nameexpress));
    			var option=document.createElement("OPTION");
    			option.value=vccode;
    			option.text=vccname;
    			document.all.notifysender.options.add(option);
    		}
	   }
    }
    function setNewMode()
    {
    	frmNotifyNanage.notifytitle.value="";
    	frmNotifyNanage.notifysender.selectedIndex=0;
    	frmNotifyNanage.notifystatus.checked=true;
    	frmNotifyNanage.notifyhigh.checked=false;
    }
    function onBeforeAddNotify()
    {
    	
    }
    function onAfterAddNotify(callbacktext)
    {
    	var result="("+trim(callbacktext.responseText)+")";    	
		var returnobject=eval(trim(result));
		if(returnobject.result=="")
		{
			alert("增加失败("+getvalue(returnobject.error)+")!");
		}
		else
		{
			var vctitle=getvalue(returnobject.vctitle);
			var id=returnobject.id;
			var senddate=getvalue(returnobject.senddate);
			senddate=senddate.replace('+',' ');
			var sendname=getvalue(returnobject.vcsendname)+";"+getvalue(returnobject.vcsendno);
			var istatus=getvalue(returnobject.istatus);
			var isshow=getvalue(returnobject.isshow);
			var ishighshow=getvalue(returnobject.ishighshow);		
			var ishighdisplay=getvalue(returnobject.ishighdisplay);
			var notifystatus=isshow+";"+istatus;
			var notifyhigh=ishighdisplay+";"+ishighshow;
			var values=id+","+escape(trim(vctitle))+","+escape(trim(sendname))+","+escape(trim(senddate))+","+escape(notifystatus)+","+escape(notifyhigh);
			onNewRow(values);
	    	alert("发布成功!");
    	}
    }
    function onAfterEditNotify(callbacktext)
    {
    	var result="("+trim(callbacktext.responseText)+")";    	
		var returnobject=eval(trim(result));		
		if(returnobject.result=="")
		{
			alert("更新失败("+getvalue(returnobject.error)+")!");
		}
		else
		{			
			var vctitle=getvalue(returnobject.vctitle);
			var id=returnobject.id;
			var senddate=getvalue(returnobject.senddate);
			senddate=senddate.replace('+',' ');
			var sendname=getvalue(returnobject.vcsendname)+";"+getvalue(returnobject.vcsendno);
			var istatus=getvalue(returnobject.istatus);
			var isshow=getvalue(returnobject.isshow);
			var ishighshow=getvalue(returnobject.ishighshow);
			var ishighdisplay=getvalue(returnobject.ishighdisplay);		
			var notifystatus=isshow+";"+istatus;
			var notifyhigh=ishighdisplay+";"+ishighshow;		
			var values=id+","+escape(trim(vctitle))+","+escape(trim(sendname))+","+escape(trim(senddate))+","+escape(notifystatus)+","+escape(notifyhigh);			
			onSaveRow(id,values);
	    	alert("更新成功!");
    	}
    }
    function onBeforeEditNotify(data)
    {
    	
    }
    function onError()
    {
    	
    }
    function saveNotify()
    {
    	if(mode!="new" && mode!="edit")
    	{
    		alert("对不起,请切换成新建状态!");
    		return;
    	}
    	if(frmNotifyNanage.notifysender.options.length==0)
    	{
    		alert("对不起,没有发布公告的用户!");
    		return;	
    	}
    	if(verifyAll(frmNotifyNanage))
		{			
			var sendercode=frmNotifyNanage.notifysender.value;
			var sendername=frmNotifyNanage.notifysender.options[frmNotifyNanage.notifysender.selectedIndex].text;
    		if(mode=="new")
			{				
    			if(trim(editor.getContent())!="")
				{
	    			//alert(trim(editor.getContent()));
    				frmNotifyNanage.hidden1.value=encodeURI(encodeURI(trim(editor.getContent())));
					frmNotifyNanage.hidden2.value=encodeURI(encodeURI(trim(frmNotifyNanage.notifytitle.value)));				
					frmNotifyNanage.hidden3.value=encodeURI(encodeURI(trim(sendercode)));
					frmNotifyNanage.hidden4.value=encodeURI(encodeURI(trim(sendername)));				
					var url="do.json?action=notifymanage&method=new";				
					send_post_request(url,onAfterAddNotify,$("#frmNotifyNanage").serialize());
				}
				else
				{
					alert("对不起,公告不能空!");	
				}
			}
			if(mode=="edit")
			{				
				if(trim(editor.getContent())!="")
				{
					frmNotifyNanage.hidden1.value=encodeURI(encodeURI(trim(editor.getContent())));
					frmNotifyNanage.hidden2.value=encodeURI(encodeURI(trim(frmNotifyNanage.notifytitle.value)));				
					frmNotifyNanage.hidden3.value=encodeURI(encodeURI(trim(sendercode)));
					frmNotifyNanage.hidden4.value=encodeURI(encodeURI(trim(sendername)));					
					var url="do.json?action=notifymanage&method=edit";				
					send_post_request(url,onAfterEditNotify,$("#frmNotifyNanage").serialize());
					
				}
				else
				{
					alert("对不起,公告不能空!");	
				}
			}
		}
    }
    </script>
  </head>  
  <body style="margin:0px" scroll=no onload="fixgridsize(document.body.clientHeight-70,gridtd.clientWidth);initpage();" onresize="fixgridsize(document.body.clientHeight-100,gridtd.clientWidth);">
    <table width="100%" border="0" cellSpacing="0" cellPadding="0">
    <tr>
    <td valign="top">	     
	     <P class="r_l_3"></P>
		 <P class="r_l_2"></P>
		 <P class="r_l_1"></P>
	     <DIV class="w_l">	
		 <DIV class="body">
				<table width="100%" cellSpacing="0" cellPadding="0" border="0">
					<tr>
						<td valign="top" id="gridtd">
						<form name="frmShow" id="frmShow" method="POST" target="_self">
						 <grid:dbgrid id="tblStat" name="tblStat" width="560" pageSize="<%=pagesize%>" pageObject="<%=pageContext%>" pageRequest="<%=request%>"
							border="0" cellSpacing="1" cellPadding="2" dataMember="" dataSource="<%=ls%>" totalRecords="<%=0%>"
							cssClass="gridTable" lightOn="true" tdIntervalColor="true" verticalHeight="375" gridPosition="absolute" 
							dataWidth="560" viewDetail="<%=viewdetail%>" viewWidth="860" viewHeight="600">
					            <grid:rownumcolumn dataField="id" headerText="序号" width="8" HAlign="center" sortable="true"/>           
					            <grid:checkboxcolumn dataField="id" headerText="checkbox" HAlign="center" sortable="true" width="8"/>
					            <grid:textcolumn dataField="vctitle" headerText="标题" HAlign="center" sortable="true"/>
					            <grid:textcolumn dataField="vcsendname" headerText="发布人" HAlign="center" sortable="true" width="12" tagField="vcsendno"/>
					            <grid:textcolumn dataField="senddate" headerText="发布时间" HAlign="center" sortable="true" width="20"/>
					            <grid:textcolumn dataField="isshow" headerText="显示" HAlign="center" sortable="true" width="8" tagField="istatus"/>
					            <grid:textcolumn dataField="ishighdisplay" headerText="高亮" HAlign="center" sortable="true" width="8" tagField="ihighshow"/>
					      </grid:dbgrid>
					      </form>
						</td>						
						<td width="60%" valign="top">
						<form action="" method="post" name="frmNotifyNanage" id="frmNotifyNanage">
						    <input type="hidden" name="hidden1"/>
						    <input type="hidden" name="hidden2"/>
						    <input type="hidden" name="hidden3"/>
						    <input type="hidden" name="hidden4"/>
						    <input type="hidden" name="notifyid" value="-1"/>
						    <table width="100%" cellSpacing="0" cellPadding="0" border="0">
						    <tr>
						    <td>
							标题*:<input type="text" name="notifytitle" size="60" tag="true,char,60,标题"/>
							发布人:<select name="notifysender"></select>
							显示:<input type="checkbox" name="notifystatus" checked/>
							高亮:<input type="checkbox" name="notifyhigh" checked/>
							</td>							
							</tr>
						    <tr>
						    <td>
							<div id="myEditor"></div>
							</td>
							</tr>
							</table>
						</form>				
						</td>
					</tr>
				</table>
			</DIV>
		 </DIV>	
		 <P class="r_l_1"></P>
		 <P class="r_l_2"></P>
		 <P class="r_l_3"></P>	    
    </td>
    </tr>
    </table>    
  </body>
</html>
