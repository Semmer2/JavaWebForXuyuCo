<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>


<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
	<head>
		<title>My JSP 'index.jsp' starting page</title>
		<script type="text/javascript" language="javascript"
			src="../scripts/jquery/json2.js">
</script>
		<script type="text/javascript" language="javascript"
			src="../scripts/jquery/jquery-1.7.min.js">
</script>
		<script type="text/javascript">
function CheckAjax() {   
	if ($('#partid').val().length == 0) {
		$('.hint').text("用户名不能位空").css( {
			"background-color" : "green"
		});
	} else {		
		$.ajax( {
			type : "POST",//使用post方法访问后台  
			dataType : "json",//返回json格式的数据  
			url : "../output.jsp",//要访问的后台地址  
			contentType : "application/json;charset=utf-8",
			data : {
				PARTID : $('#partid').val()
			},//要发送的数据  
			beforeSend : function() {
				$("span").html("<font color='red'>ajax数据处理中,请稍后...</font>");
			},

			complete : function() {
				$("span").html("<font color='red'>ajax数据处理完毕</font>");
			},//AJAX请求完成时  
			success : function(data) {//data为返回的数据，在这里做数据绑定  
				//jsonArray数组 用each遍历  
				$.each(data.jsonArray, function(index) {
					$.each(data.jsonArray[index], function(key, value) {
						alert(key + ":" + value)
						$('body').append(
								"<div>" + key + "---" + value + "</div>").css(
								"color", "red");
					});
				});

				//单个字符串输出    
				$('body').append("<div>" + data.account + "</div>").css(
						"color", "red");
			},
			error : function(XMLResponse) {
				alert(XMLResponse.responseText)
			}
		});

	}
}
</script>
	</head>

	<body>
		<table>
			<tr>
				<td>
					<input type="text" id="partid" name="partid" title="partid">
					<input type="button" value="test" onclick="CheckAjax();"/>
				</td>
			</tr>
			<tr>
				<td>
					<div class="hint">
					</div>
				</td>
			</tr>

			<tr>
				<td>
					<span></span>
				</td>
			</tr>
		</table>
	</body>
</html>
