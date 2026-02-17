package com.kseng.newsagent;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main entry point.
 * 
 * 
 * The @SpringBootApplication annotation enables auto-configuration, component scanning,
 * and allows this class to be the source for Spring Boot initialization.
 */
@SpringBootApplication
public class NewsagentApplication {

	/**
	 * Main method that bootstraps the Spring Boot application.
	 * 
	 * @param args command-line arguments passed to the application
	 */
	public static void main(String[] args) {
		SpringApplication.run(NewsagentApplication.class, args);
	}

}
