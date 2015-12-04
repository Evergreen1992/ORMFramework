package com.orm.system;

/**
 * 框架的启动类
 * @author Administrator
 *
 */
public class SystemInit {
	private static boolean isStartUp = false ;//是否启动
	
	public static void systemInit(){
		if( isStartUp == false ){
			startUp();
			isStartUp = true ;
		}
	}
	
	//初始化applicationContext.xml(数据库连接信息)
	//实体和数据库表的映射
	//初始化数据库连接池类
	private static boolean startUp(){
		return false ;
	}
}