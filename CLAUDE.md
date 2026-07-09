# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

ClinicHub is a medical clinic management system backend built with Spring Boot. It lets administrators register patients, doctors, and specialties; patients schedule/cancel appointments; doctors record medical records and request exams. See `docs/1. Requirements.md`, `docs/3. BusinessRules.md`, and `docs/2. UseCases.md` for the full functional/business spec, and `docs/6. Roadmap.md` for what phase the project is currently in.

**Current state**: the project has only reached Phase 3-4 of its roadmap — JPA entities (`model/`) and empty `JpaRepository` interfaces (`repository/`) exist. There is no service layer, no DTOs/mappers, no REST controllers, and no Spring Security/authentication yet, despite those being required (NFR04, BR11). Don't assume request/response layers exist — check before referencing them.

## Commands

This is a Maven project (Java 21, Spring Boot 4.1.0). Use the wrapper, not a system-installed Maven.

```bash
./mvnw spring-boot:run          # run the app locally
./mvnw compile                  # compile only
./mvnw test                     # run all tests
./mvnw test -Dtest=ClassName    # run a single test class
./mvnw test -Dtest=ClassName#methodName   # run a single test method
./mvnw clean package            # build the jar (target/)
```

On Windows use `mvnw.cmd` instead of `./mvnw` when running from `cmd.exe`/PowerShell without a POSIX shell.

The app requires a running MySQL instance and `DB_USERNAME`/`DB_PASSWORD` environment variables (see Configuration below) — `./mvnw spring-boot:run` will fail to start without both.

## Configuration

`src/main/resources/application.properties` connects to `jdbc:mysql://localhost:3306/clinichub_db` using `${DB_USERNAME}` and `${DB_PASSWORD}` env vars (no defaults — must be set before running). `spring.jpa.hibernate.ddl-auto=update` means schema is auto-migrated from entities on startup; there are no Flyway/Liquibase migrations, so entity field/annotation changes apply directly to the DB on next run.

## Architecture

Standard layered Spring Boot structure under `com.clinichub`:
- `model/` — JPA entities (`@Entity`), one per domain table
- `repository/` — `JpaRepository<Entity, Long>` interfaces, currently empty (no custom query methods yet)
- (not yet present) `service/`, `controller/`, `dto/` — to be added per the roadmap

### Domain model

Entities use Lombok (`@Getter`/`@Setter`/`@NoArgsConstructor`/`@AllArgsConstructor`) instead of manual boilerplate, and all `@ManyToOne`/`@OneToOne` associations are `FetchType.LAZY`.

- **User** — base account with `email` (unique), `password`, and a `Role` enum (`ADMIN`, `DOCTOR`, `PATIENT`). One user maps to at most one `Patient` or `Doctor` via a `OneToOne` on `user_id`.
- **Patient** — `OneToOne` to `User`, plus `cpf` (unique), `phone`, `birthDate`.
- **Doctor** — `OneToOne` to `User`, plus `crm` (unique) and a required `ManyToOne` to `Specialty`.
- **Specialty** — simple lookup table (`name`, unique).
- **Appointment** — links a `Patient` and `Doctor` (`ManyToOne`, both required), `appointmentDate`, and a `Status` enum (`SCHEDULED`, `COMPLETED`, `CANCELLED`).
- **MedicalRecord** — `OneToOne` to `Appointment` (unique `appointment_id`) — one record per appointment, only valid for completed appointments (BR07).
- **Exam** — `ManyToOne` to `Appointment` — an appointment can have multiple exams.

Key business rules to enforce when adding logic in this domain (full list in `docs/3. BusinessRules.md`):
- Emails, CPFs, CRMs, and specialty names must be unique (already enforced at the DB level via `unique = true`, but service-layer validation/error handling doesn't exist yet).
- Appointments cannot be scheduled in the past (BR05) — not yet validated anywhere.
- A medical record can only be created for a `COMPLETED` appointment, and only by the assigned doctor (BR07, BR14).
- Patients may only view their own appointments/exams (BR13, BR15) — this requires authentication/authorization, which is not implemented yet.

## Design docs

`docs/` contains the source-of-truth spec, written before/alongside implementation — check it when implementing a new feature to confirm you're matching the intended behavior, since the code doesn't yet cover most of it:
- `1. Requirements.md` — functional/non-functional requirements
- `2. UseCases.md` — use cases
- `3. BusinessRules.md` — numbered business rules (BR01-BR15)
- `4. ConceptualModel.md` / `5. LogicalModel.md` — data model docs (see also `docs/images/`)
- `6. Roadmap.md` — phased implementation checklist; consult this to know what should be built next and in what order
