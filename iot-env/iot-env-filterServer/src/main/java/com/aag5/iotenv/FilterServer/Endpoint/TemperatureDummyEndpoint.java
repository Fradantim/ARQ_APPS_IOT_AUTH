package com.aag5.iotenv.FilterServer.Endpoint;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Base64;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.aag5.iotenv.model.Device;
import com.aag5.iotenv.util.JwtUtil;
import com.aag5.iotenv.ws.AddDeviceRequest;
import com.aag5.iotenv.ws.TemperatureInputRequest;

@RestController
public class TemperatureDummyEndpoint {

	private static Logger logger = LoggerFactory.getLogger(TemperatureDummyEndpoint.class);
	
	@Autowired
	private RestTemplate restTemplate;

	private static final String AUTHORIZATION_HEADER = "Authorization";
	private static final String AUTHORIZATION_HEADER_KEY = "Bearer ";

	private static Integer incrementalId=1;
	
	@PostMapping("/tempDummy")
	public void post(@RequestBody TemperatureInputRequest request) {
		logger.info("Inside /tempDummy. Obj recieved: "+request);
		Device newDevice = new Device((incrementalId++).toString(), "The N Device");

		try {
			logger.info("Generating private & public Keys");
			//Add Device in Auth Server
			KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
			keyPairGenerator.initialize(2048);
			KeyPair keyPair = keyPairGenerator.generateKeyPair();
			PrivateKey privateKey = keyPair.getPrivate();
			PublicKey publicKey = keyPair.getPublic();

			String publicKeyB64 = Base64.getEncoder().encodeToString(publicKey.getEncoded());
			
			AddDeviceRequest addDeviceRequest = new AddDeviceRequest(newDevice, publicKeyB64);
			
			logger.info("Adding new authorizable");
			restTemplate.postForObject("http://auth-service/add-auth", addDeviceRequest, String.class);

			// /temp -> @RequestBody TemperatureInputRequest request, @RequestHeader(AUTHORIZATION_HEADER) String authorization
			//Input a new temperature
			
			HttpHeaders headers = new HttpHeaders();
			headers.setBearerAuth(JwtUtil.generateToken(newDevice, privateKey));
			HttpEntity<TemperatureInputRequest> requestEntity = new HttpEntity<>(request, headers);
			
			logger.info("Sending new Temperature");
			restTemplate.postForObject("http://filter-service/temp", requestEntity, String.class);
			logger.info("END");
						
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
