package com.placement.system.service.impl;

import com.placement.system.dto.UserDTO;
import com.placement.system.entity.*;
import com.placement.system.exception.*;
import com.placement.system.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final StudentProfileRepository studentProfileRepository;
    private final EmployerProfileRepository employerProfileRepository;

    public UserDTO.UserResponse getCurrentUser(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return mapToUserResponse(user);
    }

    @Transactional
    public UserDTO.UserResponse updateProfile(String email, UserDTO.UpdateProfileRequest req) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        if (req.getFullName() != null) user.setFullName(req.getFullName());
        if (req.getPhone() != null) user.setPhone(req.getPhone());
        if (req.getProfilePicture() != null) user.setProfilePicture(req.getProfilePicture());
        return mapToUserResponse(userRepository.save(user));
    }

    // STUDENT profile
    public UserDTO.StudentProfileResponse getStudentProfile(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        StudentProfile sp = studentProfileRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Student profile not found"));
        return mapToStudentResponse(user, sp);
    }

    public UserDTO.StudentProfileResponse getStudentProfileById(Long studentId) {
        User user = userRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found"));
        StudentProfile sp = studentProfileRepository.findByUserId(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student profile not found"));
        return mapToStudentResponse(user, sp);
    }

    @Transactional
    public UserDTO.StudentProfileResponse updateStudentProfile(String email, UserDTO.StudentProfileRequest req) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        StudentProfile sp = studentProfileRepository.findByUserId(user.getId())
                .orElseGet(() -> StudentProfile.builder().user(user).build());
        if (req.getRollNumber() != null) sp.setRollNumber(req.getRollNumber());
        if (req.getDepartment() != null) sp.setDepartment(req.getDepartment());
        if (req.getBatch() != null) sp.setBatch(req.getBatch());
        if (req.getCgpa() != null) sp.setCgpa(req.getCgpa());
        if (req.getSkills() != null) sp.setSkills(req.getSkills());
        if (req.getResumeUrl() != null) sp.setResumeUrl(req.getResumeUrl());
        if (req.getLinkedinUrl() != null) sp.setLinkedinUrl(req.getLinkedinUrl());
        if (req.getGithubUrl() != null) sp.setGithubUrl(req.getGithubUrl());
        if (req.getBacklogCount() != null) sp.setBacklogCount(req.getBacklogCount());
        return mapToStudentResponse(user, studentProfileRepository.save(sp));
    }

    // EMPLOYER profile
    public UserDTO.EmployerProfileResponse getEmployerProfile(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        EmployerProfile ep = employerProfileRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Employer profile not found"));
        return mapToEmployerResponse(user, ep);
    }

    @Transactional
    public UserDTO.EmployerProfileResponse updateEmployerProfile(String email, UserDTO.EmployerProfileRequest req) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        EmployerProfile ep = employerProfileRepository.findByUserId(user.getId())
                .orElseGet(() -> EmployerProfile.builder().user(user).build());
        if (req.getCompanyName() != null) ep.setCompanyName(req.getCompanyName());
        if (req.getIndustry() != null) ep.setIndustry(req.getIndustry());
        if (req.getWebsite() != null) ep.setWebsite(req.getWebsite());
        if (req.getCompanySize() != null) ep.setCompanySize(req.getCompanySize());
        if (req.getDescription() != null) ep.setDescription(req.getDescription());
        if (req.getLogoUrl() != null) ep.setLogoUrl(req.getLogoUrl());
        if (req.getAddress() != null) ep.setAddress(req.getAddress());
        if (req.getCity() != null) ep.setCity(req.getCity());
        if (req.getCountry() != null) ep.setCountry(req.getCountry());
        return mapToEmployerResponse(user, employerProfileRepository.save(ep));
    }

    // ADMIN / OFFICER: Get all students
    public List<UserDTO.StudentProfileResponse> getAllStudents() {
        return userRepository.findByRole(Role.ROLE_STUDENT).stream()
                .map(user -> {
                    StudentProfile sp = studentProfileRepository.findByUserId(user.getId()).orElse(null);
                    return mapToStudentResponse(user, sp);
                }).collect(Collectors.toList());
    }

    // ADMIN / OFFICER: Toggle student eligibility
    @Transactional
    public UserDTO.StudentProfileResponse toggleEligibility(Long studentId, boolean eligible) {
        StudentProfile sp = studentProfileRepository.findByUserId(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student profile not found"));
        sp.setEligible(eligible);
        User user = sp.getUser();
        return mapToStudentResponse(user, studentProfileRepository.save(sp));
    }

    // ADMIN: Enable/disable user
    @Transactional
    public UserDTO.UserResponse toggleUserStatus(Long userId, boolean enabled) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        user.setEnabled(enabled);
        return mapToUserResponse(userRepository.save(user));
    }

    public List<UserDTO.UserResponse> getAllUsers() {
        return userRepository.findAll().stream().map(this::mapToUserResponse).collect(Collectors.toList());
    }

    // --- Mappers ---
    private UserDTO.UserResponse mapToUserResponse(User u) {
        return UserDTO.UserResponse.builder()
                .id(u.getId()).fullName(u.getFullName()).email(u.getEmail())
                .role(u.getRole().name()).phone(u.getPhone()).profilePicture(u.getProfilePicture())
                .enabled(u.isEnabled()).emailVerified(u.isEmailVerified()).createdAt(u.getCreatedAt())
                .build();
    }

    private UserDTO.StudentProfileResponse mapToStudentResponse(User u, StudentProfile sp) {
        var b = UserDTO.StudentProfileResponse.builder()
                .userId(u.getId()).fullName(u.getFullName()).email(u.getEmail());
        if (sp != null) {
            b.id(sp.getId()).rollNumber(sp.getRollNumber()).department(sp.getDepartment())
             .batch(sp.getBatch()).cgpa(sp.getCgpa()).skills(sp.getSkills())
             .resumeUrl(sp.getResumeUrl()).linkedinUrl(sp.getLinkedinUrl()).githubUrl(sp.getGithubUrl())
             .placementStatus(sp.getPlacementStatus() != null ? sp.getPlacementStatus().name() : null)
             .placedCompany(sp.getPlacedCompany()).ctcOffered(sp.getCtcOffered())
             .placementDate(sp.getPlacementDate()).backlogCount(sp.getBacklogCount())
             .eligible(sp.isEligible());
        }
        return b.build();
    }

    private UserDTO.EmployerProfileResponse mapToEmployerResponse(User u, EmployerProfile ep) {
        return UserDTO.EmployerProfileResponse.builder()
                .id(ep.getId()).userId(u.getId()).fullName(u.getFullName()).email(u.getEmail())
                .companyName(ep.getCompanyName()).industry(ep.getIndustry()).website(ep.getWebsite())
                .companySize(ep.getCompanySize()).description(ep.getDescription()).logoUrl(ep.getLogoUrl())
                .address(ep.getAddress()).city(ep.getCity()).country(ep.getCountry()).verified(ep.isVerified())
                .build();
    }
}
