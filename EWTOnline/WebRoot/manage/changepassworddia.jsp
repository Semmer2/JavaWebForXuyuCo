<%@ page contentType="text/html;charset=utf-8" pageEncoding="utf-8"%>
<%@ page import="java.util.*"%>
<%@ taglib uri="/WEB-INF/tld/datagrid.tld" prefix="grid"%>
<%@ page import="com.jl.action.*"%>
<%@include file="../main/checksession.jsp"%>
<%
response.setHeader("Cache-Control", "no-cache");
response.setHeader("Pragma", "no-cache");
response.setDateHeader("Expires", 0);
String title="Password";
for(int i=0;i<9999;i++)
{
	title=title+"&nbsp;";
}
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
  	<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
  	<meta http-equiv="pragma" content="no-cache" />
  	<META content=IE=EmulateIE7 http-equiv=X-UA-Compatible>  	
    <title><%=title%></title>
     <link rel="stylesheet" type="text/css" href="../css/zw.css"></link>
    <link rel="stylesheet" type="text/css" href="../css/standard.css"></link>
    <link rel="stylesheet" type="text/css" href="../css/queryview.css"></link>
    <link REL="StyleSheet" HREF="../css/index.css">
    <link REL="StyleSheet" HREF="css/smpartmanage.css">
	<script Charset="gbk" type='text/javascript' src='../scripts/common.js'></script>	
	<script Charset="gbk" src="../scripts/ValidInput.js" type='text/javascript'> </script>
	<script type="text/javascript" language="javascript" src="../scripts/jquery/json2.js" ></script>  
	<script type="text/javascript" language="javascript" src="../scripts/jquery/jquery-1.7.min.js" ></script>	
    <script language="javascript">
    function changepwd()
    {    		
   		var oldpwd=document.all.oldpassword.value;
   		var newpwd=document.all.newpassword.value;    		
   		if(trim(oldpwd)=="")
   		{
   			alert("Please Enter Old Password!");
   			return;
   		}
   		if(trim(newpwd)=="")
   		{
   			alert("Please Enter New Password!");
   			return;
   		}
   		changepassword();		
    }
    function changepassword()
    {    	
    	var oldpwd=trim(document.all.oldpassword.value);
    	var newpwd=trim(document.all.newpassword.value);
    	var confirmpwd=trim(document.all.confirmpassword.value);
    	if(confirmpwd==newpwd)
    	{    		
    			var url="do.json?action=changepwd&method=changepwd&oldpwd="+oldpwd+"&newpwd="+newpwd;
				$.ajax({
				type : "GET",//使用post方法访问后台  
				dataType : "json",//返回json格式的数据  
				url : url,//要访问的后台地址 
				cache: false,//默认: true,设置为 false 将不会从浏览器缓存中加载请求信息。  
				contentType : "application/json;charset=utf-8",	
				beforeSend:onBeforeGetData,
				complete:onComplete,
				success:onAfterChange,
				error:onError 
				});
    	}
    	else
    	{
    		alert("Sorry,New Password is vastly different from Confirm PassWord!");
    	}
    }
    function onBeforeGetData()
    {
    	
    }
    function onError()
    {
    	
    }
    function onComplete()
    {
    	
    }
    function onAfterChange(data)
    {
    	if(data.result!="0")
		{    			
    		var status=getvalue(data.status);
    		if(status!="1")
    		{
    			if(status=="-1")
				{
					alert("Sorry,New Password and Old Password is Same");
				}
				if(status=="-2")
				{
					alert("Sorry,The User Old Password Error!");
				}
				if(status=="-3")
				{
					alert("Sorry,The User is not Exist!");
				}
			}
    		else
    		{
    			window.close();	
    		}
	   }
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
			 <table width="100%" border="0" cellSpacing="0" cellPadding="0">
			 <td style="font-size:10pt" style="padding:4px">
			   Change Password
			 </td>
			 <td>
			 &nbsp;
			 </td>
			 <td align="right">
			 <input type="button" value="Ok" onclick="changepwd();" style="width:60"/>
			 <input type="button" value="Cancel" onclick="window.close();" style="width:60"/>
			 </td>
			 </H4>		
			 <DIV class="body">
			 <form name="frmpassword" id="frmpassword">	
			 <table align='center' width="100%" border="1" cellspacing="0" cellpadding="0" style="word-wrap:break-word;" class="zw" bordercolorlight="#25689F" bordercolordark="#ffffff"'>
			 <tr>
				<td width="120" >Old&nbsp;&nbsp;PassWord</td>
				<td align="center" style="padding-left:1px">				    
					<input type="password" name="oldpassword" id="oldpassword" size="33" />
				</td>
			 </tr>			
			 <tr>
				<td width="120" >New PassWord</td>
				<td align="center" style="padding-left:1px">	
					<input type="password" id="newpassword" name="newpassword" size="33" />
				</td>
			 </tr>
			 <tr>
				<td width="120" >Confirm PassWord</td>
				<td align="center" style="padding-left:1px">	
					<input type="password" id="confirmpassword" name="confirmpassword" size="33" />
				</td>
			 </tr>
			 </table>
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
