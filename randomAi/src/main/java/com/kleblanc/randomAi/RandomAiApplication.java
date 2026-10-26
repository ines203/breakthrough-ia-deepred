package com.kleblanc.randomAi;

import com.kleblanc.randomAi.model.message.IAStatusMessage;
import com.kleblanc.randomAi.service.SendApiRequestBackendService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import static org.springframework.http.MediaType.APPLICATION_JSON;

@SpringBootApplication
public class RandomAiApplication {
	public static void main(String[] args) {
		SpringApplication.run(RandomAiApplication.class, args);
	}

	@Bean
	public CommandLineRunner sendApiCallAtStartup(SendApiRequestBackendService apiService) {
		return args -> {
			try {
				apiService.sendIaStatusReady();
			} catch (RestClientException e) {
				System.err.println("Error calling API at startup: " + e.getMessage());
			}
		};
	}
}
