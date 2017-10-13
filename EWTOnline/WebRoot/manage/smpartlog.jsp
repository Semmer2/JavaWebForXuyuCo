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
int pagesize=200;//每页记录数量,可修改
int width=100;//表格宽度 ,可修改
int DataWidth=100;//数据宽度,可修改
SmPartAction partaction=new SmPartAction(request); 
String querypartvccode=request.getParameter("querypartvccode");
if(querypartvccode==null) querypartvccode="";
List ls=partaction.getSmpartchangelog(querypartvccode);
List userprivmodenames=null;
UserAction useraction=new UserAction(request);	
userprivmodenames=useraction.queryUserPriv(request.getSession().getAttribute("currentuserid").toString());
String usermodenames="";
for(int i=0;i<userprivmodenames.size();i++)
{
	Hashtable ht=(Hashtable)userprivmodenames.get(i);
	String m=ht.get("modename").toString();
	usermodenames=usermodenames+","+m;
}
usermodenames=usermodenames+",";
//if(usermodenames.indexOf(",modename4,")==-1)
String sessionuserflag=request.getSession().getAttribute("userflag").toString();
if(sessionuserflag.equalsIgnoreCase("3")==false)
{
	out.write("对不起,您没有权限使用此模块!");
	return;
}
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
  	<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
  	<META content=IE=EmulateIE7 http-equiv=X-UA-Compatible>
    <title>零件日志</title>
    <link REL="StyleSheet" HREF="../css/gridstyle.css">
    <link REL="StyleSheet" HREF="../css/show.css">
    <link REL="StyleSheet" HREF="../css/globle.css">
    <script Charset="gbk" src="../scripts/datagrid/datagrid.js" type="text/javascript"></script>	
	<script Charset="gbk" type='text/javascript' src='../scripts/common.js'></script>
	<script Charset="gbk" type='text/javascript' src='../scripts/ajaxUtil.js'></script>
    <script language="javascript">
    var mode="new";
    var callbackresult;
    function initpage(){
		document.getElementById("tC").style.height =document.body.scrollHeight+160;
    }
    function onLight()
    {        
      
    }
    function querybypart()
    {
    	var querypartvccodevalue=document.all.querypartvccode.value;
    	window.open("smpartlog.jsp?querypartvccode="+querypartvccodevalue,"_self");
    }
    </script>
  </head>  
  <body style="margin:0px" onload="initpage();" scroll="no" >
    <table width="100%" border="0" cellSpacing="0" cellPadding="0">   
    <tr>
    <td valign="top" id="gridtd">
     <form name="frmShow" id="frmShow" method="POST" target="_self">
     <P class="r_l_3"></P>
	 <P class="r_l_2"></P>
	 <P class="r_l_1"></P>
     <DIV class="w_l">
	 <H4>
	 <table width="250" border="0" cellSpacing="0" cellPadding="0">	
	 <tr>
	    <td align="left" style="font-size:10pt">
	                编号
	    </td>
	    <td align="left">
	      <input type="text" value="<%=querypartvccode%>" name="querypartvccode" id="querypartvccode"/>
	    </td>
	    <td align="left">
	      <input type="button" value="GO" onclick="querybypart();"/>
	    </td>
     </tr>
     </table></H4>
	 <DIV class="body">	
	 <grid:dbgrid id="tblStat" name="tblStat" width="100" pageSize="<%=pagesize%>" pageObject="<%=pageContext%>" pageRequest="<%=request%>"
		border="0" cellSpacing="1" cellPadding="2" dataMember="" dataSource="<%=ls%>" totalRecords="<%=0%>"
		cssClass="gridTable" lightOn="true" tdIntervalColor="true" verticalHeight="375" gridPosition="relative" 
		dataWidth="100" viewDetail="<%=viewdetail%>" viewWidth="860" viewHeight="600">
            <grid:rownumcolumn dataField="id" headerText="序号" width="8" HAlign="center" sortable="true"/>
            <grid:textcolumn dataField="ipart2vccode" headerText="替代件(历史)" HAlign="center" sortable="true" width="12"/>        
            <grid:textcolumn dataField="ipart1vccode" headerText="替代件(当前)" HAlign="center" sortable="true" width="10"/>
            <grid:textcolumn dataField="changedate" headerText="修改时间" HAlign="center" sortable="true" width="12"/>                       
            <grid:textcolumn dataField="vcmemo" headerText="修改原因" HAlign="center" sortable="true"/>            
      </grid:dbgrid>
	 </DIV>
	 </DIV>	
	 <P class="r_l_1"></P>
	 <P class="r_l_2"></P>
	 <P class="r_l_3"></P>
      </form>
    </td>
    </tr>
    </table>
  </body>
</html>
