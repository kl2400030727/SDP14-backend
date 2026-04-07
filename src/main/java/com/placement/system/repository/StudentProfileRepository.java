package com.placement.system.repository;

import com.placement.system.entity.StudentProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentProfileRepository extends JpaRepository<StudentProfile, Long> {

    Optional<StudentProfile> findByUserId(Long userId);

    Optional<StudentProfile> findByRollNumber(String rollNumber);

    List<StudentProfile> findByDepartment(String department);

    List<StudentProfile> findByBatch(String batch);

    List<StudentProfile> findByEligibleTrue();

    List<StudentProfile> findByPlacementStatus(StudentProfile.PlacementStatus status);

    @Query("SELECT s FROM StudentProfile s WHERE s.cgpa >= :minCgpa AND s.eligible = true")
    List<StudentProfile> findEligibleStudentsByCgpa(Double minCgpa);

    @Query("SELECT COUNT(s) FROM StudentProfile s WHERE s.placementStatus = 'PLACED'")
    long countPlacedStudents();

    @Query("SELECT COUNT(s) FROM StudentProfile s WHERE s.batch = :batch AND s.placementStatus = 'PLACED'")
    long countPlacedStudentsByBatch(String batch);
}
