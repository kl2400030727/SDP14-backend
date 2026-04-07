package com.placement.system.dto;

import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class ApplicationDTO {

    @Data @NoArgsConstructor @AllArgsConstructor @Builder
    public static class ApplicationRequest {
        private Long jobPostingId;
        private String coverLetter;
        private String resumeUrl;
    }

    @Data @NoArgsConstructor @AllArgsConstructor @Builder
    public static class ApplicationResponse {
        private Long id;
        private Long studentId;
        private String studentName;
        private String studentEmail;
        private String rollNumber;
        private String department;
        private Double cgpa;
        private Long jobPostingId;
        private String jobTitle;
        private String companyName;
        private String status;
        private String coverLetter;
        private String resumeUrl;
        private String aptitudeScore;
        private String technicalRound;
        private String hrRound;
        private String groupDiscussion;
        private String remarks;
        private String offerLetterUrl;
        private LocalDateTime appliedAt;
        private LocalDateTime updatedAt;
    }

    @Data @NoArgsConstructor @AllArgsConstructor
    public static class UpdateApplicationStatusRequest {
        private String status;
        private String remarks;
        private String aptitudeScore;
        private String technicalRound;
        private String hrRound;
        private String groupDiscussion;
        private String offerLetterUrl;
    }
}

class PlacementDTO {

    @Data @NoArgsConstructor @AllArgsConstructor @Builder
    public static class PlacementRecordResponse {
        private Long id;
        private Long studentId;
        private String studentName;
        private String rollNumber;
        private String department;
        private String companyName;
        private String jobTitle;
        private Double ctcOffered;
        private LocalDate joiningDate;
        private String academicYear;
        private String placementType;
        private LocalDateTime recordedAt;
    }

    @Data @NoArgsConstructor @AllArgsConstructor @Builder
    public static class PlacementStatsResponse {
        private long totalStudents;
        private long placedStudents;
        private long notPlacedStudents;
        private double placementPercentage;
        private double averageCTC;
        private double maxCTC;
        private long totalCompanies;
        private long activeJobs;
    }
}
