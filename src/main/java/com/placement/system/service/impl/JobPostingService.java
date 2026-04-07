package com.placement.system.service.impl;

import com.placement.system.dto.JobDTO;
import com.placement.system.entity.*;
import com.placement.system.exception.*;
import com.placement.system.repository.*;
import com.placement.system.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class JobPostingService {

    private final JobPostingRepository jobPostingRepository;
    private final UserRepository userRepository;
    private final EmployerProfileRepository employerProfileRepository;
    private final ApplicationRepository applicationRepository;
    private final EmailService emailService;

    @Transactional
    public JobDTO.JobPostingResponse createJob(JobDTO.JobPostingRequest request, String employerEmail) {
        User employer = userRepository.findByEmail(employerEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Employer user not found"));

        EmployerProfile ep = employerProfileRepository.findByUserId(employer.getId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Employer profile not found. Please complete your company profile first."));

        JobPosting job = JobPosting.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .requirements(request.getRequirements())
                .location(request.getLocation())
                .jobType(request.getJobType() != null
                        ? JobPosting.JobType.valueOf(request.getJobType())
                        : JobPosting.JobType.FULL_TIME)
                .status(JobPosting.JobStatus.PENDING_APPROVAL)
                .minCTC(request.getMinCTC())
                .maxCTC(request.getMaxCTC())
                .openings(request.getOpenings())
                .skills(request.getSkills())
                .minCGPA(request.getMinCGPA())
                .maxBacklogs(request.getMaxBacklogs())
                .eligibleBranches(request.getEligibleBranches())
                .applicationDeadline(request.getApplicationDeadline())
                .driveDate(request.getDriveDate())
                .postedBy(ep)
                .approvedByAdmin(false)
                .build();

        job = jobPostingRepository.save(job);
        return mapToResponse(job, false);
    }

    @Transactional
    public JobDTO.JobPostingResponse updateJob(Long jobId, JobDTO.JobPostingRequest req, String email) {
        JobPosting job = jobPostingRepository.findById(jobId)
                .orElseThrow(() -> new ResourceNotFoundException("Job posting not found"));

        if (job.getPostedBy() == null
                || job.getPostedBy().getUser() == null
                || !job.getPostedBy().getUser().getEmail().equals(email)) {
            throw new BadRequestException("You can only edit your own job postings.");
        }

        job.setTitle(req.getTitle());
        job.setDescription(req.getDescription());
        job.setRequirements(req.getRequirements());
        job.setLocation(req.getLocation());
        job.setMinCTC(req.getMinCTC());
        job.setMaxCTC(req.getMaxCTC());
        job.setOpenings(req.getOpenings());
        job.setSkills(req.getSkills());
        job.setMinCGPA(req.getMinCGPA());
        job.setApplicationDeadline(req.getApplicationDeadline());
        job.setDriveDate(req.getDriveDate());
        job.setStatus(JobPosting.JobStatus.PENDING_APPROVAL);
        job.setApprovedByAdmin(false);

        return mapToResponse(jobPostingRepository.save(job), false);
    }

    @Transactional
    public JobDTO.JobPostingResponse approveJob(Long jobId, boolean approve) {
        JobPosting job = jobPostingRepository.findById(jobId)
                .orElseThrow(() -> new ResourceNotFoundException("Job posting not found"));

        job.setApprovedByAdmin(approve);
        job.setStatus(approve ? JobPosting.JobStatus.ACTIVE : JobPosting.JobStatus.CANCELLED);
        job = jobPostingRepository.save(job);

        if (job.getPostedBy() != null && job.getPostedBy().getUser() != null) {
            emailService.sendJobApprovalEmail(
                    job.getPostedBy().getUser().getEmail(),
                    job.getPostedBy().getUser().getFullName(),
                    job.getTitle(),
                    approve);
        }

        return mapToResponse(job, false);
    }

    public List<JobDTO.JobPostingResponse> getAllActiveJobs(String studentEmail) {
        User student = studentEmail != null
                ? userRepository.findByEmail(studentEmail).orElse(null) : null;

        return jobPostingRepository.findAllActiveApprovedJobs().stream().map(job -> {
            boolean applied = student != null
                    && applicationRepository.existsByStudentIdAndJobPostingId(student.getId(), job.getId());
            return mapToResponse(job, applied);
        }).collect(Collectors.toList());
    }

    public List<JobDTO.JobPostingResponse> getAllJobs() {
        return jobPostingRepository.findAll().stream()
                .map(j -> mapToResponse(j, false))
                .collect(Collectors.toList());
    }

    public List<JobDTO.JobPostingResponse> getJobsByEmployer(String email) {
        User employer = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Employer not found"));
        EmployerProfile ep = employerProfileRepository.findByUserId(employer.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Employer profile not found"));

        return jobPostingRepository.findByEmployerIdOrderByCreatedAtDesc(ep.getId())
                .stream().map(j -> mapToResponse(j, false)).collect(Collectors.toList());
    }

    public JobDTO.JobPostingResponse getJobById(Long jobId, String userEmail) {
        JobPosting job = jobPostingRepository.findById(jobId)
                .orElseThrow(() -> new ResourceNotFoundException("Job not found"));
        User user = userEmail != null ? userRepository.findByEmail(userEmail).orElse(null) : null;
        boolean applied = user != null
                && applicationRepository.existsByStudentIdAndJobPostingId(user.getId(), jobId);
        return mapToResponse(job, applied);
    }

    @Transactional
    public void deleteJob(Long jobId, String email) {
        JobPosting job = jobPostingRepository.findById(jobId)
                .orElseThrow(() -> new ResourceNotFoundException("Job posting not found"));

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        boolean isOwner = job.getPostedBy() != null
                && job.getPostedBy().getUser() != null
                && job.getPostedBy().getUser().getEmail().equals(email);
        boolean isAdmin = user.getRole() == Role.ROLE_ADMIN;

        if (!isOwner && !isAdmin)
            throw new BadRequestException("No permission to delete this job posting.");

        job.setStatus(JobPosting.JobStatus.CANCELLED);
        jobPostingRepository.save(job);
    }

    private JobDTO.JobPostingResponse mapToResponse(JobPosting job, boolean applied) {
        String companyName = null, companyLogo = null;
        Long postedById = null;
        if (job.getPostedBy() != null) {
            companyName = job.getPostedBy().getCompanyName();
            companyLogo = job.getPostedBy().getLogoUrl();
            postedById = job.getPostedBy().getId();
        }

        int appCount = job.getApplications() != null ? job.getApplications().size() : 0;

        return JobDTO.JobPostingResponse.builder()
                .id(job.getId())
                .title(job.getTitle())
                .description(job.getDescription())
                .requirements(job.getRequirements())
                .location(job.getLocation())
                .jobType(job.getJobType() != null ? job.getJobType().name() : null)
                .status(job.getStatus() != null ? job.getStatus().name() : null)
                .minCTC(job.getMinCTC())
                .maxCTC(job.getMaxCTC())
                .openings(job.getOpenings())
                .skills(job.getSkills())
                .minCGPA(job.getMinCGPA())
                .maxBacklogs(job.getMaxBacklogs())
                .eligibleBranches(job.getEligibleBranches())
                .applicationDeadline(job.getApplicationDeadline())
                .driveDate(job.getDriveDate())
                .postedById(postedById)
                .companyName(companyName)
                .companyLogo(companyLogo)
                .approvedByAdmin(job.isApprovedByAdmin())
                .approvedByOfficer(job.isApprovedByOfficer())
                .createdAt(job.getCreatedAt())
                .applicationCount(appCount)
                .alreadyApplied(applied)
                .build();
    }
}