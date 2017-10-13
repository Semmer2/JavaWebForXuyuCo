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
VehLinkAction vehlinkaction=new VehLinkAction(request);
int totalresultcount=0;
String vehvccode="";
if(request.getMethod().equalsIgnoreCase("post"))
{
	vehvccode=request.getParameter("vehvccode");
	if(vehvccode==null) vehvccode="";
	vehvccode=vehvccode.toUpperCase();
	if(vehvccode.trim().equals("")==false)
	{
		vehvccode=URLDecoder.decode(vehvccode,"UTF-8");
		vehvccode=vehvccode.trim();
		ls=vehlinkaction.getVehLinkByVccode(vehvccode);
	}
	else
	{
		request.setAttribute("pagesize",String.valueOf(pagesize));
		totalresultcount=Integer.valueOf(vehlinkaction.getVehLinkTotal()).intValue();
		ls=vehlinkaction.getVehLinks();
	}
}
else
{
	request.setAttribute("pagesize",String.valueOf(pagesize));
	totalresultcount=0;//Integer.valueOf(vehlinkaction.getVehLinkTotal()).intValue();
	ls=vehlinkaction.getVehLinkByVccode("-99999999");
}

%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
  	<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
  	<meta http-equiv="pragma" content="no-cache" />
  	<META content=IE=EmulateIE7 http-equiv=X-UA-Compatible>  	
    <title>车型BOM管理</title>
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
    function savevehlink(id,vehvccode,partvccode,imagevccode,ihot,iqty)
    {
    	var url="do.json?action=vehlinkmanage&method=savevehlink&id="+id+"&vehvccode="+vehvccode+"&partvccode="+partvccode+"&imagevccode="+imagevccode+"&ihot="+ihot+"&iqty="+iqty;
    	var data="({";
    	//data=data+"id:"+id+",";
    	//data=data+"vehvccode:"+vehvccode+",";
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
		beforeSend:onBeforeSaveVehLink,
		complete:onComplete,
		success:onAfterSaveVehLink,
		error:onError 
		});
	}
    function onBeforeSaveVehLink()
    {
    	
    }
    function onAfterSaveVehLink(data)
    {
    	if(data.result!="-1" && data.result!="")
		{	
    		var dowhat=data.dowhat;
    		var id=data.id;
    		var vehvccode=data.vehvccode;
    		var vehvccnote=getvalue(data.vehvccnote);	
    		var partvccode=data.partvccode;
    		var partvccname=getvalue(data.partvccname);    		
    		var imagevccode=data.imagevccode;
    		var imagevccnote=getvalue(data.imagevccnote);
    		var iqty=data.iqty;
    		var ihot=data.ihot; 		
    		var values=trim(id)+","+trim(vehvccode)+","+escape(trim(vehvccnote))+","+trim(partvccode)+","+escape(trim(partvccname))+","+trim(imagevccode)+","+escape(trim(imagevccnote))+","+trim(ihot)+","+trim(iqty);
    		if(dowhat=="new")
    		{
				onNewRow(values);
			}
    		if(dowhat=="edit")
    		{	    		
				onSaveRow(id,values);
			}
    	}
    	else
    	{
    		alert(getvalue(data.error));
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
    	parent.showdata();
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
    	var surl="vehlinkdialog.jsp?method=edit&id="+currentlightvalues[0];
    	var parames="dialogHeight:300px;dialogWidth:400px;center:yes";
    	window.showModelessDialog(surl,window,parames);
    }
    function newvehlink(vehvccode,partvccode,imagevccode,ihot,iqty)
    {
    	var url="do.json?action=vehlinkmanage&method=newvehlink&vehvccode="+vehvccode+"&partvccode="+partvccode+"&imagevccode="+imagevccode+"&ihot="+ihot+"&iqty="+iqty;
    	var data="({";
    	//data=data+"vehvccode:"+vehvccode+",";
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
		beforeSend:onBeforeNewVehLink,
		complete:onComplete,
		success:onAfterSaveVehLink,
		error:onError 
		});
	}
	function onBeforeNewVehLink()
	{
		
	}	
	function deleteVehLink()
	{
		var id=currentlightvalues[0];			
		onDeleteRowByIndex(currentLine);
	}
	function delVehLink()
	   {
	   	var ids=getSelectIds();
	   	if(ids=="")
	   	{
	   		alert("请选择删除的记录!");
	   		return;
	   	}
		var url="do.json?action=vehlinkmanage&method=delete&ids="+ids;
		$.ajax({
		type : "GET",//使用post方法访问后台  
		dataType : "json",//返回json格式的数据  
		url : url,//要访问的后台地址 
		cache: false,//默认: true,设置为 false 将不会从浏览器缓存中加载请求信息。  
		contentType : "application/json;charset=utf-8",	
		beforeSend:onBeforeDeleteVehLink,
		complete:onComplete,
		success:onAfterDeleteVehLink,
		error:onError
		});
	   }
	   function onBeforeDeleteVehLink()
	   {
	   	
	   }
	  function onAfterDeleteVehLink(returnobject)
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
				deleteVehLink();
			}
		}
	}
  </script>
  </head>  
  <body style="margin:0px" scroll=no onload="pagesize();" onresize="pagesize();">
    <table width="100%" border="0" cellSpacing="0" cellPadding="0">
    <tr>
		<td valign="top" id="gridtd">		   
			<form name="frmShow" id="frmShow" method="POST" target="_self">
			<input type="hidden" name="vehvccode" id="vehvccode" value="<%=vehvccode%>"/>
			 <grid:dbgrid id="tblStat" name="tblStat" width="500" pageSize="<%=pagesize%>" pageObject="<%=pageContext%>" pageRequest="<%=request%>"
				border="0" cellSpacing="1" cellPadding="2" dataMember="" dataSource="<%=ls%>" 
				cssClass="gridTable" lightOn="true" tdIntervalColor="true" verticalHeight="375" gridPosition="absolute" dataWidth="500" viewDetail="<%=viewdetail%>" 
				totalRecords="<%=totalresultcount%>" viewWidth="860" viewHeight="600">           
		            <grid:gridpager imgFirst="../images/datagrid/First.gif" 
		            imgPrevious="../images/datagrid/Previous.gif" 
		            imgNext="../images/datagrid/Next.gif" 
		            imgLast="../images/datagrid/Last.gif" 
		            imgBackground="../images/datagrid/di-bt.gif"/>
		            <grid:textcolumn dataField="id" headerText="序号" HAlign="center" sortable="true" width="5"/>		            
		            <grid:checkboxcolumn dataField="id" headerText="选择" HAlign="center" sortable="true" width="3"/>
		            <grid:textcolumn dataField="vehvccode" headerText="<center>车型编号</center>" HAlign="left" sortable="true" tagField="id"/>
		            <grid:textcolumn dataField="vehvccnote" headerText="车型描述" HAlign="center" sortable="true" tagField="id"/>		            			            
		            <grid:textcolumn dataField="partvccode" headerText="零件号" HAlign="center" sortable="true" />
		            <grid:textcolumn dataField="partvccname" headerText="零件中文描述" HAlign="center" sortable="true" />
		            <grid:textcolumn dataField="imagevccode" headerText="爆炸图号" HAlign="center" sortable="true"/>
		            <grid:textcolumn dataField="imagevccnote" headerText="爆炸图中文描述" HAlign="center" sortable="true"/>		            	
		            <grid:textcolumn dataField="ihot" headerText="热点" HAlign="center" sortable="true" width="5"/>
		            <grid:textcolumn dataField="iqty" headerText="数量" HAlign="center" sortable="true" width="5"/>			            					           
		      </grid:dbgrid>
		    </form>		 
		</td>
    </tr>
    </table>        
  </body>
</html>
