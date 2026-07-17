# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

ClinicHub is a medical clinic management system backend built with Spring Boot. Administrators register patients, doctors, and specialties; patients schedule/cancel appointments; doctors record medical records and request exams.

The git repository root and the Maven project root are both `clinichub/`, one level below the enclosing `clinichubApp/` folder. Run all commands from `clinichub/`.

**Current state (roadmap Phase 5 complete, Phase 6 next)**: entities, repositories, DTOs, mappers, and services all exist. There are **no REST controllers**, no global exception handler, and no Spring Security/authentication yet — so the app currently exposes no HTTP endpoints, and nothing invokes the service layer at runtime. Check `docs/6. Roadmap.md` for what comes next; verify a layer exists before referencing it.

## Commands

Maven project (Java 21, Spring Boot 4.1.0). Use the wrapper, not a system-installed Maven — on Windows PowerShell/`cmd.exe` use `mvnw.cmd` instead of `./mvnw`.

```bash
./mvnw spring-boot:run          # run the app locally
./mvnw compile                  # compile only
./mvnw test                     # run all tests
./mvnw test -Dtest=ClassName    # run a single test class
./mvnw test -Dtest=ClassName#methodName   # run a single test method
./mvnw clean package            # build the jar (target/)
```

`spring-boot:run` and `./mvnw test` both need a running MySQL **and** `DB_USERNAME`/`DB_PASSWORD` set — the only test, `ClinichubApplicationTests.contextLoads`, is a `@SpringBootTest` that boots the full context and therefore hits the datasource. `./mvnw compile` is the only command that works without a database.

## Configuration

`src/main/resources/application.properties` points at `jdbc:mysql://localhost:3306/clinichub_db` using `${DB_USERNAME}`/`${DB_PASSWORD}` with no defaults. `spring.jpa.hibernate.ddl-auto=update` — there are no Flyway/Liquibase migrations, so entity changes migrate the schema directly on next startup. SQL logging is on (`show-sql`, `format_sql`).

## Architecture

Layered structure under `com.clinichub`, with strict one-class-per-entity symmetry across packages — each of the 7 domain entities has a matching repository, service, mapper, and request/response DTO pair. Follow the existing shape when adding anything:

- `model/` — JPA entities. Lombok (`@Getter`/`@Setter`/`@NoArgsConstructor`/`@AllArgsConstructor`), explicit `@Table` names (plural: `users`, `appointments`, `exams`), all `@ManyToOne`/`@OneToOne` are `FetchType.LAZY`, enums nested inside their entity (`Appointment.Status`, `User.Role`) and persisted `@Enumerated(EnumType.STRING)`.
- `repository/` — `JpaRepository<Entity, Long>` plus derived lookups returning `Optional` (`findByEmail`, `findByCrm`, `findByCpf`, `findByName`, `findByAppointmentId`). No `@Query` anywhere yet.
- `dto/` — Java `record`s named `<Entity>RequestDTO` / `<Entity>ResponseDTO`, with narrow update records where a partial update is intended (`AppointmentStatusUpdateDTO`, `ExamResultUpdateDTO`, `MedicalRecordUpdateDTO`). Bean Validation annotations (`@NotNull`, `@NotBlank`, `@Email`) live here with custom messages. **These annotations do not fire yet** — nothing calls `@Valid`, which lands at the controller boundary in Phase 6. Requests reference associations by id (`userId`, `doctorId`, `appointmentId`), never by nested object.
- `mapper/` — plain classes with `static` methods only (`toEntity`, `toResponseDTO`); not Spring beans, not MapStruct, never injected. `toEntity` maps **scalar fields only** — the service is responsible for resolving and setting every association afterwards.
- `service/` — `@Service` with constructor injection into `private final` fields (no `@Autowired`, no Lombok on services). Each exposes the same CRUD surface: `create`, `getById`, `getAll`, `update`, `delete`. Services inject the repositories of associated entities to resolve foreign keys.

The service layer is where business rules and lookups live; the standard idiom is `repository.findById(id).orElseThrow(...)`, mutate, `save`, then map to a response DTO.

### Error handling

Every failure — not-found, duplicate, rule violation — is a bare `throw new RuntimeException("message")`. There are no custom exception types and no `@ControllerAdvice`, so nothing currently maps these to HTTP status codes; Phase 7 covers that. Match the existing style when editing these services, but expect this to be replaced wholesale rather than extended.

### Transactions

No `@Transactional` anywhere. Multi-step service methods rely on the repository's own transaction per `save`, so partial writes are possible in methods that touch more than one aggregate.

## Domain model

- **User** — account: `name`, unique `email`, `password`, `Role` enum (`ADMIN`, `DOCTOR`, `PATIENT`). Maps to at most one `Patient` or `Doctor` via `OneToOne` on `user_id`.
- **Patient** — `OneToOne` to `User`, plus unique `cpf`, `phone`, `birthDate`.
- **Doctor** — `OneToOne` to `User`, plus unique `crm` and a required `ManyToOne` to `Specialty`.
- **Specialty** — lookup table with unique `name`.
- **Appointment** — required `ManyToOne` to both `Patient` and `Doctor`, plus `appointmentDate` and `Status` (`SCHEDULED`, `COMPLETED`, `CANCELLED`). Status is set to `SCHEDULED` by the service on create, not by the client.
- **MedicalRecord** — `OneToOne` to `Appointment` (unique `appointment_id`): one record per appointment.
- **Exam** — `ManyToOne` to `Appointment` (an appointment has many); `requestedAt` is stamped server-side on create.

### Business rules

`docs/3. BusinessRules.md` has the numbered list (BR01–BR15). Currently enforced in services: appointments cannot be scheduled in the past (BR05, `AppointmentService.create`); medical records only for `COMPLETED` appointments (BR07, `MedicalRecordService.create`); uniqueness pre-checks on email, CPF, and CRM before insert.

Not yet enforced — all blocked on authentication (Phase 8): a medical record may only be created by the appointment's assigned doctor (BR14), and patients may only view their own appointments/exams (BR13, BR15). Note the uniqueness pre-checks race against the DB constraint and surface as an unhandled `DataIntegrityViolationException` under concurrency; the `unique = true` columns are the real guarantee.

Passwords are persisted in plaintext (`UserMapper.toEntity` copies the raw string). Hashing arrives with Spring Security in Phase 8 (NFR04, BR11) — don't treat the current `User.password` handling as a model to follow.

## Known trap

`AppointmentRepository.findByStatus` is broken in two ways and has no callers, so nothing surfaces it today — verified: the app boots and Spring Data instantiates all 7 repositories in DEFAULT (eager) mode without complaining about it.

1. It imports `java.io.ObjectInputFilter.Status` instead of `Appointment.Status`.
2. It returns `Optional<Appointment>`, but status is not unique — fixing only the import buys an `IncorrectResultSizeDataAccessException` as soon as two appointments share a status.

Both need fixing together before the method is used: `List<Appointment> findByStatus(Appointment.Status status);`

## Design docs

`docs/` is the source-of-truth spec, written ahead of implementation — consult it when implementing a feature, since the code still lags most of it:

- `1. Requirements.md` — functional/non-functional requirements
- `2. UseCases.md` — use cases
- `3. BusinessRules.md` — numbered business rules (BR01–BR15)
- `4. ConceptualModel.md` / `5. LogicalModel.md` — data model (diagrams in `docs/images/`)
- `6. Roadmap.md` — phased checklist; consult to know what to build next and in what order, and tick items off as phases complete

`HELP.md` is untouched Spring Initializr boilerplate — ignore it.

## Git conventions

`main` ← `develop` ← `feature/*`, one feature branch per roadmap phase (`feature/backend-setup`, `feature/jpa-entities`, `feature/repositories`, `feature/business-layer`). Commits follow Conventional Commits (`feat:`, `docs:`, `merge:`). Work on the current `feature/*` branch, not on `develop` or `main`.
