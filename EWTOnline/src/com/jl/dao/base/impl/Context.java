package com.jl.dao.base.impl;

import java.util.HashMap;

public class Context {
    private static ThreadLocal local= new ThreadLocal();
    private static Context context = new Context();
    public static Context getInstance(){    	
    	if(local.get()==null) 
    	{
    		//System.out.println("thread id="+Thread.currentThread().getId());
    		local.set(new HashMap());
    	}
    	return context;
    }

    public void put(String name,String value) {
    	HashMap map=(HashMap)local.get();    	
    	map.put(name,value);
    }

    public String get(String name) {
    	//System.out.println("thread id="+Thread.currentThread().getId());
    	HashMap map=(HashMap)local.get(); 
        return map.get(name)==null?"":map.get(name).toString();
    }
}
