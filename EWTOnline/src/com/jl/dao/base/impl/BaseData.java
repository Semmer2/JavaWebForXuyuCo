package com.jl.dao.base.impl;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import java.net.*;
import java.util.zip.Deflater;
import java.util.zip.Inflater;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.PageContext;

public class BaseData extends BaseMVCDAO
{
	public HttpServletRequest request;
	
	public BaseData(HttpServletRequest request)
	{
		super(request);
		this.request=request;
	}
	
	public boolean runSql(String sql,List ls) throws Exception
	{		
		boolean ret=false;
		DBASession db=getSession();
		Object ob=null;
		try
		{		
			ob=db.startlongTran();
			if(ls==null) ret=db.runSql(sql.toString());
			else		 ret=db.runSql(sql.toString(),ls.toArray());
			db.endlongTran(ob);
		} 
		catch (Exception e){ db.rollbacklongTran(ob); throw e; }
		finally	{ db.close(); db=null; }
		return ret;
	}
	
	public boolean runSql(DBASession db,String sql,List ls) throws Exception
	{		
		boolean ret=false;
		Object ob=null;
		try
		{		
			ob=db.startlongTran();
			if(ls==null) ret=db.runSql(sql.toString());
			else		 ret=db.runSql(sql.toString(),ls.toArray());
			db.endlongTran(ob);
		} 
		catch (Exception e){ db.rollbacklongTran(ob); throw e; }
		finally	{ db.close(); }
		return ret;
	}
	
	public List getData(String sql,List ls) throws Exception
	{
		List ret=null;
		DBASession db = getSession();
		try
		{ 
			if(ls==null) ret=db.openSelectbyList(sql.toString());
			else		 ret=db.openSelectbyList(sql.toString(),ls.toArray());
		}
		catch (Exception e){ throw e; }
		finally	{ db.close(); db = null; }
		return ret;
	}
	
	public List getData(DBASession db,String sql,List ls) throws Exception
	{
		List ret=null;
		try
		{ 
			if(ls==null) ret=db.openSelectbyList(sql.toString());
			else		 ret=db.openSelectbyList(sql.toString(),ls.toArray());
		}
		catch (Exception e){ throw e; }
		finally	{ db.close(); }
		return ret;
	}
	
	public List getData(StringBuffer sql,List ls) throws Exception
	{
		List ret=null;
		DBASession db=getSession();
		try
		{ 
			if(ls==null) ret=db.openSelectbyList(sql.toString());
			else		 ret=db.openSelectbyList(sql.toString(),ls.toArray());
		}
		catch (Exception e){ throw e; }
		finally	{ db.close(); db = null; }
		return ret;
	}
	
	public String getValue(List rows,int rowindex,String paramename)
	{
		String ret="";
		if(rows!=null && rowindex<rows.size())
		{
			Hashtable ht=(Hashtable)rows.get(rowindex);
			ret=ht.get(paramename).toString();
			ht=null;
		}
		return ret;
	}
	
	public int getCount(String tablename) throws Exception
	{
		String s = "select count(*) as n from " + tablename;
		DBASession db = getSession();
		List ls = null;
		try{ ls = db.openSelectbyList(s.toString());}
		catch (Exception e){ throw e; }
		finally	{ db.close(); db = null; }
		int n = 0;
		if(ls.size() > 0)
		{
			Hashtable ht = (Hashtable)ls.get(0);
			n =  Integer.parseInt(ht.get("n").toString());
			ht = null;
		}
		ls.clear();		ls = null;
		return n;
	}
	
	public boolean isExistEx(String tablename, String filter) throws Exception
	{		
		String s = "select rownum from " + tablename + " where rownum<2 and " + filter;
		List ls =  getData(s,null);
		if(ls == null) return false;
		int n = ls.size();
		ls.clear();		ls = null;
		return n > 0;
	}
	
	public String getValue(String tablename,String aimname, int mode, String filtername, String filterval) throws Exception
	{
		String s="select "+aimname;
		s+=" as a";
		s+=" from "+tablename;
		if(mode>0)s+=" where rownum<2 and "+filtername+"=";
		if(mode==1)s+=filterval;
		else if(mode==2) s+="'"+filterval.replaceAll("'", "")+"'";
		List ls = null;
		DBASession db=getSession();
		try{ ls=db.openSelectbyList(s.toString());}
		catch (Exception e){ throw e; }
		finally	{ db.close(); db = null; }
		if(ls.size()<1) return null;
		Hashtable ht = (Hashtable)ls.get(0);
		ls = null;
		s = ht.get("a").toString();
		ht = null;
		return s;
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
	
   public String toXml(List ls,String names)
   {
	  StringBuffer xml=new StringBuffer("<?xml version=\"1.0\" encoding=\"gbk\"?>");
	  String[] fields=names.split(",");
	  xml.append("<data><cells>");
	  xml.append(names);
	  xml.append("</cells>");
	  xml.append("<rows>");
	  if(ls!=null)
	  {
		  for(int i=0;i<ls.size();i++)
		  {
				if(ls.get(i) instanceof Hashtable)
				{
					Hashtable rows=(Hashtable)ls.get(i);			
					xml.append("<row>");			
					for(int j=0;j<fields.length;j++)
					{
						xml.append("<");
						xml.append(fields[j]);
						xml.append(">");
						//xml.append(StringUtils.escape(rows[j].toString().trim()));
						if(rows.get(fields[j])!=null)
							xml.append(rows.get(fields[j]).toString().trim());
						else
							xml.append("");
						xml.append("</");
						xml.append(fields[j]);
						xml.append(">");
					}
					xml.append("</row>");
					rows=null;
				}
				else
				{
				    if(ls.get(i) instanceof Object[])
				    {
						Object[] rows=(Object[])ls.get(i);			
						xml.append("<row>");			
						for(int j=0;j<fields.length;j++)
						{
							xml.append("<");
							xml.append(fields[j]);
							xml.append(">");
							//xml.append(StringUtils.escape(rows[j].toString().trim()));
							if(rows[j]!=null)
								xml.append(rows[j].toString().trim());
							else
								xml.append("");
							xml.append("</");
							xml.append(fields[j]);
							xml.append(">");
						}
						xml.append("</row>");
						rows=null;
				    }
				}
				
		  }
	  }
	  xml.append("</rows></data>");
	  fields=null;
	  ls.clear();
	  ls=null;
	  //System.out.println(xml);
	  return xml.toString();
   }
   
   protected String toXml(String returnvalue,String statusdesc)
   {
	  StringBuffer xml=new StringBuffer("<?xml version=\"1.0\" encoding=\"gbk\"?>");
	  xml.append("<data><cells>");
	  xml.append("returnvalue,statusdesc");
	  xml.append("</cells>");
	  xml.append("<rows>");
	  xml.append("<row>");
	  xml.append("<returnvalue>");
	  //xml.append(StringUtils.escape(returnvalue));
	  xml.append(returnvalue);
	  xml.append("</returnvalue>");
	  xml.append("<statusdesc>");
	  //xml.append(StringUtils.escape(statusdesc));
	  xml.append(statusdesc);
	  xml.append("</statusdesc>");
	  xml.append("</row>");
	  xml.append("</rows></data>");
	  //System.out.println(xml);
	  return xml.toString();
   }
   
   protected String toXml(String returnvalue)
   {
	  StringBuffer xml=new StringBuffer("<?xml version=\"1.0\" encoding=\"gbk\"?>");
	  xml.append("<data><cells>");
	  xml.append("returnvalue");
	  xml.append("</cells>");
	  xml.append("<rows>");
	  xml.append("<row>");
	  xml.append("<returnvalue>");
	  //xml.append(StringUtils.escape(returnvalue));
	  xml.append(returnvalue);
	  xml.append("</returnvalue>");
	  xml.append("</row>");
	  xml.append("</rows></data>");
	  //System.out.println(xml);
	  return xml.toString();
   }
   
   public String getParentPath(String fullname)
   {
   		String parentpath="";
		String[] s=fullname.split("/");
		for(int i=0;i<s.length-1;i++)
		{
			if(i<s.length-2)
			{
				parentpath=parentpath+s[i]+"/";
			}
			else
			{
				parentpath=parentpath+s[i];
			}
		}
		s=null;
		return parentpath;
   	
   }
   
    public final String ENCODING = "ISO-8859-1";
	public static final byte[] compresszip(String str)
	{
		if(str == null)return null;

		byte[] compressed;
		ByteArrayOutputStream out = null;
		ZipOutputStream zout = null;

		try
		{
			out = new ByteArrayOutputStream();
			zout = new ZipOutputStream(out);
			zout.putNextEntry(new ZipEntry("0"));
			zout.write(str.getBytes());
			zout.closeEntry();
			compressed = out.toByteArray();
		}
		catch (IOException e)
		{
			compressed = null;
		}
		finally
		{
			if(zout != null)
			{
				try
				{
					zout.close();
				}
				catch (IOException e)
				{
				}
			}
		}
		if (out != null)
		{
			try
			{
				out.close();
			}
			catch (IOException e)
			{
			}
		}
		return compressed;
	}

	public static final String decompresszip(byte[] compressed)
	{
		if(compressed == null)	return null;

		ByteArrayOutputStream out = null;
		ByteArrayInputStream in = null;
		ZipInputStream zin = null;
		String decompressed;
		try {
			out = new ByteArrayOutputStream();
			in = new ByteArrayInputStream(compressed);
			zin = new ZipInputStream(in);
			ZipEntry entry = zin.getNextEntry();
			byte[] buffer = new byte[1024];
			int offset = -1;
			while ((offset = zin.read(buffer)) != -1) {
				out.write(buffer, 0, offset);
			}
			decompressed = out.toString();
		} catch (IOException e) {
			decompressed = null;
		} finally {
			if (zin != null) {
				try {
					zin.close();
				} catch (IOException e) {
				}
			}
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
				}
			}
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
				}
			}
		}

		return decompressed;
	}
	/**
	 * * ѹ���ַ� * @param data ��Ҫѹ�����ַ� * @return ����ѹ������ַ� 
	 */
	public byte[] compressgzip(String data) throws IOException {		
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		GZIPOutputStream gzip = new GZIPOutputStream(out);
		gzip.write(data.getBytes());
		gzip.finish();
		gzip.flush();
		gzip.close();
		return out.toByteArray();
	}
	
	public static byte[] compresszlib(byte[] data) {   
       byte[] output = new byte[0];   
 
       Deflater compresser = new Deflater();   
 
       compresser.reset();   
       compresser.setInput(data);   
       compresser.finish();   
       ByteArrayOutputStream bos = new ByteArrayOutputStream(data.length);   
       try {   
           byte[] buf = new byte[1024];   
           while (!compresser.finished()) {   
               int i = compresser.deflate(buf);   
               bos.write(buf, 0, i);   
           }   
           output = bos.toByteArray();   
       } catch (Exception e) {   
           output = data;   
           e.printStackTrace();   
       } finally {   
           try {   
               bos.close();   
           } catch (IOException e) {   
               e.printStackTrace();   
           }   
       }   
       compresser.end();   
       return output;   
   }
	
	public static byte[] decompress(byte[] data) {   
       byte[] output = new byte[0];   
 
       Inflater decompresser = new Inflater();   
       decompresser.reset();   
       decompresser.setInput(data);   
 
       ByteArrayOutputStream o = new ByteArrayOutputStream(data.length);   
       try {   
           byte[] buf = new byte[1024];   
           while (!decompresser.finished()) {   
               int i = decompresser.inflate(buf);   
               o.write(buf, 0, i);   
           }   
           output = o.toByteArray();   
       } catch (Exception e) {   
           output = data;   
           e.printStackTrace();   
       } finally {   
           try {   
               o.close();   
           } catch (IOException e) {   
               e.printStackTrace();   
           }   
       }   
 
       decompresser.end();   
       return output;   
   }   

	public String uncompressgzip(byte[] bytes) throws IOException {		
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		ByteArrayInputStream in = new ByteArrayInputStream(bytes);
		GZIPInputStream gunzip = new GZIPInputStream(in);
		byte[] buffer = new byte[256];
		int n;
		while ((n = gunzip.read(buffer)) >= 0) {
			out.write(buffer, 0, n);
		}// toString()ʹ��ƽ̨Ĭ�ϱ��룬Ҳ������ʽ��ָ����toString("GBK";)
		return out.toString();
		// }
	}
   public String getLocalName(String fullname)
   {
		String[] s=fullname.split("/");
		String parentpath="";
		for(int i=0;i<s.length-1;i++)
		{
			if(i<s.length-2)
			{
				parentpath=parentpath+s[i]+"/";
			}
			else
			{
				parentpath=parentpath+s[i];
			}
		}
		String localname=s[s.length-1];
		s=null;
		return localname;
   }
}