function getweekofday(datevalue)
{
    	var day = new Date(Date.parse(datevalue.replace(/-/g, '/'))); //将日期值格式化 
		var today = new Array("星期天","星期一","星期二","星期三","星期四","星期五","星期六"); 
		return today[day.getDay()];		
}
