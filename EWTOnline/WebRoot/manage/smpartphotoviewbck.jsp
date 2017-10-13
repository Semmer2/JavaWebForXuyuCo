<%@ page contentType="text/html;charset=utf-8" pageEncoding="utf-8"%>
<%@ page import="java.util.*"%>
<%@ taglib uri="/WEB-INF/tld/datagrid.tld" prefix="grid"%>
<%@ page import="com.jl.action.*"%>
<%@include file="../main/checksession.jsp"%>
<%
response.setHeader("Cache-Control", "no-cache");
response.setHeader("Pragma", "no-cache");
response.setDateHeader("Expires", 0);
String userid=request.getSession().getAttribute("currentuserid").toString();
String smpartvccode=request.getParameter("smpartvccode");
PartPhotoAction action=new PartPhotoAction(request);
List photols=action.getSmPartPhotoByPartVccode(smpartvccode);
String vcename="";
if(photols.size()>0)
{
	Hashtable ht=(Hashtable)photols.get(0);
	vcename=ht.get("vcename").toString();	
}
String title=smpartvccode+"-"+vcename;
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<HTML>  
<HEAD>  
<TITLE><%=title%></TITLE>  
<style>  
html,body{   
    background:#ffffff;   
}   
.imgBox img{   
    border:1px dashed #666;   
}   
.imgBox{   
    position: relative;   
    float:left;   
}   
.imgBox div{   
    position:absolute;   
    left:0px;   
    top:0px;   
    width:100%;   
    height:100%;   
    background: #ffffff;   
    opacity:0.0;   
    filter:alpha(opacity=0);   
}   
.imgBox .left{   
    cursor: url('../images/pre.cur'),default;   
}   
.imgBox .right{   
    left:50%;   
    cursor: url('../images/next.cur'),default;   
}   
  
</style>
<script type="text/javascript">
var photos="";
var photoimagesrcarray=null;
var currentindex=1;
var total=0;
function pageload()
{
	<%
	String photos="";
	String total=String.valueOf(photols.size());
	for(int i=0;i<photols.size();i++)
	 {
	 	Hashtable ht=(Hashtable)photols.get(i);
	 	String photoid=ht.get("iphotoid").toString();
	 	String imagesrc="../photo/"+photoid+".jpg";
	 	photos=photos+imagesrc;
	 	if(i<photols.size()-1) 	photos=photos+",";
	 }%> 
	 photos="<%=photos%>";
	 total="<%=total%>";
	 if(photos!="")
	 {
		photoimagesrcarray=photos.split(",");
		document.all.imagepart.src=photoimagesrcarray[0];
		currentindex=1;
		document.all.imagefoot.innerHTML="CurrentImage:"+currentindex+"/Total:"+total;
	 }
	 else
	 {
		 document.all.imagefoot.innerHTML="CurrentImage:0/Total:0";
	 }
	 //alert(currentindex);
	 
	 
}
function nextimage()
{
	if(photoimagesrcarray!=null)
	{
		if((currentindex-1)<photoimagesrcarray.length-1)
		{			
			currentindex=currentindex+1;
			document.all.imagepart.src=photoimagesrcarray[currentindex-1];
			document.all.imagefoot.innerHTML="CurrentImage:"+currentindex+"/Total:"+total;
		}
	}
	//alert(currentindex);
	
}
function preimage()
{
	if(photoimagesrcarray!=null)
	{
		if((currentindex-1)>0)
		{			
			currentindex=currentindex-1;
			document.all.imagepart.src=photoimagesrcarray[currentindex-1];
			document.all.imagefoot.innerHTML="CurrentImage:"+currentindex+"/Total:"+total;
		}
	}
	//alert(currentindex);
	
}
</script>
</HEAD>  
  
<BODY scroll="no" onload="pageload();">  
<table width="100%" height="100%" border=0>
<tr>
<td align="center">
<div class="imgBox">
    <img src="../images/empty.png" width="100%" height="550" id="imagepart"/>  
    <div class="left" onclick="preimage();" title="Prev Image">&nbsp;</div>  
    <div class="right" onclick="nextimage();" title="Next Image">&nbsp;</div>  
</div>
</td>
</tr>
<tr>
<td align="right" id="imagefoot">
111111
</td>
</tr>
</table>  
</BODY>  
</HTML> 