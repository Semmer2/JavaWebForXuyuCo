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
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
  	<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
  	<meta http-equiv="pragma" content="no-cache" /> 
  	<META content=IE=EmulateIE7 http-equiv=X-UA-Compatible> 	
    <title>ViewPart</title>
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
		jsonStr=jsonStr+getfieldjsonstr("partid"); 
		jsonStr=jsonStr+",";
		jsonStr=jsonStr+getfieldjsonstr("vccode");
		jsonStr=jsonStr+",";
		jsonStr=jsonStr+getfieldjsonstr("vccname");
		jsonStr=jsonStr+",";
		jsonStr=jsonStr+getfieldjsonstr("vcename");
		jsonStr=jsonStr+",";
		jsonStr=jsonStr+getfieldjsonstr("icatalog");
		jsonStr=jsonStr+",";
		jsonStr=jsonStr+getfieldjsonstr("isaleflag");
		jsonStr=jsonStr+",";
		jsonStr=jsonStr+getfieldjsonstr("vccnote");
		jsonStr=jsonStr+",";
		jsonStr=jsonStr+getfieldjsonstr("vcenote");
		jsonStr=jsonStr+",";
		jsonStr=jsonStr+getfieldjsonstr("vclength");
		jsonStr=jsonStr+",";
		jsonStr=jsonStr+getfieldjsonstr("vcwidth");
		jsonStr=jsonStr+",";
		jsonStr=jsonStr+getfieldjsonstr("vcheight");
		jsonStr=jsonStr+",";
		jsonStr=jsonStr+getfieldjsonstr("vcweight");
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
			frmManage.partid.value=getvalue(data.partid);
	        frmManage.vccode.value=getvalue(data.vccode);
	        frmManage.vccname.value=getvalue(data.vccname);
	        frmManage.vcename.value=getvalue(data.vcename);
	        frmManage.icatalog.value=getvalue(data.icatalog);
	        frmManage.isaleflag.value=getvalue(data.isaleflag);
	        frmManage.vccnote.value=getvalue(data.vccnote);
	        frmManage.vcenote.value=getvalue(data.vcenote);
	        frmManage.vclength.value=getvalue(data.vclength);
	        frmManage.vcwidth.value=getvalue(data.vcwidth);
	        frmManage.vcheight.value=getvalue(data.vcheight);       
	        frmManage.vcweight.value=getvalue(data.vcweight);
	        frmManage.vcposition.value=getvalue(data.vcposition);
			document.all.changetr1.style.display="block"; 
	       	document.all.changetr2.style.display="block";
			frmManage.changepartid.value="";
	        frmManage.vcmemo.value="";
	        ismanagephoto=true;
	        partid=getvalue(data.partid);
	        photocount=data.photocount;
	        var photourl="照片:";
	        for(var i=0;i<photocount;i++)
	        {
	        	photourl=photourl+"<span style='cursor:hand' onclick='viewphoto("+i+")'>"+(i+1)+"</span>";
	        	if(i<photocount-1)
	        	{
	        		photourl=photourl+"&nbsp;"	
	        	}
	        }
	        photostd.innerHTML=photourl;
	        
	       
	        if(photocount>0)
	        {
	        	
	        	if(photocount>1)
	        	{
	        		document.all.prevphotobtn.disabled=false;
	        		document.all.nextphotobtn.disabled=false;
	        	}
	        	getPhotos();
	        }
	        else
	        {
	        	getPartPrice();	
	        }
	        
        }
        else
        {
        	alert("指定的零件不存在!");
        }
		
	}
    function getPhotos()
    {
    	var url="do.json?action=partphotomanage&method=query";
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
    
    function getPhotoAfterPhotoChange()
    {
    	var url="do.json?action=partphotomanage&method=query";
		$.ajax({
		type : "GET",//使用post方法访问后台  
		dataType : "json",//返回json格式的数据 
		cache: false,//默认: true,设置为 false 将不会从浏览器缓存中加载请求信息。  
		url : url,//要访问的后台地址  
		contentType : "application/json;charset=utf-8",				 
		data:{partid:$('#partid').val()},
		beforeSend:onBefore,
		complete:onComplete,
		success:onAfterAfterPhotoChange,
		error:onError 
		});
    }
    function onAfterAfterPhotoChange(data)
    {
    	if(data.result!="0")
		{
	    	if(data.totalcount!="0")
			{
    		 	photodata=data;
				photocount=data.totalcount;
				var photourl="照片:";
		        for(var i=0;i<photocount;i++)
		        {
		        	photourl=photourl+"<span style='cursor:hand' onclick='viewphoto("+i+")'>"+(i+1)+"</span>";
		        	if(i<photocount-1)
		        	{
		        		photourl=photourl+"&nbsp;"	
		        	}
		        }
		        photostd.innerHTML=photourl;
				viewphoto(0);
				document.all.prevphotobtn.disabled=true;
				if(photocount==1)
	        	{	
	        		document.all.nextphotobtn.disabled=true;
	        	}
				if(photocount>1)
	        	{	
	        		document.all.nextphotobtn.disabled=false;
	        	}				
			}
	    	else
	    	{
	    		var photourl="照片:";
	    		photostd.innerHTML=photourl;
		        document.all.prevphotobtn.disabled=true;
		        document.all.nextphotobtn.disabled=true;
		        photocount=0;
			    photodata=null;//存放图片url
			    currentphotoindex=-1;
			    photoid="";		       
		        document.all.photoindex.innerText="";
		        document.all.imagecontent.src="../images/empty.png";
	    	}
	    }
    }
    function viewphoto(photoindex)
    {
    	var s;
    	s="photodata.key"+photoindex;
    	currentphotoindex=photoindex;
    	photoid=getvalue(eval(s));
    	var imageurl="../photo/"+photoid+".jpg";    	
    	document.all.imagecontent.src="";
    	document.all.imagecontent.src=imageurl;
    	if(photocount==1)
    	{
    		document.all.nextphotobtn.disabled=true;
	    	document.all.prevphotobtn.disabled=true;
    	}
    	else
    	{
	    	if(currentphotoindex>0 && currentphotoindex<(photocount-1)) 
	    	{
	    		document.all.nextphotobtn.disabled=false;
	    		document.all.prevphotobtn.disabled=false;
	    	}
	    	else
	    	{
	    		if(currentphotoindex==0)
	    		{
	    			document.all.nextphotobtn.disabled=false;
	    			document.all.prevphotobtn.disabled=true;
	    		}
	    		else
	    		{
	    			document.all.nextphotobtn.disabled=true;
	    			document.all.prevphotobtn.disabled=false;
	    		}
	    	}
    	}
    	document.all.photoindex.innerText="第"+(currentphotoindex+1)+"张";
    }
	
	function onAfterPhotoQuery(data)
	{
		if(data.result!="0")
		{			
			photodata=data;
			viewphoto(0);
			document.all.prevphotobtn.disabled=true;
			if(photocount==1)
        	{	
        		document.all.nextphotobtn.disabled=true;
        	}
			if(photocount>1)
        	{	
        		document.all.nextphotobtn.disabled=false;
        	}
		}
		getPartPrice();
		
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
	function onclickprev()
	{
		if(photocount>0)
		{
			if(photocount>(currentphotoindex-1))
			{
				viewphoto(currentphotoindex-1);		
			}
		}
	}
	function onclicknext()
	{
		if(photocount>0)
		{
			if(photocount>(currentphotoindex+1))
			{
				viewphoto(currentphotoindex+1);		
			}
		}
	}
	function onAfterUploadPhoto(photoid)
	{
		getPhotoAfterPhotoChange();
	}
	
	function onchangepartphoto()
	{
		if(ismanagephoto)
		{
			window.showModalDialog("uploadphoto.jsp?partid="+partid+"&photoid="+photoid+"&method=edit",window,"center:yes;dialogWidth:400px;dialogHeight:100px");
		}
	}
    </script>
  </head>  
  <body style="margin:1px" onload="initpage();" scroll="no">
    <table width="100%" border="0" cellSpacing="0" cellPadding="0">
    <tr>
    <td valign="top">
				<table width="100%" cellSpacing="0" cellPadding="0" border="0">
					<tr>
						<td valign="top" id="gridtd">
						 <form name="frmManage" id="frmManage" method="POST" target="_self">
							<table width="100%" cellSpacing="5" cellPadding="0">
								<tr>
									<td>
										件号
									</td>
									<td>
										<input type="text" name="partid" id="partid" tag="true,char,30,零件件号" style="width:300px"/>
									</td>
								</tr>
								<tr>
									<td>
										编号
									</td>
									<td>
										<input type="text" name="vccode" tag="true,char,30,零件编号" style="width:300px"/>
									</td>
								</tr>
								<tr>
									<td>
										中文名称
									</td>
									<td>
										<input type="text" name="vccname" tag="true,char,30,中文名称" style="width:300px"/>
									</td>
								</tr>
								<tr>
									<td>
										英文名称:
									</td>
									<td>
										<input type="text" name="vcename" tag="true,char,30,英文名称" style="width:300px"/>
									</td>
								</tr>
								<tr>
									<td>
										等级:
									</td>
									<td>
										<select name="icatalog" style="width:300px">
											<option value=0>
												一般件
											</option>
											<option value=1>
												保养件
											</option>
											<option value=2>
												关键件
											</option>
											<option value=3>
												易损件
											</option>
											<option value=4>
												事故件
											</option>
											<option value=5>
												特殊件
											</option>
										</select>
									</td>
								</tr>
								<tr>
									<td>
										可供:
									</td>
									<td>
										<select name="isaleflag" style="width:300px">
											<option value=0>
												不供应
											</option>
											<option value=1>
												供应
											</option>
										</select>
									</td>
								</tr>
								<tr>
									<td>
										中文备注
									</td>
									<td>
										<textarea name="vccnote" cols="30" rows="2" tag="true,char,30,中文备注" style="width:300px"></textarea>
									</td>
								</tr>
								<tr>
									<td>
										英文备注
									</td>
									<td>
										<textarea name="vcenote" cols="30" rows="2" tag="true,char,30,英文备注" style="width:300px"></textarea>
									</td>
								</tr>
								<tr>
									<td>
										价格:
									</td>
									<td>
										<select name="priceuserid" id="priceuserid" style="width:200px" disabled>
											<option value=0>
												公共
											</option>
										</select>
										<input type="text" name="price" tag="false,number,30,价格" style="width:90px"/>
									</td>
								</tr>
								<tr>
									<td>
										长度:
									</td>
									<td>
										<input  type="text" name="vclength" style="width:300px"/>
									</td>
								</tr>
								<tr>
									<td>
										宽度:
									</td>
									<td>
										<input  type="text" name="vcwidth" style="width:300px"/>
									</td>
								</tr>
								<tr>
									<td>
										高度:
									</td>
									<td>
										<input  type="text" name="vcheight" style="width:300px"/>
									</td>
								</tr>
								<tr>
									<td>
										重量:
									</td>
									<td>
										<input  type="text" name="vcweight" style="width:300px"/>
									</td>
								</tr>
								<tr>
									<td>
										存储位置:
									</td>
									<td>
										<input  type="text" name="vcposition" style="width:300px"/>
									</td>
								</tr>
								<tr id="changetr1" style="display:none">
									<td>
										替代件:
									</td>
									<td>
										<input type="text" name="changepartid" style="width:300px"/>										
									</td>
								</tr>
								<tr id="changetr2" style="display:none">
									<td>
										替代备注:
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
						<td width="10%">
							&nbsp;
						</td>
						<td width="50%" valign="top">
						<table width="100%" cellSpacing="30" cellPadding="0">
						<tr>
						<td id="photostd">
						照片:
						</td>											
						<td>
						&nbsp;
						</td>
						</tr>
						<tr>
						<td colspan="2">
						<table cellSpacing="0" cellPadding="0" border=0>
						<tr>
						<td align="right" valign="top">				
						&nbsp;				
						</td>
						</tr>
						<tr>
						<td colspan="2" height="10px">
						&nbsp;
						</td>
						</tr>
						<tr>
						<td align="right" valign="top">
						<img src="../images/empty.png" width="400" height="300" id="imagecontent"/>
						<br/>
						<table width="100%" cellSpacing="0" cellPadding="0" border="0">
						<tr>
						<td width="50%" align="left">
						<span id="photoindex">&nbsp;</span>
						<td>
						<td align="right">
						<input type="button" value="上一张" id="prevphotobtn" disabled onclick="onclickprev();"/>
						<input type="button" value="下一张" id="nextphotobtn" disabled onclick="onclicknext();"/>
						</td>
						</tr>
						</table>
						</td>
						</tr>
						<tr>
						<td align="right" valign="top">
						&nbsp;
						</td>
						</tr>						
						</table>
						</td>
						</tr>
						</table>						
						</td>
					</tr>
				</table>
    </td>
    </tr>
    </table>    
  </body>
</html>

