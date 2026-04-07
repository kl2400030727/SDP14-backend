package com.placement.system.repository;

import com.placement.system.entity.JobPosting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface JobPostingRepository extends JpaRepository<JobPosting, Long> {

    List<JobPosting> findByStatus(JobPosting.JobStatus status);

    // postedBy is now EmployerProfile, so filter by EmployerProfile PK
    List<JobPosting> findByPostedById(Long employerProfileId);

    @Query("SELECT j FROM JobPosting j WHERE j.status = 'ACTIVE' AND j.approvedByAdmin = true ORDER BY j.createdAt DESC")
    List<JobPosting> findAllActiveApprovedJobs();

    @Query("SELECT j FROM JobPosting j WHERE j.postedBy.id = :employerProfileId ORDER BY j.createdAt DESC")
    List<JobPosting> findByEmployerIdOrderByCreatedAtDesc(Long employerProfileId);

    long countByStatus(JobPosting.JobStatus status);
}