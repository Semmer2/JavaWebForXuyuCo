package com.jl.dao;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.PageContext;

import com.jl.dao.base.impl.BaseData;
import com.jl.dao.base.impl.DBASession;
import java.util.Hashtable;

public class SuggestDba extends BaseData {
	public SuggestDba(HttpServletRequest request)
	{
		super(request);
	}	
	public String getNewSuggestId() throws Exception
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
	public String createSuggest(String subject,String path,String content,String fromwho,String id,String vehid,String imageid)throws Exception {
		String ret="";	
		DBASession dbsession=getSession();
		try
		{
				SimpleDateFormat sf=new SimpleDateFormat();
				StringBuffer sql=new StringBuffer();				
				sql.append("insert into jlcar.smsuggest(id, subject,path,suggestcontent,fromwho,vehid,imageid) values(?,?,?,?,?,?,?)");
				List param=new ArrayList();
				param.add(id);
				param.add(subject);
				param.add(path);
				param.add(content);
				param.add(fromwho);
				param.add(vehid);
				param.add(imageid);
				boolean isSuccess=dbsession.runSql(sql.toString(), param.toArray());				
				if(!isSuccess){
					setError("Add Suggest Fail!");
				}
				else
				{
					ret=id;
				}			
		} catch (Exception e) {
			setError("Add Suggest Fail!");
			throw e;
		} finally {
			dbsession.close();
			dbsession=null;	
		}
		return ret;
	}	
	
	public String delSuggest(String ids) throws Exception {
		String ret="";	
		DBASession dbsession=getSession();
		Object tran=null;
		try
		{			
			tran=dbsession.startlongTran();
			String[] idarray=ids.split(",");
			for(int i=0;i<idarray.length;i++)
			{
				String sql="delete from jlcar.smsuggest where id=?";
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
	public List getSuggests() throws Exception {
		List ret=null;	
		DBASession dbsession=getSession();
		try
		{
			ret=dbsession.openSelectbyList("select * from vSuggest");			
		} catch (Exception e) {
			throw e;
		} finally {
			dbsession.close();
			dbsession=null;	
		}
		return ret;
	}	
	public List getSuggestById(String id) throws Exception {
		List ret=null;	
		DBASession dbsession=getSession();
		try
		{
			List ls=new ArrayList();
			ls.add(id);
			ret=dbsession.openSelectbyList("select * from jlcar.vSuggest where id=?",ls.toArray());
		} catch (Exception e) {
			throw e;
		} finally {
			dbsession.close();
			dbsession=null;	
		}
		return ret;
	}	
}