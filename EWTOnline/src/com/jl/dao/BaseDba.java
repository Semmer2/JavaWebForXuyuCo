package com.jl.dao;
import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

public class BaseDba //真正的构造函数没有写
{
	Connection m_con;
	public Statement m_smt;
	public ResultSet m_set;
	public BaseDba()
	{
		m_con = null;
		m_smt = null;
		m_set = null;
	}
	
	public void IntialDba() throws Exception
	{
		if(m_con != null) return;
		Class.forName("oracle.jdbc.driver.OracleDriver");
		m_con = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:DBSVRCAR","jlcar","jlcar");
		m_con.setAutoCommit(false);
		m_smt = m_con.createStatement();
		m_set = null;
	}
	
	public void CloseDba() throws Exception
	{
		if(m_set != null) { m_set.close();		m_set = null; }
		if(m_smt != null) { m_smt.close();		m_smt = null; }
		if(m_con != null) { m_con.close();		m_con = null; }
	}
	
	public List getData(String s, int n, String sp) throws Exception
	{
		List ls = new ArrayList();
		try
		{
			IntialDba();			
			m_set = m_smt.executeQuery(s);
			if(n == 0) n = m_set.getMetaData().getColumnCount();
			int i;
			while(m_set.next())
			{
				s = "";
				for(i=1; i<=n; i++) { if(i>1)s += sp;	s += m_set.getString(i); }
				ls.add(s);
			}
			CloseDba();
		}
		catch (Exception e){ throw e; }
		return ls;
	}
	
	public List getDataEx(String s, int n) throws Exception
	{
		List ls = new ArrayList();
		try
		{
			IntialDba();
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
			CloseDba();
		}
		catch (Exception e){ throw e; }
		return ls;
	}
	
	public String getVehItem(int mode, String id)
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
}