package com.cooler.ai.dm;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ImportResource;

import java.util.Collections;

@SpringBootApplication
//@ImportResource(locations = "classpath:applicationContext-burouter2.xml")
public class BuRouter2Application_Burouter2 {

	public static void main(String[] args) {
//		SpringApplication.run(BuRouter2Application_Burouter2.class, args);

		SpringApplication springApplication = new SpringApplication();
		springApplication.setSources(Collections.singleton("classpath:applicationContext-burouter2.xml"));
		springApplication.run(args);
	}
}
