package com.jl.dao.base.impl;

import java.util.HashMap;

import org.apache.log4j.Logger;

//数据库连接辅助类 
public final class ConnectionMap {
    private static HashMap m=new HashMap();
    private final static LoggerContext logger=new LoggerContext(ConnectionMap.class);
	public static boolean add(AppConnection connection,String id)
	{
		if(m.containsKey(id)==false) 
		{
			m.put(id, connection);
			logger.info("add AppConnection: id="+id);
			logger.info("connections:="+m.size());
			return true;
		}
		else
		{
			return false;
		}
	}
	public static AppConnection get(String id)
	{
		if(m.containsKey(id)) 
			return (AppConnection)m.get(id);
		else
			return null;
	}
	public static boolean remove(String id)
	{
		if(m.containsKey(id))
		{			
			m.remove(id);
			logger.info("remove AppConnection: id="+id);
			logger.info("connections:="+m.size());
			return true;
		}
		else
		{
			return false;
		}
	}
}