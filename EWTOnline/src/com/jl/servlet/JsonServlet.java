package com.jl.servlet;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import javax.servlet.http.Cookie;

import com.jl.dao.*;
import com.jl.util.StringUtils;

public class JsonServlet extends HttpServlet
{  
    private static final long serialVersionUID = 1L;
    public JsonServlet()
    {  
        super();
    }
    
    private void outjsonstring(String s,HttpServletResponse response) throws IOException
    {    	
    	if(s == null)return;
        JSONObject json = new JSONObject();
        Hashtable ht = new Hashtable();
        ht.put("result", s);
        //if(s.equalsIgnoreCase("") || s.equalsIgnoreCase("-1")) ht.put("error",request.getAttribute("error").toString());
        json.putAll(ht);
        response.getWriter().print(json.toString());
    }
    
    private void outjsonarray(List ls,HttpServletResponse response) throws IOException
    {
    	if(ls == null) return;
    	JSONArray ary = new JSONArray();			
		ary.addAll(ls);
		response.getWriter().print(ary.toString());
    }
 	
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
    	String act = request.getParameter("action"); 		
    	
    	if(act.equalsIgnoreCase("login"))
    	{
    		String username = request.getParameter("username").toUpperCase();
    		String password = request.getParameter("password").toUpperCase();
    		int flag = Integer.parseInt(request.getParameter("flag").toString());
    		int ret = -1;
    		CustDba dba = new CustDba(request);
    		try{ ret = dba.login(username,password); }
    		catch(Exception e){ e.printStackTrace(); }
    		finally	{ dba = null; }
    		if(ret == 0)
    		{
    			if((flag&8) > 0)
    			{
    				Cookie mycook = new Cookie("ewtcook",username+","+password);  
    				mycook.setMaxAge(3600*24*30);  
    				response.addCookie(mycook);
    			}
    			else if((flag&4) > 0)
    			{
    				Cookie mycook = new Cookie("ewtcook",null);  
    				mycook.setMaxAge(0);  
    				response.addCookie(mycook);
    			}
    		}
    		response.getWriter().print(ret);
    	}
    	
    	else if(act.equalsIgnoreCase("queryOption"))
    	{
    		String type=request.getParameter("type");
			String vin=request.getParameter("vin");
			String seriesid=request.getParameter("seriesid");
			List ret = null;
    		CustDba dba=new CustDba(request);
    		try{ ret=dba.getOption(type,vin,seriesid); }
    		catch(Exception e){ e.printStackTrace(); }
    		finally	{ dba=null; }
      		outjsonarray(ret,response);
    	}
    	
    	/////////////////////////////////
    	
    	else if(act.equalsIgnoreCase("queryOptionEx"))
    	{
    		String type=request.getParameter("type");
			String vin=request.getParameter("vin");
			String seriesid=request.getParameter("seriesid");
			List ret = null;
    		UserDba dba=new UserDba(request);
    		try{ ret=dba.getOption(type,vin,seriesid); }
    		catch(Exception e){ e.printStackTrace(); }
    		finally	{ dba=null; }
      		outjsonarray(ret,response);
    	}
    	
    	else if(act.equalsIgnoreCase("querySearchOption"))
    	{
    		String type=request.getParameter("type");
			String p1=request.getParameter("p1");
			String p2=request.getParameter("p2");
			if(p1==null)p1="";
			if(p2==null)p2="";
			List ret = null;
    		UserDba dba=new UserDba(request);
    		try{ ret=dba.getSearchOption(type,p1,p2); }
    		catch(Exception e){ e.printStackTrace(); }
    		finally	{ dba=null; }
      		outjsonarray(ret,response);
    	}
    	
    	else if(act.equalsIgnoreCase("deleteCart"))
    	{
    		String partid=request.getParameter("partid");
    		String ret = "fail";
    		UserDba dba=new UserDba(request);
    		try{ if(dba.deleteCart(partid))ret="success";}
    		catch(Exception e){ e.printStackTrace(); }
    		finally	{ dba=null; }
    		outjsonstring(ret,response);
    	}
    	
    	else if(act.equalsIgnoreCase("emptyCart"))
    	{
    		String ret = "fail";
    		UserDba dba=new UserDba(request);
    		try{ if(dba.emptyCart())ret="success";}
    		catch(Exception e){ e.printStackTrace(); }
    		finally	{ dba=null; }
    		outjsonstring(ret,response);
    	}
    	
    	else if(act.equalsIgnoreCase("updateCartQty"))
    	{
    		String partid=request.getParameter("partid");
    		String qty=request.getParameter("qty");
    		String ret = "";
    		UserDba dba=new UserDba(request);
    		try{ if(dba.updateCartQty(partid,qty))ret="success";}
    		catch(Exception e){ e.printStackTrace(); }
    		finally	{ dba=null; }
    		outjsonstring(ret,response);
    	}
    	
    	else if(act.equalsIgnoreCase("addCart"))
    	{
    		String partid=request.getParameter("partid");
    		String qty=request.getParameter("qty");
    		String ret = "fail";
    		UserDba dba=new UserDba(request);
    		try{ if(dba.addCart(partid,qty))ret="success";}
    		catch(Exception e){ e.printStackTrace(); }
    		finally	{ dba=null; }
    		outjsonstring(ret,response);
    	}
    	
    	else if(act.equalsIgnoreCase("saveSuggest"))
    	{
    		String id=request.getParameter("id");
    		String vehid=request.getParameter("vehid");
    		String imageid=request.getParameter("imageid");
    		String content=request.getParameter("content");
    		String ret = "";
    		UserDba dba=new UserDba(request);
    		try{ ret=dba.saveSuggest(id,vehid,imageid,content)?"success":"fail"; }
    		catch(Exception e){ e.printStackTrace(); }
    		finally	{ dba=null; }
    		outjsonstring(ret,response);
    	}
    	
    	else if(act.equalsIgnoreCase("queryHot"))
    	{
    		String id=request.getParameter("id");
			List ret = null;
			UserDba dba=new UserDba(request);
    		try{ ret=dba.getHot(id); }
    		catch(Exception e){ e.printStackTrace(); }
    		finally	{ dba=null; }
      		outjsonarray(ret,response);
    	}
    	
    	else if(act.equalsIgnoreCase("deleteSuggest"))
    	{
    		String id=request.getParameter("id");
    		String ret = "fail";
    		UserDba dba=new UserDba(request);
    		try{ if(dba.deleteSuggest(id))ret="success";}
    		catch(Exception e){ e.printStackTrace(); }
    		finally	{ dba=null; }
    		outjsonstring(ret,response);
    	}
    	
    	else if(act.equalsIgnoreCase("queryMSearchOption"))
    	{
    		String type=request.getParameter("type");
			String p1=request.getParameter("p1");
			String p2=request.getParameter("p2");
			if(p1==null)p1="";
			if(p2==null)p2="";
			List ret = null;
			NotifyDba dba=new NotifyDba(request);
    		try{ ret=dba.getSearchOption(type,p1,p2); }
    		catch(Exception e){ e.printStackTrace(); }
    		finally	{ dba=null; }
      		outjsonarray(ret,response);
    	}
    	
    	else if(act.equalsIgnoreCase("queryPart"))
    	{
    		String code=request.getParameter("code");
			List ret = null;
			NotifyDba dba=new NotifyDba(request);
    		try{ ret=dba.getPart(code); }
    		catch(Exception e){ e.printStackTrace(); }
    		finally	{ dba=null; }
      		outjsonarray(ret,response);
    	}
    	
    	else if(act.equalsIgnoreCase("saveVeh"))
    	{
    		String id=request.getParameter("id");
    		String sd[]=new String[18];
    		for(int i=0;i<18;i++)
    		{
    			sd[i]=StringUtils.getParameter("s"+String.valueOf(i),request);
    			if(!sd[i].isEmpty() && (i==0||i==1||i==4||i==5||i==9||i==12)) sd[i]=URLDecoder.decode(sd[i],"UTF-8");
    		}
    		String ret = null;
    		NotifyDba dba=new NotifyDba(request);
    		try{ ret=dba.saveVeh(id,sd); }
    		catch(Exception e){ e.printStackTrace(); }
    		finally	{ dba=null; }
    		outjsonstring(ret,response);
    	}
    	
    	else if(act.equalsIgnoreCase("saveVin"))
    	{
    		String id=request.getParameter("id");
    		String sd[]=new String[8];
    		for(int i=0;i<8;i++)
    		{
    			sd[i]=StringUtils.getParameter("s"+String.valueOf(i),request);
    			if(!sd[i].isEmpty()) sd[i]=URLDecoder.decode(sd[i],"UTF-8");
    		}
    		String ret = null;
    		NotifyDba dba=new NotifyDba(request);
    		try{ ret=dba.saveVin(id,sd); }
    		catch(Exception e){ e.printStackTrace(); }
    		finally	{ dba=null; }
    		outjsonstring(ret,response);
    	}
    	
    	else if(act.equalsIgnoreCase("saveUser"))
    	{
    		String id=request.getParameter("id");
    		String sd[]=new String[7];
    		for(int i=0;i<7;i++)
    		{
    			sd[i]=StringUtils.getParameter("s"+String.valueOf(i),request);
    			if(!sd[i].isEmpty()) sd[i]=URLDecoder.decode(sd[i],"UTF-8");
    		}
    		String ret = null;
    		NotifyDba dba=new NotifyDba(request);
    		try{ ret=dba.saveUser(id,sd); }
    		catch(Exception e){ e.printStackTrace(); }
    		finally	{ dba=null; }
    		outjsonstring(ret,response);
    	}
    	else if(act.equalsIgnoreCase("getVinCount"))
    	{
    		String vehid=request.getParameter("vehid");
    		String ret = null;
    		CustDba dba=new CustDba(request);
    		try{ ret=dba.getVinCount(vehid); }
    		catch(Exception e){ e.printStackTrace(); }
    		finally	{ dba=null; }
    		outjsonstring(ret,response);
    	}
    	
    	else if(act.equalsIgnoreCase("deleteWall"))
    	{
    		String id=request.getParameter("id");
    		String ret = "fail";
    		NotifyDba dba=new NotifyDba(request);
    		try{ if(dba.deleteWall(id))ret="success";}
    		catch(Exception e){ e.printStackTrace(); }
    		finally	{ dba=null; }
    		outjsonstring(ret,response);
    	}
    	
    	else if(act.equalsIgnoreCase("setCurWall"))
    	{
    		String id=request.getParameter("id");
    		String ret = "fail";
    		NotifyDba dba=new NotifyDba(request);
    		try{ if(dba.setCurWall(id))ret="success";}
    		catch(Exception e){ e.printStackTrace(); }
    		finally	{ dba=null; }
    		outjsonstring(ret,response);
    	}
    	
    	else if(act.equalsIgnoreCase("setBulletinRead"))
    	{
    		String bulletinid = request.getParameter("id");
    		String ret = "";
    		UserDba dba=new UserDba(request);
    		try{ if(dba.setBulletinRead(bulletinid))ret="success";}
    		catch(Exception e){ e.printStackTrace(); }
    		finally	{ dba=null; }
    		outjsonstring(ret,response);
    	}
    	
    	else if(act.equalsIgnoreCase("deleteBulletin"))
    	{
    		String id=request.getParameter("id");
    		String ret = "0";
    		NotifyDba dba=new NotifyDba(request);
    		try{ if(dba.deleteBulletin(id)) ret="1";}
    		catch(Exception e){ e.printStackTrace(); }
    		finally	{ dba=null; }
    		response.getWriter().print(ret);
    	}
    	
    	else if(act.equalsIgnoreCase("setBulletinTop"))
    	{
    		String id=request.getParameter("id");
    		String ret = "0";
    		NotifyDba dba=new NotifyDba(request);
    		try{ if(dba.setBulletinTop(id)) ret="1";}
    		catch(Exception e){ e.printStackTrace(); }
    		finally	{ dba=null; }
    		response.getWriter().print(ret);
    	}
    	
    	else if(act.equalsIgnoreCase("setBulletinRed"))
    	{
    		String id=request.getParameter("id");
    		String ret = "0";
    		NotifyDba dba=new NotifyDba(request);
    		try{ if(dba.setBulletinRed(id))ret="1";}
    		catch(Exception e){ e.printStackTrace(); }
    		finally	{ dba=null; }
    		response.getWriter().print(ret);
    	}
    	
    	else if(act.equalsIgnoreCase("saveBulletinTopic"))
    	{
    		String id=request.getParameter("id");
    		String topic=request.getParameter("topic");
    		topic = URLDecoder.decode(topic, "UTF-8");
    		Object ret = "0";
    		NotifyDba dba=new NotifyDba(request);
    		try{ if(dba.saveBulletinTopic(id,topic))ret="1";}
    		catch(Exception e){ e.printStackTrace(); }
    		finally	{ dba=null; }
    		response.getWriter().print(ret);
    	}
    	
    	else if(act.equalsIgnoreCase("savePart"))
    	{
    		String id = request.getParameter("id");
    		String sd[] = new String[8];
    		for(int i=0; i<8; i++)
    		{
    			sd[i] = StringUtils.getParameter("s"+String.valueOf(i), request);
    			if(!sd[i].isEmpty()) sd[i] = URLDecoder.decode(sd[i], "UTF-8");
    		}
    		String ret = "";
    		NotifyDba dba=new NotifyDba(request);
    		try{ ret=dba.savePart(id, sd); }
    		catch(Exception e){ e.printStackTrace(); }
    		finally	{ dba=null; sd = null; }    		
    		response.getWriter().print(ret);
    	}
    	
    	else if(act.equalsIgnoreCase("getID"))
    	{
    		String mode=request.getParameter("mode");
    		String code=request.getParameter("code");
    		String ret = "0";
			NotifyDba dba=new NotifyDba(request);
    		try{ ret=dba.getID(mode,code); }
    		catch(Exception e){ e.printStackTrace(); }
    		finally	{ dba=null; } 
    		response.getWriter().print(ret);
    	}
    	
    	else if(act.equalsIgnoreCase("saveVehLink"))
    	{
    		String id = request.getParameter("id");
    		String sd[] = new String[6];
    		for(int i=0; i<6; i++)
    		{
    			sd[i] = StringUtils.getParameter("s"+String.valueOf(i), request);
    			if(!sd[i].isEmpty()) sd[i] = URLDecoder.decode(sd[i], "UTF-8");
    		}
    		String ret = "";
    		NotifyDba dba=new NotifyDba(request);
    		try{ ret=dba.saveVehLink(id, sd); }
    		catch(Exception e){ e.printStackTrace(); }
    		finally	{ dba=null; sd = null; }    		
    		response.getWriter().print(ret);
    	}
    	
    	else if(act.equalsIgnoreCase("deleteVehLink"))
    	{
    		String id=request.getParameter("id");
    		String ret = "0";
    		NotifyDba dba=new NotifyDba(request);
    		try{ if(dba.deleteVehLink(id)) ret="1";}
    		catch(Exception e){ e.printStackTrace(); }
    		finally	{ dba=null; }
    		response.getWriter().print(ret);
    	}
    	
    	else if(act.equalsIgnoreCase("saveAsmLink"))
    	{
    		String id = request.getParameter("id");
    		String sd[] = new String[6];
    		for(int i=0; i<6; i++)
    		{
    			sd[i] = StringUtils.getParameter("s"+String.valueOf(i), request);
    			if(!sd[i].isEmpty()) sd[i] = URLDecoder.decode(sd[i], "UTF-8");
    		}
    		String ret = "";
    		NotifyDba dba=new NotifyDba(request);
    		try{ ret=dba.saveAsmLink(id, sd); }
    		catch(Exception e){ e.printStackTrace(); }
    		finally	{ dba=null; sd = null; }    		
    		response.getWriter().print(ret);
    	}
    	
    	else if(act.equalsIgnoreCase("deleteAsmLink"))
    	{
    		String id=request.getParameter("id");
    		String ret = "0";
    		NotifyDba dba=new NotifyDba(request);
    		try{ if(dba.deleteAsmLink(id)) ret="1";}
    		catch(Exception e){ e.printStackTrace(); }
    		finally	{ dba=null; }
    		response.getWriter().print(ret);
    	}
    	
    	else if(act.equalsIgnoreCase("getSuggestContent"))
    	{
    		String id=request.getParameter("id");
    		String ret = "";
    		NotifyDba dba=new NotifyDba(request);
    		try{ ret=dba.getSuggestContent(id); }
    		catch(Exception e){ e.printStackTrace(); }
    		finally	{ dba=null; }
    		outjsonstring(ret,response);
    	}
    	
    	else if(act.equalsIgnoreCase("setSuggestDeal"))
    	{
    		String id=request.getParameter("id");
    		String ret = "0";
    		NotifyDba dba=new NotifyDba(request);
    		try{ if(dba.setSuggestDeal(id))ret="1";}
    		catch(Exception e){ e.printStackTrace(); }
    		finally	{ dba=null; }
    		response.getWriter().print(ret);
    	}
    	
    	else if(act.equalsIgnoreCase("saveImage"))
    	{
    		String id = request.getParameter("id");
    		String sd[] = new String[6];
    		for(int i=0; i<6; i++)
    		{
    			sd[i] = StringUtils.getParameter("s"+String.valueOf(i), request);
    			if(i==5 && !sd[i].isEmpty()) sd[i] = URLDecoder.decode(sd[i], "UTF-8");
    		}
    		String ret = "";
    		NotifyDba dba=new NotifyDba(request);
    		try{ ret=dba.saveImage(id, sd); }
    		catch(Exception e){ e.printStackTrace(); }
    		finally	{ dba=null; sd = null; }		
    		response.getWriter().print(ret);
    	}
    	
    	else if(act.equalsIgnoreCase("saveT"))
    	{
    		String type = request.getParameter("type");
    		String id = request.getParameter("id");
    		String sd[] = new String[3];
    		for(int i=0; i<3; i++)
    		{
    			sd[i] = StringUtils.getParameter("s"+String.valueOf(i), request);
    			if(i==2 && !sd[i].isEmpty()) sd[i] = URLDecoder.decode(sd[i], "UTF-8");
    		}
    		String ret = "";
    		NotifyDba dba=new NotifyDba(request);
    		try{ ret=dba.saveT(type, id, sd); }
    		catch(Exception e){ e.printStackTrace(); }
    		finally	{ dba=null; sd = null; }		
    		response.getWriter().print(ret);
    	}
    	
    	else if(act.equalsIgnoreCase("queryVehBom"))
    	{
    		String vehids=request.getParameter("veh");
    		String lines=request.getParameter("ln");
			List ret = null;
			CommonDba dba=new CommonDba();
    		try{ ret=dba.queryVehBom(vehids, lines); }
    		catch(Exception e){ e.printStackTrace(); }
    		finally	{ dba=null; }
      		outjsonarray(ret,response);
    	}
    	
    	else if(act.equalsIgnoreCase("queryAsmBom"))
    	{
    		String asmids=request.getParameter("asm");
    		String lines=request.getParameter("ln");
			List ret = null;
			CommonDba dba=new CommonDba();
    		try{ ret=dba.queryAsmBom(asmids, lines); }
    		catch(Exception e){ e.printStackTrace(); }
    		finally	{ dba=null; }
      		outjsonarray(ret,response);
    	}
    }
    
    protected void doPost(HttpServletRequest request,HttpServletResponse response) throws ServletException, IOException
    { 
    	String act = request.getParameter("action");
   	
    	if(act.equalsIgnoreCase("querySeries"))
    	{
    		List ret = null;
    		CustDba dba=new CustDba(request);
    		try{ ret=dba.getSeries(); }
    		catch(Exception e){ e.printStackTrace(); }
    		finally	{ dba=null; }
      		outjsonarray(ret,response);    		
    	}
    	
    	else if(act.equalsIgnoreCase("querySeriesEx"))
    	{
    		List ret = null;
    		UserDba dba=new UserDba(request);
    		try{ ret=dba.getSeries(); }
    		catch(Exception e){ e.printStackTrace(); }
    		finally	{ dba=null; }
      		outjsonarray(ret,response);    		
    	}
    	
    	 
    	    
    	/////////////////////////////////////////////
    	
    	else if(act.equalsIgnoreCase("changeUser"))
    	{
    		String id=request.getParameter("id");
    		String type=request.getParameter("type");
    		String name=request.getParameter("name");
    		request.getSession().setAttribute("userdd",id);
    		request.getSession().setAttribute("usertype",type);	
			request.getSession().setAttribute("username",name);
    		outjsonstring("success",response);
    	}
    	  
    	else if(act.equalsIgnoreCase("saveSuggest"))
    	{
    		String id=request.getParameter("id");
    		String vehid=request.getParameter("vehid");
    		String imageid=request.getParameter("imageid");
    		String content=request.getParameter("content");
    		String ret = "fail";
    		UserDba dba=new UserDba(request);
    		try{ if(dba.saveSuggest(id,vehid,imageid,content))ret="success"; }
    		catch(Exception e){ e.printStackTrace(); }
    		finally	{ dba=null; }
    		outjsonstring(ret,response);
    	}
    	
    	else if(act.equalsIgnoreCase("getIDs"))
    	{
    		String s = request.getParameter("dl");
    		s = URLDecoder.decode(s, "UTF-8");
    		List ret = null;
    		CommonDba dba=new CommonDba();
    		try{ ret=dba.getIDs(s); }
    		catch(Exception e){ e.printStackTrace(); }
    		finally	{ dba=null; }
      		outjsonarray(ret,response);    		
    	}
    	
    	else if(act.equalsIgnoreCase("importVehBom"))
    	{
    		String s = request.getParameter("dl");
    		List ret = null;
    		CommonDba dba=new CommonDba();
    		try{ ret=dba.importVehBom(s); }
    		catch(Exception e){ e.printStackTrace(); }
    		finally	{ dba=null; }
      		outjsonarray(ret,response);    		
    	}
    	
    	else if(act.equalsIgnoreCase("importAsmBom"))
    	{
    		String s = request.getParameter("dl");
    		List ret = null;
    		CommonDba dba=new CommonDba();
    		try{ ret=dba.importAsmBom(s); }
    		catch(Exception e){ e.printStackTrace(); }
    		finally	{ dba=null; }
      		outjsonarray(ret,response);    		
    	}
    }
}