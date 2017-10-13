<%@ page contentType="text/html;charset=utf-8" pageEncoding="utf-8"%>
<%@ page import="java.util.*"%>
<%@ taglib uri="/WEB-INF/tld/datagrid.tld" prefix="grid"%>
<%@ page import="com.jl.action.*"%>
<%@ page import="com.jl.entity.*"%>
<%@ page import="java.net.*"%>
<%
	response.setHeader("Cache-Control", "no-cache");
	response.setHeader("Pragma", "no-cache");
	response.setDateHeader("Expires", 0);
	String viewdetail = "";
	int pagesize = 20000;//每页记录数量,可修改
	int width = 800;//表格宽度 ,可修改
	int DataWidth = 800;//数据宽度,可修改
	List ls = new ArrayList();
	CommonAction commonaction = new CommonAction(request);
	String partid = request.getParameter("partid");
	if(partid==null) partid="";
	ls=commonaction.getAsmLink(partid);
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
		<meta http-equiv="pragma" content="no-cache" />
		<meta http-equiv="X-UA-Compatible" content="IE=EmulateIE7" />
		<title>总成数据</title>
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
		function onLight() {
			var asmid=currentlightvalues[0];
        	parent.querybyasm(asmid);
		}
		
		function pagesize() {		
			document.getElementById("tC").style.height=(screen.availHeight-133)/2-25;
			document.all.tblStat.style.width=document.body.clientWidth;
			document.getElementById("tC").style.width=document.all.tblStat.style.width;
			document.getElementById("bottomtable").style.width=document.all.tblStat.style.width;
		}
		</script>
	</head>
	<body style="margin: 0px" scroll=no
		onload="pagesize();"
		onresize="pagesize();">
		<table width="100%" border="0" cellSpacing="0" cellPadding="0">
			<tr>
				<td valign="top" id="gridtd">
					<form name="frmShow" id="frmShow" method="POST" target="_self">
						<grid:dbgrid id="tblStat" name="tblStat" width="800"
							pageSize="<%=pagesize%>" pageObject="<%=pageContext%>"
							pageRequest="<%=request%>" border="0" cellSpacing="1"
							cellPadding="2" dataMember="" dataSource="<%=ls%>"
							totalRecords="<%=0%>" cssClass="gridTable" lightOn="true"
							tdIntervalColor="true" verticalHeight="375"
							gridPosition="absolute" dataWidth="800">
							<grid:rownumcolumn dataField="id" headerText="" width="8"
								HAlign="center" sortable="true" />
							<grid:textcolumn dataField="id" headerText="ID"
								HAlign="center" sortable="true" width="20" />
							<grid:textcolumn dataField="iasmid" headerText="asmid"
								HAlign="center" sortable="true" tagField="vcsendno" width="15" />
							<grid:textcolumn dataField="iimageid" headerText="imageid"
								HAlign="center" sortable="true" width="15"/>
							<grid:textcolumn dataField="imagenote" headerText="imagedesc"
								HAlign="center" sortable="true" />
						</grid:dbgrid>
					</form>
				</td>
			</tr>
		</table>
	</body>
</html>
