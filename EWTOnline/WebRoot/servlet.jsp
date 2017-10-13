<%@ page contentType="text/html; charset=UTF-8"%>
<%@page import="com.jl.action.*"%>
<%@page import="com.jl.util.*"%>
<%@page import="java.util.*"%>
<%@page import="com.jl.file.*"%>
<%@page import="java.net.URLDecoder"%>
<%	
	String actiontype=request.getParameter("action");
	if(actiontype.equalsIgnoreCase("buycarmanage"))		
	{
		String domethod=request.getParameter("method");
		if(domethod.equalsIgnoreCase("updateqty"))
		{
			String qty=request.getParameter("parames");
			String id=request.getParameter("datakey");
			BuyCarAction buycaraction = new BuyCarAction(request);
			String ret=buycaraction.updateQty(id,qty);
			if(ret=="")
			{				
				String error=pageContext.getRequest().getAttribute("error").toString();
				out.write(id+","+java.net.URLDecoder.decode(error));
			}
			else
			{			
				out.write(id);
			}
		}
		if(domethod.equalsIgnoreCase("addqty"))
		{
			String vccode=request.getParameter("vccode");
			String vcename=request.getParameter("vcename");
			String qty=request.getParameter("qty");
			BuyCarAction buycaraction = new BuyCarAction(request);
			String userid=request.getSession().getAttribute("currentuserid").toString();			
			String ret=buycaraction.createBuy(userid,vccode,vcename,qty);
			if(ret=="")
			{				
				String error=pageContext.getRequest().getAttribute("error").toString();				
				out.write(ret+","+java.net.URLDecoder.decode(error));
				
			}
			else
			{			
				out.write(ret);
			}
		}
		if(domethod.equalsIgnoreCase("delete"))
		{
			String id=request.getParameter("id");			
			BuyCarAction buycaraction = new BuyCarAction(request);			
			String rtn=buycaraction.delBuyCar(id);
			if(rtn=="")
			{				
				String error=pageContext.getRequest().getAttribute("error").toString();
				out.write(rtn+","+java.net.URLDecoder.decode(error));
			}
			else
			{			
				out.write(rtn);
			}
		}
		if(domethod.equalsIgnoreCase("clearuserbuy"))
		{
			String userid=request.getSession().getAttribute("currentuserid").toString();		
			BuyCarAction buycaraction = new BuyCarAction(request);			
			String rtn=buycaraction.delBuyCarByUserid(userid);
			if(rtn=="")
			{				
				String error=pageContext.getRequest().getAttribute("error").toString();
				out.write(rtn+","+java.net.URLDecoder.decode(error));
			}
			else
			{			
				out.write(rtn);
			}
		}		
	}	
	if(actiontype.equalsIgnoreCase("smpartprice"))		
	{
		String domethod=request.getParameter("method");
		RoleAction roleaction=new RoleAction(request);		
		if(domethod.equalsIgnoreCase("changeprice"))
		{
			String username=request.getParameter("username");
			String partvccode=request.getParameter("partvccode");
			String price=request.getParameter("price");
			SmPartAction action=new SmPartAction(request);
			action.setpartprice(username,partvccode,price);
			out.write("");
		}
	}
	if(actiontype.equalsIgnoreCase("rolemanage"))		
	{
		String domethod=request.getParameter("method");
		RoleAction roleaction=new RoleAction(request);		
		if(domethod.equalsIgnoreCase("new"))
		{
			String parames=request.getParameter("parames");
			//parames=StringUtils.unescape(parames);
			String[] v=parames.split(",");
			String v1=URLDecoder.decode(v[0],"UTF-8");
			String v2=URLDecoder.decode(v[1],"UTF-8");
			String v3=URLDecoder.decode(v[2],"UTF-8");
			String id=roleaction.createRole(v1,v2,v3);
			if(id=="")
			{				
				String error=pageContext.getRequest().getAttribute("error").toString();
				out.write(id+","+java.net.URLDecoder.decode(error));
			}
			else
			{			
				out.write(id);
			}
		}
		if(domethod.equalsIgnoreCase("edit"))
		{
			String parames=request.getParameter("parames");
			String datakey=request.getParameter("datakey");			
			//parames=StringUtils.unescape(parames);
			String[] v=parames.split(",");
			String v1=URLDecoder.decode(v[0],"UTF-8");
			String v2=URLDecoder.decode(v[1],"UTF-8");
			String v3=URLDecoder.decode(v[2],"UTF-8");
			String id=roleaction.saveRole(datakey,v1,v2,v3);
			if(id=="")
			{				
				String error=pageContext.getRequest().getAttribute("error").toString();
				out.write(id+","+java.net.URLDecoder.decode(error));
			}
			else
			{			
				out.write(id);
			}
			
		}
		if(domethod.equalsIgnoreCase("delete"))
		{
			String parames=request.getParameter("parames");			
			String rtn=roleaction.delRole(parames);
			if(rtn=="")
			{				
				String error=pageContext.getRequest().getAttribute("error").toString();
				out.write(rtn+","+java.net.URLDecoder.decode(error));
			}
			else
			{			
				out.write(rtn);
			}
		}
		if(domethod.equalsIgnoreCase("managerolepriv"))
		{
			String modename=request.getParameter("modename");
			String roleid=request.getParameter("roleid");
			String checked=request.getParameter("checked");
			String rtn="";
			if(checked.equalsIgnoreCase("true"))
				rtn=roleaction.addRolePriv(roleid,modename);
			else
				rtn=roleaction.deleteRolePriv(roleid,modename);					
			out.write(rtn);			
		}
		if(domethod.equalsIgnoreCase("getrolepriv"))
		{			
			String roleid=request.getParameter("roleid");
			List ls=roleaction.getRolePriv(roleid);
			String modenames="";
			for(int i=0;i<ls.size();i++)
			{
				Hashtable ht=(Hashtable)ls.get(i);
				String modename=ht.get("modename").toString();
				if(modenames.equalsIgnoreCase(""))
					modenames=modename;
				else
					modenames=modenames+","+modename;
			}
			out.write(modenames);			
		}	
	}	
	if(actiontype.equalsIgnoreCase("usermanage"))		
	{
		String domethod=request.getParameter("method");
		UserAction useraction=new UserAction(request);		
		if(domethod.equalsIgnoreCase("new"))
		{
			String parames=request.getParameter("parames");
			//parames=StringUtils.unescape(parames);
			String[] v=parames.split(",");
			String v1=URLDecoder.decode(v[0],"UTF-8");
			String v2=URLDecoder.decode(v[1],"UTF-8");
			String v3=URLDecoder.decode(v[2],"UTF-8");
			String v4=URLDecoder.decode(v[3],"UTF-8");
			String v5=URLDecoder.decode(v[4],"UTF-8");
			String v6=URLDecoder.decode(v[5],"UTF-8");
			String id=useraction.createUser(v1,v3,v4,v5,v6,v2);
			if(id=="")
			{				
				String error=pageContext.getRequest().getAttribute("error").toString();
				out.write(id+","+java.net.URLDecoder.decode(error));
			}
			else
			{			
				out.write(id);
			}
		}
		if(domethod.equalsIgnoreCase("edit"))
		{
			String parames=request.getParameter("parames");
			String datakey=request.getParameter("datakey");
			//parames=StringUtils.unescape(parames);
			String[] v=parames.split(",");
			String v1=URLDecoder.decode(v[0],"UTF-8");
			String v2=URLDecoder.decode(v[1],"UTF-8");
			String v3=URLDecoder.decode(v[2],"UTF-8");
			String v4=URLDecoder.decode(v[3],"UTF-8");
			String v5=URLDecoder.decode(v[4],"UTF-8");
			String v6=URLDecoder.decode(v[5],"UTF-8");
			String id=useraction.saveUser(datakey,v1,v3,v4,v5,v6,v2);
			if(id=="")
			{				
				String error=pageContext.getRequest().getAttribute("error").toString();
				out.write(id+","+java.net.URLDecoder.decode(error));
			}
			else
			{			
				out.write(id);
			}
			
		}
		if(domethod.equalsIgnoreCase("delete"))
		{
			String parames=request.getParameter("parames");			
			String rtn=useraction.delUser(parames);
			if(rtn=="")
			{				
				String error=pageContext.getRequest().getAttribute("error").toString();
				out.write(rtn+","+java.net.URLDecoder.decode(error));
			}
			else
			{			
				out.write(rtn);
			}
		}
		if(domethod.equalsIgnoreCase("getuserpriv"))
		{
			String userid=request.getParameter("userid");			
			List ls=useraction.getuserpriv(userid);
			String modenames="";
			for(int i=0;i<ls.size();i++)
			{
				Hashtable ht=(Hashtable)ls.get(i);
				String modename=ht.get("modename").toString();
				if(modenames.equalsIgnoreCase(""))
					modenames=modename;
				else
					modenames=modenames+","+modename;
			}
			out.write(modenames);
		}
		if(domethod.equalsIgnoreCase("manageuserpriv"))
		{
			String modename=request.getParameter("modename");
			String userid=request.getParameter("userid");
			String checked=request.getParameter("checked");
			String rtn="";
			if(checked.equalsIgnoreCase("true"))
				rtn=useraction.addUserPriv(userid,modename);
			else
				rtn=useraction.deleteUserPriv(userid,modename);					
			out.write(rtn);			
		}
	}
	
	if(actiontype.equalsIgnoreCase("uploadpartphoto"))
	{
		BaseUploadFile uploadfile=new BaseUploadFile();
		String method=request.getParameter("method");
		String partid=request.getParameter("partid");
		String photoid=request.getParameter("photoid");
		if(method.equalsIgnoreCase("new"))
		{	
			PartPhotoAction partphotoaction=new PartPhotoAction(request);	
			String id=partphotoaction.createSmPartPhoto(partid);
			if(id.equalsIgnoreCase("-1")==false)
			{				
				Hashtable values=new Hashtable();
				List filelist=uploadfile.UploadFile(pageContext,values,id);
				if(filelist.size()>0)
				{
					partphotoaction.refresh(pageContext,"Add Success!","parent.uploadsuccess("+id+");",true);
				}
			}
		}
		if(method.equalsIgnoreCase("edit"))
		{			
			PartPhotoAction partphotoaction=new PartPhotoAction(request);	
			if(photoid.equalsIgnoreCase("-1")==false)
			{				
				Hashtable values=new Hashtable();
				List filelist=uploadfile.UploadFile(pageContext,values,photoid);
				if(filelist.size()>0)
				{
					partphotoaction.refresh(pageContext,"Update Sucess!","parent.uploadsuccess("+photoid+");",true);
				}
			}
		}
		if(method.equalsIgnoreCase("del"))
		{	
			PartPhotoAction partphotoaction=new PartPhotoAction(request);	
			String rtn=partphotoaction.delSmPartPhoto(photoid);
			if(rtn.equalsIgnoreCase("0"))
			{				
				Hashtable values=new Hashtable();
				String path=uploadfile.getUploadTargetFolder(pageContext,"photo");
				String fullname=path+"/"+photoid+".jpg";
				boolean b=uploadfile.deletefile(fullname);				
			}
		}
	}
	if(actiontype.equalsIgnoreCase("asmlinkmanage"))
	{
		BaseUploadFile uploadfile=new BaseUploadFile();
		String method=request.getParameter("method");
		AsmLinkAction action=new AsmLinkAction(request);	
		if(method.equalsIgnoreCase("import"))
		{		
			Hashtable values=new Hashtable();
			String id=action.getXlsId();
			try
			{
				List filelist=uploadfile.ImportXls(pageContext,values,id);				
				if(filelist.size()>0)
				{					
					Hashtable ht=(Hashtable)filelist.get(0);
					java.io.File file=(java.io.File)ht.get("file");
					String errorrows=action.importAsm(file.getPath());
					if(errorrows.equalsIgnoreCase("")==false)
					{
						action.refresh(pageContext,"导入完成,请检查错误!","parent.importsuccess("+id+",'"+errorrows+"');",false);
					}
					else
					{
						action.refresh(pageContext,"导入完成!","parent.importsuccess("+id+",'"+errorrows+"');",true);
					}
				}
			}
			catch(Exception ex)
			{
				action.refresh(pageContext,"导入失败!"+ex.getMessage(),"",true);
			}
		}
	}
	if(actiontype.equalsIgnoreCase("vehlinkmanage"))
	{
		BaseUploadFile uploadfile=new BaseUploadFile();
		String method=request.getParameter("method");
		VehLinkAction action=new VehLinkAction(request);	
		if(method.equalsIgnoreCase("import"))
		{		
			Hashtable values=new Hashtable();
			String id=action.getXlsId();
			try
			{
				List filelist=uploadfile.ImportXls(pageContext,values,id);				
				if(filelist.size()>0)
				{					
					Hashtable ht=(Hashtable)filelist.get(0);
					java.io.File file=(java.io.File)ht.get("file");
					String errorrows=action.importVehLink(file.getPath());
					if(errorrows.equalsIgnoreCase("")==false)
					{
						action.refresh(pageContext,"导入完成,请检查错误!","parent.importsuccess("+id+",'"+errorrows+"');",false);
					}
					else
					{
						action.refresh(pageContext,"导入完成!","parent.importsuccess("+id+",'"+errorrows+"');",true);
					}
					
				}
			}
			catch(Exception ex)
			{
				action.refresh(pageContext,"导入失败!"+ex.getMessage(),"",true);
			}
		}
	}
	if(actiontype.equalsIgnoreCase("bulletin"))
	{
		BaseUploadFile uploadfile=new BaseUploadFile();
		String method=request.getParameter("method");
		BulletinAction action=new BulletinAction(request);	
		if(method.equalsIgnoreCase("new"))
		{		
			Hashtable values=new Hashtable();
			String id=action.getNewBulletinId();
			try
			{
				List filelist=uploadfile.UploadPDF(pageContext,values,id);				
				if(filelist.size()>0)
				{
					String topic=values.get("topic").toString();
					id=action.createBulletin(topic,id);
					action.refresh(pageContext,"公告新增成功!","parent.uploadsuccess("+id+");",true);
				}
			}
			catch(Exception ex)
			{
				action.refresh(pageContext,"公告新增失败!"+ex.getMessage(),"",true);
			}
		}
		if(method.equalsIgnoreCase("del"))
		{
			String id=request.getParameter("id");	
			String rtn=action.delBulletin(id);
			if(rtn.equalsIgnoreCase("0"))
			{				
				Hashtable values=new Hashtable();
				String path=uploadfile.getUploadTargetFolder(pageContext,"pdf");
				String fullname=path+"/"+id+".pdf";
				boolean b=uploadfile.deletefile(fullname);
			}
		}
		if(method.equalsIgnoreCase("publish"))
		{
			String id=request.getParameter("id");	
			String rtn=action.publishBulletin(id);			
		}
	}	
	if(actiontype.equalsIgnoreCase("suggest"))
	{
		String method=request.getParameter("method");
		if(method.equalsIgnoreCase("publish"))
		{
			String suggestcontent=request.getParameter("suggestcontent");
			String vehid=request.getParameter("vehid");
			String imageid=request.getParameter("imageid");
			String path=request.getParameter("path");
			String subject=request.getParameter("subject");
			String fromwho=request.getSession().getAttribute("currentuserid").toString();
			if(suggestcontent==null) suggestcontent="";
			suggestcontent=suggestcontent.trim();
			if(vehid==null) vehid="";
			vehid=vehid.trim();
			if(imageid==null) imageid="";
			imageid=imageid.trim();
			if(path==null) path="";
			path=path.trim();
			if(subject==null) subject="";
			subject=subject.trim();
			SuggestAction suggestaction=new SuggestAction(request);
			String suggestid=suggestaction.getNewSuggestId();
			suggestaction.createSuggest(subject,path,suggestcontent,fromwho,suggestid,vehid,imageid);
			suggestaction.refresh(pageContext,"Send Success!","",true);
		}
	}
	if(actiontype.equalsIgnoreCase("wallimage"))
	{
		BaseUploadFile uploadfile=new BaseUploadFile();
		String method=request.getParameter("method");
		WallImageAction action=new WallImageAction(request);	
		if(method.equalsIgnoreCase("new"))
		{
			String id=action.getMaxId();
			if(id.equalsIgnoreCase("-1")==false)
			{				
				Hashtable values=new Hashtable();
				List filelist=uploadfile.UploadWallImage(pageContext,values,id);				
				if(filelist.size()>0)
				{
					String imagename=values.get("imagename").toString();
					action.addWallImage(imagename,id);
					action.refresh(pageContext,"导入成功!","parent.uploadsuccess("+id+");",true);
				}
			}
		}
		if(method.equalsIgnoreCase("del"))
		{
			String id=request.getParameter("id");	
			String rtn=action.deleteWallImage(id);
			if(rtn.equalsIgnoreCase("0"))
			{				
				Hashtable values=new Hashtable();
				String path=uploadfile.getUploadTargetFolder(pageContext,"wall");
				String fullname=path+"/"+id+".jpg";
				boolean b=uploadfile.deletefile(fullname);
			}
		}
		if(method.equalsIgnoreCase("changeshowmodel"))
		{
			String checked=request.getParameter("checked");
			String checkstatus=request.getParameter("checkstatus");
			if(checkstatus.equalsIgnoreCase("single") && checked.equalsIgnoreCase("true"))
				action.updateImageShowModel("single");
			if(checkstatus.equalsIgnoreCase("all") && checked.equalsIgnoreCase("true"))
				action.updateImageShowModel("all");			
		}
		if(method.equalsIgnoreCase("changeshowstatus"))
		{
			String checked=request.getParameter("checked");
			String id=request.getParameter("id");			
			action.updateWallImageShow(id,checked);			
		}
	}
%>