package com.aag5.iotenv.AuthServer.Resource;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.aag5.iotenv.model.Device;
import com.aag5.iotenv.util.JwtUtil;
import com.aag5.iotenv.ws.AddDeviceRequest;
import com.aag5.iotenv.ws.AuthResponse;
import com.aag5.iotenv.ws.CollectionHolder;
import com.aag5.iotenv.ws.Holder;

@RestController
public class AuthenticationEndpoint {
	
	private static final String AUTHORIZATION_HEADER = "Authorization";
	private static final String AUTHORIZATION_HEADER_KEY = "Bearer ";
	
	private static Logger logger = LoggerFactory.getLogger(AuthenticationEndpoint.class);
	
	//public static final List<Device> KNOWN_DEVICES = new CopyOnWriteArrayList<Device>();
	public static final Map<Device,String> KNOWN_DEVICES = new HashMap<>();
	
	@GetMapping("/auth")
	public CollectionHolder<Device> get() {
		return new CollectionHolder<Device>(KNOWN_DEVICES.keySet());
	}

	@CrossOrigin(origins = "*")
	@PostMapping("/add-auth")
	public void post(@RequestBody AddDeviceRequest request) {
		logger.info("A request for adding a device came.");
		logger.info("Request: "+request);
		
		try {
			KNOWN_DEVICES.put(request.getDevice(),request.getPublicKey());
		} catch (Exception e) {
			logger.error("Oops, something went wrong!",e);
			throw e;
		}
		
		logger.info("Device succesfully added!");
	}
	
	@DeleteMapping("/auth")
	public void delete(@RequestParam String id) {
		logger.info("A request for deleting a device came.");
		logger.info("id: "+id);
		Device d = new Device(id);
		KNOWN_DEVICES.remove(d);
		logger.info("Device succesfully removed!");
	}
	
	@PostMapping("/auth")
	public AuthResponse post(@RequestHeader(AUTHORIZATION_HEADER) String authorization) {
		logger.info("A request for cheking a device's jwt authenticity came.");
		Boolean doesAuthenticate=false;
		
		if(authorization.contains(AUTHORIZATION_HEADER_KEY)) {
			try {
				logger.info("'"+AUTHORIZATION_HEADER+"' content: "+authorization);
				String token = authorization.replaceFirst(AUTHORIZATION_HEADER_KEY, "");
				logger.info("Extracting deviceId.");
				String requestedDeviceId = JwtUtil.getDeviceId(token);
				Device requestedDevice= new Device(requestedDeviceId);
				logger.info("Searching for a known public key.");
				String knownKey = KNOWN_DEVICES.get(requestedDevice);
				if(knownKey!=null) {
					logger.info("Validating..");
					try {
						doesAuthenticate = JwtUtil.validateToken(token, knownKey);
						if(doesAuthenticate) 
							logger.info("Auth OK!");
						else 
							logger.info("Auth ERROR!");
					} catch (Exception e) {
						logger.error(e.getMessage(),e);
					}
				} else {
					logger.info("No known public key was found ):");
				}
				
			} catch (Exception e) {
				e.printStackTrace();
			}	
		} else {
			logger.error("No '"+AUTHORIZATION_HEADER_KEY+"' key found :(.");
		}
		
		
		
		return new AuthResponse(doesAuthenticate);
	}	
}