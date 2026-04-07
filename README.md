# Placement Management System - FSAD PS14

## Overview
A full-stack placement interaction system with 4 roles:
- **ADMIN** - Full system control
- **STUDENT** - Apply for jobs, track applications
- **EMPLOYER** - Post jobs, review candidates
- **PLACEMENT_OFFICER** - Track placements, generate reports

## Tech Stack
- **Backend**: Spring Boot 3.2, Spring Security (JWT), Spring Data JPA, Spring Mail, Spring AOP
- **Database**: MySQL 8.x
- **Build**: Maven
- **Docs**: Swagger UI (SpringDoc OpenAPI)

## How to Import in Eclipse IDE

1. Open Eclipse IDE (with Spring Tools Suite plugin)
2. **File → Import → Maven → Existing Maven Projects**
3. Browse to the extracted project folder
4. Select `pom.xml` → Click **Finish**
5. Eclipse will auto-download all Maven dependencies

## Database Setup

```sql
CREATE DATABASE placement_db;
```
Update `src/main/resources/application.properties` with your MySQL credentials.

## Email Configuration

In `application.properties`, set:
```
spring.mail.username=your-gmail@gmail.com
spring.mail.password=your-16-char-app-password
app.mail.from=your-gmail@gmail.com
```
> Generate an App Password from Google Account → Security → 2FA → App Passwords

## Running the App

```bash
mvn spring-boot:run
```

## API URLs
| URL | Description |
|-----|-------------|
| `http://localhost:8080/api/swagger-ui.html` | Swagger UI |
| `http://localhost:8080/api/auth/register` | Register |
| `http://localhost:8080/api/auth/login` | Login |
| `http://localhost:8080/api/jobs` | Job listings |
| `http://localhost:8080/api/applications` | Applications |
| `http://localhost:8080/api/officer/dashboard` | Officer dashboard |
| `http://localhost:8080/api/admin/users` | Admin user management |

## Default Admin Credentials
- **Email**: admin@placement.com  
- **Password**: Admin@123

## Role-Based API Endpoints Summary

### AUTH (Public)
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | /auth/register | Register (STUDENT/EMPLOYER/OFFICER) |
| POST | /auth/login | Login, get JWT token |
| GET  | /auth/verify-email?token= | Verify email |
| POST | /auth/forgot-password | Trigger password reset |
| POST | /auth/reset-password | Reset with token |
| POST | /auth/change-password | Change password (auth) |

### STUDENT
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | /jobs | View all active jobs |
| POST | /applications | Apply for a job |
| GET | /applications/my-applications | My applications |
| PATCH | /applications/{id}/withdraw | Withdraw application |
| GET | /profile/student | View student profile |
| PUT | /profile/student | Update student profile |

### EMPLOYER
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | /jobs | Create job posting |
| PUT | /jobs/{id} | Update job posting |
| GET | /jobs/my-postings | View own postings |
| GET | /applications/job/{jobId} | View applicants |
| PATCH | /applications/{id}/status | Update applicant status |

### PLACEMENT OFFICER
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | /officer/dashboard | Placement stats |
| GET | /officer/placement-records | All placement records |
| GET | /officer/reports/batch-wise | Batch-wise report |
| GET | /officer/reports/department-wise | Dept-wise report |
| PATCH | /officer/students/{id}/eligibility | Set eligibility |
| PATCH | /jobs/{id}/approve | Approve/reject jobs |

### ADMIN
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | /admin/users | All users |
| GET | /admin/students | All students |
| PATCH | /admin/users/{id}/status | Enable/disable user |
| PATCH | /admin/students/{id}/eligibility | Set eligibility |
| GET | /jobs/all | All job postings |
| PATCH | /jobs/{id}/approve | Approve/reject jobs |
