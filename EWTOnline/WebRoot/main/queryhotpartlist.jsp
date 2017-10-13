<%@ page contentType="text/html;charset=utf-8" pageEncoding="utf-8"%>
<%@ page import="java.util.*"%>
<%@ taglib uri="/WEB-INF/tld/datagrid.tld" prefix="grid"%>
<%@ page import="com.jl.action.*"%>
<%@ page import="com.jl.entity.*"%>
<%@ page import="java.net.*"%>
<%@include file="checksession.jsp"%>
<%
	response.setHeader("Cache-Control", "no-cache");
	response.setHeader("Pragma", "no-cache");
	response.setDateHeader("Expires", 0);
	String viewdetail = "";
	String vehid=request.getParameter("vehid");
	String pn=request.getParameter("pn");
	String pd=request.getParameter("pd");
	String userid=request.getSession().getAttribute("currentuserid").toString();
	if(vehid==null) vehid="";
	if(pn==null) pn="";
	if(pd==null) pd="";
	pn=pn.toUpperCase();
	pd=pd.toUpperCase();
	int pagesize = 5000;//每页记录数量,可修改
	int width = 850;//表格宽度 ,可修改
	int DataWidth = 850;//数据宽度,可修改
	List ls = new ArrayList();	
	SmPartAction partaction = new SmPartAction(request);
	if(vehid.equalsIgnoreCase("")==false)
	{
		ls=partaction.getSmpartByQuery(vehid,pn,pd);
	}
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
		<meta http-equiv="pragma" content="no-cache" />
		<meta http-equiv="X-UA-Compatible" content="IE=EmulateIE7" />
		<title>零件数据</title>
		<link REL="StyleSheet" HREF="../css/gridstyle.css">
		<link REL="StyleSheet" HREF="../css/show.css">
		<link REL="StyleSheet" HREF="../css/globle.css">
		<link REL="StyleSheet" HREF="css/smpartmanage.css">
		<script Charset="GBK" src="../scripts/datagrid/datagrid.js" type="text/javascript"></script>
		<script Charset="GBK" type='text/javascript' src='../scripts/common.js'></script>
		<script Charset="GBK" src="../scripts/base64.js" type='text/javascript'></script>
		<script Charset="GBK" src="../scripts/ValidInput.js" type='text/javascript'></script>
		<script Charset="UTF-8" type="text/javascript" language="javascript" src="../scripts/jquery/json2.js"></script>
		<script Charset="UTF-8" type="text/javascript" language="javascript" src="../scripts/jquery/jquery-1.7.min.js"></script>
		<script Charset="UTF-8" type='text/javascript' src='../scripts/ajaxUtil.js'></script>
		<script Charset="UTF-8" type="text/javascript" src="../ueditor/editor_config.js"></script>
		<script Charset="UTF-8" type="text/javascript" src="../ueditor/editor_all.js"></script>
		<link rel="stylesheet" href="../ueditor/themes/default/ueditor.css" />
		<script language="javascript">
		function gethightrow(hot)
		{
			var key;
			var rowobject=null;
			for(var i=0;i<document.all.tablebody.rows.length;i++)
		   	{
	   			var row=document.all.tablebody.rows(i);   					
   				if(typeof(row.cells(0).tag)=="undefined")   				
   				 key=row.cells(0).innerText;
   				else
   				 key=row.cells(0).tag;
   				var keys=key.split(",");
   				if(keys[1]==hot)
  				{
  					rowobject=row;
  					break;
  				}
		   	}
			return rowobject;
		}
		
		function onLight() {			
			
		}
		function viewpartimage()
		{
			var smpartid=event.srcElement.id;
			window.showModelessDialog("../manage/smpartview.jsp?smpartid="+smpartid, window,
			"center:yes;dialogWidth:1024px;dialogHeight:800px");
		}
		function showimage()
		{
			var key=currentRowvalue;			
			parent.showimage(key);
				
		}	
		function pagesize() {
			document.getElementById("tC").style.height=420;
			document.all.tblStat.style.width=900-20;			
			document.getElementById("tC").style.width=900;
			//document.getElementById("bottomtable").style.width=document.body.clientWidth;			
		}
		function ondblrow()
		{
			var key=currentRowvalue;
			parent.showimage(key);
		}
</script>
	</head>
	<body onload="pagesize();" onresize="pagesize();" scroll="no">		
	<form name="frmShow" id="frmShow" method="POST" target="_self">
		<grid:dbgrid id="tblStat" name="tblStat" width="<%=width%>"
			pageSize="<%=pagesize%>" pageObject="<%=pageContext%>"
			pageRequest="<%=request%>" border="0" cellSpacing="1"
			cellPadding="2" dataMember="" dataSource="<%=ls%>"
			totalRecords="<%=0%>" cssClass="gridTable" lightOn="true"
			tdIntervalColor="false" verticalHeight="400"
			gridPosition="absolute" dataWidth="<%=DataWidth%>">														
			<grid:textcolumn dataField="vccode" headerText="P/N" width="15" HAlign="center" sortable="true" tagField="iimageid" isShowTitle="false"/>							
			<grid:textcolumn dataField="vcename" headerText="<center>P/D</center>" HAlign="left" sortable="true" isShowTitle="false" cssClass="lefttd"/>
			<grid:textcolumn dataField="imagevccode" headerText="Image" HAlign="center" sortable="true" isShowTitle="false" width="20"/>
			<grid:textcolumn dataField="t2" headerText="Sub-Sys" HAlign="center" sortable="true" isShowTitle="false" width="20"/>
		</grid:dbgrid>
	</form>				
	</body>
</html>
