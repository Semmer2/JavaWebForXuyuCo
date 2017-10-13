package com.jl.service;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.PageContext;
import com.jl.dao.*;

public class VehLinkService {
	private VehLinkDba dba=null;
	public VehLinkService(HttpServletRequest request)
	{		
		dba=new VehLinkDba(request);
	}
	public String getXlsId() throws Exception
	{		
		return dba.getXlsId();
	}
	public String saveVehLink(String id, String ivehid,String ipartid,String iimageid,String ihot,String iqty) throws Exception {
		return dba.saveVehLink(id, ivehid, ipartid,  iimageid, ihot,iqty);
	}
	public List getVehLinks() throws Exception {
		return dba.getVehLinks();
	}
	public String getVehLinkTotal() throws Exception {
		return dba.getVehLinkTotal();
	}
	public List getVehLinks(String asmid) throws Exception {
		return dba.getVehLinks(asmid);
	}
	public List getVehLinks(String asmid,String partid) throws Exception {
		return dba.getVehLinks(asmid,partid);
	}	
	public String delVehLink(String ids) throws Exception {
		return dba.delVehLink(ids);
	}
		public List getIImageList() throws Exception {
		return dba.getIImageList();
	}
	public List getHotList(String imageid) throws Exception {
		return dba.getHotList(imageid);
	}
	
	public List getVehLinksById(String id) throws Exception {
		return dba.getVehLinksById(id);
	}
	public List getVehLinkByVccode(String vccode) throws Exception {
		return dba.getVehLinkByVccode(vccode);
	}
}