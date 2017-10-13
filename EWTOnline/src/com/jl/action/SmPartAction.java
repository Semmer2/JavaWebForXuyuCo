package com.jl.action;
import com.jl.service.*;
import com.jl.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.PageContext;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.*;
public class SmPartAction {
	private SmPartService service=null;
	private HttpServletRequest request=null;
    public SmPartAction(HttpServletRequest request)
    {    	
    	service=new SmPartService(request);
    	this.request=request;
    }
    public Object doing(String method) throws Exception
    {
    	Object result="";    	
    	String currentuserid=this.request.getSession().getAttribute("currentuserid").toString();
    	if(method.equalsIgnoreCase("new") || method.equalsIgnoreCase("edit"))
		{    			
			String partid=URLDecoder.decode(StringUtils.getParameter("partid",request),"UTF-8");
			String vccode=URLDecoder.decode(StringUtils.getParameter("vccode",request),"UTF-8");
			String vccname=URLDecoder.decode(StringUtils.getParameter("vccname",request),"UTF-8");
			String vcename=URLDecoder.decode(StringUtils.getParameter("vcename",request),"UTF-8");
			String icatalog=URLDecoder.decode(StringUtils.getParameter("icatalog",request),"UTF-8");
			String isaleflag=URLDecoder.decode(StringUtils.getParameter("isaleflag",request),"UTF-8");
			String vccnote=URLDecoder.decode(StringUtils.getParameter("vccnote",request),"UTF-8");
			String vcenote=URLDecoder.decode(StringUtils.getParameter("vcenote",request),"UTF-8");
			String vclength=URLDecoder.decode(StringUtils.getParameter("vclength",request),"UTF-8");
			String vcwidth=URLDecoder.decode(StringUtils.getParameter("vcwidth",request),"UTF-8");
			String vcheight=URLDecoder.decode(StringUtils.getParameter("vcheight",request),"UTF-8");
			String vcweight=URLDecoder.decode(StringUtils.getParameter("vcweith",request),"UTF-8");
			String vcposition=URLDecoder.decode(StringUtils.getParameter("vcposition",request),"UTF-8");
			String changepartvccode=URLDecoder.decode(StringUtils.getParameter("changepartvccode",request),"UTF-8");
			String vcmemo=URLDecoder.decode(StringUtils.getParameter("vcmemo",request),"UTF-8");
			String changeflag=URLDecoder.decode(StringUtils.getParameter("changeflag",request),"UTF-8");
			String priceuserid=URLDecoder.decode(StringUtils.getParameter("priceuserid",request),"UTF-8");
			String price=URLDecoder.decode(StringUtils.getParameter("price",request),"UTF-8");
			if(method.equalsIgnoreCase("new"))
			{
				String vcvolume=vclength;					
				String iphotoflag="0";					
				try {
					result = createSmPart(vccode.toUpperCase(), vccname.toUpperCase(), vcename.toUpperCase(),vccnote, vcenote, icatalog, iphotoflag, 
					vclength, vcwidth, vcheight, vcweight, vcvolume, vcposition, isaleflag,priceuserid,price);
				} catch (Exception e) {
					e.printStackTrace();
				}					
			}
			if(method.equalsIgnoreCase("edit"))
			{			
				String vcvolume=vclength;					
				String iphotoflag="0";					
				try {
					result = saveSmpart(partid, vccode.toUpperCase(), vccname.toUpperCase(), vcename.toUpperCase(), vccnote, vcenote, icatalog, iphotoflag, vclength, vcwidth, vcheight, vcweight, vcvolume, vcposition, isaleflag, changepartvccode, vcmemo, changeflag, currentuserid,priceuserid,price);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}    			
		}
		if(method.equalsIgnoreCase("query"))
		{
			String querypartcode=URLDecoder.decode(StringUtils.getParameter("querypartcode",request),"UTF-8");
			try {
				List ls=getSmparts(querypartcode.toUpperCase());
				result=ls;
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		if(method.equalsIgnoreCase("querybyvehid"))
		{
			String vehid=request.getParameter("vehid");
			try {
				List ls=getSmpartsByVehId(vehid);
				result=ls;
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if(method.equalsIgnoreCase("initpriceuser"))
		{			
			try
			{
				UserAction useraction=new UserAction(request);
				List ls=useraction.getUsers();
				result=ls;
			}
			catch (Exception e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		
		if(method.equalsIgnoreCase("queryprice"))
		{			
			try
			{
				String priceuserid=request.getParameter("priceuserid");
				String smpartvccode=request.getParameter("smpartvccode");
				String price=getUserPartPrice(priceuserid,smpartvccode.toUpperCase());
				result=price;
			}
			catch (Exception e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if(method.equalsIgnoreCase("querybyhot"))
		{
			String vehid=request.getParameter("vehid");
			String hotid=request.getParameter("hotid");
			String imageid=request.getParameter("imageid");
			try {
				List ls=getSmparts(vehid,hotid,imageid);
				result=ls;
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return result;
    }
    public String createSmPart(String vccode, String vccname, String vcename, 
			String vccnote, String vcenote, String icatalog, String iphotoflag, 
			String vclength, String vcwidth, String vcheight, String vcweight, 
			String vcvolume, String vcposition, String isaleflag,String priceuserid,String price) throws Exception {
		return service.createSmPart(vccode, vccname, vcename, 
				vccnote, vcenote, icatalog, iphotoflag, 
				vclength, vcwidth, vcheight, vcweight, 
				vcvolume, vcposition, isaleflag,priceuserid,price);
	}
	public String saveSmpart(String id, String vccode, String vccname, String vcename, 
			String vccnote, String vcenote, String icatalog, String iphotoflag, 
			String vclength, String vcwidth, String vcheight, String vcweight, 
			String vcvolume, String vcposition, String isaleflag,String changepartvccode,
			String vcmemo,String changeflag,String currentuserid,String priceuserid,String price) throws Exception {
		{
			return service.saveSmpart(id, vccode, vccname, vcename, 
					vccnote, vcenote, icatalog, iphotoflag, 
					vclength, vcwidth, vcheight, vcweight, 
					vcvolume, vcposition, isaleflag,changepartvccode,
					vcmemo,changeflag,currentuserid,priceuserid,price);
		}
	}
	public List getSmparts() throws Exception 
	{
		return service.getSmparts();
	}
	public List getSmpartQuery(String vccode) throws Exception {
		return service.getSmpartQuery(vccode.toUpperCase());
	}
	public List getSmparts(String querypartcode) throws Exception {
		return service.getSmparts(querypartcode.toUpperCase());
	}
	public List getSmpartchangelog(String querypartvccode) throws Exception {
		return service.getSmpartchangelog(querypartvccode.toUpperCase());
	}
	public List getSmparts(String partid,String partdesc) throws Exception
	{
		return service.getSmparts(partid,partdesc);
	}
	public List getSmparts(String vehid,String ihot,String imageid) throws Exception {
		return service.getSmparts(vehid,ihot,imageid);
	}
	public List getSmpartByQuery(String vehid,String pn,String pd) throws Exception {
		return service.getSmpartByQuery(vehid,pn,pd);
	}
	public List getSmpartsbyImageId(String vehid,String imageid) throws Exception {
		return service.getSmpartsbyImageId(vehid,imageid);
	}
	public List getSmpartsByVehId(String vehid) throws Exception {
		return service.getSmpartsByVehId(vehid);
	}
	public String getUserPartPrice(String priceuserid,String vccode) throws Exception {
		return service.getUserPartPrice(priceuserid,vccode);
	}
	public List AdvanceQueryPart(String pn,String pd,String vin,String sap,String userid,String year,String sys,String subsys,String series,String model) throws Exception 
	{		
		return service.AdvanceQueryPart(pn.toUpperCase(),pd.toUpperCase(),vin.toUpperCase(),sap.toUpperCase(),userid,year,sys,subsys,series,model);
	}
	public List getpartprice(String username,String partvccode) throws Exception
	{
		return service.getpartprice(username,partvccode.toUpperCase());
	}
	public void setpartprice(String username,String partvccode,String price) throws Exception
	{
		service.setpartprice(username,partvccode.toUpperCase(),price);
	}
}
