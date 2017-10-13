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
	int pagesize=20;//每页记录数量,可修改
	int width =650;//表格宽度 ,可修改
	int DataWidth = 630;//数据宽度,可修改
	List ls = new ArrayList();	
	BuyCarAction buycaraction = new BuyCarAction(request);
	ls=buycaraction.getBuycar(userid);
	int rscount=ls.size();
	String title="Order list";
for(int i=0;i<9999;i++)
{
	title=title+"&nbsp;";
}
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
		<meta http-equiv="pragma" content="no-cache" />
		<meta http-equiv="X-UA-Compatible" content="IE=EmulateIE7" />
		<title><%=title%></title>
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
		var carcount=0;
		var parentwindow=null;
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
			parentwindow.refreshbuy();
		}
		function pagesize() {
			parentwindow=window.dialogArguments;
			carcount="<%=rscount%>";
			if(carcount=="0")
				parentwindow.document.all.buyflag.style.display="none";
			else
				parentwindow.document.all.buyflag.style.display="block";
			
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
			    if(carcount=="0") parentwindow.document.all.buyflag.style.display="none";
			    parentwindow.refreshbuy();
		    }
		    else
		    {
		    	alert(returns[1]);
		    }
		}
		function download()
		{
			window.open("exportbuycar.jsp?output=all","hiddenframe");	
		}
</script>
	</head>
	<body onload="pagesize();" onresize="pagesize();" scroll="no">
		<table width="100%" border="0" cellSpacing="0" cellPadding="0">
			<tr>
				<td style="border-bottom:1px groove #000000">			
					<img src="../images/blue.png" width="16" height="16"/>
				</td>			
				<td width="200" style="border-bottom:1px groove #000000;font-size:12px">
					<b>Order List</b>
				</td>
				<td width="70%" style="border-bottom:1px groove #000000" align="right" style="padding-right:5px">
					<span style="cursor:hand;font-size:12px" onclick="download();">Download</span>
				</td>
			</tr>
			<tr>
				<td valign="top" id="gridtd" colspan="3">
					<form name="frmShow" id="frmShow" method="POST" target="_self">					
						<grid:dbgrid id="tblStat" name="tblStat" width="<%=width%>"
							pageSize="<%=pagesize%>" pageObject="<%=pageContext%>"
							pageRequest="<%=request%>" border="0" cellSpacing="1"
							cellPadding="2" dataMember="" dataSource="<%=ls%>"
							totalRecords="<%=0%>" cssClass="gridTable" lightOn="true"
							tdIntervalColor="false" verticalHeight="400"
							gridPosition="absolute" dataWidth="<%=DataWidth%>" showHead="true">
							<grid:rownumcolumn dataField="id" headerText="&nbsp;" width="5" HAlign="center" sortable="true" isShowTitle="false"/>
							<grid:textcolumn dataField="vccode" headerText="P/N" width="20" HAlign="center" sortable="true" tagField="id" isShowTitle="false"/>
							<grid:textcolumn dataField="vcename" headerText="P/D" HAlign="left" sortable="true" isShowTitle="false" cssClass="lefttd"/>
							<grid:textcolumn dataField="iqtyhtml" headerText="Order" HAlign="center" sortable="true" width="15" isShowTitle="false" />
							<grid:textcolumn alterText="Remove" dataField="R" headerText="&nbsp;" HAlign="center" width="8" doClick="removesmpartcar()" tagField="id" isShowTitle="false"/>
														
						</grid:dbgrid>
					</form>
				</td>
			</tr>
		</table>
		<iframe name="hiddenframe" id="hiddenframe" width="1" height="1" style="display:none"></iframe>		
	</body>
</html>