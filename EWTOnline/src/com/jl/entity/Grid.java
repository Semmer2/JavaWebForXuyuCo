package com.jl.entity;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;

import java.util.Hashtable;
import com.jl.entity.GridColumn;

public class Grid
{
	private String mstrSpanID;
    private String mstrPosition;
    private String mstrWidth;   
	private String mstrHeight;
   
	private String mstrTBName;
    private String mstrTBClass;
    private String mstrBorder; 
    
    private String mstrTRClass;
    private boolean mblnDblclk;
    private String mstrTitle;
    protected String mstrRedField; //根据列非空判断为红色
    protected String mstrBlueField; //根据列非空判断为蓝色
    protected String mstrBoldField; //根据列非空判断为加粗
    
    private String mstrBgColor;
    private String mstrColor;
    private String mstrCellSpacing;
    private String mstrCellPadding;
    
    private String mstrPrevImg;
    private String mstrNextImg;    
	 
	private List mlstColumns;

	public Grid()
	{		
		mstrSpanID = "tC";
	    mstrPosition = "relative";
	    mstrWidth = null;   
		mstrHeight = null;			
	   
		mstrTBName = null;
	    mstrTBClass = null;
	    mstrBorder = null; 
	    
	    mstrTRClass = null;
	    mblnDblclk = false;
	    mstrTitle = null;
	    mstrRedField = null;
	    mstrBlueField = null;
	    mstrBoldField = null;
	    
	    mstrBgColor = null;
	    mstrColor = null;
	    mstrCellSpacing = null;
	    mstrCellPadding = null;
	    
	    mstrPrevImg = null; //"../images/datagrid/Previous.gif";
		mstrNextImg = null; //"../images/datagrid/Next.gif";	    
			    
		mlstColumns = null;
	}
	
	 public void Set(String s)
	 {
		int i, k;
		String s1, s2;
		String[] ar = s.split(";");
		for(i=0; i<ar.length; i++)
		{
			k = ar[i].indexOf("=");
			if(k < 1) continue;
			s1 = ar[i].substring(0,k).trim();
			if(s1.isEmpty()) continue;
			s2 = ar[i].substring(k+1).trim();
			if(s2.isEmpty())continue;
				
			if(s1.equalsIgnoreCase("SpanID")) mstrSpanID = s2;
			else if(s1.equalsIgnoreCase("Position")) mstrPosition = s2;
			else if(s1.equalsIgnoreCase("Width")) mstrWidth = s2;
			else if(s1.equalsIgnoreCase("Height")) mstrHeight = s2;
			else if(s1.equalsIgnoreCase("TBName")) mstrTBName = s2;
			else if(s1.equalsIgnoreCase("TBClass")) mstrTBClass = s2;
			else if(s1.equalsIgnoreCase("Border")) mstrBorder = s2;
			else if(s1.equalsIgnoreCase("TRClass")) mstrTRClass = s2;
			else if(s1.equalsIgnoreCase("Title")) mstrTitle = s2;
			else if(s1.equalsIgnoreCase("BgColor")) mstrBgColor = s2;
			else if(s1.equalsIgnoreCase("Color")) mstrColor = s2;
			else if(s1.equalsIgnoreCase("CellSpacing")) mstrCellSpacing = s2;
			else if(s1.equalsIgnoreCase("CellPadding")) mstrCellPadding = s2;
			else if(s1.equalsIgnoreCase("PrevImg")) mstrPrevImg = s2;
			else if(s1.equalsIgnoreCase("NextImg")) mstrNextImg = s2;
			else if(s1.equalsIgnoreCase("Dblclk")) mblnDblclk = s2.equalsIgnoreCase("true") ? true : false;
			else if(s1.equalsIgnoreCase("RedField")) mstrRedField = s2.toLowerCase();
			else if(s1.equalsIgnoreCase("BlueField")) mstrBlueField = s2.toLowerCase();
			else if(s1.equalsIgnoreCase("BoldField")) mstrBoldField = s2.toLowerCase();
	    }
		ar = null;
	 }
	 
	 public void addColumn(String s)
	 {
		 if(mlstColumns == null) mlstColumns = new ArrayList();
		 GridColumn col = new GridColumn();
		 col.SetTD(s);
		 mlstColumns.add(col);
	 }
	 
	 public String getTRColor(Hashtable td)
	 {
		String cs, s = "";
		if(mstrBoldField != null)
	    {
	    	cs = td.get(mstrBoldField).toString().trim();
	    	if(!cs.isEmpty())
	    	{
	    		if(s.isEmpty()) s = " style='";
	    		else s += ";";
	    		s += "font-weight:bold";
	    	}	    	
	    }
		if(mstrBlueField != null)
	    {
	    	cs = td.get(mstrBlueField).toString().trim();
	    	if(!cs.isEmpty())
	    	{
	    		if(s.isEmpty()) s = " style='";
	    		else s += ";";
	    		s += "color:blue";
	    	}	    	
	    }
	    if(mstrRedField != null)
	    {
	    	cs = td.get(mstrRedField).toString().trim();
	    	if(!cs.isEmpty())
	    	{
	    		if(s.isEmpty()) s = " style='";
	    		else s += ";";
	    		s += "color:red";
	    	}	    	
	    }	    
	    if(!s.isEmpty()) s += "'";
	    return s;
	 }
	
	public void draw(PageContext pageObject, List ls, boolean showHead, int curPage, int nPageSize, int totalSize) 
	{
		JspWriter objOut = pageObject.getOut();
		Iterator iterCol = null;
		GridColumn objCol = null;
		Hashtable ht = null;
		String s, cs;
		int i, n;
				
		try
		{
			
			// ------------------ draw span ------------------
				
			s = "<span";
			if(mstrSpanID != null) s += " id='" + mstrSpanID +"'";
			s += " align='center' style='width:";
			if("absolute".equals(mstrPosition)) s += (mstrWidth == null ? "0" : mstrWidth) + "px";
			else s += "100%";
			if(mstrHeight != null) s += ";height:" + mstrHeight + "px";
			if(mstrPosition != null) s += ";gridPosition:" + mstrPosition;
			s += ";overflow:auto;z-index:99;";			
			n = 0;
			for(iterCol=mlstColumns.iterator(); iterCol.hasNext();)
			{
				objCol = (GridColumn)iterCol.next();
				n += objCol.getWidth();
			}
			iterCol = null;
			objCol = null;
			i = 0;	if(mstrHeight != null) i = Integer.parseInt(mstrHeight);
			if(n > 100)	{ if(i > 100) s += "hidden"; }
			else if(i > 0) s += "margin-right:0";
			else s += "hidden";
			s += "' class='div_scrollbar'>";
			objOut.println(s);
				
			// ------------------ draw table ------------------
				
			s = "<table align='left'";
			if(mstrBorder != null) s += " border='" + mstrBorder + "'";;
			if(mstrTBClass != null) s += " class='" + mstrTBClass + "'";;
			s += " width='";
			if("relative".equals(mstrPosition)) 
			{
				if(mstrWidth!=null && Integer.parseInt(mstrWidth) > 100) s += mstrWidth + "px'";
				else s += "100%'";
			}
			else s += mstrWidth + "px'";
			if(mstrCellSpacing != null) s += " cellspacing='" + mstrCellSpacing + "'";
			if(mstrCellPadding != null) s += " cellpadding='" + mstrCellPadding + "'";
			if(mstrTBName != null) s += " id='" + mstrTBName + "'";
			if(mstrColor != null) s += " color='" + mstrColor + "'";
			if(mstrBgColor != null) s += " bgcolor='" + mstrBgColor + "'";
			s += ">";
			objOut.println(s);
				
			// ------------------ draw head ------------------
				
			if(showHead)
			{
				objOut.println("<thead>");
				s = "<tr onselectstart='return false' style=\"position:relative;top:expression(document.getElementById('tC').scrollTop)\">";
				objOut.println(s);
				if(mlstColumns != null)
				{
					for(iterCol=mlstColumns.iterator(); iterCol.hasNext();)
					{
						objCol = (GridColumn)iterCol.next();
						objOut.println(objCol.getHD());
					}
				}
				objCol = null;
				iterCol = null;
				objOut.println("</tr>");
				objOut.println("</thead>");
			}			
			objOut.println("<tbody id='tablebody'>");
				
			// ------------------ draw TD ------------------
			if(ls != null && mlstColumns != null)
			{
				cs = "<tr";
				if(mstrTRClass != null) cs += " class='" + mstrTRClass + "'";
				cs += " onclick='lightOn(this)'";
				if(mblnDblclk) cs += " ondblclick='OnDblclkGrid(this)'";
				if(mstrTitle != null) cs += " title='" + mstrTitle + "'";			
				for(i=0; i<ls.size(); i++)
				{
					ht = (Hashtable)ls.get(i);
					s = cs + getTRColor(ht) + ">";
					for(iterCol=mlstColumns.iterator(); iterCol.hasNext();)
					{
						objCol = (GridColumn)iterCol.next();
						s += objCol.getTD(ht, (!showHead && i==0),i+1);
					}
					objCol = null;
					iterCol = null;
					ht = null;
					s += "</tr>";					
					objOut.println(s);
				}
			}
				
			objOut.println("</tbody>");
			objOut.println("</table>");
			objOut.println("</span>");
				
			// ------------------ draw Bottom ------------------
				
			if(curPage >= 0)
			{
				if(curPage == 0) curPage = 1;
		        n = totalSize / nPageSize;
		        if((totalSize % nPageSize) != 0) n++;        
		        s = "<table border=0 cellspacing=0 cellpadding=0 id='bottomtable'";
		        if(mstrWidth != null) s += " width='" + mstrWidth + "'";
		        s += "<tr><td class='gridBL'>";
		        s += "<b>Total:</b>&nbsp;" + totalSize;
		        if(n > 1)
		        {
		        	s += "&nbsp;&nbsp;&nbsp;<b>Page:</b>&nbsp;" + n;
		        	s += "&nbsp;&nbsp;&nbsp;<a href='javascript:doNavigate(1," + n + ")' style='text-decoration:none' title='Prev Page'><span style=font-family:Verdana;color:#555555;font-size:12pt'><b>&lt;</b></span></a>";
		        	//s += "<img border=0 src='" + mstrPrevImg + "' style='cursor:pointer' onclick='doNavigate(1," + n + ")'/>";
		            //s += "<img border=0 src='" + mstrNextImg + "' style='cursor:pointer' onclick='doNavigate(2," + n + ")'/>";
		            s += "&nbsp;<select style='width:30pt' onChange='getSelValue(this)'>";
		            for (i=1; i<=n; i++)
		            {
		            	s += "<option value='" + i + "'";
		            	if(i == curPage)s += " selected='selected'";
		            	s += ">" + i + "</option>";
		            }
		            s += "</select>&nbsp;";
		            //<a href='javascript:OnClose()' style='text-decoration:none'><font face='Verdana'><b>&gt;</b></font></a>/&nbsp;";
		            //s += n + "&nbsp;Pages&nbsp;&nbsp";
		            s += "<a href='javascript:doNavigate(2," + n + ")' style='text-decoration:none' title='Next Page'><span style=font-family:Verdana;color:#555555;font-size:12pt'><b>&gt;</b></span></a>";
		            s += "<input type='hidden' name='curpage' value='" + curPage + "'>";
		            s += "<input type='hidden' name='totalsize' value='" + totalSize + "'>";
		        }
		        s += "</td>";
		        s += "<td id='mybottomtext' class='gridBR'>&nbsp;</td></tr></table>";
		        objOut.println(s);
			}
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			//throw new JspException("Error: Unknown error occured!", ex);
		}
		finally
		{
			if(mlstColumns != null) { mlstColumns.clear(); mlstColumns = null; }
			if(objOut != null) objOut = null;
			if(iterCol != null) iterCol = null;
			if(objCol != null) objCol = null;
			if(ht != null) ht = null;			
		}
	}
}