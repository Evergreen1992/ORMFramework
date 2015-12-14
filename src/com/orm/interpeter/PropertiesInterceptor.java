package com.orm.interpeter;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;

/**
 * ½âÎöxmlÅäÖÃÎÄ¼ş
 * @author Administrator
 *
 */
public class PropertiesInterceptor {
	public static Properties props = new Properties();
	static{
		try {
			InputStreamReader reader = new InputStreamReader(new FileInputStream(System.getProperty("user.dir") + "\\applicationContext.properties"),"utf-8");
			props.load(reader);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
