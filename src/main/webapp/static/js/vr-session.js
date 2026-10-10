/**
 * Virtual Reality Session Runtime Controller
 * Governs session lifecycle, pre/post stress evaluations, timer HUD, and REST telemetry dispatch.
 */

let sessionState = {
    assignmentId: null,
    durationMinutes: 20,
    startTime: null,
    timerInterval: null,
    preStressRating: 5,
    postStressRating: 5,
    simulatedHeartRate: 72,
    heartRateInterval: null,
    audioContext: null
};

function initVrSession(assignmentId, durationMinutes, config) {
    sessionState.assignmentId = assignmentId;
    sessionState.durationMinutes = durationMinutes;

    // Show pre-session stress prompt dialog
    const preModal = document.getElementById('pre-stress-modal');
    if (preModal) {
        preModal.style.display = 'flex';
    }

    // Attach listeners
    document.getElementById('start-session-btn')?.addEventListener('click', () => {
        const preVal = parseInt(document.getElementById('pre-stress-slider').value, 10);
        sessionState.preStressRating = preVal;
        preModal.style.display = 'none';
        startSessionRuntime(config);
    });

    document.getElementById('finish-session-btn')?.addEventListener('click', () => {
        showPostSessionModal();
    });

    document.getElementById('submit-result-btn')?.addEventListener('click', () => {
        submitSessionResult();
    });
}

function startSessionRuntime(config) {
    sessionState.startTime = Date.now();
    const hud = document.getElementById('session-hud');
    if (hud) hud.style.display = 'flex';

    // Build procedural 3D scene
    const sceneEl = document.querySelector('a-scene');
    if (sceneEl) {
        if (sceneEl.hasLoaded) {
            buildVrScene(sceneEl, config);
        } else {
            sceneEl.addEventListener('loaded', () => buildVrScene(sceneEl, config));
        }
    }

    // Start timer HUD
    let totalSeconds = sessionState.durationMinutes * 60;
    let elapsedSeconds = 0;

    sessionState.timerInterval = setInterval(() => {
        elapsedSeconds++;
        const mins = Math.floor(elapsedSeconds / 60).toString().padStart(2, '0');
        const secs = (elapsedSeconds % 60).toString().padStart(2, '0');
        const timerEl = document.getElementById('hud-timer');
        if (timerEl) timerEl.textContent = `${mins}:${secs}`;
    }, 1000);

    // Heart rate telemetry simulation
    sessionState.heartRateInterval = setInterval(() => {
        const drift = (Math.random() - 0.5) * 4;
        sessionState.simulatedHeartRate = Math.round(Math.max(60, Math.min(105, sessionState.simulatedHeartRate + drift)));
        const hrEl = document.getElementById('hud-hr');
        if (hrEl) hrEl.textContent = `${sessionState.simulatedHeartRate} bpm`;
    }, 2000);
}

function showPostSessionModal() {
    clearInterval(sessionState.timerInterval);
    clearInterval(sessionState.heartRateInterval);

    const postModal = document.getElementById('post-stress-modal');
    if (postModal) postModal.style.display = 'flex';
}

function submitSessionResult() {
    const postVal = parseInt(document.getElementById('post-stress-slider').value, 10);
    sessionState.postStressRating = postVal;

    const elapsedMs = Date.now() - (sessionState.startTime || Date.now());
    const completedMinutes = Math.max(1, Math.round(elapsedMs / 60000));

    const payload = {
        assignmentId: sessionState.assignmentId,
        durationMinutes: completedMinutes,
        preStressRating: sessionState.preStressRating,
        postStressRating: sessionState.postStressRating,
        averageHeartRate: sessionState.simulatedHeartRate,
        peakHeartRate: sessionState.simulatedHeartRate + 8,
        clinicalNotes: "WebXR immersion session concluded successfully."
    };

    const submitBtn = document.getElementById('submit-result-btn');
    if (submitBtn) {
        submitBtn.disabled = true;
        submitBtn.textContent = 'Saving Telemetry...';
    }

    fetch('/api/session/result', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(payload)
    })
    .then(res => {
        if (!res.ok) throw new Error('Submission failed');
        return res.json();
    })
    .then(data => {
        // Direct transition to patient subjective feedback form
        window.location.href = data.feedbackUrl || ('/patient/feedback/' + sessionState.assignmentId);
    })
    .catch(err => {
        console.error('Error submitting result:', err);
        alert('Failed to save session telemetry. Redirecting to feedback...');
        window.location.href = '/patient/feedback/' + sessionState.assignmentId;
    });
}
