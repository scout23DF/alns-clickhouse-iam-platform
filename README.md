# ALNS ClickHouse IAM Platform

Spring Boot application that demonstrates user account management, authentication, and fine-grained authorization using OpenFGA.

## Project Status

This README reflects the current implementation in this repository.

- Language and runtime: Java 21
- Framework: Spring Boot 4.0.4
- Build: Maven Wrapper (`./mvnw`)
- Persistence: PostgreSQL (JPA/Hibernate)
- Authorization engine: OpenFGA
- Views: Thymeleaf MVC + REST APIs

For deeper architecture details, see `docs/ARCHITECTURE.md`.

## Main Capabilities

- User registration with email verification token flow
- Form-based login with Spring Security
- Password reset and password update workflows
- Role/privilege model (`ROLE_ADMIN`, `ROLE_USER`, `READ_PRIVILEGE`, `WRITE_PRIVILEGE`, `CHANGE_PASSWORD_PRIVILEGE`)
- Fine-grained, relationship-based permissions for protected resources (`document` type in OpenFGA)
- Dashboard pages that render resources enriched with ACL/allowed-actions
- OpenAPI docs and Actuator endpoints

## Package Overview

Source root: `src/main/java/com/clickhouse/alnscodingexercise`

- `domains/iamplatform/account`: users, roles, privileges, tokens, account APIs/pages
- `domains/iamplatform/authn`: user details service, login handlers, login-attempt guard
- `domains/iamplatform/authz`: OpenFGA adapter/service, ACL DTOs, authorization annotations/aspects
- `domains/assetmgmt`: protected resource (`ResourceThing`) model, service, REST API
- `domains/dashboard`: MVC dashboard pages
- `eventlisteners`: registration/resource lifecycle listeners
- `wiring/config`: Spring MVC/Security/OpenFGA/data bootstrap configuration

## API Surface (high level)

Prefixes from `AppConstants`:

- Account: `/api/iam/account`
- Authorization helper: `/api/iam/authz`
- Asset management: `/api/assets-mgmt`

Examples:

- `POST /api/iam/account/registration`
- `POST /api/iam/account/resetPassword`
- `POST /api/iam/account/savePassword`
- `POST /api/iam/account/updatePassword`
- `GET /api/iam/account/resendRegistrationToken`
- `POST /api/assets-mgmt/resources-things`
- `GET /api/assets-mgmt/resources-things/{resourceId}`

Swagger UI path (configured): `/swagger-ui.html`

## Security and Authorization Model

- URL-level security is configured in `SecurityConfig`.
- Authentication uses `UserDetailsService` backed by `UserRepository`.
- Authorities are assembled from both roles and privileges.
- OpenFGA checks are exposed through:
  - explicit service calls (`IAuthorizationService`)
  - Spring expression checks in `@PreAuthorize`
  - custom annotations/aspects (`@FgaCheck`, `@PreOpenFgaCheck`, `@PostOpenFgaCheck`)

## OpenFGA Model

Model files are under `src/main/resources/openfga/initial-model-tuples-data`.

Current schema includes:

- Types: `user`, `group`, `document`
- Direct relations on `document`: `owner`, `editor`, `viewer`
- Computed permissions: `can_read`, `can_write`, `can_share`, `can_delete`, `can_change_owner`

Startup initialization behavior is controlled by `openfga.*` properties.

## Configuration and Profiles

Default config: `src/main/resources/application.yml`

- Server port: `8090`
- Actuator base path: `/management`
- Swagger path: `/swagger-ui.html`
- Docker Compose integration enabled (`spring.docker.compose.enabled=true`)

Dockerized local profile overrides: `application-local-dockerized.yml`

- Profile id: `local-dockerized`
- Uses container hostnames for PostgreSQL, Mailhog, and OpenFGA

## Local Development

### 1) Start infra (PostgreSQL, OpenFGA, Mailhog, pgAdmin)

```bash
docker compose -f docker-compose-infra.yml up -d
```

### 2) Run the app from source

```bash
./mvnw spring-boot:run
```

Or with dockerized profile:

```bash
./mvnw spring-boot:run -Plocal-dockerized
```

### 3) Useful URLs

- Application: `http://localhost:8090`
- Swagger UI: `http://localhost:8090/swagger-ui.html`
- OpenAPI JSON: `http://localhost:8090/v3/api-docs`
- Actuator health: `http://localhost:8090/management/health`
- Mailhog UI: `http://localhost:1080`
- OpenFGA playground: `http://localhost:3000`
- pgAdmin: `http://localhost:16543`

## Build and Test

Build:

```bash
./mvnw clean verify
```

Unit tests only:

```bash
./mvnw test
```

Integration profile wiring exists in `pom.xml` (`integration` profile), but this module currently has no project-local test classes under `src/test/java`.

## Known Gaps and Notes

- `README` is now aligned to current code; legacy references were removed.
- Some code paths are explicitly incomplete (`TODO` / not implemented), including parts of resource update/delete + permission revocation flow.
- Compose app healthcheck currently points to `/actuator/health`, while app config exposes actuator on `/management`; validate this in your environment before relying on container health status.
- Default credentials and sample values in config/compose are development-oriented and should be externalized for production usage.
