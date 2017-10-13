package com.jl.dao.base.impl;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.PageContext;

import org.apache.axis.MessageContext;
import org.apache.commons.lang.StringUtils;

public class BaseMVCDAO implements IBaseMVCDAO {
	
	public HttpServletRequest request;
	public DBASession getSession() {
		return SessionFactory.getSession(request);
	}
	
	public HttpServletRequest getPagecontext() {
		return request;
	}
	public void setError(String errorinfo)
	{
		try {
			request.setAttribute("error",URLEncoder.encode(errorinfo,"UTF-8"));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}	
	
	public BaseMVCDAO(HttpServletRequest request) {
		this.request=request;
		this.request.setAttribute("error","");
	}
	
	public void setPagecontext(HttpServletRequest request) {
		this.request = request;
	}	
	
}