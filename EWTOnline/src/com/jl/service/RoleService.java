package com.jl.service;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.PageContext;
import com.jl.dao.*;

public class RoleService {
	private RoleDba dba=null;
	public RoleService(HttpServletRequest request)
	{		
		dba=new RoleDba(request);
	}
	public String createRole(String vcroleno,String vcrolename,String bactive) throws Exception {
		return dba.createRole(vcroleno, vcrolename, bactive);
	}
	public String saveRole(String id,String vcroleno,String vcrolename,String bactive) throws Exception {
		return dba.saveRole(id,vcroleno, vcrolename, bactive);
	}
	public String delRole(String ids) throws Exception {
		return dba.delRole(ids);
	}
	public List getRoles() throws Exception {
		return dba.getRoles();
	}
	public List getRolePriv(String roleid) throws Exception {
		return dba.getRolePriv(roleid);
	}
	public String addRolePriv(String roleid,String modename) throws Exception
    {
    	return dba.addRolePriv(roleid,modename);
    }
	public String deleteRolePriv(String roleid,String modename) throws Exception
    {
		return dba.deleteRolePriv(roleid,modename);
    }
}
