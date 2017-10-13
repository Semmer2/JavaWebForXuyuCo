package com.jl.dao;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.PageContext;

import com.jl.dao.base.impl.BaseData;
import com.jl.dao.base.impl.DBASession;

public class VehLinkDba extends BaseData {
	public VehLinkDba(HttpServletRequest request)
	{
		super(request);
	}
	public String getXlsId() throws Exception
	{
		String ret="";
		DBASession dbsession=getSession();
		try
		{			
			List ls=dbsession.openSelectbyList("select s_xls.Nextval as xlsid from dual");
			ret=getValue(ls,0,"xlsid");				
		} catch (Exception e) {
			setError(e.getMessage());
			throw e;
		} finally {
			dbsession.close();
			dbsession=null;	
		}
		return ret;
	}
	public boolean isExist(String id) throws Exception {
		boolean ret=true;	
		DBASession dbsession=getSession();
		try
		{		
			List param = new ArrayList();
			StringBuffer sql=new StringBuffer();
			sql.append("select count(*) as returnvalue from jlcar.tcvehlink where id=?");			
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
	
	public boolean isExist(String ivehid,String ipartid,String iimageid,String ihot) throws Exception {
		boolean ret=true;	
		DBASession dbsession=getSession();
		try
		{		
			List param = new ArrayList();
			StringBuffer sql=new StringBuffer();
			sql.append("select count(*) as returnvalue from jlcar.tcvehlink where ivehid=? and ipartid=? and iimageid=? and ihot=?");			
			param.add(ivehid);
			param.add(ipartid);
			param.add(iimageid);
			param.add(ihot);
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
	
	public String saveVehLink(String id, String ivehid,String ipartid,String iimageid,String ihot,String iqty) throws Exception {
		String ret="";	
		DBASession dbsession=getSession();
		try
		{	
			//if(isExist(ivehid,ipartid,iimageid,ihot)==false){
			if(id.equalsIgnoreCase("")){
				if(isExist(ivehid,ipartid,iimageid,ihot)==false){
					SimpleDateFormat sf=new SimpleDateFormat();
					List ls=dbsession.openSelectbyList("select s_vehlink_id.Nextval as noid from dual");
					id=getValue(ls,0,"noid");
					StringBuffer sql=new StringBuffer();				
					sql.append("insert into jlcar.tcvehlink(id, ivehid, ipartid, iqty, iimageid, ihot, dtupate)");				
					sql.append("values(?,?,?,?,?,?,sysdate)");
					List param = new ArrayList();
					param.add(id);
					param.add(ivehid);
					param.add(ipartid);
					param.add(iqty);
					param.add(iimageid);
					param.add(ihot);			
					boolean isSuccess=dbsession.runSql(sql.toString(), param.toArray());				
					if(!isSuccess){
						setError("增加车型BOM失败");
					}
					else
					{
						ret=id;
					}
				}
				else
				{
					setError("此车型BOM已存在!");
				}
			}else {
				SimpleDateFormat sf=new SimpleDateFormat();
				StringBuffer sql=new StringBuffer();				
				sql.append("update jlcar.tcvehlink set iqty=?,iimageid=?,ihot=?,ipartid=?,dtupate=sysdate where id=?");
				List param = new ArrayList();				
				param.add(iqty);
				param.add(iimageid);
				param.add(ihot);
				param.add(ipartid);
				param.add(id);
				boolean isSuccess=dbsession.runSql(sql.toString(), param.toArray());				
				if(!isSuccess){
					setError("更新车型BOM失败");
				}
				else
				{
					ret="0";
				}
			}
		} catch (Exception e) {
			setError("保存车型BOM失败");
			throw e;
		} finally {
			dbsession.close();
			dbsession=null;	
		}
		return ret;
	}
	
	public List getVehLinks() throws Exception {
		List ret=null;	
		DBASession dbsession=getSession();
		try
		{
			String sql="select a.*, b.vccode as vehvccode,b.vccnote as vehvccnote, c.vccode as partvccode, c.vccname as partvccname,e.vccode as imagevccode,e.vccnote as imagevccnote,row_number() over(ORDER BY a.id)AS pagerownum";
			sql=sql+" from tcvehlink a, jlcar.tcvehicle b, jlcar.smpart c,jlcar.tcimage e ";
			sql=sql+" where a.ivehid = b.id and a.ipartid = c.id and a.iimageid=e.id and 1=?";
			String[] p=new String[1];
			p[0]="1";
			ret=dbsession.openSelectbyList(sql,p);
		} catch (Exception e) {
			throw e;
		} finally {
			dbsession.close();
			dbsession=null;	
		}
		return ret;
	}
	public String getVehLinkTotal() throws Exception {
		String ret=null;	
		DBASession dbsession=getSession();
		try
		{
			String sql="select count(a.ivehid) as rscount";
			sql=sql+" from tcvehlink a, jlcar.tcvehicle b, jlcar.smpart c,jlcar.tcimage e ";
			sql=sql+" where a.ivehid = b.id and a.ipartid = c.id and a.iimageid=e.id";
			List ls=dbsession.openSelectbyList(sql);
			Hashtable ht=(Hashtable)ls.get(0);
			ret=ht.get("rscount").toString();			
		} catch (Exception e) {
			throw e;
		} finally {
			dbsession.close();
			dbsession=null;	
		}
		return ret;
	}
	
	public List getIImageList() throws Exception {
		List ret=null;	
		DBASession dbsession=getSession();
		try
		{
			ret=dbsession.openSelectbyList("select a.id,a.vccode,a.vccnote from jlcar.tcimage a");
			
		} catch (Exception e) {
			throw e;
		} finally {
			dbsession.close();
			dbsession=null;	
		}
		return ret;
	}
	public List getHotList(String imageid) throws Exception {
		List ret=null;	
		DBASession dbsession=getSession();
		try
		{
			List ls=new ArrayList();
			ls.add(imageid);
			ret=dbsession.openSelectbyList("select id,ihot from jlcar.tchot where imageid=?",ls.toArray());
		} catch (Exception e) {
			throw e;
		} finally {
			dbsession.close();
			dbsession=null;	
		}
		return ret;
	}
	
	
	public List getVehLinksById(String id) throws Exception {
		List ret=null;	
		DBASession dbsession=getSession();
		try
		{			
			List ls=new ArrayList();
			ls.add(id);
			String sql="select a.*, b.vccode as vehvccode,b.vccnote as vehvccnote, c.vccode as partvccode, c.vccname as partvccname,e.vccode as imagevccode,e.vccnote as imagevccnote";
			sql=sql+" from tcvehlink a, jlcar.tcvehicle b, jlcar.smpart c,jlcar.tcimage e ";
			sql=sql+" where a.ivehid = b.id and a.ipartid = c.id and a.iimageid=e.id and a.id=?";
			ret=dbsession.openSelectbyList(sql,ls.toArray());
		} catch (Exception e) {
			throw e;
		} finally {
			dbsession.close();
			dbsession=null;	
		}
		return ret;
	}
	
	public List getVehLinkByVccode(String vccode) throws Exception {
		List ret=null;	
		DBASession dbsession=getSession();
		try
		{			
			List ls=new ArrayList();
			ls.add('%'+vccode+'%');
			String sql="select a.*, b.mastercode as vehvccode,b.vccnote as vehvccnote, c.vccode as partvccode, c.vccname as partvccname,e.vccode as imagevccode,e.vccnote as imagevccnote,row_number() over(ORDER BY a.id)AS pagerownum";
			sql=sql+" from tcvehlink a, jlcar.vtcvehicle b, jlcar.smpart c,jlcar.tcimage e ";
			sql=sql+" where a.ivehid = b.id and a.ipartid = c.id and a.iimageid=e.id and b.mastercode like ? and rownum<9999";
			ret=dbsession.openSelectbyList(sql,ls.toArray());
		} catch (Exception e) {
			throw e;
		} finally {
			dbsession.close();
			dbsession=null;	
		}
		return ret;
	}
	
	
	public List getVehLinks(String vehid,String partid) throws Exception {
		List ret=null;	
		DBASession dbsession=getSession();
		try
		{
			List ls=new ArrayList();
			ls.add(vehid);
			ls.add(partid);
			ret=dbsession.openSelectbyList("select a.*,b.vccnote as vehvccnote,c.vccnote as partcnote from tcvehlink a,jlcar.tcvehicle b,jlcar.smpart c where a.ivehid=b.id and a.ipartid=c.id and a.ivehid=? and a.ipartid=?",ls.toArray());
		} catch (Exception e) {
			throw e;
		} finally {
			dbsession.close();
			dbsession=null;	
		}
		return ret;
	}
	public List getVehLinks(String vehid) throws Exception {
		List ret=null;	
		DBASession dbsession=getSession();
		try
		{
			List ls=new ArrayList();
			ls.add(vehid);
			ret=dbsession.openSelectbyList("select distinct a.ivehid,b.vccnote from tcvehlink a,jlcar.smpart b where a.ivehid=b.id and a.ivehid=?",ls.toArray());
			
		} catch (Exception e) {
			throw e;
		} finally {
			dbsession.close();
			dbsession=null;	
		}
		return ret;
	}
	
	public String delVehLink(String ids) throws Exception {
		String ret="";	
		DBASession dbsession=getSession();
		Object tran=null;
		try
		{			
			tran=dbsession.startlongTran();
			String[] idarray=ids.split(",");
			for(int i=0;i<idarray.length;i++)
			{
				String sql="delete from jlcar.tcvehlink where id=?";
				List param = new ArrayList();			
				param.add(idarray[i]);
				boolean isSuccess=dbsession.runSql(sql.toString(), param.toArray());
			}
			dbsession.endlongTran(tran);
			ret="0";
		} catch (Exception e) {
			setError("删除车型BOM失败");
			dbsession.rollbacklongTran(tran);
			throw e;
		} finally {			
			dbsession.close();
			dbsession=null;	
		}
		return ret;
	}
}