package com.jl.service;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.PageContext;
import com.jl.dao.*;

public class SmPartService {
	private SmPartDba dba=null;	
	public SmPartService(HttpServletRequest request)
	{		
		dba=new SmPartDba(request);
	}
	public String createSmPart(String vccode, String vccname, String vcename, 
			String vccnote, String vcenote, String icatalog, String iphotoflag, 
			String vclength, String vcwidth, String vcheight, String vcweight, 
			String vcvolume, String vcposition, String isaleflag,String priceuserid,String price) throws Exception {
		return dba.createSmPart( vccode, vccname, vcename, 
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
			return dba.saveSmpart(id, vccode, vccname, vcename, 
					vccnote, vcenote, icatalog, iphotoflag, 
					vclength, vcwidth, vcheight, vcweight, 
					vcvolume, vcposition, isaleflag,changepartvccode,
					vcmemo,changeflag,currentuserid,priceuserid,price);
		}
	}
	public List getSmparts() throws Exception 
	{
		return dba.getSmparts();
	}
	public List getSmparts(String querypartcode) throws Exception {
		return dba.getSmparts(querypartcode);
	}
	public List getSmpartQuery(String vccode) throws Exception {
		return dba.getSmpartQuery(vccode);
	}
	public List getSmpartchangelog(String vccode) throws Exception {
		return dba.getSmpartchangelog(vccode);
	}
	public List getSmparts(String partid,String partdesc) throws Exception
	{
		return dba.getSmparts(partid,partdesc);
	}
	public List getSmparts(String vehid,String ihot,String imageid) throws Exception {
		return dba.getSmparts(vehid,ihot,imageid);
	}
	public List getSmpartByQuery(String vehid,String pn,String pd) throws Exception {
		return dba.getSmpartByQuery(vehid,pn,pd);
	}
	
	public List getSmpartsbyImageId(String vehid,String imageid) throws Exception {
		return dba.getSmpartsbyImageId(vehid,imageid);
	}
	public List getpartprice(String username,String partvccode) throws Exception
	{
		return dba.getpartprice(username,partvccode);
	}
	public List getSmpartsByVehId(String vehid) throws Exception {
		return dba.getSmpartsByVehId(vehid);
	}
	public String getUserPartPrice(String priceuserid,String vccode) throws Exception {
		return dba.getUserPartPrice(priceuserid,vccode);
	}
	public List AdvanceQueryPart(String pn,String pd,String vin,String sap,String userid,String year,String sys,String subsys,String series,String model) throws Exception 
	{
		return dba.AdvanceQueryPart(pn,pd,vin,sap,userid,year,sys,subsys,series,model);
	}
	public void setpartprice(String username,String partvccode,String price) throws Exception
	{
		dba.setpartprice(username,partvccode,price);
	}
}
