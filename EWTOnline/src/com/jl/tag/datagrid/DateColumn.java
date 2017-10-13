package com.jl.tag.datagrid;

import javax.servlet.jsp.JspException;

public final class DateColumn extends AColumnTag
{
	private static final long serialVersionUID = 3193015881427040863L;
	private String mstrDataFormat;
	public DateColumn()
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

    public int doEndTag() throws JspException
    {
    	DateColumn obj = new DateColumn();
    	obj.setDataFormat(this.mstrDataFormat);
    	int ret = doEndTag(obj);
    	if(obj != null) obj = null;
    	return ret;
    }

    public String getDetail(Object pobjValue) throws JspException
    {
    	String s = formatField(pobjValue, this.mstrDataFormat, 1);
    	if((s == null || s.equals(""))) s = "&nbsp;";
    	s = "<td" + getBase(true) + ">" + s + "</td>";
    	return s;
    }

    public String getEmptyTDDetail() throws JspException
    {
    	return getDetail("&value");
    }
}
