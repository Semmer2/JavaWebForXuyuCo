package dao;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import dao.UserBean;

public class VehDba extends BaseDba
{
	HttpServletRequest request;
	public VehDba(HttpServletRequest request)
	{
		this.request = request;
	}
	
	private String getVehItem(int mode, String id)
	{
		if(mode == 0)		//Y/N
			return "case " + id + " when 1 then 'Y' when 2 then 'N' else '' end";
		else if(mode == 1)	//Model
			return "(select vccode from tcmodel where id=" + id + ")";
		else if(mode == 2)	//Brand
			return "(select vccode from tcbrand where id=" + id + ")"; 
		//else if(mode == 3)	//Edition
		//	return "";
		//else if(mode == 4)	//Color
		//	return "";
		else if(mode == 5)	//Cabin
			return "case " + id + " when 1 then 'S/C' when 2 then 'D/C' when 3 then 'K/C' when 4 then 'V/C' when 5 then '5/S' when 6 then '7/S' when 7 then '23/S' else '' end";
		else if(mode == 6)	//WB
			return "to_char(" + id + ")";
		//else if(mode == 7) //Engine
		//	return "";
		else if(mode == 8)	//Drive
			return "case " + id + " when 1 then '2WD' when 2 then '4WD' when 3 then 'REAR' else '' end";
		else if(mode == 9)	//Fuel
			return "case " + id + " when 1 then 'Gasoline' when 2 then 'Diesel' else '' end";
		else if(mode == 10)	//Emission
			return "'Euro-' || case " + id + " when 1 then 'I' when 2 then 'II' when 3 then 'III' when 4 then 'IV' when 5 then 'V' when 6 then 'VI' else '' end";
		else return "";
	}
	
	public String getVehSQL(int mode)
	{
		String s = "";
		if(mode == 0)s += "," + getVehItem(1,"a.imodelid") + "||'/'||a.vccode A";
		s += "," + getVehItem(2,"a.ibrandid") + " B";
		s += ",a.vcedition C,a.vccolor D";
		s += "," + getVehItem(5,"a.icabin") + " E";
		s += "," + getVehItem(6,"a.iwb") + " F,a.vcengine G";
		s += "," + getVehItem(8,"a.idrive") + " H";		
		s += "," + getVehItem(0,"a.iac") + " I";
		s += "," + getVehItem(0,"a.iabs") + " J";
		s += "," + getVehItem(0,"a.isrs") + " K";
		s += "," + getVehItem(0,"a.ipdc") + " L";
		s += "," + getVehItem(0,"a.irhd") + " M";
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
	
	///////////////////////
	
	public List getSeries() throws Exception
	{
		String userid = (String)request.getSession().getAttribute("userdd");
		if(userid==null) return null;
		String s;
		if(userid.equalsIgnoreCase("")) s = "select id,vcename from tcseries order by iseq";
		else s = "select distinct c.id,c.vcename,c.iseq from tcvin a,tcveh b,tcseries c where a.iuserid="+userid+" and a.ivehid=b.id and c.id=b.iseriesid order by c.iseq";
		return getData(s, 2, "\n");
	}
	
	public List getOption(String type,String vin,String brandid) throws Exception
	{
		String userid = (String)request.getSession().getAttribute("userdd");
		if(userid == null)return null;
						
		boolean a = false, b = false, c = false;  // a: veh   b: vin   c: model
		String s1="", s2="";
		if(type.equalsIgnoreCase("Year"))			{ b=true;	s1="b.iyear";	  	s2="to_char(b.iyear)";  }
		else if(type.equalsIgnoreCase("Brand"))		{ a=true;	s1="a.ibrandid";	s2=getVehItem(2,"a.ibrandid");	}
		else if(type.equalsIgnoreCase("Model"))		{ a=c=true;	s1="c.id";			s2="c.vccode";	}
		else if(type.equalsIgnoreCase("Emission"))	{ a=true;	s1="a.iemission";	s2=getVehItem(10,"a.iemission");	}
		else if(type.equalsIgnoreCase("Fuel"))		{ a=true;	s1="a.ifuel";		s2=getVehItem(9,"a.ifuel");	}
		else if(type.equalsIgnoreCase("Cabin"))		{ a=true;	s1="a.icabin";		s2=getVehItem(5,"a.icabin"); }
		else if(type.equalsIgnoreCase("WB"))		{ a=true;	s1="a.iwb";			s2="to_char(a.iwb)"; }
		else if(type.equalsIgnoreCase("Drive"))		{ a=true;	s1="a.idrive";		s2=getVehItem(8,"a.idrive"); }
		else return null;
		m_s = "select distinct "+s1+" as code,"+s2+" as name";
		
		UserBean d = new UserBean();
		String v[] = getVinString(vin);	//code,vin,esn
		if(d.append1("b.iuserid", userid, 1)) b = true;
		if(d.append1("a.ibrandid", brandid, 1)) a = true;
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
		
		m_s += " from " + s1;	
		if(!s2.isEmpty()) m_s += " where " + s2;
		m_s += " order by name";
		return getData(m_s, 2, "\n");
	}
	
	public void getVeh(int iPage,int nPageSize,String iSeries,String iYear,String iBrand,String iModel,String iEmission,
			String iFuel,String iCabin,String iWB,String iDrive,String sVin) throws Exception
	{
		if(m_set != null) { m_set.close();	m_set = null; }
		String userid = (String)request.getSession().getAttribute("userdd");
		if(userid == null) return;
		
		String v[] = getVinString(sVin);	//code,vin,esn
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
		
		m_s = "with t as (";
		if(v[1].isEmpty())
		{
			m_s += "select rownum as ln,a.id from tcveh a";
			if(ret) m_s += ",tcmodel c where " + v[0] + " and c.id=a.imodelid";
			else if(!v[0].isEmpty()) m_s += " where " + v[0];			
		}
		else
		{
			m_s += "select rownum as ln,d.id from (select distinct b.ivehid as id from tcvin b where " + v[1];
			if(!v[0].isEmpty())
			{
				m_s += " intersect select a.id from tcveh a";
				if(ret) m_s += ",tcmodel c";
				m_s += " where " + v[0];
				if(ret) m_s += " and c.id=a.imodelid";
			}			
			m_s += ") d";
		}
		m_s += ")";
		if(iPage <= 0)
		{
			m_s += " select max(ln) id,'' A,'' B,'' C,'' D,'' E,'' F,'' G,'' H,'' I,'' J,'' K,'' L,'' M from t union all";
		}
		
		if(nPageSize < 1) nPageSize = 25;
		iPage = iPage > 0 ? iPage-1 : 0;
		iPage = iPage*nPageSize;		
		m_s += " select a.id" + getVehSQL(0) + " from tcveh a where a.id in(select id from t where ln>" + iPage + " and ln<" + (iPage+nPageSize+1) + ")";
		Open(m_s, true);
	}
	
	public String getVinCount(String vehid) throws Exception
	{
		String userid = (String)request.getSession().getAttribute("userdd");
		if(userid == null) return null;
		
		String sfrom="", scountry="", svin="";		
		m_s = "select min(iyear) as A,count(id) as B from tcvin where ivehid=" + vehid;
		if(!userid.isEmpty()) m_s += " and iuserid=" + userid;
		Intial();
		m_set = m_smt.executeQuery(m_s);
		if(m_set.next())
		{
			sfrom = m_set.getString(1);
			svin = m_set.getString(2);
		}
		m_set.close();
		if(userid.isEmpty())
		{
			m_s = "select count(1) as A from (select distinct iuserid from tcvin where ivehid=" + vehid + ")";
			m_set = m_smt.executeQuery(m_s);
			if(m_set.next()) scountry = m_set.getString(1);
			m_set.close();
		}
		Close(true);
		m_s = "<b>Since:</b>&nbsp;" + sfrom;
		if(!scountry.isEmpty()) m_s += "&nbsp;&nbsp;&nbsp;<b>Country:</b>&nbsp;" + scountry;
		m_s += "&nbsp;&nbsp;&nbsp;<b>VIN:</b>&nbsp;" + svin;
		return m_s;
	}
	
	public List getSearchOption(String type,String p1, String p2) throws Exception
	{
		String userid = (String)request.getSession().getAttribute("userdd");
		if(userid == null) return null;
				
		UserBean d = new UserBean();
		String s1, s2 = "";
		if(type.equalsIgnoreCase("Series"))
		{
			if(d.append1("a.iuserid", userid, 1))
			{
				s1 = "select distinct c.id as code,c.vcename as name,c.iseq as iseq from tcvin a,tcveh b,tcseries c";
				d.append1("a.ivehid=b.id and c.id=b.iseriesid");
			}
			else s1 = "select id as code,vcename as name from tcseries";
			s2 = "iseq";
		}
		else if(type.equalsIgnoreCase("Year"))
		{
			s1 = "select distinct iyear as code,to_char(iyear) as name from tcvin";
			d.append1("iuserid", userid, 1);
			s2 = "name";
		}
		else if(type.equalsIgnoreCase("Brand"))
		{
			s1 = "select distinct b.ibrandid as code," + getVehItem(2,"b.ibrandid") + " as name from tcveh b";
			if(d.append1("c.iuserid", userid, 1))
			{
				s1 += ",tcvin c";
				d.append1("b.id=c.ivehid");
			}
			d.append1("b.iseriesid", p1, 1);
			s2 = "name";
		}
		else if(type.equalsIgnoreCase("Model"))
		{
			s1 = "select distinct a.id as code,a.vccode as name from tcmodel a,tcveh b";
			if(d.append1("c.iuserid", userid, 1))
			{
				s1 += ",tcvin c";
				d.append1("b.id=c.ivehid");
			}
			d.append1("b.iseriesid", p1, 1);
			d.append1("b.ibrandid", p2, 1);
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
			d.append1("c.it1d", p1, 1);
			d.append1("b.id=c.it2d");
			s2 = "name";
		}
		else if(type.equalsIgnoreCase("Image"))
		{
			s1="select distinct a.id as code,a.vcename as name from tct3 a,tcimage c";
			d.append1("c.it1d", p1, 1);
			d.append1("c.it2d", p2, 1);
			d.append1("a.id=c.it3d");
			s2 = "name";
		}
		else return null;
		if(!d.isEmpty()) s1 += " where "+ d.result();
		if(!s2.isEmpty()) s1 += " order by " + s2;			
		return getData(s1,2,"\n");
	}
	
	public void getAdvancedLink(String pn,String pd,String svin,String year,String series,String brand,String model,String sys,String sub,String image) throws Exception
	{
		String userid = (String)request.getSession().getAttribute("userdd");
		if(userid == null) return;
		m_s = (String)request.getSession().getAttribute("userd1");
		if(m_s == null) return;
		int flag = Integer.parseInt(m_s);
		
		String v[] = getVinString(svin);	//code,vin,esn
		String sa = "", sb = "", sc = "", sd = "";	// a: veh  b:part  c:image  d: vin  e:model	
		UserBean d = new UserBean();
		boolean ret = false;
				
		d.Empty();
		ret = d.append1("d.iuserid", userid, 1);
		if(!d.append1("d.iyear", year, 1) && ret) d.append1("d.iyear>0");
		d.append1("d.vcvin", v[1], 3);
		d.append1("d.vcesn", v[2], 3);
		sd = d.result();
		d.Empty();
		d.append1("a.iseriesid", series, 1);
		d.append1("a.ibrandid", brand, 1);
		d.append1("a.imodelid", model, 1);
		ret = d.append1("e.vccode||'/'||a.vccode", v[0], 3);
		sa = d.result();
		d.Empty();
		d.append1("b.vccode", pn, 3);
		d.append1("b.vcename", pd, 3);
		sb = d.result();
		d.Empty();
		d.append1("c.it1d", sys, 1);
		d.append1("c.it2d", sub, 1);
		d.append1("c.it3d", image, 1);
		sc = d.result();
		
		m_s = "";
		if(!sa.isEmpty())
		{
			m_s = "select a.id from tcveh a";
			if(ret) m_s += ",tcmodel e";
			m_s += " where " + sa;
			if(ret) m_s += " and e.id=a.imodelid";
		}
		if(!sd.isEmpty())
		{
			if(!m_s.isEmpty()) m_s += " intersect ";
			m_s += "select distinct d.ivehid as id from tcvin d where " + sd;
		}
		sa = m_s;		
		if(!sb.isEmpty()) sb = "select b.id from smpart b where " + sb;
		if(!sc.isEmpty()) sc = "select c.id from tcimage c where " + sc;
		
		d.Empty();
		if(!sb.isEmpty())
		{
			d.append4("sd as (" + sb +")");
			d.append4("sb as (select id from sd union select id from smpart where inewid in sd)");
		}
		if(!sc.isEmpty()) d.append4("sc as (" + sc +")");
		if(!sa.isEmpty()) d.append4("sa as (" + sa +")");
		v[0] = "with " + d.result();
		
		v[1] = "";
		if(!sb.isEmpty()) v[1] += ",sb";
		if(!sc.isEmpty()) v[1] += ",sc";
		if(!sa.isEmpty()) v[1] += ",sa";
		
		d.Empty();
		if(!sb.isEmpty()) d.append1("sb.id=v.ipartid");
		if(!sc.isEmpty()) d.append1("sc.id=v.iimageid");
		if(!sa.isEmpty()) d.append1("sa.id=v.ivehid");
		sd = "select distinct v.ipartid as p from tcvehlink v" + v[1] + " where "+ d.result();
		
		d.Empty();
		if(!sb.isEmpty()) d.append1("sb.id=f.ipartid");
		if(!sc.isEmpty()) d.append1("sc.id=f.iimageid");
		if(!sa.isEmpty()) d.append1("sa.id=v.ivehid");
		sd += " union select distinct f.ipartid as p from tcvehlink v,tcasmlink f" + v[1] + " where f.iasmid=v.ipartid and "+ d.result();
			
		/////////////////////
		v[1] = v[2] = "";
		if((flag&8)>0) v[1] = userid.isEmpty() ? (String)request.getSession().getAttribute("userid") : userid;  //cart
		if((flag&2)>0 && !userid.isEmpty()) v[2] = userid;		//price
		
		m_s  = v[0];
		m_s  += " select p.id,p.vccode as a,p.vcename as b,p.inewid as c,p.vcnote as d";
		m_s += ",case when p.inewid>0 then '<span onclick=''OnViewSupersed(this)'' title=''View Superseded''>'||(select vccode from smpart where id=p.inewid)||'</span>' else '' end as e";
		m_s += ",case when p.iphoto>0 then '<span onclick=''OnViewPhoto(this)'' title=''View Photo''>Y</span>' else '' end as f";
		if(!v[1].isEmpty()) m_s += ",case when p.inewid<1 and ct.ipartid is null then '<span onclick=''OnAddCart(this)'' title=''Add to Cart''><b>+</b></span>' else '' end as g";
		if(!v[2].isEmpty()) m_s += ",case when pr.iprice is null then (case when p.iprice=0 then '' else '$'||to_char(p.iprice,'fm99990.99') end) else '$'||to_char(pr.iprice,'fm99990.99') end as h";
		m_s += " from smpart p";		
		if(!v[1].isEmpty()) m_s += " left join tccart ct on ct.iuserid=" + v[1] + " and ct.ipartid=p.id";
		if(!v[2].isEmpty())  m_s += " left join smprice pr on pr.iuserid=" + v[2] + " and pr.ipartid=p.id";
		m_s += " where p.id in (" + sd + ")";
		v = null;
		Open(m_s, true);		
	}
	
	public void getVeh(int iPage, int nPageSize, String partid) throws Exception
	{
		if(partid == null || partid.isEmpty()) return;
		String userid = (String)request.getSession().getAttribute("userdd");
		if(userid == null) return;
		
		m_s  = "with t as (select rownum as ln,d.id from (select distinct ivehid as id from tcvehlink where ipartid=" + partid;
		m_s += " union select distinct a.ivehid as id from tcvehlink a,tcasmlink b where b.ipartid=" + partid + " and b.iasmid=a.ipartid";
		if(!userid.isEmpty()) m_s += " intersect select distinct ivehid as id from tcvin where iyear>0 and iuserid=" + userid;
		m_s += ") d)";	
			
		if(iPage <= 0)
		{
			String cs = "'' B,'' C,'' D,'' E,'' F,'' G,'' H,'' I,'' J,'' K,'' L,'' M";
			m_s += " select max(ln) as id,'' as A," + cs + " from t";
			m_s += " union all select count(c.id) as id,'' as A," + cs + " from tcvin c inner join t on t.id=c.ivehid";
			if(!userid.isEmpty()) m_s += " where c.iuserid=" + userid;
			m_s += " union all select distinct 0 as id,w.vccode as A," + cs + " from t left join tcveh v on v.id=t.id left join tcbrand w on w.id=v.ibrandid";
			m_s += " union all";
		}
		if(nPageSize < 1) nPageSize = 25;
		iPage = iPage > 0 ? iPage-1 : 0;
		iPage = iPage*nPageSize;		
		m_s += " select a.id" + getVehSQL(0) + " from tcveh a where a.id in(select id from t where ln>" + iPage + " and ln<" + (iPage+nPageSize+1) + ")";
		Open(m_s, true);
	}
	
	public List getMSearchOption(String type,String p1, String p2) throws Exception
	{
		String s1="",s2="",s3="";
		boolean bChs=false;
		if(type.equalsIgnoreCase("Series"))
		{
			s1="select id as id,vcename as name from tcseries";
			s3="iseq";
		}
		else if(type.equalsIgnoreCase("Brand"))
		{
			s1="select distinct ibrandid as id,"+getVehItem(2,"ibrandid")+" as name from tcveh";
			if(!p1.isEmpty()) s2="iseriesid="+p1;
			s3="name";
		}
		else if(type.equalsIgnoreCase("Fuel"))
		{
			s1="select distinct ifuel as id,"+getVehItem(9,"ifuel")+" as name from tcveh";
			s3="name";
		}
		else if(type.equalsIgnoreCase("Emission"))
		{
			s1="select distinct iemission as id,"+getVehItem(10,"iemission")+" as name from tcveh";
			s3="name";
		}
		else if(type.equalsIgnoreCase("Cabin"))
		{
			s1="select distinct icabin as id,"+getVehItem(5,"icabin")+" as name from tcveh";
			s3="name";
		}
		else if(type.equalsIgnoreCase("WB"))
		{
			s1="select distinct iwb as id,to_char(iwb) as name from tcveh";
			s3="name";
		}
		else if(type.equalsIgnoreCase("Drive"))
		{
			s1="select distinct idrive as id,"+getVehItem(8,"idrive")+" as name from tcveh";
			s3="name";
		}
		else if(type.equalsIgnoreCase("Model"))
		{
			s1="select distinct a.id as id,a.vccode as name from tcmodel a,tcveh b";
			s2="a.id=b.imodelid";
			if(!p1.isEmpty()) s2+=" and b.iseriesid="+p1;
			if(!p2.isEmpty()) s2+=" and b.ibrandid="+p2;
			s3="name";
		}
		else if(type.equalsIgnoreCase("Year"))
		{
			s1="select distinct iyear as id,to_char(iyear) as name from tcvin";
			s3="name";
		}
		else if(type.equalsIgnoreCase("Sys"))
		{
			s1="select id as id,vcename as name from tct1";
			s3="iseq";
		}
		else if(type.equalsIgnoreCase("Sub"))
		{
			s1="select distinct b.id as id,b.vcename as name from tct2 b,tcimage c";
			s2="b.id=c.it2d";
			if(!p1.isEmpty())s2+=" and c.it1d="+p1;
			s3="name";
		}
		else if(type.equalsIgnoreCase("Image"))
		{
			s1="select distinct a.id as id,a.vcename as name from tct3 a,tcimage c";
			s2="a.id=c.it3d";
			if(!p1.isEmpty())s2+=" and c.it1d="+p1;
			if(!p2.isEmpty())s2+=" and c.it2d="+p2;
			s3="name";
		}
		else if(type.equalsIgnoreCase("Country"))
		{
			if(p1.equalsIgnoreCase("c")) bChs=true;
			s1="select id as id,vc"+p1+"name as name from smuser";
			s2="iflag=3";
			s3="vcename";
		}
		else return null;
		if(!s2.isEmpty())s1+=" where "+s2;
		s1+=" order by "+s3;
		List ls = new ArrayList();
		try
		{
			Intial();			
			m_set = m_smt.executeQuery(s1);
			while(m_set.next())
			{
				m_s = "";
				m_s += m_set.getString(1);
				m_s +="\n";
				m_s += bChs ? URLEncoder.encode(m_set.getString(2),"UTF-8") : m_set.getString(2);
				ls.add(m_s);
			}
			Close(true);
		}
		catch (Exception e){ throw e; }
		return ls;
	}
	
	public void queryVeh(String iSeries,String iBrand,String iFuel,String iEmission,String iCabin,String iWB,String iDrive,String sModel) throws Exception
	{
		UserBean d=new UserBean();
		d.append1("a.iseriesid", iSeries, 1);
		d.append1("a.ibrandid", iBrand, 1);
		d.append1("a.ifuel", iFuel, 1);
		d.append1("a.iemission", iEmission, 1);
		d.append1("a.icabin", iCabin, 1);
		d.append1("a.iwb", iWB, 1);
		d.append1("a.idrive", iDrive, 1);
		d.append1("b.vccode||'/'||a.vccode", sModel, 3);
		m_s = "select a.id,b.vccode||'/'||a.vccode A";
		m_s += getVehSQL(1); //B-M
		m_s += " from tcveh a left join tcmodel b on b.id=a.imodelid";
		if(!d.isEmpty()) m_s += " where " + d.result();
		m_s += " order by A";
		d = null;
		Open(m_s, true);
	}
}

