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
RoleAction action=new RoleAction(request); 
List ls=action.getRoles();
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
//if(usermodenames.indexOf(",modename5,")==-1)
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
    <title>角色管理</title>
    <link REL="StyleSheet" HREF="../css/gridstyle.css">
    <link REL="StyleSheet" HREF="../css/show.css">
    <link REL="StyleSheet" HREF="../css/globle.css">
    <script Charset="gbk" src="../scripts/datagrid/datagrid.js" type="text/javascript"></script>	
	<script Charset="gbk" type='text/javascript' src='../scripts/common.js'></script>
	<script Charset="gbk" type='text/javascript' src='../scripts/ajaxUtil.js'></script>
    <script language="javascript">
    var mode="new";
    var callbackresult;
    
    function initpage(){
    	var ifm=document.getElementById("tblStat");
		document.getElementById("tC").style.height =document.body.scrollHeight+40; 
    }
    function onLight()
    {        
    	frmManage.vcroleno.value=currentlightvalues[1];
        frmManage.vcrolename.value=currentlightvalues[2];
        frmManage.bactive.value=currentlightvalues[3];        
        mode="edit";
        getrolepriv(currentRowvalue);
    }
    function getrolepriv(roleid)
    {
    	for(var i=1;i<=12;i++)
		{
			var id="modename"+i;			
			document.getElementById(id).checked=false;
		}
    	var url="../servlet.jsp?action=rolemanage&method=getrolepriv&roleid="+roleid;
		callbackresult=send_request(url,onCallBackForGetRolePriv);
    }
	
	function onCallBackForGetRolePriv(callbacktext)
	{	
		var result=trim(callbacktext.responseText);
		var modenames=trim(result);
		if(modenames!="")
		{
			var returns=modenames.split(",");
			for(var i=0;i<returns.length;i++)
			{
				var id=returns[i];
				if(document.getElementById(id)!=null)
				{
				   document.getElementById(id).checked=true;		
				}
			}			
		}
    }
    
    function setNewMode()
    {
    	frmManage.vcroleno.value="";
        frmManage.vcrolename.value="";
        frmManage.bactive.value="0";
    	mode="new";    	
    }
    function save()
    {
    	if(mode=="new")
    	{
    		var values="";
    		var v1=frmManage.vcroleno.value;    		
        	var v2=frmManage.vcrolename.value;
        	var v3=frmManage.bactive.value;
        	if(trim(v1)=="" || trim(v2)=="")
        	{
        		alert("请输入必输信息!");
        	}
        	else
        	{
	        	values=encodeURI(trim(v1))+","+encodeURI(trim(v2))+","+encodeURI(v3);
	    		NewRow(values);
    		}
    	}
    	if(mode=="edit")
    	{
    		var values="";
    		if(currentlightvalues!=null)
    		{
	    		var datakey=currentlightvalues[0];	    		
	    		var v1=frmManage.vcroleno.value;    		
	        	var v2=frmManage.vcrolename.value;
	        	var v3=frmManage.bactive.value;
	        	if(trim(v1)=="" || trim(v2)=="")
	        	{
	        		alert("请输入必输信息!");
	        	}
	        	else
	        	{
	        		values=encodeURI(trim(v1))+","+encodeURI(trim(v2))+","+encodeURI(v3);
	    			SaveRow(datakey,values);
	    		}
    		}
    		else
    		{
    			alert("请选择角色!");
    		}
    	}    	
    }
    function DeleteRow()
    {
    	if(confirm("是否要删除，确定、取消?")=="1")
    	{
    		var ids=getSelectIds();
    		var url="../servlet.jsp?action=rolemanage&method=delete&parames="+ids;
			callbackresult=send_request(url,onCallBackForDeleteRow);
    	}
    	
    }
	function NewRow(parames)
	{
		if(parames!="")
		{
			var url="../servlet.jsp?action=rolemanage&method=new&parames="+parames;
			callbackresult=send_request(url,onCallBackForNewRow);
		}
	}	
	function onCallBackForDeleteRow(callbacktext)
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
	function onCallBackForNewRow(callbacktext)
	{	
		var result=trim(callbacktext.responseText);
		var rtn=trim(result);
		var returns=rtn.split(",");
		if(returns[0]!="")
		{
			var v1=frmManage.vcroleno.value;    		
	       	var v2=frmManage.vcrolename.value;
	       	var v3=trim(frmManage.bactive.options(frmManage.bactive.selectedIndex).text)+";"+frmManage.bactive.value;       	
		    values=returns[0]+","+trim(v1)+","+trim(v2)+","+v3;
		    onNewRow(values);
		    alert("新建角色成功!");
	    }
	    else
	    {
	    	alert(returns[1]);
	    }
	}
	function SaveRow(datakey,parames)
	{
		if(parames!="")
		{
			var url="../servlet.jsp?action=rolemanage&method=edit&parames="+parames+"&datakey="+datakey;
			callbackresult=send_request(url,onCallBackForSaveRow);
		}
	}
	function onCallBackForSaveRow(callbacktext)
	{	
		var result = trim(callbacktext.responseText);
		var rtn=trim(result);
		var returns=rtn.split(",");
		if(returns[0]!="")
		{
			var v1=frmManage.vcroleno.value;    		
	       	var v2=frmManage.vcrolename.value;
	       	var v3=trim(frmManage.bactive.options(frmManage.bactive.selectedIndex).text)+";"+frmManage.bactive.value;       	
		    values=returns[0]+","+trim(v1)+","+trim(v2)+","+v3;
		    onSaveRow(returns[0],values);
		    alert("保存成功!");
	    }
	    else
	    {
	    	alert(returns[1]);
	    }
	}
	function changerolepriv()
	{
		var modename=event.srcElement.id;
		var checked=event.srcElement.checked;
		var roleid=currentRowvalue;
		if(roleid!="-1")
		{		
			var url="../servlet.jsp?action=rolemanage&method=managerolepriv&modename="+modename+"&roleid="+roleid+"&checked="+checked;
			callbackresult=send_request(url,onCallBack);
		}
		else
		{
			alert("请选择某一角色!");	
		}
	}
	function onCallBack(callbacktext)
	{
		
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
	 <H4>角色列表</H4>
	 <DIV class="body" align="right">
	 <input type="button" value="新建" onclick="setNewMode();"/>
	 <input type="button" value="删除" onclick="DeleteRow();"/>
	 <grid:dbgrid id="tblStat" name="tblStat" width="700" pageSize="<%=pagesize%>" pageObject="<%=pageContext%>" pageRequest="<%=request%>"
		border="0" cellSpacing="1" cellPadding="2" dataMember="" dataSource="<%=ls%>" totalRecords="<%=0%>"
		cssClass="gridTable" lightOn="true" tdIntervalColor="true" verticalHeight="375" gridPosition="absolute" 
		dataWidth="700" viewDetail="<%=viewdetail%>" viewWidth="860" viewHeight="600">           
            <grid:gridpager imgFirst="../images/datagrid/First.gif" imgPrevious="../images/datagrid/Previous.gif" imgNext="../images/datagrid/Next.gif" 
            			   imgLast="../images/datagrid/Last.gif" imgBackground="../images/datagrid/di-bt.gif"/>           
            <grid:rownumcolumn dataField="id" headerText="序号" width="8" HAlign="center" sortable="true"/>           
            <grid:checkboxcolumn dataField="id" headerText="&nbsp;" HAlign="center" sortable="true" width="8"/>
            <grid:textcolumn dataField="vcroleno" headerText="角色编号" HAlign="center" sortable="true" width="12"/>
            <grid:textcolumn dataField="vcrolename" headerText="角色名称" HAlign="center" sortable="true"/>
            <grid:textcolumn dataField="bactivename" headerText="状态" HAlign="center" sortable="true" width="10" tagField="bactive"/>
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
	<H4>角色管理</H4>
	<DIV class="body">
	<form name="frmManage" id="frmManage" method="POST" target="_self"> 
	<table border=0 width="400" cellSpacing="5" cellPadding="0">
	<tr>
	<td>
	编号*:
	</td>
	<td>
	<input type="text" name="vcroleno"/>
	</td>
	</tr>
	<tr>
	<td>
	名称*:
	</td>
	<td>
	<input type="text" name="vcrolename"/>
	</td>
	</tr>
	<tr>
	<td>
	状态:
	</td>
	<td>
		<select name="bactive">
		<option value=0>无效</option>
		<option value=1 selected>有效</option>
		</select>
	</td>
	</tr>
	<tr>
	<td align="center" colspan="2">
	    <input type="button" value="保存" onclick="save();"/>
	</td>
	</tr>
	</table>
	</form>
	</DIV>
	</DIV>
	<P class="r_l_3"></P>
	<P class="r_l_2"></P>
	<P class="r_l_1"></P>
	<DIV class="w_l">
	<H4>角色权限</H4>
	<DIV class="body">
	<form name="frmPrivManage" id="frmPrivManage" method="POST" target="_self"> 
	<table border=0 width="400" cellSpacing="5" cellPadding="0">
	<tr>
	<td>
	访问统计
	</td>
	<td>
	<input type="checkbox" id="modename1" name="modename1" onclick="changerolepriv();"/>
	</td>
	</tr>
	<tr>
	<td>
	公吿管理
	</td>
	<td>
	<input type="checkbox" id="modename2" name="modename2" onclick="changerolepriv();"/>
	</td>
	</tr>
	<tr>
	<td>
	用户管理
	</td>
	<td>
	<input type="checkbox" id="modename3" name="modename3" onclick="changerolepriv();"/>
	</td>
	</tr>
	<tr>
	<td>
	零件日志
	</td>
	<td>
	<input type="checkbox" id="modename4" name="modename4" onclick="changerolepriv();"/>
	</td>
	</tr>
	<tr>
	<td>
	角色维护
	</td>
	<td>
	<input type="checkbox" id="modename5" name="modename5" onclick="changerolepriv();"/>
	</td>
	</tr>
	<tr>
	<td>
	零件管理
	</td>
	<td>
	<input type="checkbox" id="modename6" name="modename6" onclick="changerolepriv();"/>
	</td>
	</tr>
	<tr>
	<td>
	零件价格
	</td>
	<td>
	<input type="checkbox" id="modename10" name="modename10" onclick="changerolepriv();"/>
	</td>
	</tr>
	<tr>
	<td>
	总成包管理
	</td>
	<td>
	<input type="checkbox" id="modename7" name="modename7" onclick="changerolepriv();"/>
	</td>
	</tr>
	<tr>
	<td>
	BOM管理
	</td>
	<td>
	<input type="checkbox" id="modename12" name="modename12" onclick="changerolepriv();"/>
	</td>
	</tr>
	<tr>
	<td>
	权限维护
	</td>
	<td>
	<input type="checkbox" id="modename8" name="modename8" onclick="changerolepriv();"/>
	</td>
	</tr>
	<tr>
	<td>
	首页维护
	</td>
	<td>
	<input type="checkbox" id="modename9" name="modename9" onclick="changerolepriv();"/>
	</td>
	</tr>
	<tr>
	<td>
	图册反馈
	</td>
	<td>
	<input type="checkbox" id="modename11" name="modename11" onclick="changerolepriv();"/>
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
