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
	String userid=request.getSession().getAttribute("currentuserid").toString();	
	int pagesize = 3;//每页记录数量,可修改
	int width = 300;//表格宽度 ,可修改
	int DataWidth = 300;//数据宽度,可修改
	List ls = new ArrayList();	
	BuyCarAction buycaraction = new BuyCarAction(request);
	ls=buycaraction.getBuycar(userid);
	int rscount=ls.size();
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
		<meta http-equiv="pragma" content="no-cache" />
		<meta http-equiv="X-UA-Compatible" content="IE=EmulateIE7" />
		<title>Buy Car</title>
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
		var carcount=0;
		function onLight() {
			//alert(currentRowvalue);
		}
		function isInteger( str ){
		var regu = /^[-]{0,1}[0-9]{1,}$/;
		return regu.test(str);
		}
		function saveqty()
		{
			var qtyvalue=event.srcElement.value;
			var id=event.srcElement.id;
			if(isInteger(qtyvalue)==false)
			{
				alert("请输入有效整数值!");
				return;
			}
			var url="../servlet.jsp?action=buycarmanage&method=updateqty&parames="+qtyvalue+"&datakey="+id;
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
		}
		function pagesize() {
			parent.showcontent();
			carcount="<%=rscount%>";
			if(carcount=="0")
				parent.document.all.buyflag.style.display="none";
			else
				parent.document.all.buyflag.style.display="block";
			if(document.body.clientWidth>20)
			{
				document.getElementById("tC").style.height=parent.buyframeheight;
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
			var smpartid=event.srcElement.id;
			window.showModelessDialog("../manage/smpartview.jsp?smpartid="+smpartid, window,
			"center:yes;dialogWidth:1024px;dialogHeight:800px");
		}
		function removesmpartcar()
		{
			var id=currentRowvalue;			
			var clickrow;
			var datagridrow=event.srcElement.parentElement.parentElement;
			if(typeof(datagridrow.cells(0).tag)=="undefined")   				
 				clickrow=datagridrow.cells(0).innerText;
 			else
 				clickrow=datagridrow.cells(0).tag;
 			id=clickrow;
	    	var url="../servlet.jsp?action=buycarmanage&method=delete&id="+id;
			callbackresult=send_request(url,onCallBackForDeleteRow);
		}
		function onCallBackForDeleteRow(callbacktext)
		{	
			var result=trim(callbacktext.responseText);
			var rtn=trim(result);
			var returns=rtn.split(",");
			if(returns[0]!="")
			{			
			    onDeleteRowByIndex(currentLine);
			    carcount--;
			    if(carcount=="0") parent.document.all.buyflag.style.display="none";				
		    }
		    else
		    {
		    	alert(returns[1]);
		    }
		}
</script>
	</head>
	<body onload="pagesize();" onresize="pagesize();" scroll="no" style="margin:0px;padding:0px;">
		<table width="100%" border="0" cellSpacing="0" cellPadding="0">
			<tr>
				<td valign="top" id="gridtd">
					<form name="frmShow" id="frmShow" method="POST" target="_self">					
						<grid:dbgrid id="tblStat" name="tblStat" width="<%=width%>"
							pageSize="<%=pagesize%>" pageObject="<%=pageContext%>"
							pageRequest="<%=request%>" border="0" cellSpacing="1"
							cellPadding="0" dataMember="" dataSource="<%=ls%>"
							totalRecords="<%=0%>" cssClass="gridTable" lightOn="true"
							tdIntervalColor="false" verticalHeight="400"
							gridPosition="absolute" dataWidth="<%=DataWidth%>" showHead="false">
							<grid:rownumcolumn dataField="vccode" headerText="&nbsp;" width="5" HAlign="center" sortable="true" isShowTitle="false"/>							
							<grid:textcolumn dataField="vccode" headerText="P/N" width="25" HAlign="center" sortable="true" tagField="id" isShowTitle="false"/>
							<grid:textcolumn dataField="vcename" headerText="P/D" HAlign="left" sortable="true" isShowTitle="false" cssClass="lefttd"/>
							<grid:textcolumn dataField="iqtyhtml" headerText="Q" HAlign="center" sortable="true" width="15" isShowTitle="false"/>							
							<grid:textcolumn alterText="Remove" dataField="R" headerText="Remove" HAlign="center" width="5" doClick="removesmpartcar()" isShowTitle="false"/>														
						</grid:dbgrid>
					</form>
				</td>
			</tr>
		</table>		
	</body>
</html>
