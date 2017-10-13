
  var modified=0
  var currentLine=-1//该变量似乎保存"ln"属性；lightOn函数中line记录实际行数
  var oldLine=-1;
  var currentlightvalues=null;//array 从1开始,不包括序号
  var currentRowvalue=-1;
  var line=-1;
  var _mainid;
 
function fixgridsize(wc)
{
	var avaiheight=document.body.clientHeight;
	if(wc>30) wc=58;
	var gridheight=avaiheight+"px";
	if(avaiheight>wc) gridheight=(avaiheight-wc)+"px";
	//document.getElementById("tCtable").height=gridheight;
	document.getElementById("tC").style.height=gridheight;
	var avaiwidth=document.body.clientWidth;
	var gridwidth=(avaiwidth-5)+"px";	
	//document.getElementById("tCtable").width=gridwidth;
	document.getElementById("tC").style.width=gridwidth;
	if(document.getElementById("tblStat")!=null) document.getElementById("tblStat").style.width=gridwidth;
    if(document.getElementById("bottomtable")!=null) document.getElementById("bottomtable").style.width=gridwidth;    
}
function fixgridsize(parentheight,parentwidth)
{
	var avaiheight=parentheight;	
	var gridheight=avaiheight+"px";
	gridheight=avaiheight+"px";
	//document.getElementById("tCtable").height=gridheight;
	document.getElementById("tC").style.height=gridheight;
	var avaiwidth=parentwidth;
	var gridwidth=avaiwidth+"px";	
	//document.getElementById("tCtable").width=gridwidth;
	document.getElementById("tC").style.width=gridwidth;
	if(document.getElementById("tblStat")!=null) document.getElementById("tblStat").style.width=gridwidth;
    if(document.getElementById("bottomtable")!=null) document.getElementById("bottomtable").style.width=gridwidth;    
}
  function ondblcellclick()
  {
	  if(typeof(ondblrow)=="function")
	  {
	    	ondblrow();	
	  }
  }
  function getlightOnValues()
  {
	  var values = new Array();	  
	  if(currentLine!="-1")
	  {
	    for(var i=0;i<document.all.tablebody.rows.length;i++)
   		{
   			var row=document.all.tablebody.rows(i);
   			if((i+1)==currentLine)
			{				
   				if(typeof(row.cells(0).tag)=="undefined")   				
   				 currentRowvalue=row.cells(0).innerText;
   				else
   				 currentRowvalue=row.cells(0).tag;
   				for(var c=1;c<row.cells.length;c++)
   				{
   					var v="";
   					if(row.cells(c).children.length==0)
		    		{
		    			if(typeof(row.cells(c).tag)=="undefined")
   							v=row.cells(c).innerText;
		    			else
		    				v=row.cells(c).tag;
		    		}
		    		else
		    		{
	   					if(row.cells(c).children.item(0).type=="checkbox")
						{						
	   						v=row.cells(c).children.item(0).value;
						}
   					}
   					values[c-1]=v;   					
   				}   				
			}
   		}
	  }
	  currentlightvalues=values;
  }
  function onNewRow(cellvalues)
    {
    	var row=document.all.exampleemptytable.rows(0);    	
    	var values=cellvalues.split(",");
    	var totalcount="";
    	if(typeof(document.all.totalcount)!="undefined") totalcount=document.all.totalcount.innerText;
    	if(totalcount=="0") document.all.tablebody.deleteRow(0);
    	//第一列为序号列,自动生成
    	row.cells(0).innerText=document.all.tablebody.rows.length+1;
    	row.cells(0).title=document.all.tablebody.rows.length+1;
    	//从第二列开始cells(i+1)
    	//alert(values.length);
    	for(var i=0;i<values.length;i++)
    	{    	
    		var v=unescape(values[i]);
    		var varray=v.split(";");
    		if(row.cells(i+1).children.length==0)
    		{
    			if(varray.length==2)
    			{
    				row.cells(i+1).innerText=varray[0];
    				row.cells(i+1).tag=varray[1];
    			}
    			else
    			{
    				row.cells(i+1).innerText=varray[0];
    			}
    			row.cells(i+1).title=varray[0];
    		}
    		else
    		{
    			if(row.cells(i+1).children.length==1)
    			{
    				if(row.cells(i+1).children.item(0).tagName=="INPUT")
    				{
    					if(row.cells(i+1).children.item(0).type=="checkbox")
    					{
    						row.cells(i+1).children.item(0).value=v;
    						row.cells(i+1).title=v;
    					}
    				}
    			}
    		}    		
    	}
    	var ln=document.all.tablebody.rows.length+1;
    	row.ln=ln;
    	var newrow = row.cloneNode(true);    	    	
    	document.all.tablebody.appendChild(newrow);
    	document.all.tablebody.moveRow(document.all.tablebody.rows.length-1,0);
    	for(var i=0;i<document.all.tablebody.rows.length;i++)
    	{
    	   document.all.tablebody.rows.item(i).ln=i+1;
    	   if((i%2)==0)
	       {	         
    		  document.all.tdt[i].className="gridRowEven";
    		  changeInputCss(document.all.tdt[i],'text','gridRowEven');
	          changeInputCss(document.all.tdt[i],'checkbox','gridRowEven');
	       }
	       else
	       {
	          document.all.tdt[i].className="gridRowOdd";
	          changeInputCss(document.all.tdt[i],'text','gridRowOdd');
	       	  changeInputCss(document.all.tdt[i],'checkbox','gridRowOdd');
	       }
    	}
    	if(typeof(document.all.totalcount)!="undefined") document.all.totalcount.innerText=document.all.tablebody.rows.length; 	
    }
    function onSaveRow(keyword,cellvalues)
    {    		
    	var values=cellvalues.split(",");
    	for(var i=0;i<document.all.tablebody.rows.length;i++)
   		{
   			var row=document.all.tablebody.rows(i); 
   			if(row.cells(1).children.item(0).tagName=="INPUT")
			{
				if(row.cells(1).children.item(0).type=="checkbox")
				{
					if(row.cells(1).children.item(0).value==keyword)
					{
						 //从第二列开始cells(i+2)
				    	for(var j=0;j<values.length;j++)
				    	{    	
				    		var v=unescape(values[j]);
				    		v=v.split(";");
				    		if(row.cells(j+1).children.length==0)
				    		{
				    			if(v.length==2)
				    			{
				    				row.cells(j+1).innerText=v[0];
				    				row.cells(j+1).tag=v[1];
				    			}
				    			else
				    			{
				    				row.cells(j+1).innerText=v[0];
				    			}
				    		}
				    		row.cells(j+1).title=v[0];
				    	} 							
					}
				}
			}
   		}
    }
    function onDeleteRowByIndex(rowindex)
    {
    	document.all.tablebody.deleteRow(rowindex-1);//rowindex是从0开始   						
    	for(var i=0;i<document.all.tablebody.rows.length;i++)
    	{    	   
    	   document.all.tablebody.rows.item(i).ln=i+1;
    	   if((i%2)==0)
	       {	         
    		  document.all.tdt[i].className="gridRowEven";
    		  changeInputCss(document.all.tdt[i],'text','gridRowEven');
	          changeInputCss(document.all.tdt[i],'checkbox','gridRowEven');
	       }
	       else
	       {
	          document.all.tdt[i].className="gridRowOdd";
	          changeInputCss(document.all.tdt[i],'text','gridRowOdd');
	       	  changeInputCss(document.all.tdt[i],'checkbox','gridRowOdd');
	       }
    	}
    	if(typeof(document.all.totalcount)!="undefined") document.all.totalcount.innerText=document.all.tablebody.rows.length;
    	currentLine=-1;
  		oldLine=-1;
    }
    function onDeleteRow()
    {    	
    	var ids=getSelectIds();
    	var idarray=ids.split(",");
    	for(var j=0;j<idarray.length;j++)
    	{    	
	    	for(var i=0;i<document.all.tablebody.rows.length;i++)
    		{
    			var row=document.all.tablebody.rows(i); 
    			if(row.cells(1).children.item(0).tagName=="INPUT")
   				{
   					if(row.cells(1).children.item(0).type=="checkbox")
   					{
   						if(row.cells(1).children.item(0).value==idarray[j])
   						{
   							document.all.tablebody.deleteRow(i);
   							break;    							
   						}
   					}
   				}
    		}
    	}
    	for(var i=0;i<document.all.tablebody.rows.length;i++)
    	{    	   
    	   document.all.tablebody.rows.item(i).ln=i+1;
    	   if((i%2)==0)
	       {	         
    		  document.all.tdt[i].className="gridRowEven";
    		  changeInputCss(document.all.tdt[i],'text','gridRowEven');
	          changeInputCss(document.all.tdt[i],'checkbox','gridRowEven');
	       }
	       else
	       {
	          document.all.tdt[i].className="gridRowOdd";
	          changeInputCss(document.all.tdt[i],'text','gridRowOdd');
	       	  changeInputCss(document.all.tdt[i],'checkbox','gridRowOdd');
	       }
    	}
    	if(typeof(document.all.totalcount)!="undefined") document.all.totalcount.innerText=document.all.tablebody.rows.length;
    	currentLine=-1;
  		oldLine=-1;
    }
  //数据高亮度显示
  function lightOn(ln)
  {	   
	var elementobject=event.srcElement;
	var startindex=document.all.tdt[0].ln;
	while(elementobject.tagName!="TR")
	{
		elementobject=elementobject.parentElement;
	}
	ln=elementobject.ln;
	currentLine=ln-startindex+1;
	getlightOnValues();
    if(currentLine==oldLine) return;
    if(currentLine>0)
    {
      document.all.tdt[currentLine-1].className="tableDataHit";
      changeInputCss(document.all.tdt[currentLine-1],'text','tableDataHit');
      changeInputCss(document.all.tdt[currentLine-1],'checkbox','tableDataHit');
      changeRowNumColumn(tblStat.rows.item(currentLine));
      if(oldLine>0)
      {
    	  if(((oldLine-1)%2)==0)
	       {	         
    		  document.all.tdt[oldLine-1].className="gridRowEven";
    		  changeInputCss(document.all.tdt[oldLine-1],'text','gridRowEven');
	          changeInputCss(document.all.tdt[oldLine-1],'checkbox','gridRowEven');
	          changeRowNumColumn(tblStat.rows.item(oldLine));
	       }
	       else
	       {
	          document.all.tdt[oldLine-1].className="gridRowOdd";
	          changeInputCss(document.all.tdt[oldLine-1],'text','gridRowOdd');
	       	  changeInputCss(document.all.tdt[oldLine-1],'checkbox','gridRowOdd');
	          changeRowNumColumn(tblStat.rows.item(oldLine));
	       }
     }        
    }else{
	      document.all.tdt.className="tableDataHit";
	      changeInputCss(document.all.tdt,'text','tableDataHit');
	      changeInputCss(document.all.tdt,'checkbox','tableDataHit');
    }
    oldLine=currentLine;
    if(typeof(onLight)=="function")
    {
    	onLight();	
    }
  }
  
  
  //数据高亮度显示
  function lightOnByRow(rowobject)
  {
	var elementobject=rowobject;	
	if(elementobject!=null)
	{
		while(elementobject.tagName!="TR")
		{
			elementobject=elementobject.parentElement;
		}
		var ln=elementobject.ln;
		currentLine=ln;
		getlightOnValues();
	    if(currentLine==oldLine) return;
	    if(currentLine>0)
	    {
	      document.all.tdt[currentLine-1].className="tableDataHit";
	      changeInputCss(document.all.tdt[currentLine-1],'text','tableDataHit');
	      changeInputCss(document.all.tdt[currentLine-1],'checkbox','tableDataHit');
	      changeRowNumColumn(tblStat.rows.item(currentLine));
	      if(oldLine>0)
	      {
	    	  if(((oldLine-1)%2)==0)
		       {	         
	    		  document.all.tdt[oldLine-1].className="gridRowEven";
	    		  changeInputCss(document.all.tdt[oldLine-1],'text','gridRowEven');
		          changeInputCss(document.all.tdt[oldLine-1],'checkbox','gridRowEven');
		          changeRowNumColumn(tblStat.rows.item(oldLine));
		       }
		       else
		       {
		          document.all.tdt[oldLine-1].className="gridRowOdd";
		          changeInputCss(document.all.tdt[oldLine-1],'text','gridRowOdd');
		       	  changeInputCss(document.all.tdt[oldLine-1],'checkbox','gridRowOdd');
		          changeRowNumColumn(tblStat.rows.item(oldLine));
		       }
	     }        
	    }else{
		      document.all.tdt.className="tableDataHit";
		      changeInputCss(document.all.tdt,'text','tableDataHit');
		      changeInputCss(document.all.tdt,'checkbox','tableDataHit');
	    }
	    oldLine=currentLine;
	    if(typeof(onLight)=="function")
	    {
	    	onLight();	
	    }
	 }
	else
	{
		document.all.tdt[oldLine-1].className="gridRowOdd";
        changeInputCss(document.all.tdt[oldLine-1],'text','gridRowOdd');
       	changeInputCss(document.all.tdt[oldLine-1],'checkbox','gridRowOdd');
        changeRowNumColumn(tblStat.rows.item(oldLine));
        currentLine=-1;
        oldLine=-1
	}	
  }
  
  //数据高亮度显示,但不调用onlight
  function lightOnByRowNoCallLight(rowobject)
  {
	var elementobject=rowobject;	
	if(elementobject!=null)
	{
		while(elementobject.tagName!="TR")
		{
			elementobject=elementobject.parentElement;
		}
		var ln=elementobject.ln;
		currentLine=ln;
		getlightOnValues();
	    if(currentLine==oldLine) return;
	    if(currentLine>0)
	    {
	      document.all.tdt[currentLine-1].className="tableDataHit";
	      changeInputCss(document.all.tdt[currentLine-1],'text','tableDataHit');
	      changeInputCss(document.all.tdt[currentLine-1],'checkbox','tableDataHit');
	      changeRowNumColumn(tblStat.rows.item(currentLine));
	      if(oldLine>0)
	      {
	    	  if(((oldLine-1)%2)==0)
		       {	         
	    		  document.all.tdt[oldLine-1].className="gridRowEven";
	    		  changeInputCss(document.all.tdt[oldLine-1],'text','gridRowEven');
		          changeInputCss(document.all.tdt[oldLine-1],'checkbox','gridRowEven');
		          changeRowNumColumn(tblStat.rows.item(oldLine));
		       }
		       else
		       {
		          document.all.tdt[oldLine-1].className="gridRowOdd";
		          changeInputCss(document.all.tdt[oldLine-1],'text','gridRowOdd');
		       	  changeInputCss(document.all.tdt[oldLine-1],'checkbox','gridRowOdd');
		          changeRowNumColumn(tblStat.rows.item(oldLine));
		       }
	     }        
	    }else{
		      document.all.tdt.className="tableDataHit";
		      changeInputCss(document.all.tdt,'text','tableDataHit');
		      changeInputCss(document.all.tdt,'checkbox','tableDataHit');
	    }
	    oldLine=currentLine;	    
	 }
	else
	{
		document.all.tdt[oldLine-1].className="gridRowOdd";
        changeInputCss(document.all.tdt[oldLine-1],'text','gridRowOdd');
       	changeInputCss(document.all.tdt[oldLine-1],'checkbox','gridRowOdd');
        changeRowNumColumn(tblStat.rows.item(oldLine));
        currentLine=-1;
        oldLine=-1
	}	
  }
  
  function viewdetail(objid,viewdetail,viewwidth,viewheight)
  {
   	if(viewdetail!="")
   	{
   		if(typeof viewwidth=="undefined"||typeof viewheight=="undefined")
	   	{
	   		var detailwindow=window.open(viewdetail+objid,"_blank","menubar=0,toolbar=0,directories=0,location=0,status=1,scrollbars,resizable");
		   	detailwindow.moveTo(0,0);
		    detailwindow.resizeTo(screen.width,screen.height);	    
		    detailwindow.focus();
	   	}
	   	else if(viewwidth!="" && viewheight!="")
	   	{
	   		var detailwindow=window.open(viewdetail+objid,"_blank","menubar=0,toolbar=0,directories=0,location=0,status=1,scrollbars,resizable");
		   	detailwindow.moveTo((screen.width-viewwidth)/2,(screen.height-viewheight)/2);
		    detailwindow.resizeTo(viewwidth,viewheight);	    
		    detailwindow.focus();
	   	}
	   	else
	   	{
	   		var detailwindow=window.open(viewdetail+objid,"_blank","menubar=0,toolbar=0,directories=0,location=0,status=1,scrollbars,resizable");
		   	detailwindow.moveTo(0,0);
		    detailwindow.resizeTo(screen.width,screen.height);	    
		    detailwindow.focus();
	   	}
    }
    else
    {
    	//alert("请配置详细页面!");
    }
  }  
  function changeRowNumColumn(_tr)
  {
    if(_tr==null) return;
	if (_tr.cells.item(0).firstChild != null)
    {
        if(typeof(_tr.cells.item(0).childNodes.item(0).tagName)=='undefined')
        {
		    var param=_tr.cells.item(0).innerText;
		    var keyword=_tr.cells.item(0).tag;//主键
		    var viewdetail=_tr.cells.item(0).viewdetail;
		    var viewwidth=_tr.cells.item(0).viewwidth;
		    var viewheight=_tr.cells.item(0).viewheight;
		    
		    if(typeof(viewdetail)!="undefined" && viewdetail!="")
		    {
		    	if(keyword!="")  _tr.cells.item(0).innerHTML="<img src='/EWTOnline/images/datagrid/detailView.gif' border=0 onclick='viewdetail(\""+keyword+"\",\""+viewdetail+"\",\""+viewwidth+"\",\""+viewheight+"\")' style='cursor:hand' title='查看详细数据' tag='"+param+"'>";
		    }
	    }
	    else
	    {
	    	if(_tr.cells.item(0).childNodes.item(0).tagName=='IMG')
	    	{
	    		var param=_tr.cells.item(0).childNodes.item(0).tag;
	    		_tr.cells.item(0).innerHTML=param;
	    	}
	    }	    
    }    
  }
  
  function changeInputCss(_tr,_type,_css)
  {
    if(_tr.tagName=="TR")
    {
		for (var i=0; i<_tr.cells.length; i++)
	    {
	      if (_tr.cells[i].firstChild != null){
	        if (_tr.cells[i].firstChild.type=='text' && _tr.className=='tableDataHit' &&
	        _tr.cells[i].firstChild.className=='inputData')
	        {
	          continue;
	        }
	        if (_tr.cells[i].firstChild.type == _type){
	          _tr.cells[i].firstChild.className = _css;
	        }
	      }
	    }
	}
  }

  //单击事件改变input的样式
  /*function changeInputClass(obj)
  {
    if (obj.ln >= 1){
      var trObj;
      if(document.all.tdt.length == undefined)
        trObj = document.all.tdt;
      else
        trObj = document.all.tdt[obj.ln-1];

      for (var i=0;i<trObj.cells.length;i++){
        if (trObj.cells[i].firstChild=='object' && trObj.cells[i].firstChild.type=='text')
        {
          trObj.cells[i].firstChild.className = 'tableDataHit';
        }
      }
    }
    obj.className = 'inputData';
  }*/
  
  function changeInputClass(obj)
  {
    if (obj.ln >= 1){
      var trObj;
      trObj=obj.parentElement.parentElement;
      if(trObj!=null)
      {
	      for (var i=0;i<trObj.cells.length;i++){
	        if (trObj.cells[i].firstChild=='object' && trObj.cells[i].firstChild.type=='text')
	        {
	          trObj.cells[i].firstChild.className = 'tableDataHit';
	        }
	      }
      }
    }
    obj.className = 'inputData';
  }

  function selectRow(ln)
  {
    if (ln==null)
      ln=parseInt(ln,10);
      //alert(tblStat.rows.length);
    if (tblStat.rows.length == 2){
      line = 1;
    }else{
      for (i=0;i<tblStat.rows.length-1;i++){
        if (tdt[i].ln == ln){
          line=i+1;
        }
      }
    }
  }
  
//导出所有数据
	function exportall() {
		frmShow.output.value='all';
		frmShow.target='hiddenframe';
		frmShow.submit();
		frmShow.output.value='';
	}
//导出当前页的数据

	function exportcurrent() {
		frmShow.output.value='current';
		frmShow.target='hiddenframe';
		frmShow.submit();
		frmShow.output.value='';
	}
  
  function addTableRow(mainid)
  {
     _mainid=mainid;
    if (line != -1){   //table大于一行的情况

      newRow=tblStat.insertRow(line+1);
      newRow.id="tdt";
      newRow.ln=line+1;
      newRow.bgColor="#e0e0e0";
      newRow.className="newRow";
      for (var i=0; i<tblStat.rows[line].cells.length; i++)
      {
        temp = newRow.insertCell(i);
        var _tdHTML = tblStat.rows[line].cells[i].innerHTML;
        if (tblStat.rows[line].cells[i].firstChild.className != ''){
          _tdHTML = parseHTML(_tdHTML,"tableDataHit","inputData");
        }

        var change = "onchange=changeCheckBox("+(line-1)+")";

        if (tblStat.rows[line].cells[i].iscopy == 'true'){
            _tdHTML = parseHTML(_tdHTML,"onclick=changeInputClass(this)","");
            _tdHTML = parseHTML(_tdHTML,change,"");
            temp.innerHTML = _tdHTML;
        }
        else{
          _tdHTML = parseHTML(_tdHTML,"onclick=changeInputClass(this)","");
          _tdHTML = parseHTML(_tdHTML,change,"");
          temp.innerHTML = _tdHTML;
     //     alert(i+"---"+temp.firstChild.type);
          if(temp.firstChild.type == 'text'){
            temp.firstChild.value ='';
          }else{
            temp.innerHTML = '<font color=red>*</font>';
          }
        }
        temp.align = tblStat.rows[2].cells[i].align;
        //改变text的只读属性
        if (temp.firstChild.readOnly == true){
          temp.firstChild.readOnly = false;
        }
        //改变text的ID,NAME
        if (temp.firstChild.type == 'checkbox'){
          temp.firstChild.disabled = true;
          temp.firstChild.value = mainid;
        }
        var dept_object=document.getElementById("dept_organ_name");
        var deptName="";
        if(dept_object!=null)
          deptName = document.getElementById("dept_organ_name").value;

        if (temp.firstChild.type == 'text'){
          var inputName = temp.firstChild.name;
          var replacedText = pickNumber(inputName);
          temp.innerHTML = parseHTML(temp.innerHTML,replacedText,mainid);
          //alert(temp.firstChild.id);
          var _name=temp.firstChild.id;
          //temp.firstChild.attributes['name'].value=_name;
          //temp.firstChild.attributes['name'].value);
          var temphtml=temp.innerHTML;
          //alert("html:"+temphtml+"\n name:"+temp.firstChild.attributes['name'].value+"   \n id:"+temp.firstChild.id);
          //temp.innerHTML = replaceHTML(temphtml,temp.firstChild.attributes['name'].value,_name);
          temp.innerHTML = replaceHTML(temphtml,temp.firstChild.name,_name);
          if(temp.firstChild.value=='一般缺陷'||temp.firstChild.value=='严重缺陷'||temp.firstChild.value=='危急缺陷'){
            temp.firstChild.value='';
          }
          //alert(temp.innerHTML);
          //取消LN值
          var inputLn = temp.firstChild.ln;
          replacedText = 'ln="'+inputLn+'"';
          temp.innerHTML = parseHTML(temp.innerHTML,replacedText," ");//alert("click me: "+temp.innerHTML);
          //获取部门信息
          if (temp.firstChild.name.indexOf("dept_organ_name")>-1){
            temp.firstChild.value = deptName;
          }
        }
        //alert(temp.innerHTML);
      }
    }else{
      var dept_object=document.getElementById("dept_organ_name");
      tblStat.cellspacing=0;
      tblStat.cellpadding=0;
      newRow=tblStat.insertRow(1);
      newRow.id="tdt";
      newRow.ln=1;
      newRow.bgColor="#e0e0e0";
      newRow.className="newRow";
      newRow.height=20;
      if(dept_object != undefined || dept_object != null){
      var cell=newRow.insertCell(0);
      cell.bgColor='white';
      cell.innerHTML="<font color='red'>*</font>";
      cell.align="right";
      cell=newRow.insertCell(1);
      cell.bgColor='white';
      cell.innerHTML="<input type='checkbox'  disabled='disabled' id='chk' name='chk' value='"+mainid+"'/>";
      cell=newRow.insertCell(2);
      cell.bgColor='white';
      cell.innerHTML="<input type='text' id='dept_organ_name"+mainid+"' name='dept_organ_name"+mainid+"' class='tableData' value='"+dept_object.value+"'/>";
      cell=newRow.insertCell(3);
      cell.bgColor='white';
      cell.innerHTML="<input type='text' id='tbr"+mainid+"' name='tbr"+mainid+"' class='tableData'/>";
      cell=newRow.insertCell(4);
      cell.bgColor='white';
      cell.innerHTML="<input type='text' id='estimate_item"+mainid+"' name='estimate_item"+mainid+"' class='tableData'/>";
      cell=newRow.insertCell(5);
      cell.bgColor='white';
      cell.innerHTML="<input type='text' id='estimate_item_class"+mainid+"' name='estimate_item_class"+mainid+"' class='tableData'/>";
      cell=newRow.insertCell(6);
      cell.bgColor='white';
      cell.innerHTML="<input type='text' id='estimate_item_score"+mainid+"' name='estimate_item_score"+mainid+"' class='tableData'/>";
      }
      else{
      var cell=newRow.insertCell(0);
      cell.bgColor='white';
      cell.innerHTML="<font color='red'>*</font>";
      cell.align="right";
      cell=newRow.insertCell(1);
      cell.bgColor='white';
      cell.innerHTML="<input type='checkbox'  disabled='disabled' id='chk' name='chk' value='"+mainid+"'/>";

        for(var j=2;j<tblStat.rows[0].cells.length; j++){
          cell = newRow.insertCell(j);
          cell.bgColor='white';
          cell.innerHTML="<input type='text' id='" + tblStat.rows[0].cells[j].id + mainid+"' name='" + tblStat.rows[0].cells[j].name + mainid+"' class='tableData'/>";
        }
      }
    }

  }

  //从字符串中提取数字
  function pickNumber(_str)
  {
    var temp;
    var pos='';
    var int_chars = "1234567890";
    var i;
    var n;
    for(i=0;i<_str.length;i++){
            if((i+1)<=_str.length&&!isNaN(_str.substring(i,i+1))){
              pos+=_str.substring(i,i+1);
            }

    }   
    return pos;
  }

function replaceHTML(_text,dealtext,replace){
    var temp;
    var tempstr;
    var prestr;
    //alert(_text+"\n"+dealtext+"\n"+replace);
    var beginindex=_text.indexOf("name=");
    tempstr=_text.substring(beginindex);
    prestr=_text.substring(0,beginindex-1);
    temp = tempstr.replace(dealtext,replace);
    return prestr+" "+temp;
}
  //替换字符
  function parseHTML(_text,dealtext,replace)
  {
    var temp;
    temp = _text.replace(dealtext,replace);
    return temp;
  }

  function doNavigate(pstrWhere, pintTot)
  {
          var strTmp;
          var intPg;
          var frmObject;
          if (typeof (document.frmShow) == 'object'){
            frmObject = document.frmShow;
          }else if (typeof (document.frmShow1) == 'object'){
            frmObject = document.frmShow1;
          }else if (typeof (document.frmShow2) == 'object'){
            frmObject = document.frmShow2;
          }else if (typeof (document.frmShow3) == 'object'){
            frmObject = document.frmShow3;
          }          
          strTmp = frmObject.currpage.value;
          intPg = parseInt(strTmp);
          if (isNaN(intPg)) intPg = 1;

          if ((pstrWhere == 'F' || pstrWhere == 'P') && intPg == 1)
          {
                  alert("已经到达第一页!");
                  return;
          }
          else if ((pstrWhere == 'N' || pstrWhere == 'L') && intPg == pintTot)
          {
                  alert("已经到达最后一页!");
                  return;
          }

          if (pstrWhere == 'F')
                  intPg = 1;
          else if (pstrWhere == 'P')
                  intPg = intPg - 1;
          else if (pstrWhere == 'N')
                  intPg = intPg + 1;
          else if (pstrWhere == 'L')
                  intPg = pintTot;

          if (intPg < 1)
                  intPg = 1;
          if (intPg > pintTot)
                  intPg = pintTot;
          frmObject.currpage.value = intPg;
          if (frmObject.target != ''){
            frmObject.target = '';
          }
          
          //将所有页的选中id集合保存起来以便一同处理，必须在框架页加上id为selectedIds的hidden来保存
          var selectedIds;
          if(parent.document.getElementById("selectedIds")){
            selectedIds = parent.document.getElementById("selectedIds");

            var tmpArr1;//该页所有的id集合
            var tmpArr2;//现在该页选中的id集合
            var tmpArr3;//保存原来选中的id集合
            
            tmpArr1 = getCheckIDs().split(",");
            tmpArr2 = getSelectIds().split(",");
            tmpArr3 = selectedIds.value.split(",");

            for(var i=0;i<tmpArr2.length;i++){
                if(arrayContain(tmpArr3,tmpArr2[i])){
                    continue;
                }
                else{
                    tmpArr3.push(tmpArr2[i]);
                }
            }
            for(var i=0;i<tmpArr3.length;i++){
                if(arrayContain(tmpArr1,tmpArr3[i])&&(!arrayContain(tmpArr2,tmpArr3[i]))){
                    tmpArr3[i]='';
                }
            }
            removeEmptyFromArray(tmpArr3);
            
            //alert("tmpArr1="+tmpArr1);
            //alert("tmpArr2="+tmpArr2);
            //alert("tmpArr3="+tmpArr3);
            
            selectedIds.value=tmpArr3;
            //parent.printSelectArr();
          }
          copyFormEleToShowForm();
          frmObject.method="POST";
          if(typeof(frmObject.output)=='object') frmObject.output.value="";
          frmObject.submit();
  }

  function arrayContain(arr, obj)
  {
    for(var i=0;i<arr.length;i++){
        if(arr[i]==obj){
            return true;
        }
    }
    return false;
  }
  
  function removeEmptyFromArray(arr)
  {
    var i=0;
    while(i<arr.length){
        if(arr[i]==''){
            arr.splice(i,1);
            i--;
        }
        i++;
    }
  }
  
  function setChecked()
  {
    var ids = parent.document.getElementById("selectedIds").value.split(",");
    var chks = document.frmShow.elements('chk');

    if(chks!=null){
      for(var i=0;i<chks.length;i++){
        if(arrayContain(ids,chks[i].value)){
            chks[i].checked = true;
        }
      }
    }
  }

  function doSort(pstrFld, pstrOrd)
  {
          document.frmShow.txtSortCol.value = pstrFld;
          document.frmShow.txtSortOrd.value = pstrOrd;
          if(typeof(document.frmShow.output)=='object') document.frmShow.output.value="";
          document.frmShow.submit();
  }

  function getSelValue()
  {
    //将导出execl的标志去除
    if(parent!=null)
    {
	    if(parent.document!=null)
	    {
		    if(parent.document.getElementById("output")!=null)
		    {
		    	if(parent.document.getElementById("output").value=="all" || parent.document.getElementById("output").value=="current")
		    	{
		    		parent.document.getElementById("output").value="";
		    	}
		    }
	    }
    }
    var frmObject;
    if (typeof (document.frmShow) == 'object'){
      frmObject = document.frmShow;
    }else if (typeof (document.frmShow1) == 'object'){
      frmObject = document.frmShow1;
    }else if (typeof (document.frmShow2) == 'object'){
      frmObject = document.frmShow2;
    }else if (typeof (document.frmShow3) == 'object'){
      frmObject = document.frmShow3;
    }
    var seltag = frmObject.txtCurr;
    var selvalue = seltag.options[seltag.selectedIndex].value;
    if (selvalue != ''){
      frmObject.currpage.value = selvalue;
      if (frmObject.target != ''){
        frmObject.target = '';
      }
      copyFormEleToShowForm();
      frmObject.method ='POST';
      if(typeof(frmObject.output)=='object') frmObject.output.value="";
      frmObject.submit();
    }
  }


  function btnChkAll()                  //点击"全选"checkbox
  {
  try{
    if(typeof(document.frmShow.elements('chk').length)=='undefined')
    {
      if(document.frmShow.chkAll.checked)
      {
        document.frmShow.chk.checked=true;
      }
      else
      {
        document.frmShow.chk.checked=false;
      }
    }
    else
    {
      if(document.frmShow.chkAll.checked)
      {
        for(var i=0;i<document.all('chk').length;i++)
          document.frmShow.chk(i).checked=true;
      }
      else
      {
        for(var i=0;i<document.all('chk').length;i++)
          document.frmShow.chk(i).checked=false;
      }
    }
    }catch(e){
    
    }
  }

   function getCheckIDs()
   {
       var values = "";
       var chk = document.frmShow.elements('chk');
       if(chk != null){
         if (typeof(chk) == 'object'){
           if (typeof(chk.length) == 'undefined'){
             values = chk.value;
           }else{
             for(var i=0;i < chk.length;i++)
             {
               if (values != '')
                  values = values + "," + document.frmShow.chk(i).value;
               else
                  values = document.frmShow.chk(i).value;
             }
           }
         }
       }else{
          alert("没有数据!");
       }
       return values;
   }

  function getSelectIds()
  {
       var values = "";
       var chk = document.frmShow.elements('chk');  
       if (document.frmShow.elements('chk') != null){
         if (typeof(chk) == 'object'){
           if (typeof(chk.length) == 'undefined'){
             if (chk.checked)
             values = chk.value;
           }else{
             for(var i=0;i < chk.length;i++)
             {
               if (chk(i).checked){

                 if (values != '')
                  values = values + "," + document.frmShow.chk(i).value;
                 else
                  values = document.frmShow.chk(i).value;
               }else{
                continue;
               }
             }
           }
         }
         return values;
      }else{
        alert("没有数据!");
        return false;
      }  
  }

  function getSelectIds_ext()
  {
       var values = "";
       var chk = document.frmShow.elements('chk');  
       if (document.frmShow.elements('chk') != null){
         if (typeof(chk) == 'object'){
           if (typeof(chk.length) == 'undefined'){
             if (chk.checked)
             values = chk.value;
           }else{
             for(var i=0;i < chk.length;i++)
             {
               if (chk(i).checked){

                 if (values != '')
                  values = values + "," + document.frmShow.chk(i).value;
                 else
                  values = document.frmShow.chk(i).value;
               }else{
                continue;
               }
             }
           }
         }
         return values;
      } 
  }

function getSelectedCommonCols(cols){
  var ret = '';
  if(typeof(cols)!='undefined' && cols != null && cols != ''){
    var chkmainids = getSelectIds();
    var mainidAry = chkmainids.split(',');
    var colAry = cols.split(',');
    var tmp = '';
    for(var i=0;i<mainidAry.length;i++){
      tmp = mainidAry[i];
      for(var j=0;j<colAry.length;j++){
        tmp += ',' + document.getElementById(colAry[j]+''+mainidAry[i]).value;
      }
      if(i == 0)
        ret = tmp;
      else
        ret += ';'+tmp
      tmp = '';
    }
  }
  return ret;
}

//设置datagrid某一td的innerText
function setDGTdContent(mainId,index,val){
  var chk = document.frmShow.elements('chk');
  if(typeof(chk) == 'object'){
      //单条记录
      if(typeof(chk.length) == 'undefined'){
          if(mainId == chk.value)
            chk.parentNode.parentNode.cells[index].innerText = val;
      }else{
          for(var i=0;i < chk.length;i++){
            if(mainId == chk[i].value)
              chk(i).parentNode.parentNode.cells[index].innerText = val;
          }
      }
  }
}
//index:datagrid列的索引值,based-0,返回mainid,index对应列1值，index对应列2值;mainid,index对应列1值，index对应列2值;...
  function getSelectedCols(index)
  {
    var indexAry = index.split(',');
    var ival = '';
       var values = "";
       var chk = document.frmShow.elements('chk');
       if (document.frmShow.elements('chk') != null){
         if (typeof(chk) == 'object'){
         //单条记录
           if (typeof(chk.length) == 'undefined'){
             if (chk.checked){
              values = chk.value;
              for(var j=0;j<indexAry.length;j++){
                values +=','+ chk.parentNode.parentNode.cells[indexAry[j]].innerText;
              }
             }
           }else{
             for(var i=0;i < chk.length;i++)
             {
               if (chk(i).checked){

                for(var j=0;j<indexAry.length;j++){
                  ival +=','+ chk(i).parentNode.parentNode.cells[indexAry[j]].innerText;
                }

                 if (values != '')
                  values = values + ";" + document.frmShow.chk(i).value + ival;
                 else
                  values = document.frmShow.chk(i).value + ival;
               }else{
                continue;
               }
               ival = '';
             }
           }
         }
         return values;
      }else{
        alert("没有数据!");
        return false;
      }
  }
  //
  //index:datagrid列的索引值,based-0,返回mainid,index对应列1值，index对应列2值;mainid,index对应列1值，index对应列2值;...
  function getSelectedCols(index,myform)
  {
    var indexAry = index.split(',');
    var ival = '';
       var values = "";
       var chk = myform.elements('chk');
       if (myform.elements('chk') != null){
         if (typeof(chk) == 'object'){
         //单条记录
           if (typeof(chk.length) == 'undefined'){
             if (chk.checked){
              values = chk.value;
              for(var j=0;j<indexAry.length;j++){
                values +=','+ chk.parentNode.parentNode.cells[indexAry[j]].innerText;
              }
             }
           }else{
             for(var i=0;i < chk.length;i++)
             {
               if (chk(i).checked){

                for(var j=0;j<indexAry.length;j++){
                  ival +=','+ chk(i).parentNode.parentNode.cells[indexAry[j]].innerText;
                }

                 if (values != '')
                  values = values + ";" + myform.chk(i).value + ival;
                 else
                  values = myform.chk(i).value + ival;
               }else{
                continue;
               }
               ival = '';
             }
           }
         }
         return values;
      }else{
        alert("没有数据!");
        return false;
      }
  }
  
  
  
  
  
  
  //获取有隐藏列的选中值
  function getSelectedColsWithHidden(index,hiddenCols){
    var indexAry = index.split(',');
    var hiddenColAry = hiddenCols.split(',');
    var ival = '';
       var values = "";
       var chk = document.frmShow.elements('chk');
       if (document.frmShow.elements('chk') != null){
         if (typeof(chk) == 'object'){
           //单条记录
           if (typeof(chk.length) == 'undefined'){
             if (chk.checked){
              values = chk.value;
              for(var j=0;j<indexAry.length;j++){
                values +=','+ chk.parentNode.parentNode.cells[indexAry[j]].innerText;
              }
              //隐藏值
              for(var n=0;n<hiddenColAry.length;n++){
                values +=','+ document.getElementById(hiddenColAry[n]+chk.value).value;
              }
             }
           }else{
             for(var i=0;i < chk.length;i++)
             {
               if (chk(i).checked){
                for(var j=0;j<indexAry.length;j++){
                  ival +=','+ chk(i).parentNode.parentNode.cells[indexAry[j]].innerText;
                }
                //隐藏值
                for(var n=0;n<hiddenColAry.length;n++){
                  ival +=','+ document.getElementById(hiddenColAry[n]+chk(i).value).value;
                }
                 if (values != '')
                  values = values + ";" + document.frmShow.chk(i).value + ival;
                 else
                  values = document.frmShow.chk(i).value + ival;
               }else{
                continue;
               }
               ival = '';
             }
           }
         }
         return values;
      }else{
        alert("没有数据!");
        return false;
      }
  }
  
  //formObj: form对象
  function getSelectIdsFrom_FORM(formObj)
  {      
       var values = "";
       var chk = formObj.elements('chk');
       if (formObj.elements('chk') != null){
         if (typeof(chk) == 'object'){
           if (typeof(chk.length) == 'undefined'){
             if (chk.checked)
             values = chk.value;
           }else{
             for(var i=0;i < chk.length;i++)
             {
               if (chk(i).checked){
                 if (values != '')
                  values = values + "," + formObj.chk(i).value;
                 else
                  values = formObj.chk(i).value;
               }else{
                continue;
               }
             }
           }
         }
         return values;
      }else{
        alert("没有数据!");
        return false;
      }
  }

  //点击全选checkbox
  function chkAllFrom_FROM(formObj)
  {
    var chk = formObj.elements('chk');
    if (formObj.elements('chk') != null){
      if(typeof(formObj.elements('chk').length)=='undefined')
      {
        if(formObj.chkAll.checked)
        {
          formObj.chk.checked=true;
        }
        else
        {
          formObj.chk.checked=false;
        }
      }
      else
      {
        if(formObj.chkAll.checked)
        {
          for(var i=0;i<document.all('chk').length;i++)
          formObj.chk(i).checked=true;
        }
        else
        {
          for(var i=0;i<document.all('chk').length;i++)
          formObj.chk(i).checked=false;
        }
      }
    }
  }

  //修改数据后把当前行的标志置为true
  function changeCheckBox(i)
  {
  try{
    var chk = document.frmShow.elements('chk');
    if(i == 0) 
        chk.checked =true;
    chk(i).checked = true;
    }catch(e){}
  }

<!--
/*----------------------------------------------------------------------------\
| Table Sort |
|-----------------------------------------------------------------------------|
| Created by Erik Arvidsson |
| (http://webfx.eae.net/contact.html#erik) |
| For WebFX (http://webfx.eae.net/) |
|-----------------------------------------------------------------------------|
| A DOM 1 based script that allows an ordinary HTML table to be sortable. |
|-----------------------------------------------------------------------------|
| Copyright (c) 1998 - 2002 Erik Arvidsson |
|-----------------------------------------------------------------------------|
| 1998-??-?? | First version |
|-----------------------------------------------------------------------------|
| Created 1998-??-?? | All changes are in the log above. | Updated 2001-??-?? |
\----------------------------------------------------------------------------*/

var dom = (document.getElementsByTagName) ? true : false;
var ie5 = (document.getElementsByTagName && document.all) ? true : false;
var arrowUp, arrowDown;

if (ie5 || dom)
initSortTable();

function initSortTable() {
arrowUp = document.createElement("SPAN");
var tn = document.createTextNode("∧");
arrowUp.appendChild(tn);
arrowUp.className = "arrow";

arrowDown = document.createElement("SPAN");
var tn = document.createTextNode("∨");
arrowDown.appendChild(tn);
arrowDown.className = "arrow";
}


//nCol是列在表格中索引顺序下标，从0开始
function sortTable(tableNode, nCol, bDesc, sType) {
var tBody = tableNode.tBodies[0];
var trs = tBody.rows;
var trl= trs.length;
var a = new Array();

for (var i = 0; i < trl; i++) {
a[i] = trs[i];
}

var start = new Date;
//window.status = "Sorting data...";
a.sort(compareByColumn(nCol,bDesc,sType));
//window.status = "Sorting data done";

//tableNode.parentNode.style.overflow = "visible";
for (var i = 0; i < trl; i++) {
tBody.appendChild(a[i]);
//window.status = "Updating row " + (i + 1) + " of " + trl +
//" (Time spent: " + (new Date - start) + "ms)";
}
//tableNode.parentNode.style.overflow = "auto";
//tableNode.parentNode.style.margin = "0";
// check for onsort
if (typeof tableNode.onsort == "string")
tableNode.onsort = new Function("", tableNode.onsort);
if (typeof tableNode.onsort == "function")
tableNode.onsort();
}

function CaseInsensitiveString(s) {
return String(s).toUpperCase();
}

function parseDate(s) {
return Date.parse(s.replace(/\-/g, '/'));
}

/* alternative to number function
* This one is slower but can handle non numerical characters in
* the string allow strings like the follow (as well as a lot more)
* to be used:
* "1,000,000"
* "1 000 000"
* "100cm"
*/

function toNumber(s) {
return Number(s.replace(/[^0-9\.]/g, ""));
}

function compareByColumn(nCol, bDescending, sType) {
var c = nCol;
var d = bDescending;

var fTypeCast = String;

if (sType == "Number")
fTypeCast = Number;
else if (sType == "Date")
fTypeCast = parseDate;
else if (sType == "CaseInsensitiveString")
fTypeCast = CaseInsensitiveString;

return function (n1, n2) {
//自己改的
if(!isNaN(getInnerText(n1.cells[c]))){
  fTypeCast=Number;
}
//alert(getInnerText(n1.cells[c]));

if(n1.cells[c].innerHTML==''||n1.cells[c].innerHTML=='&nbsp;'){
    fTypeCast = String;
}
//结束
if (fTypeCast(getInnerText(n1.cells[c])) < fTypeCast(getInnerText(n2.cells[c])))
return d ? -1 : +1;
if (fTypeCast(getInnerText(n1.cells[c])) > fTypeCast(getInnerText(n2.cells[c])))
return d ? +1 : -1;
return 0;
};
}

function sortColumnWithHold(e) {
// find table element
var el = ie5 ? e.srcElement : e.target;
var table = getParent(el, "TABLE");

// backup old cursor and onclick
var oldCursor = table.style.cursor;
var oldClick = table.onclick;

// change cursor and onclick
table.style.cursor = "wait";
table.onclick = null;

// the event object is destroyed after this thread but we only need
// the srcElement and/or the target
var fakeEvent = {srcElement : e.srcElement, target : e.target};

// call sortColumn in a new thread to allow the ui thread to be updated
// with the cursor/onclick
window.setTimeout(function () {
sortColumn(fakeEvent);
// once done resore cursor and onclick
table.style.cursor = oldCursor;
table.onclick = oldClick;
}, 100);
}

function sortColumn(e) {
var tmp = e.target ? e.target : e.srcElement;
var tHeadParent = getParent(tmp, "THEAD");
var el = getParent(tmp, "TD");

if (tHeadParent == null)
return;

if (el != null) {
var p = el.parentNode;
var i;

// typecast to Boolean
el._descending = !Boolean(el._descending);

if (tHeadParent.arrow != null) {
if (tHeadParent.arrow.parentNode != el) {
tHeadParent.arrow.parentNode._descending = null; //reset sort order
}
tHeadParent.arrow.parentNode.removeChild(tHeadParent.arrow);
}

if (el._descending)
tHeadParent.arrow = arrowUp.cloneNode(true);
else
tHeadParent.arrow = arrowDown.cloneNode(true);

el.appendChild(tHeadParent.arrow);


// get the index of the td
var cells = p.cells;
var l = cells.length;
for (i = 0; i < l; i++) {
if (cells[i] == el) break;
}

var table = getParent(el, "TABLE");
// can't fail

sortTable(table,i,el._descending, el.getAttribute("type"));
}
}
function cTrim(sInputString,iType)
  {
    var sTmpStr = ' '
    var i = -1

    if(iType == 0 || iType == 1)
    {
      while(sTmpStr == ' ')
      {
        ++i
        sTmpStr = sInputString.substr(i,1)
      }
      sInputString = sInputString.substring(i)
    }

    if(iType == 0 || iType == 2)
    {
      sTmpStr = ' '
      i = sInputString.length
      while(sTmpStr == ' ')
      {
        --i
        sTmpStr = sInputString.substr(i,1)
      }
      sInputString = sInputString.substring(0,i+1)
    }
    return sInputString;
  }

function getInnerText(el) {  //alert(el.firstChild.type);

if (ie5){
  if (el!=null){
    //input 类型
    if(typeof(el.firstChild.type) != 'undefined'&&el.firstChild.type!=''){
        return el.firstChild.value;
    }
    return el.innerText; //Not needed but it is faster
  }
  else{
    if(typeof(el.firstChild.type) != 'undefined'){
        return el.firstChild.value;
    }
    return "";
  }
}

var str = "";

var cs = el.childNodes;
var l = cs.length;
for (var i = 0; i < l; i++) {
switch (cs[i].nodeType) {
case 1: //ELEMENT_NODE
str += getInnerText(cs[i]);
break;
case 3: //TEXT_NODE
str += cs[i].nodeValue;
break;
}

}

return str;
}

function getParent(el, pTagName) {
if (el == null) return null;
else if (el.nodeType == 1 && el.tagName.toLowerCase() == pTagName.toLowerCase()) // Gecko bug, supposed to be uppercase
return el;
else
return getParent(el.parentNode, pTagName);
}

//动态改变table滚动的高度
function setsize(h){
  var tmp;
  var th=document.all.tblStat.offsetHeight;
  if(h>th){
    tmp=th+6;    
  }else{
    tmp=h;
  }
  //if(tmp<70) tmp=75;
  tmp=tmp+"px";
  return tmp;
  //return document.all.tblStat.offsetHeight+30;
}
function setsizeb(h){
  var tmp;
  var th=document.all.tblStat.offsetHeight;
  if(h>th){    
    tmp=th+6;
  }else{
    tmp=h;
  }
  //if(tmp<70) tmp=75;
  tmp=tmp+"px";
  return tmp;
  //return document.all.tblStat.offsetHeight+30;
}

function copyFormEleToShowForm(){
   try{
   var mydiv=document.getElementById("divGetOtherForm");
   if(mydiv==null||typeof(mydiv)=='undefined'){
      var frmShow=document.getElementById("frmShow");
      frmShow.innerHTML+="<div id='divGetOtherForm' name='divGetOtherForm' style='display:none'></div>";
      mydiv=document.getElementById("divGetOtherForm");
   }
   
   var frmCommon=document.getElementById("frmCommon");
   if(frmCommon==null||typeof(frmCommon)=='undefined')
   {
      frmCommon=parent.document.getElementById("queryform");
      if(frmCommon==null||typeof(frmCommon)=='undefined'){
      	if(parent.postframe)
        frmCommon=parent.postframe.document.getElementById("queryform");
      }
      if(frmCommon==null||typeof(frmCommon)=='undefined'){
        frmCommon=parent.document.getElementById("frmCommon");
      }
   }
   mydiv.innerHTML=frmCommon.innerHTML;
   }catch(e){
   }
}

function doprtCur(){
try{
    copyFormEleToShowForm();
    document.getElementById("isPrintStatus").value="currentpage";
    document.frmShow.action="";
    document.frmShow.target="hiddenframe_datagrid";
    document.frmShow.method="POST";
    if(typeof(document.frmShow.output)=='object') document.frmShow.output.value="";
    document.frmShow.submit();
    document.getElementById("isPrintStatus").value="";
    }catch(e){alert("printCurrentPage:"+e.message);}
}

function doprtAll(){
try{
    copyFormEleToShowForm();
    document.getElementById("isPrintStatus").value="allpage";
    document.frmShow.action="";
    document.frmShow.target="hiddenframe_datagrid";
    document.frmShow.method="POST";
    if(typeof(document.frmShow.output)=='object') document.frmShow.output.value="";
    document.frmShow.submit();
    document.getElementById("isPrintStatus").value="";
    }catch(e){alert("printAllPage:"+e.message);}
}
