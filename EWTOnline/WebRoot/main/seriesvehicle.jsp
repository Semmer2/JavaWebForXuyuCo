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
	String userid="";
	String userflag="";
	UserBean userbean=null;
	Object bean = request.getSession().getAttribute("userbean");
	String vcroleno=request.getSession().getAttribute("vcroleno").toString();
	String sessionuserflag=request.getSession().getAttribute("userflag").toString();
	if(vcroleno==null) vcroleno="";
	if (bean != null) {
		userbean = (UserBean) bean;
		userflag=userbean.getIflag();
		if (userbean.getIflag().equalsIgnoreCase("2")) userid=userbean.getId();
	}	
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
		<meta http-equiv="X-UA-Compatible" content="IE=EmulateIE7" />
		<meta http-equiv="pragma" content="no-cache"/> 
		<meta http-equiv="Cache-Control" content="no-cache, must-revalidate"/> 
		<meta http-equiv="expires" content="Wed, 26 Feb 1997 08:21:57 GMT"/> 
		<title>EWT Models</title>
		<script type="text/javascript" src="../css/lib/jquery-1.2.3.pack.js"></script>
		<script type="text/javascript" src="../css/lib/jquery.jcarousel.js"></script>
		<link rel="stylesheet" type="text/css"	href="../css/lib/jquery.jcarousel.css" />
		<script Charset="GBK" type='text/javascript' src='../scripts/common.js'></script>
		<script Charset="UTF-8" type='text/javascript'	src='../scripts/ajaxUtil.js'></script>
		<link href="../css/index.css" rel="stylesheet" type="text/css" />
		<link href="../css/index_table.css" rel="stylesheet" type="text/css" />
		<script type="text/javascript">
var mycarousel_itemList = new Array();
var currentSeriesID = "";
var serieschangeflag="true";
var currentcheckobject = null;
var currentmycheckobject = null;
var currenttable = null;
var w=screen.availWidth;
var h=screen.availHeight;
var sFeatures = "fullscreen=0,toolbar=0,location=0,directories=0,status=1,menubar=0";
sFeatures += ",scrollbars=0,resizable=1,top=0,left=0,width=" + w + ",height=" + h + " ";
sFeatures="";

function changecheckbox(itemid) {
	var mycheckbox = document.getElementById("mycheckbox" + itemid);
	var o = document.getElementById("check" + itemid);
	currentmycheckobject = mycheckbox;
	if (o.checked == false) {
		mycheckbox.style.backgroundImage = "url(../images/checkbox/checkoff.gif)";
	} else {
		mycheckbox.style.backgroundImage = "url(../images/checkbox/checkon.gif)";
	}
}
function checkclick() {
	if (typeof (event.srcElement.tag) == "undefined") {
		event.srcElement.checked = false;
	} else {
		if (event.srcElement.tag == false) {
			event.srcElement.checked = false;

		} else {
			event.srcElement.checked = true;
		}
	}
	event.srcElement.tag = event.srcElement.checked;
}
function mycarousel_itemLoadCallback(carousel, state) {
	for ( var i = carousel.first; i <= carousel.last; i++) {
		if (carousel.has(i)) {
			continue;
		}
		if (i > mycarousel_itemList.length) {
			break;
		}
		carousel.add(i, mycarousel_getItemHTML(mycarousel_itemList[i - 1]));
	}
	changedataframeheight();
};

/**
 * Item html creation helper.
 */
function mycarousel_getItemHTML(item) {
	var htmlcontent="";
	if(item.flag=="1")
	{
		htmlcontent = '<center><table border="0" cellspacing="0" cellpadding="0" align="center" style="margin: 0px;cursor:hand" width="115"  height="98" title="';
		htmlcontent = htmlcontent + 'JMC '+item.title
				+ '" onclick="itemclick()"><tr><td id="' + item.id
				+ '" style="padding:5px;filter:progid:DXImageTransform.Microsoft.AlphaImageLoader(src='+item.url+',sizingMethod=scale);background-repeat:no-repeat" ';
		htmlcontent = htmlcontent
				+ ' width="100%" align="right" valign="top"><input type=\"checkbox\" id="check'
				+ item.id + '" onclick="checkclick();" style="display:none"/>';
		htmlcontent = htmlcontent
				+ '<div id="mycheckbox'
				+ item.id
				+ '" style="width:17px; height:17px;background-image:url(../images/checkbox/checkoff.gif);">&nbsp;</div></td></tr></table></center>';
	}
	else
	{
		//htmlcontent = '<center><table class="gray" border="0" cellspacing="0" cellpadding="0" align="center" style="margin: 0px;cursor:hand" width="115"  height="102" title=';
		//htmlcontent = htmlcontent + item.title
		//		+ ' ><tr><td id="' + item.id
		//		+ '" style="padding:5px;filter:progid:DXImageTransform.Microsoft.AlphaImageLoader(src='+item.url+',sizingMethod=scale);background-repeat:no-repeat" ';
		//htmlcontent = htmlcontent
		//		+ ' width="100%" align="right" valign="top"><input type=\"checkbox\" id="check'
		//		+ item.id + '" style="display:none"/>';
		//htmlcontent = htmlcontent
		//		+ '</td></tr></table></center>';
	}
	return htmlcontent;
};

function itemclick() {
	if (event.srcElement.tagName == "TD") {
		if (currentcheckobject != null
				&& currentcheckobject != event.srcElement.children(0)) {
			if (currentcheckobject.checked) {
				currentcheckobject.checked = false;
				currentmycheckobject.style.backgroundImage = "url(../images/checkbox/checkoff.gif)";
			}
		}
		if (currenttable != null) {
			currenttable.style.border = "0px solid #379be9";
		}
		if (event.srcElement.children(0).checked)
			event.srcElement.children(0).checked = false;
		else
			event.srcElement.children(0).checked = true;
		var itemid = event.srcElement.id;
		changecheckbox(itemid);
		event.srcElement.children(0).tag = event.srcElement.children(0).checked;
		currentcheckobject = event.srcElement.children(0);
		if (event.srcElement.children(0).checked)
			currentSeriesID = event.srcElement.id;
		else
			currentSeriesID = "";
		clearoptions(document.all.year);
		clearoptions(document.all.Cabin);
		clearoptions(document.all.WheelBase);
		clearoptions(document.all.Fuel);
		clearoptions(document.all.Model);
		clearoptions(document.all.Emmission);
		clearoptions(document.all.Drive);
		clearoptions(document.all.Color);		
		event.srcElement.parentElement.parentElement.parentElement.style.borderCollapse = "collapse";
		event.srcElement.parentElement.parentElement.parentElement.style.border = "1px solid #379be9";
		currenttable = event.srcElement.parentElement.parentElement.parentElement;
	}

	queryvehicle();
}
function clearoptions(selectobject)
{
	while(selectobject.options.length>0)
	{
		selectobject.options.remove(0);
	}
}
function initpage() {	
	var rowheight=document.all.maintable.rows.item(0).offsetHeight+document.all.maintable.rows.item(1).offsetHeight;
	var avaiheight = window.screen.height-window.screenTop;	
	document.all.dataframe.height=avaiheight-rowheight-52-5;
	var tdwidth=(document.body.clientWidth-100-120-100-100-100-100-80-140)/7;	
	document.all.td2.width=tdwidth;	
	document.all.td3.width=tdwidth;	
	document.all.td4.width=tdwidth;	
	document.all.td5.width=tdwidth;	
	document.all.td6.width=tdwidth;	
	document.all.td7.width=tdwidth;
	document.all.td8.width=tdwidth;		
	var url = "do.json?action=seriesvehicle&method=queryseries";
	send_request(url, onAfterQuerySeries);
}

function onAfterQuerySeriesByVinEsn(callbacktext) {
	currentSeriesID = "";
	var result = "(" + trim(callbacktext.responseText) + ")";
	var data = eval(trim(result));
	while (mycarousel_itemList.length > 0) {
		mycarousel_itemList.pop();		
	}
	for ( var i = 0; i < data.length; i++) {
		var series = {
			url : '',
			title : ''
		};
		series.url = '../Series/' + data[i].seriesid + '.jpg';
		series.title =data[i].vcename;
		series.id = data[i].seriesid;
		series.flag=data[i].flag;
		mycarousel_itemList[i] = series;
	}
	jQuery(document).ready(function() {
		jQuery('#mycarousel').jcarousel( {
			vertical : true,
			scroll : 4,
			size : mycarousel_itemList.length,
			itemLoadCallback : {
				onBeforeAnimation : mycarousel_itemLoadCallback
			}
		});
	});	
	queryvehiclebyvinesn();
}
function onAfterQuerySeries(callbacktext) {
	currentSeriesID = "";
	var result = "(" + trim(callbacktext.responseText) + ")";
	var data = eval(trim(result));
	while (mycarousel_itemList.length > 0) {
		mycarousel_itemList.pop();
	}
	for ( var i = 0; i < data.length; i++) {
		var series = {
			url : '',
			title : ''
		};
		series.url = '../Series/' + data[i].seriesid + '.jpg';
		series.title = data[i].vcename;
		series.id = data[i].seriesid;
		series.flag=data[i].flag;
		mycarousel_itemList[i] = series;
	}
	jQuery(document).ready(function() {
		jQuery('#mycarousel').jcarousel( {
			vertical : true,
			scroll : 4,
			size : mycarousel_itemList.length,
			itemLoadCallback : {
				onBeforeAnimation : mycarousel_itemLoadCallback
			}
		});
	});
	queryvehicle();
}
var flagload=0;
function changedataframeheight() {
	if(flagload==0)
	{
		flagload=1;
		var rowheight=document.all.maintable.rows.item(0).offsetHeight+document.all.maintable.rows.item(1).offsetHeight;
		var avaiheight = window.screen.height-window.screenTop;
		document.all.dataframe.height=document.all.dataframe.height/1+10;
		if(document.all.dataframe.height>0)
		{
			mycarousel.parentElement.style.height = document.all.dataframe.height-30;
			mycarouseltd.children(0).style.height = document.all.dataframe.height;
		}
		else
		{
			mycarousel.parentElement.style.height = avaiheight-rowheight-30;
			mycarouseltd.children(0).style.height = avaiheight-rowheight;
		}
		
	}
}
function onBeforeLoad() {

}
function onComplete() {

}
function onError(XMLResponse) {
	alert(XMLResponse.responseText);
}
function querySeriesbyVinOrEsnOrCode() {
	var vinesn = document.all.vinesn.value;
	var url = "do.json?action=seriesvehicle&method=queryseries&vinesn="
			+ vinesn;
	send_request(url, onAfterQuerySeriesByVinEsn);
}

function queryvehiclebyvinesn() {
	var url = "vehiclelist.jsp?vinesn="+document.all.vinesn.value;
	window.open(url, "dataframe");
}
function queryvehicle() {

	var yearvalue = document.all.year.value;
	var cabinvalue = document.all.Cabin.value;
	var wheelbasevalue = document.all.WheelBase.value;
	var fuelvalue = document.all.Fuel.value;
	var modelvalue = document.all.Model.value;
	var emmissionvalue = document.all.Emmission.value;
	var drivevalue = document.all.Drive.value;
	var colorvalue=document.all.Color.value;
	var seriesid = "";
	seriesid = currentSeriesID;
	var url = "vehiclelist.jsp?seriesid=" + seriesid + "&year=" + yearvalue
			+ "&cabin=" + cabinvalue + "&wheelbasevalue=" + wheelbasevalue
			+ "&fuel=" + fuelvalue+"&model="+modelvalue+"&emmission="+emmissionvalue+"&drive="+drivevalue+"&color="+colorvalue;
	window.open(url, "dataframe");
}
function openset() {
	window.open("systemmanage.jsp", "_blank",sFeatures);
}
function openbulletin() {
	//window.showModelessDialog("bulletinread.jsp", window,"center:yes;dialogWidth:1024px;dialogHeight:800px");
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
function clickcondition() {	
	fillcomboobject = event.srcElement;
	if (event.srcElement.id == "year") {
		fillcondition("year");
	}
	if (event.srcElement.id == "Cabin") {
		fillcondition("cabin");
	}
	if (event.srcElement.id == "WheelBase") {
		fillcondition("wheelbase");
	}
	if (event.srcElement.id == "Fuel") {
		fillcondition("fuel");
	}
	if (event.srcElement.id == "Model") {
		fillcondition("Model");
	}
	if (event.srcElement.id == "Emmission") {
		fillcondition("Emmission");
	}
	if (event.srcElement.id == "Drive") {
		fillcondition("Drive");
	}
	if (event.srcElement.id == "Color") {
		fillcondition("Color");
	}
	
	
}
function fillcondition(type) {
	if (fillcomboobject.options.length == 0) {
		var vinesnvalue="";		
		fillconditiontype = type;
		var url = "do.json?action=seriesvehicle&method=fillcondition&vinesn="+vinesnvalue+"&type="+type+"&seriesid="+currentSeriesID;
		send__request(url, onAfterFillCondition,false);
	}
}
function onAfterFillCondition(callbacktext) {
	var result = "(" + trim(callbacktext.responseText) + ")";
	var data = eval(trim(result));
	for (var i = 0; i < data.length; i++){
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
function logout()
{
	logoutfrm.submit();
}
</script>
	</head>
	<body style="margin: 0px" onload="initpage();" scroll="no">
		<table width="100%" border="0" cellspacing="0" cellpadding="0" align="center" style="padding: 0px" id="maintable">
			<tr>
				<td colspan="17" style="padding: 0px">
					<table width="100%" border="0" cellspacing="0" cellpadding="0" style="padding: 0px">
						<tr>
							<td class="loginimage" height="36px" width="126" style="padding: 0px">
								&nbsp;
							</td>
							<td class="bar" style="color: #000000;font-size:13px;font-weight:bold" style="padding: 0px">
								<span title="Home" style="padding-right:15px" style="cursor:hand" onclick="gohome();">Home</span>
								<span title="ChangePassword" style="padding-right:15px" style="cursor:hand" onclick="changepassword();" >User</span>
								<span title="AdvanceQuery" style="padding-right:15px" style="cursor: hand" onclick="AdvanceSearch();">Search</span>
								<%if(sessionuserflag.equalsIgnoreCase("3"))
								{%>
								<span title="Setting" style="padding-right:15px" style="cursor: hand" onclick="openset();" >Setting</span>
								<%}%>
								<span title="Bulletin" style="padding-right:15px" style="cursor: hand" onclick="openbulletin();" >Bulletin</span>
								<span title="Logout" style="padding-right:15px" style="cursor: hand" onclick="logout();" >Logout</span>
							</td>
							<td class="bar" width="20px" style="padding: 0px">
								&nbsp;
							</td>
						</tr>
					</table>
				</td>
			</tr>
			<tr>
				<td class="bluetd" width="120px" style="padding: 0px;">
					<table class="nobordtable" border="0" cellspacing="0"
						cellpadding="0">
						<tr>
							<td class="bluetd" colspan="2">
								VIN/ESN/CODE
							</td>
						</tr>
						<tr>
							<td class="bluetd" style="padding: 0px">
								<input type="text" style="width:91px" name="vinesn" id="vinesn" />
							</td>
							<td class="bluetd" style="padding: 0px">
								<input type="button" value="Go" style="cursor: hand" onclick="querySeriesbyVinOrEsnOrCode();" />
							</td>
						</tr>
					</table>
				</td>				
				<td class="bluetd" style="padding-left:0px" width="100">
					<table class="nobordtable" border="0" cellspacing="0" cellpadding="0">
						<tr>
							<td class="bluetd">
								Year
							</td>
						</tr>
						<tr>
							<td class="bluetd" style="padding: 0px">
								<Select name="year" id="year" style="width: 100px;font-size:10pt" onchange="queryvehicle();" onclick="clickcondition();"></Select>
							</td>
						</tr>
					</table>
				</td>
				<td class="bluetd" id="td2">
				&nbsp;
				</td>
				<td class="bluetd" width="120">
					<table class="nobordtable" border="0" cellspacing="0"
						cellpadding="0">
						<tr>
							<td class="bluetd">
								Model
							</td>
						</tr>
						<tr>
							<td class="bluetd" style="padding: 0px">
								<Select name="Model" id="Model" style="width: 120px;font-size:10pt"
									onchange="queryvehicle();" onclick="clickcondition();">
								</Select>
							</td>
						</tr>
					</table>
				</td>
				<td class="bluetd" id="td3">
				&nbsp;
				</td>
				<td class="bluetd" width="100">
					<table class="nobordtable" border="0" cellspacing="0"
						cellpadding="0">
						<tr>
							<td class="bluetd">
								Emmission
							</td>
						</tr>
						<tr>
							<td class="bluetd" style="padding: 0px">
								<Select name="Emmission" id="Emmission" style="width: 100px;font-size:10pt"
									onchange="queryvehicle();" onclick="clickcondition();">
								</Select>
							</td>
						</tr>
					</table>
				</td>
				<td class="bluetd" id="td4">
				&nbsp;
				</td>
				<td class="bluetd" width="100">
					<table class="nobordtable" border="0" cellspacing="0"
						cellpadding="0">
						<tr>
							<td class="bluetd">
								Fuel
							</td>
						</tr>
						<tr>
							<td class="bluetd" style="padding: 0px">
								<Select name="Fuel" id="Fuel" style="width: 100px;font-size:10pt"
									onchange="queryvehicle();" onclick="clickcondition();">
								</Select>
							</td>
						</tr>
					</table>
				</td>
				<td class="bluetd" id="td5">
				&nbsp;
				</td>
				<td class="bluetd" width="100">
					<table class="nobordtable" border="0" cellspacing="0"
						cellpadding="0">
						<tr>
							<td class="bluetd">
								Cabin
							</td>
						</tr>
						<tr>
							<td class="bluetd" style="padding: 0px">
								<Select name="Cabin" id="Cabin" style="width: 100px;font-size:10pt"
									onchange="queryvehicle();" onclick="clickcondition();">
								</Select>
							</td>
						</tr>
					</table>
				</td>
				<td class="bluetd" id="td6">
				&nbsp;
				</td>
				<td class="bluetd" width="100">
					<table class="nobordtable" border="0" cellspacing="0"
						cellpadding="0">
						<tr>
							<td class="bluetd">
								WB
							</td>
						</tr>
						<tr>
							<td class="bluetd" style="padding: 0px">
								<Select name="WheelBase" id="WheelBase" style="width: 100px;font-size:10pt" onchange="queryvehicle();" onclick="clickcondition();">
								</Select>
							</td>
						</tr>
					</table>
				</td>
				<td class="bluetd" id="td7">
				&nbsp;
				</td>
				<td class="bluetd" align="right" style="padding-right:0px" width="80">
					<table class="nobordtable" border="0" cellspacing="0"
						cellpadding="0">
						<tr>
							<td class="bluetd" align="left">
								Drive
							</td>
						</tr>
						<tr>
							<td class="bluetd" style="padding: 0px" align="right">
								<Select name="Drive" id="Drive" style="width: 80px;font-size:10pt"
									onchange="queryvehicle();" onclick="clickcondition();">
								</Select>
							</td>
						</tr>
					</table>
				</td>
				<td class="bluetd" id="td8">
				&nbsp;
				</td>
				<td class="bluetd" align="right" style="padding-right:0px" width="140">
					<table class="nobordtable" border="0" cellspacing="0"
						cellpadding="0">
						<tr>
							<td class="bluetd" align="left">
								Color
							</td>
						</tr>
						<tr>
							<td class="bluetd" style="padding: 0px" align="right">
								<Select name="Color" id="Color" style="width:140px;font-size:10pt"
									onchange="queryvehicle();" onclick="clickcondition();">
								</Select>
							</td>
						</tr>
					</table>
				</td>				
			</tr>		
		<tr>
		<td colspan="16">
		<table width="100%" border="0" cellspacing="0" cellpadding="0" style="padding:0px">
		<tr>
		<td style="margin: 0px; padding: 0;" valign="top" align="left" id="mycarouseltd" width="120">
				<ul id="mycarousel" style="margin: 0px;padding: 0;">
				</ul>
			</td>
			<td style="margin: 0px; padding: 0" valign="top" align="left">
				<iframe src="" name="dataframe" id="dataframe" frameborder=0 width="100%" style="margin: 0px; padding: 0"></iframe>
			</td>
		</tr>
		</table>
		</td>			
		</tr>
		</table>
		<form action="../login.jsp" method="post" target="_self" name="logoutfrm" style="display:none">
		<input type="hidden" name="logoutinput" value="logout"/>
		</form>
	</body>
</html>