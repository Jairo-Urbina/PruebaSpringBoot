package com.bezkoder.springjwt;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.bezkoder.springjwt.models.User;
import com.bezkoder.springjwt.repository.UserRepository;


@SpringBootApplication
public class SpringBootSecurityJwtApplication {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(SpringBootSecurityJwtApplication.class);
	
	public static void main(String[] args) {
		SpringApplication.run(SpringBootSecurityJwtApplication.class, args);
		LOGGER.info("Entra");
	}

}
