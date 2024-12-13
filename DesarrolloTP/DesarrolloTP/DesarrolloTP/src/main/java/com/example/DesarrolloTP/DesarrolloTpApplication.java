package com.example.DesarrolloTP;

import com.example.DesarrolloTP.controller.Principal;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DesarrolloTpApplication {

	public static void main(String[] args) {
		SpringApplication.run(DesarrolloTpApplication.class, args);
        new Principal();
	}

}
