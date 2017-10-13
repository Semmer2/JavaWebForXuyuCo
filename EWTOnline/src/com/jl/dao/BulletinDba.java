package com.jl.dao;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.PageContext;

import com.jl.dao.base.impl.BaseData;
import com.jl.dao.base.impl.DBASession;
import java.util.Hashtable;

public class BulletinDba extends BaseData {
	public BulletinDba(HttpServletRequest request)
	{
		super(request);
	}
	public boolean isExist(String topic,String id) throws Exception {
		boolean ret=true;	
		DBASession dbsession=getSession();
		try
		{		
			List param = new ArrayList();
			StringBuffer sql=new StringBuffer();
			sql.append("select count(*) as returnvalue from jlcar.smbulletin where topic=? and id<>? ");
			param.add(topic);			
			param.add(id);			
			List ls=dbsession.openSelectbyList(sql.toString(),param.toArray());
			String returnvalue=getValue(ls,0,"returnvalue");
			if("0".equals(returnvalue)||"-1".equals(returnvalue)){
				ret=false;
			}else {
				ret=true;
			}
		} catch (Exception e) {
			throw e;
		} finally {
			dbsession.close();
			dbsession=null;	
		}
		return ret;
	}
	public boolean isExist(String topic) throws Exception {
		boolean ret=true;	
		DBASession dbsession=getSession();
		try
		{		
			List param = new ArrayList();
			StringBuffer sql=new StringBuffer();
			sql.append("select count(*) as returnvalue from jlcar.smbulletin where topic=?");
			param.add(topic);
			List ls=dbsession.openSelectbyList(sql.toString(),param.toArray());
			String returnvalue=getValue(ls,0,"returnvalue");
			if("0".equals(returnvalue)||"-1".equals(returnvalue)){
				ret=false;
			}else {
				ret=true;
			}
		} catch (Exception e) {
			throw e;
		} finally {
			dbsession.close();
			dbsession=null;	
		}
		return ret;
	}
	public String getNewBulletinId() throws Exception
	{
		String ret="";
		DBASession dbsession=getSession();
		try
		{			
			List ls=dbsession.openSelectbyList("select s_notifyid.Nextval as noid from dual");
			ret=getValue(ls,0,"noid");				
		} catch (Exception e) {
			setError(e.getMessage());
			throw e;
		} finally {
			dbsession.close();
			dbsession=null;	
		}
		return ret;
	}
	public String createBulletin(String topic,String id)throws Exception {
		String ret="";	
		DBASession dbsession=getSession();
		try
		{	
			if(isExist(topic)){
				setError("公告已存在!");
			}else {
				SimpleDateFormat sf=new SimpleDateFormat();
				StringBuffer sql=new StringBuffer();				
				sql.append("insert into jlcar.smbulletin(id, topic) values(?,?)");
				List param=new ArrayList();
				param.add(id);
				param.add(topic);
				boolean isSuccess=dbsession.runSql(sql.toString(), param.toArray());				
				if(!isSuccess){
					setError("增加公告失败");
				}
				else
				{
					ret=id;
				}
			}
		} catch (Exception e) {
			setError("增加公告失败");
			throw e;
		} finally {
			dbsession.close();
			dbsession=null;	
		}
		return ret;
	}
	
	public String publishBulletin(String id) throws Exception {
		String ret="";	
		DBASession dbsession=getSession();
		Object tran=null;
		try
		{
			String sql="update jlcar.smbulletin set publishflag=1 where id=?";
			List param = new ArrayList();			
			param.add(id);
			boolean isSuccess=dbsession.runSql(sql.toString(), param.toArray());			
			ret="0";
		} catch (Exception e) {
			setError("发布公告失败");
			dbsession.rollbacklongTran(tran);
			throw e;
		} finally {			
			dbsession.close();
			dbsession=null;	
		}
		return ret;
	}
	public String delBulletin(String ids) throws Exception {
		String ret="";	
		DBASession dbsession=getSession();
		Object tran=null;
		try
		{			
			tran=dbsession.startlongTran();
			String[] idarray=ids.split(",");
			for(int i=0;i<idarray.length;i++)
			{
				String sql="delete from jlcar.smbulletin where id=?";
				List param = new ArrayList();			
				param.add(idarray[i]);
				boolean isSuccess=dbsession.runSql(sql.toString(), param.toArray());
			}
			dbsession.endlongTran(tran);
			ret="0";
		} catch (Exception e) {
			setError("删除公告失败");
			dbsession.rollbacklongTran(tran);
			throw e;
		} finally {			
			dbsession.close();
			dbsession=null;	
		}
		return ret;
	}
	public String saveBulletin(String id,String topic) throws Exception {
		String ret="";	
		DBASession dbsession=getSession();
		try
		{
			if(isExist(topic,id)){
				setError("公告已存在!");
			}else {
			SimpleDateFormat sf=new SimpleDateFormat();
			StringBuffer sql=new StringBuffer();			
			sql.append("update jlcar.smbulletin set topic=? where id=?");
			List param = new ArrayList();
			param.add(topic);			
			param.add(id);
			boolean isSuccess=dbsession.runSql(sql.toString(), param.toArray());				
			if(!isSuccess){
				setError("保存公告失败");
			}
			else
			{
				ret=id;
			}
			}
		} catch (Exception e) {
			setError("保存公告失败");
			throw e;
		} finally {
			dbsession.close();
			dbsession=null;	
		}
		return ret;
	}
	public List getBulletins() throws Exception {
		List ret=null;	
		DBASession dbsession=getSession();
		try
		{
			ret=dbsession.openSelectbyList("select * from vbulletin");			
		} catch (Exception e) {
			throw e;
		} finally {
			dbsession.close();
			dbsession=null;	
		}
		return ret;
	}
	public List getBulletinsforUser() throws Exception {
		List ret=null;	
		DBASession dbsession=getSession();
		try
		{
			ret=dbsession.openSelectbyList("select * from vbulletin where publishflag=1");			
		} catch (Exception e) {
			throw e;
		} finally {
			dbsession.close();
			dbsession=null;	
		}
		return ret;
	}
	public List getBulletinById(String id) throws Exception {
		List ret=null;	
		DBASession dbsession=getSession();
		try
		{
			List ls=new ArrayList();
			ls.add(id);
			ret=dbsession.openSelectbyList("select * from jlcar.smbulletin where id=?",ls.toArray());
		} catch (Exception e) {
			throw e;
		} finally {
			dbsession.close();
			dbsession=null;	
		}
		return ret;
	}	
}