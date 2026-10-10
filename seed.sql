-- ==============================================================================
-- Virtual Reality Therapy Sessions: Connected VR Mental Health Platform
-- Seed Data for Testing and Demonstration
-- Default Passwords:
-- admin / admin123
-- therapist1, therapist2, patient1, patient2, etc. / password123
-- ==============================================================================

-- 1. Users (1 Admin, 2 Therapists, 5 Patients)
-- BCrypt for 'admin123': $2a$10$qR6eQ7O6pL4U0.7u4I1Oue6J1aF4Xn2G3e.dM3jJ7V2O2z.9h1G.S
-- BCrypt for 'password123': $2a$10$O0Wc8mXyYvYv9QyM8K8X..n2U1X.vX9L2QyK3U4v1X.yYvYv9QyM8
INSERT INTO users (id, username, password, full_name, email, role, active, phone, clinical_bio_or_notes, assigned_therapist_id, emergency_contact, baseline_anxiety, license_number, specialization, department) VALUES
(1, 'admin', '$2a$10$qR6eQ7O6pL4U0.7u4I1Oue6J1aF4Xn2G3e.dM3jJ7V2O2z.9h1G.S', 'System Administrator', 'admin@vrtherapy.org', 'ADMIN', TRUE, '+1-555-0100', 'Chief Systems Administrator and Security Officer', NULL, NULL, NULL, NULL, NULL, 'Clinical IT & Operations'),
(2, 'dr_sarah', '$2a$10$O0Wc8mXyYvYv9QyM8K8X..n2U1X.vX9L2QyK3U4v1X.yYvYv9QyM8', 'Dr. Sarah Jenkins, PsyD', 'sarah.jenkins@vrtherapy.org', 'THERAPIST', TRUE, '+1-555-0102', 'Clinical Psychologist specializing in Anxiety Disorders, PTSD, and Exposure Therapy.', NULL, NULL, NULL, 'PSY-90214-CA', 'Exposure Therapy & PTSD', NULL),
(3, 'dr_marcus', '$2a$10$O0Wc8mXyYvYv9QyM8K8X..n2U1X.vX9L2QyK3U4v1X.yYvYv9QyM8', 'Dr. Marcus Vance, MD', 'marcus.vance@vrtherapy.org', 'THERAPIST', TRUE, '+1-555-0103', 'Neuropsychiatrist focusing on Panic Disorders, Social Phobia, and Somatosensory Regulation.', NULL, NULL, NULL, 'MED-48192-NY', 'Phobias & Neuro-Regulation', NULL),
(4, 'alex_turner', '$2a$10$O0Wc8mXyYvYv9QyM8K8X..n2U1X.vX9L2QyK3U4v1X.yYvYv9QyM8', 'Alex Turner', 'alex.turner@example.com', 'PATIENT', TRUE, '+1-555-0201', 'Referred for Generalized Anxiety Disorder and Acrophobia (fear of heights).', 2, 'Jane Turner (+1-555-9001, Mother)', 6, NULL, NULL, NULL),
(5, 'emma_watson', '$2a$10$O0Wc8mXyYvYv9QyM8K8X..n2U1X.vX9L2QyK3U4v1X.yYvYv9QyM8', 'Emma Watson', 'emma.watson@example.com', 'PATIENT', TRUE, '+1-555-0202', 'Referred for Social Anxiety Disorder with avoidance behaviors in public speaking.', 2, 'David Watson (+1-555-9002, Spouse)', 7, NULL, NULL, NULL),
(6, 'david_beck', '$2a$10$O0Wc8mXyYvYv9QyM8K8X..n2U1X.vX9L2QyK3U4v1X.yYvYv9QyM8', 'David Beck', 'david.beck@example.com', 'PATIENT', TRUE, '+1-555-0203', 'Undergoing trauma-informed therapy following motor vehicle accident (PTSD).', 3, 'Sarah Beck (+1-555-9003, Sister)', 8, NULL, NULL, NULL),
(7, 'sophia_chen', '$2a$10$O0Wc8mXyYvYv9QyM8K8X..n2U1X.vX9L2QyK3U4v1X.yYvYv9QyM8', 'Sophia Chen', 'sophia.chen@example.com', 'PATIENT', TRUE, '+1-555-0204', 'Experiencing severe work-related chronic stress and acute insomnia.', 3, 'Robert Chen (+1-555-9004, Father)', 5, NULL, NULL, NULL),
(8, 'james_wilson', '$2a$10$O0Wc8mXyYvYv9QyM8K8X..n2U1X.vX9L2QyK3U4v1X.yYvYv9QyM8', 'James Wilson', 'james.wilson@example.com', 'PATIENT', TRUE, '+1-555-0205', 'Mild depressive symptoms and claustrophobia; responsive to calming nature environments.', 2, 'Mary Wilson (+1-555-9005, Spouse)', 4, NULL, NULL, NULL);

-- 2. Therapy Sessions (6 Standard VR Protocol Templates)
INSERT INTO therapy_sessions (id, title, condition_type, vr_environment, default_duration_minutes, intensity_level, audio_enabled, guided_breathing_enabled, therapist_notes, config_json, created_by) VALUES
(1, 'Coastal Respite & Diaphragmatic Cadence', 'Anxiety', 'Calm Beach', 15, 1, TRUE, TRUE, 'Gentle ocean swell pacing 4-7-8 breathing cycles. Recommended for baseline regulation.', '{"skyColor":"#80deea","groundColor":"#ffe082","waveSpeed":1.0,"breathingGuidePace":4}', 2),
(2, 'Arboreal Mindful Walk & Sensory Grounding', 'Stress', 'Forest Walk', 20, 2, TRUE, TRUE, 'Binaural wind in pine canopies with dappled sunlight. Great for tactile and auditory grounding.', '{"skyColor":"#b2dfdb","foliageDensity":5,"ambientBirds":true}', 2),
(3, 'Progressive High-Rise Elevation Exposure', 'Phobia', 'Heights Exposure', 12, 3, TRUE, FALSE, 'Exposure module overlooking glass balconies. Unlock only if distress under level 4.', '{"altitudeMeters":45,"railingVisible":true,"windAudioLevel":0.6}', 2),
(4, 'Virtual Auditorium Social Public Speaking', 'Social Gathering', 'Social Gathering', 15, 3, TRUE, TRUE, 'Simulated seminar room with 12 avatar spectators. Useful for desensitization.', '{"audienceCount":12,"audienceReactions":"neutral","podiumLight":true}', 3),
(5, 'Somatic Chamber: Guided Coherence Sphere', 'Panic', 'Breathing Room', 10, 1, TRUE, TRUE, 'Minimalist sanctuary focusing solely on a breathing sphere that expands and contracts.', '{"sphereColor":"#26a69a","expansionDuration":4.0,"holdDuration":2.0}', 3),
(6, 'Nocturnal Celestial Slumber Immersion', 'Insomnia', 'Night Sky Sleep Room', 25, 1, TRUE, TRUE, 'Starlit zenith with slow constellation drifting and 432Hz sleep soundscape.', '{"starCount":800,"nebulaIntensity":0.4,"ambientFrequency":432}', 3);

-- 3. Therapy Plans
INSERT INTO therapy_plans (id, patient_id, therapist_id, title, condition_type, total_weeks, status, goals) VALUES
(1, 4, 2, 'Graduated Acrophobia Desensitization Plan', 'Phobia', 6, 'ACTIVE', 'Reduce somatic distress from 8/10 to under 4/10 when exposed to heights above 30 meters.'),
(2, 5, 2, 'Social Confidence and Audience Desensitization', 'Social Gathering', 8, 'ACTIVE', 'Overcome anticipatory tachycardia and maintain relaxed speech in group settings.');

-- 4. Exposure Hierarchy Levels
INSERT INTO exposure_levels (id, plan_id, session_id, level_order, title, description, distress_rating_threshold, is_unlocked, notes) VALUES
(1, 1, 1, 1, 'Virtual Beach Grounding & Breath Coherence', 'Establish parasympathetic relaxation anchor before elevation tasks.', 4, TRUE, 'Patient completed baseline calibration successfully.'),
(2, 1, 3, 2, 'Low Balcony Vantage (10 Meters)', 'Look down through safety glass railing with therapist audio guidance.', 5, TRUE, 'Stress dropped to 3 after 8 minutes.'),
(3, 1, 3, 3, 'Mid-Rise Observation Deck (45 Meters)', 'Step onto open terrace with gentle wind effects.', 4, FALSE, 'Locked until patient consistently rates Level 2 <= 4.'),
(4, 1, 3, 4, 'High-Rise Glass Platform (100 Meters)', 'Full open glass floor over cityscape.', 3, FALSE, 'Peak exposure milestone.'),
(5, 2, 5, 1, 'Sanctuary Somatic Breathing Calibration', 'Master paced respiration before social exposure.', 4, TRUE, 'Completed with excellent compliance.'),
(6, 2, 4, 2, 'Small Seminar Group (3 Listeners)', 'Deliver a 3-minute self-introduction to attentive avatars.', 5, TRUE, 'Level unlocked and active.');

-- 5. Session Assignments
INSERT INTO session_assignments (id, patient_id, session_id, plan_id, status, scheduled_date) VALUES
(1, 4, 1, 1, 'COMPLETED', '2026-10-01'),
(2, 4, 3, 1, 'COMPLETED', '2026-10-05'),
(3, 4, 3, 1, 'SCHEDULED', '2026-10-12'),
(4, 5, 5, 2, 'COMPLETED', '2026-10-02'),
(5, 5, 4, 2, 'SCHEDULED', '2026-10-14'),
(6, 6, 2, NULL, 'COMPLETED', '2026-10-03'),
(7, 7, 6, NULL, 'COMPLETED', '2026-10-04');

-- 6. Live Sessions
INSERT INTO live_sessions (id, session_id, patient_id, therapist_id, state, current_environment, current_intensity, breathing_guide_active, elapsed_seconds, current_heart_rate, current_stress_score, in_vr_message) VALUES
(1, 1, 4, 2, 'COMPLETED', 'Calm Beach', 1, TRUE, 900, 68, 2, 'Excellent breath pacing, Alex.'),
(2, 3, 4, 2, 'COMPLETED', 'Heights Exposure', 2, FALSE, 720, 78, 4, 'Focus on the horizon and let your shoulders drop.'),
(3, 5, 5, 2, 'COMPLETED', 'Breathing Room', 1, TRUE, 600, 72, 3, 'Inhale as the orb expands.'),
(4, 2, 6, 3, 'COMPLETED', 'Forest Walk', 2, TRUE, 1200, 74, 3, 'Notice the rustle of the trees.');

-- 7. Session Results (10 Results for rich analytics)
INSERT INTO session_results (id, session_id, patient_id, pre_stress_score, post_stress_score, duration_seconds, average_heart_rate, peak_heart_rate, emergency_stopped, stop_reason, completion_notes, completed_at) VALUES
(1, 1, 4, 8, 3, 900, 71, 84, FALSE, NULL, 'Remarkable de-escalation; patient engaged deeply with the ocean audio.', '2026-10-01 10:15:00'),
(2, 3, 4, 7, 4, 720, 82, 102, FALSE, NULL, 'Mild elevation panic at minute 4, effectively mitigated with breathing prompts.', '2026-10-05 14:30:00'),
(3, 1, 4, 6, 2, 900, 69, 78, FALSE, NULL, 'Smooth regulation session; vital signs stable throughout.', '2026-10-08 11:00:00'),
(4, 5, 5, 8, 4, 600, 75, 88, FALSE, NULL, 'Heart rate variability improved notably during the 2nd half.', '2026-10-02 09:30:00'),
(5, 4, 5, 9, 6, 900, 89, 114, FALSE, NULL, 'Anticipatory anxiety spike; recommended continuing with small-group exposure.', '2026-10-06 16:00:00'),
(6, 2, 6, 8, 3, 1200, 73, 85, FALSE, NULL, 'Reported significant sensory calming from forest lighting.', '2026-10-03 15:00:00'),
(7, 2, 6, 7, 3, 1200, 70, 81, FALSE, NULL, 'Consistent positive response; somatic flashbacks reduced.', '2026-10-07 15:30:00'),
(8, 6, 7, 7, 2, 1500, 64, 73, FALSE, NULL, 'Fell into deep relaxation state by minute 18.', '2026-10-04 21:00:00'),
(9, 6, 7, 6, 2, 1500, 62, 70, FALSE, NULL, 'Subjective sleep latency improved noticeably following session.', '2026-10-07 21:30:00'),
(10, 1, 8, 5, 2, 900, 66, 74, FALSE, NULL, 'Calm response, no adverse reactions.', '2026-10-06 11:30:00');

-- 8. Feedbacks
INSERT INTO feedbacks (id, result_id, patient_id, session_id, rating, comfort_score, nausea_score, comments, applied_adjustment) VALUES
(1, 1, 4, 1, 5, 5, 1, 'The crashing waves helped me sync my breathing almost instantly.', 'Keep default audio gain at 80%'),
(2, 2, 4, 3, 4, 4, 2, 'My palms were sweating initially, but therapist prompts helped ground me.', 'Enable breathing guide during height transitions'),
(3, 4, 5, 5, 5, 5, 1, 'The expanding orb gave me an anchor when racing thoughts started.', 'Maintained cadence 4-2-4'),
(4, 5, 5, 4, 3, 3, 2, 'The avatar eyes felt a bit intense at the start.', 'Lower audience eye contact frequency'),
(5, 6, 6, 2, 5, 5, 1, 'I felt genuine safety in the wooded setting for the first time in months.', 'Extend session duration to 25 mins'),
(6, 8, 7, 6, 5, 5, 1, 'The drifting stars and binaural chords helped me unhook from work worries.', 'Scheduled as weekly pre-sleep routine');

-- 9. VR Devices
INSERT INTO vr_devices (id, device_name, device_type, pairing_code, code_expires_at, status, last_heartbeat, patient_id, ip_address, is_blocked) VALUES
(1, 'Meta Quest 3 (Clinical Unit 01)', 'HEADSET', '842910', '2026-10-15 12:00:00', 'CONNECTED', CURRENT_TIMESTAMP, 4, '192.168.1.45', FALSE),
(2, 'Chrome WebXR Browser (Alex Laptop)', 'BROWSER', '193852', '2026-10-15 12:00:00', 'CONNECTED', CURRENT_TIMESTAMP, 4, '192.168.1.101', FALSE),
(3, 'Meta Quest 2 (Clinical Unit 02)', 'HEADSET', '572941', '2026-10-15 12:00:00', 'DISCONNECTED', '2026-10-08 16:30:00', 5, '192.168.1.48', FALSE);

-- 10. Mood Entries
INSERT INTO mood_entries (id, patient_id, score, primary_emotion, notes, recorded_at) VALUES
(1, 4, 4, 'Anxious', 'Work deadlines causing tight chest and shallow breathing.', '2026-10-01 08:30:00'),
(2, 4, 7, 'Calm', 'Felt relief after morning VR beach session.', '2026-10-01 11:30:00'),
(3, 4, 6, 'Optimistic', 'Ready to attempt level 2 height exposure.', '2026-10-05 09:00:00'),
(4, 5, 3, 'Overwhelmed', 'Presentation rehearsal triggered racing heartbeat.', '2026-10-02 08:00:00'),
(5, 5, 7, 'Confident', 'Completed breathing room with low somatic tension.', '2026-10-02 10:30:00');

-- 11. Thought Records (CBT)
INSERT INTO thought_records (id, patient_id, situation, automatic_thought, cognitive_distortion, rational_response, outcome_belief) VALUES
(1, 4, 'Standing on a 3rd-floor glass balcony at the clinic', 'The glass is going to crack and I have zero control.', 'Catastrophizing', 'The structure is rated for thousands of pounds; my sensations are adrenaline, not actual physical danger.', 30),
(2, 5, 'Asked to introduce myself in the upcoming VR conference room', 'Everyone will see my hands shaking and think I am incompetent.', 'Mind Reading & Fortune Telling', 'People are focused on the content, and mild nervousness is completely normal and human.', 35);

-- 12. Homework Assignments
INSERT INTO homework (id, patient_id, therapist_id, title, description, due_date, completed, reminder_sent) VALUES
(1, 4, 2, 'Daily 10-minute Paced Breathing', 'Practice the 4-7-8 breathing sequence twice daily before breakfast and bed.', '2026-10-14', TRUE, TRUE),
(2, 4, 2, 'Height Exposure Sensory Journal', 'Log 3 instances of looking down from stairs without avoiding looking.', '2026-10-16', FALSE, FALSE),
(3, 5, 2, 'Thought Record on Public Speaking', 'Record automatic thoughts before each team meeting.', '2026-10-15', TRUE, TRUE);

-- 13. Badges (Gamification & Adherence)
INSERT INTO badges (id, patient_id, badge_type, title, description, icon_name) VALUES
(1, 4, 'FIRST_SESSION', 'First Step Taken', 'Completed your first immersion session in VR.', 'compass'),
(2, 4, 'FIVE_SESSIONS', 'Persistence Pioneer', 'Completed 5 clinical exposure sessions.', 'award'),
(3, 4, 'CALM_MASTER', 'Heart Coherence', 'Achieved a post-stress score reduction of 50% or more.', 'heart'),
(4, 5, 'FIRST_SESSION', 'First Step Taken', 'Completed your first immersion session in VR.', 'compass');

-- 14. Messages
INSERT INTO messages (id, sender_id, recipient_id, content, is_read, sent_at) VALUES
(1, 2, 4, 'Hello Alex, excellent progress on the beach session yesterday. Review your homework before Friday.', TRUE, '2026-10-02 09:00:00'),
(2, 4, 2, 'Thank you Dr. Sarah! The breathing guide has really helped in stressful morning traffic.', TRUE, '2026-10-02 11:20:00'),
(3, 2, 5, 'Emma, we will begin the small audience exposure next Tuesday. Take your time with the thought record.', FALSE, '2026-10-08 14:00:00');

-- 15. Support Tickets & Replies
INSERT INTO support_tickets (id, created_by, title, description, priority, status) VALUES
(1, 4, 'Quest 3 Lens Recalibration Notice', 'The right eyepiece had slight blur during the height balcony scene.', 'LOW', 'RESOLVED'),
(2, 2, 'Therapy Room Audio Artifact on Mac Chrome', 'Audio crackling observed when WebXR runs in Chrome on macOS Sonoma.', 'MEDIUM', 'IN_PROGRESS'),
(3, 1, 'Bi-weekly Telemetry Database Backup Verification', 'Routine check of telemetry time-series partition performance.', 'HIGH', 'OPEN');

INSERT INTO ticket_replies (id, ticket_id, author_id, message) VALUES
(1, 1, 1, 'Alex, please wipe the proximity sensor with a microfiber cloth and verify IPD slider is at 64mm.'),
(2, 1, 4, 'That fixed the alignment completely, thank you!'),
(3, 2, 1, 'Engineering is investigating Web Audio API buffer size on Chromium 128.');

-- 16. System Settings
INSERT INTO system_settings (setting_key, setting_value, description) VALUES
('max_session_duration_minutes', '45', 'Maximum permissible continuous VR immersion duration'),
('maintenance_mode', 'false', 'Global maintenance switch for patient portals'),
('stress_alert_threshold', '8', 'Real-time distress score triggering therapist crisis intervention'),
('heartbeat_timeout_seconds', '30', 'Interval before inactivity flags a VR headset as disconnected'),
('guided_breathing_default_bpm', '6', 'Default respiration cadence for relaxation environments');

-- 17. Audit Logs
INSERT INTO audit_logs (id, user_id, action, details, ip_address) VALUES
(1, 1, 'SYSTEM_STARTUP', 'Platform initialized successfully on Apache Tomcat 10.1', '127.0.0.1'),
(2, 2, 'SESSION_CREATED', 'Dr. Sarah created session "Coastal Respite & Diaphragmatic Cadence"', '192.168.1.20'),
(3, 4, 'DEVICE_PAIRED', 'Alex paired Meta Quest 3 with code 842910', '192.168.1.45');
