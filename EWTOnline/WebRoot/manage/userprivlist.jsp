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
int pagesize=20;//每页记录数量,可修改
int width=100;//表格宽度 ,可修改
int DataWidth=100;//数据宽度,可修改
List ls=new ArrayList();
AsmLinkAction asmlinkaction=new AsmLinkAction(request);
int totalresultcount=0;

if(request.getMethod().equalsIgnoreCase("post"))
{
	String asmvccode=request.getParameter("asmvccode");
	if(asmvccode==null) asmvccode="";
	if(asmvccode.trim().equals("")==false)
	{
		asmvccode=URLDecoder.decode(asmvccode,"UTF-8");
		asmvccode=asmvccode.trim();
		ls=asmlinkaction.getAsmLinkByVccode(asmvccode);
	}
	else
	{
		request.setAttribute("pagesize",String.valueOf(pagesize));
		totalresultcount=Integer.valueOf(asmlinkaction.getAsmLinkTotal()).intValue();
		ls=asmlinkaction.getAsmLinks();
	}
}
else
{
	request.setAttribute("pagesize",String.valueOf(pagesize));
	totalresultcount=Integer.valueOf(asmlinkaction.getAsmLinkTotal()).intValue();
	ls=asmlinkaction.getAsmLinks();
}
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
  	<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
  	<meta http-equiv="pragma" content="no-cache" />
  	<META content=IE=EmulateIE7 http-equiv=X-UA-Compatible>  	
    <title>总成包管理</title>
    <link REL="StyleSheet" HREF="../css/gridstyle.css">
    <link REL="StyleSheet" HREF="../css/show.css">
    <link REL="StyleSheet" HREF="../css/globle.css">
    <link REL="StyleSheet" HREF="css/smpartmanage.css">
    <script Charset="GBK" src="../scripts/datagrid/datagrid.js" type="text/javascript"></script>	
	<script Charset="GBK" type='text/javascript' src='../scripts/common.js'></script>	
	<script Charset="GBK" src="../scripts/ValidInput.js" type='text/javascript'> </script>
	<script Charset="UTF-8" type="text/javascript" language="javascript" src="../scripts/jquery/json2.js" ></script>  
	<script Charset="UTF-8" type="text/javascript" language="javascript" src="../scripts/jquery/jquery-1.7.min.js" ></script>
	<script Charset="UTF-8" type='text/javascript' src='../scripts/ajaxUtil.js'></script>
    <script language="javascript">
    var mode="";
    var callbackresult;
    function saveasm(id,asmvccode,partvccode,imagevccode,ihot,iqty)
    {
    	var url="do.json?action=asmlinkmanage&method=saveasm&id="+id+"&asmvccode="+asmvccode+"&partvccode="+partvccode+"&imagevccode="+imagevccode+"&ihot="+ihot+"&iqty="+iqty;
    	var data="({";
    	//data=data+"id:"+id+",";
    	//data=data+"asmvccode:"+asmvccode+",";
    	//data=data+"partvccode:"+partvccode+",";
    	//data=data+"imagevccode:"+imagevccode+",";
    	//data=data+"ihot:"+ihot+",";
    	//data=data+"iqty:"+iqty;
    	data=data+"})";
    	var jsonobject=eval(data);    	
		$.ajax({
		type : "GET",//使用post方法访问后台  
		dataType : "json",//返回json格式的数据  
		url : url,//要访问的后台地址 
		cache: false,//默认: true,设置为 false 将不会从浏览器缓存中加载请求信息。  
		contentType : "application/json;charset=utf-8",
		data:jsonobject,
		beforeSend:onBeforeSaveAsm,
		complete:onComplete,
		success:onAfterSaveAsm,
		error:onError 
		});
	}
    function onBeforeSaveAsm()
    {
    	
    }
    function onAfterSaveAsm(data)
    {
    	if(data.result!="0")
		{	
    		var dowhat=data.dowhat;
    		var id=data.id;
    		var asmvccode=data.asmvccode;
    		var partvccode=data.partvccode;
    		var imagevccode=data.imagevccode;
    		var iqty=data.iqty;
    		var ihot=data.ihot;
    		var asmcnote=getvalue(data.asmcnote);
    		var partcnote=getvalue(data.partcnote);
    		var imagecnote=getvalue(data.imagecnote);		            
    		var values=escape(trim(id))+","+escape(trim(asmvccode))+","+escape(trim(partvccode))+","+escape(trim(imagevccode))+","+escape(trim(asmcnote))+","+escape(trim(partcnote))+","+escape(trim(imagecnote))+","+escape(trim(ihot))+","+escape(trim(iqty));
    		if(dowhat=="new")
    		{
				onNewRow(values);
			}
    		if(dowhat=="edit")
    		{	    		
				onSaveRow(id,values);
			}
    	}
    }    
    function onLight()
    {        
        var id=currentlightvalues[0];//id
        //alert(id);
    }
    function pagesize()
    {
    	var avaiheight = window.screen.height-window.screenTop;	
    	fixgridsize(avaiheight-110,gridtd.clientWidth);
    }
    function onComplete()
    {
    	
    }
    function onError(XMLResponse)
    {
    	alert(XMLResponse.responseText);
    }
    function ondblrow()
    {    	
    	var surl="asmlinkdialog.jsp?method=edit&id="+currentlightvalues[0];
    	var parames="dialogHeight:250px;dialogWidth:400px;center:yes";
    	window.showModelessDialog(surl,window,parames);
    }
    function newasm(asmvccode,partvccode,imagevccode,ihot,iqty)
    {
    	var url="do.json?action=asmlinkmanage&method=newasm&asmvccode="+asmvccode+"&partvccode="+partvccode+"&imagevccode="+imagevccode+"&ihot="+ihot+"&iqty="+iqty;
    	var data="({";
    	//data=data+"asmvccode:"+asmvccode+",";
    	//data=data+"partvccode:"+partvccode+",";
    	//data=data+"imagevccode:"+imagevccode+",";
    	//data=data+"ihot:"+ihot+",";
    	//data=data+"iqty:"+iqty;
    	data=data+"})";    	
    	var jsonobject=eval(data);    	
		$.ajax({
		type : "GET",//使用post方法访问后台  
		dataType : "json",//返回json格式的数据  
		url : url,//要访问的后台地址 
		cache: false,//默认: true,设置为 false 将不会从浏览器缓存中加载请求信息。  
		contentType : "application/json;charset=utf-8",
		data:jsonobject,
		beforeSend:onBeforeNewAsm,
		complete:onComplete,
		success:onAfterSaveAsm,
		error:onError 
		});
	}
	function onBeforeNewAsm()
	{
		
	}	
	function deleteAsm()
	{
		var id=currentlightvalues[0];			
		onDeleteRowByIndex(currentLine);
	}
	function delAsmLink()
	   {
	   	var ids=getSelectIds();
	   	if(ids=="")
	   	{
	   		alert("请选择删除的记录!");
	   		return;
	   	}
		var url="do.json?action=asmlinkmanage&method=delete&ids="+ids;
		$.ajax({
		type : "GET",//使用post方法访问后台  
		dataType : "json",//返回json格式的数据  
		url : url,//要访问的后台地址 
		cache: false,//默认: true,设置为 false 将不会从浏览器缓存中加载请求信息。  
		contentType : "application/json;charset=utf-8",	
		beforeSend:onBeforeDeleteAsmLink,
		complete:onComplete,
		success:onAfterDeleteAsmLink,
		error:onError
		});
	   }
	   function onBeforeDeleteAsmLink()
	   {
	   	
	   }
	  function onAfterDeleteAsmLink(returnobject)
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
			var totalcount=document.all.totalcount.innerText;
	   		if(totalcount=="0")
	   		{			
				deleteAsm();
			}
		}
	}
  </script>
  </head>  
  <body style="margin:0px" scroll=no onload="pagesize();" onresize="pagesize();">
    <table width="100%" border="0" cellSpacing="0" cellPadding="0">
    <tr>
		<td valign="top" id="gridtd">
		    <P class="r_l_3"></P>
			 <P class="r_l_2"></P>
			 <P class="r_l_1"></P>
		     <DIV class="w_l">	
			 <DIV class="body">
			<form name="frmShow" id="frmShow" method="POST" target="_self">
			 <grid:dbgrid id="tblStat" name="tblStat" width="500" pageSize="<%=pagesize%>" pageObject="<%=pageContext%>" pageRequest="<%=request%>"
				border="0" cellSpacing="1" cellPadding="2" dataMember="" dataSource="<%=ls%>" totalRecords="<%=0%>"
				cssClass="gridTable" lightOn="true" tdIntervalColor="true" verticalHeight="375" gridPosition="absolute" dataWidth="500" viewDetail="<%=viewdetail%>" 
				totalRecords="<%=totalresultcount%>" viewWidth="860" viewHeight="600">           
		            <grid:gridpager imgFirst="../images/datagrid/First.gif" 
		            imgPrevious="../images/datagrid/Previous.gif" 
		            imgNext="../images/datagrid/Next.gif" 
		            imgLast="../images/datagrid/Last.gif" 
		            imgBackground="../images/datagrid/di-bt.gif"/>
		            <grid:textcolumn dataField="id" headerText="序号" HAlign="center" sortable="true" width="5"/>		            
		            <grid:checkboxcolumn dataField="id" headerText="checkbox" HAlign="center" sortable="true" width="2"/>
		            <grid:textcolumn dataField="asmvccode" headerText="总成编号" HAlign="center" sortable="true" width="8" tagField="id"/>		            
		            <grid:textcolumn dataField="partvccode" headerText="件号" HAlign="center" sortable="true" width="8"/>
		            <grid:textcolumn dataField="imagevccode" headerText="爆炸图编号" HAlign="center" sortable="true" width="8"/>
		            <grid:textcolumn dataField="asmcnote" headerText="总成描述" HAlign="center" sortable="true" width="12"/>
		            <grid:textcolumn dataField="partcnote" headerText="件号描述" HAlign="center" sortable="true" width="12"/>		            
		            <grid:textcolumn dataField="imagecnote" headerText="爆炸图描述" HAlign="center" sortable="true" width="12"/>	
		            <grid:textcolumn dataField="ihot" headerText="热点" HAlign="center" sortable="true" width="5"/>
		            <grid:textcolumn dataField="iqty" headerText="数量" HAlign="center" sortable="true" width="5"/>			            					           
		      </grid:dbgrid>
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
