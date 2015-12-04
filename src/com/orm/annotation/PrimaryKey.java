package com.orm.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * ����
 * @author Administrator
 *
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface PrimaryKey{
	/**
	 * �������ɲ���
	 * @return
	 */
	public String generateType() default "uuid";
	/**
	 * ������������
	 * @return
	 */
	public String primaryKeyName() default "id";
}