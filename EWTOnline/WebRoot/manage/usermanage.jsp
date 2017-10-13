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
RoleAction roleaction=new RoleAction(request);
List rolels=roleaction.getRoles();
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
//if(usermodenames.indexOf(",modename3,")==-1)
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
    <title>用户管理</title>
    <link REL="StyleSheet" HREF="../css/gridstyle.css">
    <link REL="StyleSheet" HREF="../css/show.css">
    <link REL="StyleSheet" HREF="../css/globle.css">
    <script Charset="gbk" src="../scripts/datagrid/datagrid.js" type="text/javascript"></script>	
	<script Charset="gbk" type='text/javascript' src='../scripts/common.js'></script>
	<script Charset="gbk" type='text/javascript' src='../scripts/ajaxUtil.js'></script>
	<script Charset="gbk" src="../scripts/ValidInput.js" type='text/javascript'> </script>
    <script language="javascript">
    var mode="new";
    var callbackresult;
    function initpage(){
    	<%
    	for(int i=0;i<rolels.size();i++)
    	{
    		Hashtable ht=(Hashtable)rolels.get(i);
    		String rolename=ht.get("vcrolename").toString();
    		String id=ht.get("id").toString();
    		%>
    		var aElement=document.createElement("OPTION");
			aElement.value="<%=id%>";
			aElement.text="<%=rolename%>";
			document.all.iroleid.options.add(aElement);
    		<%
    	}
    	%>
    	
		var ifm=document.getElementById("tblStat");
		document.getElementById("tC").style.height =document.body.scrollHeight+71; 
		
    }
    function onLight()
    {        
        frmManage.vccode.value=currentlightvalues[1];
        frmManage.vcpassword.value=currentlightvalues[2];
        frmManage.vccname.value=currentlightvalues[3];
        frmManage.vcename.value=currentlightvalues[4];
        frmManage.iflag.value=currentlightvalues[5];
        frmManage.iroleid.value=currentlightvalues[6];
        mode="edit";
    }
    function setNewUser()
    {
    	frmManage.vccode.value="";
        frmManage.vccname.value="";
        frmManage.vcename.value="";
        frmManage.iflag.selectedIndex=1;
        frmManage.iroleid.selectedIndex=0;
        frmManage.vcpassword.value="";
    	mode="new";    	
    }
    function save()
    {
    	if(verifyAll(frmManage))
		{
	    	if(mode=="new")
	    	{
	    		var values="";
	    		var v1=frmManage.vccode.value;    		
	        	var v2=frmManage.vcpassword.value;
	        	var v3=frmManage.vccname.value;
		        var v4=frmManage.vcename.value;
		        var v5=frmManage.iflag.value;
		        var v6=frmManage.iroleid.value;
	        	if(trim(v1)=="" || trim(v2)=="")
	        	{
	        		alert("请输入必输信息!");
	        	}
	        	else
	        	{
		        	values=encodeURI(trim(v1))+","+encodeURI(trim(v2))+","+encodeURI(v3)+","+encodeURI(trim(v4))+","+encodeURI(trim(v5))+","+encodeURI(trim(v6));
		    		NewUser(values);
	    		}
	    	}
	    	if(mode=="edit")
	    	{
	    		var values="";
	    		if(currentlightvalues!=null)
	    		{
		    		var datakey=currentlightvalues[0];	    		
		    		var v1=frmManage.vccode.value;    		
		        	var v2=frmManage.vcpassword.value;
		        	var v3=frmManage.vccname.value;
			        var v4=frmManage.vcename.value;
			        var v5=frmManage.iflag.value;
			        var v6=frmManage.iroleid.value;
		        	if(trim(v1)=="" || trim(v2)=="")
		        	{
		        		alert("请输入必输信息!");
		        	}
		        	else
		        	{
		        		values=encodeURI(trim(v1))+","+encodeURI(trim(v2))+","+encodeURI(v3)+","+encodeURI(trim(v4))+","+encodeURI(trim(v5))+","+encodeURI(trim(v6));
		    			SaveUser(datakey,values);
		    		}
	    		}
	    		else
	    		{
	    			alert("请选择角色!");
	    		}
	    	}
	    }
    }
    function DeleteUser()
    {
    	if(confirm("是否要删除，确定、取消?")=="1")
    	{
    	var ids=getSelectIds();
    	var url="../servlet.jsp?action=usermanage&method=delete&parames="+ids;
		callbackresult=send_request(url,onCallBackForDeleteUser);
		}
    }
	function NewUser(parames)
	{
		if(parames!="")
		{
			var url="../servlet.jsp?action=usermanage&method=new&parames="+parames;
			callbackresult=send_request(url,onCallBackForNewUser);
		}
	}	
	function onCallBackForDeleteUser(callbacktext)
	{	
		var result=trim(callbacktext.responseText);
		var rtn=trim(result);
		var returns=rtn.split(",");
		if(returns[0]!="")
		{			
		    onDeleteRow();
		    alert("删除成功!");
	    }
	    else
	    {
	    	alert(returns[1]);
	    }
	}
	function onCallBackForNewUser(callbacktext)
	{	
		var result=trim(callbacktext.responseText);
		var rtn=trim(result);
		var returns=rtn.split(",");
		if(returns[0]!="")
		{
			var v1=frmManage.vccode.value;    		
        	var v2=frmManage.vcpassword.value;
        	var v3=frmManage.vccname.value;
	        var v4=frmManage.vcename.value;	        
	       	var v5=trim(frmManage.iflag.options(frmManage.iflag.selectedIndex).text)+";"+frmManage.iflag.value;
	       	var v6=trim(frmManage.iroleid.options(frmManage.iroleid.selectedIndex).text)+";"+frmManage.iroleid.value;       	
		    values=returns[0]+","+trim(v1)+","+trim(v2)+","+v3+","+trim(v4)+","+trim(v5)+","+trim(v6);
		    onNewRow(values);
		    alert("新建用户成功!");
	    }
	    else
	    {
	    	alert(returns[1]);
	    }
	}
	function SaveUser(datakey,parames)
	{
		if(parames!="")
		{
			var url="../servlet.jsp?action=usermanage&method=edit&parames="+parames+"&datakey="+datakey;
			callbackresult=send_request(url,onCallBackForSaveUser);
		}
	}
	function onCallBackForSaveUser(callbacktext)
	{	
		var result = trim(callbacktext.responseText);
		var rtn=trim(result);
		var returns=rtn.split(",");
		if(returns[0]!="")
		{
			var v1=frmManage.vccode.value;    		
        	var v2=frmManage.vcpassword.value;
        	var v3=frmManage.vccname.value;
	        var v4=frmManage.vcename.value;	        
	       	var v5=trim(frmManage.iflag.options(frmManage.iflag.selectedIndex).text)+";"+frmManage.iflag.value;
	       	var v6=trim(frmManage.iroleid.options(frmManage.iroleid.selectedIndex).text)+";"+frmManage.iroleid.value;       	
		    values=returns[0]+","+trim(v1)+","+trim(v2)+","+v3+","+trim(v4)+","+trim(v5)+","+trim(v6);
		    onSaveRow(returns[0],values);
		    alert("保存成功!");
	    }
	    else
	    {
	    	alert(returns[1]);
	    }
	}
    </script>
  </head>  
  <body style="margin:0px" onload="initpage();" scroll="no">
    <table width="100%" border="0" cellSpacing="0" cellPadding="0">
    <tr>
    <td valign="top" id="gridtd">
     <form name="frmShow" id="frmShow" method="POST" target="_self">
     <P class="r_l_3"></P>
	 <P class="r_l_2"></P>
	 <P class="r_l_1"></P>
     <DIV class="w_l">
	 <H4>用户列表</H4>
	 <DIV class="body" align="right">
	 <input type="button" value="新建" onclick="setNewUser();"/>
	 <input type="button" value="删除" onclick="DeleteUser();"/>
	 <grid:dbgrid id="tblStat" name="tblStat" width="700" pageSize="<%=pagesize%>" pageObject="<%=pageContext%>" pageRequest="<%=request%>"
		border="0" cellSpacing="1" cellPadding="2" dataMember="" dataSource="<%=ls%>" totalRecords="<%=0%>"
		cssClass="gridTable" lightOn="true" tdIntervalColor="true" verticalHeight="375" gridPosition="absolute" 
		dataWidth="700" viewDetail="<%=viewdetail%>" viewWidth="860" viewHeight="600">           
            <grid:gridpager imgFirst="../images/datagrid/First.gif" imgPrevious="../images/datagrid/Previous.gif" imgNext="../images/datagrid/Next.gif" 
            			   imgLast="../images/datagrid/Last.gif" imgBackground="../images/datagrid/di-bt.gif"/>           
            <grid:rownumcolumn dataField="id" headerText="序号" width="8" HAlign="center" sortable="true"/>           
            <grid:checkboxcolumn dataField="id" headerText="&nbsp;" HAlign="center" sortable="true" width="8"/>
            <grid:textcolumn dataField="vccode" headerText="用户账号" HAlign="center" sortable="true" width="12"/>
            <grid:textcolumn dataField="vcpassword" headerText="用户密码" HAlign="center" sortable="true" width="15" tagField="vcpassword"/>
            <grid:textcolumn dataField="vccname" headerText="中文名称" HAlign="center" sortable="true" width="10"/>
            <grid:textcolumn dataField="vcename" headerText="英文名称" HAlign="center" sortable="true" />
            <grid:textcolumn dataField="flagname" headerText="用户类型" HAlign="center" sortable="true" width="10" tagField="iflag"/>            
      </grid:dbgrid>
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
	<H4>用户管理</H4>
	<DIV class="body">
	<form name="frmManage" id="frmManage" method="POST" target="_self"> 
	<table border=0 width="400" cellSpacing="5" cellPadding="0">
	<tr>
	<td>
	用户账号*:
	</td>
	<td>
	<input type="text" name="vccode" tag="true,char,30,用户账号"/>
	</td>
	</tr>
	<tr>
	<td>
	用户密码*:
	</td>
	<td>
		<input type="password" name="vcpassword" tag="true,char,30,用户密码"/>
	</td>
	</tr>
	<tr>
	<td>
	中文名称*:
	</td>
	<td>
	<input type="text" name="vccname" tag="true,char,30,中文名称"/>
	</td>
	</tr>
	<tr>
	<td>
	英文名称*:
	</td>
	<td>
	<input type="text" name="vcename" tag="true,char,30,英文名称"/>
	</td>
	</tr>
	<tr>
	<td>
	用户类型:
	</td>
	<td>
		<select name="iflag">
		<option value=1>江铃进出口使用人员</option>
		<option value=2>经销商</option>
		<option value=3>管理员</option>
		</select>
	</td>
	</tr>
	<tr style="display:none">
	<td>
	所属角色:
	</td>
	<td>
		<select name="iroleid">		
		</select>
	</td>
	</tr>	
	<tr>
	<td align="center" colspan="2">
	    <input type="button" value="保存" onclick="save();"/>
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
