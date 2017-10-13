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
	int pagesize = 22;//每页记录数量,可修改
	int width = 1300;//表格宽度 ,可修改
	int DataWidth = 1300;//数据宽度,可修改
	List ls = new ArrayList();
	CommonAction commonaction = new CommonAction(request);
	String partvccode=request.getParameter("partvccode");
	if(partvccode==null) partvccode="-1";
	String seriesid = request.getParameter("series");
	if(seriesid==null) seriesid="";
	String year = request.getParameter("year");
	if(year==null || year.equalsIgnoreCase("")) year="-1";
	String sys = request.getParameter("sys");
	if(sys==null || sys.equalsIgnoreCase("")) sys="-1";
	String subsys = request.getParameter("subsys");
	if(subsys==null || subsys.equalsIgnoreCase("")) subsys="-1";		
	String model = request.getParameter("model");
	if(model==null || model.equalsIgnoreCase("")) model="-1";
	
	UserBean userbean = null;
	Object bean = request.getSession().getAttribute("userbean");
	if (bean != null) {
		userbean = (UserBean) bean;
		String userid = userbean.getId();
		if (userbean.getIflag().equalsIgnoreCase("2")) //经销商
			ls = commonaction.getVehicleforsearch(partvccode, userid);
		else
			ls = commonaction.getVehicleforsearch(partvccode, "");
	}
	int totalresultcount=0;
	if(request.getAttribute("totalresultcount")!=null)
	{
		totalresultcount=Integer.valueOf(request.getAttribute("totalresultcount").toString()).intValue();
	}
	boolean wbexist=false;
	for(int i=0;i<ls.size();i++)
	{
		Hashtable ht=(Hashtable)ls.get(i);
		if(ht.get("iwbname")!=null)
		{
			String wb=ht.get("iwbname").toString();
			if(wb==null) wb="";
			if(wb.equalsIgnoreCase("")==false) 
			{
				wbexist=true;
				break;
			}
		}
	}
	boolean specialexist=false;
	for(int i=0;i<ls.size();i++)
	{
		Hashtable ht=(Hashtable)ls.get(i);
		if(ht.get("vcspecial")!=null)
		{
			String vcspecial=ht.get("vcspecial").toString();		
			if(vcspecial==null) vcspecial="";
			if(vcspecial.equalsIgnoreCase("")==false) 
			{
				specialexist=true;
				break;
			}
		}
	}
	if(specialexist && wbexist)
	{
		width = 1350+100;//表格宽度 ,可修改
		DataWidth = 1350+100;//数据宽度,可修改
	}
	else
	{
		if(specialexist)
		{
			width = 1350+250;//表格宽度 ,可修改
			DataWidth = 1350+250;//数据宽度,可修改
		}
		if(wbexist)
		{
			width = 1350;//表格宽度 ,可修改
			DataWidth = 1350;//数据宽度,可修改
		}
	}		
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
		<meta http-equiv="pragma" content="no-cache" />
		<meta http-equiv="X-UA-Compatible" content="IE=EmulateIE7" />
		<title>车型数据</title>
		<link REL="StyleSheet" HREF="../css/gridstyle.css">
		<script Charset="GBK" src="../scripts/datagrid/datagrid.js" type="text/javascript"></script>
		<script Charset="GBK" type='text/javascript' src='../scripts/common.js'></script>
		<script Charset="GBK" src="../scripts/base64.js" type='text/javascript'></script>
		<script Charset="GBK" src="../scripts/ValidInput.js" type='text/javascript'></script>
		<script Charset="UTF-8" type="text/javascript" language="javascript" src="../scripts/jquery/json2.js"></script>
		<script Charset="UTF-8" type="text/javascript" language="javascript" src="../scripts/jquery/jquery-1.7.min.js"></script>
		<script Charset="UTF-8" type='text/javascript' src='../scripts/ajaxUtil.js'></script>
		<script Charset="UTF-8" type="text/javascript" src="../ueditor/editor_config.js"></script>
		<script Charset="UTF-8" type="text/javascript" src="../ueditor/editor_all.js"></script>
		<link rel="stylesheet" href="../ueditor/themes/default/ueditor.css"/>
		<script language="javascript">
		var mode = "new";
		var callbackresult;
		var editor = null;
		function onLight() {
			
		}
		function ondblrow()
		{
			var key=currentRowvalue;
			var keys=key.split(",");
			var id=keys[0];
			var imageid=keys[1];
			var hotid=parent.partframe.selecthot;
			var w=screen.availWidth;
			var h=screen.availHeight;
		  	var sFeatures = "fullscreen=0,toolbar=0,location=0,directories=0,status=1,menubar=0";
		    sFeatures += ",scrollbars=0,resizable=1,top=0,left=0,width=" + w + ",height=" + h + " ";
			window.open("vehhot.jsp?vehid="+id+"&imageid="+imageid+"&hotid="+hotid,"hotimagewindow", sFeatures);
		}
		
		function pagesize() {
			var avaiheight = window.screen.height-window.screenTop;
			document.getElementById("tC").style.height=avaiheight-25-25-12;
			//document.all.tblStat.style.width=document.body.clientWidth;
			document.getElementById("tC").style.width=document.body.clientWidth;
			document.getElementById("bottomtable").style.width=document.body.clientWidth;	
		}
	</script>
	</head>
	<body style="margin: 0px;" onresize="pagesize();" scroll="no" onload="pagesize();">
		<table width="100%" border="0" cellSpacing="0" cellPadding="0">
			<tr>
				<td valign="top" id="gridtd">
					<form name="frmShow" id="frmShow" method="POST" target="_self">
						<%if(wbexist && specialexist) {%>
						<grid:dbgrid id="tblStat" name="tblStat" width="<%=width%>"
							pageSize="<%=pagesize%>" pageObject="<%=pageContext%>"
							pageRequest="<%=request%>" border="0" cellSpacing="1"
							cellPadding="2" dataMember="" dataSource="<%=ls%>"
							totalRecords="<%=totalresultcount%>" cssClass="gridTable" lightOn="true"
							tdIntervalColor="false" verticalHeight="100"
							gridPosition="absolute" dataWidth="<%=DataWidth%>">
							 <grid:gridpager imgFirst="../images/datagrid/First.gif" imgPrevious="../images/datagrid/Previous.gif" imgNext="../images/datagrid/Next.gif" 
		            			   imgLast="../images/datagrid/Last.gif" imgBackground="../images/datagrid/di-bt.gif"/>		            		
							<grid:textcolumn dataField="mastercode" headerText="Model" HAlign="center" sortable="true" width="14" alterText="Double Click" tagField="key"/>
							
							<grid:textcolumn dataField="vcbrand" headerText="Brand" HAlign="center" width="5" sortable="true"  alterText="Double Click"/>
							<grid:textcolumn dataField="vcedition" headerText="Edition" HAlign="center" width="4" sortable="true"  alterText="Double Click"/>
							<grid:textcolumn dataField="vccolor" headerText="COLOR" HAlign="center" sortable="true" width="7" alterText="Double Click"/>
							<grid:textcolumn dataField="icabinname" headerText="Cabin" HAlign="center" sortable="true" width="4" alterText="Double Click"/>
							<grid:textcolumn dataField="iwbname" headerText="WB" HAlign="center" sortable="true" width="5"  alterText="Double Click"/>
							<grid:textcolumn dataField="vcengine" headerText="Engine" HAlign="center" sortable="true" width="7" alterText="Double Click"/>
							<grid:textcolumn dataField="ifuelname" headerText="Fuel" HAlign="center" width="5" sortable="true"  alterText="Double Click"/>
							<grid:textcolumn dataField="iemissionname" headerText="Emmission" HAlign="center" width="5" sortable="true"  alterText="Double Click"/>
							<grid:textcolumn dataField="idrivename" headerText="Drive" HAlign="center" sortable="true" width="3" alterText="Double Click"/>
							<grid:textcolumn dataField="acname" headerText="A/C" HAlign="center" sortable="true" width="2" alterText="Double Click"/>
							<grid:textcolumn dataField="absname" headerText="ABS" HAlign="center" sortable="true"  width="2" alterText="Double Click"/>
							<grid:textcolumn dataField="airbagname" headerText="Airbag" HAlign="center" sortable="true" width="3" alterText="Double Click"/>
							<grid:textcolumn dataField="redarname" headerText="R/Redar" HAlign="center" sortable="true" width="4" alterText="Double Click"/>
							<grid:textcolumn dataField="vcspecial" headerText="Special" HAlign="center" sortable="true"  width="5" alterText="Double Click"/>
							</grid:dbgrid>
							<%}
							else
							{
								if(wbexist==false && specialexist==false)
								{%>
									<grid:dbgrid id="tblStat" name="tblStat" width="<%=width%>"
										pageSize="<%=pagesize%>" pageObject="<%=pageContext%>"
										pageRequest="<%=request%>" border="0" cellSpacing="1"
										cellPadding="2" dataMember="" dataSource="<%=ls%>"
										totalRecords="<%=totalresultcount%>" cssClass="gridTable" lightOn="true"
										tdIntervalColor="false" verticalHeight="100"
										gridPosition="absolute" dataWidth="<%=DataWidth%>">
										 <grid:gridpager imgFirst="../images/datagrid/First.gif" imgPrevious="../images/datagrid/Previous.gif" imgNext="../images/datagrid/Next.gif" 
					            			   imgLast="../images/datagrid/Last.gif" imgBackground="../images/datagrid/di-bt.gif"/>		            		
										<grid:textcolumn dataField="mastercode" headerText="Model" HAlign="center" sortable="true" width="14" alterText="Double Click" tagField="key"/>
										<grid:textcolumn dataField="vcbrand" headerText="Brand" HAlign="center" width="5" sortable="true"  alterText="Double Click"/>
										<grid:textcolumn dataField="vcedition" headerText="Edition" HAlign="center" width="4" sortable="true"  alterText="Double Click"/>
										<grid:textcolumn dataField="vccolor" headerText="COLOR" HAlign="center" sortable="true" width="7" alterText="Double Click"/>
										<grid:textcolumn dataField="icabinname" headerText="Cabin" HAlign="center" sortable="true"  width="4" alterText="Double Click"/>																	
										<grid:textcolumn dataField="vcengine" headerText="Engine" HAlign="center" sortable="true" width="7" alterText="Double Click"/>
										<grid:textcolumn dataField="ifuelname" headerText="Fuel" HAlign="center" width="5" sortable="true"  alterText="Double Click"/>
										<grid:textcolumn dataField="iemissionname" headerText="Emmission" HAlign="center" width="5" sortable="true"  alterText="Double Click"/>
										<grid:textcolumn dataField="idrivename" headerText="Drive" HAlign="center" sortable="true" width="3" alterText="Double Click"/>		
										<grid:textcolumn dataField="acname" headerText="A/C" HAlign="center" sortable="true" width="2" alterText="Double Click"/>
										<grid:textcolumn dataField="absname" headerText="ABS" HAlign="center" sortable="true"  width="2" alterText="Double Click"/>
										<grid:textcolumn dataField="airbagname" headerText="Airbag" HAlign="center" sortable="true" width="3" alterText="Double Click"/>
										<grid:textcolumn dataField="redarname" headerText="R/Redar" HAlign="center" sortable="true" width="4" alterText="Double Click"/>
										</grid:dbgrid>
								<%}
								else
								{
								   if(wbexist)
								   {
								   		%>
								   		<grid:dbgrid id="tblStat" name="tblStat" width="<%=width%>"
										pageSize="<%=pagesize%>" pageObject="<%=pageContext%>"
										pageRequest="<%=request%>" border="0" cellSpacing="1"
										cellPadding="2" dataMember="" dataSource="<%=ls%>"
										totalRecords="<%=totalresultcount%>" cssClass="gridTable" lightOn="true"
										tdIntervalColor="false" verticalHeight="100"
										gridPosition="absolute" dataWidth="<%=DataWidth%>">
										 <grid:gridpager imgFirst="../images/datagrid/First.gif" imgPrevious="../images/datagrid/Previous.gif" imgNext="../images/datagrid/Next.gif" 
					            			   imgLast="../images/datagrid/Last.gif" imgBackground="../images/datagrid/di-bt.gif"/>		            		
										<grid:textcolumn dataField="mastercode" headerText="Model" HAlign="center" sortable="true" width="12" alterText="Double Click" tagField="key"/>
										<grid:textcolumn dataField="vcbrand" headerText="Brand" HAlign="center" width="5" sortable="true"  alterText="Double Click"/>
										<grid:textcolumn dataField="vcedition" headerText="Edition" HAlign="center" width="4" sortable="true"  alterText="Double Click"/>
										<grid:textcolumn dataField="vccolor" headerText="COLOR" HAlign="center" sortable="true" width="6" alterText="Double Click"/>
										<grid:textcolumn dataField="icabinname" headerText="Cabin" HAlign="center" sortable="true"  width="4" alterText="Double Click"/>
										<grid:textcolumn dataField="iwbname" headerText="WB" HAlign="center" sortable="true" width="5"  alterText="Double Click"/>							
										<grid:textcolumn dataField="vcengine" headerText="Engine" HAlign="center" sortable="true" width="7" alterText="Double Click"/>
										<grid:textcolumn dataField="ifuelname" headerText="Fuel" HAlign="center" width="5" sortable="true"  alterText="Double Click"/>
										<grid:textcolumn dataField="iemissionname" headerText="Emmission" HAlign="center" width="5" sortable="true"  alterText="Double Click"/>
										<grid:textcolumn dataField="idrivename" headerText="Drive" HAlign="center" sortable="true" width="3" alterText="Double Click"/>		
										<grid:textcolumn dataField="acname" headerText="A/C" HAlign="center" sortable="true" width="2" alterText="Double Click"/>
										<grid:textcolumn dataField="absname" headerText="ABS" HAlign="center" sortable="true"  width="2" alterText="Double Click"/>
										<grid:textcolumn dataField="airbagname" headerText="Airbag" HAlign="center" sortable="true" width="3" alterText="Double Click"/>
										<grid:textcolumn dataField="redarname" headerText="R/Redar" HAlign="center" sortable="true" width="4" alterText="Double Click"/>
										</grid:dbgrid>
								   		<%
								   }
								   else
								   {
								   	%>
								   		<grid:dbgrid id="tblStat" name="tblStat" width="<%=width%>"
										pageSize="<%=pagesize%>" pageObject="<%=pageContext%>"
										pageRequest="<%=request%>" border="0" cellSpacing="1"
										cellPadding="2" dataMember="" dataSource="<%=ls%>"
										totalRecords="<%=totalresultcount%>" cssClass="gridTable" lightOn="true"
										tdIntervalColor="false" verticalHeight="100"
										gridPosition="absolute" dataWidth="<%=DataWidth%>">
										 <grid:gridpager imgFirst="../images/datagrid/First.gif" imgPrevious="../images/datagrid/Previous.gif" imgNext="../images/datagrid/Next.gif" 
					            			   imgLast="../images/datagrid/Last.gif" imgBackground="../images/datagrid/di-bt.gif"/>		            		
										<grid:textcolumn dataField="mastercode" headerText="Model" HAlign="center" sortable="true" width="12" alterText="Double Click" tagField="key"/>
										<grid:textcolumn dataField="vcbrand" headerText="Brand" HAlign="center" width="5" sortable="true"  alterText="Double Click"/>
										<grid:textcolumn dataField="vcedition" headerText="Edition" HAlign="center" width="4" sortable="true"  alterText="Double Click"/>
										<grid:textcolumn dataField="vccolor" headerText="COLOR" HAlign="center" sortable="true" width="7" alterText="Double Click"/>
										<grid:textcolumn dataField="icabinname" headerText="Cabin" HAlign="center" sortable="true"  width="4" alterText="Double Click"/>							
										<grid:textcolumn dataField="vcengine" headerText="Engine" HAlign="center" sortable="true" width="5" alterText="Double Click"/>
										<grid:textcolumn dataField="ifuelname" headerText="Fuel" HAlign="center" width="4" sortable="true"  alterText="Double Click"/>
										<grid:textcolumn dataField="iemissionname" headerText="Emmission" HAlign="center" width="5" sortable="true"  alterText="Double Click"/>
										<grid:textcolumn dataField="idrivename" headerText="Drive" HAlign="center" sortable="true" width="3" alterText="Double Click"/>		
										<grid:textcolumn dataField="acname" headerText="A/C" HAlign="center" sortable="true" width="2" alterText="Double Click"/>
										<grid:textcolumn dataField="absname" headerText="ABS" HAlign="center" sortable="true"  width="2" alterText="Double Click"/>
										<grid:textcolumn dataField="airbagname" headerText="Airbag" HAlign="center" sortable="true" width="3" alterText="Double Click"/>
										<grid:textcolumn dataField="redarname" headerText="R/Redar" HAlign="center" sortable="true" width="4" alterText="Double Click"/>
										<grid:textcolumn dataField="vcspecial" headerText="Special" HAlign="center" sortable="true"  width="8" alterText="Double Click"/>
										</grid:dbgrid>
								   	<%
								   }
								%>								  
								<%}
							}%>
					</form>
				</td>
			</tr>
		</table>
	</body>
</html>
