package com.jl.action;
import com.jl.service.*;
import com.jl.util.Constants;
import com.jl.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.PageContext;

import java.io.PrintWriter;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.*;
public class PartPhotoAction {
	private PartPhotoService service=null;
	private HttpServletRequest request;
    public PartPhotoAction(HttpServletRequest request)
    {    	
    	this.request=request;
    	service=new PartPhotoService(request);
    }
    private String getUploadTargetFolder(String targetpath)
    {
    	StringBuffer tmpfolder=new StringBuffer(request.getRealPath("/"));
        tmpfolder.append(targetpath);
        return tmpfolder.toString();
    }
    public Object doing(String method) throws Exception
    {
    	Object result="";    	
		if(method.equalsIgnoreCase("viewimage"))
		{
			String querypartid=URLDecoder.decode(StringUtils.getParameter("partid",request),"UTF-8");
			try {
				List ls=getSmPartPhoto(querypartid);				
				result=ls;
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if(method.equalsIgnoreCase("query"))
		{
			String querypartid=URLDecoder.decode(StringUtils.getParameter("partid",request),"UTF-8");
			try {
				List ls=getSmPartPhoto(querypartid);
				List htlist=new ArrayList();
				Hashtable photoht=new Hashtable();
				for(int i=0;i<ls.size();i++)
				{
					Hashtable ht=(Hashtable)ls.get(i);
					String photoid=ht.get("iphotoid").toString();
					String photourl=photoid;
					photoht.put("key"+String.valueOf(i),photourl);
				}
				photoht.put("totalcount",String.valueOf(ls.size()));
				htlist.add(photoht);
				result=htlist;
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if(method.equalsIgnoreCase("del"))
		{
			String delphotoid=URLDecoder.decode(StringUtils.getParameter("photoid",request),"UTF-8");
			String partid=URLDecoder.decode(StringUtils.getParameter("partid",request),"UTF-8");
			try {				
				String rtn=delSmPartPhoto(delphotoid);
				if(rtn.equalsIgnoreCase("0"))
				{
					//ɾ��ͼƬ�ļ�
					String targetpath=Constants.getValue("photo");
					String filename=getUploadTargetFolder(targetpath);
					filename=filename+"\\"+delphotoid+".jpg";					
					java.io.File myFilePath = new java.io.File(filename);
					myFilePath.delete();
					List ls=getSmPartPhoto(partid);
					List htlist=new ArrayList();
					Hashtable photoht=new Hashtable();
					for(int i=0;i<ls.size();i++)
					{
						Hashtable ht=(Hashtable)ls.get(i);
						String photoid=ht.get("iphotoid").toString();
						String photourl=photoid;
						photoht.put("key"+String.valueOf(i),photourl);
					}
					photoht.put("totalcount",String.valueOf(ls.size()));
					htlist.add(photoht);
					result=htlist;
				}
				else
				{
					result="";
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		return result;
    }
    public String createSmPartPhoto(String smpartid) throws Exception {
		return service.createSmPartPhoto(smpartid);
	}    
	public String delSmPartPhoto(String photoid) throws Exception {
		return service.delSmPartPhoto(photoid);
	}
	public List getSmPartPhoto(String partid) throws Exception{
		return service.getSmPartPhoto(partid);
	}
	public List getSmPartPhotoByPartVccode(String partvccode) throws Exception {
		return service.getSmPartPhotoByPartVccode(partvccode);
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
