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
	String imageid=request.getParameter("imageid");
	String hotid=request.getParameter("hotid");
	String userid=request.getSession().getAttribute("currentuserid").toString();
	if(vehid==null) vehid="";
	if(imageid==null) imageid="";
	if(hotid==null) hotid="";
	int pagesize = 5000;//每页记录数量,可修改
	int width = 300;//表格宽度 ,可修改
	int DataWidth = 300;//数据宽度,可修改
	List ls = new ArrayList();	
	SmPartAction partaction = new SmPartAction(request);
	if(vehid.equalsIgnoreCase("")==false)
	{
		if(imageid.equalsIgnoreCase("") && hotid.equalsIgnoreCase(""))
		{
			ls=partaction.getSmpartsByVehId(vehid);
		}
		else
		{
			if(imageid.equalsIgnoreCase("")==false && hotid.equalsIgnoreCase(""))
			{
				ls=partaction.getSmpartsbyImageId(vehid,imageid);
			}
			else
			{
				ls=partaction.getSmparts(vehid,hotid,imageid);
			}
		}
	}
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
		<meta http-equiv="pragma" content="no-cache" />
		<meta http-equiv="X-UA-Compatible" content="IE=EmulateIE7" />
		<title>零件数据</title>
		<link REL="StyleSheet" HREF="../css/globle.css">
		<link REL="StyleSheet" HREF="../css/gridstyle.css">    	
		<script Charset="GBK" src="../scripts/datagrid/datagrid.js" type="text/javascript"></script>
		<script Charset="GBK" type='text/javascript' src='../scripts/common.js'></script>
		<script Charset="GBK" src="../scripts/base64.js" type='text/javascript'></script>
		<script Charset="GBK" src="../scripts/ValidInput.js" type='text/javascript'></script>
		<script Charset="UTF-8" type="text/javascript" language="javascript" src="../scripts/jquery/json2.js"></script>
		<script Charset="UTF-8" type="text/javascript" language="javascript" src="../scripts/jquery/jquery-1.7.min.js"></script>
		<script Charset="UTF-8" type='text/javascript' src='../scripts/ajaxUtil.js'></script>
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
			var key=currentRowvalue;
			var keys=key.split(",");
			var vccode=keys[0];
			var hot=keys[1];
			parent.showhotatsvg(hot);
		}		
		function pagesize() {
			parent.showcontent();
			if(document.body.clientWidth>20)
			{
				document.getElementById("tC").style.height=parent.svgheight-parent.buyframeheight;
				if(document.all.tblStat.scrollHeight<(parent.svgheight-parent.buyframeheight))
					document.all.tblStat.style.width=document.body.clientWidth;//没有scroll
				else
					document.all.tblStat.style.width=document.body.clientWidth-20;			
				document.getElementById("tC").style.width=document.body.clientWidth;
				//document.getElementById("bottomtable").style.width=document.body.clientWidth;
			}			
		}
		function viewpartimage()
		{
			var smpartvccode=event.srcElement.parentElement.tag;			
			//window.showModelessDialog("../manage/smpartview.jsp?smpartvccode="+smpartvccode, window,
			//"center:yes;dialogWidth:1024px;dialogHeight:600px");
			window.showModelessDialog("../manage/smpartphotoview.jsp?smpartvccode="+smpartvccode, window,
			"center:yes;dialogWidth:770px;dialogHeight:550px");
		}
		function addsmpartcar()
		{
			lightOnByRow(event.srcElement.parentElement.parentElement);
			var key=currentRowvalue;
			var keys=key.split(",");
			var vccode=keys[0];
			var hot=keys[1];
			var vcename=currentlightvalues[1];
			var qty=currentlightvalues[2];			
			addbuycar(vccode,vcename,qty);
		}
		function addbuycar(vccode,vcename,qty)
		{
			var url="../servlet.jsp?action=buycarmanage&method=addqty&vccode="+vccode+"&vcename="+vcename+"&qty="+qty;
			callbackresult=send_request(url,onCallBackForSaveRow);
		}
		function onCallBackForSaveRow(callbacktext)
		{	
			var result = trim(callbacktext.responseText);
			var rtn=trim(result);
			var returns=rtn.split(",");
			if(returns[0]=="")
			{
		    	alert(returns[1]);
		    }
			else
			{
				window.open("buycar.jsp","buypartframe");
			}
		}
		viewpartimage(); 
</script>
	</head>
	<body onload="pagesize();" onresize="pagesize();" scroll="no" style="margin:0px;padding:0px;">
		<form name="frmShow" id="frmShow" method="POST" target="_self">
			<grid:dbgrid id="tblStat" name="tblStat" width="<%=width%>"
				pageSize="<%=pagesize%>" pageObject="<%=pageContext%>"
				pageRequest="<%=request%>" border="0" cellSpacing="1"
				cellPadding="0" dataMember="" dataSource="<%=ls%>"
				totalRecords="<%=0%>" cssClass="gridTable" lightOn="true"
				tdIntervalColor="false" verticalHeight="400"
				gridPosition="absolute" dataWidth="<%=DataWidth%>">
				<grid:textcolumn dataField="ihot" headerText="&nbsp;" HAlign="center" sortable="true" width="5" tagField="key" isShowTitle="false"/>							
				<grid:textcolumn dataField="vccode" headerText="P/N" width="20" HAlign="center" sortable="true" isShowTitle="false"/>							
				<grid:textcolumn dataField="vcename" headerText="<center>P/D</center>" HAlign="left" sortable="true" isShowTitle="false" cssClass="lefttd"/>
				<grid:textcolumn dataField="rl" headerText="S,Superseded" HAlign="center" sortable="true" width="5" />							
				<grid:textcolumn dataField="iqty" headerText="Q,Qty" HAlign="center" sortable="true" width="5" isShowTitle="false" />														
				<grid:textcolumn alterText="View Photo" dataField="P" headerText="P,Photo" HAlign="center" sortable="true" width="5" doClick="viewpartimage()" tagField="vccode"/>
				<grid:textcolumn alterText="Add" dataField="O" headerText="O,Order" HAlign="center" width="5" doClick="addsmpartcar()"/>							
			</grid:dbgrid>
		</form>				
	</body>
</html>
