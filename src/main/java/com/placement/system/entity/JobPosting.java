package com.placement.system.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "job_postings")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JobPosting {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(columnDefinition = "TEXT")
    private String requirements;

    private String location;

    @Enumerated(EnumType.STRING)
    private JobType jobType;

    @Enumerated(EnumType.STRING)
    private JobStatus status;

    private Double minCTC;
    private Double maxCTC;
    private Integer openings;
    private String skills;
    private Double minCGPA;
    private Integer maxBacklogs;
    private String eligibleBranches;
    private LocalDate applicationDeadline;
    private LocalDate driveDate;

    // postedBy is EmployerProfile directly (not User)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employer_profile_id")
    private EmployerProfile postedBy;

    // mappedBy="jobPosting" => Application.jobPosting is of type JobPosting ✓
    @OneToMany(mappedBy = "jobPosting", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Application> applications;

    private boolean approvedByAdmin = false;
    private boolean approvedByOfficer = false;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    public enum JobType {
        FULL_TIME, PART_TIME, INTERNSHIP, CONTRACT
    }

    public enum JobStatus {
        DRAFT, PENDING_APPROVAL, ACTIVE, CLOSED, CANCELLED
    }
}