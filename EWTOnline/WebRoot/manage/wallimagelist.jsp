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
UserAction useraction=new UserAction(request); 
List ls=useraction.getUsers();
WallImageAction action=new WallImageAction(request);
List imagelist=action.getWallImageList();
String imagesrc="";
if(imagelist.size()>0)
{
	Hashtable ht=(Hashtable)imagelist.get(0);
	imagesrc=ht.get("id").toString()+".jpg";
	imagesrc="../wall/"+imagesrc;
}
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
  	<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
  	<META content=IE=EmulateIE7 http-equiv=X-UA-Compatible>
    <title>图片列表</title>
    <link REL="StyleSheet" HREF="../css/gridstyle.css">
    <link REL="StyleSheet" HREF="../css/show.css">
    <link REL="StyleSheet" HREF="../css/globle.css">
    <script Charset="gbk" src="../scripts/datagrid/datagrid.js" type="text/javascript"></script>	
	<script Charset="gbk" type='text/javascript' src='../scripts/common.js'></script>
	<script type="text/javascript" src="../css/lib/jquery-1.2.3.pack.js"></script>
	<script type="text/javascript" src="../css/lib/jquery.jcarousel.js"></script>
	<script Charset="gbk" type='text/javascript' src='../scripts/ajaxUtil.js'></script>
	<link rel="stylesheet" type="text/css"	href="../css/lib/jquery.jcarousel.css" />
    <script language="javascript">
    var callbackresult;
    function initpage(){    	
		
		document.getElementById("tC").style.height=document.body.scrollHeight+56;
		document.getElementById("tC").style.width=document.body.clientWidth-10;
		document.getElementById("tblStat").style.width=document.body.clientWidth-10;
		document.getElementById("bottomtable").style.width=document.body.clientWidth-10;
		parent.changeimageframeheight(document.body.scrollHeight+56);
		var imagesrc="<%=imagesrc%>";
		if(imagesrc!="")
		{
			parent.document.all.imagecontent.src=imagesrc;	
		}
    }
    function onLight()
    {        
        var id=currentRowvalue;
        parent.document.all.imagecontent.src="../wall/"+id+".jpg";
    }   
	function onCallBackForRemove(callbacktext)
	{		
		onDeleteRowByIndex(currentLine);
		currentLine=-1;
	}	
	function removewallimage()
	{
		var id=currentRowvalue;
		var url="../servlet.jsp?action=wallimage&method=del&id="+id;
		callbackresult=send_request(url,onCallBackForRemove);
	}
	function onCallBack(callbacktext)
	{	
		
	}
	function changeshowstatus()
	{
		var id=event.srcElement.value;
		var checked=event.srcElement.checked;	
		var url="../servlet.jsp?action=wallimage&method=changeshowstatus&id="+id+"&checked="+checked;
		callbackresult=send_request(url,onCallBack);
	}
	function selectall()
	{
		var inputcontrols=document.getElementsByTagName("INPUT");
		for(var i=0;i<inputcontrols.length;i++)
		{
			var inputcontrol=inputcontrols.item(i);
			if(inputcontrol.type=="checkbox")
			{
				inputcontrol.checked=event.srcElement.checked;	
			}
		}
		var id=event.srcElement.value;
		var checked=event.srcElement.checked;	
		var url="../servlet.jsp?action=wallimage&method=changeshowstatus&id=all&checked="+checked;
		callbackresult=send_request(url,onCallBack);
	}
    </script>
  </head>  
  <body style="margin:0px" onload="initpage();" scroll="no">
  <table width="100%">
  <tr>
  <td align="right">    
     <form name="frmShow" id="frmShow" method="POST" target="_self">	    
		 <grid:dbgrid id="tblStat" name="tblStat" width="650" pageSize="<%=pagesize%>" pageObject="<%=pageContext%>" pageRequest="<%=request%>"
			border="0" cellSpacing="1" cellPadding="2" dataMember="" dataSource="<%=imagelist%>" totalRecords="<%=0%>"
			cssClass="gridTable" lightOn="true" tdIntervalColor="true" verticalHeight="375" gridPosition="absolute" 
			dataWidth="650" viewDetail="<%=viewdetail%>" viewWidth="860" viewHeight="600">           
	            <grid:gridpager imgFirst="../images/datagrid/First.gif" imgPrevious="../images/datagrid/Previous.gif" imgNext="../images/datagrid/Next.gif" 
	            			   imgLast="../images/datagrid/Last.gif" imgBackground="../images/datagrid/di-bt.gif"/>           
	            <grid:textcolumn dataField="id" headerText="序号" width="8" HAlign="center" sortable="false"/>
	            <grid:textcolumn dataField="imagename" headerText="图片名称" HAlign="center" sortable="false"/>           
	            <grid:textcolumn dataField="showstatus" headerText="显示<input type='checkbox' onclick='selectall();'>" HAlign="center" sortable="false" width="8" isShowTitle="false"/>
	            <grid:imagecolumn alterText="Remove" imageSrc="../images/delete.png" imageWidth="16" imageHeight="16" headerText="操作 " HAlign="center" width="10" linkUrl="javascript:removewallimage();" />		            
	      </grid:dbgrid>	      
     </form>
 </td>
 </tr>
 </table>    
  </body>
</html>
