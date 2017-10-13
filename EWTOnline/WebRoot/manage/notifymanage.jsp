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
List ls=new ArrayList();
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

//if(usermodenames.indexOf(",modename2,")==-1)
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
  	<meta http-equiv="pragma" content="no-cache" />
  	<META content=IE=EmulateIE7 http-equiv=X-UA-Compatible>  	
    <title>公告管理</title>
    <link REL="StyleSheet" HREF="../css/gridstyle.css">
    <link REL="StyleSheet" HREF="../css/show.css">
    <link REL="StyleSheet" HREF="../css/globle.css">
    <link REL="StyleSheet" HREF="css/smpartmanage.css">
    <script Charset="gbk" src="../scripts/datagrid/datagrid.js" type="text/javascript"></script>	
	<script Charset="gbk" type='text/javascript' src='../scripts/common.js'></script>	
	<script Charset="gbk" src="../scripts/ValidInput.js" type='text/javascript'> </script>
	<script type="text/javascript" language="javascript" src="../scripts/jquery/json2.js" ></script>  
	<script type="text/javascript" language="javascript" src="../scripts/jquery/jquery-1.7.min.js" ></script>
	<script type="text/javascript" src="../ueditor/editor_config.js"></script>
	<script type="text/javascript" src="../ueditor/editor_all.js"></script>
	<link rel="stylesheet" href="../ueditor/themes/default/ueditor.css"/>
    <script language="javascript">
    function iFrameHeight()
    {
		var ifm= document.getElementById("downframe"); 
		var subWeb = document.frames ? document.frames["downframe"].document : ifm.contentDocument; 
		if(ifm != null && subWeb != null) { 
			ifm.height = subWeb.body.scrollHeight; 
		}
	} 
    function querynotify()
    {
    	formquery.querytitle.value=encodeURI(trim(formquery.notifytitle.value));
    	formquery.submit();
    }
    function setNewNotify()
    {
    	downframe.mode="new";
    	downframe.setEditContent("");
    	downframe.setNewMode();
    }
    function delNotify()
    {
    	downframe.delNotify();
    }
    function saveNotify()
    {
    	downframe.saveNotify();
    }
    </script>
  </head>  
  <body style="margin:0px" scroll=no>
    <table width="100%" border="0" cellSpacing="0" cellPadding="0">
    <tr>
    <td valign="top">	     
	     <P class="r_l_3"></P>
		 <P class="r_l_2"></P>
		 <P class="r_l_1"></P>
	     <DIV class="w_l">		    
			 <H4>
			    <form action="notifymanagedown.jsp" target="downframe" method="post" name="formquery"> 
					<table width="100%" cellSpacing="0" cellPadding="0" border=0>
						<tr>
							<td width="50%">
								标题:
								<input type="text" name="notifytitle" id="notifytitle" size="60"/>
								<input type="button" value="GO" onclick="querynotify();"/>
								<input type="hidden" name="querytitle" id="querytitle"/>
							</td>
							<td align="right">
								<input type="button" value="新建" onclick="setNewNotify();" />
								<input type="button" value="删除" onclick="delNotify();" />
								<input type="button" value="保存" onclick="saveNotify();"/>
							</td>
						</tr>
					</table>
				</form>
			 </H4>		
			 <DIV class="body">
			 	<iframe src="notifymanagedown.jsp" name="downframe" id="downframe" frameborder=0 width="100%" onload="iFrameHeight()"></iframe> 		
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
