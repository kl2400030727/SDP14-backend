package com.placement.system.controller;

import com.placement.system.dto.*;
import com.placement.system.service.impl.ApplicationService;
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
@RequestMapping("/applications")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
@Tag(name = "Applications", description = "Job application management")
public class ApplicationController {

    private final ApplicationService applicationService;

    @PostMapping
    @PreAuthorize("hasRole('STUDENT')")
    @Operation(summary = "Apply for a job (STUDENT)")
    public ResponseEntity<ApiResponse<ApplicationDTO.ApplicationResponse>> applyForJob(
            @Valid @RequestBody ApplicationDTO.ApplicationRequest request, Authentication auth) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(
                "Application submitted successfully.",
                applicationService.applyForJob(request, auth.getName())));
    }

    @GetMapping("/my-applications")
    @PreAuthorize("hasRole('STUDENT')")
    @Operation(summary = "Get student's own applications (STUDENT)")
    public ResponseEntity<ApiResponse<List<ApplicationDTO.ApplicationResponse>>> getMyApplications(
            Authentication auth) {
        return ResponseEntity.ok(ApiResponse.success("Applications",
                applicationService.getMyApplications(auth.getName())));
    }

    @GetMapping("/job/{jobId}")
    @PreAuthorize("hasAnyRole('EMPLOYER','ADMIN','PLACEMENT_OFFICER')")
    @Operation(summary = "Get applications for a specific job (EMPLOYER / ADMIN / OFFICER)")
    public ResponseEntity<ApiResponse<List<ApplicationDTO.ApplicationResponse>>> getApplicationsForJob(
            @PathVariable Long jobId, Authentication auth) {
        return ResponseEntity.ok(ApiResponse.success("Applications for job",
                applicationService.getApplicationsForJob(jobId, auth.getName())));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('EMPLOYER','ADMIN','PLACEMENT_OFFICER')")
    @Operation(summary = "Update application status (EMPLOYER / ADMIN / OFFICER)")
    public ResponseEntity<ApiResponse<ApplicationDTO.ApplicationResponse>> updateStatus(
            @PathVariable Long id,
            @RequestBody ApplicationDTO.UpdateApplicationStatusRequest request,
            Authentication auth) {
        return ResponseEntity.ok(ApiResponse.success("Application status updated",
                applicationService.updateApplicationStatus(id, request, auth.getName())));
    }

    @PatchMapping("/{id}/withdraw")
    @PreAuthorize("hasRole('STUDENT')")
    @Operation(summary = "Withdraw application (STUDENT)")
    public ResponseEntity<ApiResponse<ApplicationDTO.ApplicationResponse>> withdraw(
            @PathVariable Long id, Authentication auth) {
        return ResponseEntity.ok(ApiResponse.success("Application withdrawn",
                applicationService.withdrawApplication(id, auth.getName())));
    }

    @GetMapping("/all")
    @PreAuthorize("hasAnyRole('ADMIN','PLACEMENT_OFFICER')")
    @Operation(summary = "Get all applications (ADMIN / PLACEMENT_OFFICER)")
    public ResponseEntity<ApiResponse<List<ApplicationDTO.ApplicationResponse>>> getAllApplications() {
        return ResponseEntity.ok(ApiResponse.success("All applications",
                applicationService.getAllApplications()));
    }
}
