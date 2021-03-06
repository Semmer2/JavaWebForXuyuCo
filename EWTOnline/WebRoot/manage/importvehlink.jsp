<%@ page contentType="text/html; charset=GBK"%>
<%
response.setHeader("Cache-Control", "no-cache");
response.setHeader("Pragma", "no-cache");
response.setDateHeader("Expires", 0);
%>
<html>
  <head>
  	<META content=IE=EmulateIE7 http-equiv=X-UA-Compatible>
    <title>导入车型BOM</title>
    <link href="../css/zw.css" rel="stylesheet" type="text/css" />
	<link href="../css/standard.css" rel="stylesheet" type="text/css" />
	<link rel="stylesheet" type="text/css" href="../css/queryview.css"></link>	
	<script src="../scripts/ValidInput.js" type='text/javascript'> </script>
	<script type="text/javascript" src="../scripts/datepicker/WdatePicker.js"></script>
	<script language="javascript">
	function save(myform)
	{
		if(verifyAll(myform))
		{
			wait.style.display="block";
			event.srcElement.enabled=false;
			myform.submit();
		}
	}
	function importsuccess(id,errorrows)
	{
		wait.style.display="none";
		var parentwindow=window.dialogArguments;		
		if(parentwindow!=null)
		{
			document.all.errors.value=errorrows;
			parentwindow.onAfterImportSuccess(id);	
		}
	}
	</script>
  </head>
<body scroll="no">
	<form action='../servlet.jsp?action=vehlinkmanage&method=import' enctype="multipart/form-data" method='post' style="margin: 0px" target='postfrm' name="dataform">
	<table align='center' width='100%' border='1' cellpadding='1' cellspacing='0' bordercolorlight='#25689F' bordercolordark='#ffffff'>
		<tr>
			<td align="center" class='cuteLable' colspan="2">请选择xls或xlsx文件</td>			
		</tr>
		<tr>
			<td align="center" class='cuteLable' width="40%">数据格式</td>
			<td>			
				车型编号,零件号,爆炸图号,热点,数量							
			</td>
		</tr>
		<tr>
			<td align="center" class='cuteLable' width="40%">数据文件</td>
			<td>			
			<input type='file' id='newfilename' name='newfilename' size="40" value=""/>				
			</td>
		</tr>
		<tr>
			<td align="center" class='cuteLable' width="40%">错误行号</td>
			<td>			
			<textarea id="errors" name="errors" rows=4 cols="50" readOnly style="background-color:#DCDCDC"></textarea>				
			</td>
		</tr>		
	</table>
	<p align='center'>		
		<input type="button" onclick="return save(document.dataform);" class="btn" value="确定"></input>
		&nbsp;&nbsp;		
		<input class="btn" type="button" value="关闭" onclick="window.close();"></input>
	</p>
	</form>
	<table width="90%" border=0 align="center" style="display:none" id="wait">
	<tr>
		<td align="center" valign="middle">
			<img src="../images/loading.gif"/>			
		</td>
	</tr>
	<tr>
		<td align="center" valign="middle">
			文件正在上传,请稍等。		
		</td>
	</tr>	
	<iframe frameborder='0' name='postfrm' id='postfrm' src='' width='0' height='0'></iframe>
</body>
</html>