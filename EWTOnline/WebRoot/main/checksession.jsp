<%
response.setHeader("Cache-Control", "no-cache");
response.setHeader("Pragma", "no-cache");
response.setDateHeader("Expires", 0);
if(request.getSession().getAttribute("currentuserid")==null){%>
   <script language="javascript">       
       window.opener = null;   
	   window.open('','_self'); 
	   window.top.close();
	   window.open('/EWTOnline/login.jsp','loginjlcar'); 
   </script>
<%return;}
%>

