package com.jl.tag.datagrid;

import java.io.*;
import java.lang.reflect.*;
import java.text.*;
import java.util.*;
import javax.servlet.jsp.*;

import org.apache.commons.beanutils.*;
import java.sql.Timestamp;

public final class InputColumn extends AColumnTag
{

	private String inputType;
	private boolean allowEdit;
	private int mintInputWidth;
	private int mintInputHeight;
	private int mintInputBorder;
	private String mintAppendText;
	private String mintInputCss;
	private String mintAnnexID; 
	private String mintValidInput = null;
	private String mintSelectValue;
	private String mintSelectText;
	private String mstrOnClickEvent;
	private String mstrOnDBClickEvent;
	private String mstrOnChange;
	private boolean onChangeEvent;
	private String mstrDataFormat;
	private boolean isSeek=true;;
	private String defaultValue="";
	private String mstrAlterText = null;

	public InputColumn()
	{
		super();
		this.inputType = "text";
		this.mintAppendText = "";
		this.allowEdit = true;
		this.mintInputWidth = -1;
		this.mintInputHeight = -1;
		this.mintInputBorder = -1;
		this.mintInputCss = "tableData";
		onChangeEvent = false;
	}

	public String getAlterText()
	{
		return this.mstrAlterText;
	}

	public String getOnClickEvent()
	{
		return this.mstrOnClickEvent;
	}
	public String getOnDBClickEvent()
	{
		return this.mstrOnDBClickEvent;
	}

	public boolean getOnChangeEvent()
	{
		return this.getOnChangeEvent();
	}

	public String getDataFormat()
	{
		return this.mstrDataFormat;
	}

	public String getSelectText()
	{
		return this.mintSelectText;
	}

	public String getSelectValue()
	{
		return this.mintSelectValue;
	}

	public String getValidInput()
	{
		return this.mintValidInput;
	}

	public String getAnnexID()
	{
		return this.mintAnnexID;
	}

	public String getInputCss()
	{
		return this.mintInputCss;
	}
	public String getAppendText()
	{
		return mintAppendText;
	}

	public String getInputType()
	{
		return inputType;
	}

	public boolean isAllowEdit()
	{
		return allowEdit;
	}

	public boolean isIsSeek()
	{
		return isSeek;
	}

	public String getDefaultValue()
	{
		return defaultValue;
	}

	public int getInputBorder()
	{
		return mintInputBorder;
	}

	public int getInputHeight()
	{
		return mintInputHeight;
	}

	public int getInputWidth()
	{
		return mintInputWidth;
	}

	public void setAlterText(String pstrAlterText)
	{
	this.mstrAlterText = pstrAlterText;
	}

	public void setOnClickEvent(String pstrOnClickEvent)
	{
	this.mstrOnClickEvent = pstrOnClickEvent;
	}
	public void setOnDBClickEvent(String pstrOnDBClickEvent)
	{
		this.mstrOnDBClickEvent = pstrOnDBClickEvent;
	}

	public void setOnChangeEvent(boolean pOnChangeEvent)
	{
		this.onChangeEvent = pOnChangeEvent;
	}

	public void setDataFormat(String pstrDataFormat)
	{
		this.mstrDataFormat = pstrDataFormat;
	}

	public void setSelectText(String selectText)
	{
		this.mintSelectText = selectText;
	}

	public void setSelectValue(String selectValue)
	{
		this.mintSelectValue = selectValue;
	}

	public void setValidInput(String validInput)
	{
		this.mintValidInput = validInput;
	}

	public void setAnnexID(String annexID)
	{
		this.mintAnnexID = annexID.toLowerCase();
	}

	public void setInputCss(String inputCss)
	{
		this.mintInputCss = inputCss;
	}

	public void setAppendText(String appendText)
	{
		this.mintAppendText = appendText;
	}

	public void setInputType(String inputType)
	{
		this.inputType = inputType;
	}

	public void setAllowEdit(boolean allowEdit)
	{
		this.allowEdit = allowEdit;
	}

	public void setIsSeek(boolean isSeek)
	{
		this.isSeek = isSeek;
	}

	public void setDefaultValue(String defaultValue)
	{
		this.defaultValue = defaultValue;
	}

	public void setInputBorder(int inputBorder)
	{
		this.mintInputBorder = inputBorder;
	}

	public void setInputHeight(int inputHeight)
	{
		this.mintInputHeight = inputHeight;
	}

	public void setInputWidth(int inputWidth)
	{
		this.mintInputWidth = inputWidth;
	}
	
	public String getOnChange()
	{
		return mstrOnChange;
	}

	public void setOnChange(String mstrOnChange)
	{
		this.mstrOnChange = mstrOnChange;
	}

	public int doEndTag() throws JspException
	{
		InputColumn obj = new InputColumn();
		obj.setInputType(this.inputType);
		obj.setAllowEdit(this.allowEdit);
		obj.setInputBorder(this.mintInputBorder);
		obj.setInputHeight(this.mintInputHeight);
		obj.setInputWidth(this.mintInputWidth);
		obj.setAppendText(this.mintAppendText);
		obj.setInputCss(this.mintInputCss);
		obj.setAnnexID(this.mintAnnexID);
		obj.setValidInput(this.mintValidInput);
		obj.setSelectText(this.mintSelectText);
		obj.setSelectValue(this.mintSelectValue);
		obj.setDataFormat(this.mstrDataFormat);
		obj.setOnClickEvent(this.mstrOnClickEvent);
		obj.setOnDBClickEvent(this.mstrOnDBClickEvent);
		obj.setOnChange(this.mstrOnChange);
		obj.setOnChangeEvent(this.onChangeEvent);
		obj.setAlterText(this.mstrAlterText);
		obj.setDefaultValue(this.defaultValue);
		int ret = doEndTag(obj);
		if(obj != null) obj = null;
		return ret;
	}

	private String printInputText(Object pobjValue,int recordPos,boolean lightOn)
	{
		String cs = this.mstrDataField + this.resolveFields(this.mintAnnexID);
		String s = "<input type='" + this.inputType + "' name='" + cs + "' id='" + cs + "'";
		if(this.inputType.equalsIgnoreCase("checkbox") || this.inputType.equalsIgnoreCase("radio"))
		{
			if(pobjValue.toString().equals("1")) s += " checked";
			s += " value='1'";
		}
		else
		{
			if(this.mstrDataFormat != null) cs = formatField(pobjValue,this.mstrDataFormat,6);
			else cs = pobjValue.toString();
			s += " value='" + (this.isSeek ? cs : this.defaultValue) + "'";
			s += " style='width:" + (this.mintInputWidth==-1 ? "100%" : this.mintInputWidth+"pt") + "'";
			s += " border='" + this.mintInputBorder + "'";
			s += " class='" + this.mintInputCss + "'";
			if(this.allowEdit == false) s += " readonly";
			else if(lightOn) s += " onFocus='changeInputClass(this)'";
			if(this.mintValidInput != null) s += " alt='" + this.mintValidInput + "'";
			if(this.mstrAlterText != null) s += " title='" + this.mstrAlterText + "'";
			if(this.mstrOnClickEvent != null) s += " onclick='" + this.mstrOnClickEvent + "'";
			if(this.mstrOnDBClickEvent != null) s += " ondblclick='" + this.mstrOnDBClickEvent + "'";
			if(this.mstrOnChange != null) s += " onchange='" + this.mstrOnChange + "'";
			if(this.onChangeEvent == true) s += " onchange='changeCheckBox(" + recordPos + ")'";
			if(lightOn) s += " ln='" + (recordPos+1) + "'/>";
		}
		return s;
	}

	private String printSelectCheckInput(Object pobjValue,int recordPos)
	{
		if(pobjValue instanceof java.util.List) return "";
		
		Object objRet = null;
		Object selectValue = null;
		Object selectText = null;
		String selectName = this.mstrDataField + this.resolveFields(this.mintAnnexID);
		
		String s = "";
		List ls = new ArrayList();
		ls = (List) pobjValue;
		Iterator it = ls.iterator();
		s = "<select id='" + selectName + "' name='" + selectName;
		s += "' class='" + this.mintInputCss;
		s += "' style='width:" + this.mintInputWidth +"pt'";
		if(this.mstrOnChange != null) s += " onchange='" + this.mstrOnChange + "'";
		s += ">";
			
		while (it.hasNext())
		{
			try
			{
				objRet = it.next();
				if(objRet instanceof java.lang.String)
					s += "<option value='" + objRet.toString() + "'>" + objRet.toString() + "</option>";
				else
				{
					selectValue = PropertyUtils.getProperty(objRet, this.mintSelectValue);
					selectText = PropertyUtils.getProperty(objRet, this.mintSelectText);
					s += "<option";
					if(this.defaultValue !=null && !"".equals(this.defaultValue) && selectValue.toString().equals(this.resolveFields(this.defaultValue)))
						s += " selected";
					s += "value='" + selectValue + "'>" + selectValue + "</option>";
				}
			}
			catch (NoSuchMethodException ex) {ex.printStackTrace();	}
			catch (InvocationTargetException ex) {ex.printStackTrace();	}
			catch (IllegalAccessException ex) {ex.printStackTrace(); }
		}
		s += "</select>";
		return s;
	}

	private String resolveFields(String param) throws ClassCastException
	{
		DBGrid objTmp = null;
		String values = "";
		objTmp = (DBGrid) getParent();
		values = objTmp.getColumnValue(param).toString();
		return values;
	}

	public String getDetail(Object pobjValue) throws JspException
	{
		return null;
	}
	
	public String getDetail(Object pobjValue,int recordPos,boolean lightOn) throws JspException
	{
		String s;
		if(this.inputType.equalsIgnoreCase("hidden")) s = this.printInputText(pobjValue,recordPos,lightOn);
		else
		{
			s = "<td" + getBase(true);
			s += " iscopy='" + (this.mblnCopy == true ? "true" : "false") + "'";
			s += ">";
			if (this.inputType.equalsIgnoreCase("select"))
				s += this.printSelectCheckInput(pobjValue,recordPos);
			else
				s += this.printInputText(pobjValue,recordPos,lightOn);
			s += "</td>";
		}
		return s;
	}
	
	public String getEmptyTDDetail() throws JspException
	{
		return null;
	}

	public String getEmptyTDDetail(int recordPos, boolean lightOn) throws JspException
	{
		return getDetail("&value", recordPos, lightOn);
	}
}
