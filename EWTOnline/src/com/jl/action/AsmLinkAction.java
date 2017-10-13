package com.jl.action;

import com.jl.service.*;
import com.jl.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.PageContext;

import oracle.bali.ewt.selection.Cell;

import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.*;

import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;

public class AsmLinkAction {
	private AsmLinkService service = null;
	private HttpServletRequest request = null;

	public AsmLinkAction(HttpServletRequest request) {
		service = new AsmLinkService(request);
		this.request = request;
	}
	public final void refresh(PageContext pagecontext,String message,String refeshmethod,boolean isclose) throws Exception
	 {
		try
		{
			HttpServletResponse response=(HttpServletResponse)pagecontext.getResponse();
			PrintWriter writer=response.getWriter();
			writer.println("<script>");
			writer.println("alert('"+message+"');");			
			writer.println(refeshmethod);
			if(isclose)
			{
				writer.println("window.close();");
			}
			writer.println("</script>");
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw ex;
		}
	 }
	public String importAsm(String strPath) throws Exception
	{		
			/*
			 * SAXReader reader = new SAXReader(); Document document =
			 * reader.read(new
			 * File("E:\\Tomcat 5.0\\webapps\\EWTOnline\\xml\\asmlink.xml"));
			 * Element root = document.getRootElement(); Iterator it =
			 * root.elementIterator(); while(it.hasNext()){ Element
			 * element=(Element) it.next(); //已知属性名称情况下 String
			 * asmvccode=element.elementText("asm"); String
			 * partvccode=element.elementText("part"); String
			 * qty=element.elementText("qty"); String
			 * imagevccode=element.elementText("image"); String
			 * hot=element.elementText("hot"); String
			 * asmimagevccode=element.elementText("asmimage");
			 * 
			 * SmPartAction partaction=new SmPartAction(request); CommonAction
			 * commonaction=new CommonAction(request); List
			 * ls=partaction.getSmparts(asmvccode); String asmid="";
			 * if(ls.size()==1) { Hashtable ht=(Hashtable)ls.get(0);
			 * asmid=ht.get("id").toString(); }
			 * ls=partaction.getSmparts(partvccode); String partid="";
			 * if(ls.size()==1) { Hashtable ht=(Hashtable)ls.get(0);
			 * partid=ht.get("id").toString(); } String imageid="";
			 * ls=commonaction.getImage(imagevccode); if(ls.size()==1) {
			 * Hashtable ht=(Hashtable)ls.get(0);
			 * imageid=ht.get("id").toString(); } String asmimageid="0";
			 * ls=commonaction.getImage(asmimagevccode); if(ls.size()==1) {
			 * Hashtable ht=(Hashtable)ls.get(0);
			 * asmimageid=ht.get("id").toString(); }
			 * if(asmid.equalsIgnoreCase("")==false &&
			 * partid.equalsIgnoreCase("")==false &&
			 * imageid.equalsIgnoreCase("")==false) { try { String
			 * newid=saveAsmLink("",asmid,partid,imageid,hot,qty,asmimageid);
			 * if(newid.equalsIgnoreCase("")==false) { //success } }
			 * catch(Exception ex) { System.out.println(ex); } } }
			 */
			//String strPath = "E:\\Tomcat 5.0\\webapps\\EWTOnline\\importxls\\asmlink.xls";
		    String errorrowindex="";
			File file = new File(strPath);			
			String extname = strPath.substring(strPath.lastIndexOf('.')+1).toLowerCase().trim();// xls xlsx
			int successcount = 0;
			if (extname.equalsIgnoreCase("xls")) {
				Workbook wb = null;
				try {
					// 构造Workbook（工作薄）对象
					wb = Workbook.getWorkbook(file);
				} catch (BiffException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
				if (wb == null)
					return null;
				// 获得了Workbook对象之后，就可以通过它得到Sheet（工作表）对象了
				Sheet[] sheet = wb.getSheets();
				if (sheet != null && sheet.length > 0) {
					// 对每个工作表进行循环
					for (int i = 0; i < sheet.length; i++) {
						// 得到当前工作表的行数
						int rowNum = sheet[i].getRows();
						for (int j = 0; j < rowNum; j++) {
							// 得到当前行的所有单元格
							jxl.Cell[] cells = sheet[i].getRow(j);
							if (cells != null && cells.length > 0) {
								// 对每个单元格进行循环
								// for(int k=0;k<cells.length;k++){
								if (cells.length == 6) {
									// 读取当前单元格的值
									String asmvccode = cells[0].getContents();
									String partvccode = cells[1].getContents();
									String imagevccode = cells[2].getContents();
									String asmimagevccode = cells[3]
											.getContents();									
									String hot = cells[4].getContents();
									String qty = cells[5].getContents();

									SmPartAction partaction = new SmPartAction(
											request);
									CommonAction commonaction = new CommonAction(
											request);
									List ls = partaction.getSmparts(asmvccode);
									String asmid = "";
									if (ls.size() == 1) {
										Hashtable ht = (Hashtable) ls.get(0);
										asmid = ht.get("id").toString();
									}
									ls = partaction.getSmparts(partvccode);
									String partid = "";
									if (ls.size() == 1) {
										Hashtable ht = (Hashtable) ls.get(0);
										partid = ht.get("id").toString();
									}
									String imageid = "";
									ls = commonaction.getImage(imagevccode);
									if (ls.size() == 1) {
										Hashtable ht = (Hashtable) ls.get(0);
										imageid = ht.get("id").toString();
									}
									String asmimageid = "0";
									ls = commonaction.getImage(asmimagevccode);
									if (ls.size() == 1) {
										Hashtable ht = (Hashtable) ls.get(0);
										asmimageid = ht.get("id").toString();
									}
									if (asmid.equalsIgnoreCase("") == false
											&& partid.equalsIgnoreCase("") == false
											&& imageid.equalsIgnoreCase("") == false) {
										try {
											String newid=saveAsmLink("",asmid,partid,imageid,hot,qty,asmimageid);											
											if (newid.equalsIgnoreCase("") == false) {
												successcount++;
											}
											else
											{
												errorrowindex=errorrowindex+","+String.valueOf(j+1);
											}
										} catch (Exception ex) {
											System.out.println(ex);
											errorrowindex=errorrowindex+","+String.valueOf(j+1);											
										}
									}
									else
									{
										errorrowindex=errorrowindex+","+String.valueOf(j+1);		
									}
								}
							}
						}
					}
				}
				// 最后关闭资源，释放内存
				wb.close();
			}
			if (extname.equalsIgnoreCase("xlsx")) {
				try {
					// 构造 XSSFWorkbook 对象，strPath 传入文件路径
					XSSFWorkbook xwb = new XSSFWorkbook(strPath);
					// 读取第一章表格内容
					XSSFSheet sheet = xwb.getSheetAt(0);
					// 定义 row、cell
					XSSFRow row;
					String cell;
					// 循环输出表格中的内容
					for (int i = sheet.getFirstRowNum(); i < sheet
							.getPhysicalNumberOfRows(); i++) {
						row = sheet.getRow(i);
						/*
						 * for (int j = row.getFirstCellNum(); j <
						 * row.getPhysicalNumberOfCells(); j++) { // 通过
						 * row.getCell(j).toString() 获取单元格内容， cell =
						 * row.getCell(j).toString(); System.out.print(cell +
						 * "\t"); }
						 */
						if (row.getPhysicalNumberOfCells() == 6) {
							String asmvccode = row.getCell(0).toString();
							String partvccode = row.getCell(1).toString();
							String imagevccode = row.getCell(2).toString();
							String asmimagevccode = row.getCell(3).toString();
							String qty = row.getCell(5).toString();
							String hot = row.getCell(4).toString();

							SmPartAction partaction = new SmPartAction(request);
							CommonAction commonaction = new CommonAction(
									request);
							List ls = partaction.getSmparts(asmvccode);
							String asmid = "";
							if (ls.size() == 1) {
								Hashtable ht = (Hashtable) ls.get(0);
								asmid = ht.get("id").toString();
							}
							ls = partaction.getSmparts(partvccode);
							String partid = "";
							if (ls.size() == 1) {
								Hashtable ht = (Hashtable) ls.get(0);
								partid = ht.get("id").toString();
							}
							String imageid = "";
							ls = commonaction.getImage(imagevccode);
							if (ls.size() == 1) {
								Hashtable ht = (Hashtable) ls.get(0);
								imageid = ht.get("id").toString();
							}
							String asmimageid = "0";
							ls = commonaction.getImage(asmimagevccode);
							if (ls.size() == 1) {
								Hashtable ht = (Hashtable) ls.get(0);
								asmimageid = ht.get("id").toString();
							}
							if (asmid.equalsIgnoreCase("") == false
									&& partid.equalsIgnoreCase("") == false
									&& imageid.equalsIgnoreCase("") == false) {
								try {
									String newid=saveAsmLink("",asmid,partid,imageid,hot,qty,asmimageid);									
									if (newid.equalsIgnoreCase("") == false) {
										successcount++;
									}
									else
									{
										errorrowindex=errorrowindex+","+String.valueOf(i+1);
									}
								} catch (Exception ex) {
									System.out.println(ex);
									errorrowindex=errorrowindex+","+String.valueOf(i+1);
								}
							}
							else
							{
								errorrowindex=errorrowindex+","+String.valueOf(i+1);
							}
							// System.out.println("");
						}
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if(errorrowindex.equalsIgnoreCase("")==false) errorrowindex=errorrowindex.substring(1,errorrowindex.length());
			return errorrowindex;
	}
	public Object doing(String method) throws Exception {
		Object result = "";

		

		if (method.equalsIgnoreCase("newasm")) {
			String id = "";
			String asmvccode = request.getParameter("asmvccode");
			String partvccode = request.getParameter("partvccode");
			String imagevccode = request.getParameter("imagevccode");
			String asmimagevccode = request.getParameter("asmimagevccode");
			String ihot = request.getParameter("ihot");
			String iqty = request.getParameter("iqty");
			SmPartAction partaction = new SmPartAction(request);
			CommonAction commonaction = new CommonAction(request);
			List ls = partaction.getSmparts(asmvccode);
			String asmid = "";
			if (ls.size() == 1) {
				Hashtable ht = (Hashtable) ls.get(0);
				asmid = ht.get("id").toString();
			}
			ls = partaction.getSmparts(partvccode);
			String partid = "";
			if (ls.size() == 1) {
				Hashtable ht = (Hashtable) ls.get(0);
				partid = ht.get("id").toString();
			}
			String imageid = "";
			ls = commonaction.getImage(imagevccode);
			if (ls.size() == 1) {
				Hashtable ht = (Hashtable) ls.get(0);
				imageid = ht.get("id").toString();
			}
			String iasmimageid = "0";
			ls = commonaction.getImage(asmimagevccode);
			if (ls.size() == 1) {
				Hashtable ht = (Hashtable) ls.get(0);
				iasmimageid = ht.get("id").toString();
			}

			String newid = saveAsmLink("", asmid, partid, imageid, ihot, iqty,
					iasmimageid);
			if (newid.equalsIgnoreCase("")) {
				ls = getAsmLinksById(newid);
				if (ls.size() == 1) {
					Hashtable ht = (Hashtable) ls.get(0);
					ht.put("totalcount", String.valueOf(ls.size()));
					ht.put("dowhat", "new");
					List htlist = new ArrayList();
					htlist.add(ht);
					result = htlist;
				} else {
					result = "-1";
				}
			} else {
				result = "-1";
			}
		}
		if (method.equalsIgnoreCase("getdesc")) {
			String typename = request.getParameter("typename");
			String value = request.getParameter("value");
			SmPartService smpartservice = new SmPartService(request);
			if (typename.equalsIgnoreCase("asmvccode")) {
				List ls = smpartservice.getSmparts(value);
				if (ls.size() == 1) {
					Hashtable ht = (Hashtable) ls.get(0);
					ht.put("desc", ht.get("vccname").toString());
					List htlist = new ArrayList();
					htlist.add(ht);
					result = htlist;
				} else {
					result = "0";
				}

			}
			if (typename.equalsIgnoreCase("partvccode")) {
				List ls = smpartservice.getSmparts(value);
				if (ls.size() == 1) {
					Hashtable ht = (Hashtable) ls.get(0);
					ht.put("desc", ht.get("vccname").toString());
					List htlist = new ArrayList();
					htlist.add(ht);
					result = htlist;
				} else {
					result = "0";
				}
			}
			if (typename.equalsIgnoreCase("imagevccode")) {
				CommonAction commonaction = new CommonAction(request);
				List ls = commonaction.getImage(value);
				if (ls.size() == 1) {
					Hashtable ht = (Hashtable) ls.get(0);
					ht.put("desc", ht.get("vccnote").toString());
					List htlist = new ArrayList();
					htlist.add(ht);
					result = htlist;
				} else {
					result = "0";
				}
			}
		}

		if (method.equalsIgnoreCase("querypartforedit")) {
			// 返回零件序号列表
			String selectasmid = request.getParameter("asmid");
			List ls = getPartListForEdit(selectasmid);
			List htlist = new ArrayList();
			Hashtable availpartht = new Hashtable();
			for (int i = 0; i < ls.size(); i++) {
				Hashtable ht = (Hashtable) ls.get(i);
				String id = ht.get("id").toString();
				String vccnote = ht.get("vccnote").toString();

				availpartht.put("key" + String.valueOf(i) + "_code", id);
				availpartht.put("key" + String.valueOf(i) + "_name", "编号:" + id
						+ ".描述:" + vccnote);
			}
			availpartht.put("totalcount", String.valueOf(ls.size()));
			htlist.add(availpartht);
			result = htlist;
		}
		if (method.equalsIgnoreCase("querypart")) {
			// 返回零件序号列表
			String selectasmid = request.getParameter("asmid");
			List ls = getPartList(selectasmid);
			List htlist = new ArrayList();
			Hashtable availpartht = new Hashtable();
			for (int i = 0; i < ls.size(); i++) {
				Hashtable ht = (Hashtable) ls.get(i);
				String id = ht.get("id").toString();
				String vccnote = ht.get("vccnote").toString();

				availpartht.put("key" + String.valueOf(i) + "_code", id);
				availpartht.put("key" + String.valueOf(i) + "_name", "编号:" + id
						+ ".描述:" + vccnote);
			}
			availpartht.put("totalcount", String.valueOf(ls.size()));
			htlist.add(availpartht);
			result = htlist;
		}
		if (method.equalsIgnoreCase("queryhotlist")) {
			// 返回热点列表
			String imageid = request.getParameter("imageid");
			List ls = getHotList(imageid);
			List htlist = new ArrayList();
			Hashtable availhotht = new Hashtable();
			for (int i = 0; i < ls.size(); i++) {
				Hashtable ht = (Hashtable) ls.get(i);
				String id = ht.get("id").toString();
				String ihot = ht.get("ihot").toString();

				availhotht.put("key" + String.valueOf(i) + "_code", id);
				availhotht.put("key" + String.valueOf(i) + "_name", ihot);
			}
			availhotht.put("totalcount", String.valueOf(ls.size()));
			htlist.add(availhotht);
			result = htlist;
		}
		if (method.equalsIgnoreCase("queryiimagelist")) {
			// 返回在爆炸图列表
			List ls = getIImageList();
			List htlist = new ArrayList();
			Hashtable availimageht = new Hashtable();
			for (int i = 0; i < ls.size(); i++) {
				Hashtable ht = (Hashtable) ls.get(i);
				String id = ht.get("id").toString();
				String vccode = ht.get("vccode").toString();
				String vccnote = ht.get("vccnote").toString();

				availimageht.put("key" + String.valueOf(i) + "_code", id);
				availimageht.put("key" + String.valueOf(i) + "_name", "编号:"
						+ vccode + ".描述:" + vccnote);
			}
			availimageht.put("totalcount", String.valueOf(ls.size()));
			htlist.add(availimageht);
			result = htlist;
		}
		if (method.equalsIgnoreCase("queryavailasm")) {
			// 返回在总成包表中还没有的总成序号
			List ls = getAvailAsmLinks();
			List htlist = new ArrayList();
			Hashtable availasmht = new Hashtable();
			for (int i = 0; i < ls.size(); i++) {
				Hashtable ht = (Hashtable) ls.get(i);
				// vccode, vccname
				String iasmid = ht.get("iasmid").toString();
				String vccnote = ht.get("vccnote").toString();
				availasmht.put("key" + String.valueOf(i) + "_code", iasmid);
				availasmht.put("key" + String.valueOf(i) + "_name", "序号:"
						+ iasmid + ".描述:" + vccnote);
			}
			availasmht.put("totalcount", String.valueOf(ls.size()));
			htlist.add(availasmht);
			result = htlist;
		}
		if (method.equalsIgnoreCase("queryasmbyid")) {
			String id = request.getParameter("id");
			List ls = getAsmLinksById(id);
			List htlist = new ArrayList();
			Hashtable ht = (Hashtable) ls.get(0);
			ht.put("totalcount", String.valueOf(ls.size()));
			htlist.add(ht);
			result = htlist;
		}
		if (method.equalsIgnoreCase("checkparames")) {
			String asmvccode = request.getParameter("p1");
			String partvccode = request.getParameter("p2");
			String imagevccode = request.getParameter("p3");
			String asmimagevccode = request.getParameter("p4");
			SmPartAction partaction = new SmPartAction(request);
			CommonAction commonaction = new CommonAction(request);
			List ls = partaction.getSmparts(asmvccode);
			String asmid = "";
			if (ls.size() == 1) {
				Hashtable ht = (Hashtable) ls.get(0);
				asmid = ht.get("id").toString();
			}
			ls = partaction.getSmparts(partvccode);
			String partid = "";
			if (ls.size() == 1) {
				Hashtable ht = (Hashtable) ls.get(0);
				partid = ht.get("id").toString();
			}
			String imageid = "";
			ls = commonaction.getImage(imagevccode);
			if (ls.size() == 1) {
				Hashtable ht = (Hashtable) ls.get(0);
				imageid = ht.get("id").toString();
			}
			String asmimageid = "0";
			ls = commonaction.getImage(asmimagevccode);
			if (ls.size() == 1) {
				Hashtable ht = (Hashtable) ls.get(0);
				asmimageid = ht.get("id").toString();
			}
			Hashtable ht = new Hashtable();
			ht.put("asmid", asmid);
			ht.put("partid", partid);
			ht.put("imageid", imageid);
			ht.put("asmimageid", asmimageid);
			List htlist = new ArrayList();
			htlist.add(ht);
			result = htlist;
		}
		if (method.equalsIgnoreCase("saveasm")) {
			String id = request.getParameter("id");
			String asmvccode = request.getParameter("asmvccode");
			String partvccode = request.getParameter("partvccode");
			String imagevccode = request.getParameter("imagevccode");
			String ihot = request.getParameter("ihot");
			String iqty = request.getParameter("iqty");
			String asmimagevccode = request.getParameter("asmimagevccode");
			SmPartAction partaction = new SmPartAction(request);
			CommonAction commonaction = new CommonAction(request);
			List ls = partaction.getSmparts(asmvccode);
			String asmid = "";
			if (ls.size() == 1) {
				Hashtable ht = (Hashtable) ls.get(0);
				asmid = ht.get("id").toString();
			}
			ls = partaction.getSmparts(partvccode);
			String partid = "";
			if (ls.size() == 1) {
				Hashtable ht = (Hashtable) ls.get(0);
				partid = ht.get("id").toString();
			}
			String imageid = "";
			ls = commonaction.getImage(imagevccode);
			if (ls.size() == 1) {
				Hashtable ht = (Hashtable) ls.get(0);
				imageid = ht.get("id").toString();
			}
			String iasmimageid = "0";
			ls = commonaction.getImage(asmimagevccode);
			if (ls.size() == 1) {
				Hashtable ht = (Hashtable) ls.get(0);
				iasmimageid = ht.get("id").toString();
			}
			String newid = saveAsmLink(id, asmid, partid, imageid, ihot, iqty,
					iasmimageid);
			if (id.equalsIgnoreCase("") == false) {
				// 做了编辑保存
				ls = getAsmLinksById(id);
				Hashtable ht = (Hashtable) ls.get(0);
				ht.put("totalcount", String.valueOf(ls.size()));
				ht.put("dowhat", "edit");
				List htlist = new ArrayList();
				htlist.add(ht);
				result = htlist;

			} else {
				if (id.equalsIgnoreCase("")) {
					// 做了新建
					ls = getAsmLinksById(newid);
					Hashtable ht = (Hashtable) ls.get(0);
					ht.put("totalcount", String.valueOf(ls.size()));
					ht.put("dowhat", "new");
					List htlist = new ArrayList();
					htlist.add(ht);
					result = htlist;
				}
			}
		}
		if (method.equalsIgnoreCase("delete")) {
			String ids = request.getParameter("ids");
			result = delAsmLink(ids);
		}
		return result;
	}

	public String saveAsmLink(String id, String iasmid, String ipartid,
			String iimageid, String ihot, String iqty, String iasmimageid)
			throws Exception {
		return service.saveAsmLink(id, iasmid, ipartid, iimageid, ihot, iqty,
				iasmimageid);
	}

	public List getAsmLinks() throws Exception {
		return service.getAsmLinks();
	}

	public String getAsmLinkTotal() throws Exception {
		return service.getAsmLinkTotal();
	}

	public List getAsmLinks(String asmid) throws Exception {
		return service.getAsmLinks(asmid);
	}

	public List getAsmLinks(String asmid, String partid) throws Exception {
		return service.getAsmLinks(asmid, partid);
	}

	public List getChildAsmLinks(String asmid) throws Exception {
		return service.getChildAsmLinks(asmid);
	}

	public String delAsmLink(String ids) throws Exception {
		return service.delAsmLink(ids);
	}

	public List getAvailAsmLinks() throws Exception {
		return service.getAvailAsmLinks();
	}

	public List getIImageList() throws Exception {
		return service.getIImageList();
	}

	public List getPartList(String asmid) throws Exception {
		return service.getPartList(asmid);
	}

	public List getPartListForEdit(String asmid) throws Exception {
		return service.getPartListForEdit(asmid);
	}

	public List getHotList(String imageid) throws Exception {
		return service.getHotList(imageid);
	}

	public List getAsmLinksById(String id) throws Exception {
		return service.getAsmLinksById(id);
	}

	public List getAsmLinkByVccode(String vccode) throws Exception {
		return service.getAsmLinkByVccode(vccode);
	}
	public String getXlsId() throws Exception
	{		
		return service.getXlsId();
	}
}