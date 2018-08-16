/**
 * 
 */
$("#query_button").click(function(){
//	console.log("query begin");
	var inputStr = $("#query_input").val();
//	console.log(inputStr);
	$.post("/query",{"inputStr":inputStr},function(data){
		//科室数据添加到表单
		var jsonObj = eval(data);
		var query_parse = jsonObj["query"]; //每个词的权重
//		console.log(jsonObj);
//		console.log(query_parse);
		$("#query_table").children().remove();
		delete jsonObj["query"];
		var sortedKeys=Object.keys(jsonObj).sort(function(a,b){return jsonObj[b]-jsonObj[a]});
		for(item in sortedKeys){
			//每个item代表一个科室，创建一行
			item = sortedKeys[item];
			var newtr = $("<tr></tr>");
			var newtd_1 = $("<td>"+item+"</td>");
			var newtd_2 = $("<td>"+jsonObj[item]+"</td>");
			newtr.append(newtd_1).append(newtd_2);
			$("#query_table").append(newtr);
//			console.log(item,jsonObj[item]);
		}
		
		//输出每个词的权重，高亮重点词汇
		$("#query_parse").children().remove();
		var max_alpha = 0;
		for( item in query_parse){
			var temp = query_parse[item].split("###");
			if(parseFloat(temp[1])>parseFloat(max_alpha)){
				max_alpha = temp[1];
			}
		}
//		console.log(max_alpha);
		for( item in query_parse){
			var temp = query_parse[item].split("###");
			var relative_alpha = parseFloat(temp[1])/parseFloat(max_alpha);
//			console.log(relative_alpha);
			var temp_span = $("<span "+"style='background:rgba(255,255,0,"+relative_alpha+");'"+">"+temp[0]+"</span>");
			$("#query_parse").append(temp_span);
		}
	});
	return false;
	
});