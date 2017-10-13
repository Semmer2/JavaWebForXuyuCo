package com.jl.tag.datagrid;

import javax.servlet.jsp.JspException;

public final class DecodeColumn extends AColumnTag
{
	private static final long serialVersionUID = -6868221442424930015L;

	private String mstrDecodeValues;
    private String mstrDisplayValues;
    private String mstrValueSeperator;

    public DecodeColumn()
    {
        super();
    }

    public String getDecodeValues()
    {
        return this.mstrDecodeValues;
    }

    public String getDisplayValues()
    {
        return this.mstrDisplayValues;
    }

    public String getValueSeperator()
    {
        return this.mstrValueSeperator;
    }

    public void setDecodeValues(String mstrValues)
    {
        this.mstrDecodeValues = mstrValues;
    }

    public void setDisplayValues(String mstrValues)
    {
        this.mstrDisplayValues = mstrValues;
    }

    public void setValueSeperator(String pstrSeperator)
    {
        this.mstrValueSeperator = pstrSeperator;
    }

    private boolean checkValues()
    {
        boolean  blnRet     = false;
        String[] arrDecode  = null;
        String[] arrDisplay = null;
        arrDecode = this.mstrDecodeValues.split(this.mstrValueSeperator);
        arrDisplay = this.mstrDisplayValues.split(this.mstrValueSeperator);
        if(arrDecode.length == arrDisplay.length || arrDecode.length < arrDisplay.length) blnRet = true;
        arrDecode = null;
        arrDisplay = null;
        return blnRet;
    }
    
    public int doStartTag() throws JspException
    {
    	if(!(this.getParent() instanceof DBGrid))
            throw new JspException("Error: Column tag needs to be a child of DBGrid/ProjectFileGrid!");
        if(!checkValues())
            throw new JspException("Error: For every decode value a display value must be specified!");
        return SKIP_BODY;
    }
    
    public int doEndTag() throws JspException
    {
    	DecodeColumn obj = new DecodeColumn();
        obj.setDecodeValues(this.mstrDecodeValues);
        obj.setDisplayValues(this.mstrDisplayValues);
        obj.setValueSeperator(this.mstrValueSeperator);
    	int ret = doEndTag(obj);
    	if(obj != null) obj = null;
    	return ret;
    }
    
    private String formatField(Object pobjVal) throws ClassCastException
    {
        int    intCnt = 0;
        String strRet = null;
        String[] arrDecode  = null;
        String[] arrDisplay = null;
        arrDecode = this.mstrDecodeValues.split(this.mstrValueSeperator);
        arrDisplay = this.mstrDisplayValues.split(this.mstrValueSeperator);
        for (intCnt = 0; intCnt < arrDecode.length; intCnt++)
        {
            if (pobjVal.equals(arrDecode[intCnt]))
            {
                strRet = arrDisplay[intCnt];
                break;
            }
            else if (pobjVal.toString().equals(arrDecode[intCnt]))
            {
                strRet = arrDisplay[intCnt];
                break;
            }
        }
        if (arrDecode != null) arrDecode = null;
        if (arrDisplay != null) arrDisplay = null;
        if (strRet == null) strRet = DBGrid.DEFAULT_NULLTEXT;
        return strRet;
    }
    
    public String getDetail(Object pobjValue) throws JspException
    {
        String s = "<td" + getBase(true) + ">";
        s +=  formatField(pobjValue);
        s += "</td>";
        return s;     
    }

	public String getEmptyTDDetail() throws JspException
	{
		return getDetail("&value");
	}
}
