<%@ page contentType="text/html;charset=utf-8" pageEncoding="utf-8"%>
<%@ page import="java.util.*"%>
<%@ page import="com.jl.action.*"%>
<%@include file="../main/checksession.jsp"%>
<%
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
String title=smpartvccode+" - "+vcename;
for(int i=0;i<9999;i++)
{
	title=title+"&nbsp;";
}
%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
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
<style type="text/css">
.div1{
    /*非IE内核*/
    display:table-cell;
    vertical-align:middle;
    /*IEneihe*/
    *display:block;
    *font-size:698px;/*高度为200PX，  则800*0.873约为175px*/    
    text-align:center;
    width:770px;
    height:500px;
    }
    .img1{ vertical-align:middle;/*之前的是图片上下左右居中。后面的是处理等比例缩放的*/ max-width:800px; width:expression(this.width > 770 ? "770px" : this.width); max-height:500px; height:expression(this.height > 500 ? "500px" : this.height);}
</style>
  
</style>
<script type="text/javascript">
var photos="";
var photoimagesrcarray=null;
var currentindex=1;
var total=0;
var selectfootelement=null;
function openimage()
{
	document.all.firstimagefoot.style.color="black";
	if(selectfootelement!=null)
	{
		selectfootelement.style.color="black";
	}
	var imageindex=event.srcElement.innerText;
	document.all.imagepart.src=photoimagesrcarray[imageindex-1];
	event.srcElement.style.color="blue";
	selectfootelement=event.srcElement;
	
}
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
		var imageurl="";
		for(var i=1;i<=total;i++)
		{			
			if(i==1)
				imageurl=imageurl+"<span id='firstimagefoot' onclick='openimage()' style='cursor:pointer;font-size:11pt;color:blue'>"+i+"</span>"+"&nbsp;&nbsp;";
			else
				imageurl=imageurl+"<span onclick='openimage()' style='cursor:pointer;font-size:11pt'>"+i+"</span>"+"&nbsp;&nbsp;";			
		}
		document.all.imagefoot.innerHTML=imageurl+"&nbsp;";
		//document.all.imagefoot.innerHTML="CurrentImage:"+currentindex+"/Total:"+total;
	 }
	 else
	 {
		 document.all.imagefoot.innerHTML="CurrentImage:0/Total:0";
	 }
	 document.all.imagepart.style.className="img1";
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
<table width="100%" border=0 cellSpacing="0" cellPadding="0" border="0">
<tr>
<td align="center" valign="middle" style="padding-top:20px">
<div class="div1">
    <img src="../images/empty.png" id="imagepart"/>
</div>  
</td>
</tr>
<tr>
<td align="center" id="imagefoot">
&nbsp;
</td>
</tr>
</table>  
</BODY>  
</HTML> 