package com.placement.system.controller;

import com.placement.system.dto.*;
import com.placement.system.service.impl.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/admin")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Admin", description = "Admin-only operations: user management, system control")
public class AdminController {

    private final UserService userService;

    @GetMapping("/users")
    @Operation(summary = "Get all users (ADMIN)")
    public ResponseEntity<ApiResponse<List<UserDTO.UserResponse>>> getAllUsers() {
        return ResponseEntity.ok(ApiResponse.success("All users", userService.getAllUsers()));
    }

    @GetMapping("/students")
    @Operation(summary = "Get all students (ADMIN)")
    public ResponseEntity<ApiResponse<List<UserDTO.StudentProfileResponse>>> getAllStudents() {
        return ResponseEntity.ok(ApiResponse.success("All students", userService.getAllStudents()));
    }

    @PatchMapping("/users/{id}/status")
    @Operation(summary = "Enable or disable a user account (ADMIN)")
    public ResponseEntity<ApiResponse<UserDTO.UserResponse>> toggleUserStatus(
            @PathVariable Long id, @RequestParam boolean enabled) {
        return ResponseEntity.ok(ApiResponse.success(
                enabled ? "User enabled" : "User disabled",
                userService.toggleUserStatus(id, enabled)));
    }

    @PatchMapping("/students/{id}/eligibility")
    @Operation(summary = "Set student placement eligibility (ADMIN)")
    public ResponseEntity<ApiResponse<UserDTO.StudentProfileResponse>> toggleEligibility(
            @PathVariable Long id, @RequestParam boolean eligible) {
        return ResponseEntity.ok(ApiResponse.success(
                "Student eligibility updated", userService.toggleEligibility(id, eligible)));
    }

    @GetMapping("/students/{id}")
    @Operation(summary = "Get a student's profile by ID (ADMIN)")
    public ResponseEntity<ApiResponse<UserDTO.StudentProfileResponse>> getStudentById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success("Student", userService.getStudentProfileById(id)));
    }
}
