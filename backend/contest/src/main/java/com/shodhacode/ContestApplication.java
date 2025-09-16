package com.shodhacode;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ContestApplication {

    public static void main(String[] args) {
        SpringApplication.run(ContestApplication.class, args);
        System.out.println("\n========================================");
        System.out.println("🚀 Shodh-a-Code Platform Started!");
        System.out.println("========================================");
        System.out.println("📍 API Base URL: http://localhost:8080/api");
        System.out.println("📍 H2 Console: http://localhost:8080/api/h2-console");
        System.out.println("   Username: sa");
        System.out.println("   Password: (leave empty)");
        System.out.println("   JDBC URL: jdbc:h2:mem:contestdb");
        System.out.println("========================================\n");
    }

}