package com.infina.hissenet;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class HissenetApplication {

	public static void main(String[] args) {
		SpringApplication.run(HissenetApplication.class, args);
	}

}
