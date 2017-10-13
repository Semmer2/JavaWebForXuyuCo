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
	int pagesize = 20000;//每页记录数量,可修改
	int width = 800;//表格宽度 ,可修改
	int DataWidth = 800;//数据宽度,可修改
	List ls = new ArrayList();
	CommonAction commonaction = new CommonAction(request);	
	String asmid = request.getParameter("asmid");
	if(asmid==null) asmid="";
	String vinesn = request.getParameter("vinesn");
	if(vinesn==null) vinesn="";
	ls = commonaction.queryVehicle(asmid,vinesn);
	
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
		<meta http-equiv="pragma" content="no-cache" />
		<meta http-equiv="X-UA-Compatible" content="IE=EmulateIE7" />
		<title>车型数据</title>
		<link REL="StyleSheet" HREF="../css/gridstyle.css">
		<link REL="StyleSheet" HREF="../css/show.css">
		<link REL="StyleSheet" HREF="../css/globle.css">
		<link REL="StyleSheet" HREF="css/smpartmanage.css">
		<script Charset="GBK" src="../scripts/datagrid/datagrid.js"
			type="text/javascript">
</script>
		<script Charset="GBK" type='text/javascript'
			src='../scripts/common.js'>
</script>
		<script Charset="GBK" src="../scripts/base64.js"
			type='text/javascript'>
</script>
		<script Charset="GBK" src="../scripts/ValidInput.js"
			type='text/javascript'>
</script>
		<script Charset="UTF-8" type="text/javascript" language="javascript"
			src="../scripts/jquery/json2.js">
</script>
		<script Charset="UTF-8" type="text/javascript" language="javascript"
			src="../scripts/jquery/jquery-1.7.min.js">
</script>
		<script Charset="UTF-8" type='text/javascript'
			src='../scripts/ajaxUtil.js'>
</script>
		<script Charset="UTF-8" type="text/javascript"
			src="../ueditor/editor_config.js">
</script>
		<script Charset="UTF-8" type="text/javascript"
			src="../ueditor/editor_all.js">
</script>
		<link rel="stylesheet" href="../ueditor/themes/default/ueditor.css" />
		<script language="javascript">
var mode = "new";
var callbackresult;
var editor = null;
function onLight() {

}

function pagesize() {
			document.all.tblStat.width = document.body.clientWidth;
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
							<grid:textcolumn dataField="imodelid" headerText="Style"
								HAlign="center" sortable="true" width="20" />
							<grid:textcolumn dataField="icabin" headerText="Cabin"
								HAlign="center" sortable="true" tagField="vcsendno" width="15" />
							<grid:textcolumn dataField="iwb" headerText="WheelBase"
								HAlign="center" sortable="true" width="15" />							
							<grid:textcolumn dataField="iemission" headerText="Emmission"
								HAlign="center" sortable="true" width="10" />
							<grid:textcolumn dataField="isw" headerText="Steering Wheel"
								HAlign="center" sortable="true" width="10" />
							<grid:textcolumn dataField="ifuel" headerText="Fuel"
								HAlign="center" sortable="true" width="10" />
						</grid:dbgrid>
					</form>
				</td>
			</tr>
		</table>
	</body>
</html>
