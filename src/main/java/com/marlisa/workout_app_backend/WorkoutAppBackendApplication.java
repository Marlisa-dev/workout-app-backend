package com.marlisa.workout_app_backend;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class WorkoutAppBackendApplication {

	private Dotenv dotenv;

	public static void main(String[] args) {
		SpringApplication.run(WorkoutAppBackendApplication.class, args);
	}

}
