<%@ page language="java" import="java.util.*,java.io.*" 
pageEncoding="ISO-8859-1"%> 
<% 
String path = request.getContextPath(); 
String basePath = request.getScheme() + "://" 
    + request.getServerName() + ":" + request.getServerPort() 
    + path + "/"; 
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
    String strPdfPath = new String("c://1.pdf"); 
    //�жϸ�·���µ��ļ��Ƿ���� 
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
     out.print(strPdfPath + " �ļ�������!"); 
    } 

   } catch (Exception e) { 
    out.println(e.getMessage()); 
   } 
%> 
<body> 
   <br> 
</body> 
</html> 
