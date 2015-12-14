package com.orm.system;

import java.util.HashMap;
import java.util.Map;

import com.orm.interpeter.PropertiesInterceptor;

/**
 * 框架的启动类
 * @author Administrator
 *
 */
public class SystemInit {
	public static final Map<String, Class<?>> nameAndClassObject = 
			new HashMap<String, Class<?>>();
	
	static{
		//系统配置文件读取.
		String entityPath = PropertiesInterceptor.props.getProperty("entityPackageBase");
		System.out.println(entityPath);
		//获取实体类名、class对象map.
	}
	
	public static void main(String[] ags){
		System.out.println(System.getProperty("user.dir") + "****************");
	}
}