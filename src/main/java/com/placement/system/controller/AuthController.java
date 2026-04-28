package com.placement.system.controller;

import com.placement.system.dto.ApiResponse;
import com.placement.system.dto.AuthDTO;
import com.placement.system.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Register, Login, Email Verification, Password Reset")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    @Operation(summary = "Register a new user (STUDENT, EMPLOYER, PLACEMENT_OFFICER)")
    public ResponseEntity<ApiResponse<AuthDTO.AuthResponse>> register(
            @Valid @RequestBody AuthDTO.RegisterRequest request) {
        AuthDTO.AuthResponse response = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Registration successful. Please verify your email.", response));
    }
    @GetMapping("/")  // This handles root path
    public ResponseEntity<String> root() {
        return ResponseEntity.ok("Placement System API is running");
    }
    @PostMapping("/login")
    @Operation(summary = "Login with email and password")
    public ResponseEntity<ApiResponse<AuthDTO.AuthResponse>> login(
            @Valid @RequestBody AuthDTO.LoginRequest request) {
        AuthDTO.AuthResponse response = authService.login(request);
        return ResponseEntity.ok(ApiResponse.success("Login successful", response));
    }

    @GetMapping("/verify-email")
    @Operation(summary = "Verify email with token")
    public ResponseEntity<ApiResponse<Object>> verifyEmail(@RequestParam String token) {
        authService.verifyEmail(token);
        return ResponseEntity.ok(ApiResponse.success("Email verified successfully!"));
    }

    @PostMapping("/forgot-password")
    @Operation(summary = "Send password reset email")
    public ResponseEntity<ApiResponse<Object>> forgotPassword(
            @Valid @RequestBody AuthDTO.ForgotPasswordRequest request) {
        authService.forgotPassword(request.getEmail());
        return ResponseEntity.ok(ApiResponse.success("Password reset email sent. Please check your inbox."));
    }

    @PostMapping("/reset-password")
    @Operation(summary = "Reset password using token")
    public ResponseEntity<ApiResponse<Object>> resetPassword(
            @Valid @RequestBody AuthDTO.ResetPasswordRequest request) {
        authService.resetPassword(request);
        return ResponseEntity.ok(ApiResponse.success("Password reset successfully."));
    }

    @PostMapping("/change-password")
    @Operation(summary = "Change password (authenticated)")
    public ResponseEntity<ApiResponse<Object>> changePassword(
            @Valid @RequestBody AuthDTO.ChangePasswordRequest request,
            Authentication authentication) {
        authService.changePassword(authentication.getName(), request);
        return ResponseEntity.ok(ApiResponse.success("Password changed successfully."));
    }

    @PostMapping("/refresh-token")
    @Operation(summary = "Refresh JWT access token")
    public ResponseEntity<ApiResponse<AuthDTO.AuthResponse>> refreshToken(
            @RequestHeader("Refresh-Token") String refreshToken) {
        AuthDTO.AuthResponse response = authService.refreshToken(refreshToken);
        return ResponseEntity.ok(ApiResponse.success("Token refreshed", response));
    }
}
