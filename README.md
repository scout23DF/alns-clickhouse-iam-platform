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

### Build

This project relies on Maven as its build tool. You can build a runnable .jar package, by issuing:

```bash
./mvnw clean package -DskipTests
```
Find the built package at: `./target/alns-clickhouse-iam-platform.jar`

### How to Run locally:

This app also relies on Docker-Compose to make available locally some needed services (Take a look at the `docker-compose-infra.yml` file).

Follow the instructions below, depending on how you wish to run this app:

#### 1) Run the app from source (from Terminal CLI)

By default, this App takes advantage of Spring-Boot and Docker-Compose integration. 
This means that when the App starts using its default profile, automatically the services described at `docker-compose-infra.yml` will start before, initializing the App itself.
Therefore, only type:

```bash
./mvnw spring-boot:run -DskipTests
```
And verify If the following infra services are started successfully: 
- PostgreSQL;
- OpenFGA;
- Mailhog;
- pgAdmin.

#### 2) Run the App in a totally dockerized way

If you want to run this Ap in a dockerized way, its docker image will be automatically build when you issue the following:

- Build the App:
```bash
./mvnw clean package -DskipTests
```

And then:

```bash
./docker-compose -f docker-compose.yml up -d --build
```

### Testing

Unit tests only:

```bash
./mvnw test
```
Integration profile wiring exists in `pom.xml` (`integration` profile), but this module currently has no project-local test classes under `src/test/java`.

[TBD]: The tests must be implemented. 

For the sake of manual testing and simulations, consider the following:

- When the PostgreSQL container starts, some SQL scripts run to create the database objects, as well as, some sample data are loaded;
- After a fresh build and starting, the App has the following data:
  - 07 Users Accounts: `chapolin`, `spiderman`, `ironman`, `black-widow`, `batman`, `superman`, `wonder-woman`
  - Same Password for all: `Demo@159`
  - Each User Account is assigned to at least one OpenFGA relation: `owner`, `editor` and/or `viewer`.  
  - 13 Resources, which were associated to the Users Accounts above, by OpenFGA relations.
- You can use the Swagger-UI Endpoints. There are some JSON files at `src/test/resources` which can be used to Create/Update some `Resources` and their Permissions in OpenFGA way.

### 3) Useful URLs

- Application: `http://localhost:8090`
- Swagger UI: `http://localhost:8090/swagger-ui.html`
- OpenAPI JSON: `http://localhost:8090/v3/api-docs`
- Actuator health: `http://localhost:8090/management/health`
- Mailhog UI: `http://localhost:1080`
- OpenFGA playground: `http://localhost:3000`
- pgAdmin: `http://localhost:16543`

## Known Gaps and Notes

- All the Tests must be implemented.
- Some code paths are explicitly incomplete (`TODO` / not implemented), including parts of resource update/delete + permission revocation flow.
- Compose app healthcheck currently points to `/actuator/health`, while app config exposes actuator on `/management`; validate this in your environment before relying on container health status.
- Default credentials and sample values in config/compose are development-oriented and should be externalized for production usage.
 
