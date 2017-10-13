/*------------------------------------------------------------------------------
 * PACKAGE: com.tellhow.gridtag
 * FILE   : AColumnTag.java
 * CREATED: 2006-4-20
 * AUTHOR : ������
 *------------------------------------------------------------------------------
 * Change Log:
 *-----------------------------------------------------------------------------*/
package com.jl.tag.datagrid;

//import java.io.IOException;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.Format;
import java.text.SimpleDateFormat;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;


public abstract class AColumnTag extends TagSupport	implements IColumnTag
{
    protected int mintWidth;
    protected int mintHeight;
    protected int mintBorder;
    protected String mstrName;
	protected String mstrBgColor;
    protected String mstrForeColor;
    protected String mstrCssClass;
    protected String mstrHAlign;
    protected String mstrVAlign;
    protected String mstrHeaderText;
    protected String mstrDataField;
    protected String mstrTagField;
    

    protected boolean mblnSortable;
    protected boolean mblnCopy;

    public AColumnTag()
    {
    	super();
    	mintWidth = -1;
    	mintHeight = -1;
    	mintBorder = -1;
    	mblnCopy = false;
    }

    public boolean getIsCopy()
    {
    	return mblnCopy;
    }

    public int getWidth()
    {
    	return mintWidth;
    }

    public int getHeight()
    {
    	return mintHeight;
    }

    public int getBorder()
    {
    	return mintBorder;
    }

	public String getName()
    {
    	return mstrName;
    }

	public String getBgColor()
    {
    	return mstrBgColor;
    }

    public String getForeColor()
    {
    	return mstrForeColor;
    }

     public String getCssClass()
    {
    	return mstrCssClass;
    }

    public String getHAlign()
    {
    	return mstrHAlign;
    }

    public String getVAlign()
    {
    	return mstrVAlign;
    }

    public String getHeaderText()
    {
    	return mstrHeaderText;
    }

    public String getDataField()
    {
    	return mstrDataField;
    }

    public boolean getSortable()
    {
    	return mblnSortable;
    }
    
    public String getTagField()
    {
    	return mstrTagField;
	}
	
    public void setTagField(String tagField)
    {
		mstrTagField = tagField;
	}
    
    public void setIsCopy(boolean isCopy)
    {
    	mblnCopy = isCopy;
    }

    public void setWidth(int pintWidth)
    {
    	mintWidth = pintWidth;
    }

    public void setHeight(int pintHeight)
    {
    	mintHeight = pintHeight;
    }

    public void setBorder(int pintBorder)
    {
    	mintBorder = pintBorder;
    }

    public void setName(String pstrName)
    {
    	mstrName = pstrName;
    }

    public void setBgColor(String pstrColor)
    {
    	mstrBgColor = pstrColor;
    }

    public void setForeColor(String pstrColor)
    {
    	mstrForeColor = pstrColor;
    }

    public void setCssClass(String pstrCssClass)
    {
    	mstrCssClass = pstrCssClass;
    }

    public void setHAlign(String pstrHAlign)
    {
    	mstrHAlign = pstrHAlign;
    }

    public void setVAlign(String pstrVAlign)
    {
    	mstrVAlign = pstrVAlign;
    }

    public void setHeaderText(String pstrHdrText)
    {
    	mstrHeaderText = pstrHdrText;
    }

    public void setDataField(String pstrDataField)
    {
    	mstrDataField = pstrDataField;
    }

    public void setSortable(boolean pblnSortable)
    {
    	mblnSortable = pblnSortable;
    }
    
	public String formatField(Object pobjVal, String pstrFmt, int mode) throws ClassCastException
	{
        String strRet = null;
        Format objFmt = null;
        boolean b = true;
        try
        {
        	if(mode == 2) b = (pstrFmt!=null && !pstrFmt.equals(""));
        	if(mode != 5 && b && (pobjVal instanceof java.sql.Date || pobjVal instanceof java.util.Date))
        	{
        		objFmt = new SimpleDateFormat(pstrFmt);
        		strRet = objFmt.format(pobjVal);
        		objFmt = null;
        	}
        	else if(b && pobjVal instanceof Number)
        	{
        		objFmt = new DecimalFormat(pstrFmt);
        		strRet = objFmt.format(pobjVal);
        		if(mode == 4 && strRet.equals("0")) strRet = "";
        		objFmt = null;
            }
        	else if((mode==1 || mode==6) && pobjVal instanceof java.sql.Timestamp)
            {
        		//SimpleDateFormat objFmt;
                objFmt = new SimpleDateFormat(pstrFmt);
                strRet = objFmt.format(Timestamp.valueOf(pobjVal.toString()));
            }
        	else if(mode == 5)
        	{
        		int i=Float.valueOf(pobjVal.toString()).intValue();
            	float f=Float.valueOf(pobjVal.toString()).floatValue();
            	if(f>i) strRet=String.valueOf(Float.valueOf(pobjVal.toString()));
            	else strRet=pobjVal.toString();
        	}
            else strRet = pobjVal.toString();
        }
        catch(NullPointerException NPExIgnore) {}
        catch (IllegalArgumentException IArgExIgnore) {}
        catch (Exception e) { e.printStackTrace(); }
        finally { if(objFmt != null) objFmt = null;    }
        if(strRet == null) strRet = DBGrid.DEFAULT_NULLTEXT;
        return strRet;
    }
	
    public void copyAttributesTo(IColumnTag pobjDest)
    {
    	pobjDest.setBgColor(mstrBgColor);
    	pobjDest.setBorder(mintBorder);
    	pobjDest.setCssClass(mstrCssClass);
    	if(mstrDataField!=null)	pobjDest.setDataField(mstrDataField.toLowerCase());
    	if(mstrTagField!=null) 	pobjDest.setTagField(mstrTagField.toLowerCase());
    	pobjDest.setForeColor(mstrForeColor);
    	pobjDest.setHAlign(mstrHAlign);
    	pobjDest.setHeaderText(mstrHeaderText);
    	pobjDest.setHeight(mintHeight);
    	pobjDest.setName(mstrName);
    	pobjDest.setSortable(mblnSortable);
    	pobjDest.setVAlign(mstrVAlign);
    	pobjDest.setWidth(mintWidth);
    	pobjDest.setIsCopy(mblnCopy);
    }

    public int doStartTag() throws JspException
    {
    	if(!(this.getParent() instanceof DBGrid))
            throw new JspException("Error: Column tag needs to be a child of DBGrid!");
        return SKIP_BODY;
    }
    
    public int doEndTag(AColumnTag pobjDest) throws JspException
    {
    	copyAttributesTo(pobjDest);
    	pobjDest.setId(this.getId());
    	pobjDest.setPageContext(this.pageContext);
    	pobjDest.setParent(this.getParent());
    	try
	    {
	    	if((this.getParent() instanceof DBGrid))
	    	{
	    		DBGrid obj = (DBGrid)this.getParent();	    		
	    		obj.addColumn(pobjDest);
	    		if(obj != null) obj = null;
	    	}
	    }
	    catch (ClassCastException e)
	    {
	    	throw new JspException("Error: Column tag is not a child of DBGrid!", e);
	    }
	    return EVAL_PAGE;
    }
    
    public String getBase(boolean bHead)
	{
		String s = "";
		if(bHead && mintWidth > 0) s += " width='" + mintWidth + "%'";
		if(mintHeight > 0) s += " height='" + mintHeight + "'";
		if(mstrCssClass != null) s += " class='" + mstrCssClass + "'";
		//else if(bClass) s += " class='gridColumn'";
		if(mstrHAlign != null) s += " align='" + mstrHAlign + "'";	
		if(mstrVAlign != null) s += " valign='" + mstrVAlign + "'";
		if(mstrBgColor != null) s += " bgcolor='" + mstrBgColor + "'";
		if(mstrForeColor != null) s += " color='" + mstrForeColor + "'";		
		return s;
	}
    
    public String getHeader() throws JspException
    {
		if(mstrHeaderText.equalsIgnoreCase("hidden")) return "";
		String s = "", sTxt = "&nbsp;", sTitle = "";
		if(mstrHeaderText != null)
		{
	    	 if(mstrHeaderText.equals("checkbox"))
	    		 sTxt = "<input id='chkAll' type='checkbox' name='chkAll' onclick='return btnChkAll()'>";
	    	 else if(!mstrHeaderText.equals("radio") && !mstrHeaderText.equals(""))
	    		 sTxt = mstrHeaderText;
	    	 String heads[] = mstrHeaderText.split(",");
	    	 if(heads.length >= 1) sTxt = heads[0];
	    	 if(heads.length >= 2) sTitle = heads[1];
		}	
		s = "<td";
		//if(mstrDataField != null) s += " id='" + mstrDataField + "'";
		if(mintWidth > 0) s += " width='" + mintWidth + "%'";
		if(mstrHAlign != null) s += " align='center'";
		if(mintHeight > 0) s += " height='" + mintHeight + "'";
		//if(mstrCssClass != null) s += " class='" + mstrCssClass + "'";	
		if(mstrBgColor != null) s += " bgcolor='" + mstrBgColor + "'";
		if(mstrForeColor != null) s += " color='" + mstrForeColor + "'";		
		
		if(!sTitle.equalsIgnoreCase("")) s += " title='" + sTitle + "'";
		if(mblnSortable && !mstrHeaderText.equals("checkbox")) s += " style='cursor:hand' onclick='sortColumn(this)'";
		s += ">" + sTxt;
		s += "</td>";
		return s;
    }

    public String getBlank() throws JspException
    {
    	return "<td" + getBase(false) + ">&nbsp;</td>";
    }
    
    public String getDetail(Object pobjValue, int recordPos, boolean lightOn) throws JspException
    {
    	return null;
    }
    
	public String getEmptyTDDetail(int recordPos, boolean lightOn) throws JspException
	{
		return null;
	}

	public String getEmptyTDDetail(Object pobjValue) throws JspException
	{
		return null;
	}
}
