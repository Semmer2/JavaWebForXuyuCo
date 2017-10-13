//GET����
function send_request(url,callback) {
  var http_request =createXMLHttpRequest1();
  http_request.open("GET", url, true);

//define a inline callback function
  function calbackWrapper() {
    if (http_request.readyState == 4) {
      if (http_request.status == 200)
        callback(http_request);
      else
        alert("���������ҳ�����쳣��");
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
        alert("���������ҳ�����쳣��");
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
        alert("���������ҳ�����쳣��");
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
        alert("���������ҳ�����쳣��");
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
  {//Mozilla �����
    http_request = new XMLHttpRequest();
    if (http_request.overrideMimeType)
    {//����MiME���
      http_request.overrideMimeType('text/xml');
    }
  }
  else if (window.ActiveXObject){// IE�����
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
  if (!http_request) { // �쳣����������ʵ��ʧ��
    window.alert("���ܴ���XMLHttpRequest����ʵ��.");
    return null;
  }
  return http_request;
}

// bfctl:ǰ�ÿؼ�id,cbfn:�ص�����!
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
    var vals = v.split('#$#'); //���ڷ��ص��Ƕ������,���������������ʽ���ն������
    var myArray = new Array(vals.length);
    for(var k=0;k<vals.length;k++){
        var invals = vals[k].split('$#$');
        myArray[k] = invals;
    }
    return myArray;
}


//ctls:Ϊ�ؼ� �÷���ֻ��� rownum=1�������������Զ���ֵ��ids�ؼ��� ctls split by ','
function str2arr_autolink(v,ctls){
    var myarr = str2arr(v)
    var strarr = myarr[0];
    var ctlsarr = ctls.split(',');
    for(var e=0;e<strarr.length;e++){
        document.getElementById(ctlsarr[e]).value = strarr[e];
    }
}

function commonCBF(request){
    var val=request.responseText.replace(/\r|\n/g,'');//�滻�ո�ͻ���
    var lhobj = document.getElementById('linkhidden');
    if(lhobj.value){
        str2arr_autolink(val,lhobj.value);
    }else{
        alert("editҳ��û�����������linkhidden");
    }
    lhobj.value="";
}