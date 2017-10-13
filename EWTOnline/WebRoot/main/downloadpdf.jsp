<%@ page language="java" import="java.util.*,java.io.*" 
pageEncoding="ISO-8859-1"%> 
<% 
String path = request.getContextPath(); 
String basePath = request.getScheme() + "://" 
    + request.getServerName() + ":" + request.getServerPort() 
    + path + "/"; 
String pdfid=request.getParameter("pdfid");
if(pdfid==null) pdfid="";
String rootpath=request.getRealPath("/");
String pdfurl=rootpath+"\\pdf\\"+pdfid+".pdf";
System.out.println(pdfurl);
%> 

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"> 
<html> 
<head> 
   <base href="<%=basePath%>"> 
</head> 
<% 
   out.clear(); 
   out = pageContext.pushBody(); 
   response.setContentType("application/pdf");
   try { 
    String strPdfPath = new String(pdfurl); 
    File file = new File(strPdfPath); 
    if (file.exists()) { 
     DataOutputStream temps = new DataOutputStream(response 
       .getOutputStream()); 
     DataInputStream in = new DataInputStream( 
       new FileInputStream(strPdfPath)); 

     byte[] b = new byte[2048]; 
     while ((in.read(b)) != -1) { 
      temps.write(b); 
      temps.flush(); 
     } 

     in.close(); 
     temps.close(); 
    } else { 
     out.print(strPdfPath + "Open Error!"); 
    } 

   } catch (Exception e) { 
    out.println(e.getMessage()); 
   } 
%> 
<body> 
   <br> 
</body> 
</html> 
