package com.entity;

import com.orm.annotation.Column;
import com.orm.annotation.PrimaryKey;
import com.orm.annotation.Table;

@Table(tableName="t_address")
public class Address {
	@PrimaryKey(generateType = "uuid" , primaryKeyName = "id")
	@Column(columnName="id")
	private String id ;
	@Column(columnName="name")
	private String name ;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
}