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
String showmodel=action.getImageShowModel();
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
//if(usermodenames.indexOf(",modename9,")==-1)
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
    <title>首页管理</title>
    <link REL="StyleSheet" HREF="../css/gridstyle.css">
    <link REL="StyleSheet" HREF="../css/show.css">
    <link REL="StyleSheet" HREF="../css/globle.css">
    <script Charset="gbk" src="../scripts/datagrid/datagrid.js" type="text/javascript"></script>	
	<script Charset="gbk" type='text/javascript' src='../scripts/common.js'></script>
	<script type="text/javascript" src="../css/lib/jquery-1.2.3.pack.js"></script>
	<script type="text/javascript" src="../css/lib/jquery.jcarousel.js"></script>
	<link rel="stylesheet" type="text/css"	href="../css/lib/jquery.jcarousel.css" />
	<script Charset="gbk" type='text/javascript' src='../scripts/ajaxUtil.js'></script>
    <script language="javascript">
    var mode="new";
    var callbackresult;
    var avaiheight = window.screen.height-window.screenTop;
    function onaddwall()
	{
		window.showModalDialog("uploadwall.jsp",window,"center:yes;dialogWidth:400px;dialogHeight:200px");		
	}
    
    function onAfterUploadWall(id)
	{
		window.open("./manage/wallimagelist.jsp","imageframe");
	}
    function initpage(){ 
		document.all.imagecontent.height=560;
		document.all.imagecontent.width=420;
		var imagesrc="<%=imagesrc%>";
		if(imagesrc!="")
		{
			document.all.imagecontent.src=imagesrc;	
		}
    }
    function changeimageframeheight(frameheight)
    {
    	document.all.imageframe.height=frameheight;
    }
    function DeleteUser()
    {
    	var ids=getSelectIds();
    	var url="../servlet.jsp?action=usermanage&method=delete&parames="+ids;
		callbackresult=send_request(url,onCallBackForDeleteUser);
    }
	function onCallBack(callbacktext)
	{	
		
	}
	function changeshowmodel()
	{
		var checkstatus=event.srcElement.value;
		var checked=event.srcElement.checked;		
		var url="../servlet.jsp?action=wallimage&method=changeshowmodel&checkstatus="+checkstatus+"&checked="+checked;
		callbackresult=send_request(url,onCallBack);
	}	
    </script>
  </head>  
  <body style="margin:0px" onload="initpage();" scroll="no">
    <table width="100%" border="0" cellSpacing="0" cellPadding="0">
    <tr>
    <td valign="top">
     <form name="frmShow" id="frmShow" method="POST" target="_self">
	     <P class="r_l_3"></P>
		 <P class="r_l_2"></P>
		 <P class="r_l_1"></P>
	     <DIV class="w_l">
		 <H4>首页图片</H4>
		 <DIV class="body" align="left">
		 <table width="100%" border="0" cellSpacing="0" cellPadding="0">		 
		 <tr>
		 <td style="padding-top:10px;padding-bottom:15px;padding-left:30px;width:500px"> 
			<img src="../images/emptyview.png" id="imagecontent"/>
		  </td>
		 <td valign="baseline">
		 <table width="100%">
		 <tr>
		 <td align="right" style="font-size:10pt">
		 模式:
		 <%if(showmodel.equalsIgnoreCase("0"))
		 {%>
		 	<input type="radio" name="showmodel" value="single" checked onclick="changeshowmodel();"/>单图片循环&nbsp;
		 	<input type="radio" name="showmodel" value="all" onclick="changeshowmodel();"/>全部循环&nbsp;
		 <%}
		 else
		 {%>
			<input type="radio" name="showmodel" value="single" onclick="changeshowmodel();"/>单图片循环&nbsp;		 	
			<input type="radio" name="showmodel" value="all" checked onclick="changeshowmodel();"/>全部循环&nbsp;
		 <%}%>
		 <input type="button" value="导入新图片" onclick="onaddwall();"/>
		 </td>
		 </tr>
		 <tr>
		 <td align="right">
		     <iframe src="wallimagelist.jsp" name="imageframe" id="imageframe" frameborder=0 width="100%" style="padding:0"></iframe>			
	      </td>
	      </tr>
	      </table>
	      </td>	      
		  <td width="20">		     
			     &nbsp;
		   </td>
		  </tr>
		  </table>
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
