package com.aag5.iotenv.ThermometerSensor;

import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

import com.aag5.iotenv.model.Device;
import com.aag5.iotenv.util.JwtUtil;

@SpringBootApplication
@EnableEurekaClient
public class ThermometerSensorApplication implements CommandLineRunner {
	
	@Value("${spring.application.name}")
	private String serviceName;
	
	public static void main(String[] args) {
		SpringApplication.run(ThermometerSensorApplication.class, args);
	}

	// Create a Bean to let Spring FrameWork handle it's instances
	// RestTemplate IS thread safe
	@Bean
	// Enables the RestTemplate to ask registered apps to the Eureka server.
	// The "load balancing" is done by the client, so if many clients consume
	// the service they may all end up calling the same server
	// If the Eureka Server dies the clients still holds a cache of endpoints
	@LoadBalanced
	public RestTemplate getRestTemplate() {
		return new RestTemplate();
	}
	
	private static Logger logger = LoggerFactory.getLogger(ThermometerSensorApplication.class);

	public static Device device;
	
	public static PrivateKey privateKey;
	public static String privateKeyB64;
	public static PublicKey publicKey;
	public static String publicKeyB64;
	
	@Override
	public void run(String... args) throws Exception {
		logger.info("Starting ThermometerSensor.");
		
		device= new Device(UUID.randomUUID().toString(), serviceName);
		
		regenerateKeys();
	}
	
	public static void regenerateKeys() throws NoSuchAlgorithmException {
		logger.info("Regenerating keys...");
		
		Map<String, Object> keys=JwtUtil.getRSAKeys();
		
		privateKey = (PrivateKey) keys.get("private");
		publicKey = (PublicKey) keys.get("public");
		privateKeyB64 = (String) keys.get("privateB64");
		publicKeyB64 = (String) keys.get("publicB64");
		
		logger.info("Regeneration DONE!.");
	}
}
