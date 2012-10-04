package com.integralblue.hammertime;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionSignUp;

import com.integralblue.hammertime.model.User;

public class SimpleConnectionSignUp implements ConnectionSignUp {

	@PersistenceContext
	EntityManager entityManager;

	@Override
	public String execute(Connection<?> connection) {
		User user = new User();
		user.setId(connection.getKey().getProviderId());
		entityManager.merge(user);
		return user.getId();
	}

}
