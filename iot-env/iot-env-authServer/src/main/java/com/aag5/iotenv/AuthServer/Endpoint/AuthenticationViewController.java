package com.aag5.iotenv.AuthServer.Endpoint;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class AuthenticationViewController {
	
	private static Logger logger = LoggerFactory.getLogger(AuthenticationViewController.class);

	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String redirect() {
		logger.info("A request for getting the auth view.");
		return "redirect:/index.html";
	}
}