package com.jl.dao;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.PageContext;
import com.jl.dao.base.impl.BaseData;
import com.jl.dao.base.impl.DBASession;

public class WallImagedba extends BaseData {
	public WallImagedba(HttpServletRequest request)
	{
		super(request);
	}
	public String getTodayWall() throws Exception
	{
		String result="";
		List rtn=null;	
		DBASession dbsession=getSession();
		try
		{		
			List param = new ArrayList();
			StringBuffer sql=new StringBuffer();
			sql.append("select * from tcwall t where to_char(t.dtupdate,'yyyy-mm-dd')=to_char(sysdate,'yyyy-mm-dd')");
			rtn=dbsession.openSelectbyList(sql.toString(),param.toArray());
			if(rtn.size()==1)
			{
				Hashtable ht=(Hashtable)rtn.get(0);
				result=ht.get("wall").toString();			
			}
		} catch (Exception e) {
			throw e;
		} finally {
			dbsession.close();
			dbsession=null;	
		}
		return result;
	}
	public List getWallImageList() throws Exception
	{		
		List ret=null;	
		DBASession dbsession=getSession();
		try
		{
			ret=dbsession.openSelectbyList("select * from vwallimage order by id desc");
			
		} catch (Exception e) {
			throw e;
		} finally {
			dbsession.close();
			dbsession=null;	
		}
		return ret;		
	}
	public List getWallAllShowImageList() throws Exception
	{		
		List ret=null;	
		DBASession dbsession=getSession();
		try
		{
			ret=dbsession.openSelectbyList("select * from vwallimage where showflag='1' order by id desc");
			
		} catch (Exception e) {
			throw e;
		} finally {
			dbsession.close();
			dbsession=null;	
		}
		return ret;		
	}
	public String getWall() throws Exception
	{
		String result="";
		List rtn=null;	
		DBASession dbsession=getSession();
		try
		{		
			List param = new ArrayList();
			StringBuffer sql=new StringBuffer();
			sql.append("select * from tcwall t");
			rtn=dbsession.openSelectbyList(sql.toString(),param.toArray());
			if(rtn.size()==1)
			{
				Hashtable ht=(Hashtable)rtn.get(0);
				result=ht.get("wall").toString();			
			}
		} catch (Exception e) {
			throw e;
		} finally {
			dbsession.close();
			dbsession=null;	
		}
		return result;
	}
	public String getMaxId() throws Exception
	{
		String ret="";
		DBASession dbsession=getSession();
		try
		{			
			String sql="select max(id)+1 as imageid from jlcar.tcwallimage";
			List ls=dbsession.openSelectbyList(sql);
			Hashtable ht=(Hashtable)ls.get(0);
			ret=ht.get("imageid").toString();			
		} catch (Exception e) {
			setError(e.getMessage());
			throw e;
		} finally {
			dbsession.close();
			dbsession=null;	
		}
		return ret;
	}
	public void addWallImage(String imagename,String id) throws Exception
	{
		String ret="";
		String Wall="";
		DBASession dbsession=getSession();
		try
		{
			Wall=id+".jpg";
			String sql="insert into jlcar.tcwallimage(wall,id,showflag,imagename) values(?,?,'1',?)";
			List param = new ArrayList();
			param.add(Wall);
			param.add(id);
			param.add(imagename);
			boolean isSuccess=dbsession.runSql(sql.toString(), param.toArray());
		} catch (Exception e) {
			setError(e.getMessage());
			throw e;
		} finally {
			dbsession.close();
			dbsession=null;	
		}
		return;
	}
	public void updateWallImage(String id,String imagename) throws Exception
	{
		String ret="";	
		DBASession dbsession=getSession();
		try
		{
			String sql="update jlcar.tcwallimage set imagename=? where id=?";
			List param = new ArrayList();
			param.add(imagename);
			param.add(id);
			boolean isSuccess=dbsession.runSql(sql.toString(), param.toArray());
		} catch (Exception e) {
			setError(e.getMessage());
			throw e;
		} finally {
			dbsession.close();
			dbsession=null;	
		}
		return;
	}
	public void updateWallImageShow(String id,String showflag) throws Exception
	{
		String ret="";	
		DBASession dbsession=getSession();
		try
		{
			String sql="";
			if(id.equalsIgnoreCase("all"))
			{
				sql="update jlcar.tcwallimage set showflag=?";
				List param = new ArrayList();
				if(showflag.equalsIgnoreCase("true"))
					param.add("1");
				else
					param.add("0");
				boolean isSuccess=dbsession.runSql(sql.toString(), param.toArray());
			}
			else
			{
				sql="update jlcar.tcwallimage set showflag=? where id=?";
				List param = new ArrayList();
				if(showflag.equalsIgnoreCase("true"))
					param.add("1");
				else
					param.add("0");
				param.add(id);
				boolean isSuccess=dbsession.runSql(sql.toString(), param.toArray());
			}
		} catch (Exception e) {
			setError(e.getMessage());
			throw e;
		} finally {
			dbsession.close();
			dbsession=null;	
		}
		return;
	}
	public String deleteWallImage(String id) throws Exception
	{
		String ret="";	
		DBASession dbsession=getSession();
		try
		{
			String sql="delete jlcar.tcwallimage where id=?";
			List param = new ArrayList();
			param.add(id);
			boolean isSuccess=dbsession.runSql(sql.toString(), param.toArray());
			return "0";
		} catch (Exception e) {
			setError(e.getMessage());
			throw e;
		} finally {
			dbsession.close();
			dbsession=null;	
		}
	}
	public void addWall(String Wall) throws Exception
	{
		String ret="";	
		DBASession dbsession=getSession();
		try
		{
			String sql="insert into jlcar.tcwall(id,wall,dtupdate) values(1,?,sysdate)";
			List param = new ArrayList();
			param.add(Wall);
			boolean isSuccess=dbsession.runSql(sql.toString(), param.toArray());
		} catch (Exception e) {
			setError(e.getMessage());
			throw e;
		} finally {
			dbsession.close();
			dbsession=null;	
		}
		return;
	}
	public void updateWall(String Wall) throws Exception
	{
		String ret="";	
		DBASession dbsession=getSession();
		try
		{
			String sql="update jlcar.tcwall set wall=? where id=1";
			List param = new ArrayList();
			param.add(Wall);
			boolean isSuccess=dbsession.runSql(sql.toString(), param.toArray());
		} catch (Exception e) {
			setError(e.getMessage());
			throw e;
		} finally {
			dbsession.close();
			dbsession=null;	
		}
		return;
	}
	
	public void updateImageShowModel(String model) throws Exception
	{
		String ret="";	
		DBASession dbsession=getSession();
		try
		{
			String sql="update jlcar.tcwallshowmodel set showmodel=?";
			List param = new ArrayList();
			if(model.equalsIgnoreCase("single"))
				param.add("0");
			else
				param.add("1");
			boolean isSuccess=dbsession.runSql(sql.toString(), param.toArray());
		} catch (Exception e) {
			setError(e.getMessage());
			throw e;
		} finally {
			dbsession.close();
			dbsession=null;	
		}
		return;
	}
	public String getImageShowModel() throws Exception
	{
		String ret="";	
		DBASession dbsession=getSession();
		try
		{
			String sql="select showmodel from jlcar.tcwallshowmodel";			
			List ls=dbsession.openSelectbyList(sql.toString());
			Hashtable ht=(Hashtable)ls.get(0);
			ret=ht.get("showmodel").toString();
		} catch (Exception e) {
			setError(e.getMessage());
			throw e;
		} finally {
			dbsession.close();
			dbsession=null;	
		}
		return ret;
	}
	
	
}