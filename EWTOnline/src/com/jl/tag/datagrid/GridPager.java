/*
 * PACKAGE: com.tellhow.gridtag
 * FILE   : GridPager.java
 * CREATED: 2006-4-20
 * AUTHOR : 鍛ㄤ竾浣�
 *------------------------------------------------------------------------------
 * Change Log:
 *-----------------------------------------------------------------------------*/
package com.jl.tag.datagrid;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;
import javax.servlet.jsp.PageContext;

public final class GridPager extends TagSupport
{
    private static final long serialVersionUID = 6100864455702214641L;

    private String mstrImgFirst = null;
    private String mstrImgPrevious = null;
    private String mstrImgNext = null;
    private String mstrImgLast = null;
    private String mstrImgBackground = null;
    private String mstrIsPrintAble = null;

    public GridPager()
    {
        super();
    }

    public String getImgFirst()
    {
        return this.mstrImgFirst;
    }

    public String getImgPrevious()
    {
        return this.mstrImgPrevious;
    }

    public String getImgNext()
    {
        return this.mstrImgNext;
    }

    public String getImgLast()
    {
        return this.mstrImgLast;
    }

    public String getImgBackground()
    {
        return this.mstrImgBackground;
    }

    public void setImgFirst(String pstrImgFirst)
    {
        this.mstrImgFirst = pstrImgFirst;
    }

    public void setImgPrevious(String pstrImgPreviosu)
    {
        this.mstrImgPrevious = pstrImgPreviosu;
    }

    public void setImgNext(String pstrImgNext)
    {
        this.mstrImgNext = pstrImgNext;
    }

    public void setImgLast(String pstrImgLast)
    {
        this.mstrImgLast = pstrImgLast;
    }

    public void setImgBackground(String pstrImgBackground)
    {
        this.mstrImgBackground = pstrImgBackground;
    }

    public int doEndTag() throws JspException
    {
        try
        {           
            if ((this.getParent() instanceof DBGrid))
            {
            	DBGrid objTmp = null;
            	objTmp=(DBGrid)this.getParent();
            	objTmp.setPager(this);
            	if(objTmp != null) objTmp = null;
            }                        
        }
        catch (ClassCastException CCEx)
        {
            throw new JspException("Error: ImageColumn tag is not a child of DBGrid", CCEx);
        }
        return EVAL_PAGE;
    }

     public int doStartTag() throws JspException
    {
        if (!(this.getParent() instanceof DBGrid))
            throw new JspException("Error: Column tag needs to be a child of DBGrid/ProjectFileGrid!");
        return SKIP_BODY;
    }

    public String getPager(int curPage, int allRec, int pageSize, String tableWidth) throws JspException
    {
        int n = 0;
        n = allRec / pageSize;
        if((allRec % pageSize) != 0) n++;
        //pageSize = i;

        String s = "<table border=0 cellspacing=0 cellpadding=0 id='bottomtable' width='";
        s += tableWidth + "'>";
        s += "<tr><td class='gridBL'>";
        if(n > 1)
        {
        	s += "&nbsp;<a href=\"javascript:doNavigate('P'," + n + ")\">";
        	s += "<img border=0 src='" + this.mstrImgPrevious + "'></a>";
            s += "<a href=\"javascript:doNavigate('N'," + n + ")\">";
            s += "<img border=0 src='" + this.mstrImgNext + "'></a>&nbsp;To&nbsp;";
            s += "<select name='txtCurr' style='width:30pt' onChange='getSelValue()'>";
            for (int i=1; i<=n; i++)
            {
            	s += "<option value='" + i + "'";
            	if(i==curPage)s += " selected='selected'";
            	s += ">" + i + "</option>";
            }
            s += "</select>&nbsp;/&nbsp;";
            s += n + "&nbsp;Pages&nbsp;&nbsp";

            s += "<input type='hidden' name='currpage' value='" + curPage + "'>";
			//objOut.println("<input TYPE='hidden' NAME='isPrintStatus' id='isPrintStatus' value=''>");
			//objOut.println("<input TYPE='hidden' NAME='DBRecordNum' id='DBRecordNum' value='"+intTotRec+"'>");
        }
        s += "&nbsp;Total:&nbsp;" + allRec + "</td>";
        s += "<td id='mybottext' class='gridBR'>&nbsp;</td></tr></table>";
        return s;
    }

    private GridPager getCopy()
    {
        GridPager objRet = null;

        objRet = new GridPager();
        objRet.setId(this.getId());
        objRet.setPageContext(this.pageContext);
        objRet.setParent(this.getParent());
        objRet.setImgFirst(this.mstrImgFirst);
        objRet.setImgPrevious(this.mstrImgPrevious);
        objRet.setImgNext(this.mstrImgNext);
        objRet.setImgLast(this.mstrImgLast);
        objRet.setImgBackground(this.mstrImgBackground);
        objRet.setIsPrintAble(this.mstrIsPrintAble);
        return objRet;
    }

    public void setIsPrintAble(String IsPrintAble)
    {
        this.mstrIsPrintAble = IsPrintAble;
    }

    public String getIsPrintAble()
    {
        return mstrIsPrintAble;
    }
}
