package com.placement.system.repository;

import com.placement.system.entity.JobPosting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface JobPostingRepository extends JpaRepository<JobPosting, Long> {

    List<JobPosting> findByStatus(JobPosting.JobStatus status);

    List<JobPosting> findByPostedById(Long employerProfileId);

    // FIXED: pass enum as parameter instead of hardcoded string 'ACTIVE'
    // LEFT JOIN FETCH j.postedBy loads company name in same query — no LazyInit error
    @Query("SELECT j FROM JobPosting j LEFT JOIN FETCH j.postedBy " +
           "WHERE j.status = :status AND j.approvedByAdmin = true " +
           "ORDER BY j.createdAt DESC")
    List<JobPosting> findAllActiveApprovedJobs(@Param("status") JobPosting.JobStatus status);

    // FIXED: removed LEFT JOIN FETCH j.applications — caused MultipleBagFetchException
    @Query("SELECT j FROM JobPosting j LEFT JOIN FETCH j.postedBy p " +
           "WHERE p.id = :employerProfileId " +
           "ORDER BY j.createdAt DESC")
    List<JobPosting> findByEmployerIdOrderByCreatedAtDesc(
            @Param("employerProfileId") Long employerProfileId);

    @Query("SELECT j FROM JobPosting j LEFT JOIN FETCH j.postedBy")
    List<JobPosting> findAllWithPostedBy();

    long countByStatus(JobPosting.JobStatus status);
}