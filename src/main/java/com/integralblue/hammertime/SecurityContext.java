package com.integralblue.hammertime;

import com.integralblue.hammertime.model.User;

/**
 * Simple SecurityContext that stores the currently signed-in connection in a
 * thread local.
 * 
 * @author Keith Donald
 */
public class SecurityContext {

	private static final ThreadLocal<String> currentUser = new ThreadLocal<String>();

	public String getCurrentUserId() {
		String id = currentUser.get();
		if (id == null) {
			throw new NotLoggedInException();
		}
		return id;
	}

	public void setCurrentUserId(String id) {
		currentUser.set(id);
	}

	public boolean userSignedIn() {
		return currentUser.get() != null;
	}

	public void remove() {
		currentUser.remove();
	}

}