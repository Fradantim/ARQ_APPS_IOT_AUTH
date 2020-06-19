package com.aag5.iotenv.FilterServer.Endpoint;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.aag5.iotenv.ws.AuthResponse;

@RestController
public class AuthenticationEndpoint {
	
	private static Logger logger = LoggerFactory.getLogger(AuthenticationEndpoint.class);
	
	@Autowired
	private RestTemplate restTemplate;

	private static final String AUTHORIZATION_HEADER = "Authorization";
	private static final String AUTHORIZATION_HEADER_KEY = "Bearer ";
	
	@PostMapping("/auth")
	public AuthResponse post(@RequestHeader(AUTHORIZATION_HEADER) String authorization) {
		logger.info("A request for cheking a device's jwt authenticity came.");
		HttpHeaders headers = new HttpHeaders();
		String token=authorization;
		if(token != null && token.contains(AUTHORIZATION_HEADER_KEY)) {
			token=token.replaceFirst(AUTHORIZATION_HEADER_KEY, "");
		} else {
			logger.error("Bad '"+AUTHORIZATION_HEADER+"' header: "+token);
			return new AuthResponse(false);
		}
		headers.setBearerAuth(token);

		HttpEntity<String> entity = new HttpEntity<>("body", headers);

		logger.info("Asking auth-service if the request is valid.");
		ResponseEntity<AuthResponse> authResponse = restTemplate.exchange("http://auth-service/auth", HttpMethod.POST, entity, AuthResponse.class);
		
		if(authResponse.getBody().getAuthenticates()) {
			logger.info("Auth-service said it is!.");
		} else {
			logger.info("Auth-service said it isn't :( .");
		}
		
		return authResponse.getBody();
	}	
}
