package com.jl.service;

import java.io.File;
import java.util.Hashtable;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.PageContext;
import com.jl.dao.*;

public class WallImageService {
	private WallImagedba dba=null;
	private HttpServletRequest request;
	public WallImageService(HttpServletRequest request)
	{		
		this.request=request;
		dba=new WallImagedba(request);
	}
	public String getMaxId() throws Exception
    {
    	return dba.getMaxId();
    }
	public List getWallImageList() throws Exception
	{
		return dba.getWallImageList();
	}
	
	public String getWall() throws Exception
	{
		String todaywall=dba.getTodayWall();
		String wall=dba.getWall();
		String wallpath=request.getRealPath("wall");
		File folder=new File(wallpath);
		File[] t = folder.listFiles();
		if(wall=="")
		{
			//初始化	
			 wall=t[0].getName();
			 dba.addWall(wall);
		}
		else
		{
			if(todaywall=="")
			{
				for(int i=0;i<t.length;i++)
				{
					String wallfilename=t[i].getName();
					if(wall.equals(wallfilename))
					{
						if(i==t.length-1)
						{
							wall=t[0].getName();
						}
						else
						{
							wall=t[i+1].getName();							
						}
						break;
					}
				}
				dba.updateWall(wall);
			}
			else
			{
				wall=todaywall;
			}
		}
		return "../wall/"+wall;
	}
	public String getWallFromDataBase() throws Exception
	{		
		String wall=dba.getWall();
		String wallpath=request.getRealPath("wall");		
		File folder=new File(wallpath);
		List ls=dba.getWallAllShowImageList();
		String showmodel=dba.getImageShowModel();
		if(showmodel.equalsIgnoreCase("1"))
		{
			if(wall=="")
			{
				for(int j=0;j<ls.size();j++)
				{
					Hashtable ht=(Hashtable)ls.get(j);	
					//初始化
					wall=ht.get("wall").toString();					
					dba.addWall(wall);
					break;
				}
			}
			else
			{
				
					for(int i=0;i<ls.size();i++)
					{
						Hashtable ht=(Hashtable)ls.get(i);	
						String wallfilename=ht.get("wall").toString();
						if(wall.equals(wallfilename))
						{
							if(i==ls.size()-1)
							{
								ht=(Hashtable)ls.get(0);	
								wall=ht.get("wall").toString();
							}
							else
							{
								ht=(Hashtable)ls.get(i+1);	
								wall=ht.get("wall").toString();					
							}
							break;
						}
					}
					dba.updateWall(wall);						
			}
		}
		else
		{
			if(wall=="")
			{
				if(ls.size()>0)
				{
					Hashtable ht=(Hashtable)ls.get(0);	
					//初始化
					wall=ht.get("wall").toString();					
					dba.addWall(wall);
				}
			}
			else
			{
				if(ls.size()>0)
				{
					Hashtable ht=(Hashtable)ls.get(0);	
					String wallfilename=ht.get("wall").toString();
					dba.updateWall(wallfilename);
				}						
			}
		}
		return "../wall/"+wall;
	}
	public void addWallImage(String imagename,String id) throws Exception
	{
		dba.addWallImage(imagename,id);
	}
	public void updateWallImage(String id,String imagename) throws Exception
	{
		dba.updateWallImage(id,imagename);
	}
	public void updateWallImageShow(String id,String showflag) throws Exception
	{
		dba.updateWallImageShow(id,showflag);
	}
	public String deleteWallImage(String id) throws Exception
	{
		return dba.deleteWallImage(id);
	}
	public void updateImageShowModel(String model) throws Exception
	{
		dba.updateImageShowModel(model);
	}
	public String getImageShowModel() throws Exception
	{
		return dba.getImageShowModel();
	}
}
