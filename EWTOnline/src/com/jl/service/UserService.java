package com.jl.service;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.PageContext;
import com.jl.dao.*;

public class UserService {
	private UserDba dba=null;
	public UserService(HttpServletRequest request)
	{		
		dba=new UserDba(request);
	}
	public String createUser(String vccode,String vccname,String vcename,String iflag,String iroleid,String vcpassword) throws Exception {
		return dba.createUser(vccode, vccname, vcename, iflag, iroleid, vcpassword);
	}
	public String saveUser(String id,String vccode,String vccname,String vcename,String iflag,String iroleid,String vcpassword) throws Exception {
		return dba.saveUser(id, vccode, vccname, vcename, iflag, iroleid, vcpassword);
	}
	public String delUser(String ids) throws Exception {
		return dba.delUser(ids);
	}
	public List getUsers() throws Exception {
		return dba.getUsers();
	}
	public boolean login(String username,String password,String ip) throws Exception {	
		return dba.login(username,password,ip);
	}
	public List getVisitTj() throws Exception {
		return dba.getVisitTj();
	}
	public List getUsers(String username) throws Exception {
		return dba.getUsers(username);
	}
	public List queryUsers(String username) throws Exception {
		return dba.queryUsers(username);
	}
	public List getuserpriv(String userid) throws Exception {
		return dba.getuserpriv(userid);
	}
	public String addUserPriv(String userid,String modename) throws Exception
    {
    	return dba.addUserPriv(userid,modename);
    }
    public String deleteUserPriv(String userid,String modename) throws Exception
    {
    	return dba.deleteUserPriv(userid,modename);
    }
    public List queryUserPriv(String userid) throws Exception
	{
		return dba.queryUserPriv(userid);
	}
}