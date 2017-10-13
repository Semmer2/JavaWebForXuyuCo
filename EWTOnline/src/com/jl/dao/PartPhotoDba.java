package com.jl.dao;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.PageContext;

import com.jl.dao.base.impl.BaseData;
import com.jl.dao.base.impl.DBASession;

public class PartPhotoDba extends BaseData {
	public PartPhotoDba(HttpServletRequest request)
	{
		super(request);
	}
	
	public String getNewPhotoId() throws Exception
	{
		String ret="";	
		DBASession dbsession=getSession();		
		try
		{			
			List ls=dbsession.openSelectbyList("select S_SMPHOTOID.Nextval as photoid from dual");
			String id=getValue(ls,0,"photoid");
			ret=id;
		} catch (Exception e) {
			setError("µÃµ½ÐÂÁã¼þÍ¼Æ¬IDÊ§°Ü");
			throw e;
		} finally {
			dbsession.close();
			dbsession=null;	
		}
		return ret;
	}
	public String createSmPartPhoto(String smpartid) throws Exception {
		String ret="";	
		DBASession dbsession=getSession();
		Object tranObject=dbsession.startlongTran();
		try
		{			
			String id=getNewPhotoId();
			StringBuffer sql=new StringBuffer();			
			sql.append("insert into jlcar.smphoto(id,DTUPATE) values(?,sysdate)");
			List param = new ArrayList();
			param.add(id);
			boolean isSuccess=dbsession.runSql(sql.toString(), param.toArray());				
			if(isSuccess){
				StringBuffer sql0=new StringBuffer();
				sql0.append("insert into jlcar.smpartphoto(id, ipartid, iphotoid, dtupate) values(S_SMPARTPHOTOID.Nextval,?,?,sysdate)");
				param.clear();
				param.add(smpartid);
				param.add(id);
				isSuccess=dbsession.runSql(sql0.toString(), param.toArray());	
				ret=id;
			}
			else
			{
				setError("Ôö¼ÓÁã¼þÍ¼Æ¬Ê§°Ü");
				
			}
		} catch (Exception e) {
			dbsession.rollbacklongTran(tranObject);
			setError("Ôö¼ÓÁã¼þÍ¼Æ¬Ê§°Ü");
			throw e;
		} finally {
			dbsession.endlongTran(tranObject);
			dbsession.close();
			dbsession=null;	
		}
		return ret;
	}
	
	public String delSmPartPhoto(String photoid) throws Exception {
		String ret="";	
		DBASession dbsession=getSession();
		Object tran=null;
		try
		{			
			tran=dbsession.startlongTran();			
			String sql="delete from jlcar.smpartphoto where iphotoid=?";
			List param = new ArrayList();
			param.add(photoid);
			boolean isSuccess=dbsession.runSql(sql.toString(), param.toArray());
			sql="delete from jlcar.smphoto where id=?";
			param.clear();
			param.add(photoid);
			isSuccess=dbsession.runSql(sql.toString(), param.toArray());
			dbsession.endlongTran(tran);
			ret="0";
		} catch (Exception e) {
			setError("É¾³ýÁã¼þÍ¼Æ¬Ê§°Ü");
			dbsession.rollbacklongTran(tran);
			throw e;
		} finally {			
			dbsession.close();
			dbsession=null;	
		}
		return ret;
	}
	public List getSmPartPhoto(String smpartid) throws Exception {
		List ret=null;	
		DBASession dbsession=getSession();
		Object tran=null;
		try
		{			
				
			String sql="select iphotoid from jlcar.smpartphoto where ipartid=? ";
			List param = new ArrayList();			
			param.add(smpartid);
			ret=dbsession.openSelectbyList(sql.toString(), param.toArray());
		} catch (Exception e) {
			setError("²éÑ¯Áã¼þÍ¼Æ¬Ê§°Ü");
			throw e;
		} finally {			
			dbsession.close();
			dbsession=null;	
		}
		return ret;
	}
	public List getSmPartPhotoByPartVccode(String partvccode) throws Exception {
		List ret=null;	
		DBASession dbsession=getSession();
		Object tran=null;
		try
		{	
			String sql="select iphotoid,b.vccode,b.vcename from jlcar.smpartphoto a,jlcar.smpart b where a.ipartid=b.id and b.vccode=?";
			List param = new ArrayList();			
			param.add(partvccode);
			ret=dbsession.openSelectbyList(sql.toString(), param.toArray());
		} catch (Exception e) {
			setError("²éÑ¯Áã¼þÍ¼Æ¬Ê§°Ü");
			throw e;
		} finally {			
			dbsession.close();
			dbsession=null;	
		}
		return ret;
	}
	
}