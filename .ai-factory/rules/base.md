# Project Base Rules

> Auto-detected conventions from codebase analysis. Edit as needed.

## Naming Conventions

- Files: PascalCase for Java classes, interfaces, DTOs, and configuration types.
- Variables: camelCase for fields, parameters, and local variables.
- Functions: camelCase methods following standard Java/Spring naming.
- Classes: PascalCase, with Spring stereotypes reflected in suffixes such as `Controller`, `Service`, `Repository`, `Config`, `Mapper`, and `DTO`/`Dto`.
- Packages: lowercase hierarchical packages under `com.telerikacademy.com.springdemo`.

## Module Structure

- Main source code lives under `src/main/java/com/telerikacademy/com/springdemo`.
- Technical layers are split into `controllers`, `services`, `repositories`, `models`, `security`, `config`, `exceptions`, `dto`, and `mappers`.
- Controllers are further split by delivery style: `controllers.mvc`, `controllers.rest`, and `controllers.debug`.
- Tests live under `src/test/java/com/telerikacademy/com/springdemo`, mostly following production package names.

## Error Handling

- Prefer domain-specific runtime exceptions such as `EntityNotFoundException`, `DuplicateEntityException`, and `UnauthorizedOperationException`.
- Favor centralized HTTP error translation in `GlobalExceptionHandler` instead of duplicating response mapping in controllers.
- Local `try/catch` blocks exist in controllers and services; new code should use them only when handling is truly local and cannot be centralized.

## Logging

- Default application logging is configured through Spring properties.
- Prefer a single logging style per component; `slf4j` is already present and should be the default for new code.
- Avoid mixing `java.util.logging`, static logger imports, and ad-hoc controller logging in new code.

## Testing

- Use JUnit 5 with Spring Boot Test support.
- Controller-level tests use `MockMvc` and Spring Security test helpers.
- Keep test package structure aligned with production code and name tests after the unit or controller under test.
