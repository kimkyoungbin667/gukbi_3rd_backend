package com.project.animal;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class AnimalApplication {

	// 테스트
	public static void main(String[] args) {
		SpringApplication.run(AnimalApplication.class, args);
	}

}
