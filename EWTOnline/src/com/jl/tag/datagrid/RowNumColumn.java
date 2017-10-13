package com.jl.tag.datagrid;

import java.io.IOException;
import javax.servlet.jsp.JspException;

public final class RowNumColumn extends AColumnTag
{
	private static final long serialVersionUID = 4269818630991011588L;
	private boolean isShowTitle;
	public RowNumColumn()
    {
		super();
		isShowTitle=true;
    }
	
    public void setIsShowTitle(boolean isShowTitle)
    {
    	isShowTitle = isShowTitle;
    }

    public boolean isIsShowTitle()
    {
		return isShowTitle;
    }

    public int doEndTag() throws JspException
    {
    	RowNumColumn obj = new RowNumColumn();
		obj.setIsShowTitle(this.isShowTitle);
    	int ret = doEndTag(obj);
    	if(obj != null) obj = null;
    	return ret;
    }
    
	public String getTDDetail(Object pobjValue,Object pobjkeyword,String viewdetail,String viewwidth,String viewheight)
	{
		String s = "<td" + getBase(true);		
		if(pobjkeyword != null) s += " tag='" + pobjkeyword.toString() + "'";
		if(pobjkeyword != null) s += " tag='" + pobjkeyword.toString() + "'";
		if(viewdetail != null) s += " viewdetail='" + viewdetail.toString() + "'";
		if(viewwidth != null) s += " viewwidth='" + viewwidth.toString() + "'";
		if(viewheight != null) s += " viewheight='" + viewheight.toString() + "'";
		if(pobjValue != null && isShowTitle) s += " title='" + pobjValue.toString() + "'";
		s += ">";
		if(pobjValue != null) s += pobjValue.toString();
		s += "</td>";
		return s;
	}

	public String getEmptyTDDetail(String viewdetail,String viewwidth,String viewheight)
    {
    	return getTDDetail("&value","&keyword",viewdetail,viewwidth,viewheight);
    }
	
	public String getDetail(Object pobjValue) throws JspException
	{
	    	return getTDDetail(pobjValue,null,null,null,null);
	}
	
	public String getEmptyTDDetail() throws JspException
	{
		return getTDDetail("&nbsp;", null, null, null, null);		
	}	
}
