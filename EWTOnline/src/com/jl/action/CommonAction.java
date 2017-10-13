package com.jl.action;
import com.jl.service.*;
import com.jl.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.PageContext;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.*;
public class CommonAction {
	private CommonService service;
	private HttpServletRequest request=null;
    public CommonAction(HttpServletRequest request)
    {    	
    	service=new CommonService(request);
    	this.request=request;
    }
    public List getSeries(String userid,String vinesn) throws Exception
	{
		if(vinesn==null) vinesn="";
    	return service.getSeries(userid,vinesn.toUpperCase());
	}
    public List getVehicle(String userid,String seriesid,String year,String cabin,String wheelbase,String fuel,String model,String emmission,String drive,String color) throws Exception
	{
		return service.getVehicle(userid,seriesid,year,cabin,wheelbase,fuel,model,emmission,drive,color);
	}
    public List getVehicle(String userid,String vinesn) throws Exception
	{
    	if(vinesn==null) vinesn="";
    	return service.getVehicle(userid,vinesn.toUpperCase());
	}
    public List getVehicle(String vccode) throws Exception
	{
		return service.getVehicle(vccode.toUpperCase());
	}
    public List getImage(String imagevccode) throws Exception
	{
		return service.getImage(imagevccode.toUpperCase());
	}
    public List getVehicleforsearch(String partvccode,String userid) throws Exception
	{
		return service.getVehicleforsearch(partvccode.toUpperCase(),userid);
	}
    
    public List getSearchCondition(String userid,String conditiontype,String vinesn,String seriesid) throws Exception
	{
		return service.getSearchCondition(userid,conditiontype,vinesn.toUpperCase(),seriesid);
	}
    public List getAdvanceSearchCondition(String userid,String conditiontype,String sysvalue) throws Exception
	{		
    	return service.getAdvanceSearchCondition(userid,conditiontype,sysvalue.toUpperCase());
	}
    
    public List getTcvehicle() throws Exception
	{
		return service.getTcvehicle();
	}
    public List getTcvehicle(String vehid) throws Exception
	{
    	return service.getTcvehicle(vehid);
	}
    public List getAsmLink(String partid) throws Exception
	{
		return service.getAsmLink(partid);
	}
    public List queryVehicle(String asmid,String vinesn) throws Exception
	{
		return service.queryVehicle(asmid,vinesn.toUpperCase());
	}
    public List querySeries(String asmid,String vinesn) throws Exception
	{
		return service.querySeries(asmid,vinesn.toUpperCase());
	}
    
    public String getSeriesid(String vehid) throws Exception
    {
    	return service.getSeriesid(vehid);
    }
    public List getDomMenu(String vehid) throws Exception
	{
    	List alldom=service.getDomMenu(vehid);    	
    	return alldom;
	}
    public List getDomMenuByPath(String vehid,String imagepath) throws Exception
	{
    	List ls=service.getDomMenuByPath(vehid,imagepath);    	
    	return ls;
	}
    public String getDomMenuPath(String vehid,String imageid) throws Exception
	{
		return service.getDomMenuPath(vehid,imageid);
	}
    public List getDomMenuByNum(String vehid,String imagenum) throws Exception
	{
		return service.getDomMenuByNum(vehid,imagenum);
	}
    public String getDomMenuPathBySvg(String vehid,String imageid) throws Exception
	{
		return service.getDomMenuPathBySvg(vehid,imageid);
	}
    
    public List getHots(String imageid) throws Exception
    {
    	List allhots=service.getHots(imageid);
    	return allhots;
    }
    public String getImageCode(String imageid) throws Exception
    {
    	List ls=service.getImageCode(imageid);
    	String imagecode="";
    	if(ls.size()==1)
    	{
    		Hashtable ht=(Hashtable)ls.get(0);
    		imagecode=ht.get("vccode").toString();
    	}
    	return imagecode;
    }
    public List getTwoDomMenu(String t1d,List ls) throws Exception
	{
    	Hashtable childmenu=new Hashtable();
    	List twomenu=new ArrayList();
    	for(int i=0;i<ls.size();i++)
    	{
    		Hashtable ht=(Hashtable)ls.get(i);
    		String key=ht.get("t1d").toString();
    		String menuid=ht.get("t2d").toString();
    		String menuname=ht.get("t2dname").toString();
    		String imageid=ht.get("imageid").toString();
    		String rsnum=ht.get("rsnum").toString();
    		if(t1d.equalsIgnoreCase(key) && menuid.equalsIgnoreCase("")==false)
    		{    			
    			if(childmenu.containsKey(menuid)==false)
    			{
    				String[] values=new String[3];
    				values[0]=menuname;
    				values[1]=imageid;
    				values[2]=menuid;
    				//System.out.println(menuname);
    				childmenu.put(menuid,values);
    				twomenu.add(0,values);
    			}
    		}
    	}
    	return twomenu;
	}
    
    public List getThreeDomMenu(String t1d,String t2d,List ls) throws Exception
	{
    	Hashtable childmenu=new Hashtable();
    	List thirdmenu=new ArrayList();
    	for(int i=0;i<ls.size();i++)
    	{
    		Hashtable ht=(Hashtable)ls.get(i);
    		String key0=ht.get("t1d").toString();
    		String key1=ht.get("t2d").toString();
    		String key=key0+"_"+key1;
    		String menuid=ht.get("t3d").toString();
    		String menuname=ht.get("t3dname").toString();
    		String imageid=ht.get("imageid").toString();
    		String sourcekey=t1d+"_"+t2d; 
    		if(sourcekey.equalsIgnoreCase(key) && menuid.equalsIgnoreCase("")==false)
    		{
    			if(childmenu.containsKey(menuid)==false)
    			{
    				String[] values=new String[3];
    				values[0]=menuname;
    				values[1]=imageid;
    				values[2]=menuid;
    				childmenu.put(menuid,values);
    				thirdmenu.add(0,values);
    			}
    		}
    	}
    	return thirdmenu;
	}   
    public String changePassword(String userid,String oldpwd,String newpwd) throws Exception
    {
    	return service.changePassword(userid,oldpwd,newpwd);
    }
    public List getUsers(String username) throws Exception
	{
    	return service.getUsers(username);
	}
}