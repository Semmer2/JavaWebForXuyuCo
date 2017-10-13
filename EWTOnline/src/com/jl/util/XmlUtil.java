package com.jl.util;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.dom4j.io.*;
import org.dom4j.*;

import com.jl.action.CommonAction;
import com.jl.action.SmPartAction;

public class XmlUtil {
	public static String getxml(String url) throws Exception
	{	  
		try
		{
			SAXReader readXML = new SAXReader();
	        Document doc  = readXML.read(url);
	        return doc.asXML();
		}
		catch(Exception ex)
		{
		    //System.out.println(ex.getMessage());
			throw ex;
		}
	}
	public void testxlsx()
	{
		try{ 
		   // 构造 XSSFWorkbook 对象，strPath 传入文件路径
		   String strPath="E:\\Tomcat 5.0\\webapps\\EWTOnline\\importxls\\asmlink.xlsx";	
		   XSSFWorkbook xwb = new XSSFWorkbook(strPath); 
		   // 读取第一章表格内容 
		   XSSFSheet sheet = xwb.getSheetAt(0); 
		   // 定义 row、cell 
		   XSSFRow row; 
		   String cell; 
		   // 循环输出表格中的内容 
		   for (int i = sheet.getFirstRowNum(); i < sheet.getPhysicalNumberOfRows(); i++) 
		   { 
		    row = sheet.getRow(i); 
		    for (int j = row.getFirstCellNum(); j < row.getPhysicalNumberOfCells(); j++) 
		    { 
		     // 通过 row.getCell(j).toString() 获取单元格内容， 
		     cell = row.getCell(j).toString(); 
		      System.out.print(cell + "\t"); 
		    } 
		    System.out.println(""); 
		   } 
		  } 
		  catch (IOException e) 
		  { 
		   // TODO Auto-generated catch block 
		   e.printStackTrace(); 
		  } 

	}
	public void testxlsforpol() throws IOException
	{
		InputStream input=null;
		try
		{
		   input = new FileInputStream("E:\\Tomcat 5.0\\webapps\\EWTOnline\\importxls\\asmlink.xls");
		   POIFSFileSystem fs = new POIFSFileSystem(input);
		   HSSFWorkbook wb = new HSSFWorkbook(fs);
		   HSSFSheet sheet = wb.getSheetAt(0);
		   // Iterate over each row in the sheet
		   Iterator rows = sheet.rowIterator();
		   while (rows.hasNext()){
		    HSSFRow row = (HSSFRow) rows.next();
		    //System.out.println("Row #" + row.getRowNum());
		    // Iterate over each cell in the row and print out the cell"s content
		    Iterator cells = row.cellIterator();		    
		    while (cells.hasNext()) {
		     HSSFCell cell = (HSSFCell) cells.next();
		     //System.out.println("Cell #" + cell.getCellNum());
		     switch (cell.getCellType()) {
		     case HSSFCell.CELL_TYPE_NUMERIC:
		      System.out.println(cell.getNumericCellValue());
		      break;
		     case HSSFCell.CELL_TYPE_STRING:
		      System.out.println(cell.getStringCellValue());
		      break;
		     case HSSFCell.CELL_TYPE_BOOLEAN:
		      System.out.println(cell.getBooleanCellValue());
		      break;
		     case HSSFCell.CELL_TYPE_FORMULA:
		      System.out.println(cell.getCellFormula());
		      break;
		     default:
		      System.out.println("unsuported sell type");
		      break;
		     }
		    }
		   }
		  }
		  catch (IOException ex) {
		   ex.printStackTrace();
		  }
		  finally
		  {
			  input.close();
		  }
	}
	public void testxls() //jxl.jar
	{
		File file=new File("E:\\Tomcat 5.0\\webapps\\EWTOnline\\importxls\\asmlink.xls");
		StringBuffer sb = new StringBuffer();
	    Workbook wb = null;
	    try {    
	        //构造Workbook（工作薄）对象    
	        wb=Workbook.getWorkbook(file);    
	    } catch (BiffException e) {    
	        e.printStackTrace();    
	    } catch (IOException e) {    
	        e.printStackTrace();    
	    }   
	    if(wb==null) return;
	    //获得了Workbook对象之后，就可以通过它得到Sheet（工作表）对象了    
	    Sheet[] sheet = wb.getSheets();
	    if(sheet!=null&&sheet.length>0){    
	        //对每个工作表进行循环    
	        for(int i=0;i<sheet.length;i++){ 
	            //得到当前工作表的行数    
	            int rowNum = sheet[i].getRows();    
	            for(int j=0;j<rowNum;j++){ 
	                //得到当前行的所有单元格    
	                jxl.Cell[] cells = sheet[i].getRow(j);    
	                if(cells!=null&&cells.length>0){    
	                    //对每个单元格进行循环    
	                    //for(int k=0;k<cells.length;k++){ 
	                        //读取当前单元格的值    
	                        String asmvccode = cells[0].getContents();    	                        
	        	            String partvccode=cells[1].getContents();
	        	            String imagevccode=cells[2].getContents();
	        	            String asmimagevccode=cells[3].getContents();
	        	            String qty=cells[4].getContents();    	        	            
	        	            String hot=cells[5].getContents();
	        	            
	        	            System.out.println(asmvccode);
	        	            System.out.println(partvccode);
	        	            System.out.println(imagevccode);
	        	            System.out.println(asmimagevccode);
	        	            System.out.println(qty);
	        	            System.out.println(hot);	        	            
	                    //}    
	                }  
	            }      
	        }    
	    }    
	    //最后关闭资源，释放内存    
	    wb.close();
	}
	public static void main(String[] args) throws Exception{
		XmlUtil xmlutil=new XmlUtil();
		xmlutil.testxlsforpol();
	}
}