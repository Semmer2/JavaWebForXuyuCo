package com.jl.dao;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.PageContext;

import com.jl.dao.base.impl.BaseData;
import com.jl.dao.base.impl.DBASession;

public class RoleDba extends BaseData {
	public RoleDba(HttpServletRequest request)
	{
		super(request);
	}
	public boolean isExist(String vcrolename,String vcroleno) throws Exception {
		boolean ret=true;	
		DBASession dbsession=getSession();
		try
		{		
			List param = new ArrayList();
			StringBuffer sql=new StringBuffer();
			sql.append("select count(*) as returnvalue from jlcar.smrole where 1=1 ");
			if(!"".equals(vcrolename)){
				sql.append(" and vcrolename=?");
				param.add(vcrolename);
			}
			if(!"".equals(vcroleno)){
				sql.append(" or vcroleno=?");
				param.add(vcroleno);
			}
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
	public boolean isExist(String vcrolename,String vcroleno,String id) throws Exception {
		boolean ret=true;	
		DBASession dbsession=getSession();
		try
		{		
			List param = new ArrayList();
			StringBuffer sql=new StringBuffer();
			sql.append("select count(*) as returnvalue from jlcar.smrole where 1=1 ");
			if(!"".equals(vcrolename)){
				sql.append(" and (vcrolename=?");
				param.add(vcrolename);
			}
			if(!"".equals(vcroleno)){
				sql.append(" or vcroleno=?)");
				param.add(vcroleno);
			}
			if(!"".equals(id)){
				sql.append(" and id!=?");
				param.add(id);
			}
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
	public String createRole(String vcroleno,String vcrolename,String bactive) throws Exception {
		String ret="";	
		DBASession dbsession=getSession();
		try
		{	
			if(isExist(vcrolename,vcroleno)){
				setError("角色已存在!");
			}else {
				SimpleDateFormat sf=new SimpleDateFormat();
				StringBuffer sql=new StringBuffer();
				List ls=dbsession.openSelectbyList("select S_ROLEID.Nextval as noid from dual");
				String id=getValue(ls,0,"noid");
				sql.append("insert into jlcar.smrole(id,vcroleno,vcrolename,bactive) values(?,?,?,?)");
				List param = new ArrayList();
				param.add(id);
				param.add(vcroleno);
				param.add(vcrolename);
				param.add(bactive);
				boolean isSuccess=dbsession.runSql(sql.toString(), param.toArray());				
				if(!isSuccess){
					setError("增加角色失败");
				}
				else
				{
					ret=id;
				}
			}
		} catch (Exception e) {
			setError("增加角色失败");
			throw e;
		} finally {
			dbsession.close();
			dbsession=null;	
		}
		return ret;
	}
	public String delRole(String ids) throws Exception {
		String ret="";	
		DBASession dbsession=getSession();
		Object tran=null;
		try
		{			
			tran=dbsession.startlongTran();
			String[] idarray=ids.split(",");
			for(int i=0;i<idarray.length;i++)
			{				
				List param = new ArrayList();			
				param.add(idarray[i]);
				String sql="select count(1) as rscount from smuser t where iroleid=?";
				List ls=dbsession.openSelectbyList(sql, param.toArray());
				Hashtable ht=(Hashtable)ls.get(0);
				int rscount=Integer.valueOf(ht.get("rscount").toString());
				if(rscount==0)
				{
					sql="delete from jlcar.smrole where id=?";					
					dbsession.runSql(sql.toString(), param.toArray());
					sql="delete from smrolepriv t where roleid=?";
					dbsession.runSql(sql.toString(), param.toArray());
				}
			}
			dbsession.endlongTran(tran);
			ret="0";
		} catch (Exception e) {
			setError("删除角色失败");
			dbsession.rollbacklongTran(tran);
			throw e;
		} finally {			
			dbsession.close();
			dbsession=null;	
		}
		return ret;
	}
	public String saveRole(String id,String vcroleno,String vcrolename,String bactive) throws Exception {
		String ret="";	
		DBASession dbsession=getSession();
		try
		{
			if(isExist(vcrolename,vcroleno,id)){
				setError("角色已存在!");
			}else {
			SimpleDateFormat sf=new SimpleDateFormat();
			StringBuffer sql=new StringBuffer();			
			sql.append("update jlcar.smrole set vcroleno=?,vcrolename=?,bactive=? where id=?");
			List param = new ArrayList();
			param.add(vcroleno);
			param.add(vcrolename);
			param.add(bactive);
			param.add(id);
			boolean isSuccess=dbsession.runSql(sql.toString(), param.toArray());				
			if(!isSuccess){
				setError("保存角色失败");
			}
			else
			{
				ret=id;
			}
			}
		} catch (Exception e) {
			setError("保存角色失败");
			throw e;
		} finally {
			dbsession.close();
			dbsession=null;	
		}
		return ret;
	}
	public List getRoles() throws Exception {
		List ret=null;	
		DBASession dbsession=getSession();
		try
		{
			ret=dbsession.openSelectbyList("select id,vcroleno,vcrolename,bactive,bactivename from jlcar.vrole");
			
		} catch (Exception e) {
			throw e;
		} finally {
			dbsession.close();
			dbsession=null;	
		}
		return ret;
	}
	public List getRolePriv(String roleid) throws Exception {
		List ret=null;	
		DBASession dbsession=getSession();
		try
		{
			List param = new ArrayList();			
			param.add(roleid);
			ret=dbsession.openSelectbyList("select * from jlcar.SMROLEPRIV where roleid=?",param.toArray());
		} catch (Exception e) {
			throw e;
		} finally {
			dbsession.close();
			dbsession=null;	
		}
		return ret;
	}
	public String addRolePriv(String roleid,String modename) throws Exception
    {
		String ret="";	
		DBASession dbsession=getSession();
		Object tran=null;
		try
		{			
			tran=dbsession.startlongTran();
			String sql="delete from jlcar.SMROLEPRIV where roleid=? and modename=?";
			List param = new ArrayList();			
			param.add(roleid);
			param.add(modename);
			dbsession.runSql(sql.toString(), param.toArray());
			sql="insert into jlcar.SMROLEPRIV(roleid,modename) values(?,?)";
			dbsession.runSql(sql.toString(), param.toArray());						
			dbsession.endlongTran(tran);
			ret="0";
		} catch (Exception e) {
			setError("设置角色权限失败");
			dbsession.rollbacklongTran(tran);
			throw e;
		} finally {			
			dbsession.close();
			dbsession=null;	
		}
		return ret;
    }
	public String deleteRolePriv(String roleid,String modename) throws Exception
    {
		String ret="";	
		DBASession dbsession=getSession();
		Object tran=null;
		try
		{			
			tran=dbsession.startlongTran();
			String sql="delete from jlcar.SMROLEPRIV where roleid=? and modename=?";
			List param = new ArrayList();			
			param.add(roleid);
			param.add(modename);
			dbsession.runSql(sql.toString(), param.toArray());							
			dbsession.endlongTran(tran);
			ret="0";
		} catch (Exception e) {
			setError("删除角色权限失败");
			dbsession.rollbacklongTran(tran);
			throw e;
		} finally {			
			dbsession.close();
			dbsession=null;	
		}
		return ret;
    }
}