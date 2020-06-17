package com.aag5.iotenv.FilterServer.Endpoint;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.aag5.iotenv.ws.AuthResponse;
import com.aag5.iotenv.ws.TemperatureInputRequest;

@RestController
public class TemperatureEndpoint {
	
	private static Logger logger = LoggerFactory.getLogger(TemperatureEndpoint.class);
	
	@Autowired
	private RestTemplate restTemplate;

	private static final String AUTHORIZATION_HEADER = "Authorization";
	private static final String AUTHORIZATION_HEADER_KEY = "Bearer ";
	
	@CrossOrigin(origins = "*")
	@PostMapping("/temp")
	public void post(@RequestBody TemperatureInputRequest request, @RequestHeader(AUTHORIZATION_HEADER) String authorization) {
		logger.info("A request for setting a new temperature came: "+ request);
		HttpHeaders headers = new HttpHeaders();
		
		String token=authorization;
		if(token != null && token.contains(AUTHORIZATION_HEADER_KEY)) {
			token=token.replaceFirst(AUTHORIZATION_HEADER_KEY, "");
		} else {
			logger.error("Bad '"+AUTHORIZATION_HEADER+"' header: "+token);
			return;
		}
		
		headers.setBearerAuth(token);

		HttpEntity<String> entity = new HttpEntity<>("body", headers);

		ResponseEntity<AuthResponse> authResponse = restTemplate.exchange("http://auth-service/auth", HttpMethod.POST, entity, AuthResponse.class);
		
		if(authResponse.getBody().getAuthenticates()) {
			logger.info("Validated! Answering and sending info to HeatingServer in a new thread.");
			Runnable runnable = () -> { 
				HttpHeaders innerheaders = new HttpHeaders();
				HttpEntity<TemperatureInputRequest> requestEntity = new HttpEntity<>(request, headers);
				
				logger.info("Sending new Temperature");
				restTemplate.postForObject("http://heating-service/temp", requestEntity, String.class);
			};
			Thread t = new Thread(runnable);
			t.start();
		}
	}
}
