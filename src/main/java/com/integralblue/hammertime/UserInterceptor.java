package com.integralblue.hammertime;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.social.connect.UsersConnectionRepository;
import org.springframework.social.facebook.api.Facebook;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import org.springframework.web.servlet.view.RedirectView;

/**
 * Before a request is handled: 1. sets the current User in the
 * {@link SecurityContext} from a cookie, if present and the user is still
 * connected to Facebook. 2. requires that the user sign-in if he or she hasn't
 * already.
 * 
 * @author Keith Donald
 */
public final class UserInterceptor extends HandlerInterceptorAdapter {
	
	@Inject SecurityContext securityContext;

	private final UsersConnectionRepository connectionRepository;

	private final UserCookieGenerator userCookieGenerator = new UserCookieGenerator();

	public UserInterceptor(UsersConnectionRepository connectionRepository) {
		this.connectionRepository = connectionRepository;
	}

	public boolean preHandle(HttpServletRequest request,
			HttpServletResponse response, Object handler) throws Exception {
		rememberUser(request, response);
		handleSignOut(request, response);
		return true;
	}

	public void afterCompletion(HttpServletRequest request,
			HttpServletResponse response, Object handler, Exception ex)
			throws Exception {
		securityContext.remove();
	}

	// internal helpers

	private void rememberUser(HttpServletRequest request,
			HttpServletResponse response) {
		String userId = userCookieGenerator.readCookieValue(request);
		if (userId == null) {
			return;
		}
		if (!userNotFound(userId)) {
			userCookieGenerator.removeCookie(response);
			return;
		}
		securityContext.setCurrentUserId(userId);
	}

	private void handleSignOut(HttpServletRequest request,
			HttpServletResponse response) {
		if (securityContext.userSignedIn()
				&& request.getServletPath().startsWith("/signout")) {
			connectionRepository.createConnectionRepository(
					securityContext.getCurrentUserId())
					.removeConnections("facebook");
			userCookieGenerator.removeCookie(response);
			securityContext.remove();
		}
	}

	private boolean userNotFound(String userId) {
		// doesn't bother checking a local user database: simply checks if the
		// userId is connected to Facebook
		return connectionRepository.createConnectionRepository(userId)
				.findPrimaryConnection(Facebook.class) != null;
	}

}