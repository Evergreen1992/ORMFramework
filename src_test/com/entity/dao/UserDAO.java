package com.entity.dao;

import com.entity.User;
import com.orm.dao.BasicDAO;

public class UserDAO extends BasicDAO<User>{
	public UserDAO(Class<User> t) {
		super(t);
	}

	public static void main(String[] args){
		UserDAO userDao = new UserDAO(User.class);
		User u = new User();
		u.setId("1");
		u.setPasswd("uestc");
		u.setGender("F");
		userDao.create(u);
	}
}
