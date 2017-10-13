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
public class SuggestAction {
	private SuggestService service=null;
	private HttpServletRequest request=null;
    public SuggestAction(HttpServletRequest request)
    {    	
    	service=new SuggestService(request);
    	this.request=request;
    }
    public Object doing(String method) throws Exception
    {
    	Object result="";
		if(method.equalsIgnoreCase("getpath"))
		{
			String id=request.getParameter("id");
			List ls=getSuggestById(id);			
			result=ls;
		}		
		return result;
    }
    public String createSuggest(String subject, String path, String content, String fromwho, String id,String vehid,String imageid) throws Exception {
		return service.createSuggest(subject, path, content, fromwho, id,vehid,imageid);
	}
	public String getNewSuggestId() throws Exception
	{
		return service.getNewSuggestId();
	}
	public String delSuggest(String ids) throws Exception {
		return service.delSuggest(ids);
	}	
	public List getSuggests() throws Exception {
		return service.getSuggests();
	}	
	public List getSuggestById(String id) throws Exception {
		return service.getSuggestById(id);
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