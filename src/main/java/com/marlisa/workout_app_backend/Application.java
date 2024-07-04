package com.marlisa.workout_app_backend;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application implements CommandLineRunner {

	private static Dotenv dotenv;

	public Application(Dotenv dotenv) {
		this.dotenv = dotenv;
	}

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		System.out.println("DB_USERNAME: " + dotenv.get("DB_USERNAME"));
		System.out.println("DB_PASSWORD: " + dotenv.get("DB_PASSWORD"));
	}
}
