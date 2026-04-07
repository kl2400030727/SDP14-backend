package com.placement.system.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    private String title;

    @Column(columnDefinition = "TEXT")
    private String message;

    @Enumerated(EnumType.STRING)
    private NotificationType type;

    // FIX: "read" is a reserved MySQL keyword — rename column to "is_read"
    @Column(name = "is_read", nullable = false)
    private boolean read = false;

    private String actionUrl;

    @CreationTimestamp
    private LocalDateTime createdAt;

    public enum NotificationType {
        APPLICATION_UPDATE, JOB_POSTED, DRIVE_REMINDER,
        OFFER_LETTER, SYSTEM, ADMIN_MESSAGE
    }
}