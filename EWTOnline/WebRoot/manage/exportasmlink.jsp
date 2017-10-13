<%@ page contentType="application/vnd.ms-excel;charset=UTF-8"%>
<%@ page import="java.util.*"%>
<%@ taglib uri="/WEB-INF/tld/datagrid.tld" prefix="grid"%>
<%@ page import="com.jl.action.*"%>
<%@ page import="com.jl.entity.*"%>
<%@ page import="java.net.*"%>
<%@include file="../main/checksession.jsp"%>
<%
	response.setContentType("application/vnd.ms-excel;charset=UTF-8");
	String fileName="export.xls";
	response.setHeader("Content-disposition","attachment;filename="+fileName);	
	String viewdetail = "";
	String queryasmvccode=request.getParameter("asmvccode");	
	int pagesize = 5000;//每页记录数量,可修改
	int width = 300;//表格宽度 ,可修改
	int DataWidth = 300;//数据宽度,可修改
	List ls=new ArrayList();
	AsmLinkAction asmlinkaction=new AsmLinkAction(request);
	queryasmvccode=URLDecoder.decode(queryasmvccode,"UTF-8");
	queryasmvccode=queryasmvccode.trim();
	ls=asmlinkaction.getAsmLinkByVccode(queryasmvccode);
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
							<grid:textcolumn dataField="id" headerText="ID" HAlign="center" sortable="true" width="5"/>
				            <grid:textcolumn dataField="asmvccode" headerText="asmvccode" HAlign="center" sortable="true" tagField="id"/>
				            <grid:textcolumn dataField="asmvccname" headerText="asmvccname" HAlign="center" sortable="true" tagField="id"/>
				            <grid:textcolumn dataField="asmimagevccode" headerText="asmimagevccode" HAlign="center" sortable="true" tagField="id" width="10"/>			            
				            <grid:textcolumn dataField="partvccode" headerText="partvccode" HAlign="center" sortable="true" />
				            <grid:textcolumn dataField="partvccname" headerText="partvccname" HAlign="center" sortable="true" />
				            <grid:textcolumn dataField="imagevccode" headerText="imagevccode" HAlign="center" sortable="true"/>
				            <grid:textcolumn dataField="imagevccnote" headerText="imagevccnote" HAlign="center" sortable="true"/>		            	
				            <grid:textcolumn dataField="ihot" headerText="hot" HAlign="center" sortable="true" width="5"/>
				            <grid:textcolumn dataField="iqty" headerText="qty" HAlign="center" sortable="true" width="5"/>
						</grid:dbgrid>
					</form>
				</td>
			</tr>
		</table>
	</body>
</html>