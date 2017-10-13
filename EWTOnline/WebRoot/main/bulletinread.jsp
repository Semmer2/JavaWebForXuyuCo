<%@ page contentType="text/html;charset=utf-8" pageEncoding="utf-8"%>
<%@ page import="java.util.*"%>
<%@ taglib uri="/WEB-INF/tld/datagrid.tld" prefix="grid"%>
<%@ page import="com.jl.action.*"%>
<%@ page import="java.net.*"%>
<%@include file="../main/checksession.jsp"%>
<%
response.setHeader("Cache-Control", "no-cache");
response.setHeader("Pragma", "no-cache");
response.setDateHeader("Expires", 0);
String viewdetail="";
int pagesize=2000;//每页记录数量,可修改
int width=100;//表格宽度 ,可修改
int DataWidth=100;//数据宽度,可修改
List ls=new ArrayList();
BulletinAction action=new BulletinAction(request);
ls=action.getBulletinsforUser();
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
  	<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
  	<meta http-equiv="pragma" content="no-cache" />
  	<META content=IE=EmulateIE7 http-equiv=X-UA-Compatible>  	
    <title>Bulletin</title>
    <link REL="StyleSheet" HREF="../css/gridstyle.css">
    <link REL="StyleSheet" HREF="../css/show.css">
    <link REL="StyleSheet" HREF="../css/globle.css">
    <link REL="StyleSheet" HREF="css/smpartmanage.css">
    <script Charset="GBK" src="../scripts/datagrid/datagrid.js" type="text/javascript"></script>	
	<script Charset="GBK" type='text/javascript' src='../scripts/common.js'></script>
	<script Charset="GBK" src="../scripts/base64.js" type='text/javascript'> </script>	
	<script Charset="GBK" src="../scripts/ValidInput.js" type='text/javascript'> </script>
	<script Charset="UTF-8" type="text/javascript" language="javascript" src="../scripts/jquery/json2.js" ></script>  
	<script Charset="UTF-8" type="text/javascript" language="javascript" src="../scripts/jquery/jquery-1.7.min.js" ></script>
	<script Charset="UTF-8" type='text/javascript' src='../scripts/ajaxUtil.js'></script>
	<script Charset="UTF-8" type="text/javascript" src="../ueditor/editor_config.js"></script>
	<script Charset="UTF-8" type="text/javascript" src="../ueditor/editor_all.js"></script>
	<link rel="stylesheet" href="../ueditor/themes/default/ueditor.css"/>
    <script language="javascript">
    var mode="new";
    var callbackresult;
    var currentpdfid="-1";
    function onLight()
    {        
        var id=currentRowvalue;
        currentpdfid=id;
        var topic=currentlightvalues[0];
        document.all.topictd.innerText="Bulletin Topic:"+topic;
    	showpdf(id);	  
    }
    function showpdf(pdfid)
    {
    	window.open("../manage/pdfview.jsp?pdfid="+pdfid,"pdfframe");
    }   
  
    function initpage()
    {
    	document.all.pdfframe.height=document.body.clientHeight-50;
    } 
    
    
    function downloadpdf()
    {
    	if(currentpdfid!=-1)
    	{
    		window.open("downloadpdf.jsp?pdfid="+currentpdfid,"_blank");
    	}
    	else
    	{
    		alert("Please Select Bulletin!")	
    	}
    }
    
    </script>
  </head>  
  <body style="margin:0px" scroll=no onload="fixgridsize(document.body.clientHeight-70,gridtd.clientWidth);initpage();" onresize="fixgridsize(document.body.clientHeight-100,gridtd.clientWidth);">
  <table width="100%" cellSpacing="0" cellPadding="0" border="0">
	    <tr>
		    <td style="border-bottom:1px groove #000000;padding-left:2pt;">			
				<table><tr><td><img src="../images/blue.png" width="16" height="16"/></td><td style="font-size:10pt"><b>Bulletin</b></td></tr></table>
			</td>
		    <td valign="middle" style="border-bottom:1px groove #000000;padding-right:10pt;font-size:10pt;cursor:hand" align="right" onclick="downloadpdf();">
			    Download
			</td>			
		</tr>		
		<tr>
		<td colspan="2">
			<table width="100%" cellSpacing="0" cellPadding="0" border="0">
				<tr>
					<td valign="top" id="gridtd" style="padding-right:15pt">
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
							<grid:textcolumn dataField="screatedate" headerText="Date" HAlign="center" sortable="true" width="20" tagField="id"/>					            
					        <grid:textcolumn dataField="topic" headerText="Topic" HAlign="center" sortable="true"/>
					      </grid:dbgrid>
				      </form>
					</td>
					<td width="80%" valign="top">
					    <table width="100%" cellSpacing="0" cellPadding="0" border="0">
					    <thead>
						    <td style="font-size:10pt;" id="topictd">
								Bulletin Topic:&nbsp;
							</td>							
						</thead>
					    <tr>
						    <td>
							   <iframe src="../manage/pdfview.jsp" name="pdfframe" id="pdfframe" frameborder=0 width="100%"></iframe>							
							</td>
						</tr>
						</table>									
					</td>
				</tr>
			</table>
		</td>
		</tr>
		</table>
		 <iframe name="hiddenframe" id="hiddenframe" style="display:none"/>
  </body>
</html>