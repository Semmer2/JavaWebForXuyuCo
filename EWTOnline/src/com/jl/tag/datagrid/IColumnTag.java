package com.jl.tag.datagrid;

import javax.servlet.jsp.JspException;

interface IColumnTag
{
    public int getWidth();
    public int getHeight();
    public int getBorder();

    public String getName();
    public String getBgColor();
    public String getForeColor();
    public String getCssClass();
    public String getHAlign();
    public String getVAlign();
    public String getHeaderText();
    public String getDataField();
    public String getTagField();

    public boolean getSortable();
    public boolean getIsCopy();

    public void setWidth(int pintWidth);
    public void setHeight(int pintHeight);
    public void setBorder(int pintBorder);

    public void setName(String pstrName);
    public void setBgColor(String pstrColor);
    public void setForeColor(String pstrColor);
    public void setCssClass(String pstrCssClass);
    public void setHAlign(String pstrHAlign);
    public void setVAlign(String pstrVAlign);
    public void setHeaderText(String pstrHdrText);
    public void setDataField(String pstrField);
    public void setTagField(String pstrField);

    public void setSortable(boolean pblnSortable);
    public void setIsCopy(boolean pblnCopy);

    public String getDetail(Object pobjValue) throws JspException;
    public String getDetail(Object pobjValue,int recordPos,boolean lightOn) throws JspException; 
    public String getEmptyTDDetail() throws JspException;
    public String getEmptyTDDetail(Object pobjValue) throws JspException;    
    public String getEmptyTDDetail(int recordPos,boolean lightOn) throws JspException; 
    public String getHeader() throws JspException;
    public String getBlank() throws JspException;

    public void copyAttributesTo(IColumnTag pobjDest);
}
