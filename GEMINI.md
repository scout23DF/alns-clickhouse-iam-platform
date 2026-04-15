# GEMINI.md - ALNS ClickHouse IAM Platform

## Project Overview
`alns-clickhouse-iam-platform` is a monolithic Spring Boot 4 application that demonstrates a comprehensive Identity and Access Management (IAM) system. It combines traditional authentication/authorization with modern fine-grained relationship-based access control (ReBAC).

### Core Technologies
- **Runtime:** Java 21
- **Framework:** Spring Boot 4.0.4, Spring Security 6
- **Authorization Engine:** [OpenFGA](https://openfga.dev/) (using `openfga-spring-boot-starter`)
- **Persistence:** PostgreSQL
- **UI/Views:** Thymeleaf MVC + REST APIs
- **Documentation:** SpringDoc (OpenAPI/Swagger UI)
- **Infrastructure:** Docker Compose (PostgreSQL, OpenFGA, Mailhog, pgAdmin, Keycloak)

### Architecture Highlights
The project follows a domain-driven structure under `com.clickhouse.alnscodingexercise.domains`:
- `iamplatform/account`: User account lifecycle (registration, verification, password management).
- `iamplatform/authn`: Authentication logic, custom login handlers.
- `iamplatform/authz`: Fine-grained authorization using OpenFGA adapters and aspects.
- `assetmgmt`: Management of protected resources (`ResourceThing`).
- `eventlisteners`: Decoupled workflow handling using Spring Application Events.

## Building and Running

### Prerequisites
- Java 21 SDK
- Docker and Docker Compose
- Maven (or use the provided `./mvnw` wrapper)

### Key Commands
- **Build the application:**
  ```bash
  ./mvnw clean package -DskipTests
  ```
- **Run locally (recommended):**
  ```bash
  ./mvnw spring-boot:run -DskipTests
  ```
  *Note: This command automatically starts the required infrastructure (PostgreSQL, OpenFGA, etc.) using Spring Boot's Docker Compose integration.*
- **Run in Docker:**
  ```bash
  docker-compose up -d --build
  ```
- **Testing:**
  - **Unit tests:** `./mvnw test`
  - **Integration tests:** `./mvnw verify -Pintegration`
  *Note: As of the current state, project-local tests need to be implemented.*

### Useful URLs
- **Main Application:** `http://localhost:8090`
- **Swagger UI:** `http://localhost:8090/swagger-ui.html`
- **Mailhog (Email Testing):** `http://localhost:1080`
- **OpenFGA Playground:** `http://localhost:3000`
- **pgAdmin:** `http://localhost:16543`

## Development Conventions

### Coding Style & Patterns
- **Domain-Centric:** Logic is grouped by functional domains.
- **Service Layer:** Interfaces are used for service definitions (e.g., `IUserAccountMgmtService`).
- **Events:** Cross-domain side effects are handled via event listeners (e.g., `OnRegistrationSubmittedEvent`).
- **Lombok:** Used extensively to reduce boilerplate.

### Security & Authorization
- **Authentication:** Standard Spring Security form-based login.
- **Coarse-Grained Auth:** Roles and Privileges are checked via URL-level security and `@PreAuthorize`.
- **Fine-Grained Auth (OpenFGA):** Relationship-based checks are performed using:
  - Custom annotations: `@FgaCheck`, `@PreOpenFgaCheck`, `@PostOpenFgaCheck`.
  - Service-level calls: `IAuthorizationService`.
- **Naming:** Follows standard Spring/Java camelCase for fields and PascalCase for classes. Database columns use `snake_case`.

### Configuration
- **Profiles:** `default` (local development) and `local-dockerized` (running inside containers).
- **Environment Variables:** Most infrastructure settings are externalized and can be overridden via `.env` or system properties.
- **OpenFGA Bootstrap:** Controlled by `openfga.*` properties in `application.yml`. Startup can automatically import models and tuples if configured.

## Key Files
- `src/main/resources/application.yml`: Primary configuration.
- `src/main/resources/openfga/`: OpenFGA model schemas and initial data.
- `docs/ARCHITECTURE.md`: Detailed architectural design and flows.
- `docker-compose-infra.yml`: Infrastructure services definition.
- `SecurityConfig.java`: Central security policy definition.
