package com.integralblue.hammertime;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.social.connect.Connection;
import org.springframework.social.connect.web.SignInAdapter;
import org.springframework.web.context.request.NativeWebRequest;

/**
 * Signs the user in by setting the currentUser property on the
 * {@link SecurityContext}. Remembers the sign-in after the current request
 * completes by storing the user's id in a cookie. This is cookie is read in
 * {@link UserInterceptor#preHandle(HttpServletRequest, HttpServletResponse, Object)}
 * on subsequent requests.
 * 
 * @author Keith Donald
 * @see UserInterceptor
 */
public class SimpleSignInAdapter implements SignInAdapter {
	
	@Inject
	SecurityContext securityContext;

	private final UserCookieGenerator userCookieGenerator = new UserCookieGenerator();

	public String signIn(String userId, Connection<?> connection,
			NativeWebRequest request) {
		securityContext.setCurrentUserId(userId);
		userCookieGenerator.addCookie(userId,
				request.getNativeResponse(HttpServletResponse.class));
		return null;
	}

}