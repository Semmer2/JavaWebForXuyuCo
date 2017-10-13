package com.jl.dao;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.PageContext;

import com.jl.dao.base.impl.BaseData;
import com.jl.dao.base.impl.DBASession;

public class SmPartDba extends BaseData {
	public SmPartDba(HttpServletRequest request)
	{
		super(request);
	}
	public boolean isExist(String vccode) throws Exception {
		boolean ret=true;	
		DBASession dbsession=getSession();
		try
		{		
			List param = new ArrayList();
			StringBuffer sql=new StringBuffer();
			sql.append("select count(*) as returnvalue from jlcar.smpart where vccode=?");
			param.add(vccode);			
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
	public boolean isExistPartPrice(String userid,String vccode) throws Exception {
		boolean ret=true;	
		DBASession dbsession=getSession();
		try
		{		
			List param = new ArrayList();
			StringBuffer sql=new StringBuffer();
			sql.append("select count(*) as returnvalue from jlcar.smpartprice where userid=? and vccode=? ");			
			param.add(userid);			
			param.add(vccode);			
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
	
	public String createSmPart(String vccode, String vccname, String vcename, 
			String vccnote, String vcenote, String icatalog, String iphotoflag, 
			String vclength, String vcwidth, String vcheight, String vcweight, 
			String vcvolume, String vcposition, String isaleflag,String priceuserid,String price) throws Exception {
		String ret="";	
		DBASession dbsession=getSession();
		Object tran=dbsession.startlongTran();
		try
		{	
			if(isExist(vccode)){
				setError("零件已存在!");
			}else {
				SimpleDateFormat sf=new SimpleDateFormat();
				StringBuffer sql=new StringBuffer();
				String id="";
				String sql0="select jlcar.s_partid.Nextval as partid from dual";
				List ls=dbsession.openSelectbyList(sql0);
				Hashtable ht=(Hashtable)ls.get(0);
				id=ht.get("partid").toString();
				sql.append("insert into jlcar.smpart(id, vccode, vccname, vcename, vccnote, vcenote, icatalog, iphotoflag, ");
				sql.append("vclength, vcwidth, vcheight, vcweight, vcvolume, vcposition, isaleflag, dtupate) ");
				sql.append("values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,sysdate)");
				List param = new ArrayList();
				param.add(id);
				param.add(vccode);
				param.add(vccname);
				param.add(vcename);
				param.add(vccnote);
				param.add(vcenote);
				param.add(icatalog);
				param.add(iphotoflag);
				param.add(vclength);
				param.add(vcwidth);
				param.add(vcheight);
				param.add(vcweight);
				param.add(vcvolume);
				param.add(vcposition);
				param.add(isaleflag);
				boolean b1=dbsession.runSql(sql.toString(), param.toArray());
				boolean b2=true;
				/*if(price.equalsIgnoreCase("")==false)
				{
					StringBuffer sql1=new StringBuffer();				
					sql1.append("insert into jlcar.smpartprice(vccode, dprice, userid) values(?,?,?)");
					List param1 = new ArrayList();
					param1.add(vccode);
					param1.add(price);
					param1.add(priceuserid);
					b2=dbsession.runSql(sql1.toString(), param1.toArray());	
				}*/
				if(b1==false || b2==false){
					setError("数据库增加失败");
				}
				else
				{					
					ret=id;
				}
				dbsession.endlongTran(tran);
			}
		} catch (Exception e) {
			setError("新建失败");
			dbsession.rollbacklongTran(tran);
			throw e;
		} finally {
			dbsession.close();
			dbsession=null;	
		}
		return ret;
	}
	
	public String saveSmpart(String id, String vccode, String vccname, String vcename, 
			String vccnote, String vcenote, String icatalog, String iphotoflag, 
			String vclength, String vcwidth, String vcheight, String vcweight, 
			String vcvolume, String vcposition, String isaleflag,String changepartvccode,
			String vcmemo,String changeflag,String currentuserid,String priceuserid,String price) throws Exception {
		String ret="";	
		DBASession dbsession=getSession();
		Object tran=dbsession.startlongTran();
		boolean b=true;
		try
		{
			if(changeflag.equalsIgnoreCase("true") && isExist(changepartvccode)){
				setError("零件已存在!");
			}else {
				/*if(price.equalsIgnoreCase("")==false)
				{
					if(isExistPartPrice(priceuserid,vccode)==false)
					{
						StringBuffer sql1=new StringBuffer();				
						sql1.append("insert into jlcar.smpartprice(vccode, dprice, userid) values(?,?,?)");
						List param1 = new ArrayList();
						param1.add(vccode);
						param1.add(price);
						param1.add(priceuserid);
						b=dbsession.runSql(sql1.toString(), param1.toArray());
					}
					else
					{
						StringBuffer sql1=new StringBuffer();				
						sql1.append("update jlcar.smpartprice set dprice=? where vccode=? and userid=?");
						List param1 = new ArrayList();						
						param1.add(price);
						param1.add(vccode);
						param1.add(priceuserid);
						b=dbsession.runSql(sql1.toString(), param1.toArray());
					}
				}
				else
				{
					StringBuffer sql1=new StringBuffer();				
					sql1.append("delete from jlcar.smpartprice where vccode=? and userid=?");
					List param1 = new ArrayList();
					param1.add(vccode);
					param1.add(priceuserid);
					b=dbsession.runSql(sql1.toString(), param1.toArray());
				}*/
				if(b)
				{
					if(changeflag.equalsIgnoreCase("false"))
					{					
						StringBuffer sql=new StringBuffer();			
						sql.append("update jlcar.smpart set vccode=?,vccname=?,vcename=?, ");
						sql.append("vccnote=?,vcenote=?,icatalog=?,iphotoflag=?,vclength=?,vcwidth=?,vcheight=?,vcweight=?,"); 
						sql.append("vcvolume=?,vcposition=?,isaleflag=?,dtupate=sysdate where id=?");
						List param = new ArrayList();
						param.add(vccode);
						param.add(vccname);
						param.add(vcename);
						param.add(vccnote);
						param.add(vcenote);
						param.add(icatalog);
						param.add(iphotoflag);
						param.add(vclength);
						param.add(vcwidth);
						param.add(vcheight);
						param.add(vcweight);
						param.add(vcvolume);
						param.add(vcposition);
						param.add(isaleflag);
						param.add(id);					
						boolean isSuccess=dbsession.runSql(sql.toString(), param.toArray());				
						if(!isSuccess){
							setError("更新失败");
						}
						else
						{
							ret=id;
						}
					}
					else
					{					
						StringBuffer sql=new StringBuffer();			
						sql.append("update jlcar.smpart set vccode=?,vccname=?,vcename=?, ");
						sql.append("vccnote=?,vcenote=?,icatalog=?,iphotoflag=?,vclength=?,vcwidth=?,vcheight=?,vcweight=?,"); 
						sql.append("vcvolume=?,vcposition=?,isaleflag=?,dtupate=sysdate where id=?");
						List param = new ArrayList();
						param.add(changepartvccode);
						//param.add(vccode);
						param.add(vccname);
						param.add(vcename);
						param.add(vccnote);
						param.add(vcenote);
						param.add(icatalog);
						param.add(iphotoflag);
						param.add(vclength);
						param.add(vcwidth);
						param.add(vcheight);
						param.add(vcweight);
						param.add(vcvolume);
						param.add(vcposition);
						param.add(isaleflag);
						param.add(id);					
						boolean rtn=dbsession.runSql(sql.toString(), param.toArray());
						if(rtn)
						{
							StringBuffer sql0=new StringBuffer();							
							sql0.append("insert into smpartchangelog(id, iuserid, ipart1vccode, ipart2vccode, dtdate, vcmemo) values(jlcar.S_artChangeID.Nextval,?,?,?,sysdate,?)");
							List param0 = new ArrayList();
							param0.add(currentuserid);
							param0.add(changepartvccode);
							param0.add(vccode);
							param0.add(vcmemo);
							rtn=dbsession.runSql(sql0.toString(), param0.toArray());
							
							StringBuffer sql11=new StringBuffer();							
							sql11.append("insert into rl(part1id,part2id) values(?,?)");
							List param1 = new ArrayList();						
							param1.add(changepartvccode);
							param1.add(vccode);							
							rtn=dbsession.runSql(sql0.toString(), param1.toArray());
							ret=changepartvccode;
						}
					}
				}
			}			
			dbsession.endlongTran(tran);
		} catch (Exception e) {
			setError("保存失败");
			dbsession.rollbacklongTran(tran);
			throw e;
		} finally {
			dbsession.close();
			dbsession=null;	
		}
		return ret;
	}
	public List getSmparts() throws Exception {
		List ret=null;	
		DBASession dbsession=getSession();
		try
		{
			ret=dbsession.openSelectbyList("select * from jlcar.vsmpart");
			
		} catch (Exception e) {
			throw e;
		} finally {
			dbsession.close();
			dbsession=null;	
		}
		return ret;
	}
	public List getSmparts(String vccode) throws Exception {
		List ret=null;	
		DBASession dbsession=getSession();
		try
		{
			List ls=new ArrayList();
			ls.add(vccode);
			ret=dbsession.openSelectbyList("select a.* from jlcar.smpart a where a.vccode=?",ls.toArray());			
		} catch (Exception e) {
			throw e;
		} finally {
			dbsession.close();
			dbsession=null;	
		}
		return ret;
	}
	public List getSmpartQuery(String vccode) throws Exception {
		List ret=null;	
		DBASession dbsession=getSession();
		try
		{
			List ls=new ArrayList();
			if(vccode.equalsIgnoreCase("")==false)
			{
				ls.add("%"+vccode+"%");
				ret=dbsession.openSelectbyList("select a.*,row_number() over(ORDER BY id) AS pagerownum from jlcar.smpart a where a.vccode like ?",ls.toArray());
			}
			else
			{	ls.add("1");			
				ret=dbsession.openSelectbyList("select a.*,row_number() over(ORDER BY id) AS pagerownum from jlcar.smpart a where 1=?",ls.toArray());
			}
		} catch (Exception e) {
			throw e;
		} finally {
			dbsession.close();
			dbsession=null;	
		}
		return ret;
	}
	public List getSmparts(String vehid,String ihot,String imageid) throws Exception {
		List ret=null;	
		DBASession dbsession=getSession();
		try
		{
			List ls=new ArrayList();
			ls.add(vehid);
			ls.add(ihot);
			ls.add(imageid);
			//ls.add(vehid);
			//ls.add(ihot);
			//ls.add(imageid);
			/*String sql="select a.*,a.id as partid,b.iQty,b.ihot,a.vccode||','||b.ihot as key from jlcar.vsmpart a,jlcar.tcVehLink b where a.id=b.ipartid and b.ivehid=? and b.ihot=? and b.iimageid=?";
			sql=sql+" union ";
			sql=sql+" select c.*,c.id as partid,d.iQty,d.ihot,c.vccode||','||d.ihot as key from jlcar.vsmpart c,jlcar.tcasmlink d,jlcar.tcVehLink e where c.id=d.ipartid and d.iasmid=e.ipartid and e.ivehid=? and d.ihot=? and d.iimageid=?";*/
			String sql="select distinct a.*,(case when a.PHOTOCOUNT>0 then 'Y' else '' end) as P,'+' as O,getsmpartreplacerelation(a.vccode) as rl from jlcar.SMPARTVEHCACHE a where ivehid=? and ihot=? and iimageid=?";
			ret=dbsession.openSelectbyList(sql,ls.toArray());
			
		} catch (Exception e) {
			throw e;
		} finally {
			dbsession.close();
			dbsession=null;	
		}
		return ret;
	}
	public List getSmpartByQuery(String vehid,String pn,String pd) throws Exception {
		List ret=null;	
		DBASession dbsession=getSession();
		try
		{
			List ls=new ArrayList();			
			if(pn==null) pn="";
			if(pd==null) pd="";
			String sql="";
			if(pn.equalsIgnoreCase("") && pd.equalsIgnoreCase(""))
			{
				ls.add(vehid);
				ls.add(vehid);
				sql="select a.*,a.id as partid,b.iQty,b.ihot,b.iimageid,a.vccode || ',' || b.ihot as key,f.fullmenu,f.t2,f.vccode as imagevccode from jlcar.vsmpart a, jlcar.tcVehLink b,vtcimage f";
				sql=sql+" where a.id = b.ipartid and b.iimageid=f.ID";
				sql=sql+" and b.ivehid = ? and b.iimageid > 0 ";
				sql=sql+" union ";
				sql=sql+" select c.*,c.id as partid,d.iQty,d.ihot,d.iimageid,c.vccode || ',' || d.ihot as key,g.fullmenu,g.t2,g.vccode as imagevccode from jlcar.vsmpart c, jlcar.tcasmlink d, jlcar.tcVehLink e,vtcimage g";
				sql=sql+" where c.id = d.ipartid and d.iimageid=g.ID and d.iasmid = e.ipartid ";
				sql=sql+" and e.ivehid = ? and d.iimageid > 0";
			}
			else
			{
				if(pn.equalsIgnoreCase("")==false && pd.equalsIgnoreCase("")==false)
				{
					ls.add(vehid);
					ls.add("%"+pn+"%");
					ls.add("%"+pd+"%");
					ls.add(vehid);
					ls.add("%"+pn+"%");
					ls.add("%"+pd+"%");
					sql="select a.*,a.id as partid,b.iQty,b.ihot,b.iimageid,a.vccode || ',' || b.ihot as key,f.fullmenu,f.t2,f.vccode as imagevccode from jlcar.vsmpart a, jlcar.tcVehLink b,vtcimage f";
					sql=sql+" where a.id = b.ipartid and b.iimageid=f.ID";
					sql=sql+" and b.ivehid = ? and a.vccode like ? and a.vcename like ? and b.iimageid > 0 ";
					sql=sql+" union ";
					sql=sql+" select c.*,c.id as partid,d.iQty,d.ihot,d.iimageid,c.vccode || ',' || d.ihot as key,g.fullmenu,g.t2,g.vccode as imagevccode from jlcar.vsmpart c, jlcar.tcasmlink d, jlcar.tcVehLink e,vtcimage g";
					sql=sql+" where c.id = d.ipartid and d.iimageid=g.ID and d.iasmid = e.ipartid ";
					sql=sql+" and e.ivehid = ? and c.vccode like ? and c.vcename like ? and d.iimageid > 0";
				}
				else
				{
					if(pn.equalsIgnoreCase("")==false)
					{
						ls.add(vehid);
						ls.add("%"+pn+"%");
						ls.add(vehid);
						ls.add("%"+pn+"%");
						sql="select a.*,a.id as partid,b.iQty,b.ihot,b.iimageid,a.vccode || ',' || b.ihot as key,f.fullmenu,f.t2,f.vccode as imagevccode from jlcar.vsmpart a, jlcar.tcVehLink b,vtcimage f";
						sql=sql+" where a.id = b.ipartid and b.iimageid=f.ID";
						sql=sql+" and b.ivehid = ? and a.vccode like ? and b.iimageid > 0 ";
						sql=sql+" union ";
						sql=sql+" select c.*,c.id as partid,d.iQty,d.ihot,d.iimageid,c.vccode || ',' || d.ihot as key,g.fullmenu,g.t2,g.vccode as imagevccode from jlcar.vsmpart c, jlcar.tcasmlink d, jlcar.tcVehLink e,vtcimage g";
						sql=sql+" where c.id = d.ipartid and d.iimageid=g.ID and d.iasmid = e.ipartid ";
						sql=sql+" and e.ivehid = ? and c.vccode like ? and d.iimageid > 0";
					}
					else
					{
						ls.add(vehid);
						ls.add("%"+pd+"%");
						ls.add(vehid);
						ls.add("%"+pd+"%");
						sql="select a.*,a.id as partid,b.iQty,b.ihot,b.iimageid,a.vccode || ',' || b.ihot as key,f.fullmenu,f.t2,f.vccode as imagevccode from jlcar.vsmpart a, jlcar.tcVehLink b,vtcimage f";
						sql=sql+" where a.id = b.ipartid and b.iimageid=f.ID";
						sql=sql+" and b.ivehid =? and a.vcename like ? and b.iimageid > 0 ";
						sql=sql+" union ";
						sql=sql+" select c.*,c.id as partid,d.iQty,d.ihot,d.iimageid,c.vccode || ',' || d.ihot as key,g.fullmenu,g.t2,g.vccode as imagevccode from jlcar.vsmpart c, jlcar.tcasmlink d, jlcar.tcVehLink e,vtcimage g";
						sql=sql+" where c.id = d.ipartid and d.iimageid=g.ID and d.iasmid = e.ipartid ";
						sql=sql+" and e.ivehid = ? and c.vcename like ? and d.iimageid > 0";
					}
				}
			}
			ret=dbsession.openSelectbyList(sql,ls.toArray());
			
		} catch (Exception e) {
			throw e;
		} finally {
			dbsession.close();
			dbsession=null;	
		}
		return ret;
	}
	
	public List getSmpartsbyImageId(String vehid,String imageid) throws Exception{
		List ret=null;	
		DBASession dbsession=getSession();
		try
		{
			List ls=new ArrayList();
			if(imageid.equalsIgnoreCase("undefined")) imageid="-2";
			ls.add(vehid);
			ls.add(imageid);
			//ls.add(vehid);
			//ls.add(imageid);
			//String sql="select * from (select a.*,a.id as partid,b.iQty,b.ihot,a.vccode||','||b.ihot as key from jlcar.vsmpart a,jlcar.tcVehLink b where a.id=b.ipartid and b.ivehid=? and b.iimageid=? and b.ihot>0";
			//sql=sql+" union ";
			//sql=sql+" select c.*,c.id as partid,d.iQty,d.ihot,c.vccode||','||d.ihot as key from jlcar.vsmpart c,jlcar.tcasmlink d,jlcar.tcVehLink e where c.id=d.ipartid and d.iasmid=e.ipartid and e.ivehid=? and d.iimageid=? and d.ihot>0) order by ihot";
			String sql="select distinct a.*,(case when a.PHOTOCOUNT>0 then 'Y' else '' end) as P,'+' as O,getsmpartreplacerelation(a.vccode) as rl from jlcar.SMPARTVEHCACHE a where ivehid=? and iimageid=? order by ihot ";
			ret=dbsession.openSelectbyList(sql,ls.toArray());
		} catch (Exception e) {
			throw e;
		} finally {
			dbsession.close();
			dbsession=null;	
		}
		return ret;
	}
	public String getUserPartPrice(String priceuserid,String vccode) throws Exception{
		List ret=null;	
		DBASession dbsession=getSession();		
		String rtn="";		
		try
		{
			List ls=new ArrayList();
			ls.add(vccode);
			ls.add(priceuserid);
			String sql="select * from smpartprice t where vccode=? and userid=?";			
			ret=dbsession.openSelectbyList(sql,ls.toArray());
			if(ret.size()==1)
			{
				Hashtable ht=(Hashtable)ret.get(0);
				rtn=ht.get("dprice").toString();
			}
		} catch (Exception e) {
			throw e;
		} finally {
			dbsession.close();
			dbsession=null;	
		}
		return rtn;
	}
	
	public List getSmpartsByVehId(String vehid) throws Exception {
		List ret=null;	
		DBASession dbsession=getSession();
		try
		{
			List ls=new ArrayList();
			ls.add(vehid);
			//ls.add(vehid);
			/*String sql="select a.*,a.id as partid,b.iQty,b.ihot,a.vccode||','||b.ihot as key from jlcar.vsmpart a,jlcar.tcVehLink b where a.id=b.ipartid and b.ivehid=? and b.ihot>0";
			sql=sql+" union ";
			sql=sql+" select c.*,c.id as partid,d.iQty,d.ihot,c.vccode||','||d.ihot as key from jlcar.vsmpart c,jlcar.tcasmlink d,jlcar.tcVehLink e where c.id=d.ipartid and d.iasmid=e.ipartid and e.ivehid=? and d.ihot>0";
			*/
			String sql="select distinct a.*,(case when a.PHOTOCOUNT>0 then 'Y' else '' end) as P,'+' as O,getsmpartreplacerelation(a.vccode) as rl from jlcar.SMPARTVEHCACHE a where ivehid=? order by iimageid,ihot";
			ret=dbsession.openSelectbyList(sql,ls.toArray());			
		} catch (Exception e) {
			throw e;
		} finally {
			dbsession.close();
			dbsession=null;	
		}
		return ret;
	}
	
	public List getpartprice(String username,String partvccode) throws Exception
	{
		List ret=null;	
		DBASession dbsession=getSession();
		try
		{
			List ls=new ArrayList();
			String sql="";
			if(partvccode.equalsIgnoreCase("")==false)
			{
				if(username.equalsIgnoreCase(""))
				{
					sql="select * from (select '<input size=10  onchange=\"changeprice();\" type=text value=\"'||(case when substr(price,0,1)='.' then '0'||price else price end)||'\" id=\"'|| username ||'\"&gt;' as priceexpress,username,usercn,to_number(price) as price from vuserpartprice where partvccode=?";
					sql=sql+" union "; 
					sql=sql+" select '<input onchange=\"changeprice();\" size=10 type=text value=\"\" id=\"'|| vccode ||'\"&gt;' as priceexpress,vccode as username,vccname as usercn,null as price from vsmuser where vccode not in (select username from vuserpartprice where partvccode=?)) order by price,username ";
					ls.add(partvccode);
					ls.add(partvccode);
				}
				else
				{
					sql="select * from (select '<input onchange=\"changeprice();\" size=10 type=text value=\"'||(case when substr(price,0,1)='.' then '0'||price else price end)||'\" id=\"'|| username ||'\"&gt;' as priceexpress,username,usercn,to_number(price) as price from vuserpartprice where partvccode=?";
					sql=sql+" union "; 
					sql=sql+" select '<input onchange=\"changeprice();\" size=10 type=text value=\"\" id=\"'|| vccode ||'\"&gt;' as priceexpress,vccode as username,vccname as usercn,null as price from vsmuser where vccode not in (select username from vuserpartprice where partvccode=?)) where username=? order by price,username ";
					ls.add(partvccode);
					ls.add(partvccode);
					ls.add(username);
				}
			}
			else
			{				
				sql=" select '' as priceexpress,'' as username,'' as usercn from vsmuser where 1=?";
				ls.add("2");
			}
			ret=dbsession.openSelectbyList(sql,ls.toArray());			
		} catch (Exception e) {
			throw e;
		} finally {
			dbsession.close();
			dbsession=null;	
		}
		return ret;
	}
	
	public List getSmparts(String partid,String partdesc) throws Exception
	{
		List ret=null;	
		DBASession dbsession=getSession();
		try
		{
			List ls=new ArrayList();			
			String sql="select id,vccode,vccname,vccnote from jlcar.smpart a where 1=1 ";
			if(partid.equalsIgnoreCase("")==false)
			{
				sql=sql+" and vccode like ?";
				ls.add("%"+partid+"%");
			}
			if(partdesc.equalsIgnoreCase("")==false)
			{
				sql=sql+" and vccnote like ?";
				ls.add("%"+partdesc+"%");
			}			
			ret=dbsession.openSelectbyList(sql,ls.toArray());
			
		} catch (Exception e) {
			throw e;
		} finally {
			dbsession.close();
			dbsession=null;	
		}
		return ret;
	}
	public List getSmpartchangelog(String vccode) throws Exception {
		List ret=null;	
		DBASession dbsession=getSession();
		try
		{
			List ls=new ArrayList();			
			String sql="select id, iuserid, ipart1vccode, ipart2vccode, to_char(dtdate,'yyyy-mm-dd hh24:mi:ss') as changedate, vcmemo from smpartchangelog";
			if(vccode.equalsIgnoreCase("")==false)
			{
				sql=sql+" where IPART1VCCODE=?";
				ls.add(vccode);
			}			
			ret=dbsession.openSelectbyList(sql,ls.toArray());
			
		} catch (Exception e) {
			throw e;
		} finally {
			dbsession.close();
			dbsession=null;	
		}
		return ret;
	}
	public void setpartprice(String username,String partvccode,String price) throws Exception
	{
		List ret=null;	
		DBASession dbsession=getSession();		
		try
		{
			List ls=new ArrayList();
			String sql="select id as userid from smuser t where t.vccode=?";
			ls.add(username);
			ls=dbsession.openSelectbyList(sql,ls.toArray());
			if(ls.size()==1)
			{
				String userid="";
				Hashtable ht=(Hashtable)ls.get(0);
				userid=ht.get("userid").toString();
				sql="select * from smpartprice  where userid=? and vccode=?";
				ls.clear();
				ls.add(userid);
				ls.add(partvccode);	
				List pricels=dbsession.openSelectbyList(sql,ls.toArray());
				if(pricels.size()>0)
				{
					List pls=new ArrayList();
					sql="update smpartprice set dprice=? where userid=? and vccode=?";			
					pls.add(price.trim());
					pls.add(userid);
					pls.add(partvccode);						
					dbsession.runSql(sql,pls.toArray());
				}
				else
				{
					List pls=new ArrayList();
					sql="insert into smpartprice(dprice,userid,vccode) values(?,?,?)";			
					pls.add(price.trim());
					pls.add(userid);
					pls.add(partvccode);						
					dbsession.runSql(sql,pls.toArray());
				}
			}
		} catch (Exception e) {
			throw e;
		} finally {
			dbsession.close();
			dbsession=null;	
		}
		return;		
	}
	public List AdvanceQueryPart(String pn,String pd,String vin,String sap,String userid,String year,String sys,String subsys,String series,String model ) throws Exception 
	{
		List ret=null;	
		DBASession dbsession=getSession();                        
		try
		{
			List ls=new ArrayList();
			String sql="";
			if(userid.trim().equalsIgnoreCase("")==false)
			{		
				sql="select b.*,'+' as O,row_number() over(ORDER BY b.key)AS pagerownum from (select distinct t.pn,t.pd,decode(y.myprice,'',y.commonprice,y.myprice) as price,key from vsmpartforsearch t,(select vccode,sum(decode(userid,0,dprice,'')) as commonprice,sum(decode(userid,?,dprice,'')) as myprice from smpartprice t group by vccode) y where 1=1 and t.pn = y.vccode(+) ";				
				ls.add(userid);
				if(userid.trim().equalsIgnoreCase("")==false)
				{				
					sql=sql+" and t.vinuserid=?";				
					ls.add(userid);
				}
				if(year.trim().equalsIgnoreCase("-1")==false)
				{				
					sql=sql+" and t.vcyear=?";				
					ls.add(year);
				}
			}
			else
			{
				if(year.trim().equalsIgnoreCase("-1") && vin.trim().equalsIgnoreCase(""))
					sql="select b.*,'+' as O,row_number() over(ORDER BY b.key) AS pagerownum from (select distinct t.pn, t.pd, y.dprice as price,key from vsmpartforsearchforadmin t,smpartprice y where 1 = 1 and t.pn = y.vccode(+)";
				else					
					sql="select b.*,'+' as O,row_number() over(ORDER BY b.key) AS pagerownum from (select distinct t.pn, t.pd, y.dprice as price,key from vsmpartforsearch t,smpartprice y where 1 = 1 and t.pn = y.vccode(+)";
			}			
			
			if(sys.trim().equalsIgnoreCase("-1")==false)
			{				
				sql=sql+" and t.t1d=?";				
				ls.add(sys);
			}
			if(subsys.trim().equalsIgnoreCase("-1")==false)
			{				
				sql=sql+" and t.t2d=?";				
				ls.add(subsys);
			}
			if(model.trim().equalsIgnoreCase("-1")==false)
			{				
				sql=sql+" and t.imodelid=?";				
				ls.add(model);
			}
			if(series.trim().equalsIgnoreCase("-1")==false)
			{				
				sql=sql+" and iseriesid=?";				
				ls.add(series);
			}
			if(pn.trim().equalsIgnoreCase("")==false && pn.trim().equalsIgnoreCase("-9999999999")==false)
			{				
				//sql=sql+" and t.pn like ?";				
				//ls.add("%"+pn.trim()+"%");
				sql=sql+" and t.pn=?";
				ls.add(pn.trim());
			}
			if(pd.trim().equalsIgnoreCase("")==false)
			{				
				//sql=sql+" and t.pd like ?";				
				//ls.add("%"+pd.trim()+"%");
				sql=sql+" and t.pd=?";
				ls.add(pd.trim());
			}
			if(sap.trim().equalsIgnoreCase("")==false)
			{				
				//sql=sql+" and t.sap like ?";				
				//ls.add("%"+sap.trim()+"%");
				sql=sql+" and t.sap=?";
				ls.add(sap.trim());
			}
			if(vin.trim().equalsIgnoreCase("")==false)
			{				
				//sql=sql+" and t.vcvin like ?";				
				//ls.add("%"+vin.trim()+"%");
				sql=sql+" and t.vcvin=?";
				ls.add(vin.trim());
			}
			
			if(userid.trim().equalsIgnoreCase("")==false)
				sql=sql+" and rownum<29999 ";
			else				
				sql=sql+" and rownum<29999) b ";
			
			if(userid.trim().equalsIgnoreCase("")==false)
			{
				sql=sql+" group by t.pn,t.pd,y.myprice,y.commonprice,key) b";
			}
			if(userid.trim().equalsIgnoreCase("")==false)
			{
				ret=dbsession.openSelectbyList(sql,ls.toArray());
			}
			else
			{
				if(ls.size()==0)
				{					
					ret=new ArrayList();//dbsession.openSelectbyList(sql,ls.toArray());//管理员没有条件不允许查询
				}
				else
				{
					ret=dbsession.openSelectbyList(sql,ls.toArray());
				}
			}
		} catch (Exception e) {
			throw e;
		} finally {
			dbsession.close();
			dbsession=null;	
		}
		return ret;		
	}
}