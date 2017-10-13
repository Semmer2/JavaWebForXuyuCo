function $(d){return document.getElementById(d);}
/* 检测指定FORM表单所有应被检测的元素
（那些具有自定义属性的元素）是否合法，此函数用于表单的onsubmit事件 */
function verifyAll(myform,tab)
{
	try
	{
		var i;
	    for (i=0;i<myform.elements.length;i++)
	    {    
	        if (myform.elements[i].tag+""=="undefined") continue;/* 非自定义属性的元素不予理睬 */  
	        if(typeof(tab)!="undefined"&&tab!=null){
	            if (verifyInput2(myform.elements[i])==false)/* 校验当前元素 */
	            {
	                var tabid=myform.elements[i].attr1;
	                try{  
	                    if(typeof(tab)!="undefined"&&tab!=null){
	                      tabs.activate(tabid);
	                    }	                    
	                    setcontrolfocus(myform.elements[i]);
	                  }catch(e){alert("验证数据时："+myform.elements[i].title+e.message);}
	                  return false;
	            }
	
	        }else{
	            if (verifyInput(myform.elements[i])==false)/* 校验当前元素 */
	            {
	                try{
	                	setcontrolfocus(myform.elements[i]);
	                    //myform.elements[i].focus();
	                  }catch(e){alert("验证数据时："+myform.elements[i].title+e.message);}
	                  return false;
	            }
	        }
	    }
	}
	catch(e)
	{
	}
    return true;
}
function setcontrolfocus(pagecontrol)
{
	if(pagecontrol.tagName=="INPUT")
	{
		if(pagecontrol.type=="hidden" || pagecontrol.type=="HIDDEN")
			return;
		else
			pagecontrol.focus();
	}
}
function verifyInput2(input)
{
    var image;
    var i;
    var error = false;
    /*定义数组及对应属性变量 */
    var maxsize;
    var chname;
    var nullable;
    var datatype;
    var arr;
    /*用于校验的格式*/
    var reg;
    /*拆分页面输入的属性：用于struts标签*/
    if (input.tag!=""){
        arr=input.tag.split(";");
        maxsize=arr[0];
        chname=arr[1];
        nullable=arr[2];
        datatype=arr[3];
    }
    /* 长度校验 */
    if ((input.value).length>parseInt(maxsize))
    {
        alert(chname+"超出最大长度"+maxsize);
        setcontrolfocus(input);
        //input.focus();
        error = true;
    }
    else
    /* 非空校验 20011-3-8 edit by youzf 增加下拉框为-1时表示为空*/ 
    if (trim(input.value)==''||(input.tagName=="SELECT"&&trim(input.value)=='-1'))
    {
        if (nullable=="true"||nullable=="TRUE")
         {
              alert(chname+"不能为空");
              setcontrolfocus(input);
              error = true;
         }
    }
    else{
        /* 数据类型校验 */
        switch(datatype)
        {
            /* 电话号码类型校验 */
            case "Phone":
            reg=/[^0-9-]/g;
            if (reg.test(input.value)){
              alert(chname+"格式输入有误");
              setcontrolfocus(input);
              error = true;
            }
            break;
            /* 电子邮件类型校验 */
            case "email":
            reg = "^(([0-9a-zA-Z]+)|([0-9a-zA-Z]+[_.0-9a-zA-Z-]*[0-9a-zA-Z]+))@([a-zA-Z0-9-]+[.])+([a-zA-Z]{2}|net|NET|com|COM|gov|GOV|mil|MIL|org|ORG|edu|EDU|int|INT)$"
            var re = new RegExp(reg);
            if (input.value.search(re) == -1){
              alert(chname+"值应该为电子邮件固定格式");
              setcontrolfocus(input);
              error = true;
            }
            break;
            /* 整数类型校验 */
            case "int":
            if (isInt(input.value)==false)
            {
                alert(chname+"值应该全为整数");
                setcontrolfocus(input);
                error = true;
            }
            break;
            case "number":
            if (isNaN(input.value)){
                alert(chname+"必须为数字型");
                setcontrolfocus(input);
                error = true;
            }
            break;
            case "date":
            if (!isValidDate2(input.value)){
                alert(chname+"必须为日期型");
                setcontrolfocus(input);
                error = true;
            }
            if(input.value.length!=10){
              alert(chname+"格式不正确,应该为:YYYY-mm-dd,如:2008-09-03");
              setcontrolfocus(input);
              error = true;
            }
            break;
            /* 在这里可以添加多个自定义数据类型的校验判断 */
            /*  case datatype1: ... ; break;        */
            /*  case datatype2: ... ; break;        */
            /*  ....................................*/
            default		: break;
        }
    }    
    /* 返回校验的结果 */
    if (error)
    {
          //校验未通过
          return false;
    }
    else
    {
      //校验通过
        return true;
    }
}
/* 检测字符串是否为整数 */
function isInt(str)
{
    var int_chars = "0123456789";
    var i;
    for (i=0;i<str.length;i++)
    {
        if (int_chars.indexOf(str.charAt(i))==-1) {
            return false;
        }
    }
    return true;
}
/*   检测指定文本框输入是否合法
   1、上附加自定义的chname属性来判断是否需要进行统一监测，
      同时能够通过alert来提是不合法输入的对象名称；
   2、如果需要检查是否允许为空，请设置input的附加属性nullable="yes"
   3、设置input的附加属性datatype来指定input的类型（number、Phone、email、int）、
      可以按照需要随意定义与扩充，而后根据本方法中的switch(input.datatype)按需要来做具体的监测
   4、对于使用struts标签的输入，需要使用标签的固定属性alt，顺序写入maxsize,chname,nullable,datatype
      的值，如：alt="50;客户名称;no;String"，对于不需要检测输入类型的写入String，否则写入对应
      的类型。
*/
function verifyInput(input)
{
    var image;
    var i;
    var error = false;
    /*定义数组及对应属性变量 */
    var maxsize;
    var chname;
    var nullable;
    var datatype;
    var arr;
    /*用于校验的格式*/
    var reg;
    /*拆分页面输入的属性：用于struts标签*/
    if(typeof(input.tag)=="undefined"){
        arr=input.alt.split(",");
        maxsize=arr[2];
        chname=arr[3];
        nullable=arr[0];
        datatype=arr[1];
    }else if (input.tag!=""){    	
		arr=input.tag.split(",");
        maxsize=arr[2];
        chname=arr[3];
        nullable=arr[0];
        datatype=arr[1];
    }
    /* 长度校验 */
    if ((input.value).length>parseInt(maxsize))
    {
        alert(chname+"超出最大长度"+maxsize);
        setcontrolfocus(input);
        error = true;
    }
    else
    /* 非空校验 20011-3-8 edit by youzf 增加下拉框为-1时表示为空*/ 
    if (trim(input.value)==''||(input.tagName=="SELECT"&&trim(input.value)=='-1'))
    {	
         if(nullable=="true"||nullable=="TRUE")
         {
              alert(chname+"不能为空");
              setcontrolfocus(input);
              error = true;
         }
    }
    else{
        /* 数据类型校验 */
        switch(datatype)
        {
            /* 电话号码类型校验 */
            case "Phone":
            reg=/[^0-9-]/g;
            if (reg.test(input.value)){
              alert(chname+"格式输入有误");
              setcontrolfocus(input);
              error = true;
            }
            break;
            /* 电子邮件类型校验 */
            case "email":
            reg = "^(([0-9a-zA-Z]+)|([0-9a-zA-Z]+[_.0-9a-zA-Z-]*[0-9a-zA-Z]+))@([a-zA-Z0-9-]+[.])+([a-zA-Z]{2}|net|NET|com|COM|gov|GOV|mil|MIL|org|ORG|edu|EDU|int|INT)$"
            var re = new RegExp(reg);
            if (input.value.search(re) == -1){
              alert(chname+"值应该为电子邮件固定格式");
              setcontrolfocus(input);
              error = true;
            }
            break;
            /* 整数类型校验 */
            case "int":
            if (isInt(input.value)==false)
            {
                alert(chname+"值应该全为整数");
                setcontrolfocus(input);
                error = true;
            }
            break;
            case "number":
            if (isNaN(input.value)){
                alert(chname+"必须为数字型");
                setcontrolfocus(input);
                error = true;
            }
            break;
            case "date":
            if (!isValidDate2(input.value)){
                alert(chname+"必须为日期型");
                setcontrolfocus(input);
                error = true;
                return false;
            }
            if(input.value.length!=10&&input.value.length!=8&&input.value.length!=9){
              alert(chname+"格式不正确,应该为:YYYY-mm-dd(yyyy-m-d),如:2008-09-03(2008-9-3)");
              setcontrolfocus(input);
              error = true;
            }
            break;
            case "time":
            if (!strDateTime(input.value)){
                alert(chname+"必须为时间型");
                setcontrolfocus(input);
                error = true;
            }
            break;
            /* 在这里可以添加多个自定义数据类型的校验判断 */
            /*  case datatype1: ... ; break;        */
            /*  case datatype2: ... ; break;        */
            /*  ....................................*/
            default		: break;
        }
    }    
    /* 返回校验的结果 */
    if (error)
    {
          //校验未通过
          return false;
    }
    else
    {
      //校验通过
        return true;
    }
}
function trim(srcString){
    return srcString.replace(/(^\s*)|(\s*$)/g, "");
  }

//****************************************************************
// Description: sInputString 为输入字符串，iType为类型，分别为
// 0 - 去除前后空格; 1 - 去前导空格; 2 - 去尾部空格
//****************************************************************
  function cTrim(sInputString,iType)
  {
    var sTmpStr = ' ';
    var i = -1;

    if(iType == 0 || iType == 1)
    {
      while(sTmpStr == ' ')
      {
        ++i;
        sTmpStr = sInputString.substr(i,1);
      }
      sInputString = sInputString.substring(i);
    }

    if(iType == 0 || iType == 2)
    {
      sTmpStr = ' ';
      i = sInputString.length;
      while(sTmpStr == ' ')
      {
        --i;
        sTmpStr = sInputString.substr(i,1);
      }
      sInputString = sInputString.substring(0,i+1);
    }
    return sInputString;
  }
function strDateTime(str) 
{ 
    if(str.length!=19){
        str+=":00";
    }
    var reg = /^(\d{1,4})(-|\/)(\d{1,2})\2(\d{1,2}) (\d{1,2}):(\d{1,2}):(\d{1,2})$/; 
    var r = str.match(reg); 
    if(r==null)return false; 
    var d= new Date(r[1], r[3]-1,r[4],r[5],r[6],r[7]); 
    return (d.getFullYear()==r[1]&&(d.getMonth()+1)==r[3]&&d.getDate()==r[4]&&d.getHours()==r[5]&&d.getMinutes()==r[6]&&d.getSeconds()==r[7]); 
}
//验证日期格式
function isValidDate2(d)
{
    var re = /^(?:(?:1[6-9]|[2-9]\d)?\d{2}[\/\-\.](?:0?[1,3-9]|1[0-2])[\/\-\.](?:29|30))(?: (?:0?\d|1\d|2[0-3])\:(?:0?\d|[1-5]\d)\:(?:0?\d|[1-5]\d)(?: \d{1,3})?)?$|^(?:(?:1[6-9]|[2-9]\d)?\d{2}[\/\-\.](?:0?[1,3,5,7,8]|1[02])[\/\-\.]31)(?: (?:0?\d|1\d|2[0-3])\:(?:0?\d|[1-5]\d)\:(?:0?\d|[1-5]\d)(?: \d{1,3})?)?$|^(?:(?:1[6-9]|[2-9]\d)?(?:0[48]|[2468][048]|[13579][26])[\/\-\.]0?2[\/\-\.]29)(?: (?:0?\d|1\d|2[0-3])\:(?:0?\d|[1-5]\d)\:(?:0?\d|[1-5]\d)(?: \d{1,3})?)?$|^(?:(?:16|[2468][048]|[3579][26])00[\/\-\.]0?2[\/\-\.]29)(?: (?:0?\d|1\d|2[0-3])\:(?:0?\d|[1-5]\d)\:(?:0?\d|[1-5]\d)(?: \d{1,3})?)?$|^(?:(?:1[6-9]|[2-9]\d)?\d{2}[\/\-\.](?:0?[1-9]|1[0-2])[\/\-\.](?:0?[1-9]|1\d|2[0-8]))(?: (?:0?\d|1\d|2[0-3])\:(?:0?\d|[1-5]\d)\:(?:0?\d|[1-5]\d)(?: \d{1,3})?)?$/gi;
    return re.test(d);
}