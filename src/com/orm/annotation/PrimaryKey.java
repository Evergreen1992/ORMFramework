package com.orm.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 主键
 * @author Administrator
 *
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface PrimaryKey{
	/**
	 * 主键生成策略
	 * @return
	 */
	public String generateType() default "uuid";
	/**
	 * 主键属性名称
	 * @return
	 */
	public String primaryKeyName() default "id";
}