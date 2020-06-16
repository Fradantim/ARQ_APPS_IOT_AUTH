package com.aag5.iotenv.WindowActuator.Endpoint;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
public class WindowActuatorEndpoint {
	
	private static Logger logger = LoggerFactory.getLogger(WindowActuatorEndpoint.class);

	private static final String AUTHORIZATION_HEADER = "Authorization";
	private static final String AUTHORIZATION_HEADER_KEY = "Bearer ";
		
	@Autowired
	private RestTemplate restTemplate;
	
	@PostMapping("/status")
	public void post(@RequestBody String status, @RequestHeader(AUTHORIZATION_HEADER) String authorization) {
		try {
			logger.info("A request for changing windows status came: "+status);			
			
			String token=authorization;
			if(token != null && token.contains(AUTHORIZATION_HEADER_KEY)) {
				token=token.replaceFirst(AUTHORIZATION_HEADER_KEY, "");
			} else {
				logger.error("Bad '"+AUTHORIZATION_HEADER+"' header: "+token);
				throw new IllegalAccessError("Unauthoriced access.");
			}
			
			HttpHeaders headers = new HttpHeaders();
			HttpEntity<String> entity = new HttpEntity<>(status, headers);

			ResponseEntity<String> authResponse = restTemplate.exchange("http://window-sensor/status", HttpMethod.POST, entity, String.class);
			logger.info("Done!");
		} catch (Exception e) {
			logger.error("Oops! ", e);
		}
	}
}
