package com.integralblue.hammertime;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionSignUp;
import org.springframework.transaction.annotation.Transactional;

import com.integralblue.hammertime.model.User;

public class SimpleConnectionSignUp implements ConnectionSignUp {

	@PersistenceContext
	EntityManager entityManager;

	@Override
	@Transactional
	public String execute(Connection<?> connection) {
		User user = new User();
		user.setId(connection.getKey().getProviderUserId());
		entityManager.merge(user);
		return user.getId();
	}

}
