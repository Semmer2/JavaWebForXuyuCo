package com.jl.action;
import com.jl.service.*;
import com.jl.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.PageContext;

import java.io.PrintWriter;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.*;
public class BulletinAction {
	private BulletinService service=null;
	private HttpServletRequest request=null;
    public BulletinAction(HttpServletRequest request)
    {    	
    	service=new BulletinService(request);
    	this.request=request;
    }
    public Object doing(String method) throws Exception
    {
    	Object result="";
		if(method.equalsIgnoreCase("delete"))
		{
			String ids=request.getParameter("ids");
			result=delBulletin(ids);			
		}		
		return result;
    }
    public String createBulletin(String topic,String id) throws Exception {
		return service.createBulletin(topic,id);
	}
    public String getNewBulletinId() throws Exception
	{
		return service.getNewBulletinId();
	}
	public String delBulletin(String ids) throws Exception {
		return service.delBulletin(ids);
	}
	public String publishBulletin(String id) throws Exception {
		return service.publishBulletin(id);
	}
	
	public String saveBulletin(String id,String topic) throws Exception {
		return service.saveBulletin(id, topic);
	}
	public List getBulletins() throws Exception {
		return service.getBulletins();
	}	
	public List getBulletinById(String id) throws Exception {
		return service.getBulletinById(id);
	}
	public List getBulletinsforUser() throws Exception {
		return service.getBulletinsforUser();	
	}
	public final void refresh(PageContext pagecontext,String message,String refeshmethod,boolean isclose) throws Exception
	 {
		try
		{
			HttpServletResponse response=(HttpServletResponse)pagecontext.getResponse();
			PrintWriter writer=response.getWriter();
			writer.println("<script>");
			writer.println("alert('"+message+"');");			
			writer.println(refeshmethod);
			if(isclose)
			{
				writer.println("window.close();");
			}
			writer.println("</script>");
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw ex;
		}
	 }	
}