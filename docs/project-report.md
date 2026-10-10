# Academic Project Report: Virtual Reality Therapy Sessions: Connected VR Mental Health Platform

**Academic Course:** Advanced Java Web-Based Project / Telehealth Rehabilitation Systems  
**Project Title:** Virtual Reality Therapy Sessions: Connected VR Mental Health Platform  
**Architecture:** Plain Jakarta Servlet 6.0, JSP 3.1, JSTL 3.0, and Raw JDBC Architecture (No Spring, No Hibernate)  
**Release Version:** 2.0.0  

---

## 1. Problem Statement
Traditional psychotherapy and behavioral desensitization (such as exposure therapy for Phobias, PTSD, and generalized Anxiety) frequently face critical clinical barriers:
- **In-vivo exposure** (e.g. taking patients to high balconies or crowded transit) is logistically difficult, expensive, and cannot be dynamically controlled if a patient enters acute panic.
- **In-office imaginal exposure** often lacks visceral sensory fidelity, making emotional habituation slow.
- **Clinicians lack quantitative, continuous biometric telemetry** (autonomic heart rate response, subjective stress ratings, adherence metrics) during remote sessions.
- **Legacy telehealth platforms** are purely videoconferencing tools and lack bidirectional virtual reality control, real-time physiological crisis safety gates, device pairing, and graduated fear hierarchies.

## 2. Project Objectives
1. **Interactive Connected WebXR Immersion:** Deliver 6 procedural 3D WebXR environments (*Calm Beach, Forest Walk, Heights Exposure, Social Gathering, Breathing Room, Night Sky Sleep Room*) playable in physical VR headsets (Meta Quest Browser / Pico) or in standard browsers using keyboard/mouse orbit controls.
2. **Real-Time Live Telehealth Guidance (AJAX 2-Second Polling & Command Queue):** Enable therapists to guide live remote sessions, observing biometric heart-rate and stress telemetry in real-time, switching environments on the fly, toggling 4-4-4 breathing guidance spheres, pushing text guidance into the patient's floating VR HUD, and executing immediate safety stops.
3. **Cryptographic VR Device Pairing & Heartbeat Daemon:** Implement a 6-digit cryptographic pairing system (5-minute validity), device fleet management, latency diagnostics (ping in ms), and a scheduled daemon monitor detecting client disconnects.
4. **Graduated Exposure Hierarchy Builder:** Construct structured ladders of increasing difficulty where subsequent steps unlock in Java only when patient distress drops below clinician-configured thresholds.
5. **Clinical Decision Support & Safety Strategies:** 
   - **Suggestion Strategy Pattern:** Evaluates post-session comfort and ratings to suggest parameter adjustments.
   - **Crisis Detection Strategy Pattern:** Continuously evaluates incoming telemetry pulses (heart rate ceiling >= 135 bpm, acute stress >= 9/10, prolonged arousal) to auto-pause the session and dispatch urgent clinician alerts.
   - **Badge Milestone Strategy Pattern:** Evaluates patient milestones (*Virtual Pioneer, 7-Day Resilient Flow, Fear Conqueror Level 3, Zen Recovery Master*) without duplication.
6. **Comprehensive Patient Wellbeing Ecosystem:** Daily mood journaling with trend charts, CBT cognitive restructuring thought records, inter-session homework assignments, gamified achievement badges, and secure clinician messaging.
7. **Simulated Headset Mode:** Integrated simulator page (`/simulator`) producing realistic physiological biometrics so the full telehealth platform can be demonstrated without physical hardware.

---

## 3. System Architecture & Modules

### 3.1 Layered Architecture Overview
```
Presentation Layer (JSP 3.1, JSTL 3.0, A-Frame WebXR 3D, Chart.js, Vanilla CSS)
       ↕
Web & Servlets Layer (Jakarta Servlet 6.0, Filters, Listeners, AJAX Polling APIs)
       ↕
Service & Business Logic Layer (Device Pairing, Live Session State Machine, Telemetry, Strategy Engines)
       ↕
Persistence Layer (Raw JDBC, PreparedStatement, GenericDao, Connection Pool)
       ↕
Database Layer (MySQL 8 / Embedded H2 Database with schema.sql and seed.sql)
```

### 3.2 Core Functional Modules
1. **Therapist Clinical Hub (`com.vrtherapy.service.TherapySessionService` & `TherapyPlanService`):**
   - **Live Session Control Room (`/therapist/live-control`):** Real-time Chart.js telemetry monitor, intensity slider (1–5), environment switcher, guided breathing orb toggle, text guidance transmitter, and emergency halt override.
   - **Therapy Plans & Exposure Hierarchy Builder (`/therapist/plan`, `/therapist/hierarchy`):** Multi-week treatment plan authoring with step-by-step fear ladders and rule-based threshold unlocks.
   - **Inter-Session Homework Manager (`/therapist/homework`):** Assigns CBT exercises, mindfulness drills, and journaling tasks with tracking.
   - **Clinical Messaging Hub (`/messages`):** Asynchronous direct secure communications.
   - **Progress & Analytics Service (`ProgressAnalyticsService`):** Stress deltas, recovery percentages, adherence rates, and downloadable CSV/text exports.

2. **Patient Rehabilitation Portal (`com.vrtherapy.servlet.VrLaunchServlet`, `DevicePairingServlet`):**
   - **Device Pairing ("Connect My VR Device" `/patient/device-pairing`):** Registers headsets/browsers, generates 6-digit codes with countdown timers, tests roundtrip ping latency in ms, and performs pre-session hardware checks.
   - **Live Guided VR Scene (`/vr/launch`):** Full WebXR scene reflecting live therapist commands in real time with floating in-VR HUD and visible "I NEED TO STOP" panic button.
   - **Daily Mood Journal (`/patient/mood-journal`):** Mood score (1-10), energy rating, emotion tags, and historical trend chart.
   - **CBT Thought Records (`/patient/thought-record`):** Situation, automatic thought, cognitive distortion analysis, rational counter-thought, and outcome score.
   - **Homework & Badges (`/patient/homework`, `/feedback`):** Task checklist and gamified clinical milestone badges.
   - **Emergency Grounding Screen (`/patient/emergency-grounding`):** Triggered on panic stop; displays somatic 5-4-3-2-1 exercise and 24/7 crisis helpline contacts.

3. **System Administration & Fleet Control:**
   - **User Management (`/admin/users`):** Account provisioning, role assignments, clinical profile updates, and active/inactive toggles.
   - **Device Fleet Management (`/admin/devices`):** Hardware fleet list, force-disconnect capability, pairing logs, and device blocking.
   - **System Health Monitor (`/admin/status`):** Real-time JVM memory stats, active session counter, device connection count, and average HTTP response time tracked by `ResponseTimeFilter`.
   - **Technical Support Helpdesk (`/tickets`):** Full ticket ticketing system with statuses (OPEN, IN_PROGRESS, RESOLVED, CLOSED) and threaded responses.

---

## 4. Academic Evaluation Criteria & Technology Stack

| Marking Rubric Area | Project Implementation Highlights |
| :--- | :--- |
| **1. Problem Understanding & Design (8 Marks)** | Detailed mental health tele-rehabilitation platform addressing in-vivo exposure safety; MVC architecture with strict separation of concerns; 70.6% Java lines of code. |
| **2. Core Java Concepts (10 Marks)** | **OOP:** Abstract `BaseEntity` and `User` with `Patient`, `Therapist`, `Admin` subclasses; `VrEnvironment` polymorphic implementations and Factory.<br>**Patterns:** Strategy, Observer, State Machine, DAO, Singleton.<br>**Exceptions:** Custom checked/unchecked exceptions, deterministic try-with-resources.<br>**Generics:** `GenericDao<T, ID>`, `Result<T>`, Java Streams, Comparators.<br>**Multithreading:** `ScheduledExecutorService` (`HeartbeatMonitor`, `ReminderService`), `TelemetrySimulator` pool, thread-safe `VrConnectionRegistry` and `CommandQueue`. |
| **3. Database Integration (8 Marks)** | Custom Singleton `ConnectionManager` connection pool (`BlockingQueue<Connection>`); 18 DAO implementations with `PreparedStatement` everywhere; explicit transaction boundaries (`commit`/`rollback`); batch inserts for telemetry; comprehensive `schema.sql` and `seed.sql`. |
| **4. Servlets & Web Integration (7 Marks)** | Jakarta Servlet 6.0 (`@WebServlet`), JSP 3.1, JSTL 3.0; filters (`AuthFilter`, `RoleFilter`, `ResponseTimeFilter`, `CharacterEncodingFilter`, `MaintenanceModeFilter`); listeners (`AppContextListener`, `AppSessionListener`); JSON AJAX endpoints with 2-second polling; WebXR A-Frame 3D scenes; hardware-free simulator mode (`/simulator`). |

---

## 5. Safety, Ethical & Clinical Considerations
1. **Zero-Delay Emergency Intervention:** The in-VR "I NEED TO STOP" button immediately executes `POST /api/emergency-stop`, terminating telemetry generation, alerting the clinician, transitioning the session state machine to `EMERGENCY_STOPPED`, and routing the patient to grounding protocols.
2. **Physiological Safety Ceilings:** Background crisis evaluation triggers automatic clinician alarms when heart rate exceeds 135 bpm or stress reaches 9/10.
3. **Medical Disclaimer:** The interface persistently displays: *"This is an academic prototype and not a substitute for professional medical care."*
4. **Data Privacy & Cryptography:** Passwords hashed with salted BCrypt; authorization filters protect all patient clinical records from unauthorized cross-role access.

---

## 6. Future Scope
- Integration with clinical Bluetooth Low Energy (BLE) photoplethysmography (PPG) sensors (e.g. Polar H10 or Apple Watch HealthKit) for hardware biofeedback.
- AI-driven adaptive audio spatialization based on real-time heart rate variability (HRV).
- Multi-user group therapy environments for social phobia support circles.
