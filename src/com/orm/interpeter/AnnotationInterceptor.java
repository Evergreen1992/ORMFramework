package com.orm.interpeter;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.entity.User;
import com.orm.annotation.Column;
import com.orm.annotation.PrimaryKey;
import com.orm.annotation.Table;
import com.orm.system.TableManager;
//源码光盘+打印文档（组号写上）
/**
 * 解析实体注解
 * @author Administrator
 *
 */
public class AnnotationInterceptor<T> extends TableManager{
	protected Class<T> className ;
	private String tableName ;//表名
	private String primaryKeyName ;//主键名称
	
	public AnnotationInterceptor(Class<T> t){
		this.className = t ;
		this.tableName = t.getAnnotation(Table.class).tableName();
		initPrimaryKey();
	}
	/**
	 * 获取属性set方法
	 * @param
	 * @return
	 */
	public Map<Method, String> getAllSetterMethods(){
		Map<Method, String> methodAndColumnName = new HashMap<Method, String>();
		String methodName = null ;
		//获取方法
		for(Method method : className.getDeclaredMethods()){
			methodName = method.getName() ;
			//set方法.获取列名
			if(methodName.contains("set")){
				methodAndColumnName.put(method , "");
			}
		}
		return methodAndColumnName ;
	}
	/**
	 * 初始化主键
	 */
	public void initPrimaryKey(){
		for(Field field : className.getDeclaredFields()){
			PrimaryKey pk = field.getAnnotation(PrimaryKey.class);
			if( pk != null ){
				this.primaryKeyName = pk.primaryKeyName();
				break ;
			}
		}
	}
	/**
	 * 获取属性值
	 * @return
	 */
	public String getValuesByType(Object obj){
		String result = "";
		if( obj instanceof Integer)
			result = (Integer)obj + "";
		else if( obj instanceof String)
			result = "'" + (String)obj + "'";
		return result ;
	}
	/**
	 * 获取属性名称和值
	 * @return
	 */
	public Map<String, Object> getFiledsNameAndValue(Object instance){
		String methodName = "";
		Object filedValue ;
		String key = "";	
		//字段名、属性值
		Map<String , Object> filedMap = new HashMap<String, Object>();
		//获取注解的映射属性
		for (Field f : className.getDeclaredFields()) {
			 //获取字段
	         Column column = f.getAnnotation(Column.class);
	         //获取主键
	         PrimaryKey pk = f.getAnnotation(PrimaryKey.class);
	         if (column != null) {
	        	 filedMap.put(column.columnName(), null);
	         }
	         if( pk != null ){
	        	 primaryKeyName = pk.primaryKeyName();
	         }
	    }
		//获取属性的值
		for(Method method : className.getDeclaredMethods()){
			methodName = method.getName() ;
			if( methodName.contains("get")){
				key = methodName.substring(3, methodName.length()).toLowerCase() ;
				try {
					filedValue = method.invoke(instance);
				} catch (Exception e) {
					filedValue = null ;
				}
				if( filedMap.containsKey(key))
					filedMap.put(key, filedValue);
			}
		}
		return filedMap ;
	}
	/**
	 * 生成插入sql语句
	 * @return
	 */
	protected String generateInsertSql(Object instance){
		//遍历map组装sql语句
		StringBuffer filedPart = new StringBuffer("insert into " + tableName + "(");
		StringBuffer valuesPart = new StringBuffer("  values(");
		Map<String, Object> filedMap = getFiledsNameAndValue(instance) ;
		Object filedValue ;
		
		for(String key : filedMap.keySet()){
			filedValue = filedMap.get(key) ;
			filedPart.append(key + ",");
			valuesPart.append(getValuesByType(filedValue) + ",");
		}
		if( filedPart.indexOf(",") != -1)
			filedPart.deleteCharAt(filedPart.lastIndexOf(","));
		if( valuesPart.indexOf(",") != -1)
			valuesPart.deleteCharAt(valuesPart.lastIndexOf(","));
		filedPart.append(")");
		valuesPart.append(")");
		return filedPart.toString() + valuesPart.toString();
	}
	/**
	 * 
	 * @param instance
	 * @return
	 */
	protected String generateDeleteSql(Object instance){
		Object primaryKeyValue = getPrimaryKeyValue(instance) ;
		String deleteSql = "delete from " + this.tableName + " where " + this.primaryKeyName + " = " 
		+ (primaryKeyValue instanceof Integer == true ? primaryKeyValue.toString() : "'" + primaryKeyValue.toString() + "'")  ;
		return deleteSql ;
	}
	/**
	 * 
	 * @param instance
	 * @return
	 */
	protected String generateUpdateSql(Object instance){
		Map<String, Object> valueMap = getFiledsNameAndValue(instance);
		StringBuffer sb = new StringBuffer();
		for(String key : valueMap.keySet()){
			if( !key.equals(this.primaryKeyName)){
				sb.append(key + " = ");
				sb.append(this.getValuesByType(valueMap.get(key)) + " , ");
			}
		}
		if( sb.lastIndexOf(",") != -1)
			sb.deleteCharAt(sb.lastIndexOf(","));
		
		Object primaryKeyValue = getPrimaryKeyValue(instance) ;
		String deleteSql = "update " + this.tableName + " set "
		+ sb.toString()
		+ " where " + this.primaryKeyName + " = " 
		+ (primaryKeyValue instanceof Integer == true ? primaryKeyValue.toString() : "'" + primaryKeyValue.toString() + "'")  ;
		return deleteSql ;
	}
	/**
	 * 获取主键值
	 * @param instance
	 * @return
	 */
	public Object getPrimaryKeyValue(Object instance){
		Object primaryKeyValue = null ;
		for(Method mt : this.className.getDeclaredMethods()){
			if( mt.getName().contains("get") && mt.getName().toLowerCase().contains(this.primaryKeyName)){
				try {
					primaryKeyValue = mt.invoke(instance);
				} catch (Exception e) {
				}
				break ;
			}
		}
		return primaryKeyValue ;
	}
	/**
	 * 获取字段列表
	 * @return
	 */
	public List<String> getFieldList(){
		List<String> filedList = new ArrayList<String>();
		for(Field filed : this.className.getDeclaredFields()){
			Column column = filed.getAnnotation(Column.class);
			if( column != null )
				filedList.add(column.columnName());
		}
		return filedList ;
	}
	/**
	 * 主键查询
	 * @param instance
	 * @return
	 */
	public String generateQuerySqlByPrimaryKey(Object instance){
		Object primaryKey = getPrimaryKeyValue(instance);
		StringBuffer sb = new StringBuffer("select ");
		for(String filed : getFieldList()){
			sb.append(filed + ",");
		}
		if( sb.lastIndexOf(",") != -1)
			sb.deleteCharAt(sb.lastIndexOf(","));
		sb.append(" from " + this.tableName + " where " + this.primaryKeyName + " = " + this.getValuesByType(primaryKey));
		return sb.toString() ;
	}
	/**
	 * 条件查询
	 * @param parameter
	 * @return
	 */
	public String generateQuerySqlByParameters(Map<String, Object> parameter){
		StringBuffer sb = new StringBuffer("select ");
		int index = 0 , parameterSize = parameter.keySet().size();
		for(String filed : getFieldList()){
			sb.append(filed + ",");
		}
		if( sb.lastIndexOf(",") != -1)
			sb.deleteCharAt(sb.lastIndexOf(","));
		sb.append(" from " + this.tableName + " where " );
		//添加查询条件
		for(Object key : parameter.keySet()){
			index ++ ;
			sb.append(key.toString() + " = " + getValuesByType(parameter.get(key)));
			if( index < parameterSize)
				sb.append(" and ");
		}
		if( sb.lastIndexOf("and") != -1){
		}
		return sb.toString() ;
	}
	public static void main(String[] args) throws Exception{
		User user = new User();
		user.setId("1111");
		user.setPasswd("helloworld");
		
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("id", "0999");
		parameters.put("passwd", "fdasfdas");
		
		AnnotationInterceptor<User> ac = new AnnotationInterceptor<User>(User.class);
		System.out.println(ac.generateQuerySqlByParameters(parameters));
	}
}