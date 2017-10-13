package com.jl.service;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.PageContext;
import com.jl.dao.*;

public class BuyCarService {
	private BuyCarDba dba=null;
	public BuyCarService(HttpServletRequest request)
	{		
		dba=new BuyCarDba(request);
	}
	public String createBuy(String userid,String vccode,String vcename,String iqty) throws Exception {
		return dba.createBuy(userid,vccode,vcename,iqty);
	}
	public String delBuyCarByUserid(String userid) throws Exception {
		return dba.delBuyCarByUserid(userid);
	}
	public String delBuyCar(String id) throws Exception {
		return dba.delBuyCar(id);
	}
	public String updateQty(String id,String qty) throws Exception {
		return dba.updateQty(id,qty);
	}
	public List getBuycar(String userid) throws Exception {
		return dba.getBuycar(userid);
	}
}
