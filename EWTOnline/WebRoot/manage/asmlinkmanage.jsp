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
//if(usermodenames.indexOf(",modename7,")==-1)
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
    <title>总成包管理</title>
    <link REL="StyleSheet" HREF="../css/gridstyle.css">
    <link REL="StyleSheet" HREF="../css/show.css">
    <link REL="StyleSheet" HREF="../css/globle.css">
    <link REL="StyleSheet" HREF="css/smpartmanage.css">
    <script Charset="gbk" src="../scripts/datagrid/datagrid.js" type="text/javascript"></script>	
	<script Charset="gbk" type='text/javascript' src='../scripts/common.js'></script>	
	<script Charset="gbk" src="../scripts/ValidInput.js" type='text/javascript'> </script>
	<script type="text/javascript" language="javascript" src="../scripts/jquery/json2.js" ></script>  
	<script type="text/javascript" language="javascript" src="../scripts/jquery/jquery-1.7.min.js" ></script>	
    <script language="javascript">    
    function pagesize()
    {
      	document.all.dataframe.height=window.screen.height-window.screenTop;
      	showdata();  	
    }
    function showdata()    
    {
    	document.all.dataframe.style.display="block";
		document.all.waittable.style.display="none";
    }   
    function queryAsmLink()
    {    	
    	formquery.action="asmlinklist.jsp";
    	if(trim(document.all.asmvccode.value)!="")
    	{    		
    		document.all.exportbtn.disabled=false;
    	}
    	else
    	{
    		document.all.exportbtn.disabled=true;
    	}
    	document.all.dataframe.style.display="none";
		document.all.waittable.style.display="block";
    	formquery.submit();
    }
    function NewAsmLink()
    {
    	var surl="asmlinkdialog.jsp?method=new";
    	var parames="dialogHeight:300px;dialogWidth:400px;center:yes";
    	window.showModelessDialog(surl,dataframe,parames);
    }
    function delAsmLink()
    {
    	dataframe.delAsmLink();
    }
    function exportAsmLink()
    {
    	var p1=document.all.asmvccode.value;
    	window.open("exportasmlink.jsp?asmvccode="+p1,"hiddenframe");
    }
    function importAsmLink()
    {
    	window.showModalDialog("importasmlink.jsp",window,"center:yes;dialogWidth:500px;dialogHeight:250px");
    }
    function onAfterImportSuccess(id)
	{
		formquery.action="./manage/asmlinklist.jsp";
		queryAsmLink();
		//window.open("./manage/asmlinklist.jsp","dataframe");
	}
    </script>
  </head>  
  <body style="margin:0px" scroll=no onload="pagesize();" onresize="pagesize();">
    <table width="100%" border="0" cellSpacing="0" cellPadding="0">
    <tr>
    <td valign="top">	     
	     <P class="r_l_3"></P>
		 <P class="r_l_2"></P>
		 <P class="r_l_1"></P>
	     <DIV class="w_l">		    
			 <H4>
			    <form action="asmlinklist.jsp" target="dataframe" method="post" name="formquery"> 
					<table width="100%" cellSpacing="0" cellPadding="0" border=0>
						<tr>
							<td width="50%" style="font-size:10pt">
								总成编号:
								<input type="text" name="asmvccode" id="asmvccode" size="30"/>
								<input type="button" value="GO" onclick="queryAsmLink();"/>
							</td>
							<td align="right">
								<input type="button" value="新建" onclick="NewAsmLink();"/>
								<input type="button" value="导入" onclick="importAsmLink();" id="importbtn" name="importbtn"/>
								<input type="button" value="删除" onclick="delAsmLink();" />
								<input type="button" value="导出" onclick="exportAsmLink();" id="exportbtn" name="exportbtn" disabled/>
							</td>
						</tr>
					</table>
				</form>
			 </H4>		
			 <DIV class="body">
			 	<iframe src="asmlinklist.jsp" name="dataframe" id="dataframe" frameborder=0 width="100%" style="display:none"></iframe> 		
			 </DIV>
		 </DIV>	
		 <P class="r_l_1"></P>
		 <P class="r_l_2"></P>
		 <P class="r_l_3"></P>	    
    </td>
    </tr>
    </table>
    <table id="waittable"  width="100%" height="600" border="0" cellspacing="0" cellpadding="0" style="display:block">
	<tr>
	<td align="center" valign="middle">
	    <img src ="../images/loading.gif"/>
	</td>
	</tr>
	</table>         
    <iframe name="hiddenframe" id="hiddenframe" width="1" height="1" style="display:none"/>   
  </body>
</html>