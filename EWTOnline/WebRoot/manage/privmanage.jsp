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
usermodenames=usermodenames+",";
//if(usermodenames.indexOf(",modename8,")==-1)
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
    <title>用户权限 </title>
    <link REL="StyleSheet" HREF="../css/gridstyle.css">
    <link REL="StyleSheet" HREF="../css/show.css">
    <link REL="StyleSheet" HREF="../css/globle.css">
    <script Charset="gbk" src="../scripts/datagrid/datagrid.js" type="text/javascript"></script>	
	<script Charset="gbk" type='text/javascript' src='../scripts/common.js'></script>
	<script Charset="gbk" type='text/javascript' src='../scripts/ajaxUtil.js'></script>
    <script language="javascript">   
    var callbackresult;
    function initpage(){
		document.all.dataframe.height=window.screen.height-window.screenTop; 
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
    	frmShow.submit();
    }
    </script>
  </head>  
  <body style="margin:0px" onload="initpage();" scroll="no">
    <table width="100%" border="0" cellSpacing="0" cellPadding="0">
    <tr>
    <td valign="top" id="gridtd">
     <form name="frmShow" id="frmShow" method="POST" action="queryuserlist.jsp" target="dataframe">
     <P class="r_l_3"></P>
	 <P class="r_l_2"></P>
	 <P class="r_l_1"></P>
     <DIV class="w_l">	
	 <DIV class="body" align="left" style="font-size:10pt">
	 用户名:<input type="text" name="queryusername" id="queryusername" size="30"/>
	 <input type="button" value="GO" onclick="queryuser();"/>	 
	 <iframe src="queryuserlist.jsp" name="dataframe" id="dataframe" frameborder=0 width="100%"></iframe>
	 </DIV>
	 </DIV>	
	 <P class="r_l_1"></P>
	 <P class="r_l_2"></P>
	 <P class="r_l_3"></P>
      </form>
    </td>
    <td width="200">
    &nbsp;
    </td>
    <td valign="top">	
	<P class="r_l_3"></P>
	<P class="r_l_2"></P>
	<P class="r_l_1"></P>
	<DIV class="w_l">
	<H4>用户权限</H4>
	<DIV class="body">
	<form name="frmManage" id="frmManage" method="POST" target="_self"> 
	<table border=0 width="400" cellSpacing="5" cellPadding="0">
	<tr>
	<td>
	访问统计
	</td>
	<td>
	<input type="checkbox" id="modename1" name="modename1" onclick="changeuserpriv();"/>
	</td>
	</tr>
	<tr>
	<td>
	公吿管理
	</td>
	<td>
	<input type="checkbox" id="modename2" name="modename2" onclick="changeuserpriv();"/>
	</td>
	</tr>
	<tr>
	<td>
	用户管理
	</td>
	<td>
	<input type="checkbox" id="modename3" name="modename3" onclick="changeuserpriv();"/>
	</td>
	</tr>
	<tr>
	<td>
	零件日志
	</td>
	<td>
	<input type="checkbox" id="modename4" name="modename4" onclick="changeuserpriv();"/>
	</td>
	</tr>
	<tr>
	<td>
	角色维护
	</td>
	<td>
	<input type="checkbox" id="modename5" name="modename5" onclick="changeuserpriv();"/>
	</td>
	</tr>
	<tr>
	<td>
	零件管理
	</td>
	<td>
	<input type="checkbox" id="modename6" name="modename6" onclick="changeuserpriv();"/>
	</td>
	</tr>
	<tr>
	<td>
	零件价格
	</td>
	<td>
	<input type="checkbox" id="modename10" name="modename10" onclick="changeuserpriv();"/>
	</td>
	</tr>
	<tr>
	<td>
	总成包管理
	</td>
	<td>
	<input type="checkbox" id="modename7" name="modename7" onclick="changeuserpriv();"/>
	</td>
	</tr>
	<tr>
	<td>
	BOM管理
	</td>
	<td>
	<input type="checkbox" id="modename12" name="modename12" onclick="changeuserpriv();"/>
	</td>
	</tr>
	<tr>
	<td>
	权限维护
	</td>
	<td>
	<input type="checkbox" id="modename8" name="modename8" onclick="changeuserpriv();"/>
	</td>
	</tr>
	<tr>
	<td>
	首页维护
	</td>
	<td>
	<input type="checkbox" id="modename9" name="modename9" onclick="changeuserpriv();"/>
	</td>
	</tr>
	<tr>
	<td>
	图册反馈
	</td>
	<td>
	<input type="checkbox" id="modename11" name="modename11" onclick="changeuserpriv();"/>
	</td>
	</tr>
	</table>
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