package com.jl.dao;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.PageContext;

import com.jl.dao.base.impl.BaseData;
import com.jl.dao.base.impl.DBASession;

public class BuyCarDba extends BaseData {
	public BuyCarDba(HttpServletRequest request)
	{
		super(request);
	}
	public boolean isExist(String userid,String vccode) throws Exception {
		boolean ret=true;	
		DBASession dbsession=getSession();
		try
		{		
			List param = new ArrayList();
			StringBuffer sql=new StringBuffer();
			sql.append("select count(*) as returnvalue from jlcar.tccar where 1=1 ");
			if(!"".equals(userid)){
				sql.append(" and userid=?");
				param.add(userid);
			}
			if(!"".equals(vccode)){
				sql.append(" and vccode=?");
				param.add(vccode);
			}
			List ls=dbsession.openSelectbyList(sql.toString(),param.toArray());
			String returnvalue=getValue(ls,0,"returnvalue");
			if("0".equals(returnvalue)||"-1".equals(returnvalue)){
				ret=false;
			}else {
				ret=true;
			}
		} catch (Exception e) {
			throw e;
		} finally {
			dbsession.close();
			dbsession=null;	
		}
		return ret;
	}
	
	public String createBuy(String userid,String vccode,String vcename,String iqty) throws Exception {
		String ret="";	
		DBASession dbsession=getSession();
		try
		{	
			if(isExist(userid,vccode)){
				setError("The Part exist in buylist!");
			}else {				
				StringBuffer sql=new StringBuffer();
				List ls=dbsession.openSelectbyList("select s_buycarid.Nextval as buyid from dual");
				String id=getValue(ls,0,"buyid");
				sql.append("insert into jlcar.tccar(id,vccode,vcename,iqty,userid) values(?,?,?,?,?)");
				List param = new ArrayList();
				param.add(id);
				param.add(vccode);
				param.add(vcename);
				param.add(iqty);
				param.add(userid);
				boolean isSuccess=dbsession.runSql(sql.toString(), param.toArray());				
				if(!isSuccess){
					setError("增加失败");
				}
				else
				{
					ret=id;
				}
			}
		} catch (Exception e) {
			setError("增加发生异常!");
			throw e;
		} finally {
			dbsession.close();
			dbsession=null;	
		}
		return ret;
	}
	public String delBuyCarByUserid(String userid) throws Exception {
		String ret="";	
		DBASession dbsession=getSession();
		Object tran=null;
		try
		{
			String sql="delete from jlcar.tccar where userid=?";
			List param = new ArrayList();			
			param.add(userid);
			boolean isSuccess=dbsession.runSql(sql.toString(), param.toArray());			
			dbsession.endlongTran(tran);
			ret="0";
		} catch (Exception e) {
			setError("删除发生异常");
			dbsession.rollbacklongTran(tran);
			throw e;
		} finally {			
			dbsession.close();
			dbsession=null;	
		}
		return ret;
	}
	public String delBuyCar(String id) throws Exception {
		String ret="";	
		DBASession dbsession=getSession();
		Object tran=null;
		try
		{
			String sql="delete from jlcar.tccar where id=?";
			List param = new ArrayList();			
			param.add(id);
			boolean isSuccess=dbsession.runSql(sql.toString(), param.toArray());			
			dbsession.endlongTran(tran);
			ret="0";
		} catch (Exception e) {
			setError("删除发生异常!");
			dbsession.rollbacklongTran(tran);
			throw e;
		} finally {			
			dbsession.close();
			dbsession=null;	
		}
		return ret;
	}
	public String updateQty(String id,String qty) throws Exception {
		String ret="";	
		DBASession dbsession=getSession();
		try
		{			
			StringBuffer sql=new StringBuffer();			
			sql.append("update jlcar.tccar set iqty=? where id=?");
			List param = new ArrayList();
			param.add(qty);
			param.add(id);
			boolean isSuccess=dbsession.runSql(sql.toString(), param.toArray());				
			if(!isSuccess){
				setError("更新数量失败");
			}
			else
			{
				ret=id;
			}
		} catch (Exception e) {
			setError("更新数量异常!");
			throw e;
		} finally {
			dbsession.close();
			dbsession=null;	
		}
		return ret;
	}
	public List getBuycar(String userid) throws Exception {
		List ret=null;	
		DBASession dbsession=getSession();
		try
		{
			List param = new ArrayList();
			param.add(userid);
			ret=dbsession.openSelectbyList("select 'X' as R, id,userid,vccode,vcename,iqty,'<input id='||id||' type=text size=4 maxlength=2 value='''|| iqty|| ''' onchange=''saveqty()'' style=''border:0px;width:expression(this.parentElement.clientWidth)''/>' as iqtyhtml from jlcar.tccar where userid=? order by id desc",param.toArray());			
		} catch (Exception e) {
			throw e;
		} finally {
			dbsession.close();
			dbsession=null;	
		}
		return ret;
	}
}