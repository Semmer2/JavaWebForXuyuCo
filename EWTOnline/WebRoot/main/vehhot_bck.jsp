<%@ page contentType="text/html;charset=utf-8" pageEncoding="utf-8"%>
<%@ page import="java.util.*"%>
<%@ taglib uri="/WEB-INF/tld/datagrid.tld" prefix="grid"%>
<%@ page import="com.jl.action.*"%>
<%@ page import="com.jl.entity.*"%>
<%@ page import="java.net.*"%>
<%@ page import="com.jl.util.*"%>
<%@include file="checksession.jsp"%>
<%
	response.setHeader("Cache-Control", "no-cache");
	response.setHeader("Pragma", "no-cache");
	response.setDateHeader("Expires", 0);
	String sessionuserflag=request.getSession().getAttribute("userflag").toString();
	CommonAction commonaction=new CommonAction(request);
	String vehid=request.getParameter("vehid");
	String initimageid=request.getParameter("imageid");
	String inithotid=request.getParameter("hotid");
	if(initimageid==null) initimageid="";
	if(inithotid==null) inithotid="";
	List ls=commonaction.getDomMenu(vehid);
	List vehlist=commonaction.getTcvehicle(vehid);
	Hashtable vehht=(Hashtable)vehlist.get(0);
	String mastercode=vehht.get("mastercode").toString();
	String seriesid=commonaction.getSeriesid(vehid);
	String seriesimage="../Series/big/"+seriesid+".jpg";
	String menu_one_level_key="";
	String menu_two_level_key="";
	String vcroleno=request.getSession().getAttribute("vcroleno").toString();
	if(vcroleno==null) vcroleno="";
	
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<meta http-equiv="X-UA-Compatible" content="IE=EmulateIE7" />
<title>EWT Online PartHot</title>
<link rel="stylesheet" href="../css/slider/ext-all-gray.css" />
<link rel="stylesheet" href="../css/slider/app.css" />
<link rel="stylesheet" type="text/css" href="menu/dropdown.css" />
<script Charset="GBK" type='text/javascript' src='../scripts/common.js'></script>
<script Charset="UTF-8" type="text/javascript" src="../scripts/jquery/json2.js" ></script>  
<script Charset="UTF-8" type="text/javascript" src="../scripts/jquery/jquery-1.7.min.js" ></script>
<script Charset="UTF-8" type='text/javascript' src='../scripts/ajaxUtil.js'></script>	
<script src="menu/stuHover.js"type="text/javascript"></script>
<script src="SVGPan.js"type="text/javascript"></script>
<script src="../scripts/slider/ext-all.js"></script>
<script src="../scripts/slider/app.js"></script>
<link href="../css/index.css" rel="stylesheet" type="text/css" />
<link href="../css/index_table.css" rel="stylesheet" type="text/css" />


<script language="javascript">
var svgDoc = null ;
var svgRoot = null ;
var svgWnd = null ; //svg的window对象
var selectimageid=0;
var hotzoom=null;
var bl=1;//图形比例
var drawedelement=new Array();
var svgheight;
var buyframeheight=78;
var currentsvghotelement=null;
var headheight=0;
var loadflag=0;
var menuid=-1;

var w=screen.availWidth;
var h=screen.availHeight;
var sFeatures = "fullscreen=0,toolbar=0,location=0,directories=0,status=1,menubar=0";
sFeatures += ",scrollbars=0,resizable=1,top=0,left=0,width=" + w + ",height=" + h + " ";
sFeatures="";
function resetZoom()
{
	svgWnd.resetZoom();
}
function showhotatsvg(hot)
{
	if(currentsvghotelement!=null) 
	{	
		currentsvghotelement.setAttribute("stroke", "white");
	}
	var rectelement=svgRoot.getElementById("rect"+hot);
	if(rectelement!=null)
	{		
		rectelement.setAttribute("stroke", "blue");
		currentsvghotelement=rectelement;
	}
}

function clearbuycar()
{	
   	var url="../servlet.jsp?action=buycarmanage&method=clearuserbuy";
	callbackresult=send_request(url,onCallBackForDeleteRow);
}
function onCallBackForDeleteRow(callbacktext)
{	
	var result=trim(callbacktext.responseText);
	var rtn=trim(result);
	var returns=rtn.split(",");
	if(returns[0]!="")
	{			
	    window.open("buycar.jsp","buypartframe");
    }
    else
    {
    	alert(returns[1]);
    }
}
function initpage()
{
	headheight=maintable.rows.item(0).offsetHeight+maintable.rows.item(1).offsetHeight+20;
	svgheight=document.body.clientHeight-headheight;
	document.all.hottable.style.height=svgheight;
	document.all.imagezoom.style.height=svgheight;
  	document.all.imagezoom.style.width=svgheight;
  	document.all.partlistzoom.style.width=document.body.clientWidth-svgheight-controlzoom.width;
  	document.all.partframe.style.height=svgheight-buyframeheight;
  	document.all.buypartframe.style.height=buyframeheight;
  	initSVG();
  	svgWnd = emSvg.window;  
	svgRoot = svgWnd.svgRoot ; //svgRoot在svg的js中是个全局的变量	
	svgDoc=svgWnd.svgDoc;	
  	initimage();  	
	//document.all.zoomcontroltd.style.paddingTop=(document.body.clientHeight-200)/10;
	//var myimageobject = svgRoot.getElementById("myimage");
	//myimageobject.addEventListener("click",imageclick,false);	
	showcontent();
	openimage();
	var flag="<%=initimageid%>";
	if(flag=="") document.all.suggestion.style.display="none";
	
}
function showcontent()
{
	loadflag=loadflag+1;	
	if(loadflag==3)
	{
		document.all.hottable.style.display="block";
		document.all.waittable.style.display="none";		
	}
}
function openhotinfo()
{
	selectimageid=event.srcElement.tag;	
	menuid=event.srcElement.id;	
	document.all.suggestion.style.display="block";
	resetzoom();
	SetImage(selectimageid);
}
function openhotinfobyimageid(imageid)
{
	selectimageid=imageid;
	resetzoom();
	SetImage(selectimageid);
}
function openset()
{
	window.open("systemmanage.jsp", "_blank",sFeatures);
}
function openbulletin()
{
	//window.showModelessDialog("../manage/notifyview.jsp",window,"center:yes;dialogWidth:1024px;dialogHeight:900px");
	var s = "fullscreen=0,toolbar=0,location=0,directories=0,status=1,menubar=0";
	s += ",scrollbars=0,resizable=1,top=0,left=0,width=" + w + ",height=" + h + " ";
	window.open("bulletinread.jsp","_blank",s);
}

function AdvanceSearch()
{
	var windowhandle=window.open("search.jsp","_blank",sFeatures);
	windowhandle.focus();
}
function gohome()
{
	var windowhandle=window.open("seriesvehicle.jsp","_blank",sFeatures);
	windowhandle.focus();
}
function SetImage(imageid)
{	
	var url="do.json?action=vehhot&method=tif2jpg&imageid="+imageid;
	$.ajax({
	type : "GET",//使用post方法访问后台  
	dataType : "json",//返回json格式的数据  
	url : url,//要访问的后台地址 
	cache: false,//默认: true,设置为 false 将不会从浏览器缓存中加载请求信息。  
	contentType : "application/json;charset=utf-8",	
	beforeSend:onBefore,
	complete:onComplete,
	success:onAfterConvern,
	error:onError
	});
	var url="hotpartlist.jsp?vehid=<%=vehid%>&imageid="+selectimageid;
	window.open(url,"partframe");	
}

function onComplete()
{
	
}
function onError(XMLResponse)
{
	alert(XMLResponse.responseText);
} 
function onBefore()
{
	
}

function onAfterConvern(returnobject)
{
	if(returnobject.result=="-1")
	{
		alert("This image doesn't exist!");
	}
	else
	{			
		var data=returnobject;		
		var imageurl=data[0].imageurl;
		var imagecode=data[0].imagecode;
		var imageheight=data[0].imageheight/1;
		var imageobject=svgRoot.getElementById('myimage') ;
		if(imageobject) 
		{
		  	imageobject.setAttribute('xlink:href', imageurl);
		  	imageobject.setAttribute('width', svgheight);
		  	imageobject.setAttribute('height', svgheight);		  	
		  	svgWnd.setimagecode(imagecode);
		  	//改变比例
		  	bl=svgheight/imageheight;
		  	DrawHot();
		}		
	}	
}
function initimage()
{		
	var imageurl="<%=seriesimage%>";
	var imageobject=svgRoot.getElementById('myimage') ;
	if(imageobject) 
	{
	  	imageobject.setAttribute('xlink:href', imageurl);		  	
	}	
}
function DrawHot()
{
	svgWnd = emSvg.window;
	if(svgWnd == null) return ;
	svgRoot = svgWnd.svgRoot ; //svgRoot在svg的js中是个全局的变量
	svgDoc=svgWnd.svgDoc;
	if(svgRoot == null) return ;
	var url="do.json?action=vehhot&method=drawhot&imageid="+selectimageid;
	$.ajax({
	type : "GET",//使用post方法访问后台  
	dataType : "json",//返回json格式的数据  
	url : url,//要访问的后台地址 
	cache: false,//默认: true,设置为 false 将不会从浏览器缓存中加载请求信息。  
	contentType : "application/json;charset=utf-8",	
	beforeSend:onBefore,
	complete:onComplete,
	success:onAfterDrawHot,
	error:onError
	});
}
function controlsvg()
{
	if(document.all.imagezoom.style.display!="none")
	{
		document.all.imagezoom.style.display="none";
		document.all.imagezoom.style.width=0;
	  	document.all.partlistzoom.style.width=document.body.clientWidth-controlzoom.width;
		event.srcElement.src="../images/new/show.png";
	}
	else
	{
		document.all.emSvg.width="100%";
		document.all.imagezoom.style.display="block";		
		document.all.hottable.style.height=svgheight;
		document.all.imagezoom.style.height=svgheight;
	  	document.all.imagezoom.style.width=svgheight;
	  	document.all.partlistzoom.style.width=document.body.clientWidth-svgheight-controlzoom.width;
		event.srcElement.src="../images/new/hidden.png";
	}
}
function imageclick(evt)
{
	if(currentsvghotelement!=null) 
	{		
		currentsvghotelement.setAttribute("stroke", "white");
	}	
	var url="hotpartlist.jsp?vehid=<%=vehid%>&imageid="+selectimageid;
	window.open(url,"partframe");
}
function hotmousedown(evt)
{
	if(currentsvghotelement!=null) 
	{
		currentsvghotelement.setAttribute("stroke","white");
	}
	var hot=evt.target.getAttribute("hotid");
	var rectelement=svgRoot.getElementById("rect"+hot);
	if(rectelement!=null)
	{
		rectelement.setAttribute("stroke", "blue");
	}
	currentsvghotelement=rectelement;
	var rowobject=partframe.gethightrow(hot);
	if(rowobject!=null)
	{
		partframe.tC.scrollTop=rowobject.offsetTop-27;		
	}	
	partframe.lightOnByRow(rowobject);
	//evt.target.setAttribute("style","text-align:center;text-anchor:start;font-size:14;font-family:Arial;fill:red;font-weight:bolder");
	//var url="hotpartlist.jsp?vehid=<%=vehid%>&imageid="+selectimageid+"&hotid="+evt.target.id;
	//window.open(url,"partframe");	
}
function selecthot(hotid)
{
	if(currentsvghotelement!=null) 
	{	
		currentsvghotelement.setAttribute("stroke","white");
	}
	var hot=hotid;	
	var rectelement=svgRoot.getElementById("rect"+hot);
	
	if(rectelement!=null)
	{
		//hotelement.setAttribute("style","text-align:center;text-anchor:start;font-size:14;font-family:Arial;fill:white;font-weight:bolder");
		rectelement.setAttribute("stroke", "blue");
	}
	currentsvghotelement=rectelement;
	var rowobject=partframe.gethightrow(hot);
	if(rowobject!=null)
	{
		partframe.tC.scrollTop=rowobject.offsetTop-27;		
	}	
	partframe.lightOnByRow(rowobject);
	
	//evt.target.setAttribute("style","text-align:center;text-anchor:start;font-size:14;font-family:Arial;fill:red;font-weight:bolder");
	//var url="hotpartlist.jsp?vehid=<%=vehid%>&imageid="+selectimageid+"&hotid="+evt.target.id;
	//window.open(url,"partframe");
	
}
function onAfterDrawHot(returnobject)
{
   hotzoom=svgDoc.getElementById("viewportlayer");
   var data=returnobject;
   while(drawedelement.length>0)
   {
	   hotzoom.removeChild(drawedelement.pop());
   }
   for(var i=0;i<data.length;i++)
   {	  
		var hot=data[i].ihot;
		var x=data[i].ix/1;//rect left x
		var y=data[i].iy/1;//rect left y
		var rectwidth=data[i].ialign/1;//rect length
		var rectheight=data[i].len/1;//rect with	  
		
		var hrefelement = svgDoc.createElementNS(svgNS, "a");
	    hrefelement.setAttribute("xlink:href", "");
		
		
		var rect = svgDoc.createElementNS("http://www.w3.org/2000/svg", "rect");
		rect.setAttribute("id", "rect"+hot);
		rect.setAttribute("hotid",hot);		
		//rect.setAttribute("x", textnode.getAttribute("x")-2);
	    //rect.setAttribute("y",textnode.getAttribute("y")-12);
	    rect.setAttribute("x", bl*x);
	    rect.setAttribute("y", bl*y);
	    rect.setAttribute("width", bl*rectwidth);
	    rect.setAttribute("height", bl*rectheight);
	    
		rect.addEventListener("click",hotmousedown,false);
	    rect.setAttribute("stroke", "white");
	    rect.setAttribute("stroke-width", "2");
	    rect.setAttribute("fill-opacity", "0");	    
	    hrefelement.appendChild(rect);
	    hotzoom.appendChild(hrefelement);	   
		drawedelement[drawedelement.length]=hrefelement;
		
   }
   var inithotid="<%=inithotid%>";
   if(inithotid!="")
   {
	   selecthot(inithotid);
   }
}
function resetzoom()
{
	var currentvalue = 100;
	myslider.setValue(100);
	svgWnd.zoomIn(1.0);
}
function zoomin() {
	var currentvalue = myslider.getValue();
	var value = currentvalue + 30;
	myslider.setValue(value);
	svgWnd.zoomIn(value/100);
}
function zoomout() {
	var currentvalue = myslider.getValue();
	var value = currentvalue - 30;
	myslider.setValue(value);
	svgWnd.zoomIn(value/100);
}
function zoomvalue(factor)
{
	if(event.button==1)
	{		
		svgWnd.zoomIn(accDiv(factor,100));
	}
}
function exportbuy()
{
	window.open("exportbuycar.jsp?output=all","hiddenframe");	
}
function refreshbuy()
{
	window.open("buycar.jsp","buypartframe");	
}
function accDiv(arg1,arg2){ 
var t1=0,t2=0,r1,r2; 
try{t1=arg1.toString().split(".")[1].length}catch(e){} 
try{t2=arg2.toString().split(".")[1].length}catch(e){} 
with(Math){ 
r1=Number(arg1.toString().replace(".","")) 
r2=Number(arg2.toString().replace(".","")) 
return (r1/r2)*pow(10,t2-t1); 
} 
}
function openboard() {
	window.showModelessDialog("publishsuggest.jsp?imageid="+selectimageid+"&vehid=<%=vehid%>", window,
			"center:yes;dialogWidth:750px;dialogHeight:600px");
}
function queryhotimage() {
	window.showModelessDialog("queryhot.jsp?mastercode=<%=mastercode%>&vehid=<%=vehid%>", window,
			"center:yes;dialogWidth:900px;dialogHeight:450px;Minimize=yes;location=0");	
}
function changepassword()
{    	
   	var surl="../manage/changepassworddia.jsp";
   	var parames="dialogHeight:130px;dialogWidth:360px;center:yes";
   	window.showModelessDialog(surl,window,parames);
}
function openimage()
{
	var imageid="<%=initimageid%>";
	if(imageid!="")
	{
		selectimageid=imageid;
		SetImage(selectimageid);
	}	
}
function showbuycar()
{	
	window.showModelessDialog("allbuycar.jsp", window,
	"center:yes;dialogWidth:620px;dialogHeight:400px");
}
</script>
</head>
<body style="margin:0px" onload="initpage();" scroll="no">
<table width="100%" border="0" cellspacing="0" cellpadding="0" align="center" style="margin: 0px" id="maintable">
			<tr>
			   <td style="padding:0px" colspan="3">
				   <table width="100%" border="0" cellspacing="0" cellpadding="0" style="padding: 0px">
				   <tr>
					<td class="loginimage" height="36px" width="190px">
						&nbsp; 
					</td>					
					<td class="bar" style="color: #000000;font-size:13px;font-weight:bold" style="padding: 0px">
						<span title="Home" style="padding-right:15px" style="cursor:hand" onclick="gohome();">Home</span>
						<span title="ChangePassword" style="padding-right:15px" style="cursor:hand" onclick="changepassword();">User</span>
						<span title="AdvanceQuery" style="padding-right:15px" style="cursor: hand" onclick="AdvanceSearch();" >Search</span>
						<%if(sessionuserflag.equalsIgnoreCase("3"))
						{%>
						<span title="Setting" style="padding-right:15px" style="cursor: hand" onclick="openset();">Setting</span>
						<%}%>
						<span title="Bulletin" style="padding-right:15px" style="cursor: hand" onclick="openbulletin();">Bullet</span>
					</td>
					<td class="bar" width="20px">
						&nbsp;
					</td>
					</tr>
					</table>
				</td>
			</tr>
			<tr>
				<td style="padding:0px;" >
					<ul id="nav">
					    <% 
					    Hashtable twomenu=null;
					    for(int i=0;i<ls.size();i++)
					    {    	
					    	Hashtable ht=(Hashtable)ls.get(i);
					    	String imageid=ht.get("imageid").toString();
					    	String t1d=ht.get("t1d").toString();
					    	String t1dname=ht.get("t1dname").toString();
					    	if(menu_one_level_key.equalsIgnoreCase(t1d)==false)
							{	
					    		out.print("<li class=\"top\"><a href=\"#nogo1\" class=\"top_link\"><span id=\""+t1d+"\" tag=\""+imageid+"\" onclick='openhotinfo();' style='font-weight:bold'>"+t1dname+"</span></a>");
					    		twomenu=commonaction.getTwoDomMenu(t1d,ls);
					    		if(twomenu.size()>0)
					    		{
					    			out.println("<ul class=\"sub\">");
					    		}    	
						    	for(Iterator it=twomenu.keySet().iterator();it.hasNext();)
						    	{   
						            //从ht中取  
						            String key=(String)it.next();   
						            Object value=twomenu.get(key);
						            String[] twovalues=(String[])value;
						            //放进hm中	            
						            Hashtable threemenu=commonaction.getThreeDomMenu(t1d,key,ls);
						            if(threemenu.size()>0)
						            {	            	
						            	out.println("<li><a href=\"#\" class=\"fly\"><span id=\"t2d"+key+"\" tag=\""+twovalues[1]+"\" onclick='openhotinfo();'>"+twovalues[0]+"</span></a>");
						            	int m=0;
						            	for(Iterator threeit=threemenu.keySet().iterator();threeit.hasNext();)
						    	    	{
						            		//从ht中取  
						    	            String threekey=(String)threeit.next();   
						    	            Object threevalue=threemenu.get(threekey);   
						    	            //放进hm中
						    	            if(m==0)
						    	    		{
						    	    			out.println("<ul>");
						    	    		}
						    	            String[] threevalues=(String[])threevalue;
						    	            out.println("<li><a href=\"#\"><span id=\"t3d"+threekey+"\" tag=\""+threevalues[1]+"\" onclick='openhotinfo();'>"+threevalues[0]+"</span></a></li>");    	    	            
						    	            if(m==threemenu.size()-1)
						    	    		{
						    	    			out.println("</ul>");
						    	    		}    	    		 
						    	    		m++; 
						    	    	}
						            	out.println("</li>");
						            }
						            else
						            {
						            	out.println("<li><a href=\"#\" class=\"fly\"><span id=\"t2d"+key+"\" tag=\""+twovalues[1]+"\" onclick='openhotinfo();'>"+twovalues[0]+"</span></a></li>");
						            }                  
						        }
					   			if(twomenu.size()>0)
					       		{
					   				out.println("</ul>");
					       		}
					   			out.println("</li>");    		
					    	}    	
					    	if(i==(ls.size()-1)) out.print("</li>");
					    	menu_one_level_key=t1d;
					    }%>
					</ul>
				</td>
				<td align="right" valign="top" class="t1" style="padding-top:2px;padding-bottom:0px;padding-right:20px;">
					<%=mastercode%>
				</td>
			</tr>
			<tr>
			<td colspan="2">
				<table id="hottable" border="0" cellspacing="0" cellpadding="0" style="display:none;padding:0px; border-bottom:1px solid #000000;">
			    <tbody>
			       <tr>
			            <td rowspan="3" valign="top" id="imagezoom">			            
			            	<embed id='emSvg' wmode="transparent" name='svgbox' src='hotsvg.svg' width='100%' height='100%' type='image/xml-svg' pluginspage='http://www.adobe.com/svg/viewer/install/'></embed>			            	
			            </td>
			            <td rowspan="3" valign="top" id="controlzoom" width="30px" style="background-color:#ffffff">
			           	    <table width="100%" border=0 cellspacing="0" cellpadding="0" style="padding:0px;BORDER-RIGHT: #999999 1px outset; BORDER-TOP: #ffffff 0px outset; BORDER-LEFT: #999999 1px outset; BORDER-BOTTOM: #ffffff 0px outset; BORDER-COLLAPSE: collapse;height:expression(document.body.clientHeight-headheight+20);">
			           	    <tr>
			           	    <td height="25px" valign="top" style="padding-top:20px;" align="center">
			           	    <img src="../images/new/hidden.png" style="cursor:hand" onclick="controlsvg();" width="22" height="22"/>
			           	    </td>
			           	    </tr>
			           	    <tr>
			           	    <td height="25px" align="center" valign="top" style="padding-top:20px">
			           	    <img src="../images/new/reporterror.png" style="cursor:hand" onclick="openboard();" width="20" height="20" id="suggestion"/>
			           	    </td>
			           	    </tr>
			           	    <tr>
			           	    <td height="25px" align="center" valign="top" style="padding-top:20px">
			           	    <img src="../images/new/vehquery.png" style="cursor:hand" onclick="queryhotimage();" width="20" height="20"/>
			           	    </td>
			           	    </tr>
			           	    <tr>
			           	    <td align="center" valign="middle" id="zoomcontroltd" height="100%">
			           	    	<table style="padding: 0px; margin: 0px;" border="0" cellspacing="0" cellpadding="0">
									<tr>
										<td align="center">											
											<img src="../images/new/zoomin.png" onclick="zoomin();" style="cursor:hand;font-size:25px;font-weight:bolder;color:#379be9" width="20" height="20"/>											
										</td>
										<tr>
											<td align="center" style="padding:0px">
												<div id="slider1" onmousemove="zoomvalue(myslider.getValue());"></div>
											</td>
										</tr>
										<tr>
											<td align="center" valign="top">
												<img src="../images/new/zoomout.png" onclick="zoomout();" style="cursor:hand;font-size:25px;font-weight:bolder;color:#379be9" width="20" height="20"/>	
											</td>
										</tr>
								</table>
			           	    </td>
			           	    </tr>			           	   
			           	    <tr>
			           	    <td height="25px" align="center" valign="top" style="padding-top:20px">
			           	    <img src="../images/new/buy.png" style="cursor:hand;display:none" id="buyflag" width="22" height="22" onclick="showbuycar();"/>
			           	    </td>
			           	    </tr>			           	    
			           	    <tr>
			           	    <td height="56">
			           	    &nbsp;
			           	    </td>
			           	    </tr>
			           	    </table>
			            </td>
			            <td id="partlistzoom" width="100%" valign="top" style="padding:0px">
			            	<iframe src="hotpartlist.jsp?vehid=-1" name="partframe" id="partframe" frameborder=0 width="100%" style="padding:0"></iframe>
			            </td>
			        </tr>
			        <tr>
			            <td valign="top" style='background: url("/EWTOnline/images/datagrid/grid_head_bg.gif") repeat;' height="20">
			            &nbsp;			            	
			            </td>
			        </tr> 
			        <tr>
			            <td valign="top">
			            	<iframe src="buycar.jsp" name="buypartframe" id="buypartframe" frameborder=0 width="100%"></iframe>
			            </td>
			        </tr>     
			    </tbody>
				</table>
				<table id="waittable"  width="100%" height="600" border="0" cellspacing="0" cellpadding="0" style="display:block">
				<tr>
				<td align="center" valign="middle">
				    <img src ="../images/loading.gif"/>
				</td>
				</tr>
				</table>
				</td>
			</tr>
		</table>
		<iframe name="hiddenframe" id="hiddenframe" width="1" height="1" style="display:none"></iframe>
</body>
</html>