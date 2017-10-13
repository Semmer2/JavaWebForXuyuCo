package com.jl.dao.base.impl;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.PageContext;

import org.apache.axis.MessageContext;

import com.jl.dao.*;
public final class SessionFactory {
   public static DBASession getSession(HttpServletRequest request)   {
	   return new SessionImpl(request);  
   }   
}
