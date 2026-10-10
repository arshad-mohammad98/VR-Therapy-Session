# Academic Marking Rubric Mapping

**Project Title:** Virtual Reality Therapy Sessions: Connected VR Mental Health Platform  
**Architecture:** Plain Java Servlets (Jakarta 6.0), JSP 3.1, JSTL 3.0, and Raw JDBC (No Spring / No JPA / No Hibernate)  
**Total Marks:** 33 Marks  

This document serves as the grading matrix, cross-referencing each requirement of the academic marking rubric directly to the concrete Java classes, methods, design patterns, and source files within the project.

---

## 1. Problem Understanding & Solution Design (8 Marks)

### Clinical Objective & Real-World Domain Problem
Exposure therapy and relaxation training for Phobias, PTSD, Anxiety, and Insomnia often suffer from logistical constraints, lack of dynamic safety controls during panic, and the inability to observe continuous real-time biometrics. This project provides a clinical tele-rehabilitation platform combining immersive WebXR 3D environments with bidirectional clinician telemetry and emergency intervention safeguards.

### Architectural Overview
- **Strict MVC Separation:** Controller (`com.vrtherapy.servlet.*`), Model/DTO (`com.vrtherapy.model.*`, `com.vrtherapy.dto.*`), Service/Logic (`com.vrtherapy.service.*`), and View (`WEB-INF/views/**/*.jsp`).
- **Zero-Spring Pure Jakarta Architecture:** Clean Jakarta Servlet 6.0 endpoints, request forwarding (`RequestDispatcher`), post-redirect-get (PRG), standard sessions (`HttpSession`), and filter chains (`jakarta.servlet.Filter`).
- **Language Breakdown:** **70.6% Java** (13,620 lines of Java business logic, validation, security, multithreading, and data access), with the remaining lines dedicated to JSP views, WebXR A-Frame scenes, and SQL scripts.

### Role Flow Implementations
| User Role | Clinical Features & Capabilities | Key Implementation Classes |
| :--- | :--- | :--- |
| **Therapist** | Session design (parameters & JSON config), Multi-week therapy plans, Exposure hierarchy unlocking logic, Live telemetry room with real-time biometric charts, AJAX 2s polling command queue, Progress analytics (stress delta, recovery %, CSV export), Rule-based feedback adjustments, Private notes & Homework assignment. | `TherapySessionServiceImpl`<br>`TherapyPlanServiceImpl`<br>`ProgressAnalyticsServiceImpl`<br>`LiveControlServlet`<br>`ReportServlet`<br>`SuggestionEngine` |
| **Patient** | 6-digit cryptographic VR device pairing (5-min expiry), Hardware readiness check, WebXR A-Frame 3D session launcher with breathing orb, In-VR "I need to stop" panic trigger, Emergency grounding protocol (5-4-3-2-1 somatic guide), Mood journal with trend analytics, CBT thought records, Homework tracker, Gamified badges. | `DevicePairingServiceImpl`<br>`SessionResultServiceImpl`<br>`WellbeingServiceImpl`<br>`DevicePairingServlet`<br>`VrLaunchServlet`<br>`EmergencyStopServlet` |
| **System Administrator** | User CRUD & role-based deactivation, System configuration settings (timeouts, alert thresholds), System status & diagnostic dashboard (uptime, JVM memory, thread count, average response time from Filter), VR fleet management, Support ticket resolution system, Security audit logging. | `UserServiceImpl`<br>`SupportTicketServiceImpl`<br>`AdminUsersServlet`<br>`AdminSettingsServlet`<br>`AdminStatusServlet`<br>`ResponseTimeFilter` |

---

## 2. Core Java Concepts (10 Marks)

### A. Object-Oriented Programming (OOP)
1. **Inheritance & Abstraction:**
   - Abstract Base Entity: [`BaseEntity.java`](file:///src/main/java/com/vrtherapy/model/BaseEntity.java) encapsulates `id`, `createdAt`, `updatedAt` for all 18 database entities.
   - Abstract User & Polymorphic Subclasses: [`User.java`](file:///src/main/java/com/vrtherapy/model/User.java) is extended by:
     - [`Patient.java`](file:///src/main/java/com/vrtherapy/model/Patient.java) (encapsulates `assignedTherapistId`, `emergencyContact`, `baselineAnxiety`).
     - [`Therapist.java`](file:///src/main/java/com/vrtherapy/model/Therapist.java) (encapsulates `licenseNumber`, `specialization`, `maxConcurrentPatients`).
     - [`Admin.java`](file:///src/main/java/com/vrtherapy/model/Admin.java) (encapsulates `department`, `adminLevel`).
   - Abstract Servlet Base: [`BaseServlet.java`](file:///src/main/java/com/vrtherapy/servlet/BaseServlet.java) provides shared JSON serialization, session user extraction, and view forwarding logic.
   - Abstract Environment Base: [`AbstractVrEnvironment.java`](file:///src/main/java/com/vrtherapy/vrenvironment/AbstractVrEnvironment.java) provides default sky, fog, and ambient audio generation.

2. **Interfaces & Polymorphism:**
   - [`VrEnvironment.java`](file:///src/main/java/com/vrtherapy/vrenvironment/VrEnvironment.java) interface defining procedural WebXR world parameters and intensity adjustment algorithms.
   - 6 Polymorphic Implementations:
     - [`BeachEnvironment.java`](file:///src/main/java/com/vrtherapy/vrenvironment/BeachEnvironment.java) (Calm Beach)
     - [`ForestEnvironment.java`](file:///src/main/java/com/vrtherapy/vrenvironment/ForestEnvironment.java) (Forest Walk)
     - [`HeightsEnvironment.java`](file:///src/main/java/com/vrtherapy/vrenvironment/HeightsEnvironment.java) (Acrophobia Balcony Exposure)
     - [`SocialEnvironment.java`](file:///src/main/java/com/vrtherapy/vrenvironment/SocialEnvironment.java) (Social Anxiety Gathering)
     - [`BreathingEnvironment.java`](file:///src/main/java/com/vrtherapy/vrenvironment/BreathingEnvironment.java) (Respiration Entrainment Room)
     - [`NightSkyEnvironment.java`](file:///src/main/java/com/vrtherapy/vrenvironment/NightSkyEnvironment.java) (Insomnia Somatosensory Void)
   - Factory Pattern: [`VrEnvironmentFactory.java`](file:///src/main/java/com/vrtherapy/vrenvironment/VrEnvironmentFactory.java) dynamically instantiates the appropriate environment strategy at runtime.

3. **Software Design Patterns:**
   - **Singleton Pattern:** [`ConnectionManager.java`](file:///src/main/java/com/vrtherapy/dao/ConnectionManager.java) thread-safe singleton connection pool.
   - **Data Access Object (DAO) Pattern:** 18 distinct DAO interfaces (`UserDao`, `SessionDao`, `DeviceDao`, etc.) with raw JDBC implementations (`UserDaoImpl`, `SessionDaoImpl`, etc.).
   - **Strategy Pattern:**
     - Session adjustment suggestions: [`SuggestionStrategy.java`](file:///src/main/java/com/vrtherapy/strategy/SuggestionStrategy.java) and [`SuggestionEngine.java`](file:///src/main/java/com/vrtherapy/strategy/SuggestionEngine.java).
     - Biometric crisis detection: [`CrisisDetectionStrategy.java`](file:///src/main/java/com/vrtherapy/strategy/CrisisDetectionStrategy.java) and [`CrisisEngine.java`](file:///src/main/java/com/vrtherapy/strategy/CrisisEngine.java).
     - Gamified badge rules: [`BadgeRuleStrategy.java`](file:///src/main/java/com/vrtherapy/strategy/BadgeRuleStrategy.java) and [`BadgeEngine.java`](file:///src/main/java/com/vrtherapy/strategy/BadgeEngine.java).
   - **Observer Pattern:** [`CrisisSubject.java`](file:///src/main/java/com/vrtherapy/observer/CrisisSubject.java) notifying registered [`CrisisObserver.java`](file:///src/main/java/com/vrtherapy/observer/CrisisObserver.java) instances when vitals exceed thresholds.
   - **State Machine Pattern:** [`LiveSessionState.java`](file:///src/main/java/com/vrtherapy/model/LiveSessionState.java) enforcing valid clinical state transitions:
     `SCHEDULED -> WAITING -> ACTIVE -> PAUSED -> COMPLETED / EMERGENCY_STOPPED`.

### B. Exception Handling
- Custom checked and unchecked clinical exceptions:
  - [`DatabaseException.java`](file:///src/main/java/com/vrtherapy/exception/DatabaseException.java) (wraps SQL failures)
  - [`ValidationException.java`](file:///src/main/java/com/vrtherapy/exception/ValidationException.java) (clinical inputs)
  - [`AuthenticationException.java`](file:///src/main/java/com/vrtherapy/exception/AuthenticationException.java) (security failures)
  - [`InvalidStateTransitionException.java`](file:///src/main/java/com/vrtherapy/exception/InvalidStateTransitionException.java) (state machine)
  - [`DeviceNotFoundException.java`](file:///src/main/java/com/vrtherapy/exception/DeviceNotFoundException.java) (pairing failures)
  - [`ResourceNotFoundException.java`](file:///src/main/java/com/vrtherapy/exception/ResourceNotFoundException.java) (entity lookups)
- Systematic `try-with-resources` ensuring all `Connection`, `PreparedStatement`, and `ResultSet` handles are deterministically closed without resource leaks.
- Central error interceptor servlet ([`CentralErrorServlet.java`](file:///src/main/java/com/vrtherapy/servlet/CentralErrorServlet.java)) mapped in `web.xml` for HTTP 404 and 500 error pages.

### C. Collections, Generics, and Functional Java
- **Generic Repository Interface:** [`GenericDao.java`](file:///src/main/java/com/vrtherapy/dao/GenericDao.java) defining parameterized CRUD methods `<T, ID>`.
- **Generic Operation Wrapper:** [`Result.java`](file:///src/main/java/com/vrtherapy/dto/Result.java) providing generic monadic success/failure encapsulation.
- **Collections Hierarchy:** Extensive usage of `List`, `Map`, `Set`, `Queue`, `ConcurrentHashMap`, `ArrayBlockingQueue`, and `Deque`.
- **Streams, Lambdas & Optional:** Filter, map, sort (`Comparator.comparing(...)`), and `Optional<T>` handling across analytical services.

### D. Multithreading & Synchronization
1. **[`HeartbeatMonitor.java`](file:///src/main/java/com/vrtherapy/thread/HeartbeatMonitor.java):** Scheduled background daemon executing on a `ScheduledExecutorService`. Periodically checks active VR hardware devices and flags disconnected headsets past the timeout threshold.
2. **[`TelemetrySimulator.java`](file:///src/main/java/com/vrtherapy/thread/TelemetrySimulator.java):** Dynamic `Runnable` scheduled on an `ExecutorService` thread pool. Simulates realistic biometrics (heart-rate pulse, variability, and autonomic stress) for live session demonstrations.
3. **[`VrConnectionRegistry.java`](file:///src/main/java/com/vrtherapy/thread/VrConnectionRegistry.java):** Thread-safe registry backed by `ConcurrentHashMap` with atomic counters (`AtomicInteger`) tracking live connected devices and sessions.
4. **[`CommandQueue.java`](file:///src/main/java/com/vrtherapy/thread/CommandQueue.java):** Thread-safe FIFO instruction queue for live therapist-to-patient commands using `BlockingQueue`, `ReentrantLock`, and synchronized locks.
5. **[`ReminderService.java`](file:///src/main/java/com/vrtherapy/thread/ReminderService.java):** Scheduled background thread executing automated reminders for pending homework assignments and upcoming sessions.
6. **Graceful Lifecycle Management:** [`AppContextListener.java`](file:///src/main/java/com/vrtherapy/listener/AppContextListener.java) starts all background executors on servlet context initialization and shuts them down gracefully (`shutdown()`, `awaitTermination()`) in `contextDestroyed`.

---

## 3. Database Integration using JDBC (8 Marks)

### Raw JDBC Connection Pool
- [`ConnectionManager.java`](file:///src/main/java/com/vrtherapy/dao/ConnectionManager.java):
  - Singleton pool backed by a `BlockingQueue<Connection>`.
  - Configured from [`db.properties`](file:///src/main/resources/db.properties).
  - Production support for MySQL 8 (`com.mysql.cj.jdbc.Driver`) with automated embedded fallback to H2 database for zero-configuration local evaluation.
  - Automatically executes `schema.sql` and `seed.sql` on startup if tables are absent.

### PreparedStatements & Transaction Management
- **Zero SQL Concatenation:** 100% of SQL queries use parameterized `PreparedStatement` preventing SQL injection.
- **Transactions (`commit` / `rollback`):** Explicit multi-table atomic transactions implemented in DAOs (e.g. `UserDaoImpl` inserting into `users` followed by role-specific `patients`, `therapists`, or `admins` tables; `SupportTicketDaoImpl` cascading reply insertions).
- **Batch Processing:** `TelemetryDaoImpl` uses `preparedStatement.addBatch()` and `executeBatch()` for high-throughput biometric logging.
- **ResultSet Row Mappers:** Standardized private extraction methods (`mapRow(ResultSet rs)`) converting relational records into domain models.

### Schema & Initial Seed Data
- [`schema.sql`](file:///schema.sql): 18 relational tables with primary keys, foreign keys (`ON DELETE CASCADE`), unique constraints, and indexes.
- [`seed.sql`](file:///seed.sql): Complete clinical test dataset:
  - 1 Administrator (`admin` / `admin123`)
  - 2 Therapists (`therapist1` / `therapist123`, `therapist2` / `therapist123`)
  - 5 Patients (`patient1` through `patient5`, password `patient123`)
  - 10 Sessions with biometric results and patient feedback
  - 2 Multi-week therapy plans with graduated exposure hierarchies
  - 3 Registered VR devices (Meta Quest 3, HTC Vive Focus 3, Pico 4)
  - Mood journal entries, CBT thought records, homework assignments, achievement badges, and technical support tickets.

---

## 4. Servlets & Web Integration (7 Marks)

### Jakarta Servlet Architecture
Annotated with `@WebServlet` (Jakarta Servlet 6.0 Specification):
- **Authentication & Core:** `LoginServlet` (`/login`), `LogoutServlet` (`/logout`), `RegisterServlet` (`/register`), `DashboardServlet` (`/dashboard`).
- **Therapist Clinical Hub:** `SessionDesignServlet` (`/therapist/session-design`), `PlanServlet` (`/therapist/plan`), `HierarchyServlet` (`/therapist/hierarchy`), `LiveControlServlet` (`/therapist/live-control`), `ProgressServlet` (`/therapist/progress`), `ReportServlet` (`/therapist/report/csv`), `FeedbackServlet` (`/therapist/feedback`, `/feedback`), `HomeworkServlet` (`/therapist/homework`, `/patient/homework`).
- **Patient Telehealth Portal:** `DevicePairingServlet` (`/patient/device-pairing`), `VrLaunchServlet` (`/vr/launch`), `MoodJournalServlet` (`/patient/mood-journal`), `ThoughtRecordServlet` (`/patient/thought-record`), `EmergencyStopServlet` (`/api/emergency-stop`, `/patient/emergency-grounding`).
- **Real-Time AJAX Endpoints (2-Second Polling):**
  - `TelemetryApiServlet` (`/api/telemetry`): Returns JSON biometric stream (heart rate, stress, HRV) consumed by Chart.js.
  - `CommandApiServlet` (`/api/command`): Transmits live therapist commands (`INTENSITY`, `ENVIRONMENT`, `BREATHING_GUIDE`, `PROMPT`) to the patient VR viewport.
- **Simulator & Hardware Emulation:** `SimulatorServlet` (`/simulator`) providing complete hardware-free evaluation of live remote guidance.
- **Admin & Fleet Management:** `AdminUsersServlet` (`/admin/users`), `AdminDevicesServlet` (`/admin/devices`), `AdminSettingsServlet` (`/admin/settings`), `AdminStatusServlet` (`/admin/status`), `TicketServlet` (`/tickets`).

### Servlet Filters & Interceptors
- [`AuthFilter.java`](file:///src/main/java/com/vrtherapy/filter/AuthFilter.java): Authenticates session tokens and prevents unauthenticated access to protected routes.
- [`RoleFilter.java`](file:///src/main/java/com/vrtherapy/filter/RoleFilter.java): Enforces role-based access control (`ROLE_ADMIN`, `ROLE_THERAPIST`, `ROLE_PATIENT`).
- [`ResponseTimeFilter.java`](file:///src/main/java/com/vrtherapy/filter/ResponseTimeFilter.java): Measures HTTP latency and computes rolling average response times displayed on the Admin Status page.
- [`CharacterEncodingFilter.java`](file:///src/main/java/com/vrtherapy/filter/CharacterEncodingFilter.java): Enforces UTF-8 character encoding across all requests and responses.
- [`MaintenanceModeFilter.java`](file:///src/main/java/com/vrtherapy/filter/MaintenanceModeFilter.java): Gracefully redirects non-administrative traffic when maintenance mode is engaged.

### Servlet Context & Session Listeners
- [`AppContextListener.java`](file:///src/main/java/com/vrtherapy/listener/AppContextListener.java): Bootstraps connection pool, schedules `HeartbeatMonitor` and `ReminderService`, and shuts down thread pools.
- [`AppSessionListener.java`](file:///src/main/java/com/vrtherapy/listener/AppSessionListener.java): Tracks active concurrent HTTP sessions.

### Security & Compliance
- **Password Hashing:** Passwords securely hashed with salt rounds using `jBCrypt` (`BCrypt.hashpw`, `BCrypt.checkpw`).
- **CSRF Protection:** Anti-CSRF token verification on clinical forms.
- **Input Validation:** Central server-side validation utility ([`ValidationUtil.java`](file:///src/main/java/com/vrtherapy/util/ValidationUtil.java)) validating string lengths, email formats, and clinical integer ranges.
- **Medical Disclaimer Banner:** Present on all user-facing headers: *"This is an academic prototype and not a substitute for professional medical care."*

---

## 5. Automated Verification & Test Coverage
- **44 JUnit 5 Tests Passing (0 Failures, 0 Errors, 0 Skipped):**
  - Live session state machine transitions (`LiveSessionStateTest`)
  - 6-digit cryptographic pairing code expiry (`DevicePairingServiceTest`)
  - Exposure hierarchy unlocking threshold logic (`ExposureHierarchyUnlockTest`)
  - Clinical analytics and stress delta algorithms (`ProgressAnalyticsServiceTest`)
  - Rule-based session suggestion strategies (`SuggestionStrategyTest`)
  - Biometric crisis threshold alerts (`CrisisDetectionStrategyTest`)
  - Gamified achievement badge rules (`BadgeRulesTest`)
  - JSON serialization & date formatting (`JsonUtilTest`)
  - CSV report generator (`ReportGeneratorTest`)
  - Polymorphic VR environment configurations (`NightSkyEnvironmentTest`, `VrEnvironmentFactoryTest`)
  - Support ticket workflow (`SupportTicketServiceTest`)
  - Therapy session service CRUD & adjustment logic (`TherapySessionServiceTest`)
