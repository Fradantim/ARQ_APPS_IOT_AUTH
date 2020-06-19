package com.aag5.iotenv.ThermometerSensor.Endpoint;

import java.security.NoSuchAlgorithmException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.aag5.iotenv.ThermometerSensor.ThermometerSensorApplication;
import com.aag5.iotenv.model.Device;
import com.aag5.iotenv.util.JwtUtil;
import com.aag5.iotenv.ws.Key;
import com.aag5.iotenv.ws.TemperatureInputRequest;

@RestController
public class ThermometerEndpoint {
	
	@Value("${spring.application.name}")
	private String serviceName;
	
	private static Logger logger = LoggerFactory.getLogger(ThermometerEndpoint.class);

	@Autowired
	private RestTemplate restTemplate;

	@GetMapping("/device")
	public Device getDeviceInfo() {
		return ThermometerSensorApplication.device;
	}
	
	@GetMapping("/keys")
	public Key getKeys() {
		return new Key(ThermometerSensorApplication.publicKeyB64,ThermometerSensorApplication.privateKeyB64);
	}
	
	@GetMapping("/regenerateKeys")
	public Key regenerateKeys() throws NoSuchAlgorithmException {
		ThermometerSensorApplication.regenerateKeys();
		return getKeys();
	}
	
	@PostMapping("/temp")
	public void post(@RequestBody TemperatureInputRequest request) {
		logger.info("A request for setting a new temperature came: "+ request);
		
		HttpHeaders headers = new HttpHeaders();			
		headers.setBearerAuth(JwtUtil.generateToken(ThermometerSensorApplication.device, ThermometerSensorApplication.privateKey));
		HttpEntity<TemperatureInputRequest> entity = new HttpEntity<>(request, headers);
		ResponseEntity<String> windowStatus = restTemplate.exchange("http://filter-service/temp", HttpMethod.POST, entity, String.class);
		logger.info("New temperature sent correctly to filter-service.");
	}
}

