package com.placement.system.controller;

import com.placement.system.dto.*;
import com.placement.system.service.impl.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/profile")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
@Tag(name = "Profile", description = "User profile management")
public class ProfileController {

    private final UserService userService;

    @GetMapping("/me")
    @Operation(summary = "Get current user info")
    public ResponseEntity<ApiResponse<UserDTO.UserResponse>> getMe(Authentication auth) {
        return ResponseEntity.ok(ApiResponse.success("Current user", userService.getCurrentUser(auth.getName())));
    }

    @PutMapping("/me")
    @Operation(summary = "Update basic profile (name, phone)")
    public ResponseEntity<ApiResponse<UserDTO.UserResponse>> updateMe(
            @RequestBody UserDTO.UpdateProfileRequest req, Authentication auth) {
        return ResponseEntity.ok(ApiResponse.success("Profile updated", userService.updateProfile(auth.getName(), req)));
    }

    @GetMapping("/student")
    @PreAuthorize("hasRole('STUDENT')")
    @Operation(summary = "Get student profile (STUDENT)")
    public ResponseEntity<ApiResponse<UserDTO.StudentProfileResponse>> getStudentProfile(Authentication auth) {
        return ResponseEntity.ok(ApiResponse.success("Student profile", userService.getStudentProfile(auth.getName())));
    }

    @PutMapping("/student")
    @PreAuthorize("hasRole('STUDENT')")
    @Operation(summary = "Update student profile (STUDENT)")
    public ResponseEntity<ApiResponse<UserDTO.StudentProfileResponse>> updateStudentProfile(
            @RequestBody UserDTO.StudentProfileRequest req, Authentication auth) {
        return ResponseEntity.ok(ApiResponse.success("Student profile updated",
                userService.updateStudentProfile(auth.getName(), req)));
    }

    @GetMapping("/employer")
    @PreAuthorize("hasRole('EMPLOYER')")
    @Operation(summary = "Get employer profile (EMPLOYER)")
    public ResponseEntity<ApiResponse<UserDTO.EmployerProfileResponse>> getEmployerProfile(Authentication auth) {
        return ResponseEntity.ok(ApiResponse.success("Employer profile", userService.getEmployerProfile(auth.getName())));
    }

    @PutMapping("/employer")
    @PreAuthorize("hasRole('EMPLOYER')")
    @Operation(summary = "Update employer/company profile (EMPLOYER)")
    public ResponseEntity<ApiResponse<UserDTO.EmployerProfileResponse>> updateEmployerProfile(
            @RequestBody UserDTO.EmployerProfileRequest req, Authentication auth) {
        return ResponseEntity.ok(ApiResponse.success("Employer profile updated",
                userService.updateEmployerProfile(auth.getName(), req)));
    }
}
