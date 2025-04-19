package com.agregator.agregator_backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
// Add component scan for both the main package and the 'backend' package
@ComponentScan(basePackages = {"com.agregator.agregator_backend", "com.agregator.backend"})
// Explicitly scan for JPA entities in the correct package
@EntityScan("com.agregator.backend.model")
// Explicitly enable and scan for JPA repositories in the correct package
@EnableJpaRepositories("com.agregator.backend.repository")
public class AgregatorBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(AgregatorBackendApplication.class, args);
	}

}
