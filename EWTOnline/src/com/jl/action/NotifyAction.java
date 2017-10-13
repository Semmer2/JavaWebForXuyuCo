package com.jl.action;
import com.jl.service.*;
import com.jl.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.PageContext;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.*;
public class NotifyAction {
	private NotifyService service=null;
	private HttpServletRequest request=null;
    public NotifyAction(HttpServletRequest request)
    {    	
    	service=new NotifyService(request);
    	this.request=request;
    }
    public Object doing(String method) throws Exception
    {
    	Object result="";    	
    	if(method.equalsIgnoreCase("new") || method.equalsIgnoreCase("edit"))
		{    			
			String notifycontent=URLDecoder.decode(StringUtils.getParameter("hidden1",request),"UTF-8");
			notifycontent=URLDecoder.decode(notifycontent,"UTF-8");
			String notifytitle=URLDecoder.decode(StringUtils.getParameter("hidden2",request),"UTF-8");
			notifytitle=URLDecoder.decode(notifytitle,"UTF-8");
			String sendercode=URLDecoder.decode(StringUtils.getParameter("hidden3",request),"UTF-8");
			sendercode=URLDecoder.decode(sendercode,"UTF-8");
			String sendercname=URLDecoder.decode(StringUtils.getParameter("hidden4",request),"UTF-8");
			sendercname=URLDecoder.decode(sendercname,"UTF-8");
			String notifystatus=URLDecoder.decode(StringUtils.getParameter("notifystatus",request),"UTF-8");
			String status="0";
			if(notifystatus.equalsIgnoreCase("on")) status="1";
			String notifyhigh=URLDecoder.decode(StringUtils.getParameter("notifyhigh",request),"UTF-8");
			String high="0";
			if(notifyhigh.equalsIgnoreCase("on")) high="1";
			
			if(method.equalsIgnoreCase("new"))
			{
				String rtn=createNotify(notifytitle,notifycontent,sendercode,sendercname,status,high);
				if(rtn!="" && rtn!="-1")
				{
					result=getNotifyById(rtn);
					//Hashtable ht=(Hashtable)ls.get(0);
					//result=rtn+","+URLEncoder.encode(ht.get("vctitle").toString())+","+URLEncoder.encode(ht.get("vcsendname").toString())+","+ht.get("senddate").toString();
				}
				else
				{
					result="";
				}
			}
			if(method.equalsIgnoreCase("edit"))
			{
				String notifyid=URLDecoder.decode(StringUtils.getParameter("notifyid",request),"UTF-8");;
				String rtn=saveNotify(notifyid,notifytitle,notifycontent,sendercode,sendercname,status,high);
				if(rtn!="")
					result=getNotifyById(notifyid);
				else
					result="";
				//Hashtable ht=(Hashtable)ls.get(0);
				//result=rtn+","+URLEncoder.encode(ht.get("vctitle").toString())+","+URLEncoder.encode(ht.get("vcsendname").toString())+","+ht.get("senddate").toString();
			}    			
		}
		if(method.equalsIgnoreCase("query"))
		{
			String notifyid=request.getParameter("notifyid");
			result=getContent(notifyid);
			List ls=new ArrayList();
			Hashtable ht=new Hashtable();
			ht.put("content",result.toString());
			ls.add(ht);
			result=ls;
		}
		if(method.equalsIgnoreCase("delete"))
		{
			String ids=request.getParameter("ids");
			result=delNotify(ids);			
		}		
		return result;
    }
    public String createNotify(String vctitle, String vccontent, String vcsendno, String vcsendname, String istatus, String ihighshow) throws Exception {
		return service.createNotify(vctitle, vccontent, vcsendno, vcsendname, istatus, ihighshow);
	}
	public String delNotify(String ids) throws Exception {
		return service.delNotify(ids);
	}
	public String saveNotify(String id,String vctitle, String vccontent, String vcsendno, String vcsendname, String istatus, String ihighshow) throws Exception {
		return service.saveNotify(id, vctitle, vccontent, vcsendno, vcsendname, istatus, ihighshow);
	}
	public List getNotifys() throws Exception {
		return service.getNotifys();
	}
	public List getNotifys(String title) throws Exception {
		return service.getNotifys(title);
	}
	public List getNotifyById(String id) throws Exception {
		return service.getNotifyById(id);
	}
	public String getContent(String id) throws Exception {
		return service.getContent(id);
	}
}
