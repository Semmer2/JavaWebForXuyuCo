function getweekofday(datevalue)
{
    	var day = new Date(Date.parse(datevalue.replace(/-/g, '/'))); //������ֵ��ʽ�� 
		var today = new Array("������","����һ","���ڶ�","������","������","������","������"); 
		return today[day.getDay()];		
}
