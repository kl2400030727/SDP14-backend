package com.placement.system.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "placement_records")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlacementRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "application_id")
    private Application application;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id")
    private User student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_posting_id")
    private JobPosting jobPosting;

    // Denormalized for fast reporting
    private String companyName;
    private String jobTitle;
    private Double ctcOffered;
    private LocalDate joiningDate;
    private String offerLetterUrl;
    private String academicYear;

    @Enumerated(EnumType.STRING)
    private PlacementType placementType;

    @CreationTimestamp
    private LocalDateTime recordedAt;

    public enum PlacementType {
        CAMPUS, POOL_CAMPUS, OFF_CAMPUS, INTERNSHIP
    }
}