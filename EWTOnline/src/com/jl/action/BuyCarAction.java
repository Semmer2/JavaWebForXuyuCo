package com.jl.action;
import com.jl.service.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.PageContext;
import java.util.*;
public class BuyCarAction {
	private BuyCarService service=null;
    public BuyCarAction(HttpServletRequest request)
    {    	
    	service=new BuyCarService(request);
    }
    public String createBuy(String userid,String vccode,String vcename,String iqty) throws Exception {
		return service.createBuy(userid,vccode,vcename,iqty);
	}
	public String delBuyCarByUserid(String userid) throws Exception {
		return service.delBuyCarByUserid(userid);
	}
	public String delBuyCar(String id) throws Exception {
		return service.delBuyCar(id);
	}
	public String updateQty(String id,String qty) throws Exception {
		return service.updateQty(id,qty);
	}
	public List getBuycar(String userid) throws Exception {
		return service.getBuycar(userid);
	}
}
