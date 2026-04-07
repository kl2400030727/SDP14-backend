package com.placement.system.dto;

import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class JobDTO {

    @Data @NoArgsConstructor @AllArgsConstructor @Builder
    public static class JobPostingRequest {
        @NotBlank(message = "Job title is required")
        private String title;

        @NotBlank(message = "Description is required")
        private String description;

        private String requirements;
        private String location;
        private String jobType;
        private Double minCTC;
        private Double maxCTC;

        @Min(value = 1, message = "At least 1 opening required")
        private Integer openings;

        private String skills;
        private Double minCGPA;
        private Integer maxBacklogs;
        private String eligibleBranches;

        @NotNull(message = "Application deadline is required")
        private LocalDate applicationDeadline;

        private LocalDate driveDate;
    }

    @Data @NoArgsConstructor @AllArgsConstructor @Builder
    public static class JobPostingResponse {
        private Long id;
        private String title;
        private String description;
        private String requirements;
        private String location;
        private String jobType;
        private String status;
        private Double minCTC;
        private Double maxCTC;
        private Integer openings;
        private String skills;
        private Double minCGPA;
        private Integer maxBacklogs;
        private String eligibleBranches;
        private LocalDate applicationDeadline;
        private LocalDate driveDate;
        private Long postedById;
        private String companyName;
        private String companyLogo;
        private boolean approvedByAdmin;
        private boolean approvedByOfficer;
        private LocalDateTime createdAt;
        private int applicationCount;
        private boolean alreadyApplied;
    }
}
