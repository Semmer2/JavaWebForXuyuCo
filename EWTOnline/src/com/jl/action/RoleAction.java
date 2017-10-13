package com.jl.action;
import com.jl.service.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.PageContext;
import java.util.*;
public class RoleAction {
	private RoleService service=null;
    public RoleAction(HttpServletRequest request)
    {    	
    	service=new RoleService(request);
    }
    public String createRole(String vcroleno,String vcrolename,String bactive) throws Exception {
		return service.createRole(vcroleno, vcrolename, bactive);
	}
    public String saveRole(String id,String vcroleno,String vcrolename,String bactive) throws Exception {
		return service.saveRole(id,vcroleno, vcrolename, bactive);
	}
    public String delRole(String ids) throws Exception {
    	return service.delRole(ids);
    }
    public List getRoles() throws Exception 
    {
    	return service.getRoles();
    }
    public List getRolePriv(String roleid) throws Exception {
		return service.getRolePriv(roleid);
	}
    public String addRolePriv(String roleid,String modename) throws Exception
    {
    	return service.addRolePriv(roleid,modename);
    }
    public String deleteRolePriv(String roleid,String modename) throws Exception
    {
    	return service.deleteRolePriv(roleid,modename);
    }
}
