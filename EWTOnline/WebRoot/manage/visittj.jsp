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
UserAction useraction=new UserAction(request); 
List ls=useraction.getVisitTj();
List userprivmodenames=null;
userprivmodenames=useraction.queryUserPriv(request.getSession().getAttribute("currentuserid").toString());
String usermodenames="";
for(int i=0;i<userprivmodenames.size();i++)
{
	Hashtable ht=(Hashtable)userprivmodenames.get(i);
	String m=ht.get("modename").toString();
	usermodenames=usermodenames+","+m;
}
usermodenames=usermodenames+",";
//if(usermodenames.indexOf(",modename1,")==-1)
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
    <title>访问统计</title>
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
		document.getElementById("tC").style.height=document.body.scrollHeight+130;
    }
    function onLight()
    {        
      
    }
    function exportvisit()
    {
    	alert("对不起,还没有此功能!");
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
	 <table width="100%" border="0" cellSpacing="0" cellPadding="0">	
	 <tr>
	    <td align="right">
	     <input type="button" value="导出" onclick="exportvisit();"/>
	    </td>
     </tr>
     </table></H4>
	 <DIV class="body">	
	 <grid:dbgrid id="tblStat" name="tblStat" width="100" pageSize="<%=pagesize%>" pageObject="<%=pageContext%>" pageRequest="<%=request%>"
		border="0" cellSpacing="1" cellPadding="2" dataMember="" dataSource="<%=ls%>" totalRecords="<%=0%>"
		cssClass="gridTable" lightOn="true" tdIntervalColor="true" verticalHeight="375" gridPosition="relative" 
		dataWidth="100" viewDetail="<%=viewdetail%>" viewWidth="860" viewHeight="600">
            <grid:rownumcolumn dataField="id" headerText="序号" width="8" HAlign="center" sortable="true"/>
            <grid:textcolumn dataField="vccode" headerText="账号" HAlign="center" sortable="true" width="12"/>        
            <grid:textcolumn dataField="vccname" headerText="中文名称" HAlign="center" sortable="true" width="10"/>
            <grid:textcolumn dataField="vcename" headerText="英文名称" HAlign="center" sortable="true" width="10"/>                       
            <grid:textcolumn dataField="visitdate" headerText="最后访问时间" HAlign="center" sortable="true" width="10"/>
            <grid:textcolumn dataField="vcip" headerText="访问IP" HAlign="center" sortable="true" width="10" tagField="iroleid"/>
            <grid:textcolumn dataField="visitcount" headerText="访问次数" HAlign="center" sortable="true" width="10" tagField="iroleid"/>
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
