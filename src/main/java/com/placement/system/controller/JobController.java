package com.placement.system.controller;

import com.placement.system.dto.*;
import com.placement.system.service.impl.JobPostingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/jobs")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
@Tag(name = "Job Postings", description = "CRUD for job postings")
public class JobController {

    private final JobPostingService jobPostingService;

    @GetMapping
    @Operation(summary = "Get all active approved jobs (all authenticated users)")
    public ResponseEntity<ApiResponse<List<JobDTO.JobPostingResponse>>> getAllActiveJobs(Authentication auth) {
        return ResponseEntity.ok(ApiResponse.success("Jobs fetched",
                jobPostingService.getAllActiveJobs(auth.getName())));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get job by ID")
    public ResponseEntity<ApiResponse<JobDTO.JobPostingResponse>> getJobById(
            @PathVariable Long id, Authentication auth) {
        return ResponseEntity.ok(ApiResponse.success("Job fetched",
                jobPostingService.getJobById(id, auth.getName())));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('EMPLOYER','ADMIN')")
    @Operation(summary = "Create a new job posting (EMPLOYER / ADMIN)")
    public ResponseEntity<ApiResponse<JobDTO.JobPostingResponse>> createJob(
            @Valid @RequestBody JobDTO.JobPostingRequest request, Authentication auth) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(
                "Job posting created and submitted for approval.",
                jobPostingService.createJob(request, auth.getName())));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('EMPLOYER','ADMIN')")
    @Operation(summary = "Update own job posting (EMPLOYER / ADMIN)")
    public ResponseEntity<ApiResponse<JobDTO.JobPostingResponse>> updateJob(
            @PathVariable Long id, @Valid @RequestBody JobDTO.JobPostingRequest request, Authentication auth) {
        return ResponseEntity.ok(ApiResponse.success("Job updated",
                jobPostingService.updateJob(id, request, auth.getName())));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('EMPLOYER','ADMIN')")
    @Operation(summary = "Delete/cancel a job posting")
    public ResponseEntity<ApiResponse<Object>> deleteJob(@PathVariable Long id, Authentication auth) {
        jobPostingService.deleteJob(id, auth.getName());
        return ResponseEntity.ok(ApiResponse.success("Job posting cancelled."));
    }

    @PatchMapping("/{id}/approve")
    @PreAuthorize("hasAnyRole('ADMIN','PLACEMENT_OFFICER')")
    @Operation(summary = "Approve or reject a job posting (ADMIN / PLACEMENT_OFFICER)")
    public ResponseEntity<ApiResponse<JobDTO.JobPostingResponse>> approveJob(
            @PathVariable Long id, @RequestParam boolean approve) {
        return ResponseEntity.ok(ApiResponse.success(
                approve ? "Job approved and is now live." : "Job rejected.",
                jobPostingService.approveJob(id, approve)));
    }

    @GetMapping("/all")
    @PreAuthorize("hasAnyRole('ADMIN','PLACEMENT_OFFICER')")
    @Operation(summary = "Get all jobs (ADMIN / PLACEMENT_OFFICER)")
    public ResponseEntity<ApiResponse<List<JobDTO.JobPostingResponse>>> getAllJobs() {
        return ResponseEntity.ok(ApiResponse.success("All jobs", jobPostingService.getAllJobs()));
    }

    @GetMapping("/my-postings")
    @PreAuthorize("hasAnyRole('EMPLOYER')")
    @Operation(summary = "Get employer's own job postings")
    public ResponseEntity<ApiResponse<List<JobDTO.JobPostingResponse>>> getMyPostings(Authentication auth) {
        return ResponseEntity.ok(ApiResponse.success("Your job postings",
                jobPostingService.getJobsByEmployer(auth.getName())));
    }
}
