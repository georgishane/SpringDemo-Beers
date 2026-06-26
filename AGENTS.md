# AGENTS.md

> Keep this file factual and maintain it as the project structure changes. Use it as the first orientation point for AI agents working in this repository.

## Project Overview
SpringDemo is a Java 17 Spring Boot application for beer catalog management, authentication, user management, ratings, comments, and moderation. It exposes both server-rendered MVC pages and REST APIs backed by MariaDB.

## Tech Stack
- **Programming language:** Java 17
- **Framework:** Spring Boot 3.1.4
- **Database:** MariaDB
- **ORM:** Spring Data JPA / Hibernate

## Project Structure
```text
.
├── src/
│   ├── main/
│   │   ├── java/com/telerikacademy/com/springdemo/
│   │   │   ├── TelerikAlphaSpringApplication.java   # Spring Boot entry point
│   │   │   ├── config/                              # Spring, security, OpenAPI, and web configuration
│   │   │   ├── controllers/                         # MVC, REST, auth, and debug controllers
│   │   │   ├── dto/                                 # Request/response transport models
│   │   │   ├── exceptions/                          # Domain/application exceptions and global handlers
│   │   │   ├── mappers/                             # DTO/entity mapping helpers
│   │   │   ├── models/                              # JPA entities and related domain models
│   │   │   ├── repositories/                        # Spring Data repositories and remaining repo abstractions
│   │   │   ├── security/                            # JWT filter, JWT utilities, user details
│   │   │   └── services/                            # Business/application services
│   │   ├── resources/
│   │   │   ├── application.properties               # Runtime configuration
│   │   │   ├── static/                              # Static assets
│   │   │   ├── templates/                           # Thymeleaf templates
│   │   │   └── data/                                # Local database/data artifacts
│   │   └── web/WEB-INF/                             # Web resources
│   └── test/
│       ├── java/com/telerikacademy/com/springdemo/  # JUnit and Spring tests
│       └── resources/                               # Test configuration
├── .ai-factory/                                     # AI Factory project context
├── .codex/                                          # Codex local skills and config
├── build.gradle                                     # Gradle build definition
├── settings.gradle                                  # Gradle project name
├── HELP.md                                          # Spring Boot starter/help file
├── ROADMAP.md                                       # Project roadmap notes
└── TEST_INSTRUCTIONS.md                             # Manual or guided test notes
```

## Key Entry Points
| File | Purpose |
|------|---------|
| `src/main/java/com/telerikacademy/com/springdemo/TelerikAlphaSpringApplication.java` | Bootstraps the Spring Boot application and enables JPA plus method security. |
| `build.gradle` | Defines the Java, Spring Boot, JPA, security, MariaDB, JWT, and test dependencies. |
| `src/main/resources/application.properties` | Stores datasource, logging, JPA, and token-expiration configuration. |
| `src/main/java/com/telerikacademy/com/springdemo/config/SecurityConfig.java` | Central Spring Security configuration for authentication/authorization. |

## Documentation
| Document | Path | Description |
|----------|------|-------------|
| HELP | `HELP.md` | Default Spring Boot helper notes for the project. |
| Roadmap | `ROADMAP.md` | High-level planning and milestone notes. |
| Test Instructions | `TEST_INSTRUCTIONS.md` | Notes for executing or validating tests manually. |

## AI Context Files
| File | Purpose |
|------|---------|
| `AGENTS.md` | Repository map for agents and contributors. |
| `.ai-factory/DESCRIPTION.md` | Project summary, stack, and setup context for AI Factory workflows. |
| `.ai-factory/ARCHITECTURE.md` | Architecture guidance and dependency rules for future changes. |
| `.ai-factory/rules/base.md` | Baseline codebase conventions detected during AIF setup. |

## Agent Rules
- Decompose compound shell work into small, auditable commands instead of chaining unrelated operations.
- Incorrect combined example: `git checkout master && git pull`
- Correct decomposed example: first `git checkout master`, then `git pull origin master`
