package com.aag5.iotenv.HeatingServer.Endpoint;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.aag5.iotenv.model.Device;
import com.aag5.iotenv.util.JwtUtil;
import com.aag5.iotenv.ws.AddDeviceRequest;
import com.aag5.iotenv.ws.TemperatureInputRequest;
import com.aag5.iotenv.ws.WindowsStatus;

@RestController
public class HeatingEndpoint {
	
	@Value("${private.key}")
	private String privateKeyB64;
	
	@Value("${spring.application.name}")
	private String serviceName;
	
	@Value("${public.key}")
	private String publicKeyB64;

	private static Logger logger = LoggerFactory.getLogger(HeatingEndpoint.class);
	
	private static Boolean first=true;
	private static Device device=null;
	private static PrivateKey privateKey=null;
	private static final Integer OPENING_DELTA=26;
	private static final Integer CLOSING_DELTA=20;
	
	@Autowired
	private RestTemplate restTemplate;

	@PostMapping("/temp")
	public void post(@RequestBody TemperatureInputRequest request) {
		try {
			logger.info("A request for setting a new temperature came: "+ request);
			if(first) {
				privateKey = JwtUtil.generatePrivateKey(privateKeyB64);
				
				device= new Device(serviceName);
				logger.info("First execution, must register Device me");
				
				AddDeviceRequest addDeviceRequest = new AddDeviceRequest(device, publicKeyB64);
				
				logger.info("Adding new authorizable");
				restTemplate.postForObject("http://auth-service/add-auth", addDeviceRequest, String.class);
				
				first=false;
			}
		
			Integer temp = request.getTemp();
			
			logger.info("Getting window status.");
			HttpHeaders headers = new HttpHeaders();
			
			headers.setBearerAuth(JwtUtil.generateToken(device, privateKey));
			HttpEntity<String> entity = new HttpEntity<>("body", headers);
			ResponseEntity<String> windowStatus = restTemplate.exchange("http://window-sensor/status", HttpMethod.GET, entity, String.class);
			logger.info("Status gotten: "+windowStatus.getBody());
			
			String newStatus= null;
			if(temp <= CLOSING_DELTA) {
				if(WindowsStatus.OPEN.equals(windowStatus.getBody())) {
					newStatus= WindowsStatus.CLOSED;
				}
			} else {
				if(temp >= OPENING_DELTA) {
					if(WindowsStatus.CLOSED.equals(windowStatus.getBody())) {
						newStatus= WindowsStatus.OPEN;
					}
				}
			}
			
			if(newStatus != null) {
				logger.info("Sending new status: "+newStatus);
				headers = new HttpHeaders();
				headers.setBearerAuth(JwtUtil.generateToken(device, privateKey));
				entity = new HttpEntity<>(newStatus, headers);				
				restTemplate.exchange("http://window-actuator/status", HttpMethod.POST, entity, String.class);
				logger.info("DONE!");
			} else {
				logger.info("Nothing to be done.");
			}
			
		} catch (Exception e) {
			logger.error("Oops! ", e);
		}
	}
}
