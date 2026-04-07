package com.placement.system.dto;

import com.placement.system.entity.StudentProfile;
import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class UserDTO {

    @Data @NoArgsConstructor @AllArgsConstructor @Builder
    public static class UserResponse {
        private Long id;
        private String fullName;
        private String email;
        private String role;
        private String phone;
        private String profilePicture;
        private boolean enabled;
        private boolean emailVerified;
        private LocalDateTime createdAt;
    }

    @Data @NoArgsConstructor @AllArgsConstructor
    public static class UpdateProfileRequest {
        private String fullName;
        private String phone;
        private String profilePicture;
    }

    // ==========================================
    // STUDENT PROFILE DTOs
    // ==========================================
    @Data @NoArgsConstructor @AllArgsConstructor @Builder
    public static class StudentProfileRequest {
        private String rollNumber;
        private String department;
        private String batch;
        private Double cgpa;
        private String skills;
        private String resumeUrl;
        private String linkedinUrl;
        private String githubUrl;
        private Integer backlogCount;
    }

    @Data @NoArgsConstructor @AllArgsConstructor @Builder
    public static class StudentProfileResponse {
        private Long id;
        private Long userId;
        private String fullName;
        private String email;
        private String rollNumber;
        private String department;
        private String batch;
        private Double cgpa;
        private String skills;
        private String resumeUrl;
        private String linkedinUrl;
        private String githubUrl;
        private String placementStatus;
        private String placedCompany;
        private Double ctcOffered;
        private LocalDate placementDate;
        private Integer backlogCount;
        private boolean eligible;
    }

    // ==========================================
    // EMPLOYER PROFILE DTOs
    // ==========================================
    @Data @NoArgsConstructor @AllArgsConstructor @Builder
    public static class EmployerProfileRequest {
        @NotBlank(message = "Company name is required")
        private String companyName;
        private String industry;
        private String website;
        private String companySize;
        private String description;
        private String logoUrl;
        private String address;
        private String city;
        private String country;
    }

    @Data @NoArgsConstructor @AllArgsConstructor @Builder
    public static class EmployerProfileResponse {
        private Long id;
        private Long userId;
        private String fullName;
        private String email;
        private String companyName;
        private String industry;
        private String website;
        private String companySize;
        private String description;
        private String logoUrl;
        private String address;
        private String city;
        private String country;
        private boolean verified;
    }
}
