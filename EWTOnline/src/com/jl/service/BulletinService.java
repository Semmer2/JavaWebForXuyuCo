package com.jl.service;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.PageContext;
import com.jl.dao.*;

public class BulletinService {
	private BulletinDba dba=null;
	public BulletinService(HttpServletRequest request)
	{		
		dba=new BulletinDba(request);
	}
	public String createBulletin(String topic,String id) throws Exception {
		return dba.createBulletin(topic,id);
	}
	public String getNewBulletinId() throws Exception
	{
		return dba.getNewBulletinId();
	}
	public String delBulletin(String ids) throws Exception {
		return dba.delBulletin(ids);
	}
	public String publishBulletin(String id) throws Exception {
		return dba.publishBulletin(id);
	}
	
	public String saveBulletin(String id,String topic) throws Exception {
		return dba.saveBulletin(id, topic);
	}
	public List getBulletins() throws Exception {
		return dba.getBulletins();
	}
	public List getBulletinsforUser() throws Exception {
		return dba.getBulletinsforUser();	
	}
	public List getBulletinById(String id) throws Exception {
		return dba.getBulletinById(id);
	}
}