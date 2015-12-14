package com.entity.dao;

import com.entity.User;
import com.orm.dao.BasicDAO;

public class UserDAO extends BasicDAO<User>{
	public UserDAO(Class<User> t) {
		super(t);
	}
}
