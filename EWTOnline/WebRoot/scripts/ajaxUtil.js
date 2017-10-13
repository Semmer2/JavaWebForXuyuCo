//GET方法
function send_request(url,callback) {
  var http_request =createXMLHttpRequest1();
  http_request.open("GET", url, true);

//define a inline callback function
  function calbackWrapper() {
    if (http_request.readyState == 4) {
      if (http_request.status == 200)
        callback(http_request);
      else
        alert("您所请求的页面有异常。");
    }
  }
  http_request.onreadystatechange = calbackWrapper;
  http_request.send(null);
  return http_request;
}

function send__request(url,callback,isAsyn) {
  if(isAsyn == undefined){ 
    isAsyn = true;
  }
  
  var http_request =createXMLHttpRequest1();
  http_request.open("GET", url, isAsyn);

//define a inline callback function
  function calbackWrapper() {
    if (http_request.readyState == 4) {
      if (http_request.status == 200)
        callback(http_request);
      else
        alert("您所请求的页面有异常。");
    }
  }
  http_request.onreadystatechange = calbackWrapper;
  http_request.send(null);
  return http_request;
  
}

function send_request_syn(url){
  var ret;
  var http_request =createXMLHttpRequest1();
  http_request.open("GET", url, false);
  function calbackWrapper() {
    if (http_request.readyState == 4) {
      if (http_request.status == 200){
        ret = http_request.responseText.replace(/\r|\n/g,'');
      }else{
        alert("您所请求的页面有异常。");
      }  
    }
  }
  http_request.onreadystatechange = calbackWrapper;
  http_request.send(null);
  return ret;
}

function send_post_request(url,callback,data)
{
  var http_request=createXMLHttpRequest1();
  http_request.open("POST", url, true);
//define a inline callback function
  function calbackWrapper() {
    if (http_request.readyState == 4) {
      if (http_request.status == 200)
        if(callback)
          callback(http_request);
      else
        alert("您所请求的页面有异常。");
    }
  }
  http_request.onreadystatechange = calbackWrapper;
  http_request.setRequestHeader("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
  http_request.send(data);
  return http_request;
}
function createXMLHttpRequest1()
{
  var http_request = null;
  if(window.XMLHttpRequest)
  {//Mozilla 浏览器
    http_request = new XMLHttpRequest();
    if (http_request.overrideMimeType)
    {//设置MiME类别
      http_request.overrideMimeType('text/xml');
    }
  }
  else if (window.ActiveXObject){// IE浏览器
    try {
            http_request = new ActiveXObject("Msxml2.XMLHTTP");
      	}
        catch(e) {
            try {
                http_request = new ActiveXObject("Microsoft.XMLHTTP");
                http_request.setRequestHeader("Content-Type","text/html;charset=utf-8"); 
            }
            catch(e) {
                http_request = false;
            }
        }
  }
 //alert("http_request obj-->"+http_request);
  if (!http_request) { // 异常，创建对象实例失败
    window.alert("不能创建XMLHttpRequest对象实例.");
    return null;
  }
  return http_request;
}

// bfctl:前置控件id,cbfn:回调函数!
function ajax_sql_arr(sql,cols,cbfn,bfctl,vctls){
    document.getElementById('linkhidden').value = vctls;
    var bfctlobj = document.getElementById(bfctl);
    if(bfctlobj.value){
        sql=encode64(strUnicode2Ansi(sql));
        cols=encode64(strUnicode2Ansi(cols));
        var url = "/cps/common/ajax_common_linkage.jsp?sql="+sql+"&cols="+cols;
        send_request(url,cbfn);
    }else{
        var vctlsarr = vctls.split(',');
        for(var s=0;s<vctlsarr.length;s++){
            document.getElementById(vctlsarr[s]).value = "";
        }
    }
}

function str2arr(v){
    var vals = v.split('#$#'); //由于返回的是多个参数,这里以用数组的形式接收多个参数
    var myArray = new Array(vals.length);
    for(var k=0;k<vals.length;k++){
        var invals = vals[k].split('$#$');
        myArray[k] = invals;
    }
    return myArray;
}


//ctls:为控件 该方法只针对 rownum=1的情况，将结果自动赋值给ids控件。 ctls split by ','
function str2arr_autolink(v,ctls){
    var myarr = str2arr(v)
    var strarr = myarr[0];
    var ctlsarr = ctls.split(',');
    for(var e=0;e<strarr.length;e++){
        document.getElementById(ctlsarr[e]).value = strarr[e];
    }
}

function commonCBF(request){
    var val=request.responseText.replace(/\r|\n/g,'');//替换空格和换行
    var lhobj = document.getElementById('linkhidden');
    if(lhobj.value){
        str2arr_autolink(val,lhobj.value);
    }else{
        alert("edit页面没有添加隐藏域linkhidden");
    }
    lhobj.value="";
}