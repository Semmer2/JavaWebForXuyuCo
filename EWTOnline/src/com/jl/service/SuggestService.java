package com.jl.service;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.PageContext;
import com.jl.dao.*;

public class SuggestService {
	private SuggestDba dba=null;
	public SuggestService(HttpServletRequest request)
	{		
		dba=new SuggestDba(request);
	}
	public String createSuggest(String subject, String path, String content, String fromwho, String id,String vehid,String imageid) throws Exception {
		return dba.createSuggest(subject, path, content, fromwho, id,vehid,imageid);
	}
	public String getNewSuggestId() throws Exception
	{
		return dba.getNewSuggestId();
	}
	public String delSuggest(String ids) throws Exception {
		return dba.delSuggest(ids);
	}	
	public List getSuggests() throws Exception {
		return dba.getSuggests();
	}	
	public List getSuggestById(String id) throws Exception {
		return dba.getSuggestById(id);
	}
}