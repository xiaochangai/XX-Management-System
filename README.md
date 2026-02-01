# Enterprise Management System

A modern, full-stack enterprise management system built with Spring Boot 3 and Vue 3 + TypeScript.

## Overview

This is a comprehensive admin dashboard system featuring:

- **Enterprise-grade security**: Multi-factor authentication, password encryption, login attempt limiting, session timeout
- **Modular architecture**: Clean code structure with layered architecture
- **Responsive design**: Modern UI with responsive layout
- **Mobile support**: Cross-platform mobile application (H5, iOS, Android, Mini Program)
- **Data security**: Data masking, audit logs, operation tracking

## Tech Stack

### Backend
- Java 17
- Spring Boot 3.x
- Spring Security & Sa-Token (authentication)
- MyBatis Plus
- MySQL / PostgreSQL
- Maven

### Frontend (Web)
- TypeScript
- Vue 3
- Vite 5
- Pinia (state management)
- Ant Design Vue 4.x

### Mobile
- Uni-App (Vue 3)
- Uni-UI
- Supports: H5, iOS, Android, WeChat Mini Program

## Project Structure

```
.
├── smart-admin-api-java17-springboot3/    # Backend API
│   ├── sa-admin/                          # Admin module
│   └── sa-base/                           # Base/common module
├── smart-admin-web-typescript/            # Web frontend
└── smart-app/                             # Mobile application
```

## Quick Start (Local Development)

### Prerequisites
- JDK 17+
- Node.js 18+
- MySQL 8.0+
- Maven 3.8+

### 1. Database Setup

Create database and import initial schema:

```sql
CREATE DATABASE smart_admin DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

### 2. Backend Setup

```bash
cd smart-admin-api-java17-springboot3

# Update database configuration in:
# sa-admin/src/main/resources/dev/application.yaml

# Build and run
mvn clean package
java -jar sa-admin/target/sa-admin-*.jar --spring.profiles.active=dev
```

Backend will be available at: http://localhost:8080

### 3. Frontend Setup

```bash
cd smart-admin-web-typescript

npm install
npm run dev
```

Frontend will be available at: http://localhost:8001

### 4. Mobile App Setup

```bash
cd smart-app

npm install
# For H5 preview
npm run dev:h5
```

## Docker Deployment

For production deployment using Docker, see [docker/README.md](docker/README.md).

```bash
# Quick start with Docker Compose
cd docker
docker-compose up -d
```

## Default Credentials

| Username | Password | Role |
|----------|----------|------|
| admin | 123456 | Super Administrator |

## Key Features

- **User Management**: Employee management, department structure, role-based access control
- **System Configuration**: Menu management, dictionary, system parameters
- **Audit Logging**: Login logs, operation logs, change tracking
- **File Management**: File upload, storage management
- **Code Generation**: Table-based code generation with templates
- **Notification System**: In-app notifications, announcements

## Screenshots

The system includes:
- Modern dashboard with data visualization
- User-friendly forms with validation
- Responsive table with pagination and filtering
- Dark/Light theme support
- Mobile-optimized interface

## Development Guidelines

- Follow RESTful API design principles
- Use consistent naming conventions
- Write unit tests for business logic
- Document API endpoints
- Code review before merging

## Security Features

- Password encryption (BCrypt)
- JWT token with refresh mechanism
- Login attempt limiting (lock after failures)
- Session timeout handling
- CORS configuration
- Input validation and sanitization
- SQL injection prevention (MyBatis Plus parameter binding)
- XSS protection

## License

MIT License - Free for personal and commercial use.

## Support

For technical questions or issues, please use the project's issue tracker.

---

Built with modern technologies for enterprise use.
