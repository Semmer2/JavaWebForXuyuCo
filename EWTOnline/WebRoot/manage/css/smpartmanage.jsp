<%@ page contentType="text/html;charset=utf-8" pageEncoding="utf-8"%>
<%@ page import="java.util.*"%>
<%@ taglib uri="/WEB-INF/tld/datagrid.tld" prefix="grid"%>
<%@ page import="com.jl.action.*"%>
<%
response.setHeader("Cache-Control", "no-cache");
response.setHeader("Pragma", "no-cache");
response.setDateHeader("Expires", 0);
String viewdetail="";
int pagesize=20;//每页记录数量,可修改
int width=100;//表格宽度 ,可修改
int DataWidth=100;//数据宽度,可修改
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
  	<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
  	<meta http-equiv="pragma" content="no-cache" />  	
    <title>零件管理</title>
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
    }
    function setNewPart()
    {    	
    	frmManage.partid.value="";
        frmManage.vccode.value="";
        frmManage.vccname.value="";
        frmManage.vcename.value="";
        frmManage.icatalog.value="0";
        frmManage.isaleflag.value="0";
        frmManage.vccnote.value="";
        frmManage.vcenote.value="";
        frmManage.vclength.value="";
        frmManage.vcwidth.value="";
        frmManage.vcheight.value="";       
        frmManage.vcweight.value="";
        frmManage.vcposition.value="";
        frmManage.changepartid.value="";
        frmManage.vcmemo.value="";
        frmManage.changeflag.checked=false;
        document.all.changetr1.style.display="none"; 
        document.all.changetr2.style.display="none";
    	mode="new";
    	document.all.savepartbtn.disabled=false;
    	var photourl="照片:";
   		photostd.innerHTML=photourl;
        document.all.prevphotobtn.disabled=true;
        document.all.nextphotobtn.disabled=true;
        photocount=0;
	    photodata=null;//存放图片url
	    currentphotoindex=-1;
	    photoid="";
	    document.all.addphotobtn.disabled=true;
        document.all.delphotobtn.disabled=true;
        document.all.updatephotobtn.disabled=true;
        document.all.photoindex.innerText="";
        document.all.imagecontent.src="../images/empty.png";
        
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
		if(frmManage.changeflag.checked)
			jsonStr=jsonStr+getfieldjsonstrbyvalue("changeflag","true");
		else
			jsonStr=jsonStr+getfieldjsonstrbyvalue("changeflag","false");	
		jsonStr=jsonStr+"})";
		return eval(jsonStr);
    }
    function savesmpart()
    {
    	if(verifyAll(frmManage))
		{			
			if(mode=="new")
			{				
				var url="do.json?action=smpartmanage&method=new";
				$.ajax({
				type : "GET",//使用post方法访问后台  
				dataType : "json",//返回json格式的数据  
				url : url,//要访问的后台地址
				cache: false,//默认: true,设置为 false 将不会从浏览器缓存中加载请求信息。  
				contentType : "application/json;charset=utf-8",				 
				data:getdata(),
				beforeSend:onBeforeAddPart,
				complete:onComplete,
				success:onAfterAddPart,
				error:onError 
				});
			}
			if(mode=="edit")
			{				
				var url="do.json?action=smpartmanage&method=edit";
				if(trim(frmManage.changepartid.value)!="" && frmManage.changeflag.checked==false)
				{
					alert("本次修改将会替换零件件号,操作将会记录日志,请选中[修改]项确定操作!");
				}
				else
				{
					if(trim(frmManage.changepartid.value)=="" && frmManage.changeflag.checked)
						alert("请输入替代件");
					else
					{
						$.ajax({
						type : "GET",//使用post方法访问后台  
						dataType : "json",//返回json格式的数据  
						url : url,//要访问的后台地址  
						contentType : "application/json;charset=utf-8",				 
						data:getdata(),
						cache: false,//默认: true,设置为 false 将不会从浏览器缓存中加载请求信息。  
						beforeSend:onBeforeAddPart,
						complete:onComplete,
						success:onAfterEditPart,
						error:onError 
						});
					}
				}
			}
		}
    }
    function onComplete()
    {
    	
    }
    function onBeforeAddPart()
    {
    	
    }
    function onError(XMLResponse)
    {
    	alert(XMLResponse.responseText);
    } 
    function onAfterAddPart(data)
	{	
		var result=trim(data.result);
		var rtn=trim(result);
		if(rtn!="" && rtn!="-1")
		{
			alert("新增成功!")
			mode="edit";
			$('#changetr1').css("display","block");
			$('#changetr2').css("display","block");
			ismanagephoto=true;
			partid=rtn;
			document.all.addphotobtn.disabled=false;
			
		}
		else
		{
			alert("新增失败("+getvalue(data.error)+")!")
		}
	}
	function onAfterEditPart(data)
	{	
		var result=trim(data.result);
		var rtn=trim(result);
		if(rtn!="" && rtn!="-1")
		{
			alert("修改成功!")
			mode="edit";
			document.all.changetr1.style.display="block"; 
        	document.all.changetr2.style.display="block";
        	partid=rtn;
		}
		else
		{
			alert("修改失败("+getvalue(data.error)+")!")
		}		
	}
    function querysmpart()
    {
    	var url="do.json?action=smpartmanage&method=query";
		$.ajax({
		type : "GET",//使用post方法访问后台  
		dataType : "json",//返回json格式的数据  
		url : url,//要访问的后台地址 
		cache: false,//默认: true,设置为 false 将不会从浏览器缓存中加载请求信息。  
		contentType : "application/json;charset=utf-8",				 
		data:{querypartid:$('#querypartid').val()},
		beforeSend:onBeforeQuery,
		complete:onComplete,
		success:onAfterQuery,
		error:onError 
		});
    }
    function onBeforeQuery()
    {
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
	        frmManage.changeflag.checked=false;
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
	        
	        document.all.savepartbtn.disabled=false;
	        document.all.addphotobtn.disabled=false;
	        if(photocount>0)
	        {
	        	document.all.delphotobtn.disabled=false;
	        	document.all.updatephotobtn.disabled=false;
	        	if(photocount>1)
	        	{
	        		document.all.prevphotobtn.disabled=false;
	        		document.all.nextphotobtn.disabled=false;
	        	}
	        	getPhotos();
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
		beforeSend:onBeforeQuery,
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
		beforeSend:onBeforeQuery,
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
				document.all.delphotobtn.disabled=false;
		        document.all.updatephotobtn.disabled=false;
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
		        document.all.delphotobtn.disabled=true;
		        document.all.updatephotobtn.disabled=true;
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
	function onaddpartphoto()
	{
		if(ismanagephoto)
		{
			window.showModalDialog("uploadphoto.jsp?partid="+partid+"&method=new",window,"center:yes;dialogWidth:400px;dialogHeight:100px");
		}
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
	function deletephoto()
    {
    	var url="do.json?action=partphotomanage&method=del";
		$.ajax({
		type : "GET",//使用post方法访问后台  
		dataType : "json",//返回json格式的数据  
		url : url,//要访问的后台地址
		cache: false,//默认: true,设置为 false 将不会从浏览器缓存中加载请求信息。  
		contentType : "application/json;charset=utf-8",				 
		data:{photoid:photoid,partid:partid},
		beforeSend:onBeforeQuery,
		complete:onComplete,
		success:onAfterDeletePhoto,
		error:onError 
		});
    }
	function onAfterDeletePhoto(data)
	{
		alert("删除零件图片成功!");
		onAfterAfterPhotoChange(data);
		
	}
	function ondeletepartphoto()
	{
		if(ismanagephoto)
		{
			deletephoto();
		}
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
	     <P class="r_l_3"></P>
		 <P class="r_l_2"></P>
		 <P class="r_l_1"></P>
	     <DIV class="w_l">		    
		 <H4>
			<table width="98%" cellSpacing="0" cellPadding="0" border=0>
				<tr>
					<td width="50%">
						件号:
						<input type="text" name="querypartid" id="querypartid" size="width:100px"/>
						<input type="button" value="GO" onclick="querysmpart();"/>
					</td>
					<td align="right">
						<input type="button" value="新建" onclick="setNewPart();" />
						&nbsp;
					</td>
				</tr>
			</table>
		 </H4>		
		 <DIV class="body">
				<table width="100%" cellSpacing="0" cellPadding="0" border="0">
					<tr>
						<td valign="top" id="gridtd">
						 <form name="frmManage" id="frmManage" method="POST" target="_self">
							<table width="100%" cellSpacing="10" cellPadding="0">
								<tr>
									<td>
										件号*:
									</td>
									<td>
										<input type="text" name="partid" id="partid" tag="true,char,30,零件件号" style="width:300px"/>
									</td>
								</tr>
								<tr>
									<td>
										编号*:
									</td>
									<td>
										<input type="text" name="vccode" tag="true,char,30,零件编号" style="width:300px"/>
									</td>
								</tr>
								<tr>
									<td>
										中文名称*:
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
										中文备注*:
									</td>
									<td>
										<textarea name="vccnote" cols="30" rows="2" tag="true,char,30,中文备注" style="width:300px"></textarea>
									</td>
								</tr>
								<tr>
									<td>
										英文备注*:
									</td>
									<td>
										<textarea name="vcenote" cols="30" rows="2" tag="true,char,30,英文备注" style="width:300px"></textarea>
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
										<input type="checkbox" name="changeflag" />修改
									</td>
								</tr>
								<tr>
									<td colspan="2" align="right">
										<input type="button" value="保存" onclick="savesmpart();" id="savepartbtn" disabled>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
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
						<input type="button" value="新增" onclick="onaddpartphoto();" id="addphotobtn" disabled/>
						<input type="button" value="删除" onclick="ondeletepartphoto();" id="delphotobtn" disabled/>
						<input type="button" value="更换" onclick="onchangepartphoto();" id="updatephotobtn" disabled/>						
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
