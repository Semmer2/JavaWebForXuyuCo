<%@ page contentType="text/html;charset=utf-8" pageEncoding="utf-8"%>
<%@ page import="java.util.*"%>
<%@ taglib uri="/WEB-INF/tld/datagrid.tld" prefix="grid"%>
<%@ page import="com.jl.action.*"%>
<%@include file="../main/checksession.jsp"%>
<%
response.setHeader("Cache-Control", "no-cache");
response.setHeader("Pragma", "no-cache");
response.setDateHeader("Expires", 0);
String viewdetail="";
int pagesize=25;//每页记录数量,可修改
int width=100;//表格宽度 ,可修改
int DataWidth=100;//数据宽度,可修改
SmPartAction partaction=new SmPartAction(request);
String queryvccode=request.getParameter("queryvccode");
if(queryvccode==null) queryvccode="";
request.setAttribute("pagesize",String.valueOf(pagesize));	
List ls=partaction.getSmpartQuery(queryvccode);
int totalresultcount=0;
if(request.getAttribute("totalresultcount")!=null)
{
	totalresultcount=Integer.valueOf(request.getAttribute("totalresultcount").toString()).intValue();
}	
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
  	<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
  	<META content=IE=EmulateIE7 http-equiv=X-UA-Compatible>
    <title>零件列表 </title>
    <link REL="StyleSheet" HREF="../css/gridstyle.css">
    <link REL="StyleSheet" HREF="../css/show.css">
    <link REL="StyleSheet" HREF="../css/globle.css">
    <script Charset="gbk" src="../scripts/datagrid/datagrid.js" type="text/javascript"></script>	
	<script Charset="gbk" type='text/javascript' src='../scripts/common.js'></script>
	<script Charset="gbk" type='text/javascript' src='../scripts/ajaxUtil.js'></script>
    <script language="javascript">
    var callbackresult;
    function initpage(){
		var avaiheight = window.screen.height-window.screenTop;	
    	fixgridsize(avaiheight-110,gridtd.clientWidth-2);
    }
    function onLight()
    {    	
    	var partvccode=currentlightvalues[0];
    	parent.querypartprice(partvccode);    	
    }    
    </script>
  </head>  
  <body style="margin:0px" onload="initpage();" scroll="no">
    <table width="100%" border="0" cellSpacing="0" cellPadding="0">
    <tr>
    <td valign="top" id="gridtd">
     <form name="frmShow" id="frmShow" method="POST" target="_self">     
	 <grid:dbgrid id="tblStat" name="tblStat" width="500" pageSize="<%=pagesize%>" pageObject="<%=pageContext%>" pageRequest="<%=request%>"
		border="0" cellSpacing="1" cellPadding="2" dataMember="" dataSource="<%=ls%>" 
		cssClass="gridTable" lightOn="true" tdIntervalColor="true" verticalHeight="375" gridPosition="absolute" 
		dataWidth="500" viewDetail="<%=viewdetail%>" viewWidth="860" viewHeight="600" totalRecords="<%=totalresultcount%>">           
            <grid:gridpager imgFirst="../images/datagrid/First.gif" imgPrevious="../images/datagrid/Previous.gif" imgNext="../images/datagrid/Next.gif" 
            			   imgLast="../images/datagrid/Last.gif" imgBackground="../images/datagrid/di-bt.gif"/>           
            <grid:rownumcolumn dataField="id" headerText="序号" width="8" HAlign="center" sortable="true"/>
            <grid:textcolumn dataField="vccode" headerText="零件编号" HAlign="center" sortable="true" width="18"/>            
            <grid:textcolumn dataField="vccname" headerText="中文名称" HAlign="center" sortable="true" width="30"/>
            <grid:textcolumn dataField="vcename" headerText="英文名称" HAlign="center" sortable="true" />
      </grid:dbgrid>	 
      </form>
    </td>
   </tr>
	</DIV>
	</DIV>
	<P class="r_l_1"></P>
	<P class="r_l_2"></P>
	<P class="r_l_3"></P>
    </td>    
    </tr>
    </table>
    </form>
  </body>
</html>
