package com.placement.system.config;

import com.placement.system.entity.Role;
import com.placement.system.entity.User;
import com.placement.system.repository.UserRepository;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.*;
import io.swagger.v3.oas.models.security.*;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.*;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@RequiredArgsConstructor
public class AppConfig {

    private static final Logger logger = LoggerFactory.getLogger(AppConfig.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.admin.default-email}")
    private String adminEmail;

    @Value("${app.admin.default-password}")
    private String adminPassword;

    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }

    // ==========================================
    // SWAGGER / OPENAPI CONFIGURATION
    // ==========================================
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("Placement Management System API")
                .description("FSAD-PS14: Placement Interaction System - REST API Documentation\n\n" +
                    "**Roles:**\n" +
                    "- ADMIN: Full system control\n" +
                    "- STUDENT: Apply to jobs, track applications\n" +
                    "- EMPLOYER: Post jobs, review applications\n" +
                    "- PLACEMENT_OFFICER: Track placements, generate reports")
                .version("1.0.0")
                .contact(new Contact()
                    .name("Placement Cell")
                    .email("placement@college.edu")))
            .addSecurityItem(new SecurityRequirement().addList("Bearer Authentication"))
            .components(new io.swagger.v3.oas.models.Components()
                .addSecuritySchemes("Bearer Authentication",
                    new SecurityScheme()
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("bearer")
                        .bearerFormat("JWT")));
    }

    // ==========================================
    // DEFAULT ADMIN SEED
    // ==========================================
    @Bean
    public CommandLineRunner dataInitializer() {
        return args -> {
            if (!userRepository.existsByEmail(adminEmail)) {
                User admin = User.builder()
                        .fullName("System Administrator")
                        .email(adminEmail)
                        .password(passwordEncoder.encode(adminPassword))
                        .role(Role.ROLE_ADMIN)
                        .enabled(true)
                        .emailVerified(true)
                        .build();
                userRepository.save(admin);
                logger.info("Default admin user created: {}", adminEmail);
            }
        };
    }
    
}