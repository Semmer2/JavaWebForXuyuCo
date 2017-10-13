<%@ page contentType="text/html;charset=utf-8" pageEncoding="utf-8"%>
<%@ page import="java.util.*"%>
<%@ taglib uri="/WEB-INF/tld/datagrid.tld" prefix="grid"%>
<%@ page import="com.jl.action.*"%>
<%@include file="../main/checksession.jsp"%>
<%
response.setHeader("Cache-Control", "no-cache");
response.setHeader("Pragma", "no-cache");
response.setDateHeader("Expires", 0);
List ls=new ArrayList();
String method=request.getParameter("method");
String id=request.getParameter("id");
if(id==null) id="";
String dialogtitle="";
AsmLinkAction action=new AsmLinkAction(request);
if(method.equalsIgnoreCase("new"))
{
	dialogtitle="新建";
}
else
{
	dialogtitle="编辑";	
}
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
  	<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
  	<meta http-equiv="pragma" content="no-cache" />
  	<META content=IE=EmulateIE7 http-equiv=X-UA-Compatible>  	
    <title>总成包维护</title>
     <link rel="stylesheet" type="text/css" href="../css/zw.css"></link>
    <link rel="stylesheet" type="text/css" href="../css/standard.css"></link>
    <link rel="stylesheet" type="text/css" href="../css/queryview.css"></link>
    <link REL="StyleSheet" HREF="../css/globle.css">
    <link REL="StyleSheet" HREF="css/smpartmanage.css">
    <script Charset="gbk" src="../scripts/datagrid/datagrid.js" type="text/javascript"></script>	
	<script Charset="gbk" type='text/javascript' src='../scripts/common.js'></script>	
	<script Charset="gbk" src="../scripts/ValidInput.js" type='text/javascript'> </script>
	<script type="text/javascript" language="javascript" src="../scripts/jquery/json2.js" ></script>  
	<script type="text/javascript" language="javascript" src="../scripts/jquery/jquery-1.7.min.js" ></script>	
    <script language="javascript">
    var mode="<%=method%>";
    var editasmdata=null;
    function checkinput()
    {    	
    	var p1=document.all.asmvccode.value;
    	var p2=document.all.partvccode.value;
    	var p3=document.all.imagevccode.value;
    	var p4=document.all.asmimagevccode.value;
    	var url="do.json?action=asmlinkmanage&method=checkparames&p1="+p1+"&p2="+p2+"&p3="+p3+"&p4="+p4;
		$.ajax({
		type : "GET",//使用post方法访问后台  
		dataType : "json",//返回json格式的数据  
		url : url,//要访问的后台地址 
		cache: false,//默认: true,设置为 false 将不会从浏览器缓存中加载请求信息。  
		contentType : "application/json;charset=utf-8",	
		beforeSend:onBeforeGetData,
		complete:onComplete,
		success:onAfterCheckInput,
		error:onError 
		});    
    }
    
    function save()
    {
    	checkinput();    	
    }
    function closewindow()
    {
    	window.close();
    }
    function initpage()
    {
    	if(mode=="new")
    	{
    		document.all.asmvccode.enabled=true;    		
    	}
    	if(mode=="edit")
    	{
    		document.all.asmvccode.enabled=false;
    		getasmbyid();
    	}
    }
    
    function getasmbyid()
    {
    	var url="do.json?action=asmlinkmanage&method=queryasmbyid&id=<%=id%>";
		$.ajax({
		type : "GET",//使用post方法访问后台  
		dataType : "json",//返回json格式的数据  
		url : url,//要访问的后台地址 
		cache: false,//默认: true,设置为 false 将不会从浏览器缓存中加载请求信息。  
		contentType : "application/json;charset=utf-8",	
		beforeSend:onBeforeGetData,
		complete:onComplete,
		success:onAfterGetAsmById,
		error:onError 
		});
    }
    function onAfterCheckInput(data)
    {
    	if(data.result!="0")
		{
    		var asmid=getvalue(data.asmid);
    		var partid=getvalue(data.partid);
    		var imageid=getvalue(data.imageid);
    		var asmimageid=getvalue(data.asmimageid);//允许,不存在asmimageid=0
    		if(asmid=="")
    		{
    			alert("总成序号在零件表中没有找到!");
    			return 0;
    		}
    		if(partid=="")
    		{
    			alert("件号在零件表中没有找到!");
    			return 0;
    		}
    		if(imageid=="")
    		{
    			alert("此爆炸图不存在!");
    			return 0;
    		}
    		frmManage.hiddenasmvccode.value=document.all.asmvccode.value;
	    	frmManage.hiddenpartvccode.value=document.all.partvccode.value;
	    	frmManage.hiddenimagevccode.value=document.all.imagevccode.value;
	    	frmManage.hiddenihot.value=document.all.ihot.value;
	    	frmManage.hiddeniqty.value=document.all.iqty.value;
	    	frmManage.hiddenasmimagevccode.value=document.all.asmimagevccode.value;
	    	if(verifyAll(frmManage))
			{
	    		var openwindow=window.dialogArguments;
	    		var asmvccode_v=document.all.asmvccode.value;
	    		var partvccode_v=document.all.partvccode.value;
	    		var imagevccode_v=document.all.imagevccode.value;
	    		var ihot_v=document.all.ihot.value;
	    		var iqty_v=document.all.iqty.value;
	    		var asmimagevccode_v=document.all.asmimagevccode.value;    		
	    		if(mode=="new")
	    		{
	    			openwindow.newasm(asmvccode_v,partvccode_v,imagevccode_v,ihot_v,iqty_v,asmimagevccode_v);
	    		}
	    		else
	    		{
	    			var id="<%=id%>";
	    			openwindow.saveasm(id,asmvccode_v,partvccode_v,imagevccode_v,ihot_v,iqty_v,asmimagevccode_v);
	    		}
	    		window.close();
			}
		}
    }
    function onAfterGetAsmById(data)
    {
    	if(data.result!="0")
		{	
    		editasmdata=data;
    		var totalcount=getvalue(data.totalcount);
    		var iasmid_v=getvalue(data.iasmid);
    		var asmvccode_v=getvalue(data.asmvccode);    		
    		var partvccode_v=getvalue(data.partvccode);    		
    		var imagevccode_v=getvalue(data.imagevccode);    		
    		var ihot_v=getvalue(data.ihot);
    		var iqty_v=getvalue(data.iqty);    		
    		var asmimagevccode_v=getvalue(data.asmimagevccode);
    		var asmvccname_v=getvalue(data.asmvccname);
    		var partvccname_v=getvalue(data.partvccname);
    		var imagevccnote_v=getvalue(data.imagevccnote);
    		document.all.asmvccode.value=asmvccode_v;
    		document.all.partvccode.value=partvccode_v;    		
    		document.all.imagevccode.value=imagevccode_v;    		
    		document.all.ihot.value=ihot_v;
    		document.all.iqty.value=iqty_v;
    		document.all.asmimagevccode.value=asmimagevccode_v;
    		document.all.asmvccname.value=asmvccname_v;
    		document.all.partvccname.value=partvccname_v;
    		document.all.imagevccnote.value=imagevccnote_v;    		
	   }
    }
    function onError(XMLResponse)
    {
    	alert(XMLResponse.responseText);
    } 
    function onBeforeGetData()
    {
    	
    }
    function onComplete()
    {
    	
    }
    var typename="";
    function onGetDesc()
    {
    	var value=event.srcElement.value;
    	typename=event.srcElement.name;
    	var url="do.json?action=asmlinkmanage&method=getdesc&typename="+typename+"&value="+value;
		$.ajax({
		type : "GET",//使用post方法访问后台  
		dataType : "json",//返回json格式的数据  
		url : url,//要访问的后台地址 
		cache: false,//默认: true,设置为 false 将不会从浏览器缓存中加载请求信息。  
		contentType : "application/json;charset=utf-8",	
		beforeSend:onBeforeGetData,
		complete:onComplete,
		success:onAfterGetDesc,
		error:onError 
		});    
    }
    function onAfterGetDesc(data)
    {
    	var descobject=null;
   		if(typename=="asmvccode")
   		{
   			descobject=document.getElementById("asmvccname");
   		}
   		if(typename=="partvccode")
   		{
   			descobject=document.getElementById("partvccname");
   		}
   		if(typename=="imagevccode")
   		{
   			descobject=document.getElementById("imagevccnote");
   		}
    	if(data.result!="0")
		{
    		var desc=getvalue(data.desc);
    		
    		if(descobject!=null)
    		{
    			descobject.value=desc;
    		}
		}
		else
		{
			if(descobject!=null)
    		{
    			descobject.value="";
    		}
		}
    }
    </script>
  </head>  
  <body style="margin:0px" scroll=no onload="initpage();">
    <table width="100%" border="0" cellSpacing="0" cellPadding="0">
    <tr>
    <td valign="top">	     
	     <P class="r_l_3"></P>
		 <P class="r_l_2"></P>
		 <P class="r_l_1"></P>
	     <DIV class="w_l">		    
			 <H4>
			 <table width="100%" border="0" cellSpacing="0" cellPadding="0">
			 <td style="font-size:15px">
			   <%=dialogtitle%>
			 </td>
			 <td>
			 &nbsp;
			 </td>
			 <td align="right">
			 <input type="button" value="保存" onclick="save();"/>
			 <input type="button" value="关闭" onclick="closewindow();"/>
			 </td>
			 </H4>		
			 <DIV class="body">
			 <form name="frmManage" id="frmManage">			 
			 <input type="hidden" name="hiddenasmvccode" value="" tag="true,char,100,总成编号"/>
			 <input type="hidden" name="hiddenpartvccode" value="" tag="true,char,100,零件编号"/>
			 <input type="hidden" name="hiddenimagevccode" value="" tag="true,char,100,爆炸图编号"/>
			 <input type="hidden" name="hiddenihot" value="" tag="true,char,100,热点"/>
			 <input type="hidden" name="hiddeniqty" value="" tag="true,int,100,数量"/>
			 <input type="hidden" name="hiddenasmimagevccode" value="0"/>
			<table align='center' width="99%" border="1" cellspacing="0" cellpadding="2" style="word-wrap:break-word;" class="zw" bordercolorlight="#25689F" bordercolordark="#ffffff"'>
			<tr>
				<td width="100" class='cuteLable'>总成编号</td>
				<td>				    
					<input type="text" name="asmvccode" id="asmvccode" size="45" onchange="onGetDesc();"/>
				</td>
			</tr>
			<tr>
				<td width="100" class='cuteLable'>总成中文描述</td>
				<td>				    
					<input type="text" name="asmvccname" id="asmvccname" size="45" readOnly style="background-color:#DCDCDC"/>
				</td>
			</tr>
			<tr>
				<td width="100" class='cuteLable'>总成图编号</td>
				<td>				    
					<input type="text" name="asmimagevccode" id="asmimagevccode" size="45"/>
				</td>
			</tr>			
			<tr>
				<td width="100" class='cuteLable'>零件号</td>
				<td>
					<input type="text" id="partvccode" name="partvccode" size="45" onchange="onGetDesc();"/>
				</td>
			</tr>
			<tr>
				<td width="100" class='cuteLable'>零件中文描述</td>
				<td>
					<input type="text" id="partvccname" name="partvccname" size="45" readOnly style="background-color:#DCDCDC"/>
				</td>
			</tr>			
			<tr>
				<td width="100" class='cuteLable'>爆炸图编号</td>
				<td>
					<input type="text" name="imagevccode" id="imagevccode" size="45" onchange="onGetDesc();"/>
				</td>
			</tr>
			<tr>
				<td width="100" class='cuteLable'>爆炸图中文描述</td>
				<td>
					<input type="text" id="imagevccnote" name="imagevccnote" size="45" readOnly style="background-color:#DCDCDC"/>
				</td>
			</tr>			
			<tr>
				<td width="100" class='cuteLable'>热点</td>
				<td>
					<input type="text" name="ihot" id="ihot" size="45"/>
				</td>
			</tr>
			<tr>
				<td width="100" class='cuteLable'>数量</td>
				<td>
					<input type="text" name="iqty" id="iqty" size="45"/>
				</td>
			</tr>				
			</table>
			</form>	 		
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
