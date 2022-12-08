package com.cooler.ai.dm;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ImportResource;

@SpringBootApplication
@ImportResource(locations = "classpath:applicationContext.xml")
public class BuRouter2Application {

	public static void main(String[] args) {
		SpringApplication.run(BuRouter2Application.class, args);
	}
}
