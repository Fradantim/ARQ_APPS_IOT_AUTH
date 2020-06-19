package com.aag5.iotenv.ThermometerSensor.Endpoint;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class ThermometerView {
	
	private static Logger logger = LoggerFactory.getLogger(ThermometerView.class);
	
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String redirect() {
		logger.info("A request for getting the auth view.");
		return "redirect:/index.html";
	}
	
}
