# Architecture - ALNS ClickHouse IAM Platform

## 1. System Purpose

`alns-clickhouse-iam-platform` is a monolithic Spring Boot service that combines:

- User account lifecycle (registration, verification, password reset/update)
- Authentication (Spring Security form login)
- Authorization:
  - coarse-grained role/privilege checks
  - fine-grained relationship-based checks via OpenFGA
- Protected resource management (`ResourceThing`) with ACL-aware responses

## 2. Runtime Components

Application entry point:

- `com.clickhouse.alnscodingexercise.AlnsClickHouseIAMPlatformApplication`

Key wiring:

- `wiring/config/SecurityConfig.java`
- `wiring/config/OpenFgaAutoConfig.java`
- `wiring/config/MvcConfig.java`
- `wiring/config/SetupDataLoader.java`

External runtime dependencies (local infra setup):

- PostgreSQL
- OpenFGA server
- Mailhog (SMTP sink)
- pgAdmin (optional)

Compose definitions live in `docker-compose*.yml` and `src/main/docker/*.yaml`.

## 3. Logical Domain Modules

Top package: `com.clickhouse.alnscodingexercise.domains`

- `iamplatform/account`: entities, repositories, account services/controllers
- `iamplatform/authn`: custom auth handlers, user-details service, login-attempt control
- `iamplatform/authz`: FGA model DTOs, service abstractions, adapter, annotation/aspect checks
- `assetmgmt`: protected resource model (`ResourceThing`) + CRUD/search services/controllers
- `dashboard`: MVC pages for authenticated users/admin
- `notification`: email notification service
- `shared`: constants, exceptions, generic web DTOs/utilities

Cross-domain events are in `com.clickhouse.alnscodingexercise.eventlisteners`.

## 4. Data Model (JPA)

Primary entities:

- `CHUserAccount`
  - identity fields: username, email
  - auth fields: password, enabled
  - relationships:
    - many-to-many with `Role`
    - one-to-many with created `ResourceThing`
- `Role`
  - many-to-many with `Privilege`
- `Privilege`
- Token entities:
  - `VerificationToken`
  - `PasswordResetToken`
- Protected asset:
  - `ResourceThing`

Database is PostgreSQL in default and dockerized local profiles.

## 5. Authentication Flow

### Registration

1. MVC or REST endpoint receives registration request.
2. `UserAccountMgmtServiceImpl.registerNewUserAccount(...)` persists a disabled user with `ROLE_USER`.
3. Service publishes `OnRegistrationSubmittedEvent`.
4. `RegistrationSubmittedListener` creates verification token and sends email.
5. On confirmation, user is enabled and `OnRegistrationCompletedEvent` is published.
6. `RegistrationCompletedListener` seeds default OpenFGA permission on a default document resource.

### Login

1. Spring Security form login submits credentials.
2. `CustomUserDetailsPasswordFromDBService` loads user by username/email and maps authorities from roles + privileges.
3. On success, `CustomLoginAuthenticationSuccessHandler` redirects to dashboard/console by authority.
4. On failure, `CustomAuthenticationFailureHandler` resolves localized error and updates session auth error.

## 6. Authorization Flow (Role + ReBAC)

### Coarse-grained checks

- URL access is configured in `SecurityConfig` using `hasRole` / `hasAuthority`.

### Fine-grained OpenFGA checks

- Main contract: `IAuthorizationService`
- Main adapter to OpenFGA SDK: `OpenFGAAdapter`
- Utility layer: `AuthorizationUtils`

Used in three main ways:

1. Explicit service calls (check, list grants, add/remove tuples)
2. Spring method security expressions (`@PreAuthorize`)
3. Custom annotations + aspect (`@FgaCheck`, `FgaAspect`)

## 7. ACL Synchronization for Resources

When resource operations include ACL intent:

1. Resource service writes resource data.
2. Events and/or utility methods build permission tuples.
3. OpenFGA tuples are written/removed in batch.
4. Response can be enriched with computed allowed actions (`can_read`, `can_write`, etc.).

Main classes:

- `ResourceThingMgmtServiceImpl`
- `AuthorizationUtils`
- `ResourceThingCreatedOrUpdatedListener`

## 8. OpenFGA Bootstrap Model

Configuration prefix: `openfga.*`.

Bootstrap behavior:

- If store/model IDs are provided, startup validates them.
- If not, and `fgaShouldImportInitialStructure=true`, startup can create store/model and import tuple files.

Bootstrap helper:

- `LoadInitialFgaDataHelper`

Model files:

- `src/main/resources/openfga/initial-model-tuples-data/domain-assetmgmt-resourcething-model-schema.json`
- `src/main/resources/openfga/initial-model-tuples-data/domain-assetmgmt-resourcething-tuples-example-data.json`

## 9. Configuration and Endpoints

From `application.yml`:

- Server port: `8090`
- Actuator base path: `/management`
- OpenAPI docs: `/v3/api-docs`
- Swagger UI: `/swagger-ui.html`

Key route prefixes (`AppConstants`):

- `/api/iam/account`
- `/api/iam/authz`
- `/api/assets-mgmt`
- `pages/iam/*` and `pages/dashboard/*` for MVC pages

## 10. Current Constraints and Risks

Implementation-level concerns to track:

- Some code paths are TODO/unimplemented (notably parts of resource update/delete + permission revocation).
- Compose app healthcheck path uses `/actuator/health` while runtime actuator base path is `/management`.
- Several development credentials and defaults are embedded in local configs.
- The module currently has no project-local tests under `src/test/java`.

## 11. Extension Points

Where to extend safely:

- `IUserAccountMgmtService` for account logic variations
- `IAuthorizationService` for authorization policy orchestration
- Event listeners in `eventlisteners/listeners` for lifecycle side effects
- `SecurityConfig` for URL/method security policy changes
- OpenFGA model files for relation semantics evolution

