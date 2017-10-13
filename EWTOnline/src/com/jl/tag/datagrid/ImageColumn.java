package com.jl.tag.datagrid;

import javax.servlet.jsp.JspException;

public final class ImageColumn extends AColumnTag
{
	private static final long serialVersionUID = -3220922539015030763L;

	private int mintImageWidth;
    private int mintImageHeight;
    private int mintImageBorder;

    private String mstrImageSrc;
    private String mstrLinkUrl;
    private String mstrAlterText;
    private String mstrTarget;

    public ImageColumn()
    {
        super();
        this.mintImageWidth = -1;
        this.mintImageHeight = -1;
        this.mintImageBorder = -1;
    }

    public String getImageSrc()
    {
        return this.mstrImageSrc;
    }

    public String getLinkUrl()
    {
        return this.mstrLinkUrl;
    }

    public String getAlterText()
    {
        return this.mstrAlterText;
    }

    public String getTarget()
    {
        return this.mstrTarget;
    }

    public int getImageWidth()
    {
        return this.mintImageWidth;
    }

    public int getImageHeight()
    {
        return this.mintImageHeight;
    }

    public int getImageBorder()
    {
        return this.mintImageBorder;
    }

    public void setImageSrc(String pstrSrc)
    {
        this.mstrImageSrc = pstrSrc;
    }

    public void setLinkUrl(String pstrUrl)
    {
        this.mstrLinkUrl = pstrUrl;
    }

    public void setAlterText(String pstrAltText)
    {
        this.mstrAlterText = pstrAltText;
    }

    public void setTarget(String pstrTarget)
    {
        this.mstrTarget = pstrTarget;
    }

    public void setImageWidth(int pintWidth)
    {
        this.mintImageWidth = pintWidth;
    }

    public void setImageHeight(int pintHeight)
    {
        this.mintImageHeight = pintHeight;
    }

    public void setImageBorder(int pintBorder)
    {
        this.mintImageBorder = pintBorder;
    }

    private String resolveFields(String pstrUrl) throws ClassCastException
    {
        int    intPos = 0;
        int    intEnd = 0;
        String strCol = null;
        String strRet = null;
        DBGrid objTmp = null;
        strRet = pstrUrl;
        objTmp = (DBGrid) getParent();
        intPos = strRet.indexOf("{");
        while (intPos >= 0)
        {
            intEnd = strRet.indexOf("}", intPos + 1);
            if (intEnd != -1)
            {
                strCol = strRet.substring(intPos + 1, intEnd);
                strRet = strRet.substring(0, intPos) +
                		objTmp.getColumnValue(strCol) +
                		strRet.substring(intEnd + 1);
            }
            intPos = strRet.indexOf("{", intPos +1);
        }
        return strRet;
    }
    
    public int doEndTag() throws JspException
    {     
    	ImageColumn obj = new ImageColumn();     
        obj.setImageBorder(this.mintImageBorder);
        obj.setImageHeight(this.mintImageHeight);
        obj.setImageSrc(this.mstrImageSrc);
        obj.setImageWidth(this.mintImageWidth);
        obj.setLinkUrl(this.mstrLinkUrl);
        obj.setTarget(this.mstrTarget);
        obj.setAlterText(this.mstrAlterText);
    	int ret = doEndTag(obj);
    	if(obj != null) obj = null;
    	return ret;
    }

    public String getDetail(Object pobjValue) throws JspException
    {
        String s = "<td" + getBase(true) + ">";
        s += "<a HREF='" + resolveFields(this.mstrLinkUrl);
        if(this.mstrTarget != null) s += " taget='" + (this.mstrTarget.equals("3") ? "_self" : "this.mstrTarget") + "'";
        s += "\"><img SRC='" + this.mstrImageSrc + "'";
        if(this.mintImageWidth != -1) s += " width=" + String.valueOf(this.mintImageWidth);
        if(this.mintImageHeight != -1) s += " height=" + String.valueOf(this.mintImageHeight);
        if(this.mintImageBorder != -1) s += " border=" + String.valueOf(this.mintImageBorder);
        s += " alt='" + this.mstrAlterText + "'";
        s += "></a></td>";
        return s;
    }

	public String getEmptyTDDetail() throws JspException
	{
        return getDetail("&value");
	}
}
