package dao;

import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.sql.ResultSet;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

public class BaseDba 
{
	Connection m_con;
	public Statement m_smt;
	public ResultSet m_set;
	String m_s;
	public BaseDba()
	{
		m_con = null;
		m_smt = null;
		m_set = null;
		m_s = null;
	}
	
	public void Intial() throws Exception
	{
		if(m_con != null) return;
		Class.forName("oracle.jdbc.driver.OracleDriver");
		m_con = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:DBSVRCAR","jlcar","jlcar");
		m_con.setAutoCommit(false);
		m_smt = m_con.createStatement();
		m_set = null;
	}
	
	public void Close(boolean bAll) throws Exception
	{
		if(m_set != null) { m_set.close();		m_set = null; }
		if(bAll)
		{
			if(m_smt != null) { m_smt.close();		m_smt = null; }
			if(m_con != null) { m_con.close();		m_con = null; }
		}		
	}
	
	public void Open(String s) throws Exception
	{
		if(m_smt == null) Intial();
		else if(m_set != null) { m_set.close();	m_set = null; }
		try	{ m_set = m_smt.executeQuery(s); }
		catch (Exception e){ throw e; }
	}
	
	public void Open(String s, boolean bIntial) throws Exception
	{
		if(bIntial) Intial();
		else if(m_set != null) { m_set.close();	m_set = null; }
		try	{ m_set = m_smt.executeQuery(s); }
		catch (Exception e){ throw e; }
	}
	
	public String getString(int col) throws Exception
	{
		return m_set.getString(col);
	}
	
	public int getInt(int col) throws Exception
	{
		return m_set.getInt(col);
	}
	
	public String getString(int col, int mode) throws Exception
	{
		if(mode==0)return m_set.getString(col);
		m_s = m_set.getString(col);
		if(mode==1) return m_s==null ? "" : m_s;
		if(mode==2)
		{
			if(m_s==null)return "&nbsp;";
			m_s.trim();
			return m_s.isEmpty() ? "&nbsp;" : m_s;
		}
		return m_s;
	}
	
	public boolean isValid() throws Exception
	{
		return m_set != null;
	}
	
	public boolean Next() throws Exception
	{
		return m_set.next();
	}
	
	public boolean Prev() throws Exception
	{
		return m_set.previous();
	}
	
	public int Update(String s, int mode) throws Exception
	{
		if(mode<=1) Intial();
		int ret=m_smt.executeUpdate(s);
		if(mode>=1) Close(true);
		return ret>0?1:0;
	}
		
	public List getData(String s, int n, String sp) throws Exception
	{
		List ls = new ArrayList();
		try
		{
			Intial();			
			m_set = m_smt.executeQuery(s);
			if(n == 0) n = m_set.getMetaData().getColumnCount();
			int i;
			while(m_set.next())
			{
				s = "";
				for(i=1; i<=n; i++) { if(i>1)s += sp;	s += m_set.getString(i); }
				ls.add(s);
			}
			Close(true);
		}
		catch (Exception e){ throw e; }
		return ls;
	}
	
	public List getDataEx(String s, int n) throws Exception
	{
		List ls = new ArrayList();
		try
		{
			Intial();
			m_set = m_smt.executeQuery(s);
			ResultSetMetaData md = m_set.getMetaData();
			if(n == 0) n = md.getColumnCount();
			int i;
			String[] name = new String[n];
			for(i=0; i<n; i++) name[i] = md.getColumnName(i+1);
			Hashtable h = null;
			while(m_set.next())
			{
				h = new Hashtable();
				for(i=1; i<=n; i++)
				{
					s = m_set.getString(i);
					if(s==null || s.isEmpty()) s = "0";
					h.put(name[i], s);
				}
				ls.add(h);
				h = null;
			}
			name = null;
			Close(true);
		}
		catch (Exception e){ throw e; }
		return ls;
	}
	
	public boolean isNumber(String str)
	{
		char ch;
		for(int i=str.length();--i>=0;)
		{
		   ch = str.charAt(i);
		   if(ch<48 || ch>57) return false;
		}
		return true;
	}
	
	public String getVehID(String code, boolean bClose) throws Exception
	{
		if(code.isEmpty()) return "0";
		int k = code.indexOf("/");
		if(k > 0) m_s="select a.id from tcveh a left join tcmodel b on b.id=a.imodelid where rownum<2 and b.vccode='" + code.substring(0,k).trim() + "' and a.vccode='" + code.substring(k+1).trim() + "'";
		else if(code.length() >= 18) m_s = "select id from tcveh where rownum<2 and vccode='" + code + "'";
		else return "0";
		Open(m_s);
		if(m_set != null && m_set.next()) m_s = m_set.getString(1);
		else m_s = "0";
		Close(bClose);
		return m_s;
	}
	
	public String getID(String mode, String code, boolean bClose) throws Exception
	{
		m_s = "select id from " + mode + " where rownum<2 and vccode='" + code.replaceAll("'", "") + "'";
		Open(m_s);
		if(m_set != null && m_set.next()) m_s = m_set.getString(1);
		else m_s = "0";
		Close(bClose);
		return m_s;
	}
}