<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<%@ page import="com.jl.action.*"%>
<%@ page import="com.jl.entity.*"%>
<%@include file="checksession.jsp"%>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://"
			+ request.getServerName() + ":" + request.getServerPort()
			+ path + "/";
	UserBean userbean = null;
	String userid="";
	String userflag="";
	Object bean = request.getSession().getAttribute("userbean");
	String sessionuserflag=request.getSession().getAttribute("userflag").toString();
	if (bean != null) {
		userbean = (UserBean) bean;
		userflag=userbean.getIflag();
		if(userflag.equalsIgnoreCase("2")) userid=userbean.getId();
	}
	String vcroleno=request.getSession().getAttribute("vcroleno").toString();
	if(vcroleno==null) vcroleno="";
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
		<meta http-equiv="X-UA-Compatible" content="IE=EmulateIE7" />
		<title>EWT Search</title>
		<script type="text/javascript" src="../css/lib/jquery-1.2.3.pack.js"></script>
		<script type="text/javascript" src="../css/lib/jquery.jcarousel.js"></script>
		<link rel="stylesheet" type="text/css"	href="../css/lib/jquery.jcarousel.css" />
		<script Charset="GBK" type='text/javascript' src='../scripts/common.js'></script>
		<script Charset="UTF-8" type='text/javascript'	src='../scripts/ajaxUtil.js'></script>
		<link href="../css/index.css" rel="stylesheet" type="text/css" />
		<link href="../css/index_table.css" rel="stylesheet" type="text/css" />
		<script type="text/javascript">
		var currentpartvccode="";
		var currentinitdatatype="";
		var w=screen.availWidth;
		var h=screen.availHeight;
	  	var sFeatures = "fullscreen=0,toolbar=0,location=0,directories=0,status=1,menubar=0";
	    sFeatures += ",scrollbars=0,resizable=1,top=0,left=0,width=" + w + ",height=" + h + " ";
function clearoptions(objectid)
{
	var selectobject=document.getElementById(objectid);
	while(selectobject.options.length>0)
	{
		selectobject.options.remove(0);
	}
}
function initpage() {
   var avaiheight = window.screen.height-window.screenTop;
   document.all.partframe.height=avaiheight-25-25-33;
   document.all.vehframe.height=avaiheight-25-25-33;
   window.open("partsearch.jsp?pn=-9999999999","partframe");
   clearoptions("Year");
   clearoptions("Model");
   clearoptions("Sys");
   clearoptions("SubSys");
   clearoptions("Series");
   initcondition();
}
function onBeforeLoad() {

}
function onComplete() {

}
function onError(XMLResponse) {
	alert(XMLResponse.responseText);
}
function querybypartvccode(partvccode)
{
	currentpartvccode=partvccode;
	var url = "vehiclelistsearch.jsp?partvccode=" + partvccode;	
	window.open(url, "vehframe");
}
function queryvehicle(){	
	var yearvalue = document.all.Year.value;
	var sysvalue = document.all.Sys.value;
	var subsysvalue = document.all.SubSys.value;
	var seriesvalue = document.all.Series.value;
	var modelvalue = document.all.Model.value;	
	var url = "vehiclelistsearch.jsp?partvccode=" + currentpartvccode+"&year="+yearvalue+"&sys="+sysvalue+"&subsys="+subsysvalue+"&series="+seriesvalue+"&model="+modelvalue;
	window.open(url, "vehframe");
}

function openset() {
	window.open("systemmanage.jsp", "_blank",sFeatures);	
}
function openbulletin() {
	//window.showModelessDialog("../manage/notifyview.jsp", window,"center:yes;dialogWidth:1024px;dialogHeight:800px");
	var s = "fullscreen=0,toolbar=0,location=0,directories=0,status=1,menubar=0";
	s += ",scrollbars=0,resizable=1,top=0,left=0,width=" + w + ",height=" + h + " ";
	window.open("bulletinread.jsp","_blank",s);
}
function AdvanceSearch() {
	var windowhandle=window.open("search.jsp", "_blank",sFeatures);
	windowhandle.focus();
}
var fillconditiontype = "";
var fillcomboobject = null;
function initcondition() {
	fillcomboobject = document.all.Year;
}
function fillcondition0(){
	fillcomboobject = event.srcElement;
	if (fillcomboobject.options.length == 0) { 
	fillconditiontype="year";
	var url="do.json?action=advancequery&method=fillcondition&partvccode="+currentpartvccode+"&type="+fillconditiontype;
	send__request(url, onAfterFillCondition0,false);
	}
}
function fillcondition1(){
	fillcomboobject = event.srcElement;
	if (fillcomboobject.options.length == 0) { 
	fillconditiontype="series";
	var url="do.json?action=advancequery&method=fillcondition&partvccode="+currentpartvccode+"&type="+fillconditiontype;
	send__request(url, onAfterFillCondition1,false);
	}
}
function fillcondition2(){
	fillcomboobject = event.srcElement;
	if (fillcomboobject.options.length == 0) { 
	fillconditiontype="model";
	var url="do.json?action=advancequery&method=fillcondition&partvccode="+currentpartvccode+"&type="+fillconditiontype;
	send__request(url, onAfterFillCondition2,false);
	}
}
function fillcondition3(){
	fillcomboobject = event.srcElement;
	if (fillcomboobject.options.length == 0) { 
	fillconditiontype="sys";
	var url="do.json?action=advancequery&method=fillcondition&partvccode="+currentpartvccode+"&type="+fillconditiontype;
	send__request(url, onAfterFillCondition3,false);
	}
}
function fillcondition4(){	
    fillcomboobject = event.srcElement;
	if (fillcomboobject.options.length == 0) { 
	fillconditiontype="subsys";
	var sysvalue=document.all.Sys.value
	var url="do.json?action=advancequery&method=fillcondition&partvccode="+currentpartvccode+"&type="+fillconditiontype+"&sys="+sysvalue;
	send__request(url, onAfterFillCondition4,false);
	}
}
function onAfterFillCondition0(callbacktext) {
	var result = "(" + trim(callbacktext.responseText) + ")";
	var data = eval(trim(result));
	for (var i = 0; i < data.length; i++) {
		var code = data[i].code;
		var name = data[i].name;
		if(fillcomboobject.options.namedItem(code)==null)
		{
			var optionelement = document.createElement("OPTION");
			optionelement.value = code;
			optionelement.text = name;		
			fillcomboobject.options.add(optionelement);
		}	
	}	
}
function onAfterFillCondition1(callbacktext) {
	var result = "(" + trim(callbacktext.responseText) + ")";
	var data = eval(trim(result));
	for (var i = 0; i < data.length; i++) {
		var code = data[i].code;
		var name = data[i].name;
		if(fillcomboobject.options.namedItem(code)==null)
		{
			var optionelement = document.createElement("OPTION");
			optionelement.value = code;
			optionelement.text = name;				
			fillcomboobject.options.add(optionelement);
		}		
	}	
}
function onAfterFillCondition2(callbacktext) {
	var result = "(" + trim(callbacktext.responseText) + ")";
	var data = eval(trim(result));
	for (var i = 0; i < data.length; i++) {
		var code = data[i].code;
		var name = data[i].name;
		if(fillcomboobject.options.namedItem(code)==null)
		{
			var optionelement = document.createElement("OPTION");
			optionelement.value = code;
			optionelement.text = name;
			fillcomboobject.options.add(optionelement);
		}	
	}	
}
function onAfterFillCondition3(callbacktext) {
	var result = "(" + trim(callbacktext.responseText) + ")";
	var data = eval(trim(result));
	for (var i = 0; i < data.length; i++) {
		var code = data[i].code;
		var name = data[i].name;
		if(fillcomboobject.options.namedItem(code)==null)
		{
			var optionelement = document.createElement("OPTION");
			optionelement.value = code;
			optionelement.text = name;
			fillcomboobject.options.add(optionelement);	
		}		
	}	
}
function onAfterFillCondition4(callbacktext) {
	var result = "(" + trim(callbacktext.responseText) + ")";
	var data = eval(trim(result));
	for (var i = 0; i < data.length; i++) {
		var code = data[i].code;
		var name = data[i].name;
		if(fillcomboobject.options.namedItem(code)==null)
		{
			var optionelement = document.createElement("OPTION");
			optionelement.value = code;
			optionelement.text = name;		
			fillcomboobject.options.add(optionelement);	
		}	
	}	
}
function gohome()
{
	var windowhandle=window.open("seriesvehicle.jsp","_blank",sFeatures);
	windowhandle.focus();
}
function changepassword()
{    	
   	var surl="../manage/changepassworddia.jsp";
   		var parames="dialogHeight:130px;dialogWidth:360px;center:yes";
   	window.showModelessDialog(surl,window,parames);
}
function startquery()
{
   var pnvalue=trim(document.all.pn.value);
   var pdvalue=trim(document.all.pd.value);
   var vinvalue=trim(document.all.vin.value);
   var sapvalue=trim(document.all.sap.value);
   var userflag="<%=userflag%>";
   var yearvalue = document.all.Year.value;
   var sysvalue = document.all.Sys.value;
   var subsysvalue = document.all.SubSys.value;
   var seriesvalue = document.all.Series.value;
   var modelvalue = document.all.Model.value;	
   
   if(pnvalue=="" && pdvalue=="" && vinvalue=="" && sapvalue=="" && userflag!="2")
   {
	   alert("Sorry,Please Enter Conditions!");   
   }
   else
   {
	   document.all.partframetd.style.backgroundColor="#ffffff"; 
	   document.all.partframe.style.display="none";	   
	   document.all.waittable.style.display="block";	   
	   window.open("partsearch.jsp?pn="+pnvalue+"&pd="+pdvalue+"&vin="+vinvalue+"&sap="+sapvalue+"&year="+yearvalue+"&sys="+sysvalue+"&subsys="+subsysvalue+"&series="+seriesvalue+"&model="+modelvalue,"partframe");
	   querybypartvccode("-9999");
   }
}
function clearquery()
{
	document.all.pn.value="";
	document.all.pd.value="";
	document.all.vin.value="";
	document.all.sap.value="";
	
}
function changesys()
{
	clearoptions("SubSys");
}
</script>
	</head>
	<body style="margin: 0px" onload="initpage();" scroll="no">
		<table width="100%" border="0" cellspacing="0" cellpadding="0" align="center" style="padding: 0px">
			<tr>
				<td colspan="7" style="padding: 0px">
					<table width="100%" border="0" cellspacing="0" cellpadding="0" style="padding: 0px" style="border-bottom:2px groove #379be9">
						<tr>
							<td height="36px" width="30" valign="middle">
								<img src="../images/blue.png" width="18" height="18"/>
							</td>
							<td height="36px" valign="middle" style="font-size: 10pt">
								Search All Models
							</td>
							<td class="bar" style="color: #000000;font-size:13px;font-weight:bold" style="padding: 0px">
								&nbsp;
							</td>
							<td class="bar" width="20px" style="padding: 0px">
								&nbsp;
							</td>
						</tr>
					</table>
				</td>
			</tr>
			<tr>
				<td style="padding: 0px;" width="340">
					<table class="nobordtable" border="0" cellspacing="0" cellpadding="0" style="padding:0px">
						<tr>
							<td class="whitetd" style="padding-left: 5px">
								P/N
							</td>						
							<td  style="padding-left: 5px">
								<input type="text" name="pn" id="pn" style="width:120px"/>
							</td>
							<td class="whitetd" style="padding-left: 5px">
								P/D
							</td>						
							<td  style="padding-left: 5px">
								<input type="text" name="pd" id="pd" style="width:120px"/>
							</td>
							<td  style="padding-left:20px;padding-right:5px">
								<input type="button"  name="go" id="go" value="Go" style="cursor:hand;width:60px;text-align:center" onclick="startquery();"/>
							</td>
						</tr>
						<tr>
							<td class="whitetd" style="padding-left: 5px">
								VIN
							</td>						
							<td  style="padding-left: 5px">
								<input type="text" name="vin" id="vin" style="width:120px"/>
							</td>
							<td class="whitetd" style="padding-left: 5px">
								SAP
							</td>						
							<td  style="padding-left: 5px">
								<input type="text" name="sap" id="sap" style="width:120px"/>
							</td>
							<td  style="padding-left:20px;padding-right:5px">
								<input type="button" name="cancel" id="cancel" value="Clear" style="cursor:hand;width:60px" onclick="clearquery();"/>
							</td>
						</tr>						
					</table>
				</td>
				<td  width="150px" style="padding-left:5px">
					<table class="nobordtable" border="0" cellspacing="0" cellpadding="0" >
						<tr>
							<td class="whitetd">
								Year
							</td>
						</tr>
						<tr>
							<td  style="padding: 0px">
								<Select name="Year" id="Year" style="width: 130px;font-size:9pt" onclick="fillcondition0();">
								</Select>
							</td>
						</tr>
					</table>
				</td>
				<td  width="150px">
					<table class="nobordtable" border="0" cellspacing="0"
						cellpadding="0">
						<tr>
							<td class="whitetd">
								Series
							</td>
						</tr>
						<tr>
							<td  style="padding: 0px">
								<Select name="Series" id="Series" style="width: 130px;font-size:9pt"
									onclick="fillcondition1();">
								</Select>
							</td>
						</tr>
					</table>
				</td>
				<td  width="150px">
					<table class="nobordtable" border="0" cellspacing="0"
						cellpadding="0">
						<tr>
							<td class="whitetd">
								Model
							</td>
						</tr>
						<tr>
							<td  style="padding: 0px">
								<Select name="Model" id="Model" style="width: 130px;font-size:9pt"
									onclick="fillcondition2();">
								</Select>
							</td>
						</tr>
					</table>
				</td>
				<td  width="150px">
					<table class="nobordtable" border="0" cellspacing="0"
						cellpadding="0">
						<tr>
							<td class="whitetd">
								Sys
							</td>
						</tr>
						<tr>
							<td  style="padding: 0px">
								<Select name="Sys" id="Sys" style="width: 130px;font-size:9pt" onchange="changesys();"
									onclick="fillcondition3();">
								</Select>
							</td>
						</tr>
					</table>
				</td>
				<td  width="150px">
					<table class="nobordtable" border="0" cellspacing="0"
						cellpadding="0">
						<tr>
							<td class="whitetd">
								SubSys
							</td>
						</tr>
						<tr>
							<td  style="padding: 0px">
								<Select name="SubSys" id="SubSys" style="width: 130px;font-size:9pt"
									onclick="fillcondition4();">
								</Select>
							</td>
						</tr>
					</table>
				</td>
				<td>
					&nbsp;
				</td>
			</tr>		
		<tr>
			<td id="partframetd" style="margin: 0px; padding-right:5px;" valign="top" align="left" style="background-color:#ffffff">
				<iframe width="100%" src="" height="100px" name="partframe" id="partframe" frameborder=0 style="display:none"></iframe>
				<table id="waittable"  width="100%" height="600" border="0" cellspacing="0" cellpadding="0" style="display:block">
				<tr>
				<td align="center" valign="middle">
				    <img src ="../images/loading.gif"/>
				</td>
				</tr>
				</table>		
			</td>
			<td id="vehframetd" style="margin: 0px; padding-left:5px" valign="top" align="left" colspan="6" style="background-color:#efefef">
				<iframe width="100%" src="vehiclelistsearch.jsp" name="vehframe" id="vehframe" frameborder=0  height="100px"></iframe>
			</td>
		</tr>		
	</body>
</html>