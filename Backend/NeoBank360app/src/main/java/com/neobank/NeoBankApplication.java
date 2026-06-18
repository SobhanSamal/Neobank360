package com.neobank;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class NeoBankApplication {

	public static void main(String[] args) {
		SpringApplication.run(NeoBankApplication.class, args);
	}

}
