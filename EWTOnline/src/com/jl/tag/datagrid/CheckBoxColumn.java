package com.jl.tag.datagrid;

import java.io.IOException;
import javax.servlet.jsp.JspException;

public class CheckBoxColumn extends AColumnTag
{

    public CheckBoxColumn()
    {
        super();
    }
    
    public int doEndTag() throws JspException
    {
    	CheckBoxColumn obj = new CheckBoxColumn();
    	int ret = doEndTag(obj);
    	if(obj != null) obj = null;
    	return ret;
    }

    public String getDetail(Object pobjValue) throws JspException
    {
    	String s = "";
    	if(this.mstrHeaderText.equalsIgnoreCase("hidden"))
    		s += "<input type='hidden' name='chk' value='" + pobjValue.toString() + "'>";
     	else
     	{
     		s += "<td" + getBase(true) + " iscopy='" + (this.mblnCopy ? "true" : "false") + "'>";
     		s += "<input type='" + (this.mstrHeaderText.equalsIgnoreCase("radio") ? "radio" : "checkbox");
     		s += "' name='chk' value='" + pobjValue.toString() + "'></td>";
     	}
    	return s;
    }
    
	public String getEmptyTDDetail() throws JspException
	{
		return getDetail("&value");
	}
}
