package com.kleblanc.LeCHONKai;

import com.kleblanc.LeCHONKai.service.SendApiRequestBackendService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestClientException;

@SpringBootApplication
public class LeChonKiaApplication {

	public static void main(String[] args) {
		SpringApplication.run(LeChonKiaApplication.class, args);
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
