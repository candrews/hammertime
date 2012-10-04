package com.integralblue.hammertime.web.config;

import javax.inject.Inject;
import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.encrypt.Encryptors;
import org.springframework.social.connect.ConnectionFactory;
import org.springframework.social.connect.ConnectionFactoryLocator;
import org.springframework.social.connect.ConnectionRepository;
import org.springframework.social.connect.ConnectionSignUp;
import org.springframework.social.connect.NotConnectedException;
import org.springframework.social.connect.UsersConnectionRepository;
import org.springframework.social.connect.jdbc.JdbcUsersConnectionRepository;
import org.springframework.social.connect.support.ConnectionFactoryRegistry;
import org.springframework.social.connect.web.ProviderSignInController;
import org.springframework.social.connect.web.SignInAdapter;
import org.springframework.social.facebook.api.Facebook;
import org.springframework.social.facebook.connect.FacebookConnectionFactory;

import com.integralblue.hammertime.SecurityContext;
import com.integralblue.hammertime.SimpleConnectionSignUp;
import com.integralblue.hammertime.SimpleSignInAdapter;
import com.integralblue.hammertime.model.User;

@Configuration
public class SocialConfig {

	@Inject
	private DataSource dataSource;

	/**
	 * When a new provider is added to the app, register its
	 * {@link ConnectionFactory} here.
	 * 
	 * @see FacebookConnectionFactory
	 */
	@Bean
	public ConnectionFactoryLocator connectionFactoryLocator() {
		ConnectionFactoryRegistry registry = new ConnectionFactoryRegistry();
		// TODO these Facebook parameters (obviously) should not be hardcoded)
		registry.addConnectionFactory(new FacebookConnectionFactory("110725189085402", "f4c1a17ab61d5f8abbc78d7af4b683aa"));
		return registry;
	}

	/**
	 * Singleton data access object providing access to connections across all
	 * users.
	 */
	@Bean
	public UsersConnectionRepository usersConnectionRepository() {
		JdbcUsersConnectionRepository repository = new JdbcUsersConnectionRepository(
				dataSource, connectionFactoryLocator(), Encryptors.noOpText());
		repository.setConnectionSignUp(connectionSignUp());
		return repository;
	}
	
	@Bean
	public ConnectionSignUp connectionSignUp(){
		return new SimpleConnectionSignUp();
	}

	/**
	 * Request-scoped data access object providing access to the current user's
	 * connections.
	 */
	@Bean
	@Scope(value = "request", proxyMode = ScopedProxyMode.INTERFACES)
	public ConnectionRepository connectionRepository() {
		String id = SecurityContext.getCurrentUser();
		return usersConnectionRepository().createConnectionRepository(
				id);
	}

	/**
	 * A proxy to a request-scoped object representing the current user's
	 * primary Facebook account.
	 * 
	 * @throws NotConnectedException
	 *             if the user is not connected to facebook.
	 */
	@Bean
	@Scope(value = "request", proxyMode = ScopedProxyMode.INTERFACES)
	public Facebook facebook() {
		return connectionRepository().getPrimaryConnection(Facebook.class)
				.getApi();
	}

	/**
	 * The Spring MVC Controller that allows users to sign-in with their
	 * provider accounts.
	 */
	@Bean
	public ProviderSignInController providerSignInController() {
		return new ProviderSignInController(connectionFactoryLocator(),
				usersConnectionRepository(), signInAdapter());
	}
	
	@Bean
	public SignInAdapter signInAdapter(){
		return new SimpleSignInAdapter();
	}

}
