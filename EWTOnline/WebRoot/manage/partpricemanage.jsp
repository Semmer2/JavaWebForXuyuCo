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
List userprivmodenames=null;
userprivmodenames=useraction.queryUserPriv(request.getSession().getAttribute("currentuserid").toString());
String usermodenames="";
for(int i=0;i<userprivmodenames.size();i++)
{
	Hashtable ht=(Hashtable)userprivmodenames.get(i);
	String m=ht.get("modename").toString();
	usermodenames=usermodenames+","+m;
}
//usermodenames=usermodenames+",";
String sessionuserflag=request.getSession().getAttribute("userflag").toString();
if(sessionuserflag.equalsIgnoreCase("3")==false)
//if(usermodenames.indexOf(",modename10,")==-1)
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
    <title>用户权限 </title>
    <link REL="StyleSheet" HREF="../css/gridstyle.css">
    <link REL="StyleSheet" HREF="../css/show.css">
    <link REL="StyleSheet" HREF="../css/globle.css">
    <script Charset="gbk" src="../scripts/datagrid/datagrid.js" type="text/javascript"></script>	
	<script Charset="gbk" type='text/javascript' src='../scripts/common.js'></script>
	<script Charset="gbk" type='text/javascript' src='../scripts/ajaxUtil.js'></script>
    <script language="javascript">   
    var callbackresult;
    var selectpartvccode="";
    function initpage(){
		document.all.dataframe.height=window.screen.height-window.screenTop;
		document.all.partdataframe.height=window.screen.height-window.screenTop; 
    }
    function changeuserpriv()
	{
		var modename=event.srcElement.id;
		var checked=event.srcElement.checked;		
		if(currentuserid!="-1")
		{		
			var url="../servlet.jsp?action=usermanage&method=manageuserpriv&modename="+modename+"&userid="+currentuserid+"&checked="+checked;
			callbackresult=send_request(url,onCallBack);
		}
		else
		{
			alert("请选择某一用户!");	
		}
	}
	function onCallBack(callbacktext)
	{
		
	}
    function queryuser()
    {
    	if(frmShow.querypartvccode.value=="")
    	{
    		alert("请选择某一零件!");
    	}
    	else
    	{
	    	frmShow.action="partuserpricelist.jsp";
	    	frmShow.target="dataframe";
	    	frmShow.submit();
    	}
    }
    function querypart()
    {
    	frmShow.action="partlistforprice.jsp";
    	frmShow.target="partdataframe";
    	frmShow.querypartvccode.value="";
    	frmShow.submit();
    }
    function querypartprice(partvccode)
    {
    	frmShow.querypartvccode.value=partvccode;
    	queryuser();
    }
    </script>
  </head>  
  <body style="margin:0px" onload="initpage();" scroll="no">
   <form name="frmShow" id="frmShow" method="POST" action="partuserpricelist.jsp" target="dataframe">
   <input type="hidden" name="querypartvccode" id="querypartvccode" size="30"/>
  <P class="r_l_3"></P>
	<P class="r_l_2"></P>
	<P class="r_l_1"></P>
	<DIV class="w_l">
	<DIV class="body">
    <table width="100%" border="0" cellSpacing="0" cellPadding="0">
    <tr>    
    <td valign="top" style="padding-left:2px;padding-right:5px;font-size:10pt" width="60%">
	 零件编码:<input type="text" name="queryvccode" id="queryvccode" size="30"/>
	 <input type="button" value="GO" onclick="querypart();"/>	 
	 <iframe src="partlistforprice.jsp" name="partdataframe" id="partdataframe" frameborder=0 width="100%"></iframe>	
    </td>    
    <td valign="top" style="padding-left:5px;font-size:10pt">
            经销商:<input type="text" name="queryusername" id="queryusername" size="30"/>
	 <input type="button" value="GO" onclick="queryuser();"/>	 
	 <iframe src="partuserpricelist.jsp" name="dataframe" id="dataframe" frameborder=0 width="100%"></iframe>
    </td>    
    </tr>
    </table>
    </DIV>
	</DIV>
	<P class="r_l_1"></P>
	<P class="r_l_2"></P>
	<P class="r_l_3"></P>
    </form>
  </body>
</html>
