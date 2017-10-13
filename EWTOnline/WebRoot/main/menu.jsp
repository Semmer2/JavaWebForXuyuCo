<%@ page contentType="text/html;charset=utf-8" pageEncoding="utf-8"%>
<%@ page import="java.util.*"%>
<%@ taglib uri="/WEB-INF/tld/datagrid.tld" prefix="grid"%>
<%@ page import="com.jl.action.*"%>
<%@ page import="com.jl.entity.*"%>
<%@ page import="java.net.*"%>
<%@include file="checksession.jsp"%>
<%
	response.setHeader("Cache-Control", "no-cache");
	response.setHeader("Pragma", "no-cache");
	response.setDateHeader("Expires", 0);
	CommonAction commonaction=new CommonAction(request);
	String vehid=request.getParameter("vehid");
	vehid="906";
	List ls=commonaction.getDomMenu(vehid);
	String menu_one_level_key="";
	String menu_two_level_key="";
	
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml2/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<meta http-equiv="X-UA-Compatible" content="IE=EmulateIE7" />
<title>零件热点</title>
<link rel="stylesheet" type="text/css" href="menu/dropdown.css" />
<script src="menu/stuHover.js"type="text/javascript"></script>
<script language="javascript">
function openhotinfo()
{
	alert(event.srcElement.tag);
}
</script>
</head>
<body style="margin:0px">
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
    		out.print("<li class=\"top\"><a href=\"#nogo1\" class=\"top_link\"><span class=\"down\" id=\""+t1d+"\" tag=\""+imageid+"\" onclick='openhotinfo();'>"+t1dname+"</span></a>");
    		twomenu=(Hashtable)commonaction.getTwoDomMenu(t1d,ls);
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
	            Hashtable threemenu=(Hashtable)commonaction.getThreeDomMenu(t1d,key,ls);
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
</body>
</html>