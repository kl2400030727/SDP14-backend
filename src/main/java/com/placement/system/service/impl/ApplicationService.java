package com.placement.system.service.impl;

import com.placement.system.dto.ApplicationDTO;
import com.placement.system.entity.*;
import com.placement.system.exception.*;
import com.placement.system.repository.*;
import com.placement.system.service.EmailService;
import lombok.RequiredArgsConstructor;

import org.hibernate.LazyInitializationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final UserRepository userRepository;
    private final JobPostingRepository jobPostingRepository;
    private final StudentProfileRepository studentProfileRepository;
    private final PlacementRecordRepository placementRecordRepository;
    private final EmailService emailService;

    @Transactional
    public ApplicationDTO.ApplicationResponse applyForJob(ApplicationDTO.ApplicationRequest req, String studentEmail) {
        User student = userRepository.findByEmail(studentEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found"));
        
        JobPosting job = jobPostingRepository.findById(req.getJobPostingId())
                .orElseThrow(() -> new ResourceNotFoundException("Job posting not found"));

        if (applicationRepository.existsByStudentIdAndJobPostingId(student.getId(), job.getId()))
            throw new BadRequestException("You have already applied for this job.");
        if (job.getStatus() != JobPosting.JobStatus.ACTIVE)
            throw new BadRequestException("This job is not accepting applications.");

        StudentProfile profile = studentProfileRepository.findByUserId(student.getId()).orElse(null);

        Application app = Application.builder()
                .student(student)
                .studentProfile(profile)
                .jobPosting(job)
                .status(Application.ApplicationStatus.APPLIED)
                .coverLetter(req.getCoverLetter())
                .resumeUrl(req.getResumeUrl() != null ? req.getResumeUrl()
                        : (profile != null ? profile.getResumeUrl() : null))
                .build();

        app = applicationRepository.save(app);
        return mapToResponse(app);
    }

    public List<ApplicationDTO.ApplicationResponse> getMyApplications(String studentEmail) {
        User student = userRepository.findByEmail(studentEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found"));
        return applicationRepository.findByStudentId(student.getId())
                .stream().map(this::mapToResponse).collect(Collectors.toList());
    }
    @Transactional
    public List<ApplicationDTO.ApplicationResponse> getApplicationsForJob(Long jobId, String employerEmail) {
        JobPosting job = jobPostingRepository.findById(jobId)
                .orElseThrow(() -> new ResourceNotFoundException("Job not found"));

        boolean isOwner = job.getPostedBy() != null
                && job.getPostedBy().getUser() != null
                && job.getPostedBy().getUser().getEmail().equals(employerEmail);

        User caller = userRepository.findByEmail(employerEmail).orElse(null);
        boolean isAdmin = caller != null
                && (caller.getRole() == Role.ROLE_ADMIN
                    || caller.getRole() == Role.ROLE_PLACEMENT_OFFICER);

        if (!isOwner && !isAdmin)
            throw new BadRequestException("You can only view applications for your own jobs.");

        return applicationRepository.findByJobPostingId(jobId)
                .stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    @Transactional
    public ApplicationDTO.ApplicationResponse updateApplicationStatus(
            Long appId, ApplicationDTO.UpdateApplicationStatusRequest req, String updaterEmail) {

        Application app = applicationRepository.findById(appId)
                .orElseThrow(() -> new ResourceNotFoundException("Application not found"));

        Application.ApplicationStatus newStatus = Application.ApplicationStatus.valueOf(req.getStatus());
        app.setStatus(newStatus);
        if (req.getRemarks() != null)         app.setRemarks(req.getRemarks());
        if (req.getAptitudeScore() != null)   app.setAptitudeScore(req.getAptitudeScore());
        if (req.getTechnicalRound() != null)  app.setTechnicalRound(req.getTechnicalRound());
        if (req.getHrRound() != null)         app.setHrRound(req.getHrRound());
        if (req.getGroupDiscussion() != null) app.setGroupDiscussion(req.getGroupDiscussion());
        if (req.getOfferLetterUrl() != null)  app.setOfferLetterUrl(req.getOfferLetterUrl());

        app = applicationRepository.save(app);

        if (newStatus == Application.ApplicationStatus.SELECTED) {
            createPlacementRecord(app);
        }

        String companyName = resolveCompanyName(app);
        emailService.sendApplicationStatusEmail(
                app.getStudent().getEmail(),
                app.getStudent().getFullName(),
                app.getJobPosting() != null ? app.getJobPosting().getTitle() : "",
                companyName,
                newStatus.name());

        return mapToResponse(app);
    }

    @Transactional
    public ApplicationDTO.ApplicationResponse withdrawApplication(Long appId, String studentEmail) {
        Application app = applicationRepository.findById(appId)
                .orElseThrow(() -> new ResourceNotFoundException("Application not found"));
        if (!app.getStudent().getEmail().equals(studentEmail))
            throw new BadRequestException("You can only withdraw your own applications.");
        app.setStatus(Application.ApplicationStatus.WITHDRAWN);
        return mapToResponse(applicationRepository.save(app));
    }

    public List<ApplicationDTO.ApplicationResponse> getAllApplications() {
        return applicationRepository.findAll().stream()
                .map(this::mapToResponse).collect(Collectors.toList());
    }

    private void createPlacementRecord(Application app) {
        if (placementRecordRepository.findByApplicationId(app.getId()).isEmpty()) {
            String companyName = resolveCompanyName(app);

            PlacementRecord record = PlacementRecord.builder()
                    .application(app)
                    .student(app.getStudent())
                    .jobPosting(app.getJobPosting())
                    .companyName(companyName)
                    .jobTitle(app.getJobPosting() != null ? app.getJobPosting().getTitle() : "")
                    .ctcOffered(app.getJobPosting() != null ? app.getJobPosting().getMaxCTC() : null)
                    .academicYear(java.time.Year.now().toString())
                    .placementType(PlacementRecord.PlacementType.CAMPUS)
                    .build();

            placementRecordRepository.save(record);

            if (app.getStudentProfile() != null) {
                StudentProfile sp = app.getStudentProfile();
                sp.setPlacementStatus(StudentProfile.PlacementStatus.PLACED);
                sp.setPlacedCompany(companyName);
                sp.setCtcOffered(app.getJobPosting() != null ? app.getJobPosting().getMaxCTC() : null);
                sp.setPlacementDate(java.time.LocalDate.now());
            }
        }
    }

    // postedBy is EmployerProfile - getCompanyName() directly, no .getEmployerProfile() needed
    private String resolveCompanyName(Application app) {
        if (app.getJobPosting() != null && app.getJobPosting().getPostedBy() != null) {
            String name = app.getJobPosting().getPostedBy().getCompanyName();
            return name != null ? name : "Company";
        }
        return "Company";
    }

    private ApplicationDTO.ApplicationResponse mapToResponse(Application app) {
        StudentProfile sp = app.getStudentProfile();
        String companyName = null;
        String jobTitle = null;
        Long jobPostingId = null;
        
        // ✅ FIX: Safely get job posting details without triggering lazy loading
        if (app.getJobPosting() != null) {
            try {
                jobPostingId = app.getJobPosting().getId();
                jobTitle = app.getJobPosting().getTitle();
                
                // ✅ CRITICAL FIX: Handle postedBy safely
                if (app.getJobPosting().getPostedBy() != null) {
                    companyName = app.getJobPosting().getPostedBy().getCompanyName();
                }
            } catch (LazyInitializationException e) {
                // Log but continue - we'll return null for these fields
                System.err.println("Lazy loading failed for application " + app.getId());
            }
        }

        return ApplicationDTO.ApplicationResponse.builder()
                .id(app.getId())
                .studentId(app.getStudent() != null ? app.getStudent().getId() : null)
                .studentName(app.getStudent() != null ? app.getStudent().getFullName() : null)
                .studentEmail(app.getStudent() != null ? app.getStudent().getEmail() : null)
                .rollNumber(sp != null ? sp.getRollNumber() : null)
                .department(sp != null ? sp.getDepartment() : null)
                .cgpa(sp != null ? sp.getCgpa() : null)
                .jobPostingId(jobPostingId)
                .jobTitle(jobTitle)
                .companyName(companyName)
                .status(app.getStatus() != null ? app.getStatus().name() : null)
                .coverLetter(app.getCoverLetter())
                .resumeUrl(app.getResumeUrl())
                .aptitudeScore(app.getAptitudeScore())
                .technicalRound(app.getTechnicalRound())
                .hrRound(app.getHrRound())
                .groupDiscussion(app.getGroupDiscussion())
                .remarks(app.getRemarks())
                .offerLetterUrl(app.getOfferLetterUrl())
                .appliedAt(app.getAppliedAt())
                .updatedAt(app.getUpdatedAt())
                .build();
    }
}