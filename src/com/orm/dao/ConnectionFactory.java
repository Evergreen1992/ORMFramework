package com.orm.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * 数据库连接池
 * @author Administrator
 *
 */
public class ConnectionFactory extends Thread{
	private static int poolSize = 20 ;
	private static final BlockingQueue<Connection> connQueue = new ArrayBlockingQueue<>(poolSize);
	
	static{
		String url = "jdbc:mysql://localhost:3306/orm?user=root&password=&useUnicode=true&characterEncoding=UTF8";
		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e1) {
			e1.printStackTrace();
		}
		for( int i = 0 ; i < poolSize ; i ++){
			try {
				Connection conn = DriverManager.getConnection(url);
				connQueue.add(conn);
			}catch (Exception e) {
				e.printStackTrace();
			}
		}
		System.out.println("*** Connection Queue inited *** ");
	}
	
	/**
	 * 获取
	 * @return
	 */
	public static Connection getConnection(){
		try {
			return connQueue.take() ;
		} catch (InterruptedException e) {
			return null ;
		}
	}
	/**
	 * 回收
	 * @param conn
	 * @return
	 */
	public static boolean revoke(Connection conn){
		boolean flag = false ;
		flag = connQueue.add(conn);
		return flag ;
	}
	
	public static void main(String[] args){
		for(int i = 0 ; i < 25 ; i ++){
			System.out.println(revoke(getConnection()));
		}
	}
}
