package com.aag5.iotenv.HeatingServer;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

import com.aag5.iotenv.model.Device;
import com.aag5.iotenv.ws.AddDeviceRequest;

@SpringBootApplication
@EnableEurekaClient
public class HeatingServerApplication implements CommandLineRunner {
	
	@Value("${spring.application.name}")
	private String serviceName;
	
	@Value("${public.key}")
	private String publicKeyB64;
	
	@Autowired
	private RestTemplate restTemplate;

	public static void main(String[] args) {
		SpringApplication.run(HeatingServerApplication.class, args);
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
	
	private static Logger logger = LoggerFactory.getLogger(HeatingServerApplication.class);

	public static Device device;
	
	@Override
	public void run(String... args) throws Exception {
		logger.info("Starting HeatingServer.");
		logger.info("Registering itself on the auth-server.");
		
		device= new Device(UUID.randomUUID().toString(), serviceName);
		
		logger.info("First execution, must register Device me");
			
		AddDeviceRequest addDeviceRequest = new AddDeviceRequest(device, publicKeyB64);
		
		boolean registered = false;
				
		while (!registered) {
			try {
				restTemplate.postForObject("http://auth-service/add-auth", addDeviceRequest, String.class);
				registered=true;
			} catch (Exception e) {
				logger.error("Oops error when trying to register "+e.getMessage());
				logger.info("Will try again in 10 seconds.");
				TimeUnit.SECONDS.sleep(10);
			}
			
		}		
		
		logger.info("Registering DONE!.");
			
	}
}
