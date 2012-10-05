package com.integralblue.hammertime.web;

import javax.inject.Inject;

import org.springframework.social.facebook.api.Facebook;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class WelcomeController {
	@Inject Facebook facebook;
	
	@RequestMapping(value="/welcome", method=RequestMethod.GET)
	public void welcome(){
		facebook.isAuthorized();
	}

}
