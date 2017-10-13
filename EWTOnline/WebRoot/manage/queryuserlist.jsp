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
String queryusername=request.getParameter("queryusername");
if(queryusername==null) queryusername="";
List ls=null;
if(queryusername.equalsIgnoreCase(""))
	ls=useraction.getUsers();
else
	ls=useraction.queryUsers(queryusername);
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
  	<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
  	<META content=IE=EmulateIE7 http-equiv=X-UA-Compatible>
    <title>用户列表 </title>
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
    	fixgridsize(avaiheight-110,gridtd.clientWidth-1);
    }
    function onLight()
    {    	
    	parent.currentuserid=currentRowvalue;
    	getuserpriv(currentRowvalue);
    }
    function getuserpriv(userid)
    {
    	for(var i=1;i<=12;i++)
		{
			var id="modename"+i;			
			parent.document.getElementById(id).checked=false;
		}
    	var url="../servlet.jsp?action=usermanage&method=getuserpriv&userid="+userid;    
		callbackresult=send_request(url,onCallBackForGetUserPriv);
    }
	
	function onCallBackForGetUserPriv(callbacktext)
	{	
		var result=trim(callbacktext.responseText);
		var modenames=trim(result);
		if(modenames!="")
		{
			var returns=modenames.split(",");
			for(var i=0;i<returns.length;i++)
			{
				var id=returns[i];
				if(parent.document.getElementById(id)!=null)
				{
				   parent.document.getElementById(id).checked=true;		
				}
			}			
		}
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
            <grid:gridpager imgFirst="../images/datagrid/First.gif" imgPrevious="../images/datagrid/Previous.gif" imgNext="../images/datagrid/Next.gif" 
            			   imgLast="../images/datagrid/Last.gif" imgBackground="../images/datagrid/di-bt.gif"/>           
            <grid:rownumcolumn dataField="id" headerText="序号" width="8" HAlign="center" sortable="true"/>
            <grid:textcolumn dataField="vccode" headerText="用户账号" HAlign="center" sortable="true" width="12"/>            
            <grid:textcolumn dataField="vccname" headerText="中文名称" HAlign="center" sortable="true"/>
            <grid:textcolumn dataField="vcename" headerText="英文名称" HAlign="center" sortable="true" />
            <grid:textcolumn dataField="flagname" headerText="用户类型" HAlign="center" sortable="true" width="10" tagField="iflag"/>            
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
