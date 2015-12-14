package com.orm.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * ���
 * @author Administrator
 *
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ForignKey {
	/**
	 * ���ز��ԡ�Ĭ�ϼ��ء�
	 * @return
	 */
	public String lazy() default "init";
	/**
	 * �����
	 * @return
	 */
	public String forignTableName();
	/**
	 * �����
	 * @return
	 */
	public String forignKeyName();
}
