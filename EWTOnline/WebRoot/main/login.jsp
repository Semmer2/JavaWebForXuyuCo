<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="UTF-8"%>
<%@ page import="com.jl.action.*"%>
<%
response.setHeader("Cache-Control", "no-cache");
response.setHeader("Pragma", "no-cache");
response.setDateHeader("Expires", 0);
WallImageAction action = new WallImageAction(request);
String wall=action.getWallFromDataBase();
if(request.getMethod().equalsIgnoreCase("post"))
{
	String islogout=request.getParameter("logoutinput");
	if(islogout==null) islogout="";
	if(islogout.equals("logout"))
	{
		request.getSession().putValue("currentuserid",null);
	}
}
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
		<meta http-equiv="X-UA-Compatible" content="IE=EmulateIE7" />
		<title>EWT Login</title>
		<link href="../images/login.css" rel="stylesheet" type="text/css" />
		<script Charset="UTF-8" type='text/javascript' src='../scripts/common.js'></script>
		<script Charset="UTF-8" src="../scripts/ValidInput.js" type='text/javascript'> </script>
		<script type="text/javascript" language="javascript" src="../scripts/jquery/json2.js" ></script>  
		<script type="text/javascript" language="javascript" src="../scripts/jquery/jquery-1.7.min.js" ></script>		
		<script Charset="UTF-8" type='text/javascript' src='../scripts/ajaxUtil.js'></script>
		<script type="text/javascript">
		var i=0;
		var j=0;
		$(function() {
			$('.captcha').focus(function() {
				$('.yzm-box').show();
			});
		
			$('.captcha').focusout(function() {
				$('.yzm-box').hide();
			});
		})
		function go()
		{
			if(trim(loginform.username.value)=="" || trim(loginform.password.value)=="")
			{
				alert("请输入用户或密码!");
				return;
			}			
			var url="do.json?action=login";				
			send_post_request(url,onAfterLogin,$("#loginform").serialize());			
		}
		function onAfterLogin(callbacktext)
    	{
    		var result="("+trim(callbacktext.responseText)+")";    	
			var returnobject=eval(trim(result));
			if(returnobject.result=="success")
			{
				window.open("seriesvehicle.jsp","_self");
			}
			else
			{
				$("#message-box").show();	
			}
		}
		function onInputFocus()
		{			
			if(event.srcElement.name=="username")
			{
				if(i==0) event.srcElement.value="";
				i=i+1;
			}
			if(event.srcElement.name=="password")
			{
				if(j==0) event.srcElement.value="";
				j=j+1;
			}
		}
		function inputenter()
		{
			if(window.event.keyCode == 13)
			{
				 go();
			}
		}
		function initpage()
		{
			loginform.username.focus();
		}
		</script>
	</head>
	<body onload="initpage();">
	<div id="message-box">
		UserName or Password Error！
	</div>	
	<form action="do.json" method="post" name="loginform" id="loginform">
	<table width="1024" border="0" cellspacing="0" cellpadding="0" align="center" style="margin: 0px">
	<tr>
	<td style="padding-left:10px;padding-top:30px" align="center" width="550">
	<img src="<%=wall%>" width="450" height="600"/>
	</td>
	<td style="padding-top:0x">
		<table width="100%" border="0" cellspacing="0" cellpadding="0" align="center" style="margin: 0px">
		<tr>
		<td align="left" style="padding-bottom:40px" valign="top">
		<img src="../images/login.png"/>
		</td>
		</tr>
		<tr>
		<td align="left" style="padding-bottom:50px">
		<input class="input_text" name="username" value="someone@example.com" type="text" onfocus="onInputFocus();"/>
		</td>
		</tr>
		<tr>
		<td align="left" style="padding-bottom:50px">			
		<input class="input_text" name="password" value="password" type="password" onfocus="onInputFocus();" onkeypress="inputenter()"/>
		</td>
		</tr>
		<tr>
		<td align="left" >		
		<input name="" type="button" class="login-btn" onclick="go();"/>
		</td>
		</tr>
		<tr>
		<td align="left" style="padding-left:5px;padding-top:160px">		
		<img src="../images/email.png"/>
		<br/>
		zhuzm@jmie.com.cn		
		</td>
		</tr>			
		</table>
	</td>
	</tr>
	</table>
	</form>	
	</body>
</html>
