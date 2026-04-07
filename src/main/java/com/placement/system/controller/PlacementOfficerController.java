package com.placement.system.controller;

import com.placement.system.dto.*;
import com.placement.system.service.impl.PlacementOfficerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/officer")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('PLACEMENT_OFFICER','ADMIN')")
@Tag(name = "Placement Officer", description = "Placement records, reports, and analytics")
public class PlacementOfficerController {

    private final PlacementOfficerService officerService;

    @GetMapping("/dashboard")
    @Operation(summary = "Placement dashboard stats")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getDashboard() {
        return ResponseEntity.ok(ApiResponse.success("Dashboard stats", officerService.getDashboardStats()));
    }

    @GetMapping("/placement-records")
    @Operation(summary = "Get all placement records (filter by academic year)")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getPlacementRecords(
            @RequestParam(required = false) String academicYear) {
        return ResponseEntity.ok(ApiResponse.success("Placement records",
                officerService.getPlacementRecords(academicYear)));
    }

    @GetMapping("/reports/batch-wise")
    @Operation(summary = "Batch-wise placement statistics")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getBatchStats() {
        return ResponseEntity.ok(ApiResponse.success("Batch-wise stats", officerService.getBatchWiseStats()));
    }

    @GetMapping("/reports/department-wise")
    @Operation(summary = "Department-wise placement statistics")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getDeptStats() {
        return ResponseEntity.ok(ApiResponse.success("Department-wise stats", officerService.getDepartmentWiseStats()));
    }

    @PatchMapping("/students/{studentId}/eligibility")
    @Operation(summary = "Update a student's placement eligibility")
    public ResponseEntity<ApiResponse<Object>> updateEligibility(
            @PathVariable Long studentId, @RequestParam boolean eligible) {
        officerService.updateStudentEligibility(studentId, eligible);
        return ResponseEntity.ok(ApiResponse.success("Eligibility updated."));
    }

    @GetMapping("/applications")
    @Operation(summary = "View all applications")
    public ResponseEntity<ApiResponse<List<ApplicationDTO.ApplicationResponse>>> getAllApplications() {
        return ResponseEntity.ok(ApiResponse.success("All applications", officerService.getAllApplications()));
    }
}
