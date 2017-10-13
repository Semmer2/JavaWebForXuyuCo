package com.jl.tag.datagrid;

import javax.servlet.jsp.JspException;

public final class NumberColumn extends AColumnTag
{
	private static final long serialVersionUID = -5393135421956784602L;

	private String mstrDataFormat;
	private String mstrPreUnit="";

    public NumberColumn()
    {
        super();
    }

    public String getDataFormat()
    {
        return this.mstrDataFormat;
    }

    public void setDataFormat(String pstrDataFormat)
    {
        this.mstrDataFormat = pstrDataFormat;
    }
    
    public String getPreUnit()
    {
        return this.mstrPreUnit;
    }

    public void setPreUnit(String pstrPreUnit)
    {
        this.mstrPreUnit = pstrPreUnit;
    }
        
    public int doEndTag() throws JspException
    {
    	NumberColumn obj = new NumberColumn();
        obj.setDataFormat(this.mstrDataFormat);
        obj.setPreUnit(this.mstrPreUnit);
    	int ret = doEndTag(obj);
    	if(obj != null) obj = null;
    	return ret;
    }

    public String getDetail(Object pobjValue) throws JspException
    {
        String s = "<td" + getBase(true) + ">";
        if(this.mstrPreUnit.isEmpty()) s += formatField(pobjValue, this.mstrDataFormat,5);         
        else s += this.mstrPreUnit + formatField(pobjValue, this.mstrDataFormat,5);
        s += "</td>";
        return s;
    }

	public String getEmptyTDDetail() throws JspException
	{
		return getDetail("value");
	}
}
