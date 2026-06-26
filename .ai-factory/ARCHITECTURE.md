# Architecture: Structured Modules (Technical Layers)

## Overview
This project fits a Structured Modules architecture organized by technical layers. The existing Spring Boot codebase already uses separate controllers, services, repositories, models, DTOs, and security/config packages, which means the lowest-friction improvement is to keep that layout style while making feature boundaries more explicit.

For SpringDemo, this pattern balances delivery speed and maintainability. The domain is moderately complex: authentication, moderation, ratings, and mixed MVC/REST delivery are enough to require clearer boundaries, but not enough to justify a full explicit architecture split into domain/application/infrastructure layers yet.

## Decision Rationale
- **Project type:** Spring Boot monolith with REST APIs, MVC pages, security, and persistence.
- **Tech stack:** Java 17, Spring Boot, Spring Security, Spring Data JPA, MariaDB, Thymeleaf.
- **Key factor:** The codebase already follows layered Spring conventions and will benefit most from feature-level modularization without a disruptive rewrite.

## Folder Structure
```text
src/main/java/com/telerikacademy/com/springdemo/
├── auth/
│   ├── controllers/           # Auth endpoints and request parsing
│   ├── services/              # Login, logout, refresh-token orchestration
│   ├── repositories/          # Refresh-token persistence if split by module
│   ├── models/                # Auth-specific domain types
│   └── dto/                   # Auth request/response DTOs
├── beers/
│   ├── controllers/           # Beer MVC + REST controllers
│   ├── services/              # Beer use-case orchestration
│   ├── repositories/          # Beer persistence and querying
│   ├── models/                # Beer and style domain entities
│   └── dto/                   # Beer input/output DTOs
├── users/
│   ├── controllers/
│   ├── services/
│   ├── repositories/
│   ├── models/
│   └── dto/
├── comments/
│   ├── controllers/
│   ├── services/
│   ├── repositories/
│   ├── models/
│   └── dto/
├── ratings/
│   ├── controllers/
│   ├── services/
│   ├── repositories/
│   ├── models/
│   └── dto/
├── shared/
│   ├── config/                # Spring configuration and app bootstrap helpers
│   ├── security/              # JWT, user details, auth filters
│   ├── exceptions/            # Shared exception hierarchy and global handlers
│   └── mappers/               # Shared mapping components
└── TelerikAlphaSpringApplication.java
```

## Dependency Rules
Dependencies should flow downward inside each module:

- ✅ Controllers depend on services and DTOs.
- ✅ Services depend on repositories, models, and mappers.
- ✅ Repositories depend on models and Spring Data abstractions.
- ✅ Shared cross-cutting packages may be used by modules through stable public types.
- ❌ Controllers must not call repositories directly.
- ❌ Repositories must not depend on controllers or web concerns.
- ❌ Services should not depend on MVC view models or servlet-specific request/response types.
- ❌ One feature module should not reach into another module’s internals when a service/API boundary can be used instead.

## Layer/Module Communication
- REST and MVC controllers should remain thin: validate input, delegate to one service flow, then map the result to HTTP or view output.
- Services should orchestrate use cases and transaction boundaries, not accumulate ad-hoc web logic.
- Domain validation that belongs to an entity should move toward models instead of being repeated across controllers.
- Shared security and exception handling should stay centralized in `shared/security` and `shared/exceptions`.

## Key Principles
1. Keep feature code together conceptually, even if the physical migration to feature folders happens incrementally.
2. Use services as orchestration layers; keep HTTP concerns in controllers and persistence concerns in repositories.
3. Prefer one primary logging style and one primary exception-mapping path to reduce operational ambiguity.

## Code Examples

### Thin REST Controller
```java
@RestController
@RequestMapping("/api/beers")
public class BeerController {

    private final BeerService beerService;
    private final BeerMapper beerMapper;

    public BeerController(BeerService beerService, BeerMapper beerMapper) {
        this.beerService = beerService;
        this.beerMapper = beerMapper;
    }

    @GetMapping("/{id}")
    public ResponseEntity<BeerResponseDTO> getById(@PathVariable long id) {
        Beer beer = beerService.getById(id);
        return ResponseEntity.ok(beerMapper.toResponse(beer));
    }
}
```

### Service-to-Repository Dependency Direction
```java
@Service
public class BeerServiceImpl implements BeerService {

    private final BeerRepository beerRepository;

    public BeerServiceImpl(BeerRepository beerRepository) {
        this.beerRepository = beerRepository;
    }

    @Override
    public Beer getById(long id) {
        return beerRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Beer", id));
    }
}
```

## Anti-Patterns
- ❌ Business logic embedded in controllers because it was convenient during endpoint development.
- ❌ Mixing `java.util.logging`, SLF4J, and static logger imports within the same feature flow.
- ❌ Returning persistence-layer details directly from controllers without DTO mapping.
- ❌ Treating `shared` as a dumping ground for unrelated feature logic.
