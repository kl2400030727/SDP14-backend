package com.placement.system.repository;

import com.placement.system.entity.Application;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ApplicationRepository extends JpaRepository<Application, Long> {

    List<Application> findByStudentId(Long studentId);

    List<Application> findByJobPostingId(Long jobPostingId);

    Optional<Application> findByStudentIdAndJobPostingId(Long studentId, Long jobPostingId);

    List<Application> findByStatus(Application.ApplicationStatus status);

    // postedBy is now EmployerProfile - use postedBy.id (EmployerProfile PK)
    @Query("SELECT a FROM Application a WHERE a.jobPosting.postedBy.id = :employerProfileId ORDER BY a.appliedAt DESC")
    List<Application> findApplicationsByEmployer(Long employerProfileId);

    @Query("SELECT COUNT(a) FROM Application a WHERE a.jobPosting.id = :jobId AND a.status = :status")
    long countByJobPostingIdAndStatus(Long jobId, Application.ApplicationStatus status);

    boolean existsByStudentIdAndJobPostingId(Long studentId, Long jobPostingId);

    @Query("SELECT COUNT(a) FROM Application a WHERE a.status = 'SELECTED'")
    long countSelectedApplications();
    
    @Query("SELECT DISTINCT a FROM Application a " +
            "LEFT JOIN FETCH a.student " +
            "LEFT JOIN FETCH a.studentProfile " +
            "LEFT JOIN FETCH a.jobPosting jp " +
            "LEFT JOIN FETCH jp.postedBy " +
            "WHERE a.student.id = :studentId")
     List<Application> findByStudentIdWithDetails(@Param("studentId") Long studentId);
     
    
    
}