package dao;

import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import dao.UserBean;

public class UserDba extends BaseDba
{
	HttpServletRequest request;
	public UserDba(HttpServletRequest request)
	{
		this.request=request;
	}
	
	public void queryAsmLink(String asmid, String line, String imageid, String partid) throws Exception
	{
		UserBean d = new UserBean();
		d.append1("lk.iasmid", asmid, 1);
		d.append1("lk.isn", line, 1);
		d.append1("lk.iimageid", imageid, 1);
		d.append1("lk.ipartid", partid, 1);
		if(d.isEmpty()) return;
		
		m_s = "select lk.id A,lk.isn B,lk.ipartid C,lk.iimageid D,case when lk.iqty=255 then 'X' else to_char(lk.iqty) end E,case when lk.ihot=0 then '' else to_char(lk.ihot) end F,p1.vccname G,m.vccode H,p2.vccode I,p2.vccname J,p1.vccode K,t3.vccname L,lk.iasmid M";
		m_s += " from tcasmlink lk left join smpart p1 on p1.id=lk.iasmid left join tcimage m on m.id=lk.iimageid left join smpart p2 on p2.id=lk.ipartid left join tct3 t3 on t3.id=m.it3d";
		m_s += " where " + d.result();
		m_s += " order by lk.isn,lk.iasmid,lk.iimageid,lk.ipartid";
		Open(m_s, true);
	}

	public int saveItem(int mode, String id, String sd[], int num) throws Exception
	{
		boolean bNew = id.isEmpty();
		UserBean d = new UserBean();
		Intial();
		if(mode==1) // asmlink
    	{
			if(sd[0]!=null) d.append2("isn",sd[0],true,bNew);
			if(sd[1]!=null)
			{
				Open("select id from smpart where rownum<2 and vccode='"+sd[1]+"'",false);
				if(m_set!=null && m_set.next()) sd[1] = m_set.getString(1);
				else { Close(true); d=null; return 1; }
				d.append2("iasmid",sd[1],true,bNew);
			}
			if(sd[2]!=null)
			{
				if(!sd[2].isEmpty())
				{
					Open("select id from tcimage where rownum<2 and vccode='"+sd[2]+"'",false);
					if(m_set!=null && m_set.next()) sd[2] = m_set.getString(1);
					else { Close(true); d=null; return 2; }
				}
				else sd[2] = "0";
				d.append2("iimageid",sd[2],true,bNew);
			}
			if(sd[3]!=null)
			{
				if(sd[3].isEmpty()) sd[3] = "0";
				d.append2("ihot",sd[3],true,bNew);
			}
			if(sd[4]!=null)
			{
				Open("select id from smpart where rownum<2 and vccode='"+sd[4]+"'",false);
				if(m_set!=null && m_set.next()) sd[4] = m_set.getString(1);
				else { Close(true); d=null; return 4; }
				d.append2("ipartid",sd[4],true,bNew);
			}
			if(sd[5]!=null)
			{
				if(sd[5].isEmpty()) sd[5] = "0";
				d.append2("iqty",sd[5],true,bNew);
			}
			if(bNew || !d.isEmpty()) d.append2("ddate","sysdate",true,bNew);
			m_s = "tcasmlink";
    	}
		else if(mode>=101 && mode<=103) //t1,t2,t3
    	{
			if(sd[0]!=null) d.append2("iseq",sd[0],true,bNew);
			if(sd[1]!=null) d.append2("vcename",sd[1],false,bNew);
			if(sd[2]!=null) d.append2("vccname",sd[2],false,bNew);
			if(bNew || !d.isEmpty()) d.append2("ddate","sysdate",true,bNew);
			m_s = "tct" + (mode-100);
    	}
		else if(mode==2) // image
    	{
			if(sd[0]!=null) d.append2("vccode",sd[0],false,bNew);
			if(sd[1]!=null) d.append2("vcseq",sd[1],false,bNew);
			if(sd[2]!=null) d.append2("it1d",sd[2],true,bNew);
			if(sd[3]!=null) d.append2("it2d",sd[3],true,bNew);
			if(sd[4]!=null) d.append2("it3d",sd[4],true,bNew);
			if(sd[5]!=null) d.append2("vcnote",sd[5],false,bNew);
			if(bNew || !d.isEmpty()) d.append2("ddate","sysdate",true,bNew);
			m_s = "tcimage";
    	}
		else if(mode==3) // vehlink
    	{
			if(sd[0]!=null) d.append2("isn",sd[0],true,bNew);
			if(sd[1]!=null)
			{
				sd[1]=getVehID(sd[1],false);
				if(sd[1].equalsIgnoreCase("0")) { Close(true); d=null; return 1; }
				d.append2("ivehid",sd[1],true,bNew);
			}
			if(sd[2]!=null)
			{
				if(!sd[2].isEmpty())
				{
					Open("select id from tcimage where rownum<2 and vccode='"+sd[2]+"'",false);
					if(m_set!=null && m_set.next()) sd[2] = m_set.getString(1);
					else { Close(true); d=null; return 2; }
				}
				else sd[2] = "0";
				d.append2("iimageid",sd[2],true,bNew);
			}
			if(sd[3]!=null)
			{
				if(sd[3].isEmpty()) sd[3] = "0";
				d.append2("ihot",sd[3],true,bNew);
			}
			if(sd[4]!=null)
			{
				Open("select id from smpart where rownum<2 and vccode='"+sd[4]+"'",false);
				if(m_set!=null && m_set.next()) sd[4] = m_set.getString(1);
				else { Close(true); d=null; return 4; }
				d.append2("ipartid",sd[4],true,bNew);
			}
			if(sd[5]!=null)
			{
				if(sd[5].isEmpty()) sd[5] = "0";
				d.append2("iqty",sd[5],true,bNew);
			}
			if(bNew || !d.isEmpty()) d.append2("ddate","sysdate",true,bNew);
			m_s = "tcvehlink"; 
    	}
		else if(mode==4) // Hot
    	{
			if(sd[0]!=null) d.append2("ihot",sd[0],true,bNew);
			if(sd[1]!=null) d.append2("ix",sd[1],true,bNew);
			if(sd[2]!=null) d.append2("iy",sd[2],true,bNew);
			if(sd[3]!=null) d.append2("iw",sd[3],true,bNew);
			if(sd[4]!=null) d.append2("ih",sd[4],true,bNew);
			if(sd[5]!=null) d.append2("iimageid",sd[5],true,bNew);
			if(bNew || !d.isEmpty()) d.append2("ddate","sysdate",true,bNew);
			m_s = "tchot";
    	}
		else if(mode==5) // user
    	{
			if(sd[0]!=null) d.append2("vccode",sd[0],false,bNew);
			if(sd[1]!=null) d.append2("vcename",sd[1],false,bNew);
			if(sd[2]!=null) d.append2("vccname",sd[2],false,bNew);
			if(sd[3]!=null) d.append2("vcdh",sd[3],false,bNew);
			if(sd[4]!=null) d.append2("iflag",sd[4],true,bNew);
			if(bNew || !d.isEmpty()) d.append2("ddate","sysdate",true,bNew);
			if(sd[5]!=null) d.append2("vcpassa",sd[5],false,bNew);
			if(sd[6]!=null) d.append2("vcpassb",sd[6],false,bNew);
			if(sd[7]!=null) d.append2("vcpassc",sd[7],false,bNew);
			if(sd[5]!=null || sd[6]!=null || sd[7]!=null)d.append2("pdate","sysdate",true,bNew);
			m_s = "smuser";
    	}
		else if(mode==6) // bulletin
    	{
			if(sd[0]!=null) d.append2("vctopic",sd[0],false,bNew);
			if(sd[1]!=null) d.append2("ipublish",sd[1],true,bNew);
			if(bNew || !d.isEmpty()) d.append2("ddate","sysdate",true,bNew);
			if(sd[2]!=null) d.append2("itop",sd[2],true,bNew);
			if(sd[3]!=null) d.append2("ired",sd[3],true,bNew);
			m_s = "smbulletin";
    	}
		else if(mode==7) // wall
    	{
			if(sd[0]!=null) d.append2("iflag",sd[0],true,bNew);
			if(bNew) d.append2("ddate","sysdate",true,bNew);
			m_s = "tcwall";
    	}
		else if(mode==8) // part
    	{
			if(sd[0]!=null) d.append2("vccode",sd[0],false,bNew);
			if(sd[1]!=null) d.append2("vccname",sd[1],false,bNew);
			if(sd[2]!=null) d.append2("vcename",sd[2],false,bNew);
			if(sd[3]!=null) d.append2("vcnote",sd[3],false,bNew);			
			if(sd[5]!=null) d.append2("isale",sd[5],true,bNew);
			if(bNew || !d.isEmpty()) d.append2("ddate","sysdate",true,bNew);
			if(sd[4]!=null)
			{
				d.append2("iprice",sd[4],true,bNew);
				d.append2("cdate","sysdate",true,bNew);
			}
			if(sd[6]!=null)
			{
				if(!sd[6].isEmpty())
				{
					Open("select id from smpart where rownum<2 and vccode='"+sd[6]+"'",false);
					if(m_set!=null && m_set.next()) sd[6] = m_set.getString(1);
					else { Close(true); d=null; return 1; }
				}
				else sd[6] = "0";
				d.append2("inewid",sd[6],true,bNew);
			}
			if(sd[7]!=null) d.append2("tdate",sd[7],false,bNew);
			if(sd[6]!=null || sd[7]!=null)
			{
				d.append2("rdate","sysdate",true,bNew);
				d.append2("iuserid",(String)request.getSession().getAttribute("userid"),true,bNew);
			}
			m_s = "smpart";
    	}
		else if(mode==9) // price
    	{
			if(sd[0]!=null)
			{
				Open("select id from smpart where rownum<2 and vccode='"+sd[0]+"'",false);
				if(m_set!=null && m_set.next()) sd[0] = m_set.getString(1);
				else { Close(true); d=null; return 1; }
				d.append2("ipartid",sd[0],true,bNew);
			}
			if(sd[1]!=null)
			{
				Open("select id from smuser where rownum<2 and vccname='"+sd[1]+"'",false);
				if(m_set!=null && m_set.next()) sd[1] = m_set.getString(1);
				else { Close(true); d=null; return 2; }
				d.append2("iuserid",sd[1],true,bNew);
			}
			if(sd[2]!=null)d.append2("iprice",sd[2],true,bNew);
			if(bNew || !d.isEmpty()) d.append2("ddate","sysdate",true,bNew);
			m_s = "smprice";
    	}
		else if(mode==10) // partphoto
    	{
			if(sd[0]!=null)
			{
				Open("select id from smpart where rownum<2 and vccode='"+sd[0]+"'",false);
				if(m_set!=null && m_set.next()) sd[0] = m_set.getString(1);
				else { Close(true); d=null; return 1; }
				d.append2("ipartid",sd[0],true,bNew);
			}
			if(sd[1]!=null)d.append2("iphotoid",sd[1],true,bNew);
			if(bNew || !d.isEmpty()) d.append2("ddate","sysdate",true,bNew);
			m_s = "smpartphoto";
    	}
		else if(mode==12) // veh
    	{
			if(sd[0]!=null) d.append2("vccode",sd[0],false,bNew);
			if(sd[1]!=null)
			{
				Open("select id from tcmodel where rownum<2 and vccode='"+sd[1]+"'",false);
				if(m_set!=null && m_set.next()) sd[1] = m_set.getString(1);
				else { Close(true); d=null; return 1; }
				d.append2("imodelid",sd[1],true,bNew);
			}
			if(sd[2]!=null) d.append2("ibrandid",sd[2],true,bNew);
			if(sd[3]!=null) d.append2("vcedition",sd[3],false,bNew);
			if(sd[4]!=null) d.append2("vccolor",sd[4],false,bNew);
			if(sd[5]!=null) d.append2("icabin",sd[5],true,bNew);
			if(sd[6]!=null) d.append2("iwb",sd[6],true,bNew);
			if(sd[7]!=null) d.append2("idrive",sd[7],true,bNew);
			if(sd[8]!=null) d.append2("vcengine",sd[8],false,bNew);
			if(sd[9]!=null) d.append2("ifuel",sd[9],true,bNew);
			if(sd[10]!=null)d.append2("iemission",sd[10],true,bNew);
			if(sd[11]!=null)d.append2("iac",sd[11],true,bNew);
			if(sd[12]!=null)d.append2("iabs",sd[12],true,bNew);
			if(sd[13]!=null)d.append2("isrs",sd[13],true,bNew);
			if(sd[14]!=null)d.append2("ipdc",sd[14],true,bNew);
			if(sd[15]!=null)d.append2("irhd",sd[15],true,bNew);			
			if(bNew || !d.isEmpty()) d.append2("ddate","sysdate",true,bNew);
			m_s = "tcveh";
    	}
		else if(mode==13) // vin
    	{
			if(sd[0]!=null) d.append2("vcvin",sd[0],false,bNew);
			if(sd[1]!=null) d.append2("vcesn",sd[1],false,bNew);
			if(sd[2]!=null) d.append2("vcbill",sd[2],false,bNew);
			if(sd[3]!=null)
			{
				Open("select id from smuser where rownum<2 and vccname='"+sd[3]+"'",false);
				if(m_set!=null && m_set.next()) sd[3] = m_set.getString(1);
				else { Close(true); d=null; return 3; }
				d.append2("iuserid",sd[3],true,bNew);
			}
			if(sd[4]!=null)d.append2("iyear",sd[4],true,bNew);
			if(sd[5]!=null)
			{
				sd[5]=getVehID(sd[5],false);
				if(sd[5].equalsIgnoreCase("0")) { Close(true); d=null; return 5; }
				d.append2("ivehid",sd[5],true,bNew);
			}
			if(sd[6]!=null) d.append2("vcnote",sd[6],false,bNew);			
			if(bNew || !d.isEmpty()) d.append2("ddate","sysdate",true,bNew);
			m_s = "tcvin";
    	}
		else if(mode==14) // model
    	{
			if(sd[0]!=null) d.append2("vccode",sd[0],false,bNew);
			if(bNew || !d.isEmpty()) d.append2("ddate","sysdate",true,bNew);
			m_s = "tcmodel";
    	}
		else if(mode==15) // brand
    	{
			if(sd[0]!=null) d.append2("iseq",sd[0],true,bNew);
			if(sd[1]!=null) d.append2("vccode",sd[1],false,bNew);
			if(bNew || !d.isEmpty()) d.append2("ddate","sysdate",true,bNew);
			m_s = "tcbrand";
    	}
		else if(mode==16) // sale
    	{
			if(sd[0]!=null)
			{
				Open("select id from smpart where rownum<2 and vccode='"+sd[0]+"'",false);
				if(m_set!=null && m_set.next()) sd[0] = m_set.getString(1);
				else { Close(true); d=null; return 1; }
				d.append2("ipartid",sd[0],true,bNew);
			}
			if(sd[1]!=null) d.append2("ioff",sd[1],true,bNew);
			if(sd[2]!=null) d.append2("inum",sd[2],true,bNew);
			if(bNew)
			{
				d.append2("istate","1",true,bNew);
				d.append2("ddate1","sysdate",true,bNew);
			}
			m_s = "smsale";
    	}
		
		if(bNew)
		{
			Open("select max(id)+1 as id from "+m_s,false);
			if(m_set!=null && m_set.next()) id = m_set.getString(1);
			else { Close(true); d=null; return -2; }
			d.append2("id",id,true,bNew);
		}
		if(d.isEmpty()) { Close(true); d=null; return -3; }	
				
		if(bNew) m_s="insert into "+m_s+"("+d.result1()+") values("+d.result2()+")";
		else	 m_s="update "+m_s+" set "+d.result()+" where id="+id;
		d = null;
		bNew = m_smt.executeUpdate(m_s)>0;
		Close(true);
		return bNew ? 0 : -4;
	}
	
	public int deleteItem(int mode, String id) throws Exception 
	{
		if(mode==1) m_s = "update tcasmlink set isn=0,iasmid=0,ipartid=0,iimageid=0,ddate=sysdate";
		else if(mode==3) m_s = "update tcvehlink set isn=0,ivehid=0,ipartid=0,iimageid=0,ddate=sysdate";
		else if(mode==4) m_s = "update tchot set iimageid=0,ddate=sysdate";//X
		else if(mode==6) m_s = "delete from smbulletin";
		else if(mode==7) m_s = "delete from tcwall";
		else if(mode==9) m_s = "update smprice set ipartid=0,iuserid=0,ddate=sysdate";
		else if(mode==10)
		{
			Intial();
			String ipart="";
			Open("select ipartid from smpartphoto where rownum<2 and id="+id,false);
			if(m_set!=null && m_set.next()) ipart = m_set.getString(1);
			Close(false);
			m_s = "update smpartphoto set ipartid=0,iphotoid=0,ddate=sysdate where id="+id;
			if(m_smt.executeUpdate(m_s)<1) { Close(true);  return 0; }
			Open("select id from smpartphoto where rownum<2 and ipartid="+ipart,false);
			if(m_set!=null && m_set.next()) { Close(true);  return 1; }
			m_s = "update smpart set iphoto=0,pdate=sysdate where id="+ipart;
			return Update(m_s,2);
		}
		else return -1;
		m_s += " where id=" + id;
		return Update(m_s,1);
	}
	public int setCurWall(String id) throws Exception 
	{
		Intial();
		int ret = m_smt.executeUpdate("update tcwall set iflag=0 where iflag>0");
		if(ret>0) ret = m_smt.executeUpdate("update tcwall set iflag=1 where id="+id);
		Close(true);
		return ret>0?1:0;
	}
	
	public String getSuggestContent(String id) throws Exception 
	{
		m_s = "select vccontent from smsuggest where id="+id;
		Open(m_s,true);
		m_s = "";
		if(isValid() && Next()) m_s = getString(1);
		Close(true);
		return m_s;
	}
	
	public int setSuggestDeal(String id) throws Exception 
	{
		String userid=(String)request.getSession().getAttribute("userid");;
		m_s = "update smsuggest set ideal=1,ddate2=sysdate,idealerid="+userid+" where id="+id;
		return Update(m_s,1);
	}

	public int saveRight(String d) throws Exception
	{
		Intial();
		String[] ar=null;	int i,k,m,n;
		if(d!=null && !d.isEmpty())
		{
			ar = d.split(";");
			for(i=0; i<ar.length; i++)
			{
				k = ar[i].indexOf(',');
				n = Integer.parseInt(ar[i].substring(0,k));
				m = n/9+1;
				n = n%9+1;
				m_s = "update smRight set d"+m+"="+ar[i].substring(k+1)+" where id="+n;
				if(m_smt.executeUpdate(m_s)<=0){ Close(true); return 0; }
			}
			ar = null;
		}
		Close(true);
		return 1;
	}
	
	public int downSale(String flag,String ids) throws Exception 
	{
		Intial();
		int ret = 0;
		if(flag.equals("1")) ret = m_smt.executeUpdate("update smsale set istate=2,ddate2=sysdate where id in("+ids+")");
		else				 ret = m_smt.executeUpdate("update smsale set istate=1,ddate1=sysdate where id in("+ids+")");
		Close(true);
		return ret>0?1:0;
	}
}