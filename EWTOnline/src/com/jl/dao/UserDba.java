package com.jl.dao;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.PageContext;
import com.jl.dao.base.impl.BaseData;
import com.jl.dao.base.impl.DBASession;
import com.jl.dao.base.impl.SessionFactory;
import com.jl.entity.UserBean;

public class UserDba extends BaseData
{
	public UserDba(HttpServletRequest request)
	{
		super(request);
		this.request=request;
	}
	
	private String getUserid(int mode)
	{
		if(mode==0)	return (String)request.getSession().getAttribute("userid");
		if(mode==1)	return (String)request.getSession().getAttribute("userdd");
		String userid=(String)request.getSession().getAttribute("userdd");
		if(userid!=null && userid.isEmpty()) userid=(String)request.getSession().getAttribute("userid");
		return userid;
	}
	
	private boolean IsValidUser()
	{
		return (String)request.getSession().getAttribute("userid")!=null;
	}
	
	public String login(String username,String password,String ip) throws Exception
	{
		String ret="",sql;
		DBASession db = getSession();
		Object ob = null;
		try
		{
			sql="select id,iflag,vcpassa,vcpassb,vcpassc,vcename from smuser where vccode='"+username+"' and rownum<2";
			List ls = db.openSelectbyList(sql.toString());
			if(ls.size()<1) ret="Invalid username!";
			else
			{
				Hashtable ht = (Hashtable)ls.get(0);
				String stype="";
				if(password.equalsIgnoreCase(ht.get("vcpassa").toString()))      stype="1";
				else if(password.equalsIgnoreCase(ht.get("vcpassb").toString())) stype="2";
				else if(password.equalsIgnoreCase(ht.get("vcpassc").toString())) stype="3";
				if(stype.isEmpty())ret="Error password!";
				else
				{
					ret="success";
					String sid = ht.get("id").toString();
					String sdd;
					String sflag = ht.get("iflag").toString();
					String sname;
					if(sflag.equalsIgnoreCase("3"))
					{
						sdd=sid;
						sname = ht.get("vcename").toString();
					}
					else
					{
						sdd="";
						sname="All";
					}
					
					request.getSession().setMaxInactiveInterval(14400);
					request.getSession().setAttribute("userid",sid);
					request.getSession().setAttribute("userdd",sdd);
					request.getSession().setAttribute("username",sname);
					request.getSession().setAttribute("userflag",sflag);
					request.getSession().setAttribute("usertype",stype);
					
					sql = "insert into smvisit(iuser,vcip,itype,ddate) values("+sid+",'"+ip+"',"+stype+",sysdate)";
					ob = db.startlongTran();
					db.runSql(sql.toString());
					db.endlongTran(ob);
				}
			}
		} 
		catch (Exception e){ db.rollbacklongTran(ob); throw e; }
		finally	{ db.close(); db = null; }
		return ret;
	}
	
	public List getSeries() throws Exception
	{
		String userid=getUserid(1);
		if(userid==null) return null;
		String sql;
		if(userid.equalsIgnoreCase("")) sql = "select id,vcename from tcseries order by iseq";
		else sql = "select distinct c.id,c.vcename,c.iseq from tcvin a,tcveh b,tcseries c where a.iuserid="+userid+" and a.ivehid=b.id and c.id=b.iseriesid order by c.iseq";
		return getData(sql,null);
	}
	
	
	
	public List getVeh(int iPage,int nPageSize,String iSeries,String iYear,String iBrand,String iModel,String iEmission,
						String iFuel,String iCabin,String iWB,String iDrive,String sVin) throws Exception
	{
		String userid=getUserid(1);
		if(userid==null) return null;
		
		String s, v[] = getVinString(sVin);	//code,vin,esn
		UserBean d=new UserBean();
		boolean ret = d.append1("b.iuserid", userid, 1);
		if(!d.append1("b.iyear", iYear, 1) && ret) d.append1("b.iyear>0");
		d.append1("b.vcvin", v[1], 3);
		d.append1("b.vcesn", v[2], 3);
		v[1] = d.result();
		
		d.Empty();
		d.append1("a.iseriesid", iSeries, 1);
		d.append1("a.ibrandid", iBrand, 1);
		d.append1("a.imodelid", iModel, 1);
		d.append1("a.iEmission", iEmission, 1);
		d.append1("a.ifuel", iFuel, 1);
		d.append1("a.iCabin", iCabin, 1);
		d.append1("a.iwb", iWB, 1);
		d.append1("a.idrive", iDrive, 1);
		ret = d.append1("c.vccode||'/'||a.vccode", v[0], 3);
		v[0] = d.result();
		
		s = "with t as (";
		if(v[1].isEmpty())
		{
			s += "select rownum as ln,a.id from tcveh a";
			if(ret) s += ",tcmodel c where " + v[0] + " and c.id=a.imodelid";
			else if(!v[0].isEmpty()) s += " where " + v[0];			
		}
		else
		{
			s += "select rownum as ln,d.id from (select distinct b.ivehid as id from tcvin b where " + v[1];
			if(!v[0].isEmpty())
			{
				s += " intersect select a.id from tcveh a";
				if(ret) s += ",tcmodel c";
				s += " where " + v[0];
				if(ret) s += " and c.id=a.imodelid";
			}			
			s += ") d";
		}
		s += ")";
		if(iPage <= 0)
		{
			s += " select max(ln) id,'' A,'' B,'' C,'' D,'' E,'' F,'' G,'' H,'' I,'' J,'' K,'' L,'' M from t union all";
		}
		
		if(nPageSize < 1) nPageSize = 25;
		iPage = iPage > 0 ? iPage-1 : 0;
		iPage = iPage*nPageSize;		
		s += " select a.id" + getVehSQL(0) + " from tcveh a where a.id in(select id from t where ln>" + iPage + " and ln<" + (iPage+nPageSize+1) + ")";
		return getData(s, null);
	}
	
	public List getVeh(int iPage, int nPageSize, String partid) throws Exception
	{
		if(partid == null || partid.isEmpty()) return null;
		String userid = getUserid(1);
		if(userid == null) return null;
		
		String s;
		s  = "with t as (select rownum as ln,d.id from (select distinct ivehid as id from tcvehlink where ipartid=" + partid;
		s += " union select distinct a.ivehid as id from tcvehlink a,tcasmlink b where b.ipartid=" + partid + " and b.iasmid=a.ipartid";
		if(!userid.isEmpty()) s += " intersect select distinct ivehid as id from tcvin where iyear>0 and iuserid=" + userid;
		s += ") d)";	
			
		if(iPage <= 0)
		{
			String cs = "'' B,'' C,'' D,'' E,'' F,'' G,'' H,'' I,'' J,'' K,'' L,'' M";
			s += " select max(ln) as id,'' as A," + cs + " from t";
			s += " union all select count(c.id) as id,'' as A," + cs + " from tcvin c inner join t on t.id=c.ivehid";
			s += " union all select distinct 0 as id,w.vccode as A," + cs + " from t left join tcveh v on v.id=t.id left join tcbrand w on w.id=v.ibrandid";
			s += " union all";
		}
		if(nPageSize < 1) nPageSize = 25;
		iPage = iPage > 0 ? iPage-1 : 0;
		iPage = iPage*nPageSize;		
		s += " select a.id" + getVehSQL(0) + " from tcveh a where a.id in(select id from t where ln>" + iPage + " and ln<" + (iPage+nPageSize+1) + ")";
		return getData(s, null);
	}
	
	
	
	public List getVehLink(String vehid) throws Exception
	{
		String s="iimageid as a,ipartid as b,ihot as c,iqty as d";
		String sql = "with tmp as (select distinct " + s + " from tcvehlink where ivehid=" + vehid + ")";
		sql += " select * from tmp union select " + s + " from tcasmlink where iasmid in (select distinct b from tmp)";
		return getData(sql,null);
	}
	
	
	public List getVehTree(String s) throws Exception
	{
		if(s.isEmpty())return null;
		String sql;	
		sql="select m.id as id,m.t1d as d1,t1.vcename as n1,m.t2d as d2,t2.vcename as n2,m.vccode as d3,t3.vcename as n3,m.pdate as t from ((tcimage m left join tct1 t1 on t1.id=m.t1d) left join tct2 t2 on t2.id=m.t2d) left join tct3 t3 on t3.id=m.t3d where m.id in("+s+")  order by m.seq";	
		return getData(sql,null);
	}
	
	public List getVehInfo(String vehid) throws Exception
	{
		String sql="select b.vccode||'/'||a.vccode as a,a.ibrand as b from  tcveh a left join tcmodel b on b.id=a.imodelid where a.id ="+vehid;
		return getData(sql,null);
	}
	
	public List getVIPart(String s) throws Exception
	{
		if(s == null || s.isEmpty()) return null;
		String userid = getUserid(2);
		if(userid == null) return null;
		
		String cs = "";
		String[] ar1 = s.split(";");
		String[] ar2 = null;
		for(int i=0; i<ar1.length; i++)
		{
			ar2 = ar1[i].split(",");
			if(!cs.isEmpty()) cs += " union all ";
			cs += "select " + ar2[0] + " as p," + ar2[1] + " as h," + ar2[2] + " as q from dual";
			ar2 = null;
		}
		ar1 = null;
		if(cs.isEmpty()) return null;
		
		s = request.getSession().getAttribute("usertype").toString();
		int type = s==null ? 0 : Integer.parseInt(s);
	
		// -A:HOT   -B:P/N	 -C:P/D	   -D:SUSPERED		 -E:QTY		 -F:PHOTO		 G:ORDER		 -N:PART ID		 
		s  = "select a.p as n,b.vccode as b,b.vcename as c";
		s += ",case when a.h=0 then 255 else a.h end as z";
		s += ",case when a.h=0 then '' else to_char(a.h) end as a";
		s += ",case when a.q=255 then 'X' else to_char(a.q) end as e";
		s += ",case when b.iphotoflag>0 then '<span onclick=''OnViewPhoto(this)'' title=''View Photo''>Y</span>' else '' end as f";
		s += ",case when b.newid>0 then '<span onclick=''OnViewSupersed(this)'' title=''View Superseded''>Y</span>' else '' end as d";
		if(type == 1) s += ",case when b.newid<1 and c.id is null then '<span onclick=''OnAddCart(this)'' title=''Add to Cart''><b>+</b></span>' else '' end as g";
		s += " from (" + cs + ") a left join smpart b on b.id=a.p";
		if(type == 1) s += " left join (select ipart as id from tccart where iuser=" + userid + ") c on c.id=a.p";
		s += " order by z";
		return getData(s, null);
	}
	
	public List getCart(boolean bAll) throws Exception
	{
		String userid = getUserid(2);
		if(userid == null)return null;
		String s = "select a.ipart as a,b.vccode as b,b.vcename as c,'<input id='||a.ipart||' type=text maxlength=2 value='''||a.iqty||''' tag='''||a.iqty||''' onchange=''OnSaveQty(this)''/>' as d,'<span onclick=''OnRemove(this)'' title=''Remove''>X</span>' as e from tccart a left join smpart b on b.id=a.ipart where a.iuser="+userid+" order by a.ddate desc";
		//String s = "select a.ipart as a,b.vccode as b,b.vcename as c,'<input id='||a.ipart||' type=text maxlength=2 value='''||a.iqty||''' onchange=''saveqty()'' style=''border:0px;text-align:center;width:expression(this.parentElement.clientWidth)''/>' as d,'X' as e from tccart a left join smpart b on b.id=a.ipart where a.iuser="+userid+" order by a.ddate desc";
		if(!bAll) s = "select * from (" + s + ") where rownum<4";
		return getData(s,null);
	}
	
	public List getQuickPart(String pn, String pd, String s) throws Exception
	{
		if(!IsValidUser()) return null;
		if(s == null) return null;
		
		String s1 = "", s2 = "";		
		if(pn!=null && !pn.isEmpty())
		{
			s1 = "a.vccode like '%" + pn.trim().toUpperCase() + "%'";
		}
		if(pd!=null && !pd.isEmpty())
		{
			if(!s1.isEmpty())s1 += " and ";
			s1 += "a.vcename like '%" + pd.trim().toUpperCase() + "%'";
		}
		
		String[] ar1 = s.split(";");
		String[] ar2 = null;
		for(int i=0; i<ar1.length; i++)
		{
			ar2 = ar1[i].split(",");
			if(!s2.isEmpty()) s2 += " union all ";
			s2 += "select " + ar2[0] + " as m," + ar2[1] + " as p," + ar2[2] + " as h from dual";
			ar2 = null;
		}
		ar1 = null;
		if(s2.isEmpty()) return null;
		
		//a.iphotoflag as d,a.newid as e,
		s="select a.id as a,a.vccode as b,a.vcename as c,c.id as f,c.vccode as g,d.vcename as h from smpart a,(";
		s += s2;
		s+=") b,tcimage c,tct3 d where a.id=b.p and c.id=b.m and d.id=c.t3d";
		if(!s1.isEmpty()) s += " and " + s1;
		return getData(s, null);
	}
	
	public boolean deleteCart(String partid) throws Exception 
	{
		String userid = getUserid(2);
		if(userid == null)return false;
		String sql = "delete from tccart where ipart="+partid+" and iuser="+userid;
		return runSql(sql,null);
	}
	
	public boolean emptyCart() throws Exception 
	{
		String userid = getUserid(2);
		if(userid==null)return false;
		String sql="delete from tccart where iuser="+userid;
		return runSql(sql,null);
	}
	
	public boolean updateCartQty(String partid, String qty) throws Exception 
	{		
		String userid = getUserid(2);
		if(userid==null)return false;
		String sql="update tccart set iqty="+qty+" where ipart="+partid+" and iuser="+userid;
		return runSql(sql,null);
	}
	
	public boolean addCart(String partid, String qty) throws Exception 
	{		
		String userid = getUserid(2);
		if(userid==null)return false;
		List ls = new ArrayList();
		String sql="insert into tccart(ipart,iuser,iqty,ddate) values(?,?,?,sysdate)";
		ls.add(partid);
		ls.add(userid);
		ls.add(qty);
		return runSql(sql,ls);
	}
	
	public List getPartPhoto(String partid) throws Exception
	{
		if(!IsValidUser())return null;
		String s="select vccode,vcename from smpart where id="+partid;
		List lt=getData(s,null);
		if(lt.size()<1) return null;
		List ls=new ArrayList();
		Hashtable ht=(Hashtable)lt.get(0);
		ls.add(ht.get("vccode").toString());
		ls.add(ht.get("vcename").toString());
		lt.clear();

		s="select iphoto from smpartphoto where ipart="+partid;
		lt=getData(s,null);
		for(int i=0;i<lt.size();i++)
		{
			ht=(Hashtable)lt.get(i);
			ls.add(ht.get("iphoto").toString());	
		}
		return ls;
	}
	
	public List getHot(String id) throws Exception
	{
		String sql="select ihot,ix,iy,iw,ih from tchot where iimage="+id;
		return getData(sql,null);
	}
	
	public List getSearchOption(String type,String p1, String p2) throws Exception
	{
		String userid = getUserid(1);
		if(userid == null) return null;
		
		UserBean d = new UserBean();
		String s1, s2 = "";
		if(type.equalsIgnoreCase("Series"))
		{
			if(d.append1("a.iuser", userid, 1))
			{
				s1 = "select distinct c.id as code,c.vcename as name,c.iseq as iseq from tcvin a,tcveh b,tcseries c";
				d.append1("a.iveh=b.id and c.id=b.iseriesid");
			}
			else s1 = "select id as code,vcename as name from tcseries";
			s2 = "iseq";
		}
		else if(type.equalsIgnoreCase("Year"))
		{
			s1 = "select distinct iyear as code,to_char(iyear) as name from tcvin";
			d.append1("iuser", userid, 1);
			s2 = "name";
		}
		else if(type.equalsIgnoreCase("Brand"))
		{
			s1 = "select distinct b.ibrand as code,getname(b.ibrand,3) as name from tcveh b";
			if(d.append1("c.iuser", userid, 1))
			{
				s1 += ",tcvin c";
				d.append1("b.id=c.iveh");
			}
			d.append1("b.iseriesid", p1, 1);
			s2 = "name";
		}
		else if(type.equalsIgnoreCase("Model"))
		{
			s1 = "select distinct a.id as code,a.vccode as name from tcmodel a,tcveh b";
			if(d.append1("c.iuser", userid, 1))
			{
				s1 += ",tcvin c";
				d.append1("b.id=c.iveh");
			}
			d.append1("b.iseriesid", p1, 1);
			d.append1("b.ibrand", p2, 1);
			d.append1("a.id=b.imodelid");
			s2 = "name";
		}
		else if(type.equalsIgnoreCase("Sys"))
		{
			s1 = "select id as code,vcename as name from tct1";
			s2 = "iseq";
		}
		else if(type.equalsIgnoreCase("Sub"))
		{
			s1="select distinct b.id as code,b.vcename as name from tct2 b,tcimage c";
			d.append1("c.t1d", p1, 1);
			d.append1("b.id=c.t2d");
			s2 = "name";
		}
		else if(type.equalsIgnoreCase("Image"))
		{
			s1="select distinct a.id as code,a.vcename as name from tct3 a,tcimage c";
			d.append1("c.t1d", p1, 1);
			d.append1("c.t2d", p2, 1);
			d.append1("a.id=c.t3d");
			s2 = "name";
		}
		else return null;
		if(!d.isEmpty()) s1 += " where "+ d.result();
		if(!s2.isEmpty()) s1 += " order by " + s2;			
		return getData(s1, null);
	}
	
	public List getAdvancedLink(String pn,String pd,String svin,String year,String series,String brand,String model,String sys,String sub,String image) throws Exception
	{
		String userid = getUserid(1);
		if(userid == null) return null;
		String s = request.getSession().getAttribute("usertype").toString();
		if(s == null) return null;
		int type = Integer.parseInt(s);
		
		String code = null, vin = null, esn = null;
		if(svin!=null)
		{
			svin = svin.trim();
			if(svin.length()>1)
			{
				s = svin.substring(0, 1);
				svin = svin.substring(1).trim().toUpperCase();
				if(s.equalsIgnoreCase("1")) code = svin;
				else if(s.equalsIgnoreCase("2")) vin = svin;
				else if(s.equalsIgnoreCase("3")) esn = svin;
			}
		}
		
		// a: veh  b:part  c:image  d: vin  e:model
		String sa = "", sb = "", sc = "", sd = "";		
		UserBean d = new UserBean();
		boolean ret = false;
				
		d.Empty();
		ret = d.append1("d.iuser", userid, 1);
		if(!d.append1("d.iyear", year, 1) && ret) d.append1("d.iyear>0");
		d.append1("d.vcvin", vin, 3);
		d.append1("d.vcesn", esn, 3);
		sd = d.result();
		d.Empty();
		d.append1("a.iseriesid", series, 1);
		d.append1("a.ibrand", brand, 1);
		d.append1("a.imodelid", model, 1);
		ret = d.append1("e.vccode||'/'||a.vccode", code, 3);
		sa = d.result();
		d.Empty();
		d.append1("b.vccode", pn, 3);
		d.append1("b.vcename", pd, 3);
		sb = d.result();
		d.Empty();
		d.append1("c.t1d", sys, 1);
		d.append1("c.t2d", sub, 1);
		d.append1("c.t3d", image, 1);
		sc = d.result();
		
		s = "";
		if(!sa.isEmpty())
		{
			s = "select a.id from tcveh a";
			if(ret) s += ",tcmodel e";
			s += " where " + sa;
			if(ret) s += " and e.id=a.imodelid";
		}
		if(!sd.isEmpty())
		{
			if(!s.isEmpty()) s += " intersect ";
			s += "select distinct d.iveh as id from tcvin d where " + sd;
		}
		sa = s;		
		if(!sb.isEmpty()) sb = "select b.id from smpart b where " + sb;
		if(!sc.isEmpty()) sc = "select c.id from tcimage c where " + sc;
		
		d.Empty();
		if(!sb.isEmpty()) d.append4("sb as (" + sb +")");
		if(!sc.isEmpty()) d.append4("sc as (" + sc +")");
		if(!sa.isEmpty()) d.append4("sa as (" + sa +")");
		code = "with " + d.result();
		
		vin = "";
		if(!sb.isEmpty()) vin += ",sb";
		if(!sc.isEmpty()) vin += ",sc";
		if(!sa.isEmpty()) vin += ",sa";
		
		d.Empty();
		if(!sb.isEmpty()) d.append1("sb.id=v.ipartid");
		if(!sc.isEmpty()) d.append1("sc.id=v.iimageid");
		if(!sa.isEmpty()) d.append1("sa.id=v.ivehid");
		sd = "select distinct v.ipartid as p from tcvehlink v" + vin + " where "+ d.result();
		
		d.Empty();
		if(!sb.isEmpty()) d.append1("sb.id=f.ipartid");
		if(!sc.isEmpty()) d.append1("sc.id=f.iimageid");
		if(!sa.isEmpty()) d.append1("sa.id=v.ivehid");
		sd += " union select distinct f.ipartid as p from tcvehlink v,tcasmlink f" + vin + " where f.iasmid=v.ipartid and "+ d.result();
			
		/////////////////////
		vin = esn = "";
		if(type == 1) vin = userid.isEmpty() ? getUserid(0) : userid;  //cart
		if(type < 3 && !userid.isEmpty()) esn = userid;		//price
		
		s  = code;
		s  += " select p.id,p.vccode as a,p.vcename as b,p.newid as c,p.vcenote as d";
		s += ",case when p.newid>0 then '<span onclick=''OnViewSupersed(this)'' title=''View Superseded''>'||getname(p.newid,21)||'</span>' else '' end as e";
		s += ",case when p.iphotoflag>0 then '<span onclick=''OnViewPhoto(this)'' title=''View Photo''>Y</span>' else '' end as f";
		if(!vin.isEmpty()) s += ",case when p.newid<1 and ct.ipart is null then '<span onclick=''OnAddCart(this)'' title=''Add to Cart''><b>+</b></span>' else '' end as g";
		if(!esn.isEmpty()) s += ",'$'||to_char((case when pr.iprice is null then p.iprice else pr.iprice end),'fm99990.99') as h";
		s += " from smpart p";		
		if(!vin.isEmpty()) s += " left join tccart ct on ct.iuser=" + vin + " and ct.ipart=p.id";
		if(!esn.isEmpty())  s += " left join smprice pr on pr.iuser=" + esn + " and pr.ipart=p.id";
		s += " where p.id in (" + sd + ")";
		
		return getData(s,null);		
	}
	
	public List getPartPrice(String partid) throws Exception
	{
		if(partid == null || partid.isEmpty()) return null;
		String sql = "select '' as a,'$'||to_char(iprice,'fm99990.99') as b from smpart where id=" + partid + " and iprice>0";
		sql += "union all select u.vccname as a,'$'||to_char(p.iprice,'fm99990.99') as b from smprice p left join smuser u on u.id=p.iuser where p.ipart=" + partid + " order by a";
		return getData(sql, null);
	}
	
	public List getPartSupersed(String partid) throws Exception
	{
		if(partid.isEmpty())return null;
		String userid = getUserid(2);
		if(userid==null)return null;
		
		List lt=new ArrayList();
		DBASession db=getSession();
		String sql="select newid,vccode,vcenote,tdate from smpart where rownum<2 and id="+partid;
		List ls=getData(db,sql,null);
		if(ls.size()<1)return null;
		Hashtable h;
		h=(Hashtable)ls.get(0);
		partid=h.get("newid").toString();
		if(partid.equalsIgnoreCase("0"))return null;
		lt.add(partid);
		lt.add(h.get("vccode").toString());
		lt.add(h.get("vcenote").toString());
		lt.add(h.get("tdate").toString());
		ls.clear();
		sql="select vccode,vcename,newid,iphotoflag from smpart where rownum<2 and id="+partid;
		ls=getData(db,sql,null);
		if(ls.size()<1)return null;
		h=(Hashtable)ls.get(0);
		lt.add(h.get("vccode").toString());
		lt.add(h.get("vcename").toString());
		lt.add(h.get("newid").toString());
		lt.add(h.get("iphotoflag").toString());
		ls.clear();
		sql="select ipart from tccart where rownum<2 and iuser="+userid+" and ipart="+partid;
		ls=getData(db,sql,null);
		lt.add(ls.size()<1?"N":"Y");		
		return lt;
	}
	
	public List getVin(String vehid) throws Exception
	{
		String userid=getUserid(1);
		if(userid==null)return null;
		String sql="select a.vcvin as a,a.vcesn as b,a.vcbill as c,a.vcnote as d,b.vcename as e,to_char(a.ddate,'yyyy-mm-dd') as f from tcvin a left join smuser b on b.id=a.iuserid where a.ivehid="+vehid;
		if(!userid.isEmpty())sql+=" and a.iuserid="+userid;
		sql+=" order by c";
		return getData(sql,null);
	}
	
	public List getCountry() throws Exception
	{
		String sql="select id as a,vcename as b,vccname as c from smuser where iflag=3 order by vcename";
		return getData(sql,null);
	}
	
	public boolean saveSuggest(String id,String vehid,String imageid,String content) throws Exception
	{
		//if(content.length()>999)content=content.substring(0,999);
		String sql;
		if(id.equalsIgnoreCase("0"))
		{	
			String userid=getUserid(2);
			if(userid==null)return false;
			//DBASession db = getSession();
			sql="select max(id)+1 as maxid from smsuggest";
			List ls=getData(sql,null);
			id=getValue(ls,0,"maxid");
			if(id.isEmpty())id="1";
			sql="insert into smsuggest(id,imageid,vehid,userid,content,date1) values(";
			sql+=id+","+imageid+","+vehid+","+userid+",'"+content+"',sysdate)";
			return runSql(sql,null);
		}
		else
		{
			sql="update smsuggest set content='"+content+"',date1=sysdate where id="+id;
			return runSql(sql,null);
		}		
	}
	
	public List getSuggest(String imageid) throws Exception
	{
		String userid = getUserid(2);
		if(userid == null) return null;
		String sql = "select a.id as A,(select vccode from tcmodel z1 where z1.id=b.imodelid)||'/'||b.vccode as B,c.vccode as C,a.content as D,to_char(a.date1,'yyyy-mm-dd') as E,(case when a.deal>0 then '' else '<span onclick=\"OnRemove(this)\" title=\"Remove\">X</span>' end) as F,a.vehid as G,a.imageid as H,a.deal as I from smsuggest a,tcveh b,tcimage c where a.userid=" + userid;
		if(imageid!=null && !imageid.isEmpty()) sql += " and a.imageid=" + imageid;
		sql+=" and c.id=a.imageid and b.id=a.vehid order by a.date1 desc";
		return getData(sql,null);	
	}
	
	public boolean deleteSuggest(String id) throws Exception 
	{
		String sql="delete from smsuggest where id="+id;
		return runSql(sql,null);
	}
	
	public List getBulletin(int iPage, int nPageSize) throws Exception
	{
		String userid = getUserid(2);
		if(userid == null) return null;
		if(nPageSize < 1) nPageSize = 5;
		iPage = iPage > 0 ? iPage-1 : 0;
		iPage = iPage*nPageSize;
		String sql = "select id,A,B,C,D,E from (";
		sql += "select a.id,a.topic as A,to_char(a.ddate,'yyyy-mm-dd hh24:mi:ss') as B,case when a.top>0 then 'Y' else '' end as C,case when b.bulletinid is null then 'Y' else '' end as D,case when a.red>0 then 'Y' else '' end as E";
		sql += ",row_number() over(order by a.top desc,b.bulletinid desc,a.ddate desc) as ln";
		sql += " from smbulletin a left join smvistbulletin b on b.bulletinid=a.id and b.userid=" + userid;
		sql += ") where ln>" + iPage + " and ln<=" + (iPage+nPageSize);
		return getData(sql, null);
	}
	
	public String getVinCount(String vehid) throws Exception
	{
		String userid = getUserid(1);
		if(userid==null)return null;
		DBASession db=getSession();
		List ls = null;
		Hashtable ht = null;
		String sql, sfrom="",scountry="",svin="";
		
		sql = "select min(iyear) as A,count(id) as B from tcvin where iveh=" + vehid;
		if(!userid.isEmpty()) sql += " and iuser=" + userid;
		ls=getData(db,sql,null);
		if(ls!=null && ls.size()>0)
		{
			ht=(Hashtable)ls.get(0);
			sfrom = ht.get("a").toString();
			svin = ht.get("b").toString();
		}
		
		if(userid.isEmpty())
		{
			sql = "select count(1) as A from (select distinct iuser from tcvin where iveh=" + vehid + ")";
			ls=getData(db,sql,null);
			if(ls!=null && ls.size()>0)
			{
				ht=(Hashtable)ls.get(0);
				scountry = ht.get("a").toString();
			}
		}
		db = null;	if(ls!=null) ls.clear();	ls = null;	ht = null;		
		sql = "<b>Since:</b>&nbsp;" + sfrom;
		if(!scountry.isEmpty()) sql += "&nbsp;&nbsp;&nbsp;<b>Country:</b>&nbsp;"+scountry;
		sql += "&nbsp;&nbsp;&nbsp;<b>VIN:</b>&nbsp;"+svin;
		return sql;
	}
	
	public boolean setBulletinRead(String bulletinid) throws Exception 
	{
		if(bulletinid==null || bulletinid.isEmpty()) return false;
		String userid = getUserid(0);
		if(userid == null) return false;
		if(isExistEx("smvistbulletin", "bulletinid=" + bulletinid +" and userid=" + userid)) return true;
		String sql = "insert into smvistbulletin(bulletinid,userid) values(" + bulletinid + "," + userid + ")";
		return runSql(sql, null);
	}
	
	////////////////////////////////////////////////////////////
	
	public String getWall() throws Exception
	{
		String s = "select 'wall/'||to_char(id)||'.jpg?ver='||to_char(ddate,'yyyy-mm-dd hh24:mi:ss') as a from tcwall where flag=1 and rownum<2";
		List ls = getData(s, null);
		if(ls == null || ls.size()<1) return "";
		Hashtable ht = (Hashtable)ls.get(0);
		s = ht.get("a").toString();
		ls = null;
		return s;
	}
	
	private String[] getVinString(String sVin)
	{
		String cs,s[] = {"", "", ""};  //code,vin,esn
		if(sVin != null)
		{
			sVin = sVin.trim();
			if(sVin.length()>1)
			{
				cs = sVin.substring(0, 1);
				sVin = sVin.substring(1).trim().toUpperCase();
				if(cs.equalsIgnoreCase("1")) s[0] = sVin;
				else if(cs.equalsIgnoreCase("2")) s[1] = sVin;
				else if(cs.equalsIgnoreCase("3")) s[2] = sVin;
			}
		}
		return s;
	}
	
	public List getOption(String type,String vin,String seriesid) throws Exception
	{
		String userid = getUserid(1);;
		if(userid == null)return null;
						
		boolean a = false, b = false, c = false;  // a: veh   b: vin   c: model
		String s1="", s2="", s;
		if(type.equalsIgnoreCase("Year"))			{ b=true;	s1="b.iyear";	  	s2="to_char(b.iyear)";  }
		else if(type.equalsIgnoreCase("Brand"))		{ a=true;	s1="a.ibrandid";	s2=getVehItem(2,"a.ibrandid");	}
		else if(type.equalsIgnoreCase("Model"))		{ a=c=true;	s1="c.id";			s2="c.vccode";	}
		else if(type.equalsIgnoreCase("Emission"))	{ a=true;	s1="a.iemission";	s2=getVehItem(10,"a.iemission");	}
		else if(type.equalsIgnoreCase("Fuel"))		{ a=true;	s1="a.ifuel";		s2=getVehItem(9,"a.ifuel");	}
		else if(type.equalsIgnoreCase("Cabin"))		{ a=true;	s1="a.icabin";		s2=getVehItem(5,"a.icabin"); }
		else if(type.equalsIgnoreCase("WB"))		{ a=true;	s1="a.iwb";			s2="to_char(a.iwb)"; }
		else if(type.equalsIgnoreCase("Drive"))		{ a=true;	s1="a.idrive";		s2=getVehItem(8,"a.idrive"); }
		else return null;
		s = "select distinct "+s1+" as code,"+s2+" as name";
		
		UserBean d = new UserBean();
		String v[] = getVinString(vin);	//code,vin,esn
		if(d.append1("b.iuserid", userid, 1)) b = true;
		if(d.append1("a.iseriesid", seriesid, 1)) a = true;
		if(d.append1("c.vccode||'/'||a.vccode", v[0], 3)) a = c = true;
		if(d.append1("b.vcvin", v[1], 2)) b = true;
		if(d.append1("b.vcesn", v[2], 2)) b = true;
		v = null;
		s2 = d.result();
	
		d.Empty();
		if(a) d.append4("tcveh a");
		if(b) d.append4("tcvin b");
		if(c) d.append4("tcmodel c");
		s1 = d.result();
		
		d.Intial(s2);
		if(a && b) d.append1("a.id=b.ivehid");
		if(a && c) d.append1("c.id=a.imodelid");
		s2 = d.result();
		d = null;
		
		s += " from " + s1;	
		if(!s2.isEmpty()) s += " where " + s2;
		s += " order by name";
		return getData(s, null);
	}
}