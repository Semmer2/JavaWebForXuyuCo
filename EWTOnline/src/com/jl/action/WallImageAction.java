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
public class WallImageAction {
	private WallImageService service;
	private HttpServletRequest request=null;
    public WallImageAction(HttpServletRequest request)
    {    	
    	service=new WallImageService(request);
    	this.request=request;
    }
    public String getMaxId() throws Exception
    {
    	return service.getMaxId();
    }
    public List getWallImageList() throws Exception
	{
		return service.getWallImageList();
	}
    public String getWall() throws Exception
	{
    	return service.getWall();
	}
    public String getWallFromDataBase() throws Exception
	{
    	return service.getWallFromDataBase();
	}
    
    public void addWallImage(String imagename,String id) throws Exception
	{
    	service.addWallImage(imagename,id);
	}
	public void updateWallImage(String id,String imagename) throws Exception
	{
		service.updateWallImage(id,imagename);
	}
	public void updateWallImageShow(String id,String showflag) throws Exception
	{
		service.updateWallImageShow(id,showflag);
	}
	public String deleteWallImage(String id) throws Exception
	{
		return service.deleteWallImage(id);
	}
	public void updateImageShowModel(String model) throws Exception
	{
		service.updateImageShowModel(model);
	}
	public String getImageShowModel() throws Exception
	{
		return service.getImageShowModel();
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