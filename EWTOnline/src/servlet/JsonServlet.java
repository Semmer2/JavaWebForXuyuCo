package servlet;

import java.io.IOException;
import java.net.URLDecoder;
//import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import javax.servlet.http.Cookie;

import dao.CustDba;
import dao.VehDba;
import dao.UserDba;
import dao.ImportDba;

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
    		String username = request.getParameter("username");
    		String password = request.getParameter("password");
    		int flag = Integer.parseInt(request.getParameter("flag").toString());
    		int ret = -1;
    		CustDba dba = new CustDba(request);
    		try{ ret = dba.login(username.toUpperCase(),password.toUpperCase()); }
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
    	
    	else if(act.equalsIgnoreCase("querySeries"))
    	{
    		List ret = null;
    		VehDba dba=new VehDba(request);
    		try{ ret=dba.getSeries(); }
    		catch(Exception e){ e.printStackTrace(); }
    		finally	{ dba=null; }
    		outjsonarray(ret,response);
    	}
    	
    	else if(act.equalsIgnoreCase("queryOption"))
    	{
    		String type=request.getParameter("type");
			String vin=request.getParameter("vin");
			String brandid=request.getParameter("brandid");
			List ret = null;
    		VehDba dba=new VehDba(request);
    		try{ ret=dba.getOption(type,vin,brandid); }
    		catch(Exception e){ e.printStackTrace(); }
    		finally	{ dba=null; }
      		outjsonarray(ret,response);
    	}
    	
    	else if(act.equalsIgnoreCase("changeUser"))
    	{
    		String id=request.getParameter("id");
    		String type=request.getParameter("type");
			int ret = -1;
    		CustDba dba=new CustDba(request);
    		try{ ret=dba.changeUser(id,type);}
    		catch(Exception e){ e.printStackTrace(); }
    		finally	{ dba=null; }
    		response.getWriter().print(ret);
    	}
    	
    	else if(act.equalsIgnoreCase("addCart"))
    	{
    		String partid=request.getParameter("partid");
    		String qty=request.getParameter("qty");
    		int ret = -1;
    		CustDba dba=new CustDba(request);
    		try{ ret=dba.addCart(partid,qty);}
    		catch(Exception e){ e.printStackTrace(); }
    		finally	{ dba=null; }
    		response.getWriter().print(ret);
    	}
    	
    	else if(act.equalsIgnoreCase("deleteCart"))
    	{
    		String partid=request.getParameter("partid");
    		int ret = -1;
    		CustDba dba=new CustDba(request);
    		try{ ret=dba.deleteCart(partid);}
    		catch(Exception e){ e.printStackTrace(); }
    		finally	{ dba=null; }
    		response.getWriter().print(ret);
    	}
    	
    	else if(act.equalsIgnoreCase("emptyCart"))
    	{
    		int ret = -1;
    		CustDba dba=new CustDba(request);
    		try{ ret=dba.emptyCart();}
    		catch(Exception e){ e.printStackTrace(); }
    		finally	{ dba=null; }
    		response.getWriter().print(ret);
    	}
    	
    	else if(act.equalsIgnoreCase("updateCartQty"))
    	{
    		String partid=request.getParameter("partid");
    		String qty=request.getParameter("qty");		
    		int ret = -1;
    		CustDba dba=new CustDba(request);
    		try{ ret=dba.updateCartQty(partid,qty);}
    		catch(Exception e){ e.printStackTrace(); }
    		finally	{ dba=null; }
    		response.getWriter().print(ret);
    	}
    	
    	else if(act.equalsIgnoreCase("deleteSuggest"))
    	{
    		String id=request.getParameter("id");
    		int ret = -1;
    		CustDba dba=new CustDba(request);
    		try{ ret=dba.deleteSuggest(id);}
    		catch(Exception e){ e.printStackTrace(); }
    		finally	{ dba=null; }
    		response.getWriter().print(ret);
    	}
    	
    	else if(act.equalsIgnoreCase("getVinCount"))
    	{
    		String vehid=request.getParameter("vehid");
    		String ret = null;
    		VehDba dba=new VehDba(request);
    		try{ ret=dba.getVinCount(vehid); }
    		catch(Exception e){ e.printStackTrace(); }
    		finally	{ dba=null; }
    		outjsonstring(ret,response);
    	}
    	
    	else if(act.equalsIgnoreCase("setBulletinRead"))
    	{    		
    		String id=request.getParameter("id");
    		int ret = -1;
    		CustDba dba=new CustDba(request);
    		try{ ret=dba.setBulletinRead(id);}
    		catch(Exception e){ e.printStackTrace(); }
    		finally	{ dba=null; }
    		response.getWriter().print(ret);
    	}
    	
    	else if(act.equalsIgnoreCase("queryHot"))
    	{
    		String id=request.getParameter("id");
			List ret = null;
			CustDba dba=new CustDba(request);
    		try{ ret=dba.getHot(id); }
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
    		VehDba dba=new VehDba(request);
    		try{ ret=dba.getSearchOption(type,p1,p2); }
    		catch(Exception e){ e.printStackTrace(); }
    		finally	{ dba=null; }
      		outjsonarray(ret,response);
    	}
    	
    	else if(act.equalsIgnoreCase("saveItem"))
    	{
    		int mode = Integer.parseInt(request.getParameter("mode"));
    		String id = request.getParameter("id");
    		int num = Integer.parseInt(request.getParameter("num"));
    		String sd[] = new String[num];
    		for(int i=0; i<num; i++)
    		{
    			sd[i] = request.getParameter("d"+i);
    			if(sd[i]!=null && !sd[i].isEmpty()) sd[i] = URLDecoder.decode(sd[i],"UTF-8");
    		}
    		int ret = -1;
    		UserDba dba=new UserDba(request);
    		try{ ret=dba.saveItem(mode,id,sd,num); }
    		catch(Exception e){ e.printStackTrace(); }
    		finally	{ dba=null; sd = null; } 
    		response.getWriter().print(ret);
    	}
    	
    	else if(act.equalsIgnoreCase("deleteItem"))
    	{
    		int mode = Integer.parseInt(request.getParameter("mode"));
    		String id=request.getParameter("id");
    		int ret = -1;
    		UserDba dba=new UserDba(request);
    		try{ ret=dba.deleteItem(mode,id);}
    		catch(Exception e){ e.printStackTrace(); }
    		finally	{ dba=null; }
    		response.getWriter().print(ret);
    	}
    	
    	else if(act.equalsIgnoreCase("queryMSearchOption"))
    	{
      		String type=request.getParameter("type");
			String p1=request.getParameter("p1");
			String p2=request.getParameter("p2");
			if(p1==null)p1="";
			if(p2==null)p2="";
			List ret = null;
    		VehDba dba=new VehDba(request);
    		try{ ret=dba.getMSearchOption(type,p1,p2); }
    		catch(Exception e){ e.printStackTrace(); }
    		finally	{ dba=null; }
      		outjsonarray(ret,response);
    	}
    	else if(act.equalsIgnoreCase("setCurWall"))
    	{
    		String id=request.getParameter("id");
    		int ret = -1;
    		UserDba dba=new UserDba(request);
    		try{ ret=dba.setCurWall(id); }
    		catch(Exception e){ e.printStackTrace(); }
    		finally	{ dba=null; } 
    		response.getWriter().print(ret);
    	}
    	else if(act.equalsIgnoreCase("getSuggestContent"))
    	{
    		String id=request.getParameter("id");
    		String ret = "";
    		UserDba dba=new UserDba(request);
    		try{ ret=dba.getSuggestContent(id); }
    		catch(Exception e){ e.printStackTrace(); }
    		finally	{ dba=null; }
    		outjsonstring(ret,response);
    	}
    	
    	else if(act.equalsIgnoreCase("setSuggestDeal"))
    	{
    		String id=request.getParameter("id");
    		int ret = -1;
    		UserDba dba=new UserDba(request);
    		try{ ret=dba.setSuggestDeal(id);}
    		catch(Exception e){ e.printStackTrace(); }
    		finally	{ dba=null; }
    		response.getWriter().print(ret);
    	}
    	
    	else if(act.equalsIgnoreCase("saveRight"))
    	{
    		String d=request.getParameter("d");		
    		int ret = -1;
    		UserDba dba=new UserDba(request);
    		try{ ret=dba.saveRight(d);}
    		catch(Exception e){ e.printStackTrace(); }
    		finally	{ dba=null; }
    		response.getWriter().print(ret);
    	}
    	
    	else if(act.equalsIgnoreCase("downSale"))
    	{
    		String flag=request.getParameter("flag");
    		String ids=request.getParameter("ids");
    		int ret = -1;
    		UserDba dba=new UserDba(request);
    		try{ ret=dba.downSale(flag,ids); }
    		catch(Exception e){ e.printStackTrace(); }
    		finally	{ dba=null; } 
    		response.getWriter().print(ret);
    	}
    	
    	else if(act.equalsIgnoreCase("addSaleOrder"))
    	{
    		String partid=request.getParameter("partid");
    		String qty=request.getParameter("qty");
    		int ret = -1;
    		CustDba dba=new CustDba(request);
    		try{ ret=dba.addSaleOrder(partid,qty);}
    		catch(Exception e){ e.printStackTrace(); }
    		finally	{ dba=null; }
    		response.getWriter().print(ret);
    	}
    	
    	else if(act.equalsIgnoreCase("updateOrderQty1"))
    	{
    		String id=request.getParameter("id");
    		String qty=request.getParameter("qty");		
    		int ret = -1;
    		CustDba dba=new CustDba(request);
    		try{ ret=dba.updateOrderQty1(id,qty);}
    		catch(Exception e){ e.printStackTrace(); }
    		finally	{ dba=null; }
    		response.getWriter().print(ret);
    	}
    	
    	else if(act.equalsIgnoreCase("updateOrderQty2"))
    	{
    		String id=request.getParameter("id");
    		String qty=request.getParameter("qty");		
    		int ret = -1;
    		CustDba dba=new CustDba(request);
    		try{ ret=dba.updateOrderQty2(id,qty);}
    		catch(Exception e){ e.printStackTrace(); }
    		finally	{ dba=null; }
    		response.getWriter().print(ret);
    	}
    	
    	else if(act.equalsIgnoreCase("deleteOrder"))
    	{
    		String id=request.getParameter("id");
    		int ret = -1;
    		CustDba dba=new CustDba(request);
    		try{ ret=dba.deleteOrder(id);}
    		catch(Exception e){ e.printStackTrace(); }
    		finally	{ dba=null; }
    		response.getWriter().print(ret);
    	}
    }
    
    protected void doPost(HttpServletRequest request,HttpServletResponse response) throws ServletException, IOException
    { 
    	String act = request.getParameter("action");
  
    	if(act.equalsIgnoreCase("saveSuggest"))
    	{
    		String id=request.getParameter("id");
    		String vehid=request.getParameter("vehid");
    		String imageid=request.getParameter("imageid");
    		String content=request.getParameter("content");
    		int ret = -1;
    		CustDba dba=new CustDba(request);
    		try{ ret=dba.saveSuggest(id,vehid,imageid,content); }
    		catch(Exception e){ e.printStackTrace(); }
    		finally	{ dba=null; }
    		response.getWriter().print(ret);
    	}
    	
    	else if(act.equalsIgnoreCase("getIDs"))
    	{
    		String s = request.getParameter("dl");
    		s = URLDecoder.decode(s, "UTF-8");
    		List ret = null;
    		ImportDba dba=new ImportDba();
    		try{ ret=dba.getIDs(s); }
    		catch(Exception e){ e.printStackTrace(); }
    		finally	{ dba=null; }
      		outjsonarray(ret,response);    		
    	}
    	
    	else if(act.equalsIgnoreCase("importAsmBom"))
    	{
    		String s = request.getParameter("dl");
    		List ret = null;
    		ImportDba dba=new ImportDba();
    		try{ ret=dba.importAsmBom(s); }
    		catch(Exception e){ e.printStackTrace(); }
    		finally	{ dba=null; }
      		outjsonarray(ret,response);    		
    	}
    	
    	else if(act.equalsIgnoreCase("queryAsmBom"))
    	{
    		String lines=request.getParameter("dl");
			List ret = null;
			ImportDba dba=new ImportDba();
    		try{ ret=dba.queryAsmBom(lines); }
    		catch(Exception e){ e.printStackTrace(); }
    		finally	{ dba=null; }
      		outjsonarray(ret,response);
    	}
    	
    	else if(act.equalsIgnoreCase("queryVehBom"))
    	{
    		String s=request.getParameter("dl");
    		int k = s.indexOf('|');
			List ret = null;
			ImportDba dba=new ImportDba();
    		try{ ret=dba.queryVehBom(s.substring(0,k),s.substring(k+1)); }
    		catch(Exception e){ e.printStackTrace(); }
    		finally	{ dba=null; }
      		outjsonarray(ret,response);
    	}
    	
    	else if(act.equalsIgnoreCase("importVehBom"))
    	{
    		String s = request.getParameter("dl");
    		List ret = null;
    		ImportDba dba=new ImportDba();
    		try{ ret=dba.importVehBom(s); }
    		catch(Exception e){ e.printStackTrace(); }
    		finally	{ dba=null; }
      		outjsonarray(ret,response);    		
    	}
    	
    	else if(act.equalsIgnoreCase("queryVin"))
    	{
    		String s = request.getParameter("dl");
    		List ret = null;
    		ImportDba dba=new ImportDba();
    		try{ ret=dba.queryVin(s); }
    		catch(Exception e){ e.printStackTrace(); }
    		finally	{ dba=null; }
      		outjsonarray(ret,response);    		
    	}
    	
    	else if(act.equalsIgnoreCase("importVin"))
    	{
    		String s = request.getParameter("dl");
    		List ret = null;
    		ImportDba dba=new ImportDba();
    		try{ ret=dba.importVin(s); }
    		catch(Exception e){ e.printStackTrace(); }
    		finally	{ dba=null; }
      		outjsonarray(ret,response);    		
    	}
    	
    	else if(act.equalsIgnoreCase("queryVeh"))
    	{
    		String s = request.getParameter("dl");
    		List ret = null;
    		ImportDba dba=new ImportDba();
    		try{ ret=dba.queryVeh(s); }
    		catch(Exception e){ e.printStackTrace(); }
    		finally	{ dba=null; }
      		outjsonarray(ret,response);    		
    	}
    	
    	else if(act.equalsIgnoreCase("importVeh"))
    	{
    		String s = request.getParameter("dl");
    		List ret = null;
    		ImportDba dba=new ImportDba();
    		try{ ret=dba.importVeh(s); }
    		catch(Exception e){ e.printStackTrace(); }
    		finally	{ dba=null; }
      		outjsonarray(ret,response);    		
    	}
    	
    	else if(act.equalsIgnoreCase("queryPart"))
    	{
    		String s = request.getParameter("dl");
    		List ret = null;
    		ImportDba dba=new ImportDba();
    		try{ ret=dba.queryPart(s); }
    		catch(Exception e){ e.printStackTrace(); }
    		finally	{ dba=null; }
      		outjsonarray(ret,response);    		
    	}
    	
    	else if(act.equalsIgnoreCase("importPart"))
    	{
    		String s = request.getParameter("dl");
    		List ret = null;
    		ImportDba dba=new ImportDba();
    		try{ ret=dba.importPart(s); }
    		catch(Exception e){ e.printStackTrace(); }
    		finally	{ dba=null; }
      		outjsonarray(ret,response);    		
    	}
    	
    	else if(act.equalsIgnoreCase("queryPrice"))
    	{
    		boolean bDefault = false;
    		String s=request.getParameter("m");
    		if(s!=null)bDefault = true;
    		s=request.getParameter("dl");
    		int k = s.indexOf('|');
			List ret = null;
			ImportDba dba=new ImportDba();
    		try{ ret=dba.queryPrice(bDefault,s.substring(0,k),s.substring(k+1)); }
    		catch(Exception e){ e.printStackTrace(); }
    		finally	{ dba=null; }
      		outjsonarray(ret,response);
    	}
    	
    	else if(act.equalsIgnoreCase("importPrice"))
    	{
    		String s = request.getParameter("dl");
    		List ret = null;
    		ImportDba dba=new ImportDba();
    		try{ ret=dba.importPrice(s); }
    		catch(Exception e){ e.printStackTrace(); }
    		finally	{ dba=null; }
      		outjsonarray(ret,response);    		
    	}
    	
    	else if(act.equalsIgnoreCase("querySupersed"))
    	{
    		String s = request.getParameter("dl");
    		List ret = null;
    		ImportDba dba=new ImportDba();
    		try{ ret=dba.querySupersed(s); }
    		catch(Exception e){ e.printStackTrace(); }
    		finally	{ dba=null; }
      		outjsonarray(ret,response);    		
    	}
    	
    	else if(act.equalsIgnoreCase("importSupersed"))
    	{
    		String s = request.getParameter("dl");
    		String userid=(String)request.getSession().getAttribute("userid");
    		List ret = null;
    		ImportDba dba=new ImportDba();
    		try{ ret=dba.importSupersed(s,userid); }
    		catch(Exception e){ e.printStackTrace(); }
    		finally	{ dba=null; }
      		outjsonarray(ret,response);    		
    	}
    	
    	else if(act.equalsIgnoreCase("querySale"))
    	{
    		String s = request.getParameter("dl");
    		List ret = null;
    		ImportDba dba=new ImportDba();
    		try{ ret=dba.querySale(s); }
    		catch(Exception e){ e.printStackTrace(); }
    		finally	{ dba=null; }
      		outjsonarray(ret,response);    		
    	}
    	
    	else if(act.equalsIgnoreCase("importSale"))
    	{
    		String s = request.getParameter("dl");
    		List ret = null;
    		ImportDba dba=new ImportDba();
    		try{ ret=dba.importSale(s); }
    		catch(Exception e){ e.printStackTrace(); }
    		finally	{ dba=null; }
      		outjsonarray(ret,response);   		
    	}
   
    }
}