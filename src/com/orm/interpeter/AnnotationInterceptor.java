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
//Դ�����+��ӡ�ĵ������д�ϣ�
/**
 * ����ʵ��ע��
 * @author Administrator
 *
 */
public class AnnotationInterceptor<T> extends TableManager{
	protected Class<T> className ;
	private String tableName ;//����
	private String primaryKeyName ;//��������
	
	public AnnotationInterceptor(Class<T> t){
		this.className = t ;
		this.tableName = t.getAnnotation(Table.class).tableName();
		initPrimaryKey();
	}
	/**
	 * ��ȡ����set����
	 * @param
	 * @return
	 */
	public Map<Method, String> getAllSetterMethods(){
		Map<Method, String> methodAndColumnName = new HashMap<Method, String>();
		String methodName = null ;
		//��ȡ����
		for(Method method : className.getDeclaredMethods()){
			methodName = method.getName() ;
			//set����.��ȡ����
			if(methodName.contains("set")){
				methodAndColumnName.put(method , "");
			}
		}
		return methodAndColumnName ;
	}
	/**
	 * ��ʼ������
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
	 * ��ȡ����ֵ
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
	 * ��ȡ�������ƺ�ֵ
	 * @return
	 */
	public Map<String, Object> getFiledsNameAndValue(Object instance){
		String methodName = "";
		Object filedValue ;
		String key = "";	
		//�ֶ���������ֵ
		Map<String , Object> filedMap = new HashMap<String, Object>();
		//��ȡע���ӳ������
		for (Field f : className.getDeclaredFields()) {
			 //��ȡ�ֶ�
	         Column column = f.getAnnotation(Column.class);
	         //��ȡ����
	         PrimaryKey pk = f.getAnnotation(PrimaryKey.class);
	         if (column != null) {
	        	 filedMap.put(column.columnName(), null);
	         }
	         if( pk != null ){
	        	 primaryKeyName = pk.primaryKeyName();
	         }
	    }
		//��ȡ���Ե�ֵ
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
	 * ���ɲ���sql���
	 * @return
	 */
	protected String generateInsertSql(Object instance){
		//����map��װsql���
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
	 * ��ȡ����ֵ
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
	 * ��ȡ�ֶ��б�
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
	 * ������ѯ
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
	 * ������ѯ
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
		//��Ӳ�ѯ����
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