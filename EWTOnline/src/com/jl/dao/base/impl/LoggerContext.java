package com.jl.dao.base.impl;

import org.apache.log4j.Logger;

import com.jl.util.*;

public class LoggerContext {
	private Logger  logger=null;
	public  LoggerContext(Class classobject) {
		logger=Logger.getLogger(classobject);
	}
	
	public void info(String s)
	{
		/*if(logger!=null)
		{
			//String printlogger=Constants.getValue("printlogger");
			String printlogger = "no";
			
			if(printlogger.equalsIgnoreCase("yes"))
			{
				//System.out.println("info:"+DateUtil.getCurrentDateAndTime()+" "+s);
				logger.info(DateUtil.getCurrentDateAndTime()+" "+s);
			}
		}*/
	}
	public void error(String s)
	{
		if(logger!=null)
		{
			//System.out.println("error:"+DateUtil.getCurrentDateAndTime()+" "+s);
			logger.error(DateUtil.getCurrentDateAndTime()+" "+s);
		}
	}	
}