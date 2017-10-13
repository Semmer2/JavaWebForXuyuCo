package com.jl.dao;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.PageContext;

import com.jl.dao.base.impl.BaseData;
import com.jl.dao.base.impl.DBASession;

public class AsmLinkDba extends BaseData {
	public AsmLinkDba(HttpServletRequest request)
	{
		super(request);
	}
	public boolean isExist(String id) throws Exception {
		boolean ret=true;	
		DBASession dbsession=getSession();
		try
		{		
			List param = new ArrayList();
			StringBuffer sql=new StringBuffer();
			sql.append("select count(*) as returnvalue from jlcar.tcasmlink where id=?");			
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
	
	public boolean isExist(String iasmid,String ipartid,String iimageid,String ihot) throws Exception {
		boolean ret=true;	
		DBASession dbsession=getSession();
		try
		{		
			List param = new ArrayList();
			StringBuffer sql=new StringBuffer();
			sql.append("select count(*) as returnvalue from jlcar.tcasmlink where iasmid=? and ipartid=? and iimageid=? and ihot=?");			
			param.add(iasmid);
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
	public String saveAsmLink(String id, String iasmid,String ipartid,String iimageid,String ihot,String iqty,String iasmimageid) throws Exception {
		String ret="";	
		DBASession dbsession=getSession();
		try
		{	
			if(id.equalsIgnoreCase("")){
				if(isExist(iasmid,ipartid,iimageid,ihot)==false){
					SimpleDateFormat sf=new SimpleDateFormat();
					List ls=dbsession.openSelectbyList("select s_asmlinkid.Nextval as noid from dual");
					id=getValue(ls,0,"noid");
					StringBuffer sql=new StringBuffer();				
					sql.append("insert into jlcar.tcasmlink(id, iasmid, ipartid, iqty, iimageid, ihot, iasmimageid,dtupate)");				
					sql.append("values(?,?,?,?,?,?,?,sysdate)");
					List param = new ArrayList();
					param.add(id);
					param.add(iasmid);
					param.add(ipartid);
					param.add(iqty);
					param.add(iimageid);
					param.add(ihot);
					param.add(iasmimageid);				
					boolean isSuccess=dbsession.runSql(sql.toString(), param.toArray());				
					if(!isSuccess){
						setError("增加总成包失败!");
					}
					else
					{
						ret=id;
					}
				}
				else
				{
					setError("此总成已存在!");
				}	
			}else {
				SimpleDateFormat sf=new SimpleDateFormat();
				StringBuffer sql=new StringBuffer();				
				sql.append("update jlcar.tcasmlink set iqty=?,iimageid=?,ihot=?,ipartid=?,iasmimageid=?,dtupate=sysdate where id=?");
				List param = new ArrayList();				
				param.add(iqty);
				param.add(iimageid);
				param.add(ihot);
				param.add(ipartid);
				param.add(iasmimageid);
				param.add(id);
				boolean isSuccess=dbsession.runSql(sql.toString(), param.toArray());				
				if(!isSuccess){
					setError("更新总成包失败");
				}
				else
				{
					ret="0";
				}
			}
		} catch (Exception e) {
			setError("保存总成包失败");
			throw e;
		} finally {
			dbsession.close();
			dbsession=null;	
		}
		return ret;
	}
	
	public List getAsmLinks() throws Exception {
		List ret=null;	
		DBASession dbsession=getSession();
		try
		{
			String sql="select a.*, b.vccode as asmvccode,b.vccname as asmvccname, c.vccode as partvccode, c.vccname as partvccname,e.vccode as imagevccode,e.vccnote as imagevccnote,f.vccode as asmimagevccode,row_number() over(ORDER BY a.id)AS pagerownum";
			sql=sql+" from tcasmlink a, jlcar.smpart b, jlcar.smpart c,jlcar.tcimage e,jlcar.tcimage f ";
			sql=sql+" where a.iasmid = b.id and a.ipartid = c.id and a.iimageid=e.id and a.iasmimageid=f.id(+) and 1=?";
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
	public String getAsmLinkTotal() throws Exception {
		String ret=null;	
		DBASession dbsession=getSession();
		try
		{
			String sql="select count(a.iasmid) as rscount";
			sql=sql+" from tcasmlink a, jlcar.smpart b, jlcar.smpart c,jlcar.tcimage e ";
			sql=sql+" where a.iasmid = b.id and a.ipartid = c.id and a.iimageid=e.id";
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
	public List getAvailAsmLinks() throws Exception {
		List ret=null;	
		DBASession dbsession=getSession();
		try
		{
			ret=dbsession.openSelectbyList("select b.id as iasmid,b.vccnote from jlcar.smpart b where b.id not in (select distinct a.iasmid from jlcar.tcasmlink a)");
			
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
	
	public List getPartList(String asmid) throws Exception {
		List ret=null;	
		DBASession dbsession=getSession();
		try
		{
			String sql="select b.id,b.vccnote from jlcar.smpart b where b.id<>? and b.id not in (select a.ipartid from jlcar.tcasmlink a where a.iasmid=?)";
			List ls=new ArrayList();
			ls.add(asmid);
			ls.add(asmid);
			ret=dbsession.openSelectbyList(sql,ls.toArray());
		} catch (Exception e) {
			throw e;
		} finally {
			dbsession.close();
			dbsession=null;	
		}
		return ret;
	}
	public List getPartListForEdit(String asmid) throws Exception {
		List ret=null;	
		DBASession dbsession=getSession();
		try
		{
			String sql="select b.id,b.vccnote from jlcar.smpart b where b.id<>?";
			List ls=new ArrayList();
			ls.add(asmid);
			ret=dbsession.openSelectbyList(sql,ls.toArray());
		} catch (Exception e) {
			throw e;
		} finally {
			dbsession.close();
			dbsession=null;	
		}
		return ret;
	}
	public List getAsmLinksById(String id) throws Exception {
		List ret=null;	
		DBASession dbsession=getSession();
		try
		{			
			List ls=new ArrayList();
			ls.add(id);
			String sql="select a.*, b.vccode as asmvccode,b.vccname as asmvccname, c.vccode as partvccode, c.vccname as partvccname,e.vccode as imagevccode,e.vccnote as imagevccnote,f.vccode as asmimagevccode";
			sql=sql+" from tcasmlink a, jlcar.smpart b, jlcar.smpart c,jlcar.tcimage e,jlcar.tcimage f  ";
			sql=sql+" where a.iasmid = b.id and a.ipartid = c.id and a.iimageid=e.id and a.iasmimageid=f.id(+) and a.id=?";
			ret=dbsession.openSelectbyList(sql,ls.toArray());
		} catch (Exception e) {
			throw e;
		} finally {
			dbsession.close();
			dbsession=null;	
		}
		return ret;
	}
	
	public List getAsmLinkByVccode(String vccode) throws Exception {
		List ret=null;	
		DBASession dbsession=getSession();
		try
		{			
			List ls=new ArrayList();
			ls.add('%'+vccode+'%');
			String sql="select a.*, b.vccode as asmvccode,b.vccname as asmvccname, c.vccode as partvccode, c.vccname as partvccname,e.vccode as imagevccode,e.vccnote as imagevccnote,f.vccode as asmimagevccode";
			sql=sql+" from tcasmlink a, jlcar.smpart b, jlcar.smpart c,jlcar.tcimage e,jlcar.tcimage f  ";
			sql=sql+" where a.iasmid = b.id and a.ipartid = c.id and a.iimageid=e.id and a.iasmimageid=f.id(+) and  b.vccode like ?";
			ret=dbsession.openSelectbyList(sql,ls.toArray());
		} catch (Exception e) {
			throw e;
		} finally {
			dbsession.close();
			dbsession=null;	
		}
		return ret;
	}
	
	
	
	
	
	
	public List getAsmLinks(String asmid,String partid) throws Exception {
		List ret=null;	
		DBASession dbsession=getSession();
		try
		{
			List ls=new ArrayList();
			ls.add(asmid);
			ls.add(partid);
			ret=dbsession.openSelectbyList("select a.*,b.vccnote as asmcnote,c.vccnote as partcnote from tcasmlink a,jlcar.smpart b,jlcar.smpart c where a.iasmid=b.id and a.ipartid=c.id and a.iasmid=? and a.ipartid=?",ls.toArray());
		} catch (Exception e) {
			throw e;
		} finally {
			dbsession.close();
			dbsession=null;	
		}
		return ret;
	}
	public List getAsmLinks(String asmid) throws Exception {
		List ret=null;	
		DBASession dbsession=getSession();
		try
		{
			List ls=new ArrayList();
			ls.add(asmid);
			ret=dbsession.openSelectbyList("select distinct a.iasmid,b.vccnote from tcasmlink a,jlcar.smpart b where a.iasmid=b.id and a.iasmid=?",ls.toArray());
			
		} catch (Exception e) {
			throw e;
		} finally {
			dbsession.close();
			dbsession=null;	
		}
		return ret;
	}
	public List getChildAsmLinks(String asmid) throws Exception {
		List ret=null;	
		DBASession dbsession=getSession();
		try
		{
			List ls=new ArrayList();
			ls.add(asmid);
			ret=dbsession.openSelectbyList("select a.id,a.ipartid,b.vccnote,a.iqty,a.iimageid,a.ihot from tcasmlink a,jlcar.smpart b where a.ipartid=b.id and a.iasmid=?",ls.toArray());
			
		} catch (Exception e) {
			throw e;
		} finally {
			dbsession.close();
			dbsession=null;	
		}
		return ret;
	}
	public String delAsmLink(String ids) throws Exception {
		String ret="";	
		DBASession dbsession=getSession();
		Object tran=null;
		try
		{			
			tran=dbsession.startlongTran();
			String[] idarray=ids.split(",");
			for(int i=0;i<idarray.length;i++)
			{
				String sql="delete from jlcar.tcasmlink where id=?";
				List param = new ArrayList();			
				param.add(idarray[i]);
				boolean isSuccess=dbsession.runSql(sql.toString(), param.toArray());
			}
			dbsession.endlongTran(tran);
			ret="0";
		} catch (Exception e) {
			setError("删除总成包失败");
			dbsession.rollbacklongTran(tran);
			throw e;
		} finally {			
			dbsession.close();
			dbsession=null;	
		}
		return ret;
	}
}