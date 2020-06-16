package com.aag5.iotenv.WindowSensor.Endpoint;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.aag5.iotenv.ws.AuthResponse;
import com.aag5.iotenv.ws.WindowsStatus;

@RestController
public class WindowSensorEndpoint {
	private static Logger logger = LoggerFactory.getLogger(WindowSensorEndpoint.class);

	private static final String AUTHORIZATION_HEADER = "Authorization";
	private static final String AUTHORIZATION_HEADER_KEY = "Bearer ";
	
	private static String status = WindowsStatus.OPEN;
	
	@Autowired
	private RestTemplate restTemplate;

	@GetMapping("/statusDummy")
	public String get() {
		return status;
	}
	
	@GetMapping("/status")
	public String get(@RequestHeader(AUTHORIZATION_HEADER) String authorization) {
		logger.info("A request for getting window status came.");
		
		String token=authorization;
		if(token != null && token.contains(AUTHORIZATION_HEADER_KEY)) {
			token=token.replaceFirst(AUTHORIZATION_HEADER_KEY, "");
		} else {
			logger.error("Bad '"+AUTHORIZATION_HEADER+"' header: "+token);
			throw new IllegalAccessError("Unauthoriced access.");
		}
		
		HttpHeaders headers = new HttpHeaders();
		headers.setBearerAuth(token);
		
		HttpEntity<String> entity = new HttpEntity<>("body", headers);

		ResponseEntity<AuthResponse> authResponse = restTemplate.exchange("http://filter-service/auth", HttpMethod.POST, entity, AuthResponse.class);
		
		if(authResponse.getBody().getAuthenticates()) {
			logger.info("Returning!");
			return status;
		} else {
			throw new IllegalAccessError("Unauthoriced access.");
		}
	}
	
	@PostMapping("/status")
	public void post(@RequestBody String status) {
		logger.info("A request for changing window status came: "+status);
		this.status=status;
		logger.info("Current window status is: "+status);
	}
}
