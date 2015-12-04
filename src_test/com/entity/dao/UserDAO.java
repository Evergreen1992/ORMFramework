package com.entity.dao;

import com.entity.User;
import com.orm.dao.BasicDAO;

public class UserDAO extends BasicDAO<User>{
	public UserDAO(Class<User> t) {
		super(t);
	}

	public static void main(String[] args){
		UserDAO userDao = new UserDAO(User.class);
		//userDao.query("select * from User u where u.id = '1111' and passwd = 'fdas' ");
	}
}
