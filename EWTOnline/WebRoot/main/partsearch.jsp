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
	int pagesize = 20;//每页记录数量,可修改
	int width = 700;//表格宽度 ,可修改
	int DataWidth = 700;//数据宽度,可修改
	List ls = new ArrayList();
	SmPartAction partaction = new SmPartAction(request);
	String pn=request.getParameter("pn");
	String pd=request.getParameter("pd");
	String sap=request.getParameter("sap");
	String vin=request.getParameter("vin");
	if(pn==null) pn="";
	if(pd==null) pd="";
	if(sap==null) sap="";
	if(vin==null) vin="";
	
	pn=pn.toUpperCase();
	pd=pd.toUpperCase();
	sap=sap.toUpperCase();
	vin=vin.toUpperCase();
	
	String year=request.getParameter("year");
	String sys=request.getParameter("sys");
	String subsys=request.getParameter("subsys");
	String series=request.getParameter("series");
	String model=request.getParameter("model");
	if(year==null || year.equalsIgnoreCase("")) year="-1";
	if(sys==null || sys.equalsIgnoreCase("")) sys="-1";
	if(subsys==null || subsys.equalsIgnoreCase("")) subsys="-1";
	if(series==null || series.equalsIgnoreCase("")) series="-1";
	if(model==null || model.equalsIgnoreCase("")) model="-1";
	
	String userid=request.getSession().getAttribute("currentuserid").toString();
	
	if(pd==null) pd="";
	if(sap==null) sap="";
	if(vin==null) vin="";
	UserBean userbean = null;
	request.setAttribute("pagesize",String.valueOf(pagesize));	
	Object bean = request.getSession().getAttribute("userbean");
	if (bean != null) {
		userbean = (UserBean) bean;
		userid = userbean.getId();
		if (userbean.getIflag().equalsIgnoreCase("2")) //经销商			
			ls=partaction.AdvanceQueryPart(pn,pd,vin,sap,userid,year,sys,subsys,series,model);
		else
			ls=partaction.AdvanceQueryPart(pn,pd,vin,sap,"",year,sys,subsys,series,model);
	}	
	int totalresultcount=0;
	if(request.getAttribute("totalresultcount")!=null)
	{
		totalresultcount=Integer.valueOf(request.getAttribute("totalresultcount").toString()).intValue();
	}
	String title="Part";
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
		var selecthot="";
		function onLight() {
			var key=currentRowvalue;
			key=key.split(",");
        	var partvccode=key[0];
        	selecthot=key[1];
        	parent.querybypartvccode(partvccode);
		}
		
		function pagesize() {		
			parent.document.all.partframe.style.display="block";
			parent.document.all.waittable.style.display="none";
			parent.document.all.partframetd.style.backgroundColor="#efefef"; 
			var avaiheight = window.screen.height-window.screenTop;
			document.getElementById("tC").style.height=avaiheight-25-25-12;
			document.all.tblStat.style.width=document.body.clientWidth;
			document.getElementById("tC").style.width=document.all.tblStat.style.width;
			document.getElementById("bottomtable").style.width=document.all.tblStat.style.width;			
		}
		function addsmpartcar()
		{			
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
		}
</script>
	</head>
	<body onload="pagesize();" onresize="pagesize();" scroll="no" style="margin: 0px;">
		<table width="100%" border="0" cellSpacing="0" cellPadding="0">
			<tr>
				<td valign="top" id="gridtd">
					<form name="frmShow" id="frmShow" method="POST" target="_self">
						<grid:dbgrid id="tblStat" name="tblStat" width="400"
							pageSize="<%=pagesize%>" pageObject="<%=pageContext%>"
							pageRequest="<%=request%>" border="0" cellSpacing="1"
							cellPadding="2" dataMember="" dataSource="<%=ls%>"
							totalRecords="<%=totalresultcount%>" cssClass="gridTable" lightOn="true"
							tdIntervalColor="true" verticalHeight="100"
							gridPosition="absolute" dataWidth="400">
							 <grid:gridpager imgFirst="../images/datagrid/First.gif" imgPrevious="../images/datagrid/Previous.gif" imgNext="../images/datagrid/Next.gif" 
		            			   imgLast="../images/datagrid/Last.gif" imgBackground="../images/datagrid/di-bt.gif"/>
							<grid:textcolumn dataField="pn" headerText="P/N" width="25" HAlign="center" sortable="true" tagField="key" isShowTitle="false"/>
							<grid:textcolumn dataField="pd" headerText="<center>P/D</center>" HAlign="left" sortable="true" isShowTitle="false" cssClass="lefttd"/>														
							<grid:numbercolumn dataField="price" headerText="Price" HAlign="center" sortable="true" width="20" preUnit="$"/>
							<grid:textcolumn alterText="Add" dataField="O" headerText="O,Order" HAlign="center" width="5" doClick="addsmpartcar()"/>
						</grid:dbgrid>
					</form>
				</td>
			</tr>
		</table>
	</body>
</html>
