package com.jl.service;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.PageContext;
import com.jl.dao.*;
import com.jl.dao.base.impl.DBASession;

public class AsmLinkService {
	private AsmLinkDba dba=null;
	public AsmLinkService(HttpServletRequest request)
	{		
		dba=new AsmLinkDba(request);
	}
	public String getXlsId() throws Exception
	{		
		return dba.getXlsId();
	}
	public String saveAsmLink(String id, String iasmid,String ipartid,String iimageid,String ihot,String iqty,String iasmimageid) throws Exception {
		return dba.saveAsmLink(id, iasmid, ipartid,  iimageid, ihot,iqty,iasmimageid);
	}
	public List getAsmLinks() throws Exception {
		return dba.getAsmLinks();
	}
	public String getAsmLinkTotal() throws Exception {
		return dba.getAsmLinkTotal();
	}
	public List getAsmLinks(String asmid) throws Exception {
		return dba.getAsmLinks(asmid);
	}
	public List getAsmLinks(String asmid,String partid) throws Exception {
		return dba.getAsmLinks(asmid,partid);
	}
	public List getChildAsmLinks(String asmid) throws Exception {
		return dba.getChildAsmLinks(asmid);
	}
	public String delAsmLink(String ids) throws Exception {
		return dba.delAsmLink(ids);
	}
	public List getAvailAsmLinks() throws Exception {
		return dba.getAvailAsmLinks();
	}
	public List getIImageList() throws Exception {
		return dba.getIImageList();
	}
	public List getHotList(String imageid) throws Exception {
		return dba.getHotList(imageid);
	}
	public List getPartList(String asmid) throws Exception {
		return dba.getPartList(asmid);
	}
	public List getPartListForEdit(String asmid) throws Exception {
		return dba.getPartListForEdit(asmid);
	}
	public List getAsmLinksById(String id) throws Exception {
		return dba.getAsmLinksById(id);
	}
	public List getAsmLinkByVccode(String vccode) throws Exception {
		return dba.getAsmLinkByVccode(vccode);
	}
	
	
}