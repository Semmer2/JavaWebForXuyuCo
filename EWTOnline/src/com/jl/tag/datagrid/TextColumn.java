package com.jl.tag.datagrid;

import javax.servlet.jsp.JspException;

public final class TextColumn extends AColumnTag
{
    private static final long serialVersionUID = 5928642885957457925L;

    private int    mintMaxLength;
    private String mstrDataFormat="";
    private boolean isShowTitle;
    private String mstrAlterText="";
    private String mstrDoClick="";

    public TextColumn()
    {
        super();
        this.mintMaxLength = -1;
        isShowTitle=true;
    }
    public String getAlterText()
    {
        return this.mstrAlterText;
    }
    public void setAlterText(String pstrAltText)
    {
        this.mstrAlterText = pstrAltText;
    }
    public String getDoClick()
    {
        return this.mstrDoClick;
    }
    public void setDoClick(String strclick)
    {
        this.mstrDoClick = strclick;
    }

    public String getDataFormat()
    {
        return this.mstrDataFormat;
    }

    public int getMaxLength()
    {
        return this.mintMaxLength;
    }

    public void setDataFormat(String pstrDataFormat)
    {
        this.mstrDataFormat = pstrDataFormat;
    }

    public void setMaxLength(int pintMaxLen)
    {
        this.mintMaxLength = pintMaxLen;
    }

    public void setIsShowTitle(boolean isShowTitle)
    {
        this.isShowTitle = isShowTitle;
    }

    public boolean isIsShowTitle()
    {
        return isShowTitle;
    }
    
    public int doEndTag() throws JspException
    {
        TextColumn obj = new TextColumn();
        obj.setDataFormat(this.mstrDataFormat);
        obj.setMaxLength(this.mintMaxLength);
        obj.setIsShowTitle(this.isShowTitle);
        obj.setAlterText(this.mstrAlterText);
        obj.setDoClick(this.mstrDoClick); 
    	int ret = doEndTag(obj);
    	if(obj != null) obj = null;
    	return ret;
    }

    public String getTD(String sData, String sTag, boolean bHead)
    {
        String s = "<td" + getBase(bHead);
        if(sTag!=null && !sTag.isEmpty()) s += " tag='" + sTag + "'";
        if(this.isShowTitle && !mstrAlterText.equalsIgnoreCase("")) s += " title='" + this.mstrAlterText + "'"; 
        s += ">";
        sData.trim();
        if(sData==null || sData.isEmpty()) sData = "&nbsp;";
    	if(this.mintMaxLength > 0 && sData.length() > this.mintMaxLength)
    		sData = sData.substring(0, this.mintMaxLength) + "...";
    	if(this.mstrDoClick.isEmpty()) s += sData;
    	else s += "<span style='cursor:hand' onclick='" + this.mstrDoClick +"'>" + sData + "</span>";
    	s += "</td>";
    	return s;
    }
    
    public String getDetail(Object pobjValue) throws JspException
    {
        String cs = null, s = "<td" + getBase(true);
        if(pobjValue instanceof ComplexField)
        {
        	Object d = ((ComplexField)pobjValue).getTagfield();
        	cs = d.toString();
        	if(cs!=null && !cs.isEmpty()) s += " tag='" + cs + "'";
        	d = ((ComplexField)pobjValue).getDatafield();
        	cs = d.toString().trim();
        	d = null;
        }
        else cs = pobjValue.toString().trim();
        if(cs==null || cs.isEmpty()) cs = "&nbsp;";
        if(this.isShowTitle && !mstrAlterText.equalsIgnoreCase("")) s += " title='" + this.mstrAlterText + "'";        
        s += ">";
        
    	if(this.mintMaxLength > 0 && cs.length() > this.mintMaxLength)
    		cs = cs.substring(0, this.mintMaxLength) + "...";
    	if(this.mstrDoClick.isEmpty()) s += cs;
    	else s += "<span style='cursor:hand' onclick='" + this.mstrDoClick +"'>" + cs + "</span>";
    	s += "</td>";
    	return s;
    }

	public String getEmptyTDDetail() throws JspException
	{
        return getDetail("");
	}	
}
