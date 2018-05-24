package com.rosetta.face;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
//@ComponentScan(basePackages="com.rosetta.face")
public class FaceCollectorApplication {

	public static void main(String[] args) {
		SpringApplication.run(FaceCollectorApplication.class, args);
	}
}
