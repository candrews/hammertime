package com.integralblue.hammertime.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class WelcomeController {
	
	@RequestMapping(value="/welcome", method=RequestMethod.GET)
	public void welcome(){
		
	}

}
