package com.orm.system;

import java.util.HashMap;
import java.util.Map;

import com.orm.interpeter.PropertiesInterceptor;

/**
 * ��ܵ�������
 * @author Administrator
 *
 */
public class SystemInit {
	public static final Map<String, Class<?>> nameAndClassObject = 
			new HashMap<String, Class<?>>();
	
	static{
		//ϵͳ�����ļ���ȡ.
		String entityPath = PropertiesInterceptor.props.getProperty("entityPackageBase");
		System.out.println(entityPath);
		//��ȡʵ��������class����map.
	}
	
	public static void main(String[] ags){
		System.out.println(System.getProperty("user.dir") + "****************");
	}
}