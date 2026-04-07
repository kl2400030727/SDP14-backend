package com.placement.system.service;

import com.placement.system.dto.AuthDTO;

public interface AuthService {
    AuthDTO.AuthResponse register(AuthDTO.RegisterRequest request);
    AuthDTO.AuthResponse login(AuthDTO.LoginRequest request);
    void verifyEmail(String token);
    void forgotPassword(String email);
    void resetPassword(AuthDTO.ResetPasswordRequest request);
    void changePassword(String email, AuthDTO.ChangePasswordRequest request);
    AuthDTO.AuthResponse refreshToken(String refreshToken);
}
