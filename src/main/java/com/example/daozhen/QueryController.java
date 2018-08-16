package com.example.daozhen;

import java.io.IOException;
import java.io.InputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;

@RestController
public class QueryController {
	
	static {
		ClassLoader classloader = QueryController.class.getClassLoader();
		InputStream resourceAsString = classloader.getResourceAsStream("log4j.properties");
		PropertyConfigurator.configure(resourceAsString);
	}
	static Logger logger = Logger.getLogger(QueryController.class.getName());

	@RequestMapping("/query")
	public String query(HttpServletRequest request) throws IOException {
		logger.info("进入query处理模块");
		//获取问题字符串
		logger.info("用户问题为:"+request.getParameter("inputStr"));
		String queryString= request.getParameter("inputStr");
		String returnString = "";
		//udp通信
		DatagramSocket datagramsocket = new DatagramSocket();
		InetAddress address = InetAddress.getByName("101.6.64.222");
		if(queryString.length()!=0) {
			//请求发送
			byte[] buffer = queryString.getBytes();
			DatagramPacket packet = new DatagramPacket(buffer, buffer.length, address, 9002);
			datagramsocket.send(packet);
			//请求接受
			DatagramPacket inputPacket = new DatagramPacket(new byte[512], 512);  
            datagramsocket.receive(inputPacket); 
            returnString = new String(inputPacket.getData(), 0 , inputPacket.getLength());
            datagramsocket.close();
		}
		logger.info("收到的回复为:"+returnString);
		//处理取回的数据
		JSONObject jsonObj = JSONObject.parseObject(returnString);
		Map<String, Object> map = (Map<String,Object>)jsonObj;
		for(String key : map.keySet()) {
			if(key.equals("query")) {
				continue;
			}
			List<Object> value = (List<Object>)map.get(key);
			if(value.size() > 1) {
				map.put(key,1);
			}else {
				map.put(key, value.get(0));
			}
		}
		logger.info("处理后:"+map);
		String exampleStr = "("+map+")";
		return exampleStr;
	}
}
