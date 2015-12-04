package com.entity;

import com.orm.annotation.Column;
import com.orm.annotation.ForignKey;
import com.orm.annotation.PrimaryKey;
import com.orm.annotation.Table;

@Table(tableName = "t_user")
public class User {
	@PrimaryKey(generateType = "uuid" , primaryKeyName = "id")
	@Column(columnName = "id")
	private String id ;
	
	@Column(columnName = "passwd")
	private String passwd ;
	
	@ForignKey(forignTableName = "t_address")
	private Address address ;
	
	public Address getAddress() {
		return address;
	}
	public void setAddress(Address address) {
		this.address = address;
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getPasswd() {
		return passwd;
	}
	public void setPasswd(String passwd) {
		this.passwd = passwd;
	}
}