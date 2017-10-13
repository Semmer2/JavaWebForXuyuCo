package com.jl.file;

import java.io.File;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.PageContext;

import org.apache.commons.fileupload.*;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;

import com.jl.util.Constants;
import com.jl.util.StringUtils;
import com.jl.util.UUID;

public class BaseUploadFile {
	//�����ϴ����ļ�����
    String allowedFilesList = "swf,gif,jpg,png,bmp,jpeg,doc,xls,wps,zip,rar,pdf,txt,xml,dwg.ceb,tiff";
    int singlemaxfilesize=1024*1024*10;
    private String getUploadTmpFolder(PageContext pagecontext)
    {
    	StringBuffer tmpfolder=new StringBuffer(pagecontext.getServletConfig().getServletContext().getRealPath("/"));
        tmpfolder.append("ftp/temp");
        return tmpfolder.toString();
    }
    public String getUploadTargetFolder(PageContext pagecontext,String targetpath)
    {
    	StringBuffer tmpfolder=new StringBuffer(pagecontext.getServletConfig().getServletContext().getRealPath("/"));
        tmpfolder.append(targetpath);
        return tmpfolder.toString();
    }
    public List UploadWallImage(PageContext pagecontext,Hashtable values,String targetfilename) throws Exception {
    	String targetpath=Constants.getValue("wall");
    	List filelist=new ArrayList();
		ServletFileUpload upload=new ServletFileUpload();
	    upload.setSizeMax(singlemaxfilesize*20);
	    DiskFileItemFactory diskFileItemFactory=new DiskFileItemFactory();
	    diskFileItemFactory.setSizeThreshold(singlemaxfilesize);	    
	    diskFileItemFactory.setRepository(new File(getUploadTmpFolder(pagecontext)));
	    upload.setFileItemFactory(diskFileItemFactory);
	    HttpServletRequest request=(HttpServletRequest)pagecontext.getRequest();
	    List fileItems=null;
		try {
			fileItems=upload.parseRequest(request);			
		    String myName="";
		    String myFileName="";	    
		    Iterator it = fileItems.iterator();
		    while (it.hasNext()) {
		        FileItem fi = (FileItem) it.next();
		        if(fi.isFormField()) {
		    	   String content = fi.getString("GBK");
		    	   String fieldName = fi.getFieldName();
		    	   //ȡ����ֵ   	   
		    	   values.put(fieldName, content);
				} else {			   
				        String pathThatSrc = fi.getName();
				        if(pathThatSrc.trim().equals("")) {
				        	continue;			        }
				      	//ͨ��"\\"��ȡ�������±�λ��
				        int start = pathThatSrc.lastIndexOf(File.separator);			      	
				      	//���±�λ��+1���±꿪ʼ��ȡ�ļ���(������ϴ����������ļ������ļ� ��󴫵��������ϻ������� Ҳ��Ҫ�޸�Դ��ſ��Խ��)
				        myFileName = pathThatSrc.substring(start + 1);			      	
				        //ȡ�ú�׺��
				        String ext= myFileName.substring(myFileName.lastIndexOf('.')+1).toLowerCase();
				        if(allowedFilesList.indexOf(ext) < 0) {
			         		throw new Exception("�Բ���,�ϴ����ļ�����("+ext+")��֧��!");
			         	}
			         	Hashtable ht=new Hashtable();
			         	ht.put("srcfilename",pathThatSrc);
			         	filelist.add(ht);
				}
			}
		    String savePath=getUploadTargetFolder(pagecontext,targetpath);
		    it = fileItems.iterator();
		    int fileindex=0;
		    while (it.hasNext()) {
		        FileItem fi = (FileItem) it.next();
		        if (!fi.isFormField()) {
		            try {
		                String pathThatSrc = fi.getName();
		                if(pathThatSrc.trim().equals("")) {
		                	continue;
		                }
		                int start = pathThatSrc.lastIndexOf(File.separator);		              	
		                myFileName = pathThatSrc.substring(start + 1);		              	
		                String ext="jpg";//myFileName.substring(myFileName.lastIndexOf('.')+1);		                
		                //�ļ��ϴ����ڷ������ϵ��ļ���
		                String newfilename="";
		                if(targetfilename.equalsIgnoreCase(""))
		                {
			                newfilename=UUID.randomUUID().toString();			             	
		                }
		                else
		                {
		                	newfilename=targetfilename;
		                }
		                myName=newfilename+"."+ext;
		                myName=myName.toLowerCase();		              	
		                File bulidFile = new File(savePath, myName);
		                
		      			fi.write(bulidFile);
		      			if(filelist.get(fileindex)!=null)
		      			{
		      				Hashtable ht=(Hashtable)filelist.get(fileindex);
		      				ht.put("file",bulidFile);
		      			}
		            } catch (Exception e) {
		          	  	e.printStackTrace();
		                throw e;
		            } finally {
		          	  	fi.delete();//�����Ƿ�����쳣 ��Ҫȥ������ʱ �ļ��� �������
		            }
		            fileindex++;
		       }		       
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw e;
		}
		return filelist;
	}
    public List ImportXls(PageContext pagecontext,Hashtable values,String targetfilename) throws Exception {
    	String targetpath=Constants.getValue("xls");
    	List filelist=new ArrayList();
		ServletFileUpload upload=new ServletFileUpload();
	    upload.setSizeMax(singlemaxfilesize*20);
	    DiskFileItemFactory diskFileItemFactory=new DiskFileItemFactory();
	    diskFileItemFactory.setSizeThreshold(singlemaxfilesize);	    
	    diskFileItemFactory.setRepository(new File(getUploadTmpFolder(pagecontext)));
	    upload.setFileItemFactory(diskFileItemFactory);
	    HttpServletRequest request=(HttpServletRequest)pagecontext.getRequest();
	    List fileItems=null;
		try {
			fileItems=upload.parseRequest(request);			
		    String myName="";
		    String myFileName="";	    
		    Iterator it = fileItems.iterator();
		    while (it.hasNext()) {
		        FileItem fi = (FileItem) it.next();
		        if(fi.isFormField()) {
		    	   String content = fi.getString("GBK");
		    	   String fieldName = fi.getFieldName();
		    	   //ȡ����ֵ   	   
		    	   values.put(fieldName, content);
				} else {			   
				        String pathThatSrc = fi.getName();
				        if(pathThatSrc.trim().equals("")) {
				        	continue;			        }
				      	//ͨ��"\\"��ȡ�������±�λ��
				        int start = pathThatSrc.lastIndexOf(File.separator);			      	
				      	//���±�λ��+1���±꿪ʼ��ȡ�ļ���(������ϴ����������ļ������ļ� ��󴫵��������ϻ������� Ҳ��Ҫ�޸�Դ��ſ��Խ��)
				        myFileName = pathThatSrc.substring(start + 1);			      	
				        //ȡ�ú�׺��
				        String ext= myFileName.substring(myFileName.lastIndexOf('.')+1).toLowerCase();
				        if(ext.equalsIgnoreCase("xls")==false && ext.equalsIgnoreCase("xlsx")==false) {
			         		throw new Exception("�Բ���,�����ļ�������xls��xlsx�ļ�!");
			         	}
			         	Hashtable ht=new Hashtable();
			         	ht.put("srcfilename",pathThatSrc);
			         	filelist.add(ht);
				}
			}
		    String savePath=getUploadTargetFolder(pagecontext,targetpath);
		    it = fileItems.iterator();
		    int fileindex=0;
		    while (it.hasNext()) {
		        FileItem fi = (FileItem) it.next();
		        if (!fi.isFormField()) {
		            try {
		                String pathThatSrc = fi.getName();
		                if(pathThatSrc.trim().equals("")) {
		                	continue;
		                }
		                int start = pathThatSrc.lastIndexOf(File.separator);		              	
		                myFileName = pathThatSrc.substring(start + 1);		              	
		                String ext=myFileName.substring(myFileName.lastIndexOf('.')+1).toLowerCase().trim();		                
		                //�ļ��ϴ����ڷ������ϵ��ļ���
		                String newfilename="";
		                if(targetfilename.equalsIgnoreCase(""))
		                {
			                newfilename=UUID.randomUUID().toString();			             	
		                }
		                else
		                {
		                	newfilename=targetfilename;
		                }
		                myName=newfilename+"."+ext;
		                myName=myName.toLowerCase();		              	
		                File bulidFile = new File(savePath, myName);		                
		      			fi.write(bulidFile);
		      			if(filelist.get(fileindex)!=null)
		      			{
		      				Hashtable ht=(Hashtable)filelist.get(fileindex);
		      				ht.put("file",bulidFile);
		      			}
		            } catch (Exception e) {
		          	  	e.printStackTrace();
		                throw e;
		            } finally {
		          	  	fi.delete();//�����Ƿ�����쳣 ��Ҫȥ������ʱ �ļ��� �������
		            }
		            fileindex++;
		       }		       
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw e;
		}
		return filelist;
	}
    public List UploadPDF(PageContext pagecontext,Hashtable values,String targetfilename) throws Exception {
    	String targetpath=Constants.getValue("pdf");
    	List filelist=new ArrayList();
		ServletFileUpload upload=new ServletFileUpload();
	    upload.setSizeMax(singlemaxfilesize*20);
	    DiskFileItemFactory diskFileItemFactory=new DiskFileItemFactory();
	    diskFileItemFactory.setSizeThreshold(singlemaxfilesize);	    
	    diskFileItemFactory.setRepository(new File(getUploadTmpFolder(pagecontext)));
	    upload.setFileItemFactory(diskFileItemFactory);
	    HttpServletRequest request=(HttpServletRequest)pagecontext.getRequest();
	    List fileItems=null;
		try {
			fileItems=upload.parseRequest(request);			
		    String myName="";
		    String myFileName="";	    
		    Iterator it = fileItems.iterator();
		    while (it.hasNext()) {
		        FileItem fi = (FileItem) it.next();
		        if(fi.isFormField()) {
		    	   String content = fi.getString("GBK");
		    	   String fieldName = fi.getFieldName();
		    	   //ȡ����ֵ   	   
		    	   values.put(fieldName, content);
				} else {			   
				        String pathThatSrc = fi.getName();
				        if(pathThatSrc.trim().equals("")) {
				        	continue;			        }
				      	//ͨ��"\\"��ȡ�������±�λ��
				        int start = pathThatSrc.lastIndexOf(File.separator);			      	
				      	//���±�λ��+1���±꿪ʼ��ȡ�ļ���(������ϴ����������ļ������ļ� ��󴫵��������ϻ������� Ҳ��Ҫ�޸�Դ��ſ��Խ��)
				        myFileName = pathThatSrc.substring(start + 1);			      	
				        //ȡ�ú�׺��
				        String ext= myFileName.substring(myFileName.lastIndexOf('.')+1).toLowerCase();
				        if(ext.equalsIgnoreCase("pdf")==false) {
			         		throw new Exception("�Բ���,�ϴ����ļ����Ͳ�֧��!");
			         	}
			         	Hashtable ht=new Hashtable();
			         	ht.put("srcfilename",pathThatSrc);
			         	filelist.add(ht);
				}
			}
		    String savePath=getUploadTargetFolder(pagecontext,targetpath);
		    it = fileItems.iterator();
		    int fileindex=0;
		    while (it.hasNext()) {
		        FileItem fi = (FileItem) it.next();
		        if (!fi.isFormField()) {
		            try {
		                String pathThatSrc = fi.getName();
		                if(pathThatSrc.trim().equals("")) {
		                	continue;
		                }
		                int start = pathThatSrc.lastIndexOf(File.separator);		              	
		                myFileName = pathThatSrc.substring(start + 1);		              	
		                String ext="pdf";//myFileName.substring(myFileName.lastIndexOf('.')+1);		                
		                //�ļ��ϴ����ڷ������ϵ��ļ���
		                String newfilename="";
		                if(targetfilename.equalsIgnoreCase(""))
		                {
			                newfilename=UUID.randomUUID().toString();			             	
		                }
		                else
		                {
		                	newfilename=targetfilename;
		                }
		                myName=newfilename+"."+ext;
		                myName=myName.toLowerCase();		              	
		                File bulidFile = new File(savePath, myName);
		                
		      			fi.write(bulidFile);
		      			if(filelist.get(fileindex)!=null)
		      			{
		      				Hashtable ht=(Hashtable)filelist.get(fileindex);
		      				ht.put("file",bulidFile);
		      			}
		            } catch (Exception e) {
		          	  	e.printStackTrace();
		                throw e;
		            } finally {
		          	  	fi.delete();//�����Ƿ�����쳣 ��Ҫȥ������ʱ �ļ��� �������
		            }
		            fileindex++;
		       }		       
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw e;
		}
		return filelist;
	}
    public List UploadFile(PageContext pagecontext,Hashtable values,String targetfilename) throws Exception {
    	String targetpath=Constants.getValue("photo");
    	List filelist=new ArrayList();
		ServletFileUpload upload=new ServletFileUpload();
	    upload.setSizeMax(singlemaxfilesize*20);
	    DiskFileItemFactory diskFileItemFactory=new DiskFileItemFactory();
	    diskFileItemFactory.setSizeThreshold(singlemaxfilesize);	    
	    diskFileItemFactory.setRepository(new File(getUploadTmpFolder(pagecontext)));
	    upload.setFileItemFactory(diskFileItemFactory);
	    HttpServletRequest request=(HttpServletRequest)pagecontext.getRequest();
	    List fileItems=null;
		try {
			fileItems=upload.parseRequest(request);			
		    String myName="";
		    String myFileName="";	    
		    Iterator it = fileItems.iterator();
		    while (it.hasNext()) {
		        FileItem fi = (FileItem) it.next();
		        if(fi.isFormField()) {
		    	   String content = fi.getString("GBK");
		    	   String fieldName = fi.getFieldName();
		    	   //ȡ����ֵ   	   
		    	   values.put(fieldName, content);
				} else {			   
				        String pathThatSrc = fi.getName();
				        if(pathThatSrc.trim().equals("")) {
				        	continue;			        }
				      	//ͨ��"\\"��ȡ�������±�λ��
				        int start = pathThatSrc.lastIndexOf(File.separator);			      	
				      	//���±�λ��+1���±꿪ʼ��ȡ�ļ���(������ϴ����������ļ������ļ� ��󴫵��������ϻ������� Ҳ��Ҫ�޸�Դ��ſ��Խ��)
				        myFileName = pathThatSrc.substring(start + 1);			      	
				        //ȡ�ú�׺��
				        String ext= myFileName.substring(myFileName.lastIndexOf('.')+1).toLowerCase();
				        if(allowedFilesList.indexOf(ext) < 0) {
			         		throw new Exception("�Բ���,�ϴ����ļ�����("+ext+")��֧��!");
			         	}
			         	Hashtable ht=new Hashtable();
			         	ht.put("srcfilename",pathThatSrc);
			         	filelist.add(ht);
				}
			}
		    String savePath=getUploadTargetFolder(pagecontext,targetpath);
		    it = fileItems.iterator();
		    int fileindex=0;
		    while (it.hasNext()) {
		        FileItem fi = (FileItem) it.next();
		        if (!fi.isFormField()) {
		            try {
		                String pathThatSrc = fi.getName();
		                if(pathThatSrc.trim().equals("")) {
		                	continue;
		                }
		                int start = pathThatSrc.lastIndexOf(File.separator);		              	
		                myFileName = pathThatSrc.substring(start + 1);		              	
		                String ext="jpg";//myFileName.substring(myFileName.lastIndexOf('.')+1);		                
		                //�ļ��ϴ����ڷ������ϵ��ļ���
		                String newfilename="";
		                if(targetfilename.equalsIgnoreCase(""))
		                {
			                newfilename=UUID.randomUUID().toString();			             	
		                }
		                else
		                {
		                	newfilename=targetfilename;
		                }
		                myName=newfilename+"."+ext;
		                myName=myName.toLowerCase();		              	
		                File bulidFile = new File(savePath, myName);
		                
		      			fi.write(bulidFile);
		      			if(filelist.get(fileindex)!=null)
		      			{
		      				Hashtable ht=(Hashtable)filelist.get(fileindex);
		      				ht.put("file",bulidFile);
		      			}
		            } catch (Exception e) {
		          	  	e.printStackTrace();
		                throw e;
		            } finally {
		          	  	fi.delete();//�����Ƿ�����쳣 ��Ҫȥ������ʱ �ļ��� �������
		            }
		            fileindex++;
		       }		       
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw e;
		}
		return filelist;
	}
	public String getFormFieldValue(PageContext pagecontext,Hashtable values) throws Exception {
		String rtn="";
		ServletFileUpload upload=new ServletFileUpload();
	    upload.setSizeMax(singlemaxfilesize*20);
	    DiskFileItemFactory diskFileItemFactory=new DiskFileItemFactory();
	    diskFileItemFactory.setSizeThreshold(singlemaxfilesize);	    
	    diskFileItemFactory.setRepository(new File(getUploadTmpFolder(pagecontext)));
	    upload.setFileItemFactory(diskFileItemFactory);
	    HttpServletRequest request=(HttpServletRequest)pagecontext.getRequest();
	    List fileItems=null;
		try {
			fileItems=upload.parseRequest(request);
		    String myName="";
		    String myFileName="";	    
		    Iterator it = fileItems.iterator();
		    while (it.hasNext()) {
		        FileItem fi = (FileItem) it.next();
		        if(fi.isFormField()) {
		    	   String content = fi.getString("GBK")==null?"":fi.getString("GBK").trim();
		    	   String name = fi.getFieldName();
		    	   content=StringUtils.replaceString(content, "/", "");
		    	   if(content.equalsIgnoreCase("null")) content="";
		    	   values.put(name,content.trim());
				} 
			}		    
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw e;
		}
		return rtn;
	}
	
	public List getFileList(PageContext pagecontext,Hashtable values) throws Exception {
		List filelist=new ArrayList();
		ServletFileUpload upload=new ServletFileUpload();
	    upload.setSizeMax(singlemaxfilesize*20);
	    DiskFileItemFactory diskFileItemFactory=new DiskFileItemFactory();
	    diskFileItemFactory.setSizeThreshold(singlemaxfilesize);
	    diskFileItemFactory.setRepository(new File(getUploadTmpFolder(pagecontext)));
	    upload.setFileItemFactory(diskFileItemFactory);
	    HttpServletRequest request=(HttpServletRequest)pagecontext.getRequest();
	    List fileItems=null;
		try {
			fileItems=upload.parseRequest(request);
		    String myFileName="";		    
		    Iterator it = fileItems.iterator();
		    while (it.hasNext()) {
		        FileItem fi = (FileItem) it.next();
		        if(fi.isFormField()) {
		        	String content = fi.getString("GBK");
		        	String fieldName = fi.getFieldName();
		        	//ȡ����ֵ
		        	values.put(fieldName, content);
				} else {
			        String pathThatSrc = fi.getName();
			        if(pathThatSrc.trim().equals("")) {
			        	continue;
			        }
			      	//ͨ��"\\"��ȡ�������±�λ��
			        //int start = pathThatSrc.lastIndexOf(File.separator);
			        int start = pathThatSrc.lastIndexOf("\\");
			      	//���±�λ��+1���±꿪ʼ��ȡ�ļ���(������ϴ����������ļ������ļ� ��󴫵��������ϻ������� Ҳ��Ҫ�޸�Դ��ſ��Խ��)
			        myFileName = pathThatSrc.substring(start + 1);
			        //ȡ�ú�׺��
			        String ext= myFileName.substring(myFileName.lastIndexOf('.')+1).toLowerCase();
			        if(allowedFilesList.indexOf(ext) < 0) {
		         		throw new Exception("�Բ���,�ϴ����ļ�����("+ext+")��֧��!");
		         	}
			        String fieldName = fi.getFieldName();
			        if(fieldName==null || "".equals(fieldName.trim())) {
			        	continue;
			        }
			        String filetype = fieldName.replaceAll("file_", "");
		         	Hashtable ht=new Hashtable();
		         	ht.put("srcfilename",pathThatSrc);
		         	ht.put("filename",myFileName);
		         	ht.put("extname",ext);
		         	ht.put("filetype",filetype);
		         	filelist.add(ht);
				}
			}
		    String parenttablename = values.get("parenttablename")==null?"":values.get("parenttablename").toString();
		    it = fileItems.iterator();
		    int fileindex=0;
		    while (it.hasNext()) {
		        FileItem fi = (FileItem) it.next();
		        if (!fi.isFormField()) {
		            try {
		                String pathThatSrc = fi.getName();
		                if(pathThatSrc.trim().equals("")) {
		                	continue;
		                }
		      			if(filelist.get(fileindex)!=null) {
		      				Hashtable ht=(Hashtable)filelist.get(fileindex);
		      				ht.put("parenttablename",parenttablename);
		      				ht.put("file",fi);
		      			}
		            } catch (Exception e) {
		          	  	e.printStackTrace();
		                throw e;
		            } finally {
		          	  	fi.delete();//�����Ƿ�����쳣 ��Ҫȥ������ʱ �ļ��� �������
		            }
		            fileindex++;
		        }
			}
		} catch(Exception e) {
			e.printStackTrace();
			throw e;
		}
		return filelist;
	}
	public boolean deletefile(String fullfilename)
	{
		boolean rtn=false;
		File bulidFile = new File(fullfilename);		
		rtn=bulidFile.delete();
		return rtn;
	}
}