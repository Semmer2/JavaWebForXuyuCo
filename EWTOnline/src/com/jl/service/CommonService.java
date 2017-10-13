package com.jl.service;

import java.io.File;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.PageContext;
import com.jl.dao.*;

public class CommonService {
	private CommonDba dba=null;
	private HttpServletRequest request;
	public CommonService(HttpServletRequest request)
	{		
		this.request=request;
		dba=new CommonDba(request);
	}
	public List getSeries(String userid,String vinesn) throws Exception
	{
		return dba.getSeries(userid,vinesn);
	}
	public List getTcvehicle() throws Exception
	{
		return dba.getTcvehicle();
	}
	public List getTcvehicle(String vehid) throws Exception
	{
		return dba.getTcvehicle(vehid);
	}
	public List getVehicle(String userid,String vinesn) throws Exception
	{
		return dba.getVehicle(userid,vinesn);
	}
	public List getVehicle(String vccode) throws Exception
	{
		return dba.getVehicle(vccode);
	}
	public List getVehicle(String userid,String seriesid,String year,String cabin,String wheelbase,String fuel,String model,String emmission,String drive,String color) throws Exception
	{
		return dba.getVehicle(userid,seriesid,year,cabin,wheelbase,fuel,model,emmission,drive,color);
	}
	public List getVehicleforsearch(String partvccode,String userid) throws Exception
	{
		return dba.getVehicleforsearch(partvccode,userid);
	}
	public List getSearchCondition(String userid,String conditiontype,String vinesn,String seriesid) throws Exception
	{
		return dba.getSearchCondition(userid,conditiontype,vinesn,seriesid);
	}
	public List getAdvanceSearchCondition(String userid,String conditiontype,String sysvalue) throws Exception
	{
		return dba.getAdvanceSearchCondition(userid,conditiontype,sysvalue);
	}
	public String getSeriesid(String vehid) throws Exception
    {
    	return dba.getSeriesid(vehid);
    }
	public List getAsmLink(String partid) throws Exception
	{
		return dba.getAsmLink(partid);
	}
	public List queryVehicle(String asmid,String vinesn) throws Exception
	{
		return dba.queryVehicle(asmid,vinesn);
	}
	public List querySeries(String asmid,String vinesn) throws Exception
	{
		return dba.querySeries(asmid,vinesn);
	}
	public List getDomMenu(String vehid) throws Exception
	{
		return dba.getDomMenu(vehid);
	}
	public List getDomMenuByPath(String vehid,String imagepath) throws Exception
	{
		return dba.getDomMenuByPath(vehid,imagepath);
	}
	public List getDomMenuByNum(String vehid,String imagenum) throws Exception
	{
		return dba.getDomMenuByNum(vehid,imagenum);
	}
	public String getDomMenuPath(String vehid,String imageid) throws Exception
	{
		return dba.getDomMenuPath(vehid,imageid);
	}
	public String getDomMenuPathBySvg(String vehid,String imageid) throws Exception
	{
		return dba.getDomMenuPathBySvg(vehid,imageid);
	}
	
	public List getHots(String imageid) throws Exception
	{
		return dba.getHots(imageid);
	}
	public List getImageCode(String imageid) throws Exception
	{
		return dba.getImageCode(imageid);
	}
	public List getImage(String imagevccode) throws Exception
	{
		return dba.getImage(imagevccode);
	}
	public String changePassword(String userid,String oldpwd,String newpwd) throws Exception
	{
	    return dba.changePassword(userid,oldpwd,newpwd);
	}
	public List getUsers(String username) throws Exception
	{
    	return dba.getUsers(username);
	}	
}