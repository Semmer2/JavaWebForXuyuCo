package com.jl.entity;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;


public class GridColumn
{	
    protected String mstrWidth;    //宽度   
	protected String mstrBgColor;  //背景色
    protected String mstrColor;    //文本颜色
    protected String mstrHAlign; 
    protected String mstrVAlign;
    
    protected String mstrHead;      //标题
    protected String mstrHDTitle;   //标题提示
    protected String mstrHDClass;  //标题类名称
    protected boolean mblnSort;    //是否允许排序
    
    protected String mstrDataField; //内容对应数据项
    protected String mstrTagField;  //tag对应数据项    
    protected String mstrTDTitle;    //内容提示
    protected String mstrTDClass;  //内容类名称
    
  
    public GridColumn()
    {
    	mblnSort = false;
        mstrWidth = null;
    	mstrBgColor = null;
        mstrColor = null;
        mstrHDClass = null;
        mstrTDClass = null;
        mstrHAlign = null;
        mstrVAlign = null;
        mstrHead = null;
        mstrDataField = null;
        mstrTagField = null;
        mstrHDTitle = null;
        mstrTDTitle = null;
    }
 
    public int getWidth()
    {
    	return mstrWidth == null ? 0 : Integer.parseInt(mstrWidth);
    }

    public void SetTD(String std)
    {
		int i, k;
		String s1, s2;
		String[] ar = std.split(";");
		for(i=0; i<ar.length; i++)
		{
			k = ar[i].indexOf("=");
			if(k < 1) continue;
			s1 = ar[i].substring(0,k).trim();
			if(s1.isEmpty()) continue;
			s2 = ar[i].substring(k+1).trim();
			if(s2.isEmpty())
			{
				if(s1.equalsIgnoreCase("TDTitle")) mstrTDTitle = s2;
				continue;
			}
			
			if(s1.equalsIgnoreCase("Width")) mstrWidth = s2;
			else if(s1.equalsIgnoreCase("Color")) mstrColor = s2;
			else if(s1.equalsIgnoreCase("BgColor")) mstrBgColor = s2;
			else if(s1.equalsIgnoreCase("HAlign")) mstrHAlign = s2;
			else if(s1.equalsIgnoreCase("VAlign")) mstrVAlign = s2;
			else if(s1.equalsIgnoreCase("Head")) mstrHead = s2;
			else if(s1.equalsIgnoreCase("HDTitle")) mstrHDTitle = s2;
			else if(s1.equalsIgnoreCase("HDClass")) mstrHDClass = s2;
			else if(s1.equalsIgnoreCase("DataField")) mstrDataField = s2.toLowerCase();
			else if(s1.equalsIgnoreCase("TagField")) mstrTagField = s2.toLowerCase();
			else if(s1.equalsIgnoreCase("TDTitle")) mstrTDTitle = s2;
			else if(s1.equalsIgnoreCase("TDClass")) mstrTDClass = s2;
			else if(s1.equalsIgnoreCase("Sort")) mblnSort = s2.equalsIgnoreCase("true") ? true : false;
		}
		ar = null;
    }
    
    public String getTD(Hashtable td, boolean bHead, int nRow)
	{
    	String s = "<td", cs = null;
    	if(mstrDataField != null)
    	{
    		cs = td.get(mstrDataField).toString();
    		cs.trim();
    	}    	
		if(mstrTDClass != null) s += " class='" + mstrTDClass + "'";
		if(bHead && mstrWidth != null) s += " width='" + mstrWidth + "%'";
		if(mstrHAlign != null) s += " align='" + mstrHAlign + "'";
		if(mstrVAlign != null) s += " valign='" + mstrVAlign + "'";
		if(mstrColor != null) s += " color='" + mstrColor + "'";
		if(mstrBgColor != null) s += " bgcolor='" + mstrBgColor + "'";
		if(mstrTDTitle != null)
		{
			if(!mstrTDTitle.isEmpty()) s += " title='" + mstrHDTitle + "'";
			else if(cs !=null && !cs.isEmpty()) s += " title='" + cs + "'";			
		}
		if(mstrTagField != null) s += " tag='" +td.get(mstrTagField).toString() + "'";
		if(cs == null) cs = String.valueOf(nRow);
		else if(cs.isEmpty()) cs = "&nbsp;";
		s += ">" +  cs + "</td>";		
		return s;
	}
    
    public String getHD()
    {
		String s = "<td";
		if(mstrHDClass != null) s += " class='" + mstrHDClass + "'";
		if(mstrWidth != null) s += " width='" + mstrWidth + "%'";
		if(mstrHAlign != null) s += " align='center'";
		if(mstrVAlign != null) s += " valign='" + mstrVAlign + "'";
		if(mstrColor != null) s += " color='" + mstrColor + "'";
		if(mstrBgColor != null) s += " bgcolor='" + mstrBgColor + "'";
		if(mstrHDTitle != null) s += " title='" + mstrHDTitle + "'";
		if(mblnSort) s += " style='cursor:hand' onclick='sortColumn(this)'";
		s += ">" + ((mstrHead == null || mstrHead.isEmpty()) ? "&nbsp;" : mstrHead);
		s += "</td>";
		return s;
    }    
}
