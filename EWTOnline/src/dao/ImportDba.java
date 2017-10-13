package dao;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class ImportDba extends BaseDba
{
	public ImportDba()
	{
	}
	
	public List getIDs(String codes) throws Exception
	{
		Intial();
		int i, j, k;
		List ls = new ArrayList();
		String sID, s1, s2, s, cs, sL;
		String[] ar1 = codes.split("\n");
		String[] ar2 = null;
		for(i=0; i<ar1.length; i++)
		{
			k = ar1[i].indexOf(":")+1;
			if(k < 1) continue;
			sID = ar1[i].substring(0,k);
			s2 = ar1[i].substring(k);
			if(sID.isEmpty() || s2.isEmpty()) continue;
				
			sL = "";
			ar2 = s2.split("\t");			
			for(j=0; j<ar2.length; j++)
			{
				k = ar2[j].indexOf("=");
				if(k < 1) continue;
				s1 = ar2[j].substring(0,k+1);
				s2 = ar2[j].substring(k+1).replaceAll("'", "");
				if(s1.isEmpty() || s2.isEmpty()) continue;
				cs = s1.substring(0,1);
				m_s = "";
				if(cs.equalsIgnoreCase("V"))
				{
					k = s2.indexOf("/");
					if(k > 0) m_s="select a.id from tcveh a left join tcmodel b on b.id=a.imodelid where rownum<2 and b.vccode='" + s2.substring(0,k).trim() + "' and a.vccode='" + s2.substring(k+1).trim() + "'";
					else if(s2.length() >= 18) m_s = "select id from tcveh where rownum<2 and vccode='" + s2 + "'";
				}
				else if(cs.equalsIgnoreCase("I")) m_s = "select id from tcimage where rownum<2 and vccode='" + s2 + "'";
				else if(cs.equalsIgnoreCase("Z") || cs.equalsIgnoreCase("P")) m_s = "select id from smpart where rownum<2 and vccode='" + s2 + "'";
				else if(cs.equalsIgnoreCase("C")) m_s = "select id from smuser where rownum<2 and vccname='" + s2 + "'";
				else if(cs.equalsIgnoreCase("B")) m_s = "select id from tcbrand where rownum<2 and vccode='" + s2 + "'";
				else if(cs.equalsIgnoreCase("M")) m_s = "select id from tcmodel where rownum<2 and vccode='" + s2 + "'";
				else continue;
				if(m_s.isEmpty())s2="0";
				else
				{
					m_set = m_smt.executeQuery(m_s);
					if(m_set != null && m_set.next()) s2 = m_set.getString(1);
					else s2 = "0";
					m_set.close();
				}								
				if(!sL.isEmpty()) sL += ";";
				sL += s1 + s2;
			}
			ar2 = null;
			if(!sL.isEmpty()) ls.add(sID + sL);
		}
		ar1 = null;
		Close(true);
		return ls;
	}
	
	public List queryAsmBom(String lines) throws Exception
	{
		String[] ar = lines.split(";");
		m_s = "";  for(int i=0; i<ar.length; i++){ if(!m_s.isEmpty())m_s += " or ";  m_s += "isn in("+ar[i]+")"; }
		m_s = "select isn,iasmid,iimageid,ihot,ipartid,iqty,id from tcasmlink where " + m_s;
		ar = null;
		return getData(m_s,7,",");
	}
	
	public List queryVehBom(String lines,String vehids) throws Exception
	{
		String[] ar = lines.split(";");
		String s = "";	int i;
		for(i=0; i<ar.length; i++){ if(!s.isEmpty())s += " or "; s += "isn in("+ar[i]+")"; }
		m_s = "select isn,ivehid,iimageid,ihot,ipartid,iqty,id from tcvehlink where (" + s + ")";
		if(vehids != null && !vehids.isEmpty())
		{
			ar = null;
			ar = vehids.split(";");
			s = "";
			for(i=0; i<ar.length; i++){ if(!s.isEmpty())s += " or "; s += "ivehid in("+ar[i]+")"; }
			m_s += " and (" + s + ")";
		}
		ar = null;
		return getData(m_s,7,",");
	}
	
	public List importAsmBom(String bom) throws Exception
	{
		Intial();		
		int i,ret,k;
		long nID = 0;
		List ls = new ArrayList(),ld = null;
		String[] ar1 = bom.split(";");
		String[] ar2 = null;
		for(i=0; i<ar1.length; i++)
		{
			k = 0;
			ar2 = ar1[i].split(",");
			if(ar2[1].equalsIgnoreCase("3"))	  m_s = "update tcasmlink set isn=0,iasmid=0,iimageid=0,ihot=0,ipartid=0,iqty=0,ddate=sysdate where id="+ar2[2];
			else if(ar2[1].equalsIgnoreCase("2")) m_s = "update tcasmlink set isn="+ar2[3]+",iasmid="+ar2[4]+",iimageid="+ar2[5]+",ihot="+ar2[6]+",ipartid="+ar2[7]+",iqty="+ar2[8]+",ddate=sysdate where id="+ar2[2];
			else if(ar2[1].equalsIgnoreCase("1"))
			{
				if(ld == null)
				{
					ld = new ArrayList();
					m_set = m_smt.executeQuery("select id from tcasmlink where isn=0");
					while(m_set.next()) ld.add(m_set.getString(1));
					m_set.close();
				}
				if(ld.size() > 0)
				{
					k = 1;
					m_s = "update tcasmlink set isn="+ar2[2]+",iasmid="+ar2[3]+",iimageid="+ar2[4]+",ihot="+ar2[5]+",ipartid="+ar2[6]+",iqty="+ar2[7]+",ddate=sysdate where id="+ld.get(0).toString();
				}
				else
				{
					if(nID == 0)
					{
						m_set = m_smt.executeQuery("select max(id) as id from tcasmlink");
						if(m_set.next()) nID = m_set.getLong(1)+1;
						m_set.close();
					}
					k = 2;					
					m_s = "insert into tcasmlink(id,isn,iasmid,iimageid,ihot,ipartid,iqty,ddate) values("+nID+","+ar2[2]+","+ar2[3]+","+ar2[4]+","+ar2[5]+","+ar2[6]+","+ar2[7]+",sysdate)";
				}
			}
			else continue;
			ret = m_smt.executeUpdate(m_s);
			ls.add((ret>0?"Y":"N")+ar2[0]);
			if(ret>0){ if(k==1)ld.remove(0); else if(k==2)nID++; }
			ar2 = null;
		}
		ar1 = null;
		if(ld != null) { ld.clear();	ld = null; }
		Close(true);
		return ls;
	}
	
	public List importVehBom(String bom) throws Exception
	{
		Intial();		
		int i, ret, k;
		long nID = 0;
		List ls = new ArrayList(), ld = null;
		String[] ar1 = bom.split(";");
		String[] ar2 = null;
		for(i=0; i<ar1.length; i++)
		{
			k = 0;
			ar2 = ar1[i].split(",");
			if(ar2[2].equalsIgnoreCase("3"))	  m_s = "update tcvehlink set isn=0,ivehid=0,iimageid=0,ihot=0,ipartid=0,iqty=0,ddate=sysdate where id="+ar2[3];
			else if(ar2[2].equalsIgnoreCase("2")) m_s = "update tcvehlink set ivehid="+ar2[4]+",isn="+ar2[5]+",iimageid="+ar2[6]+",ihot="+ar2[7]+",ipartid="+ar2[8]+",iqty="+ar2[9]+",ddate=sysdate where id="+ar2[3];
			else if(ar2[2].equalsIgnoreCase("1"))
			{
				if(ld == null)
				{
					ld = new ArrayList();
					m_set = m_smt.executeQuery("select id from tcvehlink where isn=0");
					while(m_set.next()) ld.add(m_set.getString(1));
					m_set.close();
				}
				if(ld.size() > 0)
				{
					k = 1;
					m_s = "update tcvehlink set ivehid="+ar2[3]+",isn="+ar2[4]+",iimageid="+ar2[5]+",ihot="+ar2[6]+",ipartid="+ar2[7]+",iqty="+ar2[8]+",ddate=sysdate where id="+ld.get(0).toString();
				}
				else
				{
					if(nID == 0)
					{
						m_set = m_smt.executeQuery("select max(id) as id from tcvehlink");
						if(m_set.next()) nID = m_set.getLong(1)+1;
						m_set.close();
					}
					k = 2;					
					m_s = "insert into tcvehlink(id,ivehid,isn,iimageid,ihot,ipartid,iqty,ddate) values("+nID+","+ar2[3]+","+ar2[4]+","+ar2[5]+","+ar2[6]+","+ar2[7]+","+ar2[8]+",sysdate)";
				}
			}
			else continue;
			ret = m_smt.executeUpdate(m_s);
			ls.add((ret>0?"Y":"N")+ar2[0]+","+ar2[1]);
			if(ret>0){ if(k==1)ld.remove(0); else if(k==2)nID++; }
			ar2 = null;
		}
		ar1 = null;
		if(ld != null) { ld.clear();	ld = null; }
		Close(true);
		return ls;
	}
	
	public List queryVin(String vins) throws Exception
	{
		int i,k;
		String s1,s2;
		String[] ar = vins.split(";");
		List ls = new ArrayList();
		Intial();
		for(i=0; i<ar.length; i++)
		{
			k = ar[i].indexOf(":");
			if(k < 1) continue;
			s1 = ar[i].substring(0,k);
			s2 = ar[i].substring(k+1);
			m_s = "select id,vcesn,vcbill,iyear,ivehid,iuserid from tcvin where vcvin='"+s2+"'";//,vcnote
			m_set = m_smt.executeQuery(m_s);
			if(m_set != null && m_set.next())
			{
				s2 = "";
				for(k=1;k<7;k++)s2 += "\n"+getString(k,1);
			}
			else s2 = "\n0";
			m_set.close();
			ls.add(s1+s2);
		}
		Close(true);
		return ls;
	}
	
	public List importVin(String bom) throws Exception
	{
		Intial();		
		int i,j,k;	String cs;
		long nID = 0;
		List ls = new ArrayList();
		String[] ar1 = bom.split("\n");
		String[] ar2 = null;
		for(i=0; i<ar1.length; i++)
		{
			ar2 = ar1[i].split("\t");
			if(ar2[1].equalsIgnoreCase("1"))
			{
				if(nID == 0)
				{
					m_set = m_smt.executeQuery("select max(id) as id from tcvin");
					if(m_set.next()) nID = m_set.getLong(1)+1;
					m_set.close();
				}									
				m_s = "insert into tcvin(id,vcvin,vcesn,vcbill,iyear,ivehid,iuserid,ddate) values("+nID;
				for(k=2;k<=4;k++) m_s += ",'"+ar2[k]+"'";
				for(k=5;k<=7;k++) m_s += ","+ar2[k];
				//if(ar2.length>8) m_s += ",'"+ar2[8]+"'";
				//else m_s += ",''";
				m_s += ",sysdate)";
				k = 1;
			}
			else if(ar2[1].equalsIgnoreCase("2"))
			{
				m_s = "";
				for(j=3;j<ar2.length;j++)
				{
					k = ar2[j].indexOf(":");
					if(k < 1) continue;
					cs = ar2[j].substring(k+1);
					k = Integer.parseInt(ar2[j].substring(0,k));
					if(!m_s.isEmpty())m_s+=",";
					if(k==2)m_s+="vcesn='"+cs+"'";
					else if(k==3)m_s+="vcbill='"+cs+"'";
					else if(k==4)m_s+="iyear="+cs;
					else if(k==5)m_s+="ivehid="+cs;
					else if(k==6)m_s+="iuserid="+cs;					
					//else if(k==7)m_s+="vcnote='"+cs+"'";					
				}
				if(m_s.isEmpty())continue;
				m_s="update tcvin set "+m_s+",ddate=sysdate where id="+ar2[2];
				k = 0;
			}
			else continue;
			j = m_smt.executeUpdate(m_s);
			if(j>0 && k==1)nID++;
			ls.add((j>0?"Y":"N")+ar2[0]);
			ar2 = null;
		}
		ar1 = null;
		Close(true);
		return ls;
	}
	
	public List queryVeh(String codes) throws Exception
	{
		int i,k;
		String s1,s2;
		String[] ar = codes.split(";");
		List ls = new ArrayList();
		Intial();
		for(i=0; i<ar.length; i++)
		{
			k = ar[i].indexOf(":");
			if(k < 1) continue;
			s1 = ar[i].substring(0,k);
			s2 = ar[i].substring(k+1);
			k = s2.indexOf("/");
			if(k < 1) continue;			
			m_s = "select id,ibrandid,vcedition,vccolor,icabin,iwb,vcengine,ifuel,iemission,idrive,iac,iabs,isrs,ipdc,irhd from tcveh where imodelid="+s2.substring(0,k)+"and vccode='"+s2.substring(k+1)+"'";
			m_set = m_smt.executeQuery(m_s);
			if(m_set != null && m_set.next())
			{
				s2 = "";
				for(k=1;k<16;k++)s2 += "\n"+getString(k,1);
			}
			else s2 = "\n0";
			m_set.close();
			ls.add(s1+s2);
		}
		Close(true);
		return ls;
	}
	
	public List importVeh(String bom) throws Exception
	{
		Intial();		
		int i,j,k;	String cs;
		long nID = 0;
		List ls = new ArrayList();
		String[] ar1 = bom.split("\n");
		String[] ar2 = null;
		for(i=0; i<ar1.length; i++)
		{
			ar2 = ar1[i].split("\t");
			if(ar2[1].equalsIgnoreCase("1"))
			{
				if(nID == 0)
				{
					m_set = m_smt.executeQuery("select max(id) as id from tcveh");
					if(m_set.next()) nID = m_set.getLong(1)+1;
					m_set.close();
				}									
				m_s = "insert into tcveh(id,imodelid,vccode,ibrandid,vcedition,vccolor,icabin,iwb,vcengine,ifuel,iemission,idrive,iac,iabs,isrs,ipdc,irhd,ddate) values("+nID;
				m_s += ","+ar2[2];
				m_s += ",'"+ar2[3]+"'";
				m_s += ","+ar2[4];
				for(k=5;k<=6;k++) m_s += ",'"+ar2[k]+"'";
				for(k=7;k<=8;k++) m_s += ","+ar2[k];
				m_s += ",'"+ar2[9]+"'";
				for(k=10;k<=17;k++) m_s += ","+ar2[k];
				m_s += ",sysdate)";
				k = 1;
			}
			else if(ar2[1].equalsIgnoreCase("2"))
			{
				m_s = "";
				for(j=3;j<ar2.length;j++)
				{
					k = ar2[j].indexOf(":");
					if(k < 1) continue;
					cs = ar2[j].substring(k+1);
					k = Integer.parseInt(ar2[j].substring(0,k));
					if(!m_s.isEmpty())m_s+=",";
					if(k==2)m_s+="ibrandid="+cs;
					else if(k==3)m_s+="vcedition='"+cs+"'";
					else if(k==4)m_s+="vccolor='"+cs+"'";
					else if(k==5)m_s+="icabin="+cs;
					else if(k==6)m_s+="iwb="+cs;
					else if(k==7)m_s+="vcengine='"+cs+"'";
					else if(k==8)m_s+="ifuel="+cs;
					else if(k==9)m_s+="iemission="+cs;
					else if(k==10)m_s+="idrive="+cs;
					else if(k==11)m_s+="iac="+cs;
					else if(k==12)m_s+="iabs="+cs;
					else if(k==13)m_s+="isrs="+cs;
					else if(k==14)m_s+="ipdc="+cs;
					else if(k==15)m_s+="irhd="+cs;					
				}
				if(m_s.isEmpty())continue;
				m_s="update tcveh set "+m_s+",ddate=sysdate where id="+ar2[2];
				k = 0;
			}
			else continue;
			j = m_smt.executeUpdate(m_s);
			if(j>0 && k==1)nID++;
			ls.add((j>0?"Y":"N")+ar2[0]);
			ar2 = null;
		}
		ar1 = null;
		Close(true);
		return ls;
	}
	
	public List queryPart(String codes) throws Exception
	{
		int i,k;
		String s1,s2;
		String[] ar = codes.split(";");
		List ls = new ArrayList();
		Intial();
		for(i=0; i<ar.length; i++)
		{
			k = ar[i].indexOf(":");
			if(k < 1) continue;
			s1 = ar[i].substring(0,k);
			s2 = ar[i].substring(k+1);
			m_s = "select id,vcename,vccname from smpart where vccode='"+s2+"'";
			m_set = m_smt.executeQuery(m_s);
			if(m_set != null && m_set.next())
			{
				s2 = "";
				s2 += "\n"+getString(1);
				s2 += "\n"+URLEncoder.encode(getString(2),"UTF-8");
				s2 += "\n"+URLEncoder.encode(getString(3),"UTF-8");
			}
			else s2 = "\n0";
			m_set.close();
			ls.add(s1+s2);
		}
		Close(true);
		return ls;
	}
	
	public List importPart(String bom) throws Exception
	{
		Intial();		
		int i,j,k;	String cs;
		long nID = 0;
		List ls = new ArrayList();
		String[] ar1 = bom.split("\n");
		String[] ar2 = null;
		for(i=0; i<ar1.length; i++)
		{
			//ar1[i].replace('\"', ' ');
			//ar1[i].replace('\'', ' ');
			ar2 = ar1[i].split("\t");
			if(ar2[1].equalsIgnoreCase("1"))
			{
				if(nID == 0)
				{
					m_set = m_smt.executeQuery("select max(id) as id from smpart");
					if(m_set.next()) nID = m_set.getLong(1)+1;
					m_set.close();
				}									
				m_s = "insert into smpart(id,vccode,vcename,vccname,ddate) values("+nID+",'"+ar2[2];
				m_s += "','"+URLDecoder.decode(ar2[3],"UTF-8").replace("'", "").trim();
				m_s += "','"+URLDecoder.decode(ar2[4],"UTF-8").replace("'", "").trim();
				m_s += "',sysdate)";
				k = 1;
			}
			else if(ar2[1].equalsIgnoreCase("2"))
			{
				m_s = "";
				for(j=3;j<ar2.length;j++)
				{
					k = ar2[j].indexOf(":");
					if(k < 1) continue;
					cs = ar2[j].substring(k+1);
					k = Integer.parseInt(ar2[j].substring(0,k));
					if(!m_s.isEmpty())m_s+=",";
					if(k==2)m_s+="vcename='"+URLDecoder.decode(cs,"UTF-8").replace("'", "").trim()+"'";	
					else if(k==3)m_s+="vccname='"+URLDecoder.decode(cs,"UTF-8").replace("'", "").trim()+"'";
				}
				if(m_s.isEmpty())continue;
				m_s="update smpart set "+m_s+",ddate=sysdate where id="+ar2[2];
				k = 0;
			}
			else continue;
			try{ j = m_smt.executeUpdate(m_s); }
			catch(Exception e){ j=-1;System.out.println(m_s); }
			//finally	{j>0; }
			if(j>0 && k==1)nID++;
			ls.add((j>0?"Y":"N")+ar2[0]);
			ar2 = null;
		}
		ar1 = null;
		Close(true);
		return ls;
	}
	
	public List queryPrice(boolean bDefault,String partids,String userids) throws Exception
	{
		String[] ar = partids.split(";");
		String s;	int i;	m_s = "";
		if(bDefault)
		{
			s = "";
			for(i=0; i<ar.length; i++){ if(!s.isEmpty())s += " or "; s += "id in("+ar[i]+")"; }
			m_s = "select id A,0 B,iprice C,0 D from smpart where (" + s + ") union ";
		}
		s = "";
		for(i=0; i<ar.length; i++){ if(!s.isEmpty())s += " or "; s += "ipartid in("+ar[i]+")"; }
		m_s += "select ipartid A,iuserid B,iprice C,id D from smprice where (" + s + ")";
		if(userids != null && !userids.isEmpty())
		{
			ar = null;
			ar = userids.split(";");
			s = "";
			for(i=0; i<ar.length; i++){ if(!s.isEmpty())s += " or "; s += "iuserid in("+ar[i]+")"; }
			m_s += " and (" + s + ")";
		}
		ar = null;
		return getData(m_s,4,",");
	}
	
	public List importPrice(String bom) throws Exception
	{
		Intial();		
		int i, ret, k;
		long nID = 0;
		List ls = new ArrayList(), ld = null;
		String[] ar1 = bom.split(";");
		String[] ar2 = null;
		for(i=0; i<ar1.length; i++)
		{
			k = 0;
			ar2 = ar1[i].split(",");
			if(ar2[2].equalsIgnoreCase("P"))	  m_s = "update smpart set iprice="+ar2[4]+",cdate=sysdate where id="+ar2[3];
			else if(ar2[2].equalsIgnoreCase("E")) m_s = "update smprice set iprice="+ar2[4]+",ddate=sysdate where id="+ar2[3];
			else if(ar2[2].equalsIgnoreCase("D")) m_s = "update smprice set ipartid=0,iuserid=0,iprice=0,ddate=sysdate where id="+ar2[3];
			else if(ar2[2].equalsIgnoreCase("A"))
			{
				if(ld == null)
				{
					ld = new ArrayList();
					m_set = m_smt.executeQuery("select id from smprice where ipartid=0");
					while(m_set.next()) ld.add(m_set.getString(1));
					m_set.close();
				}
				if(ld.size() > 0)
				{
					k = 1;
					m_s = "update smprice set iuserid="+ar2[3]+",ipartid="+ar2[4]+",iprice="+ar2[5]+",ddate=sysdate where id="+ld.get(0).toString();
				}
				else
				{
					if(nID == 0)
					{
						m_set = m_smt.executeQuery("select max(id) as id from smprice");
						if(m_set.next()) nID = m_set.getLong(1)+1;
						m_set.close();
					}
					k = 2;					
					m_s = "insert into smprice(id,iuserid,ipartid,iprice,ddate) values("+nID+","+ar2[3]+","+ar2[4]+","+ar2[5]+",sysdate)";
				}
			}
			else continue;
			ret = m_smt.executeUpdate(m_s);
			ls.add((ret>0?"Y":"N")+ar2[0]+","+ar2[1]);
			if(ret>0){ if(k==1)ld.remove(0); else if(k==2)nID++; }
			ar2 = null;
		}
		ar1 = null;
		if(ld != null) { ld.clear();	ld = null; }
		Close(true);
		return ls;
	}
	
	public List querySupersed(String ids) throws Exception
	{
		int i,k;
		String s1,s2;
		String[] ar = ids.split(";");
		List ls = new ArrayList();
		Intial();
		for(i=0; i<ar.length; i++)
		{
			k = ar[i].indexOf(":");
			if(k < 1) continue;
			s1 = ar[i].substring(0,k);
			s2 = ar[i].substring(k+1);
			m_s = "select inewid,tdate from smpart where id="+s2;
			m_set = m_smt.executeQuery(m_s);
			if(m_set != null && m_set.next())
			{
				s2 = "";
				s2 += "\n"+getString(1);
				s2 += "\n"+getString(2);
			}
			else s2 = "\n0";
			m_set.close();
			ls.add(s1+s2);
		}
		Close(true);
		return ls;
	}
	
	public List importSupersed(String bom, String userid) throws Exception
	{
		Intial();		
		int i,ret;
		List ls = new ArrayList();
		String[] ar1 = bom.split(";");
		String[] ar2 = null;
		for(i=0; i<ar1.length; i++)
		{
			ar2 = ar1[i].split(",");
			m_s = "update smpart set inewid="+ar2[2]+",tdate='"+ar2[3]+"',rdate=sysdate,iuserid="+userid+" where id="+ar2[1];
			ret = m_smt.executeUpdate(m_s);
			ls.add((ret>0?"Y":"N")+ar2[0]);
			ar2 = null;			
		}
		ar1 = null;
		Close(true);
		return ls;
	}
	
	public List querySale(String ids) throws Exception
	{
		int i,k;
		String s1,s2;
		String[] ar = ids.split(";");
		List ls = new ArrayList();
		Intial();
		for(i=0; i<ar.length; i++)
		{
			k = ar[i].indexOf(":");
			if(k < 1) continue;
			s1 = ar[i].substring(0,k);
			s2 = ar[i].substring(k+1);
			m_s = "select id,ioff,inum,istate from smsale where ipartid="+s2;
			m_set = m_smt.executeQuery(m_s);
			if(m_set != null && m_set.next())
			{
				s2 = "";
				s2 += "\n"+getString(1);
				s2 += "\n"+getString(2);
				s2 += "\n"+getString(3);
				s2 += "\n"+getString(4);
			}
			else s2 = "\n0";
			m_set.close();
			ls.add(s1+s2);
		}
		Close(true);
		return ls;
	}
	
	public List importSale(String bom) throws Exception
	{
		Intial();		
		int i,j,k;	String cs;
		long nID = 0;
		List ls = new ArrayList();
		String[] ar1 = bom.split("\n");
		String[] ar2 = null;
		for(i=0; i<ar1.length; i++)
		{
			ar2 = ar1[i].split("\t");
			if(ar2[1].equals("1"))
			{
				if(nID == 0)
				{
					m_set = m_smt.executeQuery("select max(id) as id from smsale");
					if(m_set.next()) nID = m_set.getLong(1)+1;
					m_set.close();
				}									
				m_s = "insert into smsale(id,ipartid,ioff,inum,istate,ddate1) values("+nID+","+ar2[2]+","+ar2[3]+","+ar2[4]+",1,sysdate)";
				k = 1;
			}
			else if(ar2[1].equalsIgnoreCase("2"))
			{
				m_s = "";
				for(j=3;j<ar2.length;j++)
				{
					k = ar2[j].indexOf(":");
					if(k < 1) continue;
					cs = ar2[j].substring(k+1);
					k = Integer.parseInt(ar2[j].substring(0,k));
					if(!m_s.isEmpty())m_s+=",";
					if(k==2)m_s+="ioff="+cs;
					else if(k==3)m_s+="inum="+cs;
					else if(k==4)m_s+="istate="+cs;
				}
				if(m_s.isEmpty())continue;
				m_s="update smsale set "+m_s+",ddate1=sysdate where id="+ar2[2];
				k = 0;
			}
			else continue;
			try{ j = m_smt.executeUpdate(m_s); }
			catch(Exception e){ j=-1;System.out.println(m_s); }
			if(j>0 && k==1)nID++;
			ls.add((j>0?"Y":"N")+ar2[0]);
			ar2 = null;
		}
		ar1 = null;
		Close(true);
		return ls;
	}
}