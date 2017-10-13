package com.jl.tag.datagrid;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.BodyTagSupport;

import org.apache.commons.beanutils.PropertyUtils;
import java.util.Hashtable;
import com.jl.dao.*;
import com.jl.dao.base.impl.*;
import com.jl.util.StringUtils;

public final class DBGrid extends BodyTagSupport
{
	private static final long serialVersionUID = -1092966441298785603L;
	public static final String DEFAULT_NULLTEXT = "";

	private int mintBorder = 0;
	private int mintCellPadding = 0;
	private int mintCellSpacing = 0;
	private int mintWidth = 100;
	private int mintPageSize = 10;
	
	private int mintCurrPage = 1;
	private int mintTotalRecords = -1;
	private int mintVerticalHeight = 450; // 垂直高度
	private String gridPosition = "relative"; // div的排列方式
	private int mintDataWidth = 600;// 在position=absolute方式时,数据区的宽度
	private String mstrViewDetail = "";// 详细页面
	private String mstrViewWidth = "";// 详细页面宽度
	private String mstrViewHeight = "";// 详细页面高度
	private PageContext pageObject;// 上下文件页面对象
	private HttpServletRequest pageRequest;// 上下文件页面对象

	private String onRowSelected;//
	private String showHead;
	
	private String mstrprintStatus = null;
	private String mstrdefineHeaderStr = null;
	private String mstrCssClass = null;
	private String mstrBgColor = null;
	private String mstrForeColor = null;
	private String mstrID = null;
	private String mstrName = null;
	private String mstrDataMember = null;
	private String mstrHeaderCss = null;
	private boolean mblLightOn = false;
	private boolean mbtdIntervalColor = true;

	private List mlstColumns = null;
	private List mlstColumnHtmls=null;
	private GridPager mobjPager = null;
	private GridSorter mobjSorter = null;
	private Object mobjDataSource = null;
	private List mobjLS = null;
	private Object mCurrItem = null;
	private DBASession session = null;
	
	private String[] tdhtmlarray=null;

	public DBGrid()
	{
		super();
		this.mintPageSize = 10;
		this.mintBorder = 0;
		this.mintCellPadding = 0;
		this.mintCellSpacing = 0;
		this.mintWidth = 100;
		this.mintCurrPage = 1;
		mlstColumns = new ArrayList();
		mlstColumnHtmls=new ArrayList();
	}

	public String getGridPosition()
	{
		return gridPosition;
	}

	public void setGridPosition(String pos)
	{
		gridPosition = pos;
	}
	
	public String getViewDetail()
	{
		return mstrViewDetail;
	}
	
	public String getOnRowSelected()
	{
		return onRowSelected;
	}

	public void setOnRowSelected(String s)
	{
		onRowSelected = s;
	}
	
	public String getShowHead()
	{
		return showHead;
	}

	public void setShowHead(String s)
	{
		this.showHead = s;
	}

	public String getViewWidth()
	{
		return this.mstrViewWidth;
	}
	
	public String getViewHeight()
	{
		return this.mstrViewHeight;
	}

	public int getDataWidth()
	{
		return this.mintDataWidth;
	}

	public String getPrintStatus()
	{
		return this.mstrprintStatus;
	}

	public String getDefineHeaderStr()
	{
		return this.mstrdefineHeaderStr;
	}

	public int getVerticalHeight()
	{
		return this.mintVerticalHeight;
	}

	public String getHeaderCss()
	{
		return this.mstrHeaderCss;
	}

	public boolean getTdIntervalColor()
	{
		return this.mbtdIntervalColor;
	}

	public boolean getLightOn()
	{
		return this.mblLightOn;
	}

	public int getWidth()
	{
		return this.mintWidth;
	}

	public int getBorder()
	{
		return this.mintBorder;
	}

	public int getCellSpacing()
	{
		return this.mintCellSpacing;
	}

	public int getCellPadding()
	{
		return this.mintCellPadding;
	}

	public String getBgColor()
	{
		return this.mstrBgColor;
	}

	public String getForeColor()
	{
		return this.mstrForeColor;
	}

	public String getID()
	{
		return this.mstrID;
	}

	public String getName()
	{
		return this.mstrName;
	}

	public Object getDataSource()
	{
		return this.mobjDataSource;
	}

	public String getDataMember()
	{
		return this.mstrDataMember;
	}

	public int getPageSize()
	{
		return this.mintPageSize;
	}

	public String getCssClass()
	{
		return this.mstrCssClass;
	}

	public String getSortColumn()
	{
		if(this.mobjSorter != null) return this.mobjSorter.getSortColumn();
		return null;
	}

	public boolean getSortAscending()
	{
		if(this.mobjSorter != null) return this.mobjSorter.getSortAscending();
		return true;
	}

	public String getAscendingImage()
	{
		String strTmp = null;
		if(this.mobjSorter != null) strTmp = this.mobjSorter.getImageAscending();
		return (strTmp == null ? "images/ImgAsc.gif" : strTmp);
	}

	public String getDescendingImage()
	{
		String strTmp = null;
		if (this.mobjSorter != null) strTmp = this.mobjSorter.getImageDescending();
		return (strTmp == null ? "images/ImgDesc.gif" : strTmp);
	}

	public int getTotalRecords()
	{
		return this.mintTotalRecords;
	}

	public void setViewDetail(String viewdetail)
	{
		this.mstrViewDetail = viewdetail;
	}
	
	public void setViewWidth(String viewwidth)
	{
		this.mstrViewWidth = viewwidth;
	}
	
	public void setViewHeight(String viewheight)
	{
		this.mstrViewHeight = viewheight;
	}

	public void setDataWidth(int datawidth)
	{
		if(datawidth > 100) this.mintDataWidth = datawidth;
	}

	public void setPrintStatus(String printStatus)
	{
		this.mstrprintStatus = printStatus;
	}

	public void setDefineHeaderStr(String defineHeaderStr)
	{
		this.mstrdefineHeaderStr = defineHeaderStr;
	}

	public void setVerticalHeight(int verticalHeight)
	{
		this.mintVerticalHeight = verticalHeight;
	}

	public void setHeaderCss(String headerCss)
	{
		this.mstrHeaderCss = headerCss;
	}

	public void setTdIntervalColor(boolean tdIntervalColor)
	{
		this.mbtdIntervalColor = tdIntervalColor;
	}

	public void setLightOn(boolean lightOn)
	{
		this.mblLightOn = lightOn;
	}

	public void setWidth(int pintWidth)
	{
		if(pintWidth >= 0) this.mintWidth = pintWidth;
	}

	public void setBorder(int pintBorder)
	{
		if(pintBorder >= 0) this.mintBorder = pintBorder;
	}

	public void setCellSpacing(int pintCellSpacing)
	{
		if(pintCellSpacing >= 0) this.mintCellSpacing = pintCellSpacing;
	}

	public void setCellPadding(int pintCellPadding)
	{
		if(pintCellPadding >= 0) this.mintCellPadding = pintCellPadding;
	}

	public void setBgColor(String pstrBgColor)
	{
		this.mstrBgColor = pstrBgColor;
	}

	public void setForeColor(String pstrColor)
	{
		this.mstrForeColor = pstrColor;
	}

	public void setID(String pstrID)
	{
		this.mstrID = pstrID;
		//this.mstrName = pstrID;
	}

	public void setName(String pstrName)
	{
		this.mstrName = pstrName;
		//this.mstrID = pstrName;
	}

	public void setDataSource(Object pobjDataSrc) throws UnsupportedOperationException
	{
		if(pobjDataSrc != null && pobjDataSrc instanceof List) this.mobjDataSource = pobjDataSrc;
	}

	public void setDataMember(String pstrDataMember)
	{
		this.mstrDataMember = pstrDataMember;
	}

	public void setPageSize(int pintPageSize)
	{
		// if(pintPageSize >= 0) this.mintPageSize =20;
		this.mintPageSize = pintPageSize;
	}

	public void setCurrentPage(int pintCurrPage)
	{
		if(pintCurrPage >= 0) this.mintCurrPage = pintCurrPage;
	}

	public void setCssClass(String pstrCssClass)
	{
		this.mstrCssClass = pstrCssClass;
	}

	public void setTotalRecords(int pintTotRec)
	{
		this.mintTotalRecords = pintTotRec;
	}

	public int doEndTag() throws JspException
	{
		AColumnTag d[] = new AColumnTag[8];
		if(this.mlstColumns.size() == 0) throw new JspException("Error: No columns defined for the table!");

		drawGrid();
		String output=this.pageObject.getRequest().getParameter("output");
		if(output==null) output="";
		if(output.equalsIgnoreCase(""))
		{
			String s = "<table id='exampleemptytable' style='display:none'><tr class='gridNewRow' id=tdt ondblclick='ondblcellclick(this)' onclick='lightOn(this)'>";
			for(int i=0;i<this.mlstColumnHtmls.size();i++) s += this.mlstColumnHtmls.get(i).toString();				
			s += "</tr></table>";
			try
			{
				this.pageObject.getOut().println(s);
				datagridRelease();
			}
			catch (Exception ex) { throw new JspException(ex.getMessage());	}
		}
		return EVAL_PAGE;
	}

	public int doStartTag()
	{
		// return EVAL_BODY_INCLUDE;
		return this.EVAL_BODY_TAG;
	}

	public void datagridRelease() throws Exception
	{
		if(this.mobjLS != null) this.mobjLS = null;
		if(session != null) session.close();
		if(this.mobjLS != null) this.mobjLS = null;
		if(this.mlstColumns != null)
		{
			this.mlstColumns.clear();
			this.mlstColumnHtmls.clear();
		}
		if(this.mobjPager != null) this.mobjPager = null;
		super.release();
	}

	public boolean supportSorting()
	{
		if(this.mobjSorter == null) return false;
		return true;
	}

	public void setPager(GridPager pobjPgr)
	{
		this.mobjPager = pobjPgr;
	}

	public void setSorter(GridSorter pobjSort)
	{
		this.mobjSorter = pobjSort;
	}

	public void addColumn(IColumnTag objCol) throws JspException
	{
		String s = "";
		this.mlstColumns.add(objCol);
		if(objCol instanceof RowNumColumn)
		{
			RowNumColumn row = (RowNumColumn)objCol;
			s = row.getDataField();
			if(s.equalsIgnoreCase("")) s = row.getEmptyTDDetail(null,null,null);
			else s = row.getEmptyTDDetail(this.mstrViewDetail,this.mstrViewWidth,this.mstrViewHeight);	
		}
		else
		{
			if(objCol instanceof ImageColumn) s = objCol.getEmptyTDDetail();
			else
			{
				s = objCol.getDataField();
				if(s.equalsIgnoreCase("") == false)
				{
					if(objCol instanceof InputColumn) s = objCol.getEmptyTDDetail(0, this.getLightOn());
					else
					{
						s = objCol.getTagField();
						if(s == null || s.equalsIgnoreCase("")) s = objCol.getEmptyTDDetail();
						else
						{
							ComplexField cd = new ComplexField();
							s = objCol.getEmptyTDDetail(cd);
						}
					}
				}
			}
		}
		s = StringUtils.replaceString(s, "\"", "'");
		mlstColumnHtmls.add(s);
	}
	
	public Object getColumnValue(String pstrCol)
	{
		Object obj = null;
		try
		{
			if(pstrCol != null)
			{
				if(this.mobjDataSource!=null)
				{
					if(this.mobjDataSource instanceof List)
					{
						if (this.mCurrItem instanceof Hashtable) obj = ((Hashtable) this.mCurrItem).get(pstrCol);
						else obj = PropertyUtils.getProperty(this.mCurrItem,pstrCol);
					}
				}
				else
				{
					if (this.mobjLS instanceof List)
					{
						if(this.mCurrItem instanceof Hashtable) obj = ((Hashtable)this.mCurrItem).get(pstrCol);
						else obj = PropertyUtils.getProperty(this.mCurrItem,pstrCol);
					}
				}
			}
		}
		catch (IllegalAccessException IAEx) { IAEx.printStackTrace();}
		catch (InvocationTargetException ITargetEx) { ITargetEx.printStackTrace(); }
		catch (NoSuchMethodException NSMEx) { NSMEx.printStackTrace(); }
		if(obj == null)	obj = new String(DBGrid.DEFAULT_NULLTEXT);
		return obj;
	}

	private void drawGrid() throws JspException
	{
		int i=0, intStart=0, intTotRec=0;
		String s = null;
		JspWriter objOut = null;
		Iterator iterCol = null;
		IColumnTag objCol = null;
		
		intTotRec = findTotalRecords();
		objOut = this.pageObject.getOut();
		
		if(intTotRec > 0)
		{
			s = this.pageObject.getRequest().getParameter("currpage");
			this.mintCurrPage = (s!=null) ? Integer.parseInt(s) : 1;					
			if(this.pageObject.getRequest().getAttribute("pagesize")!=null) intStart=0;
			else
			{
				intStart = (this.mintCurrPage - 1) * this.mintPageSize;
				if(intStart >= intTotRec)
				{
					intStart = (intTotRec / this.mintPageSize);
					if((intTotRec % this.mintPageSize) == 0) intStart--;
					this.mintCurrPage = intStart;
					intStart = intStart * this.mintPageSize;						
				}
			}
		}
				
		try
		{
			//------------   drawTableStart ------------------
			
			s = "<span id='tC' align='center' style='width:";
			if(this.gridPosition.equals("absolute")) s += this.mintWidth + "px";
			else s += "100%";
			s += ";height:" + String.valueOf(this.mintVerticalHeight);
			s += "px;gridPosition:" + this.gridPosition;
			s += ";overflow:auto;z-index:99;";			
			if(this.getColsWidth() > 100)
			{
				if(this.mintVerticalHeight <= 0) s += "hidden";
			}
			else
			{
				if(this.mintVerticalHeight > 0) s += "margin-right:0";
				else s += "hidden";
			}
			s += "' class='div_scrollbar'>";
			objOut.println(s);
			
			s = "<table align='left' border='" + this.mintBorder + "'";
			if(this.mstrCssClass != null) s += " class='" + this.mstrCssClass + "'";;
			s += "' width='";
			if(this.gridPosition.equals("relative"))
			{
				if(this.mintWidth > 100) s += String.valueOf(this.mintWidth);
				else s += "100%";
			}
			else s += String.valueOf(this.mintWidth);
			s += "' cellspacing=" + String.valueOf(mintCellSpacing);
			s += " cellpadding=" + String.valueOf(mintCellPadding);
			if(this.mstrID != null) s += " id='" + String.valueOf(this.mstrID) + "'";
			if(this.mstrName != null) s += " name='" + this.mstrName + "'";
			if(this.mstrForeColor != null) s += " color='" + this.mstrForeColor + "'";
			if(this.mstrBgColor != null) s += " bgcolor='" + this.mstrBgColor + "'";
			s += ">";
			objOut.println(s);
			
			//------------   drawHeaderRow ------------------
			
			if(this.showHead==null || this.showHead.equalsIgnoreCase("true"))
			{
				objOut.println("<thead>");
				if(this.mstrdefineHeaderStr == null || this.mstrdefineHeaderStr.equals(""))
				{
					s = "<tr onselectstart='return false' style=\"position:relative;top:expression(document.getElementById('tC').scrollTop)\">";
					objOut.println(s);
					for(iterCol = this.mlstColumns.iterator(); iterCol.hasNext();)
					{
							objCol = (IColumnTag) iterCol.next();
							objOut.println(objCol.getHeader());
							objCol = null;
					}
					iterCol = null;
					objOut.println("</tr>");
				}
				else objOut.println(this.mstrdefineHeaderStr);
				objOut.println("</thead>");
			}			
			objOut.println("<tbody id='tablebody'>");
							
			//------------   drawTableRow ------------------
			
			if(intTotRec <= 0)
			{
				s = "<tr><td colspan=" + this.mlstColumns.size() + "></td></tr>";
				objOut.println(s);
			}
			else
			{
				if(this.mobjDataSource == null)
				{
					if(this.mstrprintStatus != null && this.mstrprintStatus.equals("allpage"))
						s = "select a.* from (" + this.mstrDataMember + ") a";
					else
						s = "select * from (select a.*,rownum rn from (" + this.mstrDataMember + ") a where rownum <=" + (intStart + this.mintPageSize) + ") where rn >" + intStart;
					if(session == null) session = SessionFactory.getSession(pageRequest);
					this.mobjLS = session.openSelectbyList(s);
					objOut.println("<tr>s</tr>");
				}

				if(this.mstrprintStatus != null && this.mstrprintStatus.equals("allpage"))
				{
					i = 0;
					while(true)
					{
						SetCurItem(i, i);			
						s = "<tr>";
						for(iterCol = this.mlstColumns.iterator(); iterCol.hasNext();)
						{
							objCol = (IColumnTag) iterCol.next();
							s += getCol(objCol, i);
							objCol = null;
						}
						iterCol = null;
						s += "</tr>";						
						objOut.println(s);						
						if(IsEndDrawTable(i, i)) break;
						i++;						
					}
				}
				else
				{
					String isLightOn = " onclick='lightOn(this)'";
					if(this.mblLightOn)	isLightOn +=  " ondblclick='ondblcellclick(this)'";
					if(this.onRowSelected!=null) isLightOn += " " + this.onRowSelected+";";
					Object ob = this.pageObject.getRequest().getAttribute("trtitle");
					if(ob != null) isLightOn += " title='" + ob.toString() + "'";
					
					for(i = 0; i < this.mintPageSize; i++)
					{
						SetCurItem(intStart+i, i);
						s = "<tr";
						if(this.mbtdIntervalColor == true) s += " class='gridRow" + ((i%2)==0 ? "Even'" : "Odd'");
						s += isLightOn + ">";
						for(iterCol=this.mlstColumns.iterator(); iterCol.hasNext();)
						{
							objCol = (IColumnTag) iterCol.next();
							s += getCol(objCol, i); //intStart+i);
							objCol = null;
						}
						iterCol = null;
						s += "</tr>";						
						objOut.println(s);
						if(IsEndDrawTable(intStart+i, i)) break;
					}					
				}			
			}
			
			///////////////////////////////////////
			
			objOut.println("</tbody>");
			objOut.println("</table>");
			objOut.println("</span>");
			if(this.mobjPager != null)
			{
				if(this.gridPosition.equals("absolute"))
					s = this.mobjPager.getPager(this.mintCurrPage, intTotRec, this.mintPageSize, this.mintWidth + "px");
				else
					s = this.mobjPager.getPager(this.mintCurrPage, intTotRec, this.mintPageSize, "100%");
				objOut.println(s);
			}
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			throw new JspException("Error: Unknown error occured!", ex);
		}
		finally
		{
			if(objOut != null) objOut = null;
			if(iterCol != null) iterCol = null;
			if(objCol != null) objCol = null;
			try	{ if(session != null) session.close();	}
			catch (Exception e)	{ e.printStackTrace(); }
		}
	}
	
	private void SetCurItem(int kData, int kJLS)
	{
		if(this.mobjDataSource!=null)
		{
			if(this.mobjDataSource instanceof List)
				this.mCurrItem = ((List) this.mobjDataSource).get(kData);
		}
		else
		{
			if(this.mobjLS instanceof List)
				this.mCurrItem = ((List) this.mobjLS).get(kJLS);
		}
	}
	
	private boolean IsEndDrawTable(int kData, int kJLS)
	{
		if(this.mobjDataSource != null)
		{					
			if((kData) == ((List) this.mobjDataSource).size() - 1) return true;
		}
		else
		{
			if((kJLS) == ((List) this.mobjLS).size() - 1) return true;
		}
		return false;
	}

	private String getCol(IColumnTag objCol, int k) throws JspException
	{
		String s;
		if(objCol instanceof TextColumn)
		{
			TextColumn row = (TextColumn)objCol;
			s = row.getTD(getColumnValue(row.getDataField()).toString(), getColumnValue(row.getTagField()).toString(), 
					this.showHead!=null && this.showHead.equalsIgnoreCase("false") && k==0
					);
			row = null;
		}
		else if(objCol instanceof RowNumColumn)
		{
			RowNumColumn row = (RowNumColumn)objCol;
			s = row.getDataField();
			if(s.equalsIgnoreCase(""))
				s = row.getTDDetail(new Integer(k+1), null, null, null, null);
			else
				s = row.getTDDetail(new Integer(k+1),this.getColumnValue(s),this.mstrViewDetail,this.mstrViewWidth,this.mstrViewHeight);		
			row = null;
		}
		else
		{
			if(objCol instanceof ImageColumn) s = objCol.getDetail("");
			else
			{
				s = objCol.getDataField();
				if(!s.equalsIgnoreCase(""))
				{
					if(objCol instanceof InputColumn)
						s = objCol.getDetail(this.getColumnValue(s), k, this.getLightOn());
					else
					{
						String tagFld = objCol.getTagField();
						if(tagFld==null || tagFld.equalsIgnoreCase(""))
							s = objCol.getDetail(this.getColumnValue(s));
						else
						{
							ComplexField cd = new ComplexField();
							cd.setDatafield(this.getColumnValue(s));
							cd.setTagfield(this.getColumnValue(tagFld));
							s = objCol.getDetail(cd);
						}
					}
				}
			}
		}
		//row = null;
		return s;
	}

	private int getColsWidth()
	{
		int closWidth = 0;
		IColumnTag objCol = null;
		Iterator iterCol = null;
		for(iterCol = this.mlstColumns.iterator(); iterCol.hasNext();)
		{
			objCol = (IColumnTag)iterCol.next();
			closWidth += objCol.getWidth();
		}
		iterCol = null;
		objCol = null;
		return closWidth;
	}

	public String genCountSQL()
	{		
		if(this.mstrDataMember != null) return "select count(1) as datacount from (" + this.mstrDataMember + ")";	
		else return null;
	}

	public int findTotalRecords()
	{
		if(this.mintTotalRecords == 0)
		{
			if(this.mobjDataSource instanceof List) mintTotalRecords = ((List) this.mobjDataSource).size();
			else
			{			
				if(session == null) session = SessionFactory.getSession(pageRequest);
				try
				{
					List ls = session.openSelectbyList(genCountSQL());				
					Hashtable ht=(Hashtable)ls.get(0);
					mintTotalRecords = Integer.valueOf((String)ht.get("datacount")).intValue();
				}
				catch (Exception ex) { ex.printStackTrace(); }
				finally { session = null; }
			}
		}
		return mintTotalRecords;
	}
	
	private void emptyRowsOut(int pintFrom) throws JspException
	{
		int i = 0;
		Iterator iterCol = null;
		IColumnTag objCol = null;
		JspWriter objOut = null;
		try
		{
			objOut = this.pageObject.getOut();
			for(i = pintFrom + 1; i < this.mintPageSize; i++)
			{
				if((i % 2) == 0)
					objOut.println("<tr CLASS=\"gridRowEven\">");
				else
					objOut.println("<tr CLASS=\"gridRowOdd\">");

				iterCol = null;
				for (iterCol = this.mlstColumns.iterator(); iterCol.hasNext();)
				{
					objCol = null;
					objCol = (IColumnTag) iterCol.next();
					objCol.getBlank();
				}
				objCol = null;
				objOut.println("</tr>");
			}
		}
		catch(IOException IoEx)
		{
			throw new JspException("Error: Writing empty rows!", IoEx);
		}
		finally
		{
			if(objOut != null) objOut = null;
			if(iterCol != null) iterCol = null;
			if(objCol != null)	objCol = null;
		}
	}

	public PageContext getPageObject()
	{
		return pageObject;
	}

	public void setPageObject(PageContext pageObject)
	{
		this.pageObject = pageObject;
	}
	

	public HttpServletRequest getPageRequest()
	{
		return pageRequest;
	}

	public void setPageRequest(HttpServletRequest pageRequest)
	{
		this.pageRequest = pageRequest;
	}

	private String getOrderByClause()
	{
		if(this.mobjSorter == null) return "";
		if(this.mlstColumns == null) return "";
		if(this.mlstColumns.size() <= 0) return "";
		String  strCol = this.mobjSorter.getSortColumn();
		if(strCol == null) return "";
		
		String strRet = "";
		Iterator objIter = null;
		IColumnTag objCol = null;
		for(objIter = this.mlstColumns.iterator(); objIter.hasNext();)
		{
			objCol = (IColumnTag) objIter.next();
			if(objCol.getSortable())
			{
				if(objCol.getDataField() != null)
				{
					if(objCol.getDataField().equals(strCol))
					{
						strRet = " ORDER BY " + strCol + " " + (this.mobjSorter.getSortAscending() ? "ASC" : "DESC");
						break;
					}
				}
			}
			objCol = null;
		}
		if(objIter != null) objIter = null;
		if(objCol != null) objCol = null;
		return strRet;
	}
}