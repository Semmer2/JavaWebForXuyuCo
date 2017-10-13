package com.jl.service;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.PageContext;
import com.jl.dao.*;

public class PartPhotoService {
	private PartPhotoDba dba=null;
	public PartPhotoService(HttpServletRequest request)
	{		
		dba=new PartPhotoDba(request);
	}
	public String createSmPartPhoto(String smpartid) throws Exception {
		return dba.createSmPartPhoto(smpartid);
	}
	public String delSmPartPhoto(String photoid) throws Exception {
		return dba.delSmPartPhoto(photoid);
	}
	public List getSmPartPhoto(String smpartid) throws Exception
	{
		return dba.getSmPartPhoto(smpartid);
	}
	public List getSmPartPhotoByPartVccode(String partvccode) throws Exception {
		return dba.getSmPartPhotoByPartVccode(partvccode);
	}
}
