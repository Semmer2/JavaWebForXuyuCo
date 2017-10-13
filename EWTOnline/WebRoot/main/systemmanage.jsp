<%@ page contentType="text/html;charset=utf-8" pageEncoding="utf-8"%>
<%@ page import="java.util.*"%>
<%@ taglib uri="/WEB-INF/tld/datagrid.tld" prefix="grid"%>
<%@ page import="com.jl.action.*"%>
<%@ page import="com.jl.entity.*"%>
<%@ page import="java.net.*"%>
<%@include file="checksession.jsp"%>
<%
	response.setHeader("Cache-Control", "no-cache");
	response.setHeader("Pragma", "no-cache");
	response.setDateHeader("Expires", 0);
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
	String sessionuserflag=request.getSession().getAttribute("userflag").toString();
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
	<meta http-equiv="imagetoolbar" content="no" />
	<meta http-equiv="X-UA-Compatible" content="IE=EmulateIE7" />
	<title>EWT Online System Manage</title>	
	<script type="text/javascript" src="../lib/jquery-1.2.3.pack.js"></script>
	<script type="text/javascript" src="../lib/jquery.jcarousel.pack.js"></script>		
	<script Charset="GBK" type='text/javascript' src='../scripts/common.js'></script>
	<script Charset="UTF-8" type='text/javascript' src='../scripts/ajaxUtil.js'></script>		
	<link href="../css/index.css" rel="stylesheet" type="text/css" />
	<link href="../css/index_table.css" rel="stylesheet" type="text/css" />
	<link href="../css/menu.css" rel="stylesheet" type="text/css" />
	<style>
		.menu { 
			height: 35px;
			display: block;
		}

		.menu ul {
			list-style: none;
			padding: 0;
			margin: 0;
		}

		.menu ul li {
			/* width and height of the menu items */  
			float: left;
			overflow: hidden;
			position: relative;
			text-align: center;
			line-height: 35px;
		}

		.menu ul li a {
			/* must be postioned relative  */ 
			position: relative;
			display: block;
			width: 90px;
			height: 35px;
			font-family: Arial;
			font-size: 12px;
			font-weight: bold;
			letter-spacing: 1px;
			text-transform: uppercase;
			text-decoration: none;
			cursor: pointer;
		}

		.menu ul li a span {
			/* all layers will be absolute positioned */
			position: absolute;
			left: 0;
			width: 110px;
		}

		.menu ul li a span.out {
			top: 0px;
		}

		.menu ul li a span.over,
		.menu ul li a span.bg {
			/* hide */  
			top: -35px;
		}

		/** 1st example **/

		#menu {
			background: #379be9;
		}

		#menu ul li a {
			color: #fff;
		}

		#menu ul li a span.over {
			color: #000;
		}

		#menu ul li span.bg {
			/* height of the menu items */  
			height: 35px;
			background: url('../images/menu/bg_over.gif') center center no-repeat;
		}

	</style>
	<script type="text/javascript" src="http://ajax.googleapis.com/ajax/libs/jquery/1.3/jquery.min.js"></script>
	<script language="javascript">
	var w=screen.availWidth;
	var h=screen.availHeight;
  	var sFeatures = "fullscreen=0,toolbar=0,location=0,directories=0,status=1,menubar=0";
    sFeatures += ",scrollbars=0,resizable=1,top=0,left=0,width=" + w + ",height=" + h + " ";
    sFeatures="";
	   function iFrameHeight()
      {
		var ifm= document.getElementById("manageframe"); 
		var subWeb = document.frames ? document.frames["manageframe"].document : ifm.contentDocument; 
		if(ifm != null && subWeb != null) { 
			ifm.height = subWeb.body.scrollHeight; 
		}		
	  } 
		$(document).ready(function() {
			
			/// wrap inner content of each anchor with first layer and append background layer
			$("#menu li a").wrapInner( '<span class="out"></span>' ).append( '<span class="bg"></span>' );

			// loop each anchor and add copy of text content
			$("#menu li a").each(function() {
				$( '<span class="over">' +  $(this).text() + '</span>' ).appendTo( this );
			});

			$("#menu li a").hover(function() {
				// this function is fired when the mouse is moved over
				$(".out",	this).stop().animate({'top':	'45px'},	250); // move down - hide
				$(".over",	this).stop().animate({'top':	'0px'},		250); // move down - show
				$(".bg",	this).stop().animate({'top':	'0px'},		120); // move down - show

			}, function() {
				// this function is fired when the mouse is moved off
				$(".out",	this).stop().animate({'top':	'0px'},		250); // move up - show
				$(".over",	this).stop().animate({'top':	'-45px'},	250); // move up - hide
				$(".bg",	this).stop().animate({'top':	'-45px'},	120); // move up - hide
			});
		});
        function openmanagefun(url)
        {
        	window.open(url,"manageframe");
        }
        function gohome()
        {
        	window.open("seriesvehicle.jsp","_blank",sFeatures);        	
        }
        function openbulletin()
		{
			//window.showModelessDialog("../manage/notifyview.jsp",window,"center:yes;dialogWidth:1024px;dialogHeight:800px");
			var s = "fullscreen=0,toolbar=0,location=0,directories=0,status=1,menubar=0";
			s += ",scrollbars=0,resizable=1,top=0,left=0,width=" + w + ",height=" + h + " ";
			window.open("bulletinread.jsp","_blank",s);
		}
        function AdvanceSearch()
		{
			window.open("search.jsp","_blank",sFeatures);			
		}
        function openset()
		{
			window.open("systemmanage.jsp", "_blank",sFeatures);
			
		}
        function changepassword()
		{    	
	    	var surl="../manage/changepassworddia.jsp";
	    	var parames="dialogHeight:130px;dialogWidth:360px;center:yes";
	    	window.showModelessDialog(surl,window,parames);
		}
		function logout()
		{
			logoutfrm.submit();
		}
	</script>
</head>
<body>
<body style="margin: 0px"  scroll="no">
<table width="100%" border="0" cellspacing="0" cellpadding="0" align="center" style="margin: 0px">
	<tr>
	   <td style="padding:0px">
		   <table width="100%" border="0" cellspacing="0" cellpadding="0" style="padding: 0px">
						<tr>
							<td class="loginimage" height="36px" width="126" style="padding: 0px">
								&nbsp;
							</td>
							<td class="bar" style="color: #000000;font-size:13px;font-weight:bold" style="padding: 0px">
								<span title="Home" style="padding-right:15px" style="cursor:hand" onclick="gohome();">Home</span>
								<span title="ChangePassword" style="padding-right:15px" style="cursor:hand" onclick="changepassword();">User</span>
								<span title="AdvanceQuery" style="padding-right:15px" style="cursor: hand" onclick="AdvanceSearch();" >Search</span>
								<%if(sessionuserflag.equalsIgnoreCase("3"))
								{%>
								<span title="Setting" style="padding-right:15px" style="cursor: hand" onclick="openset();" >Setting</span>
								<%}%>
								<span title="Bulletin" style="padding-right:15px" style="cursor: hand" onclick="openbulletin();" >Bulletin</span>
								<span title="Logout" style="padding-right:15px" style="cursor: hand" onclick="logout();" >Logout</span>
							</td>
							<td class="bar" width="20px" style="padding: 0px">
								&nbsp;
							</td>
						</tr>
					</table>
		</td>
	</tr>
	<tr>
	<td>	
		<div id="content">
		<div id="menu" class="menu">
			<ul>				
				<%
				String modeenabled="";
				if(sessionuserflag.equalsIgnoreCase("3")==false) modeenabled="false";				
				if(usermodenames.indexOf(",modename1,")!=-1) modeenabled="";%>
				<li><a href="javascript:openmanagefun('../manage/visittj.jsp');" <%=modeenabled%>>访问统计</a></li>
				
				<li><a href="javascript:openmanagefun('../manage/bulletinmanage.jsp');" <%=modeenabled%>>公吿管理</a></li>
				
				<li><a href="javascript:openmanagefun('../manage/usermanage.jsp');" <%=modeenabled%>>用户管理</a></li>
						
				<li><a href="javascript:openmanagefun('../manage/smpartlog.jsp');" <%=modeenabled%>>零件日志</a></li>
				
				<li><a href="javascript:openmanagefun('../manage/rolemanage.jsp');" <%=modeenabled%>>角色维护</a></li>
				
				<li><a href="javascript:openmanagefun('../manage/smpartmanage.jsp');" <%=modeenabled%>>零件管理</a></li>
				
				<li><a href="javascript:openmanagefun('../manage/partpricemanage.jsp');" <%=modeenabled%>>零件价格</a></li>
				
				<li><a href="javascript:openmanagefun('../manage/asmlinkmanage.jsp');" <%=modeenabled%>>总成管理</a></li>
				
				<li><a href="javascript:openmanagefun('../manage/vehlinkmanage.jsp');" <%=modeenabled%>>BOM管理</a></li>
				
				<li><a href="javascript:openmanagefun('../manage/privmanage.jsp');" <%=modeenabled%>>权限维护</a></li>
				
				<li><a href="javascript:openmanagefun('../manage/toppagemanage.jsp');" <%=modeenabled%>>首页维护</a></li>
				
				<li><a href="javascript:openmanagefun('../manage/suggestmanage.jsp');" <%=modeenabled%>>图册反馈</a></li>
			</ul>
		</div>
		</div>
	</td>
	</tr>
	<tr>
	<td>	
		<iframe src="../manage/visittj.jsp" name="manageframe" id="manageframe" frameborder=0 width="100%" onload="iFrameHeight();"></iframe>
	</td>
	</tr>
</table>
<form action="../login.jsp" method="post" target="_self" name="logoutfrm" style="display:none">
<input type="hidden" name="logoutinput" value="logout"/>
</form>
</body>
</html>