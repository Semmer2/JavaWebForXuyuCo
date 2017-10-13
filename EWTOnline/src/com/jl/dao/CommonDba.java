package com.jl.dao;
import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

public class CommonDba 
{
	Connection m_con;
	Statement m_smt;
	ResultSet m_set;
	public CommonDba()
	{
		m_con = null;
		m_smt = null;
		m_set = null;
	}
	
	private void Intial() throws Exception
	{
		Class.forName("oracle.jdbc.driver.OracleDriver");
		m_con = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:DBSVRCAR","jlcar","jlcar");
		m_con.setAutoCommit(false);
		m_smt = m_con.createStatement();
		m_set = null;
	}
	
	private void Close() throws Exception
	{
		if(m_set != null) {m_set.close();		m_set = null; }
		m_smt.close();		m_smt = null;
		m_con.close();		m_con = null;
	}
	
	public List getData(String sql, String sp) throws Exception
	{
		List ls = new ArrayList();
		try
		{
			Intial();			
			String s;
			m_set = m_smt.executeQuery(sql);
			int i, n = m_set.getMetaData().getColumnCount();
			while(m_set.next())
			{
				s = "";
				for(i=1; i<=n; i++) { if(i>1)s += sp;	s += m_set.getString(i); }
				ls.add(s);
			}
			Close();
		}
		catch (Exception e){ throw e; }
		return ls;
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
				k = ar2[j].indexOf(":")+1;
				if(k < 1) continue;
				s1 = ar2[j].substring(0,k);
				s2 = ar2[j].substring(k);
				if(s1.isEmpty() || s2.isEmpty()) continue;
				cs = s1.substring(0,1);
				if(cs.equalsIgnoreCase("M")) cs = "tcimage";
				else if(cs.equalsIgnoreCase("P")) cs = "smpart";
				else continue;
					
				s = "select id from " + cs + " where rownum<2 and vccode='" + s2.replaceAll("'","") + "'";
				m_set = m_smt.executeQuery(s);
				if(m_set.next()) s2 = m_set.getString(1);
				else s2 = "0";
				m_set.close();
									
				if(!sL.isEmpty()) sL += ";";
				sL += s1 + s2;
			}
			ar2 = null;
			if(!sL.isEmpty()) ls.add(sID + sL);
		}
		ar1 = null;
		Close();
		return ls;
	}
	
	public List queryVehBom(String vehids, String lines) throws Exception
	{
		String s = "select ilineid,ivehid,iimageid,ihot,ipartid,iqty,id from tcvehlink where ivehid in (" + vehids + ")";
		if(lines != null && !lines.isEmpty()) s += " and ilineid in (" + lines + ")";
		return getData(s,",");
	}
	
	public List queryAsmBom(String asmids, String lines) throws Exception
	{
		String s = "select ilineid,iasmid,iimageid,ihot,ipartid,iqty,id from tcasmlink where iasmid in (" + asmids + ")";
		if(lines != null && !lines.isEmpty()) s += " and ilineid in (" + lines + ")";
		return getData(s,",");
	}
	
	public List importVehBom(String bom) throws Exception
	{
		Intial();		
		int i, ret;
		long nID = 0;
		List ls = new ArrayList(), ld = null;
		String s1, s2, s;
		String[] ar1 = bom.split(";");
		String[] ar2 = null;
		for(i=0; i<ar1.length; i++)
		{
			ar2 = ar1[i].split(",");
			if(ar2[0].equalsIgnoreCase("D"))
			{
				s1 = ar2.length>2 ? ar2[2] : "";
				s2 = "update tcvehlink set ilineid=0,ivehid=0,iimageid=0,ihot=0,ipartid=0,iqty=0,ddate=sysdate where id=" + ar2[1];
			}
			else if(ar2[0].equalsIgnoreCase("E"))
			{
				s1 = ar2[8];
				s2 = "update tcvehlink set ilineid=" + ar2[2] + ",ivehid=" + ar2[3] + ",iimageid=" + ar2[4] + ",ihot=" + ar2[5] + ",ipartid=" + ar2[6] + ",iqty=" + ar2[7] + ",ddate=sysdate where id=" + ar2[1];
			}
			else if(ar2[0].equalsIgnoreCase("A"))
			{
				s1 = ar2[7];
				if(ld == null)
				{
					ld = new ArrayList();
					s = "select id from tcvehlink where ilineid=0 and ivehid=0 and ipartid=0";
					m_set = m_smt.executeQuery(s);
					while(m_set.next()) ls.add(m_set.getString(1));
					m_set.close();
				}
				if(ld.size() > 0)
				{
					s = ld.get(0).toString();
					ld.remove(0);
					s2 = "update tcvehlink set ilineid=" + ar2[1] + ",ivehid=" + ar2[2] + ",iimageid=" + ar2[3] + ",ihot=" + ar2[4] + ",ipartid=" + ar2[5] + ",iqty=" + ar2[6] + ",ddate=sysdate where id=" + s;
				}
				else
				{
					if(nID == 0)
					{
						s = "select max(id) from tcvehlink";
						m_set = m_smt.executeQuery(s);
						if(m_set.next()) nID = m_set.getLong(1);
						m_set.close();
					}
					nID++;
					s2 = "insert into tcvehlink(id,ilineid,ivehid,iimageid,ihot,ipartid,iqty,ddate) values(" + nID + "," + ar2[1] + "," + ar2[2] + "," + ar2[3] + "," + ar2[4] + "," + ar2[5] + "," + ar2[6] + ",sysdate)";
				}
			}
			else continue;
			ar2 = null;
			ret = m_smt.executeUpdate(s2);
			if(!s1.isEmpty()) ls.add(s1 + (ret>0?"-Y":"-N"));
		}
		ar1 = null;
		if(ld != null) { ld.clear();	ld = null; }
		Close();
		return ls;
	}
	
	public List importAsmBom(String bom) throws Exception
	{
		Intial();		
		int i, ret;
		long nID = 0;
		List ls = new ArrayList(), ld = null;
		String s1, s2, s;
		String[] ar1 = bom.split(";");
		String[] ar2 = null;
		for(i=0; i<ar1.length; i++)
		{
			ar2 = ar1[i].split(",");
			if(ar2[0].equalsIgnoreCase("D"))
			{
				s1 = ar2.length>2 ? ar2[2] : "";
				s2 = "update tcasmlink set ilineid=0,iasmid=0,iimageid=0,ihot=0,ipartid=0,iqty=0,ddate=sysdate where id=" + ar2[1];
			}
			else if(ar2[0].equalsIgnoreCase("E"))
			{
				s1 = ar2[8];
				s2 = "update tcasmlink set ilineid=" + ar2[2] + ",iasmid=" + ar2[3] + ",iimageid=" + ar2[4] + ",ihot=" + ar2[5] + ",ipartid=" + ar2[6] + ",iqty=" + ar2[7] + ",ddate=sysdate where id=" + ar2[1];
			}
			else if(ar2[0].equalsIgnoreCase("A"))
			{
				s1 = ar2[7];
				if(ld == null)
				{
					ld = new ArrayList();
					s = "select id from tcasmlink where ilineid=0 and iasmid=0 and ipartid=0";
					m_set = m_smt.executeQuery(s);
					while(m_set.next()) ls.add(m_set.getString(1));
					m_set.close();
				}
				if(ld.size() > 0)
				{
					s = ld.get(0).toString();
					ld.remove(0);
					s2 = "update tcasmlink set ilineid=" + ar2[1] + ",iasmid=" + ar2[2] + ",iimageid=" + ar2[3] + ",ihot=" + ar2[4] + ",ipartid=" + ar2[5] + ",iqty=" + ar2[6] + ",ddate=sysdate where id=" + s;
				}
				else
				{
					if(nID == 0)
					{
						s = "select max(id) from tcasmlink";
						m_set = m_smt.executeQuery(s);
						if(m_set.next()) nID = m_set.getLong(1);
						m_set.close();
					}
					nID++;
					s2 = "insert into tcasmlink(id,ilineid,iasmid,iimageid,ihot,ipartid,iqty,ddate) values(" + nID + "," + ar2[1] + "," + ar2[2] + "," + ar2[3] + "," + ar2[4] + "," + ar2[5] + "," + ar2[6] + ",sysdate)";
				}
			}
			else continue;
			ar2 = null;
			ret = m_smt.executeUpdate(s2);
			if(!s1.isEmpty()) ls.add(s1 + (ret>0?"-Y":"-N"));
		}
		ar1 = null;
		if(ld != null) { ld.clear();	ld = null; }
		Close();
		return ls;
	}
}