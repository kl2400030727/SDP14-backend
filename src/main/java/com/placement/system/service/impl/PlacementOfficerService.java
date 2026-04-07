package com.placement.system.service.impl;

import com.placement.system.dto.ApplicationDTO;
import com.placement.system.entity.*;
import com.placement.system.exception.ResourceNotFoundException;
import com.placement.system.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PlacementOfficerService {

    private final PlacementRecordRepository placementRecordRepository;
    private final StudentProfileRepository studentProfileRepository;
    private final UserRepository userRepository;
    private final JobPostingRepository jobPostingRepository;
    private final ApplicationRepository applicationRepository;

    public Map<String, Object> getDashboardStats() {
        Map<String, Object> stats = new LinkedHashMap<>();

        long totalStudents        = userRepository.countByRoleAndEnabledTrue(Role.ROLE_STUDENT);
        long placedStudents       = studentProfileRepository.countPlacedStudents();
        long activeJobs           = jobPostingRepository.countByStatus(JobPosting.JobStatus.ACTIVE);
        long totalApplications    = applicationRepository.count();
        long selectedApplications = applicationRepository.countSelectedApplications();
        long totalCompanies       = userRepository.countByRoleAndEnabledTrue(Role.ROLE_EMPLOYER);

        stats.put("totalStudents", totalStudents);
        stats.put("placedStudents", placedStudents);
        stats.put("notPlacedStudents", totalStudents - placedStudents);
        stats.put("placementPercentage", totalStudents > 0
                ? Math.round((double) placedStudents / totalStudents * 100 * 100.0) / 100.0 : 0);
        stats.put("activeJobs", activeJobs);
        stats.put("totalApplications", totalApplications);
        stats.put("selectedApplications", selectedApplications);
        stats.put("totalCompanies", totalCompanies);

        String currentYear = String.valueOf(java.time.Year.now().getValue());
        Double avgCTC = placementRecordRepository.findAverageCtcByYear(currentYear);
        Double maxCTC = placementRecordRepository.findMaxCtcByYear(currentYear);
        stats.put("averageCTC", avgCTC != null ? avgCTC : 0);
        stats.put("maxCTC", maxCTC != null ? maxCTC : 0);
        return stats;
    }

    public List<Map<String, Object>> getPlacementRecords(String academicYear) {
        List<PlacementRecord> records = academicYear != null
                ? placementRecordRepository.findByAcademicYear(academicYear)
                : placementRecordRepository.findAll();

        return records.stream().map(r -> {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("id", r.getId());
            m.put("studentName",  r.getStudent() != null ? r.getStudent().getFullName() : null);
            m.put("studentEmail", r.getStudent() != null ? r.getStudent().getEmail() : null);
            if (r.getStudent() != null) {
                studentProfileRepository.findByUserId(r.getStudent().getId()).ifPresent(sp -> {
                    m.put("rollNumber", sp.getRollNumber());
                    m.put("department", sp.getDepartment());
                    m.put("batch",      sp.getBatch());
                    m.put("cgpa",       sp.getCgpa());
                });
            }
            m.put("companyName",   r.getCompanyName());
            m.put("jobTitle",      r.getJobTitle());
            m.put("ctcOffered",    r.getCtcOffered());
            m.put("joiningDate",   r.getJoiningDate());
            m.put("academicYear",  r.getAcademicYear());
            m.put("placementType", r.getPlacementType() != null ? r.getPlacementType().name() : null);
            m.put("recordedAt",    r.getRecordedAt());
            return m;
        }).collect(Collectors.toList());
    }

    public List<Map<String, Object>> getBatchWiseStats() {
        List<StudentProfile> all = studentProfileRepository.findAll();
        Map<String, long[]> batchMap = new LinkedHashMap<>();
        for (StudentProfile sp : all) {
            String batch = sp.getBatch() != null ? sp.getBatch() : "Unknown";
            batchMap.computeIfAbsent(batch, k -> new long[]{0, 0});
            batchMap.get(batch)[0]++;
            if (sp.getPlacementStatus() == StudentProfile.PlacementStatus.PLACED)
                batchMap.get(batch)[1]++;
        }
        return batchMap.entrySet().stream().map(e -> {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("batch",      e.getKey());
            m.put("total",      e.getValue()[0]);
            m.put("placed",     e.getValue()[1]);
            m.put("percentage", e.getValue()[0] > 0
                    ? Math.round((double) e.getValue()[1] / e.getValue()[0] * 100 * 100.0) / 100.0 : 0);
            return m;
        }).collect(Collectors.toList());
    }

    public List<Map<String, Object>> getDepartmentWiseStats() {
        List<StudentProfile> all = studentProfileRepository.findAll();
        Map<String, long[]> deptMap = new LinkedHashMap<>();
        for (StudentProfile sp : all) {
            String dept = sp.getDepartment() != null ? sp.getDepartment() : "Unknown";
            deptMap.computeIfAbsent(dept, k -> new long[]{0, 0});
            deptMap.get(dept)[0]++;
            if (sp.getPlacementStatus() == StudentProfile.PlacementStatus.PLACED)
                deptMap.get(dept)[1]++;
        }
        return deptMap.entrySet().stream().map(e -> {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("department", e.getKey());
            m.put("total",      e.getValue()[0]);
            m.put("placed",     e.getValue()[1]);
            m.put("percentage", e.getValue()[0] > 0
                    ? Math.round((double) e.getValue()[1] / e.getValue()[0] * 100 * 100.0) / 100.0 : 0);
            return m;
        }).collect(Collectors.toList());
    }

    @Transactional
    public void updateStudentEligibility(Long studentId, boolean eligible) {
        StudentProfile sp = studentProfileRepository.findByUserId(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student profile not found"));
        sp.setEligible(eligible);
        studentProfileRepository.save(sp);
    }

    public List<ApplicationDTO.ApplicationResponse> getAllApplications() {
        return applicationRepository.findAll().stream().map(app -> {
            StudentProfile sp = app.getStudentProfile();
            // postedBy is EmployerProfile - getCompanyName() directly
            String company = null;
            if (app.getJobPosting() != null && app.getJobPosting().getPostedBy() != null)
                company = app.getJobPosting().getPostedBy().getCompanyName();

            return ApplicationDTO.ApplicationResponse.builder()
                    .id(app.getId())
                    .studentId(app.getStudent() != null ? app.getStudent().getId() : null)
                    .studentName(app.getStudent() != null ? app.getStudent().getFullName() : null)
                    .studentEmail(app.getStudent() != null ? app.getStudent().getEmail() : null)
                    .rollNumber(sp != null ? sp.getRollNumber() : null)
                    .department(sp != null ? sp.getDepartment() : null)
                    .cgpa(sp != null ? sp.getCgpa() : null)
                    .jobPostingId(app.getJobPosting() != null ? app.getJobPosting().getId() : null)
                    .jobTitle(app.getJobPosting() != null ? app.getJobPosting().getTitle() : null)
                    .companyName(company)
                    .status(app.getStatus() != null ? app.getStatus().name() : null)
                    .appliedAt(app.getAppliedAt())
                    .updatedAt(app.getUpdatedAt())
                    .build();
        }).collect(Collectors.toList());
    }
}