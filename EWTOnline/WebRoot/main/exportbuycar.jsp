<%@ page contentType="application/vnd.ms-excel;charset=gbk"%>
<%@ page import="java.util.*"%>
<%@ taglib uri="/WEB-INF/tld/datagrid.tld" prefix="grid"%>
<%@ page import="com.jl.action.*"%>
<%@ page import="com.jl.entity.*"%>
<%@ page import="java.net.*"%>
<%@include file="checksession.jsp"%>
<%
	response.setContentType("application/vnd.ms-excel;charset=gbk");
	String fileName="export.xls";
	response.setHeader("Content-disposition","attachment;filename="+fileName);	
	String viewdetail = "";
	String userid=request.getSession().getAttribute("currentuserid").toString();	
	int pagesize = 5000;//每页记录数量,可修改
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
		<title>Part</title>		
	</head>
	<body>
		<table width="100%" border="0" cellSpacing="0" cellPadding="0">
			<tr>
				<td valign="top" id="gridtd">
					<form name="frmShow" id="frmShow" method="POST" target="_self">					
						<grid:dbgrid id="tblStat" name="tblStat" width="<%=width%>"
							pageSize="<%=pagesize%>" pageObject="<%=pageContext%>"
							pageRequest="<%=request%>" border="0" cellSpacing="1"
							cellPadding="2" dataMember="" dataSource="<%=ls%>"
							totalRecords="<%=0%>" cssClass="gridTable" lightOn="true"
							tdIntervalColor="false" verticalHeight="400"
							gridPosition="absolute" dataWidth="<%=DataWidth%>" showHead="true">							
							<grid:textcolumn dataField="vccode" headerText="P/N" width="25" HAlign="center" sortable="true" tagField="id"/>
							<grid:textcolumn dataField="vcename" headerText="P/D" HAlign="center" sortable="true" cssClass="lefttd"/>							
							<grid:textcolumn dataField="iqty" headerText="Q" HAlign="center" sortable="true" width="5" />
						</grid:dbgrid>
					</form>
				</td>
			</tr>
		</table>
	</body>
</html>