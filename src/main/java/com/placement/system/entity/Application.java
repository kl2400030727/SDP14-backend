package com.placement.system.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "applications")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Application {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Primary student link — User entity (drives the User.applications bidirectional)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private User student;

    // Snapshot of student profile at time of application — plain ManyToOne, no bidirectional
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_profile_id")
    private StudentProfile studentProfile;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_posting_id", nullable = false)
    private JobPosting jobPosting;

    @Enumerated(EnumType.STRING)
    private ApplicationStatus status;

    private String coverLetter;
    private String resumeUrl;

    private String aptitudeScore;
    private String technicalRound;
    private String hrRound;
    private String groupDiscussion;

    private String remarks;
    private String offerLetterUrl;

    @CreationTimestamp
    private LocalDateTime appliedAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    public enum ApplicationStatus {
        APPLIED,
        SHORTLISTED,
        APTITUDE_TEST,
        TECHNICAL_ROUND,
        GD_ROUND,
        HR_ROUND,
        SELECTED,
        REJECTED,
        OFFER_ACCEPTED,
        OFFER_DECLINED,
        WITHDRAWN
    }
}