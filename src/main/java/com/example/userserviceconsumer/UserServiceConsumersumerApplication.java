package com.example.userserviceconsumer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class UserServiceConsumersumerApplication {

	public static void main(String[] args) {
		SpringApplication.run(UserServiceConsumersumerApplication.class, args);
		System.out.println("**** CONSUMER STARTED !!! ****");
	}
}
