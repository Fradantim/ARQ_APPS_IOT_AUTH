package com.aag5.iotenv.WindowSensor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
@EnableEurekaClient
public class WindowSensorApplication {

	public static void main(String[] args) {
		SpringApplication.run(WindowSensorApplication.class, args);
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
}
