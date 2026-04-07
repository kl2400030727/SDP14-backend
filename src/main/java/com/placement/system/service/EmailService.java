package com.placement.system.service;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
@RequiredArgsConstructor
public class EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    private final JavaMailSender mailSender;

    @Value("${app.mail.from}")
    private String fromEmail;

    @Value("${app.mail.from-name}")
    private String fromName;

    @Async
    public void sendVerificationEmail(String toEmail, String toName, String token) {
        String subject = "Verify Your Email - Placement Management System";
        String verifyUrl = "http://localhost:3000/verify-email?token=" + token;
        String html = buildVerificationEmailHtml(toName, verifyUrl);
        sendHtmlEmail(toEmail, subject, html);
    }

    @Async
    public void sendPasswordResetEmail(String toEmail, String toName, String token) {
        String subject = "Reset Your Password - Placement Management System";
        String resetUrl = "http://localhost:3000/reset-password?token=" + token;
        String html = buildResetPasswordHtml(toName, resetUrl);
        sendHtmlEmail(toEmail, subject, html);
    }

    @Async
    public void sendApplicationStatusEmail(String toEmail, String studentName,
                                           String jobTitle, String companyName, String status) {
        String subject = "Application Update: " + jobTitle + " at " + companyName;
        String html = buildApplicationStatusHtml(studentName, jobTitle, companyName, status);
        sendHtmlEmail(toEmail, subject, html);
    }

    @Async
    public void sendJobApprovalEmail(String toEmail, String employerName, String jobTitle, boolean approved) {
        String subject = approved
                ? "Job Posting Approved: " + jobTitle
                : "Job Posting Requires Changes: " + jobTitle;
        String html = buildJobApprovalHtml(employerName, jobTitle, approved);
        sendHtmlEmail(toEmail, subject, html);
    }

    @Async
    public void sendNewJobNotificationEmail(String toEmail, String studentName,
                                            String jobTitle, String companyName) {
        String subject = "New Job Opportunity: " + jobTitle + " at " + companyName;
        String html = buildNewJobHtml(studentName, jobTitle, companyName);
        sendHtmlEmail(toEmail, subject, html);
    }

    @Async
    public void sendWelcomeEmail(String toEmail, String toName, String role) {
        String subject = "Welcome to Placement Management System!";
        String html = buildWelcomeHtml(toName, role);
        sendHtmlEmail(toEmail, subject, html);
    }

    private void sendHtmlEmail(String to, String subject, String htmlContent) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(fromEmail, fromName);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);
            mailSender.send(message);
            logger.info("Email sent successfully to: {}", to);
        } catch (MessagingException e) {
            logger.error("Failed to send email to {}: {}", to, e.getMessage());
        } catch (Exception e) {
            logger.error("Unexpected error sending email: {}", e.getMessage());
        }
    }

    // ==========================================
    // EMAIL HTML TEMPLATES
    // ==========================================

    private String buildVerificationEmailHtml(String name, String url) {
        return """
            <html><body style="font-family:Arial,sans-serif;max-width:600px;margin:0 auto;padding:20px">
            <div style="background:#1a73e8;padding:20px;border-radius:8px 8px 0 0;text-align:center">
              <h1 style="color:#fff;margin:0">Placement Management System</h1>
            </div>
            <div style="background:#f9f9f9;padding:30px;border-radius:0 0 8px 8px">
              <h2>Hello, %s!</h2>
              <p>Thank you for registering. Please verify your email address by clicking the button below:</p>
              <div style="text-align:center;margin:30px 0">
                <a href="%s" style="background:#1a73e8;color:#fff;padding:14px 28px;text-decoration:none;border-radius:5px;font-size:16px">
                  Verify Email Address
                </a>
              </div>
              <p style="color:#666;font-size:13px">This link expires in 24 hours. If you did not register, please ignore this email.</p>
            </div></body></html>
            """.formatted(name, url);
    }

    private String buildResetPasswordHtml(String name, String url) {
        return """
            <html><body style="font-family:Arial,sans-serif;max-width:600px;margin:0 auto;padding:20px">
            <div style="background:#e53935;padding:20px;border-radius:8px 8px 0 0;text-align:center">
              <h1 style="color:#fff;margin:0">Password Reset Request</h1>
            </div>
            <div style="background:#f9f9f9;padding:30px;border-radius:0 0 8px 8px">
              <h2>Hello, %s!</h2>
              <p>We received a request to reset your password. Click below to set a new password:</p>
              <div style="text-align:center;margin:30px 0">
                <a href="%s" style="background:#e53935;color:#fff;padding:14px 28px;text-decoration:none;border-radius:5px;font-size:16px">
                  Reset Password
                </a>
              </div>
              <p style="color:#666;font-size:13px">This link expires in 1 hour. If you did not request a reset, please ignore this email.</p>
            </div></body></html>
            """.formatted(name, url);
    }

    private String buildApplicationStatusHtml(String student, String job, String company, String status) {
        String color = status.equals("SELECTED") ? "#2e7d32" : status.equals("REJECTED") ? "#c62828" : "#1a73e8";
        return """
            <html><body style="font-family:Arial,sans-serif;max-width:600px;margin:0 auto;padding:20px">
            <div style="background:%s;padding:20px;border-radius:8px 8px 0 0;text-align:center">
              <h1 style="color:#fff;margin:0">Application Update</h1>
            </div>
            <div style="background:#f9f9f9;padding:30px;border-radius:0 0 8px 8px">
              <h2>Hello, %s!</h2>
              <p>Your application for <strong>%s</strong> at <strong>%s</strong> has been updated.</p>
              <div style="background:#fff;border-left:4px solid %s;padding:15px;margin:20px 0">
                <strong>Current Status:</strong> <span style="color:%s;font-size:18px">%s</span>
              </div>
              <p>Login to the placement portal for more details.</p>
            </div></body></html>
            """.formatted(color, student, job, company, color, color, status);
    }

    private String buildJobApprovalHtml(String employer, String job, boolean approved) {
        String color = approved ? "#2e7d32" : "#e65100";
        String msg = approved
                ? "Your job posting has been approved and is now live for students to apply."
                : "Your job posting requires some changes before it can go live. Please log in for details.";
        return """
            <html><body style="font-family:Arial,sans-serif;max-width:600px;margin:0 auto;padding:20px">
            <div style="background:%s;padding:20px;border-radius:8px 8px 0 0;text-align:center">
              <h1 style="color:#fff;margin:0">Job Posting %s</h1>
            </div>
            <div style="background:#f9f9f9;padding:30px;border-radius:0 0 8px 8px">
              <h2>Hello, %s!</h2>
              <p>Regarding your job posting: <strong>%s</strong></p>
              <p>%s</p>
            </div></body></html>
            """.formatted(color, approved ? "Approved" : "Update Required", employer, job, msg);
    }

    private String buildNewJobHtml(String student, String job, String company) {
        return """
            <html><body style="font-family:Arial,sans-serif;max-width:600px;margin:0 auto;padding:20px">
            <div style="background:#1a73e8;padding:20px;border-radius:8px 8px 0 0;text-align:center">
              <h1 style="color:#fff;margin:0">New Job Opportunity!</h1>
            </div>
            <div style="background:#f9f9f9;padding:30px;border-radius:0 0 8px 8px">
              <h2>Hello, %s!</h2>
              <p>A new job opportunity matching your profile is available:</p>
              <div style="background:#fff;border-radius:8px;padding:20px;margin:20px 0;border:1px solid #ddd">
                <h3 style="margin:0;color:#1a73e8">%s</h3>
                <p style="color:#555;margin:5px 0">%s</p>
              </div>
              <p>Log in to the placement portal to view details and apply!</p>
            </div></body></html>
            """.formatted(student, job, company);
    }

    private String buildWelcomeHtml(String name, String role) {
        return """
            <html><body style="font-family:Arial,sans-serif;max-width:600px;margin:0 auto;padding:20px">
            <div style="background:#1a73e8;padding:20px;border-radius:8px 8px 0 0;text-align:center">
              <h1 style="color:#fff;margin:0">Welcome!</h1>
            </div>
            <div style="background:#f9f9f9;padding:30px;border-radius:0 0 8px 8px">
              <h2>Hello, %s!</h2>
              <p>Welcome to the Placement Management System. Your account has been created with role: <strong>%s</strong>.</p>
              <p>Please verify your email to activate your account and start using the platform.</p>
            </div></body></html>
            """.formatted(name, role.replace("ROLE_", "").replace("_", " "));
    }
}
