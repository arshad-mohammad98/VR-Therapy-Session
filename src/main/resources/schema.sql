-- ==============================================================================
-- Virtual Reality Therapy Sessions: Connected VR Mental Health Platform
-- Database Schema for MySQL 8.x / H2 in MySQL Mode
-- ==============================================================================

-- 1. Users Table (Base entity for Patient, Therapist, Admin)
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    role VARCHAR(20) NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    phone VARCHAR(30),
    clinical_bio_or_notes TEXT,
    -- Polymorphic / role-specific fields
    assigned_therapist_id BIGINT,
    emergency_contact VARCHAR(150),
    baseline_anxiety INT DEFAULT 5,
    license_number VARCHAR(100),
    specialization VARCHAR(100),
    department VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 2. Therapy Sessions Table
CREATE TABLE IF NOT EXISTS therapy_sessions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(150) NOT NULL,
    condition_type VARCHAR(50) NOT NULL,
    vr_environment VARCHAR(50) NOT NULL,
    default_duration_minutes INT NOT NULL DEFAULT 15,
    intensity_level INT NOT NULL DEFAULT 1,
    audio_enabled BOOLEAN NOT NULL DEFAULT TRUE,
    guided_breathing_enabled BOOLEAN NOT NULL DEFAULT TRUE,
    therapist_notes TEXT,
    config_json TEXT,
    created_by BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (created_by) REFERENCES users(id) ON DELETE SET NULL
);

-- 3. Therapy Plans Table
CREATE TABLE IF NOT EXISTS therapy_plans (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    patient_id BIGINT NOT NULL,
    therapist_id BIGINT NOT NULL,
    title VARCHAR(150) NOT NULL,
    condition_type VARCHAR(50) NOT NULL,
    total_weeks INT NOT NULL DEFAULT 4,
    status VARCHAR(30) NOT NULL DEFAULT 'ACTIVE',
    goals TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (patient_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (therapist_id) REFERENCES users(id) ON DELETE CASCADE
);

-- 4. Exposure Hierarchy Levels Table
CREATE TABLE IF NOT EXISTS exposure_levels (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    plan_id BIGINT NOT NULL,
    session_id BIGINT,
    level_order INT NOT NULL,
    title VARCHAR(150) NOT NULL,
    description TEXT,
    distress_rating_threshold INT NOT NULL DEFAULT 5,
    is_unlocked BOOLEAN NOT NULL DEFAULT FALSE,
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (plan_id) REFERENCES therapy_plans(id) ON DELETE CASCADE,
    FOREIGN KEY (session_id) REFERENCES therapy_sessions(id) ON DELETE SET NULL
);

-- 5. Session Assignments Table
CREATE TABLE IF NOT EXISTS session_assignments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    patient_id BIGINT NOT NULL,
    session_id BIGINT NOT NULL,
    plan_id BIGINT,
    status VARCHAR(30) NOT NULL DEFAULT 'ASSIGNED',
    scheduled_date DATE,
    assigned_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (patient_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (session_id) REFERENCES therapy_sessions(id) ON DELETE CASCADE,
    FOREIGN KEY (plan_id) REFERENCES therapy_plans(id) ON DELETE SET NULL
);

-- 6. Live Sessions Table (State Machine: SCHEDULED, WAITING, ACTIVE, PAUSED, COMPLETED, EMERGENCY_STOPPED)
CREATE TABLE IF NOT EXISTS live_sessions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    session_id BIGINT NOT NULL,
    patient_id BIGINT NOT NULL,
    therapist_id BIGINT NOT NULL,
    state VARCHAR(30) NOT NULL DEFAULT 'SCHEDULED',
    current_environment VARCHAR(50) NOT NULL,
    current_intensity INT NOT NULL DEFAULT 1,
    breathing_guide_active BOOLEAN NOT NULL DEFAULT TRUE,
    elapsed_seconds INT NOT NULL DEFAULT 0,
    current_heart_rate INT DEFAULT 75,
    current_stress_score INT DEFAULT 4,
    in_vr_message VARCHAR(255),
    started_at TIMESTAMP NULL,
    ended_at TIMESTAMP NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (session_id) REFERENCES therapy_sessions(id) ON DELETE CASCADE,
    FOREIGN KEY (patient_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (therapist_id) REFERENCES users(id) ON DELETE CASCADE
);

-- 7. Session Results Table
CREATE TABLE IF NOT EXISTS session_results (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    session_id BIGINT NOT NULL,
    patient_id BIGINT NOT NULL,
    pre_stress_score INT NOT NULL,
    post_stress_score INT NOT NULL,
    duration_seconds INT NOT NULL DEFAULT 0,
    average_heart_rate INT DEFAULT 75,
    peak_heart_rate INT DEFAULT 85,
    emergency_stopped BOOLEAN NOT NULL DEFAULT FALSE,
    stop_reason VARCHAR(255),
    completion_notes TEXT,
    completed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (session_id) REFERENCES therapy_sessions(id) ON DELETE CASCADE,
    FOREIGN KEY (patient_id) REFERENCES users(id) ON DELETE CASCADE
);

-- 8. Feedback Table
CREATE TABLE IF NOT EXISTS feedbacks (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    result_id BIGINT,
    patient_id BIGINT NOT NULL,
    session_id BIGINT NOT NULL,
    rating INT NOT NULL,
    comfort_score INT NOT NULL,
    nausea_score INT NOT NULL,
    comments TEXT,
    applied_adjustment TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (result_id) REFERENCES session_results(id) ON DELETE SET NULL,
    FOREIGN KEY (patient_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (session_id) REFERENCES therapy_sessions(id) ON DELETE CASCADE
);

-- 9. VR Devices Table
CREATE TABLE IF NOT EXISTS vr_devices (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    device_name VARCHAR(100) NOT NULL,
    device_type VARCHAR(30) NOT NULL,
    pairing_code VARCHAR(10),
    code_expires_at TIMESTAMP NULL,
    status VARCHAR(30) NOT NULL DEFAULT 'DISCONNECTED',
    last_heartbeat TIMESTAMP NULL,
    patient_id BIGINT,
    ip_address VARCHAR(45),
    is_blocked BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (patient_id) REFERENCES users(id) ON DELETE SET NULL
);

-- 10. Telemetry Samples Table (High frequency time series)
CREATE TABLE IF NOT EXISTS telemetry_samples (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    live_session_id BIGINT,
    patient_id BIGINT,
    heart_rate INT NOT NULL,
    stress_score INT NOT NULL,
    hrv INT NOT NULL DEFAULT 65,
    breathing_rate INT NOT NULL DEFAULT 16,
    recorded_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (live_session_id) REFERENCES live_sessions(id) ON DELETE CASCADE
);

-- 11. Mood Entries Table
CREATE TABLE IF NOT EXISTS mood_entries (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    patient_id BIGINT NOT NULL,
    score INT NOT NULL,
    primary_emotion VARCHAR(50) NOT NULL,
    notes TEXT,
    recorded_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (patient_id) REFERENCES users(id) ON DELETE CASCADE
);

-- 12. Thought Records Table (CBT)
CREATE TABLE IF NOT EXISTS thought_records (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    patient_id BIGINT NOT NULL,
    situation TEXT NOT NULL,
    automatic_thought TEXT NOT NULL,
    cognitive_distortion VARCHAR(100) NOT NULL,
    rational_response TEXT NOT NULL,
    outcome_belief INT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (patient_id) REFERENCES users(id) ON DELETE CASCADE
);

-- 13. Homework Table
CREATE TABLE IF NOT EXISTS homework (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    patient_id BIGINT NOT NULL,
    therapist_id BIGINT NOT NULL,
    title VARCHAR(150) NOT NULL,
    description TEXT,
    due_date DATE NOT NULL,
    completed BOOLEAN NOT NULL DEFAULT FALSE,
    reminder_sent BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (patient_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (therapist_id) REFERENCES users(id) ON DELETE CASCADE
);

-- 14. Badges Table
CREATE TABLE IF NOT EXISTS badges (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    patient_id BIGINT NOT NULL,
    badge_type VARCHAR(50) NOT NULL,
    title VARCHAR(100) NOT NULL,
    description VARCHAR(255) NOT NULL,
    icon_name VARCHAR(50) NOT NULL,
    awarded_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (patient_id) REFERENCES users(id) ON DELETE CASCADE
);

-- 15. Messages Table
CREATE TABLE IF NOT EXISTS messages (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    sender_id BIGINT NOT NULL,
    recipient_id BIGINT NOT NULL,
    content TEXT NOT NULL,
    is_read BOOLEAN NOT NULL DEFAULT FALSE,
    sent_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (sender_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (recipient_id) REFERENCES users(id) ON DELETE CASCADE
);

-- 16. Support Tickets Table
CREATE TABLE IF NOT EXISTS support_tickets (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    created_by BIGINT NOT NULL,
    title VARCHAR(150) NOT NULL,
    description TEXT NOT NULL,
    priority VARCHAR(20) NOT NULL DEFAULT 'MEDIUM',
    status VARCHAR(20) NOT NULL DEFAULT 'OPEN',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (created_by) REFERENCES users(id) ON DELETE CASCADE
);

-- 17. Ticket Replies Table
CREATE TABLE IF NOT EXISTS ticket_replies (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    ticket_id BIGINT NOT NULL,
    author_id BIGINT NOT NULL,
    message TEXT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (ticket_id) REFERENCES support_tickets(id) ON DELETE CASCADE,
    FOREIGN KEY (author_id) REFERENCES users(id) ON DELETE CASCADE
);

-- 18. System Settings Table
CREATE TABLE IF NOT EXISTS system_settings (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    setting_key VARCHAR(100) NOT NULL UNIQUE,
    setting_value VARCHAR(255) NOT NULL,
    description VARCHAR(255),
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 19. Audit Logs Table
CREATE TABLE IF NOT EXISTS audit_logs (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT,
    action VARCHAR(100) NOT NULL,
    details TEXT,
    ip_address VARCHAR(45),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL
);

-- Useful Indexing for Performance
CREATE INDEX idx_users_role ON users(role);
CREATE INDEX idx_sessions_condition ON therapy_sessions(condition_type);
CREATE INDEX idx_results_patient ON session_results(patient_id);
CREATE INDEX idx_devices_code ON vr_devices(pairing_code);
CREATE INDEX idx_telemetry_live ON telemetry_samples(live_session_id);
