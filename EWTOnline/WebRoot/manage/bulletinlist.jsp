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
List ls=new ArrayList();
List userprivmodenames=null;
BulletinAction bulletaction=new BulletinAction(request);
ls=bulletaction.getBulletins();
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
  	<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
  	<META content=IE=EmulateIE7 http-equiv=X-UA-Compatible>
    <title>公告列表 </title>
    <link REL="StyleSheet" HREF="../css/gridstyle.css">
    <link REL="StyleSheet" HREF="../css/show.css">
    <link REL="StyleSheet" HREF="../css/globle.css">
    <script Charset="gbk" src="../scripts/datagrid/datagrid.js" type="text/javascript"></script>	
	<script Charset="gbk" type='text/javascript' src='../scripts/common.js'></script>
	<script Charset="gbk" type='text/javascript' src='../scripts/ajaxUtil.js'></script>
    <script language="javascript">
    var callbackresult;
    var rowindex=-1;
    function initpage(){
		var avaiheight = window.screen.height-window.screenTop;	
    	fixgridsize(avaiheight-65,gridtd.clientWidth);
    }
    function onLight()
    {    	
    	var id=currentRowvalue;    	
    	parent.showpdf(id);	
    }
    function deletebulletin()
    {
    	if(confirm("是否要删除，确定、取消")=="1")
    	{
    	var id=event.srcElement.tag;
    	rowindex=event.srcElement.parentElement.parentElement.ln;    	
    	var url="../servlet.jsp?action=bulletin&method=del&id="+id;
		callbackresult=send_request(url,onCallBackForDeleteBulletin);
		}
    }
    function onCallBackForDeleteBulletin(callbacktext)
	{		
		onDeleteRowByIndex(rowindex);
		rowindex=-1;
		alert("删除成功!");
	}
	function publishbulletin()
	{
		var id=event.srcElement.tag;
		var url="../servlet.jsp?action=bulletin&method=publish&id="+id;
		callbackresult=send_request(url,onCallBackForPublishBulletin);
	}
	function onCallBackForPublishBulletin(callbacktext)
	{
		frmShow.submit();
	}
    </script>
  </head>  
  <body style="margin:0px" onload="initpage();" scroll="no">
    <table width="100%" border="0" cellSpacing="0" cellPadding="0">
    <tr>
    <td valign="top" id="gridtd">
     <form name="frmShow" id="frmShow" method="POST" target="_self">
		 <grid:dbgrid id="tblStat" name="tblStat" width="500" pageSize="<%=pagesize%>" pageObject="<%=pageContext%>" pageRequest="<%=request%>"
			border="0" cellSpacing="1" cellPadding="2" dataMember="" dataSource="<%=ls%>" totalRecords="<%=0%>"
			cssClass="gridTable" lightOn="true" tdIntervalColor="true" verticalHeight="375" gridPosition="absolute" 
			dataWidth="500" viewDetail="<%=viewdetail%>" viewWidth="860" viewHeight="600">
			<grid:gridpager imgFirst="../images/datagrid/First.gif" 
		            imgPrevious="../images/datagrid/Previous.gif" 
		            imgNext="../images/datagrid/Next.gif" 
		            imgLast="../images/datagrid/Last.gif" 
		            imgBackground="../images/datagrid/di-bt.gif"/>
			<grid:textcolumn dataField="screatedate" headerText="创建时间" HAlign="center" sortable="true" width="20" tagField="id"/>					            
	        <grid:textcolumn dataField="topic" headerText="主题" HAlign="center" sortable="true"/>
	        <grid:textcolumn dataField="status" headerText="状态 " HAlign="center" sortable="true" width="15"/>
	        <grid:anchorcolumn target="hiddenframe"  dataField="id" headerText="" HAlign="center" sortable="true" linkText="Del" linkUrl="javascript:deletebulletin();" width="10" tagField="id"/>
	        <grid:anchorcolumn target="hiddenframe" dataField="id" headerText="" HAlign="center" sortable="true" linkText="Pub" linkUrl="javascript:publishbulletin();" width="10" tagField="id"/>
	      </grid:dbgrid>
	 </form>
    </td>
   </tr>	
    </td>    
    </tr>
    </table>
    <iframe name="hiddenframe" id="hiddenframe" style="display:none"/>
  </body>
</html>
