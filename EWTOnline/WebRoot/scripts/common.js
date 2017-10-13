function trim(srcString) {
	return srcString.replace(/(^\s*)|(\s*$)/g, "");
}
 function getfieldjsonstr(fieldname)
    {
    	var s="\""+fieldname+"\":";
    	var controlvalue=eval("frmManage."+fieldname+".value");
		s=s+"\""+encodeURI(trim(controlvalue))+"\"";
		return s;
    }
    function getfieldjsonstrbyvalue(fieldname,value)
    {
    	var s="\""+fieldname+"\":";
    	var controlvalue=value;
		s=s+"\""+encodeURI(trim(controlvalue))+"\"";
		return s;
    }
    function getvalue(value)
    {
    	return decodeURIComponent(value);
    }