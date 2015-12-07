package com.orm.system;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import com.orm.annotation.Column;
import com.orm.annotation.PrimaryKey;
import com.orm.annotation.Table;
import com.orm.dao.ConnectionFactory;

/**
 * 创建、更新表
 * @author Administrator
 *
 */
public class TableManager {
	/**
	 * 创建表
	 * @return
	 */
	public boolean createTable(Class<?> clas){
		Table table = (Table)clas.getAnnotation(Table.class);
		StringBuffer sql = new StringBuffer("create table " + table.tableName());
			   sql.append("(");
		for(Field field : clas.getDeclaredFields()){
			Column column = field.getAnnotation(Column.class);
			PrimaryKey pk = field.getAnnotation(PrimaryKey.class);
			
			if( column != null ){
				sql.append(column.columnName() + " ");
				//数据类型	
				if( field.getType().toString().contains("java.lang.String")){
					sql.append("varchar(255)");
				}else if(field.getType().toString().contains("java.lang.Integer")){
					sql.append("int(11)");
				}
				
				//主键
				if( pk != null ){
					sql.append(" primary key , ");
				}else{
					sql.append(",");
				}
			}
			
			
		}
		if( clas.getDeclaredFields().length > 1)
			sql.deleteCharAt(sql.lastIndexOf(","));
		sql.append(")");
			   
		Connection conn = ConnectionFactory.getConnection();
		PreparedStatement pstmt = null ;
		try{
			System.out.println(sql.toString());
			pstmt = conn.prepareStatement(sql.toString());
			pstmt.executeUpdate();
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			if( pstmt != null )
				try {
					pstmt.close();
				} catch (SQLException e) {
				}
			ConnectionFactory.revoke(conn);
		}
		
		return true ;
	}
	/**
	 * 删除表
	 * @return
	 */
	public boolean deleteTable(Class<?> clas){
		Connection conn = ConnectionFactory.getConnection();
		Table table = ((Table)clas.getAnnotation(Table.class));
		String tableName = table.tableName();
		String sql = "drop table " + tableName;
		boolean flag = true ;
		PreparedStatement pstmt = null ;
		try{
			System.out.println(sql);
			pstmt = conn.prepareStatement(sql);
			
			pstmt.executeUpdate() ;
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			if( pstmt != null )
				try {
					pstmt.close();
				} catch (SQLException e) {
				}
			ConnectionFactory.revoke(conn);
		}
		return flag ;
	}
}