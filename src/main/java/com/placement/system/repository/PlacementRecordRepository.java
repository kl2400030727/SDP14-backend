package com.placement.system.repository;

import com.placement.system.entity.PlacementRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PlacementRecordRepository extends JpaRepository<PlacementRecord, Long> {

    List<PlacementRecord> findByStudentId(Long studentId);

    Optional<PlacementRecord> findByApplicationId(Long applicationId);

    List<PlacementRecord> findByAcademicYear(String academicYear);

    @Query("SELECT AVG(p.ctcOffered) FROM PlacementRecord p WHERE p.academicYear = :year")
    Double findAverageCtcByYear(String year);

    @Query("SELECT MAX(p.ctcOffered) FROM PlacementRecord p WHERE p.academicYear = :year")
    Double findMaxCtcByYear(String year);

    long countByAcademicYear(String year);
}
