package org.shevchenko.taskmanagementspringapp;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class TaskManagementSpringAppApplication {

    public static void main(String[] args) {

        Dotenv dotenv = Dotenv.configure().load();

        System.setProperty("DB_URL", dotenv.get("DB_URL"));
        System.setProperty("DB_USER", dotenv.get("DB_USER"));
        System.setProperty("DB_PASSWORD", dotenv.get("DB_PASSWORD"));

        SpringApplication.run(TaskManagementSpringAppApplication.class, args);
    }

}
