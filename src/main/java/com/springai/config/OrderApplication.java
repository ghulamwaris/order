package com.springai.config;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
@ComponentScan(basePackages = "com.springai.*")
public class OrderApplication {

	public static void main(String[] args) {
		System.out.println("Application is  starting");
		SpringApplication.run(OrderApplication.class, args);
		System.out.println("Application is  started");
	}

	@GetMapping("/")
	public String sayHelllo(){
		return "index.jsp";
	}
}
