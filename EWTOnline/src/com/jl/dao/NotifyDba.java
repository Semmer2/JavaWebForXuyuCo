package com.jl.dao;

import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.PageContext;
import javax.swing.JOptionPane;

import com.jl.entity.UserBean;
import com.jl.dao.base.impl.BaseData;
import com.jl.dao.base.impl.DBASession;
import java.util.Hashtable;
import javax.swing.JOptionPane;

import oracle.apps.fnd.wf.bes.util.StringUtils;

public class NotifyDba extends BaseData
{
	public NotifyDba(HttpServletRequest request)
	{
		super(request);
	}
		
	public List getSearchOption(String type,String p1, String p2) throws Exception
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
			s1="select distinct ibrand as id,getname(ibrand,3) as name from tcveh";
			if(!p1.isEmpty()) s2="iseriesid="+p1;
			s3="name";
		}
		else if(type.equalsIgnoreCase("Fuel"))
		{
			s1="select distinct ifuel as id,getname(ifuel,8) as name from tcveh";
			s3="name";
		}
		else if(type.equalsIgnoreCase("Emission"))
		{
			s1="select distinct iemission as id,getname(iemission,6) as name from tcveh";
			s3="name";
		}
		else if(type.equalsIgnoreCase("Cabin"))
		{
			s1="select distinct icabin as id,getname(icabin,7) as name from tcveh";
			s3="name";
		}
		else if(type.equalsIgnoreCase("WB"))
		{
			s1="select distinct iwb as id,to_char(iwb) as name from tcveh";
			s3="name";
		}
		else if(type.equalsIgnoreCase("Drive"))
		{
			s1="select distinct idrive as id,getname(idrive,5) as name from tcveh";
			s3="name";
		}
		else if(type.equalsIgnoreCase("Model"))
		{
			s1="select distinct a.id as id,a.vccode as name from tcmodel a,tcveh b";
			s2="a.id=b.imodelid";
			if(!p1.isEmpty()) s2+=" and b.iseriesid="+p1;
			if(!p2.isEmpty()) s2+=" and b.ibrand="+p2;
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
			s2="b.id=c.t2d";
			if(!p1.isEmpty())s2+=" and c.t1d="+p1;
			s3="name";
		}
		else if(type.equalsIgnoreCase("Image"))
		{
			s1="select distinct a.id as id,a.vcename as name from tct3 a,tcimage c";
			s2="a.id=c.t3d";
			if(!p1.isEmpty())s2+=" and c.t1d="+p1;
			if(!p2.isEmpty())s2+=" and c.t2d="+p2;
			s3="name";
		}
		else if(type.equalsIgnoreCase("Country"))
		{
			if(p1.equalsIgnoreCase("CHS")) { bChs=true; p2="c"; }
			else p2="e";
			s1="select id as id,vc"+p2+"name as name from smuser";
			s2="iflag=3";
			s3="vcename";
		}
		else return null;
		if(!s2.isEmpty())s1+=" where "+s2;
		s1+=" order by "+s3;
		if(bChs)
		{
			List ls=getData(s1,null);
			Hashtable ht;
			for(int i=0;i<ls.size();i++)
			{
				ht=(Hashtable)ls.get(i);
				s2=URLEncoder.encode(ht.get("name").toString(),"UTF-8");
				ht.remove("name");
				ht.put("name",s2);
				ls.set(i,ht);
			}
			return ls;
		}
		else return getData(s1,null);
	}
	
	public List getPartbyCountry(String country,String series,String brand,String model,String sap) throws Exception
	{
		String s,s1,s2;//a:part, b:veh, c:vin, d:image		
		boolean b,c;		
		if(sap != null) sap = sap.toUpperCase();
		UserBean d = new UserBean();
		d.append1("b.vccode", sap, 2);
		d.append1("b.iseriesid", series, 1);
		d.append1("b.ibrand", brand, 1);
		d.append1("b.imodelid", model, 1);
		b = !d.isEmpty();
		c = d.append1("c.iuser", country, 1);
		if(b&&c) d.append1("b.id=c.iveh");
		if(d.isEmpty()) return null;
		
		s1 = "with t1 as (select ";
		if(c) s1 += "distinct ";
		s1 += b ? "b.id" : "c.iveh id";
		s1 += " from ";
		if(b) s1 += "tcveh b";
		if(b&&c) s1 += ",";
		if(c) s1 += "tcvin c";
		s1 += " where " + d.result() + ")";
		s1 += ",t2 as (select distinct a.ipartid p,a.ivehid v,a.iimageid m from tcvehlink a,t1 where t1.id=a.ivehid)";
		s2  = "select sb.p,sb.m,sum(sc.n) as n from (";
		s2 += "select * from t2 union all select distinct a.ipartid p,t2.v v,a.iimageid m from t2,tcasmlink a where a.iasmid=t2.p";
		s2 += ") sb left join (";
		s2 += "select a.iveh v,count(a.vcvin) n from tcvin a inner join t1 on t1.id=a.iveh group by a.iveh";
		s2 += ") sc on sc.v=sb.v group by sb.p,sb.m";
		s   = s1 + " select distinct t1.vcename a,t2.vcename b,pt.vccode c,pt.vcename d,sd.n e";
		s  += ",case when pt.newid>0 then (select npt.vccode from smpart npt where npt.id=pt.newid) else '' end f";
		s  += " from ("+s2+") sd,smpart pt,tcimage img,tct1 t1,tct2 t2";
		s  += " where pt.id=sd.p and img.id=sd.m and t1.id=img.t1d and t2.id=img.t2d";
		return getData(s,null);
	}
	
	public List getPartbyCountry2(String country,String series,String brand,String model,String sap) throws Exception
	{
		
		String s,s1,s2;//a:part, b:veh, c:vin, d:image		
		boolean b,c;		
		if(sap != null) sap = sap.toUpperCase();
		UserBean d = new UserBean();
		d.append1("b.vccode", sap, 2);
		d.append1("b.iseriesid", series, 1);
		d.append1("b.ibrand", brand, 1);
		d.append1("b.imodelid", model, 1);
		b = !d.isEmpty();
		c = d.append1("c.iuser", country, 1);
		if(b&&c) d.append1("b.id=c.iveh");
		if(d.isEmpty()) return null;
		
		s1 = "with t1 as (select ";
		if(c) s1 += "distinct ";
		s1 += b ? "b.id" : "c.iveh id";
		s1 += " from ";
		if(b) s1 += "tcveh b";
		if(b&&c) s1 += ",";
		if(c) s1 += "tcvin c";
		s1 += " where " + d.result() + ")";
		s1 += ",t2 as (select distinct a.ipartid p,a.ivehid v from tcvehlink a,t1 where t1.id=a.ivehid)";
		s2  = "select sb.p,sum(sc.n) as n from (";
		s2 += "select * from t2 union all select distinct a.ipartid p,t2.v v from t2,tcasmlink a where a.iasmid=t2.p";
		s2 += ") sb left join (";
		s2 += "select a.iveh v,count(a.vcvin) n from tcvin a inner join t1 on t1.id=a.iveh";
		if(country!=null && !country.isEmpty()) s2 += " where a.iuser=" + country;
		s2 += " group by a.iveh";
		s2 += ") sc on sc.v=sb.v group by sb.p";
		s   = s1 + " select distinct pt.vccode c,pt.vcename d,sd.n e,pt.vccname b";
		s  += ",case when pt.newid>0 then (select npt.vccode from smpart npt where npt.id=pt.newid) else '' end f";
		s  += " from ("+s2+") sd,smpart pt where pt.id=sd.p";
		return getData(s,null);
	}
	
	public List queryVin1(String sVin, String iCountry,String iYear,String sBill) throws Exception
	{
		String sql="select distinct a.id as A,a.vcvin as B,a.vcesn as C,a.vcbill as D,a.vcenote as E,b.vccname as F,case when length(c.vccode)=18 then c.vccode else d.vccode||'/'||c.vccode end as G,to_char(a.ddate,'yyyy-mm-dd') as H from tcvin a,smuser b,tcveh c,tcmodel d where b.id=a.iuser and c.id=a.iveh and d.id=c.imodelid";
		if(sVin!=null && !sVin.isEmpty())sql+=" and (a.vcvin like '%"+sVin+"%' or a.vcesn like '%"+sVin+"%')";
		if(iCountry!=null && !iCountry.isEmpty())sql+=" and a.iuser="+iCountry;
		if(iYear!=null && !iYear.isEmpty())sql+=" and a.iYear="+iYear;
		if(sBill!=null && !sBill.isEmpty())sql+=" and a.vcbill like '%"+sBill+"%'";
		sql+=" order by B";
		List ls=new ArrayList();
		ls.add(sql);
		return ls;
		//return getData(sql,null);
	}
		
	public List queryVin2(String sVin, String iCountry,String iYear,String sBill) throws Exception
	{
		String sql="select distinct a.id as A,a.vcvin as B,a.vcesn as C,a.vcbill as D,a.vcenote as E,b.vccname as F,case when length(c.vccode)=18 then c.vccode else d.vccode||'/'||c.vccode end as G,to_char(a.ddate,'yyyy-mm-dd') as H from tcvin a,smuser b,tcveh c,tcmodel d where b.id=a.iuser and c.id=a.iveh and d.id=c.imodelid";
		if(sVin!=null && !sVin.isEmpty())sql+=" and (a.vcvin like '%"+sVin+"%' or a.vcesn like '%"+sVin+"%')";
		if(iCountry!=null && !iCountry.isEmpty())sql+=" and a.iuser="+iCountry;
		if(iYear!=null && !iYear.isEmpty())sql+=" and a.iYear="+iYear;
		if(sBill!=null && !sBill.isEmpty())sql+=" and a.vcbill like '%"+sBill+"%'";
		sql+=" order by B";
		return getData(sql,null);
	}
	
	public List queryPart(String sPN, String sPD) throws Exception
	{
		String s="";
		if(sPN!=null && !sPN.isEmpty()){ if(!s.isEmpty())s+=" and ";  s+="vccode like '%"+sPN+"%'"; }
		if(sPD!=null && !sPD.isEmpty()){ if(!s.isEmpty())s+=" and ";  s+="(vcename like '%"+sPD+"%' or vccname like '%"+sPD+"%')"; }
		String sql="select id as A,vccode as B,vcename as C,vccname as D,getname(newid,21) as E,case when iprice>0 then to_char(iprice,'$9999.99') else '' end as F,case when iphotoflag>0 then 'Y' else '' end as G,case when isaleflag>0 then '' else 'Y' end as H,vcenote as I,to_char(ddate,'yyyy-mm-dd') as J,tdate as K from smpart";
		if(!s.isEmpty()) sql+= " where " + s;
		sql += " order by B";
		return getData(sql,null);
	}
	
	public List queryVeh(String iSeries,String iBrand,String iFuel,String iEmission,String iCabin,String iWB,String iDrive,String sModel) throws Exception
	{
		UserBean d=new UserBean();
		d.append1("a.iseriesid", iSeries, 1);
		d.append1("a.ibrand", iBrand, 1);
		d.append1("a.ifuel", iFuel, 1);
		d.append1("a.iemission", iEmission, 1);
		d.append1("a.icabin", iCabin, 1);
		d.append1("a.iwb", iWB, 1);
		d.append1("a.idrive", iDrive, 1);
		d.append1("b.vccode||'/'||a.vccode", sModel, 3);
		String s = "select a.id,b.vccode||'/'||a.vccode A";
		s += getVehSQL(1); //B-M
		s += ",a.vcspecial as N";
		s += " from tcveh a left join tcmodel b on b.id=a.imodelid";
		if(!d.isEmpty()) s += " where " + d.result();
		s += " order by A";
		d = null;
		return getData(s, null);
	}
	
	public List queryVin(String iVeh,String iCountry,String iYear,String sBill,String sVin) throws Exception
	{
		String sql="";
		if(sVin!=null && !sVin.isEmpty())sql="(a.vcvin like '%"+sVin+"%' or a.vcesn like '%"+sVin+"%')";
		UserBean d=new UserBean(sql);
		d.append1("a.iveh", iVeh, 1);
		d.append1("a.iuser", iCountry, 1);
		d.append1("a.iyear", iYear, 1);
		d.append1("a.vcbill", sBill, 3);		
		sql="select distinct a.id,a.iyear as A,a.vcbill as B,b.vccname as C,a.vcvin as D,a.vcesn as E,d.vccode||'/'||c.vccode as F,a.vcenote as G from tcvin a,smuser b,tcveh c,tcmodel d where b.id=a.iuser and c.id=a.iveh and d.id=c.imodelid";
		if(!d.isEmpty()) sql+=" and "+d.result();
		sql+=" order by A";
		d = null;
		return getData(sql,null);
	}
	
	public List getVeh(String id) throws Exception
	{
		String sql="select a.vccode,b.vccode as vcmodel,a.iseriesid,a.ibrand,a.vcedition,a.vccolor,a.icabin,a.iwb,a.idrive,a.vcengine,a.ifuel,a.iemission,a.vcspecial,a.iac,a.iabs,a.iairbag,a.iredar,a.ihd,to_char(a.ddate,'yyyy-mm-dd hh24:mi:ss') as ddate from tcveh a left join tcmodel b on b.id=a.imodelid where rownum<2 and a.id="+id;
		return getData(sql,null);
	}
	
	public String saveVeh(String id,String sd[]) throws Exception
	{
		int i;		
		List ls;
		Hashtable ht;
		String s;
		UserBean d=new UserBean();
		DBASession db=getSession();
		boolean bNew=id.isEmpty();
		boolean b[]=new boolean[18];
		for(i=0;i<18;i++)
		{
			if(bNew)b[i]=true;
			else
			{
				b[i]=sd[i].substring(0,1).equalsIgnoreCase("Y");
				if(b[i])sd[i]=sd[i].substring(1);
			}			
		}
				
		if(b[1])
		{
			sd[1]=sd[1].replaceAll("'", "");
			s="select id from tcmodel where rownum<2 and vccode='"+sd[1]+"'";
			ls=getData(db,s,null);
			if(ls.size()>0)
			{
				ht=(Hashtable)ls.get(0);
				sd[1]=ht.get("id").toString();
			}
			else
			{
				s="select max(id)+1 as id from tcmodel";
				ls=getData(db,s,null);
				ht=(Hashtable)ls.get(0);
				s="insert into tcmodel(id,vccode,ddate) values("+ht.get("id").toString()+",'"+sd[1]+"',sysdate)";
				if(!runSql(s,null)) return "Error 101";
				sd[1]=s;
			}			
		}

		if(bNew)
		{
			bNew=true;
			s="select max(id)+1 as id from tcveh";
			ls=getData(db,s,null);;
			ht=(Hashtable)ls.get(0);
			id=ht.get("id").toString();
			d.append2("id",id,true,true);
		}
		
		if(b[0]) d.append2("vccode",sd[0],false,bNew);
		if(b[1]) d.append2("imodelid",sd[1],true,bNew);
		if(b[2]) d.append2("iseriesid",sd[2],true,bNew);
		if(b[3]) d.append2("ibrand",sd[3],true,bNew);
		if(b[4]) d.append2("vcedition",sd[4],false,bNew);
		if(b[5]) d.append2("vccolor",sd[5],false,bNew);
		if(b[6]) d.append2("icabin",sd[6],true,bNew);
		if(b[7]) d.append2("iwb",sd[7],true,bNew);
		if(b[8]) d.append2("idrive",sd[8],true,bNew);
		if(b[9]) d.append2("vcengine",sd[9],false,bNew);
		if(b[10]) d.append2("ifuel",sd[10],true,bNew);
		if(b[11]) d.append2("iemission",sd[11],true,bNew);
		if(b[12]) d.append2("vcspecial",sd[12],false,bNew);
		if(b[13]) d.append2("iac",sd[13],true,bNew);
		if(b[14]) d.append2("iabs",sd[14],true,bNew);
		if(b[15]) d.append2("iairbag",sd[15],true,bNew);
		if(b[16]) d.append2("iredar",sd[16],true,bNew);
		if(b[17]) d.append2("ihd",sd[17],true,bNew);
		if(d.isEmpty()) return "Error 102";		
		d.append2("ddate","sysdate",true,bNew);
		
		if(bNew) s="insert into tcveh("+d.result1()+") values("+d.result2()+")";
		else	 s="update tcveh set "+d.result()+" where id="+id;
		d = null;
		return runSql(s,null) ? "" : "Error 103";
	}
	
	public List getVin(String id) throws Exception
	{
		String sql="select a.vcvin,a.vcesn,a.vcbill,b.vccname,a.iyear,d.vccode||'/'||c.vccode as model,a.vcenote,a.vccnote,to_char(a.ddate,'yyyy-mm-dd hh24:mi:ss') as ddate from ((tcvin a left join smuser b on b.id=a.iuser) left join tcveh c on c.id=a.iveh) left join tcmodel d on d.id=c.imodelid where rownum<2 and a.id="+id;
		return getData(sql,null);
	}
	
	public String saveVin(String id,String sd[]) throws Exception
	{
		int i;		
		List ls;
		Hashtable ht;
		String s;
		UserBean d=new UserBean();
		DBASession db=getSession();
		boolean bNew=id.isEmpty();
		boolean b[]=new boolean[8];
		for(i=0;i<8;i++)
		{
			if(bNew)b[i]=true;
			else
			{
				b[i]=sd[i].substring(0,1).equalsIgnoreCase("Y");
				if(b[i])sd[i]=sd[i].substring(1);
			}			
		}	
		
		if(b[3])
		{
			sd[3]=sd[3].replaceAll("'", "");
			s="select id from smuser where rownum<2 and vccname='"+sd[3]+"'";
			ls=getData(db,s,null);
			if(ls.size()>0)
			{
				ht=(Hashtable)ls.get(0);
				sd[3]=ht.get("id").toString();
			}
			else return "Invalid country!";
		}

		if(b[5])
		{
			sd[5]=sd[5].replaceAll("'", "");
			i=sd[5].indexOf("/");
			if(i<1) return "Invalid model!";
			s="select a.id from tcveh a left join tcmodel b on b.id=a.imodelid where rownum<2 and b.vccode='"+sd[5].substring(0,i)+"' and a.vccode='"+sd[5].substring(i+1)+"'";
			ls=getData(db,s,null);
			if(ls.size()>0)
			{
				ht=(Hashtable)ls.get(0);
				sd[5]=ht.get("id").toString();
			}
			else return "Invalid model!";
		}
		
		if(id.isEmpty())
		{
			bNew=true;
			s="select max(id)+1 as id from tcvin";
			ls=getData(db,s,null);;
			ht=(Hashtable)ls.get(0);
			id=ht.get("id").toString();
			d.append2("id",id,true,bNew);
		}
		
		if(b[0]) d.append2("vcvin",sd[0],false,bNew);
		if(b[1]) d.append2("vcesn",sd[1],false,bNew);
		if(b[2]) d.append2("vcbill",sd[2],false,bNew);
		if(b[3]) d.append2("iuser",sd[3],true,bNew);
		if(b[4]) d.append2("iyear",sd[4],true,bNew);
		if(b[5]) d.append2("iveh",sd[5],true,bNew);
		if(b[6]) d.append2("vcenote",sd[6],false,bNew);
		if(b[7]) d.append2("vccnote",sd[7],false,bNew);
		if(d.isEmpty()) return "Error 112";		
		d.append2("ddate","sysdate",true,bNew);
		
		if(bNew) s="insert into tcvin("+d.result1()+") values("+d.result2()+")";
		else	 s="update tcvin set "+d.result()+" where id="+id;
		d = null;
		return runSql(s,null) ? "" : "Error 113";
	}
	
	public List getPart(String code) throws Exception
	{
		String sql="select vcename,vccname from smpart where vccode='"+code+"'";
		return getData(sql,null);
	}
	
	public List queryBulletin() throws Exception
	{
		String sql="select id,topic as A,to_char(ddate,'yyyy-mm-dd') as B,(case when flag=0 then '<span style=''cursor:hand'' onclick=''OnPub(this)'' title=''发布公告''>P</span>' else '' end) as C,'X' as D from smbulletin order by id desc";
		return getData(sql,null);
	}
	
	public List queryVisit() throws Exception
	{
		String sql="select a.iuser as A,to_char(a.ddate,'yyyy-mm-dd hh24:mi:ss') as B,a.vcip as C,b.vccname as D,b.vccode as E,num as F from (select ddate,iuser,vcip,count(*) over (partition by iuser) as num,row_number() over(partition by iuser order by ddate desc) as rd from smvisit) a left join smuser b on b.id=a.iuser where a.rd=1 order by B desc";
		return getData(sql,null);
	}
	
	public List getVisit(String userid) throws Exception
	{
		String sql="select case when itype=1 then 'A' when itype=2 then 'B' when itype=3 then 'C' else '' end as A, to_char(ddate,'yyyy-mm-dd hh24:mi:ss') as B,vcip as C from smvisit where iuser="+userid+" order by ddate desc";
		return getData(sql,null);
	}
	
	public List queryUser(String flag,String code,String name) throws Exception
	{
		String sql="select id,vccode as A,vccname as B,vcename as C,case when iflag=1 then '管理员' when iflag=2 then '超级用户' else '经销商' end as D,vcpassa as E,vcpassb as F,vcpassc as G,to_char(ddate,'yyyy-mm-dd hh24:mi:ss') as H from smuser";
		String s="";
		if(flag!=null && !flag.isEmpty()) s="iflag="+flag;
		if(code!=null && !code.isEmpty())
		{
			if(!s.isEmpty())s+=" and ";
			s+="vccode like '%"+code+"%'";
		}
		if(name!=null && !name.isEmpty())
		{
			if(!s.isEmpty())s+=" and ";
			s+="(vccname like '%"+name+"%' or vcename like '%"+name+"%')";
		}
		if(!s.isEmpty())sql+=" where "+s;
		return getData(sql,null);
	}
	
	public List getUser(String id) throws Exception
	{
		String sql="select id,vccode,vccname,vcename,iflag,vcpassa,vcpassb,vcpassc,to_char(ddate,'yyyy-mm-dd hh24:mi:ss') as ddate from smuser where rownum<2 and id="+id;
		return getData(sql,null);
	}
	
	public String saveUser(String id,String sd[]) throws Exception
	{
		int i;		List ls;	Hashtable ht;	String s;
		UserBean d=new UserBean();
		DBASession db=getSession();
		boolean bNew=id.isEmpty();
		boolean b[]=new boolean[7];
		for(i=0;i<7;i++)
		{
			if(bNew)b[i]=true;
			else
			{
				b[i]=sd[i].substring(0,1).equalsIgnoreCase("Y");
				if(b[i])sd[i]=sd[i].substring(1);
			}			
		}
		
		if(id.isEmpty())
		{
			bNew=true;
			s="select max(id)+1 as id from smuser";
			ls=getData(db,s,null);;
			ht=(Hashtable)ls.get(0);
			id=ht.get("id").toString();
			d.append2("id",id,true,bNew);
		}
		
		if(b[0]) d.append2("vccode",sd[0],false,bNew);
		if(b[1]) d.append2("vccname",sd[1],false,bNew);
		if(b[2]) d.append2("vcename",sd[2],false,bNew);
		if(b[3]) d.append2("iflag",sd[3],true,bNew);
		if(b[4]) d.append2("vcpassa",sd[4],false,bNew);
		if(b[5]) d.append2("vcpassb",sd[5],false,bNew);
		if(b[6]) d.append2("vcpassc",sd[6],false,bNew);
		if(d.isEmpty()) return "Error 122";		
		d.append2("ddate","sysdate",true,bNew);
		
		if(bNew) s="insert into smuser("+d.result1()+") values("+d.result2()+")";
		else	 s="update smuser set "+d.result()+" where id="+id;
		d = null;
		return runSql(s,null) ? "" : "Error 123";
	}
	
	private String getPass(int passLenth)
	{
		   StringBuffer buffer = new StringBuffer("123456789ABCDEFGHIJKLMNPQRSTUVWXYZ");//abcdefghijklmnopqrstuvwxyz
		   StringBuffer sb = new StringBuffer();
		   Random r = new Random();
		   int range = buffer.length();
		   for (int i = 0; i < passLenth; i++) sb.append(buffer.charAt(r.nextInt(range)));
		   return sb.toString();
	}
	
	public List queryWall() throws Exception
	{
		String sql="select case when flag=1 then '√' else '' end A,id B,to_char(ddate,'yyyy-mm-dd hh24:mi:ss') C,'' D from tcWall order by B";
		return getData(sql,null);
	}
	
	public boolean deleteWall(String id) throws Exception 
	{
		String sql = "delete from tcwall where id="+id;
		return runSql(sql,null);
	}
	
	public boolean setCurWall(String id) throws Exception 
	{
		if(!runSql("update tcwall set flag=0 where flag>0", null)) return false;
		return runSql("update tcwall set flag=1 where id="+id, null);
	}
	
	public List getBulletin(int iPage, int nPageSize) throws Exception
	{
		if(nPageSize < 1) nPageSize = 25;
		iPage = iPage > 0 ? iPage-1 : 0;
		iPage = iPage*nPageSize;
		String sql = "select id,A,B,C,D from (";
		sql += "select id,topic as A,to_char(ddate,'yyyy-mm-dd hh24:mi') as B,case when top>0 then 'Y' else '' end as C,case when red>0 then 'Y' else '' end as D";
		sql += ",row_number() over(order by top desc,ddate desc) as ln";
		sql += " from smbulletin) where ln>" + iPage + " and ln<=" + (iPage+nPageSize);
		return getData(sql, null);
	}
	
	public boolean deleteBulletin(String id) throws Exception 
	{
		return runSql("delete from smbulletin where id="+id, null);
	}
	
	public boolean setBulletinTop(String id) throws Exception 
	{
		return runSql("update smbulletin set top=(case when top=0 then 1 else 0 end) where id="+id, null);
	}
	
	public boolean setBulletinRed(String id) throws Exception 
	{
		return runSql("update smbulletin set red=(case when red=0 then 1 else 0 end) where id="+id, null);
	}
	
	public boolean saveBulletinTopic(String id, String topic) throws Exception 
	{
		return runSql("update smbulletin set topic='"+ topic + "' where id="+id, null);
	}
	
	public List getSuggest(int iPage, int nPageSize) throws Exception
	{
		if(nPageSize < 1) nPageSize = 25;
		iPage = iPage > 0 ? iPage-1 : 0;
		iPage = iPage*nPageSize;
		String s = "select A,B,C,D,E,F,G,H,I,J,K,L from (";
		//s += "select a.id A,(select vccode from tcmodel z1 where z1.id=b.imodelid)||'/'||b.vccode B,(select vccode from tcimage z2 where z2.id=a.imageid) C,substr(a.content,0,100) D,to_char(a.date1,'yyyy-mm-dd hh24:mi:ss') E,case when a.deal>0 then '' else '<span onclick=\"OnDeal(this)\">处理完毕</span>' end F,a.vehid G,a.imageid H,a.deal I,to_char(a.date2,'yyyy-mm-dd hh24:mi:ss') J,(select vccname from smuser z3 where z3.id=a.userid) K,case when a.dealerid>0 then (select vccname from smuser z4 where z4.id=a.dealerid) else '' end L";
		s += "select a.id A,(select vccode from tcmodel z1 where z1.id=b.imodelid)||'/'||b.vccode B,(select vccode from tcimage z2 where z2.id=a.imageid) C,substr(a.content,0,100) D,to_char(a.date1,'yyyy-mm-dd') E,case when a.deal>0 then 'Y' else '' end F,a.vehid G,a.imageid H,a.deal I,to_char(a.date2,'yyyy-mm-dd') J,(select vccname from smuser z3 where z3.id=a.userid) K,case when a.dealerid>0 then (select vccname from smuser z4 where z4.id=a.dealerid) else '' end L";
		s += ",row_number() over(order by a.deal,a.date1 desc) as ln";
		s += "  from smsuggest a left join tcveh b on b.id=a.vehid) where ln>" + iPage + " and ln<=" + (iPage+nPageSize);
		return getData(s, null);
	}
	public String getSuggestContent(String id) throws Exception 
	{
		return getValue("smsuggest","content", 1, "id", id);
	}
	
	public boolean setSuggestDeal(String id) throws Exception 
	{
		String userid=(String)request.getSession().getAttribute("userid");;
		return runSql("update smsuggest set deal=1,date2=sysdate,dealerid="+userid+" where id="+id, null);
	}
	
	public List queryPart(String code,String name,String photo,String price,String newid, String sale) throws Exception
	{
		if(code != null) code = code.trim().toUpperCase();
		if(name != null) name = name.trim().toUpperCase();
		String s = "";
		if(name!=null && !name.isEmpty())s = "(a.vcename like '%"+name+"%' or a.vccname like '%"+name+"%')";
		UserBean d = new UserBean(s);
		d.append1("a.vccode", code, 3);
		d.append1("a.iphotoflag", photo, 1);
		d.append1("a.isaleflag", sale, 1);
		if(price!=null && !price.isEmpty())
		{
			if(price.equalsIgnoreCase("0")) d.append1("a.iprice=0");
			else if(price.equalsIgnoreCase("1")) d.append1("a.iprice>0");
		}
		if(newid!=null && !newid.isEmpty())
		{
			if(newid.equalsIgnoreCase("0")) d.append1("a.newid=0");
			else if(newid.equalsIgnoreCase("1")) d.append1("a.newid>0");
		}
		if(d.isEmpty()) return null;
		s = "select a.id,a.vccode A,a.vcename B,a.vccname C,case when a.iprice>0 then '$'||to_char(a.iprice,'fm99990.99') else '' end D,case when a.newid>0 then (select b.vccode from smpart b where b.id=a.newid) else '' end E,case when a.iphotoflag=0 then '' else 'Y' end F,case when a.isaleflag=0 then 'Y' else '' end G,to_char(a.ddate,'yyyy-mm-dd hh24:mi') H";
		s += " from smpart a where " + d.result();
		s += " order by A";
		d = null;
		return getData(s, null);
	}
	
	public List getPartByID(String id) throws Exception
	{
		if(id==null || id.isEmpty()) return null;
		String s = "select a.id,a.vccode,a.vccname,a.vcename,case when a.iprice>0 then to_char(a.iprice,'fm99990.99') else '' end price,a.isaleflag,case when a.iphotoflag=0 then '无' else '有' end photo,to_char(a.ddate,'yyyy-mm-dd hh24:mi:ss') ddate";
		s += ",case when a.newid>0 then (select b.vccode from smpart b where b.id=a.newid) else '' end newcode,a.tdate,a.vcenote,case when a.iuser>0 then (select c.vccname from smuser c where c.id=a.iuser) else '' end vcuser,to_char(a.rdate,'yyyy-mm-dd hh24:mi:ss') rdate";
		s += " from smpart a where a.id=" + id;
		return getData(s, null);
	}
	
	public String savePart(String id, String sd[]) throws Exception
	{
		int i;		
		List ls;
		Hashtable ht;
		String s, userid=(String)request.getSession().getAttribute("userid");
		if(id == null) id = "";
		UserBean d = new UserBean();
		DBASession db = getSession();
		boolean bNew = id.isEmpty();
		boolean b[] = new boolean[8];
		for(i=0; i<8; i++)
		{
			if(bNew) b[i] = true;
			else
			{
				b[i] = sd[i].substring(0,1).equalsIgnoreCase("Y");
				if(b[i]) sd[i] = sd[i].substring(1);
			}			
		}	
		
		if(b[5])
		{
			s = "select id from smpart where rownum<2 and vccode='" + sd[5] + "'";
			ls = getData(db, s, null);
			if(ls.size() > 0)
			{
				ht = (Hashtable)ls.get(0);
				sd[5] = ht.get("id").toString();
			}
			else return "Invalid S.P/N!";
		}
		
		if(id.isEmpty())
		{
			s="select max(id)+1 as id from smpart";
			ls=getData(db,s,null);;
			ht=(Hashtable)ls.get(0);
			id=ht.get("id").toString();
			d.append2("id",id,true,bNew);
		}
		
		if(b[0]) d.append2("vccode",sd[0],false,bNew);
		if(b[1]) d.append2("vccname",sd[1],false,bNew);
		if(b[2]) d.append2("vcename",sd[2],false,bNew);
		if(b[3]) d.append2("iprice",sd[3],true,bNew);
		if(b[4]) d.append2("isaleflag",sd[4],true,bNew);
		if(b[5]) d.append2("newid",sd[5],true,bNew);
		if(b[6]) d.append2("tdate",sd[6],false,bNew);
		if(b[7]) d.append2("vcenote",sd[7],false,bNew);
		if(d.isEmpty()) return "Error 112";		
		if(b[0] || b[1] || b[2] || b[3] || b[4]) d.append2("ddate","sysdate",true,bNew);
		if(b[5] || b[6] || b[7])
		{
			d.append2("iuser",userid,true,bNew);
			d.append2("rdate","sysdate",true,bNew);
		}
		
		if(bNew) s="insert into smpart("+d.result1()+") values("+d.result2()+")";
		else	 s="update smpart set "+d.result()+" where id="+id;
		d = null;
		return runSql(s,null) ? "0" : "Error 113";
	}
	
	public String getID(String mode, String code) throws Exception
	{
		code = code.replaceAll("'", "");
		if(mode.isEmpty() || code.isEmpty()) return "0";
		String s = "";
		if(mode.equalsIgnoreCase("veh"))
		{
			int k = code.indexOf("/");
			if(k > 0) s="select a.id from tcveh a left join tcmodel b on b.id=a.imodelid where rownum<2 and b.vccode='" + code.substring(0,k) + "' and a.vccode='" + code.substring(k+1) + "'";
			else if(code.length() >= 18) s = "select id from tcveh where rownum<2 and vccode='" + code + "'";
		}
		else if(mode.equalsIgnoreCase("image"))
		{
			s = "select id from tcimage where rownum<2 and vccode='" + code + "'";
		}
		else if(mode.equalsIgnoreCase("part"))
		{
			s = "select id from smpart where rownum<2 and vccode='" + code + "'";
		}
		if(s.isEmpty()) return "0";
		
		List ls = getData(s,null);
		if(ls.size() < 1) return "0";
		Hashtable ht = (Hashtable)ls.get(0);
		ls = null;
		s = ht.get("id").toString();
		ht = null;
		return s;
	}
	
	public List queryVehLink(String vehid, String line, String imageid, String partid) throws Exception
	{
		UserBean d = new UserBean();
		d.append1("lk.ivehid", vehid, 1);
		d.append1("lk.ilineid", line, 1);
		d.append1("lk.iimageid", imageid, 1);
		d.append1("lk.ipartid", partid, 1);
		if(d.isEmpty()) return null;
		
		String s = "select lk.id A,lk.ilineid B,lk.ipartid C,lk.iimageid D,case when lk.iqty=255 then 'X' else to_char(lk.iqty) end E,case when lk.ihot=0 then '' else to_char(lk.ihot) end F,m.seq G,m.vccode H,p.vccode I,p.vccname J,md.vccode||'/'||v.vccode K,t3.vccname L,lk.ivehid M";
		s += " from tcvehlink lk left join tcveh v on v.id=lk.ivehid left join tcimage m on m.id=lk.iimageid left join smpart p on p.id=lk.ipartid left join tct3 t3 on t3.id=m.t3d left join tcmodel md on md.id=v.imodelid";
		s += " where " + d.result();
		s += " order by lk.ilineid,lk.ivehid,lk.iimageid,lk.ipartid";
		return getData(s,null);
	}
	
	public List getVehLinkByID(String id) throws Exception
	{
		if(id==null || id.isEmpty()) return null;
		String s = "select lk.ilineid line,md.vccode||'/'||v.vccode vcode,m.vccode mcode,case when lk.ihot=0 then '' else to_char(lk.ihot) end hot,p.vccode pcode,lk.iqty qty,to_char(lk.ddate,'yyyy-mm-dd hh24:mi:ss') ddate";
		s += " from tcvehlink lk left join tcveh v on v.id=lk.ivehid left join tcimage m on m.id=lk.iimageid left join smpart p on p.id=lk.ipartid left join tcmodel md on md.id=v.imodelid";
		s += " where lk.id=" + id;
		return getData(s, null);
	}
	
	public String saveVehLink(String id, String sd[]) throws Exception
	{
		int i;		
		List ls;
		Hashtable ht;
		String s;
		if(id == null) id = "";
		DBASession db = getSession();
		boolean bNew = id.isEmpty();
		boolean b[] = new boolean[6];
		for(i=0; i<6; i++)
		{
			if(bNew) b[i] = true;
			else
			{
				b[i] = sd[i].substring(0,1).equalsIgnoreCase("Y");
				if(b[i]) sd[i] = sd[i].substring(1);
			}			
		}	
		
		if(b[1])
		{
			i = sd[1].indexOf("/");
			if(i > 0) s="select a.id from tcveh a left join tcmodel b on b.id=a.imodelid where rownum<2 and b.vccode='" + sd[1].substring(0,i) + "' and a.vccode='" + sd[1].substring(i+1) + "'";
			else if(sd[1].length() >= 18) s = "select id from tcveh where rownum<2 and vccode='" + sd[1] + "'";
			else { b=null; db=null; return "Invalid vehicle code!"; }
			ls = getData(db, s, null);
			if(ls.size() > 0)
			{
				ht = (Hashtable)ls.get(0);
				sd[1] = ht.get("id").toString();
			}
			else { b=null; db=null; return "Invalid vehicle code!"; }
		}
		if(b[2])
		{
			if(sd[2].isEmpty())sd[2] = "0";
			else
			{
				s = "select id from tcimage where rownum<2 and vccode='" + sd[2] + "'";
				ls = getData(db, s, null);
				if(ls.size() > 0)
				{
					ht = (Hashtable)ls.get(0);
					sd[2] = ht.get("id").toString();
				}
				else { b=null; db=null; return "Invalid image code!"; }
			}
		}
		if(b[3])
		{
			if(sd[3].isEmpty())sd[3] = "0";
		}
		if(b[4])
		{
			s = "select id from smpart where rownum<2 and vccode='" + sd[4] + "'";
			ls = getData(db, s, null);
			if(ls.size() > 0)
			{
				ht = (Hashtable)ls.get(0);
				sd[4] = ht.get("id").toString();
			}
			else { b=null; db=null; return "Invalid image code!"; }
		}
		if(b[5])
		{
			if(sd[5].isEmpty())sd[5] = "0";
		}
		
		UserBean d = new UserBean();
		if(bNew)
		{
			s = "select id from tcvehlink where rownum<2 and ivehid=0";
			ls=getData(db,s,null);
			if(ls.size() > 0) bNew = false;
			else
			{
				s="select max(id)+1 as id from tcvehlink";
				ls=getData(db,s,null);
			}
			ht=(Hashtable)ls.get(0);
			id=ht.get("id").toString();
			d.append2("id",id,true,bNew);
		}
		db = null;	ls=null;	ht=null;
		
		if(b[0]) d.append2("ilineid",sd[0],true,bNew);
		if(b[1]) d.append2("ivehid",sd[1],true,bNew);
		if(b[2]) d.append2("iimageid",sd[2],true,bNew);
		if(b[3]) d.append2("ihot",sd[3],true,bNew);
		if(b[4]) d.append2("ipartid",sd[4],true,bNew);
		if(b[5]) d.append2("iqty",sd[5],true,bNew);
		sd=null;	b=null;
		if(d.isEmpty()) { d=null; return "Error 112"; }		
		d.append2("ddate","sysdate",true,bNew);
		
		if(bNew) s="insert into tcvehlink("+d.result1()+") values("+d.result2()+")";
		else	 s="update tcvehlink set "+d.result()+" where id="+id;
		d = null;
		return runSql(s,null) ? "0" : "Error 113";
	}
	
	public boolean deleteVehLink(String id) throws Exception 
	{
		String s = "update tcvehlink set ilineid=0,ivehid=0,ipartid=0,iimageid=0,ihot=0,iqty=0,ddate=sysdate where id="+id;
		return runSql(s,null);
	}
	
	public List queryAsmLink(String asmid, String line, String imageid, String partid) throws Exception
	{
		UserBean d = new UserBean();
		d.append1("lk.iasmid", asmid, 1);
		d.append1("lk.ilineid", line, 1);
		d.append1("lk.iimageid", imageid, 1);
		d.append1("lk.ipartid", partid, 1);
		if(d.isEmpty()) return null;
		
		String s = "select lk.id A,lk.ilineid B,lk.ipartid C,lk.iimageid D,case when lk.iqty=255 then 'X' else to_char(lk.iqty) end E,case when lk.ihot=0 then '' else to_char(lk.ihot) end F,m.seq G,m.vccode H,p2.vccode I,p2.vccname J,p1.vccode K,t3.vccname L,lk.iasmid M";
		s += " from tcasmlink lk left join smpart p1 on p1.id=lk.iasmid left join tcimage m on m.id=lk.iimageid left join smpart p2 on p2.id=lk.ipartid left join tct3 t3 on t3.id=m.t3d";
		s += " where " + d.result();
		s += " order by lk.ilineid,lk.iasmid,lk.iimageid,lk.ipartid";
		return getData(s,null);
	}
	
	public List getAsmLinkByID(String id) throws Exception
	{
		if(id==null || id.isEmpty()) return null;
		String s = "select lk.ilineid line,p1.vccode acode,m.vccode mcode,case when lk.ihot=0 then '' else to_char(lk.ihot) end hot,p2.vccode pcode,lk.iqty qty,to_char(lk.ddate,'yyyy-mm-dd hh24:mi:ss') ddate";
		s += " from tcasmlink lk left join smpart p1 on p1.id=lk.iasmid left join tcimage m on m.id=lk.iimageid left join smpart p2 on p2.id=lk.ipartid";
		s += " where lk.id=" + id;
		return getData(s, null);
	}
	
	public String saveAsmLink(String id, String sd[]) throws Exception
	{
		int i;		
		List ls;
		Hashtable ht;
		String s;
		if(id == null) id = "";
		DBASession db = getSession();
		boolean bNew = id.isEmpty();
		boolean b[] = new boolean[6];
		for(i=0; i<6; i++)
		{
			if(bNew) b[i] = true;
			else
			{
				b[i] = sd[i].substring(0,1).equalsIgnoreCase("Y");
				if(b[i]) sd[i] = sd[i].substring(1);
			}			
		}	
		
		if(b[1])
		{
			s = "select id from smpart where rownum<2 and vccode='" + sd[1] + "'";
			ls = getData(db, s, null);
			if(ls.size() > 0)
			{
				ht = (Hashtable)ls.get(0);
				sd[1] = ht.get("id").toString();
			}
			else { b=null; db=null; return "Invalid asm code!"; }
		}
		if(b[2])
		{
			if(sd[2].isEmpty())sd[2] = "0";
			else
			{
				s = "select id from tcimage where rownum<2 and vccode='" + sd[2] + "'";
				ls = getData(db, s, null);
				if(ls.size() > 0)
				{
					ht = (Hashtable)ls.get(0);
					sd[2] = ht.get("id").toString();
				}
				else { b=null; db=null; return "Invalid image code!"; }
			}
		}
		if(b[3])
		{
			if(sd[3].isEmpty())sd[3] = "0";
		}
		if(b[4])
		{
			s = "select id from smpart where rownum<2 and vccode='" + sd[4] + "'";
			ls = getData(db, s, null);
			if(ls.size() > 0)
			{
				ht = (Hashtable)ls.get(0);
				sd[4] = ht.get("id").toString();
			}
			else { b=null; db=null; return "Invalid image code!"; }
		}
		if(b[5])
		{
			if(sd[5].isEmpty())sd[5] = "0";
		}
		
		UserBean d = new UserBean();
		if(bNew)
		{
			s = "select id from tcasmlink where rownum<2 and iasmid=0";
			ls=getData(db,s,null);
			if(ls.size() > 0) bNew = false;
			else
			{
				s="select max(id)+1 as id from tcasmlink";
				ls=getData(db,s,null);
			}
			ht=(Hashtable)ls.get(0);
			id=ht.get("id").toString();
			d.append2("id",id,true,bNew);
		}
		db = null;	ls=null;	ht=null;
		
		if(b[0]) d.append2("ilineid",sd[0],true,bNew);
		if(b[1]) d.append2("iasmid",sd[1],true,bNew);
		if(b[2]) d.append2("iimageid",sd[2],true,bNew);
		if(b[3]) d.append2("ihot",sd[3],true,bNew);
		if(b[4]) d.append2("ipartid",sd[4],true,bNew);
		if(b[5]) d.append2("iqty",sd[5],true,bNew);
		sd=null;	b=null;
		if(d.isEmpty()) { d=null; return "Error 112"; }		
		d.append2("ddate","sysdate",true,bNew);
		
		if(bNew) s="insert into tcasmlink("+d.result1()+") values("+d.result2()+")";
		else	 s="update tcasmlink set "+d.result()+" where id="+id;
		d = null;
		return runSql(s,null) ? "0" : "Error 113";
	}
	
	public boolean deleteAsmLink(String id) throws Exception 
	{
		String s = "update tcasmlink set ilineid=0,iasmid=0,ipartid=0,iimageid=0,ihot=0,iqty=0,ddate=sysdate where id="+id;
		return runSql(s,null);
	}
	
	public List queryImage(String sys, String sub, String image, String cname, String code, String seq) throws Exception
	{
		if(cname != null) cname = cname.trim();
		if(code != null) code = code.trim().toUpperCase();
		if(seq != null) seq = seq.trim();
		UserBean d = new UserBean();
		d.append1("a.it1d", sys, 1);
		d.append1("a.it2d", sub, 1);
		d.append1("a.it3d", image, 1);
		d.append1("t3.vccname", cname, 3);
		d.append1("a.vccode", code, 3);
		d.append1("a.seq", seq, 2);
		String s = "select a.id A,a.vcseq B,a.vccode C,t1.vcename D,t2.vcename E,t3.vcename F,t3.vccname G,a.vcnote H,to_char(a.ddate,'yyyy-mm-dd hh24:mi') I,to_char(a.pdate,'yyyy-mm-dd hh24:mi') J from tcimage a left join tct1 t1 on t1.id=a.it1d left join tct2 t2 on t2.id=a.it2d left join tct3 t3 on t3.id=a.it3d";
		if(!d.isEmpty()) s += " where " + d.result();
		s += " order by a.vcseq";
		return getData(s,null);
	}
	
	public List getImage(String id) throws Exception
	{
		String sql="select vccode,seq,t1d,t2d,t3d,vccnote,to_char(ddate,'yyyy-mm-dd hh24:mi:ss') ddate from tcimage where rownum<2 and id="+id;
		return getData(sql,null);
	}
	
	public String saveImage(String id, String sd[]) throws Exception
	{
		if(id == null) id = "";
		boolean bNew = id.isEmpty();
		boolean b[] = new boolean[6];
		intialSD(bNew, 6, sd, b);
		UserBean d = new UserBean();
		if(bNew)
		{
			id=getValue("tcimage","max(id)+1", 0, null, null);
			d.append2("id",id,true,bNew);
		}
		if(b[0]) d.append2("vccode",sd[0],false,bNew);
		if(b[1]) d.append2("seq",sd[1],false,bNew);
		if(b[2]) d.append2("t1d",sd[2],true,bNew);
		if(b[3]) d.append2("t2d",sd[3],true,bNew);
		if(b[4]) d.append2("t3d",sd[4],true,bNew);
		if(b[5]) d.append2("vccnote",sd[5],false,bNew);
		sd=null;	b=null;
		if(d.isEmpty()) { d=null; return "Error 112"; }		
		d.append2("ddate","sysdate",true,bNew);
		String s;
		if(bNew) s="insert into tcimage("+d.result1()+") values("+d.result2()+")";
		else	 s="update tcimage set "+d.result()+" where id="+id;
		d = null;
		return runSql(s,null) ? "0" : "Error 113";
	}
	
	private void intialSD(boolean bNew, int n, String sd[], boolean b[])
	{
		for(int i=0; i<n; i++)
		{
			if(bNew) b[i] = true;
			else
			{
				b[i] = sd[i].substring(0,1).equalsIgnoreCase("Y");
				if(b[i]) sd[i] = sd[i].substring(1);
			}			
		}
	}
	
	public List queryT(String type, String name) throws Exception
	{
		String s = "select id A,iseq B,vcename C,vccname D,to_char(ddate,'yyyy-mm-dd hh24:mi') E";
		if(type.equalsIgnoreCase("1")) s += ",to_char(pdate,'yyyy-mm-dd hh24:mi') F";
		s += " from tct" + type;
		if(name != null)
		{
			name = name.trim();
			if(!name.isEmpty()) s += " where vcename like '%" + name.toUpperCase() + "%' or vccname like '%" + name + "%'";
		}
		s += " order by iseq,vcename";
		return getData(s,null);
	}
	
	public List getT(String type, String id) throws Exception
	{
		String sql="select iseq,vcename,vccname,to_char(ddate,'yyyy-mm-dd hh24:mi:ss') ddate from tct" + type + " where rownum<2 and id=" + id;
		return getData(sql,null);
	}
	
	public String saveT(String type, String id, String sd[]) throws Exception
	{
		if(id == null) id = "";
		boolean bNew = id.isEmpty();
		boolean b[] = new boolean[3];
		intialSD(bNew, 3, sd, b);
		UserBean d = new UserBean();
		if(bNew)
		{
			id=getValue("tct"+type, "max(id)+1", 0, null, null);
			d.append2("id",id,true,bNew);
		}
		if(b[0]) d.append2("iseq",sd[0],true,bNew);
		if(b[1]) d.append2("vcename",sd[1],false,bNew);
		if(b[2]) d.append2("vccname",sd[2],false,bNew);
		sd=null;	b=null;
		if(d.isEmpty()) { d=null; return "Error 112"; }		
		d.append2("ddate","sysdate",true,bNew);
		String s;
		if(bNew) s = "insert into tct" + type + "("+d.result1()+") values("+d.result2()+")";
		else	 s = "update tct" + type + " set "+d.result()+" where id="+id;
		d = null;
		return runSql(s,null) ? "0" : "Error 113";
	}	
}