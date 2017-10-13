package com.jl.tag.datagrid;

import javax.servlet.jsp.JspException;


public final class AnchorColumn extends AColumnTag
{
	private static final long serialVersionUID = -3201594724708690415L;

	private String mstrLinkText;
    private String mstrLinkUrl;
    private String mstrTarget;
    private String mstrDataFormat;
    private String mstrBindDataField;
    private String linkUrlable;
    private String isTatalable;

 	public AnchorColumn()
    {
        super();
    }

    public String getLinkText()
    {
        return this.mstrLinkText;
    }

    public String getLinkUrlable()
    {
        return this.linkUrlable;
    }
    
    public String getLinkUrl()
    {
        return this.mstrLinkUrl;
    }

    public String getTarget()
    {
        return this.mstrTarget;
    }

    public String getDataFormat()
    {
        return this.mstrDataFormat;
    }

    public String getBindDataField()
    {
        return this.mstrBindDataField;
    }

	public void setLinkUrlable(String linkUrlable)
	{
		this.linkUrlable = linkUrlable;
	} 
 
    public void setLinkText(String pstrLinkText)
    {
        this.mstrLinkText = pstrLinkText;
    }

    public void setLinkUrl(String pstrLinkUrl)
    {
        this.mstrLinkUrl = pstrLinkUrl;
    }

    public void setTarget(String pstrTarget)
    {
        this.mstrTarget = pstrTarget;
    }

    public void setDataFormat(String pstrDataFormat)
    {
        this.mstrDataFormat = pstrDataFormat;
    }

    public void setBindDataField(String pstrBindDataField)
    {
        this.mstrBindDataField = pstrBindDataField;
    }

    public void setIsTatalable(String isTatalable)
    {
        this.isTatalable = isTatalable;
    }

    public String getIsTatalable()
    {
        return isTatalable;
    }
    
    private String resolveFields(String pstrUrl)
    {
        int    intPos = 0;
        int    intEnd = 0;
        String strCol = null;
        String strRet = null;
        if((this.getParent() instanceof DBGrid))
        {
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
	        if (this.mstrBindDataField != null && !this.mstrBindDataField.equals(""))
	        {
	            String bindDateFields[] = this.mstrBindDataField.split(",");
	            String param = "";
	            for (int i=0; i< bindDateFields.length; i++){
	                String bf = bindDateFields[i];
	                intPos = bf.indexOf("=");
	                if (i>0)
	                    param = param + "&" + bf.substring(1,intPos) + "=" + objTmp.getColumnValue(bf.substring(intPos+1,bf.length()-1));
	                else
	                    param = bf.substring(1,intPos)+ "=" + objTmp.getColumnValue(bf.substring(intPos+1,bf.length()-1));
	            }
	            strRet = strRet + "?" +param;
	        }
	        if (objTmp != null) objTmp = null;
	    }
        
        return strRet;
    }
    
    public int doEndTag() throws JspException
    {
    	AnchorColumn obj = new AnchorColumn();
    	obj.setDataFormat(this.mstrDataFormat);
    	obj.setLinkUrlable(this.linkUrlable);
    	obj.setLinkText(this.mstrLinkText);
    	obj.setLinkUrl(this.mstrLinkUrl);
    	obj.setTarget(this.mstrTarget);
    	obj.setIsTatalable(this.isTatalable);
    	obj.setBindDataField(this.mstrBindDataField);
    	int ret = doEndTag(obj);
    	if(obj != null) obj = null;
    	return ret;
    }

    public String getDetail(Object pobjValue) throws JspException
    {
    	int mode = (this.isTatalable!=null && this.isTatalable.equals("yes")) ? 4 : 3;
        String s = "<td" + getBase(true);
        s += " iscopy='" + (this.mblnCopy ? "true" : "false");
        s += "'>";
        if(this.linkUrlable!=null && this.linkUrlable.equals("no"))
        {
        	if(this.mstrLinkText != null && !this.mstrLinkText.equals("")) s += this.mstrLinkText;
            else s += formatField(pobjValue, this.mstrDataFormat, mode);
        }
        else
        {
            s += "<a href='#' onclick='" + resolveFields(this.mstrLinkUrl) + "'";
            if(this.mstrTarget != null) s += " target='" + (this.mstrTarget.equals("3") ? "_self" : this.mstrTarget) + "'";
            if(pobjValue instanceof ComplexField)
            {                	                    
            	Object d = ((ComplexField)pobjValue).getTagfield();
            	String cs = formatField(d, this.mstrDataFormat, mode);
            	if(cs == null) cs = "";
            	cs = cs.trim();
            	if(!cs.isEmpty()) s += " tag='" + cs + "'";
            	d = null;
            }
            s += ">";
            if (this.mstrLinkText != null && !this.mstrLinkText.equals("")) s += this.mstrLinkText;
            else s += formatField(pobjValue, this.mstrDataFormat, mode);
            s += "</a>";
        }
        s += "</td>";
        return s;
    }

	public String getEmptyTDDetail() throws JspException
	{
		return getDetail("&value");
	}
}
