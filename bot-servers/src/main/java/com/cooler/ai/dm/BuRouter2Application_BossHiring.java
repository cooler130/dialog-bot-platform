package com.cooler.ai.dm;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ImportResource;

import java.util.Collections;

@SpringBootApplication
//@ImportResource(locations = "classpath:applicationContext-boss_hiring.xml")
public class BuRouter2Application_BossHiring {

	public static void main(String[] args) {
//		SpringApplication.run(BuRouter2Application_BossHiring.class, args);

		SpringApplication springApplication = new SpringApplication();
		springApplication.setSources(Collections.singleton("classpath:applicationContext-boss_hiring.xml"));
		springApplication.run(args);
	}
}
