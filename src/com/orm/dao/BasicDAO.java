package com.orm.dao;

import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import com.orm.interpeter.AnnotationInterceptor;

/**
 * 实体的数据库操作需要继承此类
 * @author Administrator
 *
 */
public class BasicDAO<T> extends AnnotationInterceptor<T> {
	public BasicDAO(Class<T> t){
		super(t);
	}
	/**
	 * 创建表
	 * @param clas
	 * @return
	 */
	public boolean newTable(Class<?> clas){
		return this.createTable(clas);
	}
	/**
	 * 删除表
	 * @param clas
	 * @return
	 */
	public boolean dropTable(Class<?> clas){
		return this.deleteTable(clas);
	}
	/**
	 * 创建一条记录
	 * @param entity
	 * @return
	 */
	public boolean create(T entity){
		String sql = generateInsertSql(entity);
		System.out.println(sql);
		return this.executeUpdate(sql) ;
	}
	/**
	 * 删除
	 * @param entity
	 * @return
	 */
	public boolean delete(T entity){
		String sql = generateDeleteSql(entity);
		return this.executeUpdate(sql) ;
	}
	/**
	 * 修改实体
	 * @param entity
	 * @return
	 */
	public boolean update(T entity){
		String sql = this.generateUpdateSql(entity);
		return executeUpdate(sql) ;
	}
	/**
	 * 按主键查找
	 * @param entity
	 * @return
	 */
	public Object queryByPrimaryKey(T entity){
		String sql = this.generateQuerySqlByPrimaryKey(entity);
		return querySingle(sql) ;
	}
	/**
	 * 按条件查找
	 * @param parameters
	 * @return
	 */
	public List<Object> query(Map<String, Object> parameters){
		String sql = this.generateQuerySqlByParameters(parameters);
		return this.executeQuery(sql) ;
	}
	////////////////////////////////////////////////////////////////////
	private boolean executeUpdate(String sql) {
		Connection conn = ConnectionFactory.getConnection();
		boolean flag = true ;
		PreparedStatement pstmt = null ;
		try {
			pstmt = conn.prepareStatement(sql);
			flag = pstmt.executeUpdate() >= 1 ? true : false ;
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			ConnectionFactory.revoke(conn);
			close(pstmt);
		}
		return flag;
	}
	/**
	 * 返回多个对象
	 * @param sql
	 * @return
	 */
	private List<Object> executeQuery(String sql) {
		Connection conn = ConnectionFactory.getConnection();
		PreparedStatement pstmt = null ;
		ResultSet rs = null ;
		Object entity = null ;
		List<Object> resultList = new ArrayList<Object>();
		Map<Method, String> methodAndColumnName = this.getAllSetterMethods() ;
		
		try {
			pstmt = conn.prepareStatement(sql);
			rs = pstmt.executeQuery();
			while( rs.next() ){
				try{
					entity = this.className.newInstance();
					for(Method method : methodAndColumnName.keySet()){
						//赋值
						if( method.getParameterTypes().length >= 1){
							String typeName = method.getParameterTypes()[0].toString();
							if( typeName.contains("java.lang.String")){
								method.invoke(entity, rs.getString(method.getName().toLowerCase().substring(3, method.getName().length())));
							}else if(typeName.contains("java.lang.Integer")){
								method.invoke(entity, rs.getInt(method.getName().toLowerCase().substring(3, method.getName().length())));
							}
						}
					}
					resultList.add(entity);
				}catch(Exception e){}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			ConnectionFactory.revoke(conn);
			close(rs);
			close(pstmt);
		}
		return resultList;
	}

	private Object querySingle(String sql) {
		Connection conn = ConnectionFactory.getConnection();
		PreparedStatement pstmt = null ;
		ResultSet rs = null ;
		Object entity = null ;
		try {
			entity = this.className.newInstance();
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		Map<Method, String> methodAndColumnName = this.getAllSetterMethods() ;
		
		try {
			pstmt = conn.prepareStatement(sql);
			rs = pstmt.executeQuery();
			rs.next();
			try{
				for(Method method : methodAndColumnName.keySet()){
					//赋值
					if( method.getParameterTypes().length >= 1){
						String typeName = method.getParameterTypes()[0].toString();
						if( typeName.contains("java.lang.String")){
							method.invoke(entity, rs.getString(method.getName().toLowerCase().substring(3, method.getName().length())));
						}else if(typeName.contains("java.lang.Integer")){
							method.invoke(entity, rs.getInt(method.getName().toLowerCase().substring(3, method.getName().length())));
						}
					}
				}
			}catch(Exception e){}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			ConnectionFactory.revoke(conn);
			close(rs);
			close(pstmt);
		}
		return entity;
	}
	
	private void close(Object obj){
		try{
			if( obj instanceof ResultSet)
				((ResultSet)obj).close();
			if( obj instanceof PreparedStatement)
				((PreparedStatement)obj).close();
		}catch(Exception e){}
	}
}