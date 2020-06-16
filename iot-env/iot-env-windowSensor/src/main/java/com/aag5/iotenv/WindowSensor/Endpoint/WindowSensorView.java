package com.aag5.iotenv.WindowSensor.Endpoint;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class WindowSensorView {
	private static Logger logger = LoggerFactory.getLogger(WindowSensorView.class);

	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String redirect() {
		logger.info("A request for getting the window view.");
		return "redirect:/index.html";
	}

}
