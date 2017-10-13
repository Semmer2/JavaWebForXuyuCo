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
int pagesize=20;//每页记录数量,可修改
int width=100;//表格宽度 ,可修改
int DataWidth=100;//数据宽度,可修改
List ls=new ArrayList();
AsmLinkAction asmlinkaction=new AsmLinkAction(request);
String asmid=request.getParameter("asmid");
if(asmid==null) asmid="";
if(asmid.equalsIgnoreCase("")==false)
{
	asmid=URLDecoder.decode(asmid,"UTF-8");
	asmid=asmid.trim();
	ls=asmlinkaction.getChildAsmLinks(asmid);
}
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
  	<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
  	<meta http-equiv="pragma" content="no-cache" />
  	<META content=IE=EmulateIE7 http-equiv=X-UA-Compatible>  	
    <title>总成包管理</title>
    <link REL="StyleSheet" HREF="../css/gridstyle.css">
    <link REL="StyleSheet" HREF="../css/show.css">
    <link REL="StyleSheet" HREF="../css/globle.css">
    <link REL="StyleSheet" HREF="css/smpartmanage.css">
    <script Charset="GBK" src="../scripts/datagrid/datagrid.js" type="text/javascript"></script>	
	<script Charset="GBK" type='text/javascript' src='../scripts/common.js'></script>	
	<script Charset="GBK" src="../scripts/ValidInput.js" type='text/javascript'> </script>
	<script Charset="UTF-8" type="text/javascript" language="javascript" src="../scripts/jquery/json2.js" ></script>  
	<script Charset="UTF-8" type="text/javascript" language="javascript" src="../scripts/jquery/jquery-1.7.min.js" ></script>
	<script Charset="UTF-8" type='text/javascript' src='../scripts/ajaxUtil.js'></script>
    <script language="javascript">
    var mode="";
    var callbackresult;
    function saveasm(asmid,partid,imageid,hotid,iqty)
    {
    	var url="do.json?action=asmlinkmanage&method=saveasm";
    	var data="({";
    	data=data+"asmid:"+asmid+",";
    	data=data+"partid:"+partid+",";
    	data=data+"imageid:"+imageid+",";
    	data=data+"hotid:"+hotid+",";
    	data=data+"iqty:"+iqty;
    	data=data+"})";    	
    	var jsonobject=eval(data);    	
		$.ajax({
		type : "GET",//使用post方法访问后台  
		dataType : "json",//返回json格式的数据  
		url : url,//要访问的后台地址 
		cache: false,//默认: true,设置为 false 将不会从浏览器缓存中加载请求信息。  
		contentType : "application/json;charset=utf-8",
		data:jsonobject,
		beforeSend:onBeforeSaveAsm,
		complete:onComplete,
		success:onAfterSaveAsm,
		error:onError 
		});
	}
    function onBeforeSaveAsm()
    {
    	
    }
    function onAfterSaveAsm(data)
    {
    	if(data.result!="0")
		{	
    		var dowhat=data.dowhat;
    		var id=data.id;
    		var iasmid=data.iasmid;
    		var ipartid=data.ipartid;
    		var iimageid=data.iimageid;
    		var iqty=data.iqty;
    		var ihot=data.ihot;
    		var partcnote=getvalue(data.partcnote);
    		var vccnote=getvalue(data.vccnote);
    		var values=escape(trim(id))+","+escape(trim(ipartid))+","+escape(trim(partcnote))+","+escape(trim(iqty))+","+escape(trim(iimageid))+","+escape(trim(ihot));
    		if(dowhat=="new")
    		{
				onNewRow(values);
			}
    		if(dowhat=="edit")
    		{	    		
				onSaveRow(id,values);
			}
    	}
    }
    function onError(XMLResponse)
    {
    	alert(XMLResponse.responseText);
    } 
    function ondblrow()
    {    	
    	var surl="asmlinkdialog.jsp?method=edit&id="+currentlightvalues[0];
    	var top=parent.document.body.clientHeight/3*2;
    	var left=parent.document.body.clientWidth/3*2;
    	var parames="dialogHeight:200px;dialogWidth:400px;dialogTop:"+top+";dialogLeft:"+left;
    	window.showModelessDialog(surl,window,parames);
    }
    function onLight()
    {        
        
    }
    function delAsmLink()
    {
    	
    }
    function onBeforeDeleteAsmLink()
    {
    	
    }
    function onAfterDeleteAsmLink(returnobject)
	{	
		
	}    
   
    function pagesize()
    {
    	 fixgridsize(document.body.clientHeight-70,gridtd.clientWidth);
    }
    function onComplete()
    {
    	
    }
    function onError(XMLResponse)
    {
    	alert(XMLResponse.responseText);
    }
     function delAsmLink()
    {
    	var ids=getSelectIds();
    	if(ids=="")
    	{
    		alert("请选择删除的记录!");
    		return;
    	}
		var url="do.json?action=asmlinkmanage&method=delete&ids="+ids;
		$.ajax({
		type : "GET",//使用post方法访问后台  
		dataType : "json",//返回json格式的数据  
		url : url,//要访问的后台地址 
		cache: false,//默认: true,设置为 false 将不会从浏览器缓存中加载请求信息。  
		contentType : "application/json;charset=utf-8",	
		beforeSend:onBeforeDeleteAsmLink,
		complete:onComplete,
		success:onAfterDeleteAsmLink,
		error:onError
		});
    }
    function onBeforeDeleteAsmLink()
    {
    	
    }
    function onAfterDeleteAsmLink(returnobject)
	{
    	if(returnobject.result=="")
		{
			alert("删除失败("+getvalue(returnobject.error)+")!");
		}
		else
		{			
			alert("删除成功!");
			onDeleteRow();
			mode="";
			var totalcount=document.all.totalcount.innerText;
    		if(totalcount=="0")
    		{			
				parent.deleteAsm();
			}
		}
	}  
    </script>
  </head>  
  <body style="margin:0px" scroll=no onload="pagesize();" onresize="pagesize();">
    <table width="100%" border="0" cellSpacing="0" cellPadding="0">
    <tr>
        <td valign="top" width="100%">
        &nbsp;
        </td>   
		<td valign="top" id="gridtd">
		<P class="r_l_3"></P>
		 <P class="r_l_2"></P>
		 <P class="r_l_1"></P>
	     <DIV class="w_l">	
		 <DIV class="body">
		<form name="frmShow" id="frmShow" method="POST" target="_self">
		 <grid:dbgrid id="tblStat" name="tblStat" width="750" pageSize="<%=pagesize%>" pageObject="<%=pageContext%>" pageRequest="<%=request%>"
			border="0" cellSpacing="1" cellPadding="2" dataMember="" dataSource="<%=ls%>" totalRecords="<%=0%>"
			cssClass="gridTable" lightOn="true" tdIntervalColor="true" verticalHeight="375" gridPosition="absolute" 
			dataWidth="750" viewDetail="<%=viewdetail%>" viewWidth="860" viewHeight="600">           
	            <grid:gridpager imgFirst="../images/datagrid/First.gif" imgPrevious="../images/datagrid/Previous.gif" imgNext="../images/datagrid/Next.gif" 
	            			   imgLast="../images/datagrid/Last.gif" imgBackground="../images/datagrid/di-bt.gif"/>
	            <grid:rownumcolumn dataField="id" headerText="序号" width="8" HAlign="center" sortable="true"/>           
	            <grid:checkboxcolumn dataField="id" headerText="checkbox" HAlign="center" sortable="true" width="8"/>
	            <grid:textcolumn dataField="ipartid" headerText="件号" HAlign="center" sortable="true" width="12"/>
	            <grid:textcolumn dataField="vccnote" headerText="中文描述" HAlign="center" sortable="true"/>
	            <grid:textcolumn dataField="iqty" headerText="数量" HAlign="center" sortable="true" width="12"/>
	            <grid:textcolumn dataField="iimageid" headerText="爆炸图序号" HAlign="center" sortable="true" width="12"/>
	            <grid:textcolumn dataField="ihot" headerText="热点" HAlign="center" sortable="true" width="12"/>
	      </grid:dbgrid>
	      </form>
	      </DIV>
		 </DIV>	
		 <P class="r_l_1"></P>
		 <P class="r_l_2"></P>
		 <P class="r_l_3"></P>	  
		</td>		
    </tr>
    </table>    
  </body>
</html>