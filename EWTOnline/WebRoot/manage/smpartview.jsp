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
<html>
  <head>
  	<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
  	<meta http-equiv="pragma" content="no-cache" />  	
    <title><%=title%></title>
    <link REL="StyleSheet" HREF="../css/gridstyle.css">
    <link REL="StyleSheet" HREF="../css/show.css">
    <link REL="StyleSheet" HREF="../css/globle.css">
    <link REL="StyleSheet" HREF="css/smpartmanage.css">
    <script Charset="gbk" src="../scripts/datagrid/datagrid.js" type="text/javascript"></script>	
	<script Charset="gbk" type='text/javascript' src='../scripts/common.js'></script>	
	<script Charset="gbk" src="../scripts/ValidInput.js" type='text/javascript'> </script>
	<script type="text/javascript" language="javascript" src="../scripts/jquery/json2.js" ></script>  
    <link rel="stylesheet" type="text/css" href="../css/demoStyleSheet.css" />
	<script type="text/javascript" src="../scripts/jquery.js"></script>
	<script type="text/javascript" src="../scripts/fadeSlideShow.js"></script>
	<script type="text/javascript">
	jQuery(document).ready(function(){
		jQuery('#slideshow').fadeSlideShow();
	});
    var mode="";
    var callbackresult;
    var ismanagephoto=false;//表示是否可以维护图片
    var partid="";
    var photocount=0;
    var photodata=null;//存放图片url
    var currentphotoindex=-1;
    var photoid="";
    function initpage(){
        gridtd.height=document.body.clientHeight-50;
        initpriceuser();
        querysmpart("<%=smpartvccode%>");
    }
    function initpriceuser()
    {
    	var url="do.json?action=smpartmanage&method=initpriceuser";
		$.ajax({
		type : "GET",//使用post方法访问后台  
		dataType : "json",//返回json格式的数据  
		url : url,//要访问的后台地址
		cache: false,//默认: true,设置为 false 将不会从浏览器缓存中加载请求信息。  
		contentType : "application/json;charset=utf-8",				 
		data:getdata(),
		beforeSend:onBefore,
		complete:onComplete,
		success:onAfterInitPriceUser,
		error:onError 
		});
    }
    function onAfterInitPriceUser(data){
    	for(var i = 0;i<data.length;i++)
    	{
			var code = data[i].id;
			var name = data[i].vccode;
			var optionelement = document.createElement("OPTION");
			optionelement.value = code;
			optionelement.text = name;
			document.all.priceuserid.options.add(optionelement);
		}
    	document.all.priceuserid.value="<%=userid%>";
    }
    
    function getdata()
    {
    	var jsonStr="({";		
		jsonStr=jsonStr+getfieldjsonstr("vccode");
		jsonStr=jsonStr+",";		
		jsonStr=jsonStr+getfieldjsonstr("vcename");
		jsonStr=jsonStr+",";
		jsonStr=jsonStr+getfieldjsonstr("vcenote");
		jsonStr=jsonStr+",";
		jsonStr=jsonStr+getfieldjsonstr("vclength");
		jsonStr=jsonStr+",";
		jsonStr=jsonStr+getfieldjsonstr("vcwidth");
		jsonStr=jsonStr+",";
		jsonStr=jsonStr+getfieldjsonstr("vcheight");
		jsonStr=jsonStr+",";		
		jsonStr=jsonStr+getfieldjsonstr("vcposition");
		jsonStr=jsonStr+",";
		jsonStr=jsonStr+getfieldjsonstr("changepartid");
		jsonStr=jsonStr+",";
		jsonStr=jsonStr+getfieldjsonstr("vcmemo");
		jsonStr=jsonStr+",";
		jsonStr=jsonStr+getfieldjsonstr("priceuserid");
		jsonStr=jsonStr+",";
		jsonStr=jsonStr+getfieldjsonstr("price");
		jsonStr=jsonStr+",";
		if(frmManage.changeflag.checked)
			jsonStr=jsonStr+getfieldjsonstrbyvalue("changeflag","true");
		else
			jsonStr=jsonStr+getfieldjsonstrbyvalue("changeflag","false");	
		jsonStr=jsonStr+"})";
		return eval(jsonStr);
    }
    
    function onComplete()
    {
    	
    }
    function onBefore()
    {
    	
    }
    function onError(XMLResponse)
    {
    	alert(XMLResponse.responseText);
    } 
   
	
    function querysmpart(vccode)
    {
    	var url="do.json?action=smpartmanage&method=query";
		$.ajax({
		type : "GET",//使用post方法访问后台  
		dataType : "json",//返回json格式的数据  
		url : url,//要访问的后台地址 
		cache: false,//默认: true,设置为 false 将不会从浏览器缓存中加载请求信息。  
		contentType : "application/json;charset=utf-8",				 
		data:{querypartcode:vccode},
		beforeSend:onBefore,
		complete:onComplete,
		success:onAfterQuery,
		error:onError 
		});
    }
     
    function onAfterQuery(data)
	{
		if(data.result!="0")
		{
			mode="edit";
	        frmManage.vccode.value=getvalue(data.vccode);	        
	        frmManage.vcename.value=getvalue(data.vcename);	       
	        frmManage.vcenote.value=getvalue(data.vcenote);
	        frmManage.vclength.value=getvalue(data.vclength);
	        frmManage.vcwidth.value=getvalue(data.vcwidth);
	        frmManage.vcheight.value=getvalue(data.vcheight);
	        frmManage.vcposition.value=getvalue(data.vcposition);
			document.all.changetr1.style.display="block"; 
	       	document.all.changetr2.style.display="block";
			frmManage.changepartid.value="";
	        frmManage.vcmemo.value="";
	        ismanagephoto=true;
	        partid=getvalue(data.partid);
	        photocount=data.photocount;	      
	        getPartPrice();	        
        }
        else
        {
        	alert("指定的零件不存在!");
        }
		
	}
    function getPhotos()
    {
    	var url="do.json?action=partphotomanage&method=viewimage";
		$.ajax({
		type : "GET",//使用post方法访问后台  
		dataType : "json",//返回json格式的数据  
		url : url,//要访问的后台地址
		cache: false,//默认: true,设置为 false 将不会从浏览器缓存中加载请求信息。  
		contentType : "application/json;charset=utf-8",				 
		data:{partid:$('#partid').val()},
		beforeSend:onBefore,
		complete:onComplete,
		success:onAfterPhotoQuery,
		error:onError 
		});
    }
	function onAfterPhotoQuery(data)
	{
		
	}
	function getPartPrice()
    {
		var priceuseridvalue=document.all.priceuserid.value;
		var url="do.json?action=smpartmanage&method=queryprice&priceuserid="+priceuseridvalue+"&smpartvccode="+frmManage.vccode.value;
		$.ajax({
		type : "GET",//使用post方法访问后台  
		dataType : "json",//返回json格式的数据  
		url : url,//要访问的后台地址
		cache: false,//默认: true,设置为 false 将不会从浏览器缓存中加载请求信息。  
		contentType : "application/json;charset=utf-8",				 
		data:{photoid:photoid,partid:partid},
		beforeSend:onBefore,
		complete:onComplete,
		success:onAfterGetPartPrice,
		error:onError 
		});
    }
	function onAfterGetPartPrice(data)
	{
		frmManage.price.value=data.result;
	}	
    </script>
  </head>  
  <body style="margin:1px" onload="initpage();" scroll="no">
    <table width="100%" border="0" cellSpacing="0" cellPadding="0">
    <tr>
    <td valign="top" width="600px">		
		<div id="slideshowWrapper">
		    <ul id="slideshow">
		    <%for(int i=0;i<photols.size();i++)
		    {
		    	Hashtable ht=(Hashtable)photols.get(i);
		    	String photoid=ht.get("iphotoid").toString();
		    	String imagesrc="../photo/"+photoid+".jpg";
		    	%>
		    	<li><img src="<%=imagesrc%>" width="400" height="300" border="0" alt="" /></li>
		    	<%
		    }%>
		    </ul>
		</div>
		&nbsp;
    </td>
	<td valign="top" id="gridtd" style="padding-top:30px">
		 <form name="frmManage" id="frmManage" method="post" target="_self">
			<table width="100%" cellSpacing="15" cellPadding="0">				
				<tr>
					<td>
						Part Code
					</td>
					<td>
						<input type="text" name="vccode" tag="true,char,30,零件编号" style="width:300px"/>
					</td>
				</tr>				
				<tr>
					<td>
						Part Name:
					</td>
					<td>
						<input type="text" name="vcename" tag="true,char,30,英文名称" style="width:300px"/>
					</td>
				</tr>
				<tr>
					<td>
						Remark
					</td>
					<td>
						<textarea name="vcenote" cols="30" rows="2" tag="true,char,30,英文备注" style="width:300px"></textarea>
					</td>
				</tr>
				<tr>
					<td>
						Price:
					</td>
					<td>
						<select name="priceuserid" id="priceuserid" style="width:200px" disabled>
							<option value=0>
								Common
							</option>
						</select>
						<input type="text" name="price" tag="false,number,30,价格" style="width:90px"/>
					</td>
				</tr>
				<tr>
					<td>
						Length:
					</td>
					<td>
						<input  type="text" name="vclength" style="width:300px"/>
					</td>
				</tr>
				<tr>
					<td>
						Width:
					</td>
					<td>
						<input  type="text" name="vcwidth" style="width:300px"/>
					</td>
				</tr>
				<tr>
					<td>
						Height:
					</td>
					<td>
						<input  type="text" name="vcheight" style="width:300px"/>
					</td>
				</tr>				
				<tr>
					<td>
						Position:
					</td>
					<td>
						<input  type="text" name="vcposition" style="width:300px"/>
					</td>
				</tr>
				<tr id="changetr1" style="display:none">
					<td>
						Replace PartCode:
					</td>
					<td>
						<input type="text" name="changepartid" style="width:300px"/>										
					</td>
				</tr>
				<tr id="changetr2" style="display:none">
					<td>
						Replace Remark:
					</td>
					<td>
						<textarea name="vcmemo" cols="30" rows="2" style="width:300px"></textarea>
						<input type="checkbox" name="changeflag" style="display:none"/>
					</td>
				</tr>
				<tr>
					<td colspan="2" align="right">
						&nbsp;
					</td>
				</tr>
			</table>
			</form>
		</td>						
    </tr>
    </table>    
  </body>
</html>

