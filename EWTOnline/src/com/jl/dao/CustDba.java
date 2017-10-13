package com.jl.dao;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

//import com.jl.dao.base.impl.DBASession;
import com.jl.entity.UserBean;

public class CustDba extends BaseDba
{
	HttpServletRequest request;
	public CustDba(HttpServletRequest request)
	{
		this.request = request;
	}
	
	public void Close() throws Exception
	{
		CloseDba();
	}
	
	private String getUserid(int mode)
	{
		if(mode==0)	return (String)request.getSession().getAttribute("userid");
		if(mode==1)	return (String)request.getSession().getAttribute("userdd");
		String userid=(String)request.getSession().getAttribute("userdd");
		if(userid!=null && userid.isEmpty()) userid=(String)request.getSession().getAttribute("userid");
		return userid;
	}
	
	public int login(String username, String password) throws Exception
	{
		String ip = request.getHeader("x-forwarded-for");
		if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip))
			ip = request.getHeader("Proxy-Client-IP");
		if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip))
			ip = request.getHeader("WL-Proxy-Client-IP");
		if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip))
			ip = request.getRemoteAddr();
		
		IntialDba();
		int ret = -1;
		String s = "select id,iflag,vcpassa,vcpassb,vcpassc,vcename from smuser where rownum<2 and vccode='" + username + "'";
		m_set = m_smt.executeQuery(s);
		if(m_set.next())
		{
			String stype = ""; 
			if(password.equalsIgnoreCase(m_set.getString(3)))      stype = "1";
			else if(password.equalsIgnoreCase(m_set.getString(4))) stype = "2";
			else if(password.equalsIgnoreCase(m_set.getString(5))) stype = "3";
			if(stype.isEmpty())
			{
				ret = 2;
				m_set.close();
			}
			else
			{
				ret = 0;
				String sid = m_set.getString(1);
				String sflag = m_set.getString(2);
				String sdd = "", sname = "All";
				if(sflag.equalsIgnoreCase("3"))
				{
					sdd = sid;
					sname = m_set.getString(6);
				}
				m_set.close();
								
				HttpSession ses = request.getSession();
				ses.setMaxInactiveInterval(4*3600);
				ses.setAttribute("userid",sid);
				ses.setAttribute("userdd",sdd);
				ses.setAttribute("username",sname);
				ses.setAttribute("userflag",sflag);
				ses.setAttribute("usertype",stype);
				
				s = "insert into smvisit(iuserid,vcip,itype,ddate) values("+sid+",'"+ip+"',"+stype+",sysdate)";
				m_smt.executeUpdate(s);
			}
		}
		else ret = 1;
		CloseDba();
		return ret;
	}
	
	public String getWall() throws Exception
	{
		IntialDba();
		String s = "select id,to_char(ddate,'yyyy-mm-dd hh24:mi:ss') d from tcwall where rownum<2 and iflag=1";
		m_set = m_smt.executeQuery(s);
		if(m_set.next()) s = "wall/" + m_set.getString(1) + ".jpg?ver=" + m_set.getString(2);
		else s = "";
		CloseDba();
		return s;
	}
	
	public List getSeries() throws Exception
	{
		String userid = getUserid(1);
		if(userid==null) return null;
		String s;
		if(userid.equalsIgnoreCase("")) s = "select id,vcename from tcseries order by iseq";
		else s = "select distinct c.id,c.vcename,c.iseq from tcvin a,tcveh b,tcseries c where a.iuserid="+userid+" and a.ivehid=b.id and c.id=b.iseriesid order by c.iseq";
		return getData(s, 2, "\n");
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
		return getData(s, 2, "\n");
	}

	public List getVeh(int iPage,int nPageSize,String iSeries,String iYear,String iBrand,String iModel,String iEmission,
			String iFuel,String iCabin,String iWB,String iDrive,String sVin) throws Exception
	{
		String userid = getUserid(1);
		if(userid == null) return null;
		
		String s, v[] = getVinString(sVin);	//code,vin,esn
		UserBean d = new UserBean();
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
		return getData(s,0,"\n");
	}
	
	public ResultSet getVehEx(int iPage,int nPageSize,String iSeries,String iYear,String iBrand,String iModel,String iEmission,
			String iFuel,String iCabin,String iWB,String iDrive,String sVin) throws Exception
	{
		String userid = getUserid(1);
		if(userid == null) return null;
		
		String s, v[] = getVinString(sVin);	//code,vin,esn
		UserBean d = new UserBean();
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
		
		IntialDba();
		try	{ m_set = m_smt.executeQuery(s); }
		catch (Exception e){ throw e; }
		return m_set;
	}
	
	public String getVinCount(String vehid) throws Exception
	{
		String userid = getUserid(1);
		if(userid == null) return null;
		
		String s, sfrom="", scountry="", svin="";		
		s = "select min(iyear) as A,count(id) as B from tcvin where ivehid=" + vehid;
		if(!userid.isEmpty()) s += " and iuserid=" + userid;
		IntialDba();
		m_set = m_smt.executeQuery(s);
		if(m_set.next())
		{
			sfrom = m_set.getString(1);
			svin = m_set.getString(2);
		}
		m_set.close();
		if(userid.isEmpty())
		{
			s = "select count(1) as A from (select distinct iuserid from tcvin where ivehid=" + vehid + ")";
			m_set = m_smt.executeQuery(s);
			if(m_set.next()) scountry = m_set.getString(1);
			m_set.close();
		}
		CloseDba();
		s = "<b>Since:</b>&nbsp;" + sfrom;
		if(!scountry.isEmpty()) s += "&nbsp;&nbsp;&nbsp;<b>Country:</b>&nbsp;" + scountry;
		s += "&nbsp;&nbsp;&nbsp;<b>VIN:</b>&nbsp;" + svin;
		return s;
	}
	
	public ResultSet getVin(String vehid) throws Exception
	{
		String userid = getUserid(1);
		if(userid == null) return null;
		String s = "select a.vcvin A,a.vcesn B,a.vcbill C,b.vcename D,a.vcnote E,to_char(a.ddate,'yyyy-mm-dd') F,a.iyear G from tcvin a left join smuser b on b.id=a.iuserid where a.ivehid=" + vehid;
		if(!userid.isEmpty()) s += " and a.iuserid=" + userid;
		s += " order by c";
		IntialDba();
		try	{ m_set = m_smt.executeQuery(s); }
		catch (Exception e){ throw e; }
		return m_set;
	}
	
	public ResultSet getVehInfo(String vehid) throws Exception
	{
		String s="select b.vccode||'/'||a.vccode A,a.ibrandid B from  tcveh a left join tcmodel b on b.id=a.imodelid where a.id ="+vehid;
		if(m_set != null) m_set.close();
		try	{ m_set = m_smt.executeQuery(s); }
		catch (Exception e){ throw e; }
		return m_set;
	}
	
	public ResultSet getVehLink(String vehid) throws Exception
	{
		String cs = "iimageid A,ipartid B,ihot C,iqty D";
		String s = "with tmp as (select distinct " + cs + " from tcvehlink where ivehid=" + vehid + ")";
		s += " select * from tmp union select " + cs + " from tcasmlink where iasmid in (select distinct B from tmp)";
		if(m_set != null) m_set.close();
		try	{ m_set = m_smt.executeQuery(s); }
		catch (Exception e){ throw e; }
		return m_set;
	}
	
	public ResultSet getVehTree(String s) throws Exception
	{
		if(s.isEmpty()) return null;
		s = "select m.id id,m.it1d d1,t1.vcename n1,m.it2d d2,t2.vcename n2,m.vccode d3,t3.vcename n3,m.pdate t from ((tcimage m left join tct1 t1 on t1.id=m.it1d) left join tct2 t2 on t2.id=m.it2d) left join tct3 t3 on t3.id=m.it3d where m.id in(" + s + ")  order by m.vcseq";	
		if(m_set != null) m_set.close();
		try	{ m_set = m_smt.executeQuery(s); }
		catch (Exception e){ throw e; }
		return m_set;
	}
}