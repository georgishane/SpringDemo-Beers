# Project Description: SpringDemo

## Overview
SpringDemo is a Java 17 Spring Boot web application for managing beers, ratings, comments, moderation, and user accounts. The codebase combines server-rendered MVC pages with REST endpoints, JWT-based authentication, and a MariaDB-backed persistence layer.

## Core Features
- Beer catalog browsing, filtering, and detail retrieval.
- User registration, login, logout, refresh-token flow, and role-based authorization.
- Ratings, comments, and comment reporting/moderation workflows.
- Mixed presentation layer with Thymeleaf MVC pages and REST APIs documented via OpenAPI.

## Tech Stack
- **Programming language:** Java 17
- **Framework:** Spring Boot 3.1.4
- **Web:** Spring MVC, Thymeleaf, Spring Security
- **Persistence:** Spring Data JPA, MariaDB
- **API/Auth:** JWT, springdoc-openapi
- **Testing:** JUnit 5, Spring Boot Test, Spring Security Test
- **Build:** Gradle

## Architecture Notes
The current project is organized around technical layers: controllers, services, repositories, models, security, configuration, DTOs, and mappers. This layout is already beyond a trivial CRUD structure, but it does not yet enforce explicit domain/application/infrastructure boundaries.

The recommended direction is **Structured Modules (Technical Layers)**: keep the current Spring-friendly layering, but tighten module boundaries around feature areas such as beers, users, comments, ratings, and auth. This preserves delivery speed while providing a cleaner path toward stronger domain boundaries if complexity grows.

## Non-Functional Requirements
- **Security:** JWT authentication, refresh-token handling, role-based access control, secure exception handling.
- **Error handling:** Centralized exception mapping through `GlobalExceptionHandler`, with some controllers still carrying local `try/catch` handling.
- **Observability:** Logging is enabled through Spring logging configuration, SLF4J, and some legacy `java.util.logging` usage.
- **Persistence:** MariaDB datasource configuration with JPA/Hibernate integration.
- **API documentation:** OpenAPI/Swagger UI via `springdoc-openapi`.

## Architecture
See `.ai-factory/ARCHITECTURE.md` for detailed architecture guidelines.
Pattern: Structured Modules (Technical Layers)
