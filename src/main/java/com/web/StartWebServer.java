package com.web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.Date;
import java.util.UUID;

@SpringBootApplication
@EnableSwagger2
public class StartWebServer {
	public static void main(String[] args) {
		System.out.println(UUID.randomUUID().toString());
		SpringApplication.run(StartWebServer.class, args);
	}
}
