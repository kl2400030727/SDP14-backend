package com.placement.system;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class PlacementManagementSystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(PlacementManagementSystemApplication.class, args);
        System.out.println("===========================================");
        System.out.println("  PLACEMENT MANAGEMENT SYSTEM STARTED");
        System.out.println("  API Base URL  : http://localhost:8080/api");
        System.out.println("  Swagger UI    : http://localhost:8080/api/swagger-ui.html");
        System.out.println("===========================================");
    }
}
