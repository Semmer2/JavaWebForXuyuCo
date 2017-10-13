package com.jl.action;
import com.jl.service.*;
import com.jl.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.PageContext;

import java.net.URLDecoder;
import java.util.*;
public class UserAction {
	private UserService service=null;	
    public UserAction(HttpServletRequest request)
    {    	
    	service=new UserService(request);
    }
    public Object doing(String method) throws Exception
    {
    	Object result="";    	
		if(method.equalsIgnoreCase("query"))
		{			
			try {
				List ls=getUsers();
				List htlist=new ArrayList();
				Hashtable photoht=new Hashtable();
				for(int i=0;i<ls.size();i++)
				{
					Hashtable ht=(Hashtable)ls.get(i);
					//vccode, vccname
					String vccode=ht.get("vccode").toString();
					String vccname=ht.get("vccname").toString();
					photoht.put("key"+String.valueOf(i)+"_code",vccode);
					photoht.put("key"+String.valueOf(i)+"_name",vccname);
				}
				photoht.put("totalcount",String.valueOf(ls.size()));
				htlist.add(photoht);
				result=htlist;
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return result;
    }
    public String createUser(String vccode,String vccname,String vcename,String iflag,String iroleid,String vcpassword) throws Exception {
		return service.createUser(vccode, vccname, vcename, iflag, iroleid, vcpassword);
	}
	public String saveUser(String id,String vccode,String vccname,String vcename,String iflag,String iroleid,String vcpassword) throws Exception {
		return service.saveUser(id, vccode, vccname, vcename, iflag, iroleid, vcpassword);
	}
	public String delUser(String ids) throws Exception {
		return service.delUser(ids);
	}
	public List getUsers() throws Exception {
		return service.getUsers();
	}
	public boolean login(String username,String password,String ip) throws Exception 
	{
		return service.login(username,password,ip);
	}
	public List getVisitTj() throws Exception {
		return service.getVisitTj();
	}
	public List getUsers(String username) throws Exception {
		return service.getUsers(username);
	}
	public List queryUsers(String username) throws Exception {
		return service.queryUsers(username);
	}
	public List getuserpriv(String userid) throws Exception {
		return service.getuserpriv(userid);
	}
	public String addUserPriv(String userid,String modename) throws Exception
    {
    	return service.addUserPriv(userid,modename);
    }
    public String deleteUserPriv(String userid,String modename) throws Exception
    {
    	return service.deleteUserPriv(userid,modename);
    }
    public List queryUserPriv(String userid) throws Exception
	{
		return service.queryUserPriv(userid);
	}
}