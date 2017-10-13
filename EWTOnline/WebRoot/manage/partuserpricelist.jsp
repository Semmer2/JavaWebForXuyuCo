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
int pagesize=20;//每页记录数量,可修改
int width=100;//表格宽度 ,可修改
int DataWidth=100;//数据宽度,可修改
SmPartAction action=new SmPartAction(request);
String queryusername=request.getParameter("queryusername");
String querypartvccode=request.getParameter("querypartvccode");
if(queryusername==null) queryusername="";
if(querypartvccode==null) querypartvccode="";
List ls=action.getpartprice(queryusername,querypartvccode);

%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
  	<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
  	<META content=IE=EmulateIE7 http-equiv=X-UA-Compatible>
    <title>零件价格 </title>
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
    	parent.currentuserid=currentRowvalue;
    	
    }
    function changeprice()
    {    	
    	if(event.srcElement.value!="")
    	{
    		 if(isNaN(event.srcElement.value)){
                alert("对不起,零件价格必须为数字型!");
                event.srcElement.value="";
                return;
            }
    	}
    	var username=event.srcElement.id;
    	var partvccode="<%=querypartvccode%>";
    	var pricevalue=event.srcElement.value;
    	var url="../servlet.jsp?action=smpartprice&method=changeprice&username="+username+"&partvccode="+partvccode+"&price="+pricevalue;    
		callbackresult=send_request(url,onCallBackForChanagePrice);
    }
	
	function onCallBackForChanagePrice(callbacktext)
	{	
		
    }	
    </script>
  </head>  
  <body style="margin:0px" onload="initpage();" scroll="no">
    <table width="100%" border="0" cellSpacing="0" cellPadding="0">
    <tr>
    <td valign="top" id="gridtd">
     <form name="frmShow" id="frmShow" method="POST" target="_self">
     <input type="hidden" name="queryusername" value="<%=queryusername%>"/>
     <input type="hidden" name="querypartvccode" value="<%=querypartvccode%>"/>   
	 <grid:dbgrid id="tblStat" name="tblStat" width="500" pageSize="<%=pagesize%>" pageObject="<%=pageContext%>" pageRequest="<%=request%>"
		border="0" cellSpacing="1" cellPadding="2" dataMember="" dataSource="<%=ls%>" totalRecords="<%=0%>"
		cssClass="gridTable" lightOn="true" tdIntervalColor="true" verticalHeight="375" gridPosition="absolute" 
		dataWidth="500" viewDetail="<%=viewdetail%>" viewWidth="860" viewHeight="600">           
            <grid:gridpager imgFirst="../images/datagrid/First.gif" imgPrevious="../images/datagrid/Previous.gif" imgNext="../images/datagrid/Next.gif" 
            			   imgLast="../images/datagrid/Last.gif" imgBackground="../images/datagrid/di-bt.gif"/>           
            <grid:rownumcolumn dataField="username" headerText="序号" width="12" HAlign="center" sortable="true"/>
            <grid:textcolumn dataField="username" headerText="经销商" HAlign="center" sortable="true" width="20"/>            
            <grid:textcolumn dataField="usercn" headerText="中文名称" HAlign="center" sortable="true" width="40"/>
            <grid:textcolumn dataField="priceexpress" headerText="价格" HAlign="center" sortable="true" isShowTitle="false"/>            
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
