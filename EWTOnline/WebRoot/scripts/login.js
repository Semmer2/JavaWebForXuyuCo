function loadstart(serverip)
{
	serverip=unescape(serverip);
	var name="carmain";
	var w=screen.availWidth;
	var h=screen.availHeight;
  	var sFeatures = "fullscreen=0,toolbar=0,location=0,directories=0,status=1,menubar=0";
    sFeatures += ",scrollbars=0,resizable=1,top=0,left=0,width=" + w + ",height=" + h + " ";
    sFeatures="";
	var cpswindow=window.open("main/login.jsp", "_blank", sFeatures);
	cpswindow.opener=null;
	window.open('','_self');//for IE7       
	window.close();	
}