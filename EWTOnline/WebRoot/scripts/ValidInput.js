function $(d){return document.getElementById(d);}
/* ���ָ��FORM������Ӧ������Ԫ��
����Щ�����Զ������Ե�Ԫ�أ��Ƿ�Ϸ����˺������ڱ���onsubmit�¼� */
function verifyAll(myform,tab)
{
	try
	{
		var i;
	    for (i=0;i<myform.elements.length;i++)
	    {    
	        if (myform.elements[i].tag+""=="undefined") continue;/* ���Զ������Ե�Ԫ�ز������ */  
	        if(typeof(tab)!="undefined"&&tab!=null){
	            if (verifyInput2(myform.elements[i])==false)/* У�鵱ǰԪ�� */
	            {
	                var tabid=myform.elements[i].attr1;
	                try{  
	                    if(typeof(tab)!="undefined"&&tab!=null){
	                      tabs.activate(tabid);
	                    }	                    
	                    setcontrolfocus(myform.elements[i]);
	                  }catch(e){alert("��֤����ʱ��"+myform.elements[i].title+e.message);}
	                  return false;
	            }
	
	        }else{
	            if (verifyInput(myform.elements[i])==false)/* У�鵱ǰԪ�� */
	            {
	                try{
	                	setcontrolfocus(myform.elements[i]);
	                    //myform.elements[i].focus();
	                  }catch(e){alert("��֤����ʱ��"+myform.elements[i].title+e.message);}
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
    /*�������鼰��Ӧ���Ա��� */
    var maxsize;
    var chname;
    var nullable;
    var datatype;
    var arr;
    /*����У��ĸ�ʽ*/
    var reg;
    /*���ҳ����������ԣ�����struts��ǩ*/
    if (input.tag!=""){
        arr=input.tag.split(";");
        maxsize=arr[0];
        chname=arr[1];
        nullable=arr[2];
        datatype=arr[3];
    }
    /* ����У�� */
    if ((input.value).length>parseInt(maxsize))
    {
        alert(chname+"������󳤶�"+maxsize);
        setcontrolfocus(input);
        //input.focus();
        error = true;
    }
    else
    /* �ǿ�У�� 20011-3-8 edit by youzf ����������Ϊ-1ʱ��ʾΪ��*/ 
    if (trim(input.value)==''||(input.tagName=="SELECT"&&trim(input.value)=='-1'))
    {
        if (nullable=="true"||nullable=="TRUE")
         {
              alert(chname+"����Ϊ��");
              setcontrolfocus(input);
              error = true;
         }
    }
    else{
        /* ��������У�� */
        switch(datatype)
        {
            /* �绰��������У�� */
            case "Phone":
            reg=/[^0-9-]/g;
            if (reg.test(input.value)){
              alert(chname+"��ʽ��������");
              setcontrolfocus(input);
              error = true;
            }
            break;
            /* �����ʼ�����У�� */
            case "email":
            reg = "^(([0-9a-zA-Z]+)|([0-9a-zA-Z]+[_.0-9a-zA-Z-]*[0-9a-zA-Z]+))@([a-zA-Z0-9-]+[.])+([a-zA-Z]{2}|net|NET|com|COM|gov|GOV|mil|MIL|org|ORG|edu|EDU|int|INT)$"
            var re = new RegExp(reg);
            if (input.value.search(re) == -1){
              alert(chname+"ֵӦ��Ϊ�����ʼ��̶���ʽ");
              setcontrolfocus(input);
              error = true;
            }
            break;
            /* ��������У�� */
            case "int":
            if (isInt(input.value)==false)
            {
                alert(chname+"ֵӦ��ȫΪ����");
                setcontrolfocus(input);
                error = true;
            }
            break;
            case "number":
            if (isNaN(input.value)){
                alert(chname+"����Ϊ������");
                setcontrolfocus(input);
                error = true;
            }
            break;
            case "date":
            if (!isValidDate2(input.value)){
                alert(chname+"����Ϊ������");
                setcontrolfocus(input);
                error = true;
            }
            if(input.value.length!=10){
              alert(chname+"��ʽ����ȷ,Ӧ��Ϊ:YYYY-mm-dd,��:2008-09-03");
              setcontrolfocus(input);
              error = true;
            }
            break;
            /* �����������Ӷ���Զ����������͵�У���ж� */
            /*  case datatype1: ... ; break;        */
            /*  case datatype2: ... ; break;        */
            /*  ....................................*/
            default		: break;
        }
    }    
    /* ����У��Ľ�� */
    if (error)
    {
          //У��δͨ��
          return false;
    }
    else
    {
      //У��ͨ��
        return true;
    }
}
/* ����ַ����Ƿ�Ϊ���� */
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
/*   ���ָ���ı��������Ƿ�Ϸ�
   1���ϸ����Զ����chname�������ж��Ƿ���Ҫ����ͳһ��⣬
      ͬʱ�ܹ�ͨ��alert�����ǲ��Ϸ�����Ķ������ƣ�
   2�������Ҫ����Ƿ�����Ϊ�գ�������input�ĸ�������nullable="yes"
   3������input�ĸ�������datatype��ָ��input�����ͣ�number��Phone��email��int����
      ���԰�����Ҫ���ⶨ�������䣬������ݱ������е�switch(input.datatype)����Ҫ��������ļ��
   4������ʹ��struts��ǩ�����룬��Ҫʹ�ñ�ǩ�Ĺ̶�����alt��˳��д��maxsize,chname,nullable,datatype
      ��ֵ���磺alt="50;�ͻ�����;no;String"�����ڲ���Ҫ����������͵�д��String������д���Ӧ
      �����͡�
*/
function verifyInput(input)
{
    var image;
    var i;
    var error = false;
    /*�������鼰��Ӧ���Ա��� */
    var maxsize;
    var chname;
    var nullable;
    var datatype;
    var arr;
    /*����У��ĸ�ʽ*/
    var reg;
    /*���ҳ����������ԣ�����struts��ǩ*/
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
    /* ����У�� */
    if ((input.value).length>parseInt(maxsize))
    {
        alert(chname+"������󳤶�"+maxsize);
        setcontrolfocus(input);
        error = true;
    }
    else
    /* �ǿ�У�� 20011-3-8 edit by youzf ����������Ϊ-1ʱ��ʾΪ��*/ 
    if (trim(input.value)==''||(input.tagName=="SELECT"&&trim(input.value)=='-1'))
    {	
         if(nullable=="true"||nullable=="TRUE")
         {
              alert(chname+"����Ϊ��");
              setcontrolfocus(input);
              error = true;
         }
    }
    else{
        /* ��������У�� */
        switch(datatype)
        {
            /* �绰��������У�� */
            case "Phone":
            reg=/[^0-9-]/g;
            if (reg.test(input.value)){
              alert(chname+"��ʽ��������");
              setcontrolfocus(input);
              error = true;
            }
            break;
            /* �����ʼ�����У�� */
            case "email":
            reg = "^(([0-9a-zA-Z]+)|([0-9a-zA-Z]+[_.0-9a-zA-Z-]*[0-9a-zA-Z]+))@([a-zA-Z0-9-]+[.])+([a-zA-Z]{2}|net|NET|com|COM|gov|GOV|mil|MIL|org|ORG|edu|EDU|int|INT)$"
            var re = new RegExp(reg);
            if (input.value.search(re) == -1){
              alert(chname+"ֵӦ��Ϊ�����ʼ��̶���ʽ");
              setcontrolfocus(input);
              error = true;
            }
            break;
            /* ��������У�� */
            case "int":
            if (isInt(input.value)==false)
            {
                alert(chname+"ֵӦ��ȫΪ����");
                setcontrolfocus(input);
                error = true;
            }
            break;
            case "number":
            if (isNaN(input.value)){
                alert(chname+"����Ϊ������");
                setcontrolfocus(input);
                error = true;
            }
            break;
            case "date":
            if (!isValidDate2(input.value)){
                alert(chname+"����Ϊ������");
                setcontrolfocus(input);
                error = true;
                return false;
            }
            if(input.value.length!=10&&input.value.length!=8&&input.value.length!=9){
              alert(chname+"��ʽ����ȷ,Ӧ��Ϊ:YYYY-mm-dd(yyyy-m-d),��:2008-09-03(2008-9-3)");
              setcontrolfocus(input);
              error = true;
            }
            break;
            case "time":
            if (!strDateTime(input.value)){
                alert(chname+"����Ϊʱ����");
                setcontrolfocus(input);
                error = true;
            }
            break;
            /* �����������Ӷ���Զ����������͵�У���ж� */
            /*  case datatype1: ... ; break;        */
            /*  case datatype2: ... ; break;        */
            /*  ....................................*/
            default		: break;
        }
    }    
    /* ����У��Ľ�� */
    if (error)
    {
          //У��δͨ��
          return false;
    }
    else
    {
      //У��ͨ��
        return true;
    }
}
function trim(srcString){
    return srcString.replace(/(^\s*)|(\s*$)/g, "");
  }

//****************************************************************
// Description: sInputString Ϊ�����ַ�����iTypeΪ���ͣ��ֱ�Ϊ
// 0 - ȥ��ǰ��ո�; 1 - ȥǰ���ո�; 2 - ȥβ���ո�
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
//��֤���ڸ�ʽ
function isValidDate2(d)
{
    var re = /^(?:(?:1[6-9]|[2-9]\d)?\d{2}[\/\-\.](?:0?[1,3-9]|1[0-2])[\/\-\.](?:29|30))(?: (?:0?\d|1\d|2[0-3])\:(?:0?\d|[1-5]\d)\:(?:0?\d|[1-5]\d)(?: \d{1,3})?)?$|^(?:(?:1[6-9]|[2-9]\d)?\d{2}[\/\-\.](?:0?[1,3,5,7,8]|1[02])[\/\-\.]31)(?: (?:0?\d|1\d|2[0-3])\:(?:0?\d|[1-5]\d)\:(?:0?\d|[1-5]\d)(?: \d{1,3})?)?$|^(?:(?:1[6-9]|[2-9]\d)?(?:0[48]|[2468][048]|[13579][26])[\/\-\.]0?2[\/\-\.]29)(?: (?:0?\d|1\d|2[0-3])\:(?:0?\d|[1-5]\d)\:(?:0?\d|[1-5]\d)(?: \d{1,3})?)?$|^(?:(?:16|[2468][048]|[3579][26])00[\/\-\.]0?2[\/\-\.]29)(?: (?:0?\d|1\d|2[0-3])\:(?:0?\d|[1-5]\d)\:(?:0?\d|[1-5]\d)(?: \d{1,3})?)?$|^(?:(?:1[6-9]|[2-9]\d)?\d{2}[\/\-\.](?:0?[1-9]|1[0-2])[\/\-\.](?:0?[1-9]|1\d|2[0-8]))(?: (?:0?\d|1\d|2[0-3])\:(?:0?\d|[1-5]\d)\:(?:0?\d|[1-5]\d)(?: \d{1,3})?)?$/gi;
    return re.test(d);
}