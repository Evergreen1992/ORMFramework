package com.orm.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 外键
 * @author Administrator
 *
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ForignKey {
	/**
	 * 加载策略。默认加载。
	 * @return
	 */
	public String lazy() default "init";
	/**
	 * 外键表
	 * @return
	 */
	public String forignTableName();
	/**
	 * 外键名
	 * @return
	 */
	public String forignKeyName();
}
