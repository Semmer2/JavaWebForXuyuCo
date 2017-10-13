<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>  
<%@ page import="net.sf.json.*,java.io.*" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">  
<html>  
  <head>
    <title>My JSP 'Output.jsp' starting page</title>
    <META content=IE=EmulateIE7 http-equiv=X-UA-Compatible> 
  </head> 
  <body>  
    <%  
        
 response.setContentType("text/html;charset=utf-8");
 System.out.println(request.getMethod());    
  String partid = request.getParameter("PARTID");
  System.out.println(partid);    
      
  JSONObject json = new JSONObject();    
      
  JSONArray array = new JSONArray();    
  JSONObject member = null;    
  for (int i = 1; i < 2; i++) {    
     member = new JSONObject();    
     member.put("name", "xiaohua"+i);    
     member.put("age",new Integer(i));    
  }    
     
  json.put("partid", partid);    
  json.put("jsonArray", array);    
     
  PrintWriter pw = response.getWriter();     
  pw.print(json.toString());    
      
 System.out.println("json array :"+array.toString());    
  System.out.println("json object :"+json.toString());    
      
  pw.close();    
  

     
  %>  
  </body>  
</html>  
