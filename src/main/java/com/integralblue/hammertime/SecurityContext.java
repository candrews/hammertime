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

	public static String getCurrentUser() {
		String id = currentUser.get();
		if (id == null) {
			throw new IllegalStateException("No user is currently signed in");
		}
		return id;
	}

	public static void setCurrentUser(String id) {
		currentUser.set(id);
	}

	public static boolean userSignedIn() {
		return currentUser.get() != null;
	}

	public static void remove() {
		currentUser.remove();
	}

}