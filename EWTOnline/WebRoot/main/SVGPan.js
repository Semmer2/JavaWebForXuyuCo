var svgWin;
var svgDoc;
var svgRoot = null;
var svgControlElement=null;
var mapType;
var stationID;
var curElement;
var isMoving;
var svgNS = "http://www.w3.org/2000/svg";//SVG的命名空间
var xlinkNS="http://www.w3.org/1999/xlink";

function initSVG() {
	addListener();
}
function addListener()
{
	try
	{
		svgWin = svgbox.window;
		svgDoc = svgbox.getSVGDocument();
		svgRoot = svgDoc.getDocumentElement();
		mapType = svgRoot.getAttribute("MapType");
		
		//svgControlElement = svgRoot.getElementById("viewportlayer");
		
		//--------------------定义SVG图元对像事件侦听器及处理程序------------------
		//svgControlElement.addEventListener("click",deviceShapeOnClick,false);	 //设置图元设备的onclick事件处理函数
		//svgControlElement.addEventListener("mouseover",deviceShapeOnMouseover,false);	 //设备图元设备鼠标进入事件处理函数
		//svgControlElement.addEventListener("mouseout",deviceShapeOnMouseout,false);	 //设备图元设备鼠标离开事件处理函数
		//svgControlElement.addEventListener("mousedown",deviceShapeOnMousedown,false);	 //设备图元设备鼠标进入事件处理函数
		//svgControlElement.addEventListener("mouseup",deviceShapeOnMouseup,false);
		//svgControlElement.addEventListener("mousemove",deviceShapeOnMousemove,false);		
	}
	catch(e)
	{
		alert(e.description);
	}
}

/**
 * Instance an SVGPoint object with given event coordinates.
 */
function getEventPoint(evt) {
	var p = root.createSVGPoint();

	p.x = evt.clientX;
	p.y = evt.clientY;

	return p;
}
function addHandCurcor(element) {
	if(element.getNodeName() == "svg")
		return;
	var aElement = svgDoc.createElementNS(svgNS, "a");
	aElement.setAttribute("xlink:href", "");
	var parent = element.parentNode;
	aElement.appendChild(element);
	parent.appendChild(aElement);
}
function removeHandCurcor(element) {
	if(element.getNodeName() == "svg")
		return;
	var hand = element.parentNode;
	var parent = hand.parentNode;
	var nodeList = hand.getChildNodes();
	for (var i = 0; i < nodeList.length; i++) {
		parent.appendChild(nodeList.item(i));
	}
	parent.removeChild(hand);
}

function deviceShapeOnMouseover(evt)
{
	  try
      {
		  if(isMoving == true)
		 		return;
		    var deviceElement = evt.target.parentNode;		   
			addHandCurcor(evt.target);		    
	  }
      catch(e) {
      		alert(e.description);
      }
}
function deviceShapeOnMouseout(evt)
{
	 try
     {
		    var deviceElement = evt.target.parentNode;			
			removeHandCurcor(evt.target);
			
	 }
	 catch(e) {
      		alert(e.description);
     }
}
function deviceShapeOnClick(evt)
{
	
}
function deviceShapeOnMousedown(evt)
{
	curElement = evt.target.parentNode.parentNode;
	if(evt.button == 0)
	{
		addHandCurcor(evt.target);
		var scale = svgControlElement.currentScale;
		cx=evt.clientX;
	    cy=evt.clientY
		isMoving = true;
	}
	else if(evt.button == 2)
	{
		
	}
}

String.prototype.trim = function() { 
  return this.replace(/^\s\s*/, '').replace(/\s\s*$/, ''); 
} 

function deviceShapeOnMousemove(evt)
{
	if(isMoving == true)
	{
			var xx = evt.clientX;
			var yy = evt.clientY;
			//if(Math.abs(xx-cx) > 30 || Math.abs(yy-cy) > 30)
			//{
				svgControlElement.currentTranslate.x = svgControlElement.currentTranslate.x + xx-cx;
				svgControlElement.currentTranslate.y = svgControlElement.currentTranslate.y + yy-cy;
			    cx=xx;
			    cy=yy;
			//}
	}
}

function deviceShapeOnMouseup(evt)
{
	removeHandCurcor(evt.target);
	isMoving = false;
}

function getHref(deviceElement) {
	var href = "";
	if(deviceElement.getAttribute("href") != "")
		href = deviceElement.getAttribute("href");
	return href;
}

function getChildByTagName(e, name) {
	var element = null;
	var nodeList = e.getChildNodes();
	for (var i = 0; i < nodeList.length; i++)
	{
		if (nodeList.item(i).nodeName == name) 
		{
			element =  nodeList.item(i);
			break;
		}
	}
	return element;
}