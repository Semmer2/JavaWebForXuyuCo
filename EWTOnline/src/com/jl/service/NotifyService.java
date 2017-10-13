package com.jl.service;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.PageContext;
import com.jl.dao.*;

public class NotifyService {
	private NotifyDba dba=null;
	public NotifyService(HttpServletRequest request)
	{		
		dba=new NotifyDba(request);
	}
	public String createNotify(String vctitle, String vccontent, String vcsendno, String vcsendname, String istatus, String ihighshow) throws Exception {
		return dba.createNotify(vctitle, vccontent, vcsendno, vcsendname, istatus, ihighshow);
	}
	public String delNotify(String ids) throws Exception {
		return dba.delNotify(ids);
	}
	public String saveNotify(String id,String vctitle, String vccontent, String vcsendno, String vcsendname, String istatus, String ihighshow) throws Exception {
		return dba.saveNotify(id, vctitle, vccontent, vcsendno, vcsendname, istatus, ihighshow);
	}
	public List getNotifys() throws Exception {
		return dba.getNotifys();
	}
	public List getNotifys(String title) throws Exception {
		return dba.getNotifys(title);
	}
	public List getNotifyById(String id) throws Exception {
		return dba.getNotifyById(id);
	}
	public String getContent(String id) throws Exception {
		return dba.getContent(id);
	}
}