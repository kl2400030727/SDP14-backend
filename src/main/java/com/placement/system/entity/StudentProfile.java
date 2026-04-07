package com.placement.system.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "student_profiles")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private String rollNumber;
    private String department;
    private String batch;
    private Double cgpa;
    private String skills;
    private String resumeUrl;
    private String linkedinUrl;
    private String githubUrl;

    @Enumerated(EnumType.STRING)
    private PlacementStatus placementStatus;

    private String placedCompany;
    private Double ctcOffered;
    private LocalDate placementDate;

    private Integer backlogCount;
    private boolean eligible = true;

    // NO @OneToMany applications here.
    // Application.studentProfile is a ManyToOne snapshot reference only.
    // The real bidirectional owner is Application.student -> User.

    public enum PlacementStatus {
        NOT_PLACED, PLACED, INTERNSHIP, HIGHER_STUDIES
    }
}