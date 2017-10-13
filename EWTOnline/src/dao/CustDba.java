package dao;

import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public class CustDba extends BaseDba
{ 
	HttpServletRequest request;
	public CustDba(HttpServletRequest request)
	{
		this.request = request;
	}
		
	private String getUserid(int mode)
	{
		if(mode==0)	return (String)request.getSession().getAttribute("userid");
		if(mode==1)	return (String)request.getSession().getAttribute("userdd");
		m_s=(String)request.getSession().getAttribute("userdd");
		if(m_s!=null && m_s.isEmpty()) m_s=(String)request.getSession().getAttribute("userid");
		return m_s;
	}
	
	public int login(String username, String password) throws Exception
	{
		String ip = request.getHeader("x-forwarded-for");
		if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip))
			ip = request.getHeader("Proxy-Client-IP");
		if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip))
			ip = request.getHeader("WL-Proxy-Client-IP");
		if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip))
			ip = request.getRemoteAddr();
		
		Intial();
		int ret = -1;
		m_s = "select id,iflag,vcpassa,vcpassb,vcpassc,vcename from smuser where rownum<2 and vccode='" + username + "'";
		m_set = m_smt.executeQuery(m_s);
		if(m_set!=null && m_set.next())
		{
			int type = 0;
			if(password.equalsIgnoreCase(m_set.getString(3)))      type = 1;
			else if(password.equalsIgnoreCase(m_set.getString(4))) type = 2;
			else if(password.equalsIgnoreCase(m_set.getString(5))) type = 3;
			if(type == 0)
			{
				ret = 2;
				m_set.close();
			}
			else
			{
				ret = 0;
				String sid = m_set.getString(1);
				int flag = m_set.getInt(2);
				String sdd = "", sname = "All";
				if(flag == 3)
				{
					sdd = sid;
					sname = m_set.getString(6);
				}
				m_set.close();
				
				String d1="",d2="",d3="";
				m_s = "select d1,d2,d3 from smright where rownum<2 and id=" + ((flag-1)*3+type);
				m_set = m_smt.executeQuery(m_s);
				if(m_set!=null && m_set.next())
				{
					d1 = m_set.getString(1);
					d2 = m_set.getString(2);
					d3 = m_set.getString(3);
					m_set.close();
				}
								
				HttpSession ses = request.getSession();
				ses.setMaxInactiveInterval(4*3600);
				ses.setAttribute("userid",sid);
				ses.setAttribute("userdd",sdd);
				ses.setAttribute("username",sname);
				ses.setAttribute("usertype",Integer.toString(type));
				if(d1.isEmpty()) ses.removeAttribute("userd1"); else ses.setAttribute("userd1",d1); 
				if(d2.isEmpty()) ses.removeAttribute("userd2"); else ses.setAttribute("userd2",d2);
				if(d3.isEmpty()) ses.removeAttribute("userd3"); else ses.setAttribute("userd3",d3);
								
				m_s = "insert into smvisit(iuserid,vcip,itype,ddate) values("+sid+",'"+ip+"',"+type+",sysdate)";
				m_smt.executeUpdate(m_s);
			}
		}
		else ret = 1;
		Close(true);
		return ret;
	}
	
	public int changeUser(String id, String type) throws Exception 
	{
		String name;
		HttpSession ses = request.getSession();
		if(id.isEmpty())
		{
			name = (String)ses.getAttribute("userid");
			type = (String)ses.getAttribute("oldtype");
			if(type==null)type = (String)ses.getAttribute("usertype");
		}
		else name=id;
		int flag=0;
		m_s = "select b.d1,a.vcename from smuser a left join smright b on b.id=(a.iflag-1)*3+"+type+" where a.id="+name; 
		Intial();
		m_set = m_smt.executeQuery(m_s);
		if(m_set!=null && m_set.next())
		{
			flag = m_set.getInt(1);
			name = m_set.getString(2);
		}
		Close(true);
		if(flag==0)return 0;
		
		if(id.isEmpty())
		{
			ses.removeAttribute("oldtype");
			name = "All";
		}
		else
		{
			m_s = (String)ses.getAttribute("oldtype");
			if(m_s==null)
			{
				m_s = (String)ses.getAttribute("usertype");
				ses.setAttribute("oldtype",m_s);
			}
			m_s = (String)ses.getAttribute("userd1");
			flag = (flag&63)|(Integer.parseInt(m_s)&448);
		}
		ses.setAttribute("userdd",id);
		ses.setAttribute("usertype",type);	
		ses.setAttribute("username",name);
		ses.setAttribute("userd1",Integer.toString(flag));
		return 1;
	}
	
	public int addCart(String partid, String qty) throws Exception 
	{		
		String userid = getUserid(2);
		if(userid == null)return 0;
		m_s = "insert into tccart(ipartid,iuserid,iqty,ddate) values("+partid+","+userid+","+qty+",sysdate)";
		return Update(m_s,1);
	}
	
	public int deleteCart(String partid) throws Exception 
	{
		String userid = getUserid(2);
		if(userid == null)return 0;
		m_s = "delete from tccart where ipartid="+partid+" and iuserid="+userid;
		return Update(m_s,1);
	}
	
	public int emptyCart() throws Exception 
	{
		String userid = getUserid(2);
		if(userid==null)return 0;
		m_s="delete from tccart where iuserid="+userid;
		return Update(m_s,1);
	}
	
	public int updateCartQty(String partid, String qty) throws Exception 
	{		
		String userid = getUserid(2);
		if(userid==null)return 0;
		m_s="update tccart set iqty="+qty+" where ipartid="+partid+" and iuserid="+userid;
		return Update(m_s,1);
	}
	
	public void getQuickPart(String pn, String pd, String s) throws Exception
	{
		if(s == null) return;
		String s1 = "", s2 = "";		
		if(pn!=null && !pn.isEmpty())
		{
			s1 = "a.vccode like '%" + pn.trim().toUpperCase() + "%'";
		}
		if(pd!=null && !pd.isEmpty())
		{
			if(!s1.isEmpty())s1 += " and ";
			s1 += "a.vcename like '%" + pd.trim().toUpperCase() + "%'";
		}
		
		String[] ar1 = s.split(";");
		String[] ar2 = null;
		for(int i=0; i<ar1.length; i++)
		{
			ar2 = ar1[i].split(",");
			if(!s2.isEmpty()) s2 += " union all ";
			s2 += "select " + ar2[0] + " as m," + ar2[1] + " as p," + ar2[2] + " as h from dual";
			ar2 = null;
		}
		ar1 = null;
		if(s2.isEmpty()) return;
		
		//a.iphotoflag as d,a.newid as e,
		s = "select a.id as a,a.vccode as b,a.vcename as c,c.id as f,c.vccode as g,d.vcename as h from smpart a,(";
		s += s2;
		s+=") b,tcimage c,tct3 d where a.id=b.p and c.id=b.m and d.id=c.it3d";
		if(!s1.isEmpty()) s += " and " + s1;
		Open(s, true);
	}
	
	public int deleteSuggest(String id) throws Exception 
	{
		m_s = "delete from smsuggest where id="+id;
		return Update(m_s,1);
	}
	
	public int saveSuggest(String id,String vehid,String imageid,String content) throws Exception
	{
		if(id.equalsIgnoreCase("0"))
		{	
			String userid=getUserid(2);
			if(userid==null) return 0;
			Intial();
			m_s = "select max(id)+1 as id from smsuggest";
			m_set = m_smt.executeQuery(m_s);
			if(m_set!=null && m_set.next()) id = m_set.getString(1);
			else id = "1";
			m_set.close();
				
			m_s = "insert into smsuggest(id,iimageid,ivehid,iuserid,vccontent,ddate1) values(";
			m_s += id+","+imageid+","+vehid+","+userid+",'"+content+"',sysdate)";
		}
		else m_s = "update smsuggest set vccontent='"+content+"',ddate1=sysdate where id="+id;
		return Update(m_s,1);
	}
	
	public int setBulletinRead(String bulletinid) throws Exception 
	{
		if(bulletinid==null || bulletinid.isEmpty()) return 0;
		String userid = getUserid(0);
		if(userid == null) return 0;
		m_s = "insert into smvistbulletin(ibulletinid,iuserid) values(" + bulletinid + "," + userid + ")";
		return Update(m_s,1);
	}
	
	public List getHot(String id) throws Exception
	{
		m_s = "select ihot,ix,iy,iw,ih from tchot where iimageid="+id;
		return getData(m_s, 5, ",");
	}
	
	public int addSaleOrder(String partid, String qty) throws Exception 
	{			
		String userid=getUserid(2);
		if(userid==null) return -1;
		
		Intial();
		String id="";
		Open("select max(id)+1 as id from tcorder",true);
		if(m_set!=null && m_set.next()) id = m_set.getString(1); else  { Close(true);  return -1; }
		if(id==null || id.isEmpty())id = "1";
		Close(false);
		
		int num = -1;
		float price = 0;
		m_s = "select a.inum,a.ioff,case when c.iprice is null then b.iprice else c.iprice end as p from smsale a left join smpart b on b.id=a.ipartid left join smprice c on c.ipartid=a.ipartid and c.iuserid="+userid+" where a.ipartid="+partid;
		Open(m_s,false);
		if(m_set!=null && m_set.next()){num = m_set.getInt(1); price = m_set.getFloat(2)*m_set.getFloat(3); } else  { Close(true);  return -1; }
		Close(false);
		if(num<0)return -2;
		if(Integer.parseInt(qty)>num)return -3;
		
		m_s = "insert into tcorder(id,ipartid,iuserid,iqty,iprice,istate,ddate) values("+id+","+partid+","+userid+","+qty+","+price+",1,sysdate)";
		return Update(m_s,2);
	}
	
	public int updateOrderQty1(String id, String qty) throws Exception 
	{		
		m_s="update tcorder set iqty="+qty+" where istate=1 and id="+id;
		return Update(m_s,1);
	}
	
	public int updateOrderQty2(String id, String qty) throws Exception 
	{				
		int q = Integer.parseInt(qty);
		if(q<1)return -2;
		
		Intial();
		String id2;		int num;
		Open("select b.id,b.inum from tcorder a left join smsale b on b.ipartid=a.ipartid where a.id="+id,true);
		if(m_set!=null && m_set.next())
		{
			id2 = m_set.getString(1);
			num = m_set.getInt(2);
		}
		else  { Close(true);  return -1; }		
		Close(false);
		if(q>num) { Close(true);  return -3; }
		
		m_s="update tcorder set istate=2,iqty="+qty+" where id="+id;
		int ret=m_smt.executeUpdate(m_s);
		if(ret<=0) { Close(true);  return -4; }
		
		m_s="update smsale set inum="+(num-q)+" where id="+id2;
		ret=m_smt.executeUpdate(m_s);
		Close(true);
		if(ret<=0) return -5;
		
		return num-q;
	}
	
	public int deleteOrder(String id) throws Exception 
	{
		m_s = "delete from tcorder where istate=1 and id="+id;
		return Update(m_s,1);
	}
}