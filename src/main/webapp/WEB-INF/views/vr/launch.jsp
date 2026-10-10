<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0, user-scalable=no">
    <title>${sessionTemplate.title} - Virtual Reality Session</title>
    <!-- A-Frame VR Library with WebXR support -->
    <script src="https://aframe.io/releases/1.5.0/aframe.min.js"></script>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/style.css">
    <style>
        body { margin: 0; overflow: hidden; background: #000; font-family: system-ui, -apple-system, sans-serif; }
        .hud-overlay {
            position: fixed;
            top: 20px;
            left: 20px;
            z-index: 999;
            background: rgba(15, 23, 42, 0.85);
            backdrop-filter: blur(8px);
            padding: 1rem 1.5rem;
            border-radius: 12px;
            color: white;
            border: 1px solid rgba(255,255,255,0.1);
            pointer-events: auto;
        }
        .emergency-btn {
            position: fixed;
            bottom: 30px;
            right: 30px;
            z-index: 999;
            background: #dc2626;
            color: white;
            border: 2px solid #ef4444;
            padding: 1rem 1.75rem;
            border-radius: 9999px;
            font-size: 1.1rem;
            font-weight: 800;
            cursor: pointer;
            box-shadow: 0 10px 25px -5px rgba(220, 38, 38, 0.5);
            transition: transform 0.15s, background 0.15s;
        }
        .emergency-btn:hover { background: #b91c1c; transform: scale(1.05); }
        .modal-overlay {
            position: fixed;
            inset: 0;
            background: rgba(0,0,0,0.85);
            backdrop-filter: blur(6px);
            z-index: 1000;
            display: flex;
            align-items: center;
            justify-content: center;
            padding: 1rem;
        }
        .modal-card {
            background: #ffffff;
            border-radius: 16px;
            padding: 2.25rem;
            max-width: 520px;
            width: 95%;
            text-align: center;
            color: #0f172a;
            box-shadow: 0 20px 25px -5px rgba(0,0,0,0.3);
            max-height: 90vh;
            overflow-y: auto;
        }
        .readiness-grid {
            display: grid;
            grid-template-columns: 1fr 1fr;
            gap: 0.75rem;
            margin: 1.25rem 0;
            text-align: left;
        }
        .readiness-item {
            background: #f8fafc;
            border: 1px solid #e2e8f0;
            padding: 0.75rem;
            border-radius: 8px;
            font-size: 0.8rem;
        }
        .readiness-label {
            font-weight: 600;
            color: #475569;
            display: block;
            margin-bottom: 0.2rem;
        }
        .readiness-val {
            font-weight: 700;
        }
    </style>
</head>
<body>

<!-- INSECURE ORIGIN MODAL (shown if HTTP on LAN) -->
<div id="insecureOriginModal" class="modal-overlay" style="display: none;">
    <div class="modal-card" style="border-top: 5px solid #f59e0b;">
        <div style="font-size: 3rem; margin-bottom: 0.5rem;">🔒</div>
        <h2 style="font-size: 1.35rem; font-weight: 700; color: #1e293b; margin-bottom: 0.5rem;">
            WebXR &amp; Sensor Security Notice
        </h2>
        <p style="color: #64748b; font-size: 0.875rem; line-height: 1.5; margin-bottom: 1.25rem;">
            Modern mobile browsers (Chrome on Android, Safari on iOS, Meta Quest Browser) require an <strong>encrypted HTTPS origin</strong>
            to enter stereoscopic VR mode and grant access to device orientation/gyroscope sensors when accessed over a local network IP.
        </p>

        <div style="background: #eff6ff; border: 1px solid #bfdbfe; border-radius: 8px; padding: 1rem; margin-bottom: 1.5rem; text-align: left; font-size: 0.825rem; color: #1e40af;">
            <strong>Recommended for full VR:</strong> Switch to the secure HTTPS server on port <strong>8443</strong>. You will see a self-signed certificate warning: tap <em>"Advanced" &rarr; "Proceed"</em>.
        </div>

        <div style="display: flex; flex-direction: column; gap: 0.75rem;">
            <button id="btnOpenSecure" onclick="redirectToHttps()"
                    style="width: 100%; padding: 0.85rem; background: #0284c7; color: white; border: none; border-radius: 8px; font-weight: 700; font-size: 0.95rem; cursor: pointer;">
                🔒 Open Secure Version (HTTPS :8443)
            </button>
            <button onclick="dismissInsecureWarning()"
                    style="width: 100%; padding: 0.75rem; background: #f1f5f9; color: #334155; border: 1px solid #cbd5e1; border-radius: 8px; font-weight: 600; font-size: 0.9rem; cursor: pointer;">
                🌐 Continue in 360° Touch/Mouse Mode (No Sensors)
            </button>
            <a href="${pageContext.request.contextPath}/simulator"
               style="text-decoration: none; padding: 0.75rem; background: #f8fafc; color: #64748b; border: 1px solid #e2e8f0; border-radius: 8px; font-weight: 600; font-size: 0.85rem; display: block;">
                💻 Open VR Headset Simulator Mode
            </a>
        </div>
    </div>
</div>

<!-- Pre-Session Rating & Hardware Readiness Modal -->
<div id="preSessionModal" class="modal-overlay">
    <div class="modal-card">
        <h2 style="font-size: 1.4rem; font-weight: 700; margin-bottom: 0.5rem;">Pre-Session Readiness</h2>
        <p style="color: #64748b; font-size: 0.875rem; margin-bottom: 1rem;">
            Before entering VR, rate your current anxiety or stress level from 1 (Completely Relaxed) to 10 (Extreme Distress).
        </p>

        <!-- Hardware Readiness Matrix -->
        <div class="readiness-grid">
            <div class="readiness-item">
                <span class="readiness-label">Security Origin</span>
                <span id="readinessSecurity" class="readiness-val" style="color: #64748b;">Checking...</span>
            </div>
            <div class="readiness-item">
                <span class="readiness-label">WebXR Support</span>
                <span id="readinessXr" class="readiness-val" style="color: #64748b;">Checking...</span>
            </div>
            <div class="readiness-item">
                <span class="readiness-label">Motion Sensors</span>
                <span id="readinessSensors" class="readiness-val" style="color: #64748b;">Checking...</span>
            </div>
            <div class="readiness-item">
                <span class="readiness-label">Spatial Audio</span>
                <span id="readinessAudio" class="readiness-val" style="color: #10b981;">✅ Ready</span>
            </div>
        </div>

        <div style="margin-bottom: 1.5rem;">
            <div id="preStressDisplay" style="font-size: 3rem; font-weight: 800; color: #0284c7; margin-bottom: 0.25rem;">5</div>
            <input type="range" id="preStressInput" min="1" max="10" value="5"
                   style="width: 100%; height: 8px; border-radius: 4px; accent-color: #0284c7;"
                   oninput="document.getElementById('preStressDisplay').innerText = this.value" />
            <div style="display: flex; justify-content: space-between; font-size: 0.75rem; color: #64748b; margin-top: 0.25rem;">
                <span>1 - Peaceful</span>
                <span>5 - Moderate</span>
                <span>10 - Severe</span>
            </div>
        </div>

        <button onclick="requestSensorsAndStart()" style="width: 100%; padding: 0.85rem; background: #0284c7; color: white; border: none; border-radius: 8px; font-weight: 700; font-size: 1rem; cursor: pointer;">
            Enter Virtual Reality &rarr;
        </button>
    </div>
</div>

<!-- Post-Session Rating Modal (hidden until complete) -->
<div id="postSessionModal" class="modal-overlay" style="display: none;">
    <div class="modal-card">
        <h2 style="font-size: 1.4rem; font-weight: 700; margin-bottom: 0.5rem;">Session Concluded</h2>
        <p style="color: #64748b; font-size: 0.9rem; margin-bottom: 1.5rem;">
            Rate your post-session stress level to calculate your therapeutic desensitization score.
        </p>

        <form action="${pageContext.request.contextPath}/vr/launch" method="post">
            <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}" />
            <input type="hidden" name="sessionId" value="${sessionTemplate.id}" />
            <input type="hidden" id="formPreStress" name="preStressScore" value="5" />
            <input type="hidden" id="formDuration" name="durationSeconds" value="0" />
            <input type="hidden" id="formAvgHr" name="avgHeartRate" value="72" />
            <input type="hidden" id="formPeakHr" name="peakHeartRate" value="84" />
            <input type="hidden" id="formEmergency" name="emergencyStopped" value="false" />
            <input type="hidden" id="formReason" name="stopReason" value="" />

            <div style="margin-bottom: 1.5rem;">
                <div id="postStressDisplay" style="font-size: 3rem; font-weight: 800; color: #10b981; margin-bottom: 0.5rem;">3</div>
                <input type="range" name="postStressScore" min="1" max="10" value="3"
                       style="width: 100%; height: 8px; border-radius: 4px; accent-color: #10b981;"
                       oninput="document.getElementById('postStressDisplay').innerText = this.value" />
            </div>

            <div style="margin-bottom: 1.5rem; text-align: left;">
                <label style="display: block; font-size: 0.85rem; font-weight: 600; color: #334155; margin-bottom: 0.35rem;">Subjective Experience Notes</label>
                <textarea name="completionNotes" rows="2" placeholder="How did this session feel?"
                          style="width: 100%; padding: 0.5rem; border: 1px solid #cbd5e1; border-radius: 6px; font-size: 0.85rem;"></textarea>
            </div>

            <button type="submit" style="width: 100%; padding: 0.85rem; background: #10b981; color: white; border: none; border-radius: 8px; font-weight: 700; font-size: 1rem; cursor: pointer;">
                Save Results &amp; View Summary &rarr;
            </button>
        </form>
    </div>
</div>

<!-- 2D HUD Info Overlay -->
<div class="hud-overlay">
    <div style="font-weight: 700; font-size: 1rem;">${sessionTemplate.title}</div>
    <div style="font-size: 0.8rem; color: #94a3b8; margin-top: 0.25rem;">
        Environment: <strong>${sessionTemplate.vrEnvironment.displayName}</strong> &bull; Intensity: <strong>${sessionTemplate.intensityLevel}/5</strong>
    </div>
    <div style="font-size: 1.4rem; font-weight: 800; color: #38bdf8; margin-top: 0.5rem;" id="hudTimer">00:00</div>
    <div id="hudPrompt" style="display: none; background: rgba(99, 102, 241, 0.3); border: 1px solid #6366f1; padding: 0.5rem; border-radius: 6px; font-size: 0.85rem; margin-top: 0.5rem; color: #e0e7ff;"></div>
</div>

<!-- EMERGENCY STOP BUTTON -->
<button class="emergency-btn" onclick="triggerEmergencyStop()">
    🛑 I NEED TO STOP
</button>

<!-- A-FRAME 3D WEBXR SCENE -->
<a-scene embedded style="width: 100vw; height: 100vh;">
    <!-- Procedural Sky -->
    <a-sky id="vrSky" color="#87ceeb"></a-sky>

    <!-- Procedural Terrain / Ground Platform -->
    <a-plane id="vrGround" position="0 0 -4" rotation="-90 0 0" width="100" height="100" color="#e0f2fe"></a-plane>

    <!-- Animated Guided Breathing Sphere -->
    <c:if test="${sessionTemplate.guidedBreathingEnabled}">
        <a-sphere id="breathingSphere" position="0 1.6 -3" radius="0.4" color="#0d9488" material="opacity: 0.8; transparent: true">
            <a-animation attribute="scale"
                         dur="4000"
                         from="1 1 1"
                         to="1.8 1.8 1.8"
                         direction="alternate"
                         repeat="indefinite"
                         easing="ease-in-out">
            </a-animation>
        </a-sphere>
    </c:if>

    <!-- Ambient Lighting -->
    <a-light type="ambient" color="#ffffff" intensity="0.8"></a-light>
    <a-light type="directional" position="-1 2 1" intensity="0.5"></a-light>

    <!-- Camera Rig with Headset Tracking & Desktop Controls -->
    <a-entity id="rig" position="0 1.6 0">
        <a-camera id="mainCamera" look-controls="touchEnabled: true; mouseEnabled: true" wasd-controls position="0 0 0">
            <!-- In-VR Text HUD Anchor for Therapist Live Prompts -->
            <a-text id="vrHudText" value="" position="0 0.4 -1.5" align="center" color="#ffffff" width="2" visible="false"></a-text>
        </a-camera>
    </a-entity>
</a-scene>

<script>
let startTime = 0;
let timerInterval = null;
let elapsedSeconds = 0;
const isLocalhost = (location.hostname === 'localhost' || location.hostname === '127.0.0.1');
const isSecure = window.isSecureContext || location.protocol === 'https:';

// 1. Initial Origin & Capabilities Verification
function runReadinessCheck() {
    // Security check
    const secEl = document.getElementById('readinessSecurity');
    if (isSecure) {
        secEl.innerText = isLocalhost ? '✅ Localhost (Secure)' : '✅ HTTPS (Secure)';
        secEl.style.color = '#10b981';
    } else {
        secEl.innerText = '⚠️ Insecure HTTP';
        secEl.style.color = '#f59e0b';
    }

    // WebXR check
    const xrEl = document.getElementById('readinessXr');
    if (navigator.xr) {
        navigator.xr.isSessionSupported('immersive-vr')
            .then(supported => {
                xrEl.innerText = supported ? '✅ Immersive VR Ready' : 'ℹ️ 360° Non-VR Ready';
                xrEl.style.color = supported ? '#10b981' : '#0284c7';
            })
            .catch(() => {
                xrEl.innerText = 'ℹ️ 360° Orbit Mode';
                xrEl.style.color = '#64748b';
            });
    } else {
        xrEl.innerText = isSecure ? 'ℹ️ 360° Drag Look' : '⚠️ Blocked by HTTP';
        xrEl.style.color = isSecure ? '#0284c7' : '#ef4444';
    }

    // Sensor check
    const sensorEl = document.getElementById('readinessSensors');
    if (window.DeviceOrientationEvent || window.DeviceMotionEvent) {
        sensorEl.innerText = isSecure ? '✅ Available' : '⚠️ Disabled (HTTP)';
        sensorEl.style.color = isSecure ? '#10b981' : '#f59e0b';
    } else {
        sensorEl.innerText = 'ℹ️ Touch/Mouse Look';
        sensorEl.style.color = '#64748b';
    }

    // Audio check
    const audioEl = document.getElementById('readinessAudio');
    if (window.AudioContext || window.webkitAudioContext) {
        audioEl.innerText = '✅ Available';
        audioEl.style.color = '#10b981';
    } else {
        audioEl.innerText = 'ℹ️ Standard';
        audioEl.style.color = '#64748b';
    }

    // If on insecure LAN origin, present the friendly security modal
    if (!isSecure && !isLocalhost) {
        document.getElementById('insecureOriginModal').style.display = 'flex';
        document.getElementById('preSessionModal').style.display = 'none';
    }
}

function redirectToHttps() {
    const port = (location.port === '8080' || location.port === '') ? '8443' : location.port;
    window.location.href = 'https://' + location.hostname + ':' + port + location.pathname + location.search;
}

function dismissInsecureWarning() {
    document.getElementById('insecureOriginModal').style.display = 'none';
    document.getElementById('preSessionModal').style.display = 'flex';
}

function requestSensorsAndStart() {
    // iOS 13+ sensor permission must be triggered synchronously from a user gesture
    if (typeof DeviceOrientationEvent !== 'undefined' && typeof DeviceOrientationEvent.requestPermission === 'function') {
        DeviceOrientationEvent.requestPermission()
            .then(response => {
                console.log('DeviceOrientation permission:', response);
                startSession();
            })
            .catch(err => {
                console.warn('Orientation permission error:', err);
                startSession();
            });
    } else if (typeof DeviceMotionEvent !== 'undefined' && typeof DeviceMotionEvent.requestPermission === 'function') {
        DeviceMotionEvent.requestPermission()
            .then(response => {
                console.log('DeviceMotion permission:', response);
                startSession();
            })
            .catch(err => {
                console.warn('Motion permission error:', err);
                startSession();
            });
    } else {
        startSession();
    }
}

// Apply JSON configuration from Java
const config = ${configJson};
if (config.skyColor) {
    document.getElementById('vrSky').setAttribute('color', config.skyColor);
}
if (config.groundColor) {
    document.getElementById('vrGround').setAttribute('color', config.groundColor);
}

function startSession() {
    const pre = document.getElementById('preStressInput').value;
    document.getElementById('formPreStress').value = pre;
    document.getElementById('preSessionModal').style.display = 'none';

    startTime = Date.now();
    timerInterval = setInterval(() => {
        elapsedSeconds++;
        const m = String(Math.floor(elapsedSeconds / 60)).padStart(2, '0');
        const s = String(elapsedSeconds % 60).padStart(2, '0');
        document.getElementById('hudTimer').innerText = m + ':' + s;
    }, 1000);

    // Poll live therapist commands if live session exists
    <c:if test="${not empty liveSession}">
    setInterval(pollTherapistCommands, 2000);
    </c:if>
}

function concludeSession() {
    clearInterval(timerInterval);
    document.getElementById('formDuration').value = elapsedSeconds;
    document.getElementById('postSessionModal').style.display = 'flex';
}

function triggerEmergencyStop() {
    clearInterval(timerInterval);
    document.getElementById('formDuration').value = elapsedSeconds;
    document.getElementById('formEmergency').value = 'true';
    document.getElementById('formReason').value = 'Patient pressed in-VR emergency stop button';

    // Direct to emergency grounding sequence
    window.location.href = '${pageContext.request.contextPath}/patient/emergency-grounding?sessionId=${sessionTemplate.id}';
}

function pollTherapistCommands() {
    fetch('${pageContext.request.contextPath}/api/commands?sessionId=${liveSession.id}')
        .then(r => r.json())
        .then(cmds => {
            if (cmds && cmds.length > 0) {
                cmds.forEach(cmd => {
                    if (cmd.type === 'PROMPT') {
                        const txt = cmd.payload;
                        document.getElementById('hudPrompt').style.display = 'block';
                        document.getElementById('hudPrompt').innerText = 'Therapist: ' + txt;
                        const vrText = document.getElementById('vrHudText');
                        vrText.setAttribute('value', txt);
                        vrText.setAttribute('visible', 'true');
                        setTimeout(() => {
                            document.getElementById('hudPrompt').style.display = 'none';
                            vrText.setAttribute('visible', 'false');
                        }, 8000);
                    } else if (cmd.type === 'EMERGENCY_STOP') {
                        alert('Your therapist has safely concluded this session.');
                        window.location.href = '${pageContext.request.contextPath}/patient/emergency-grounding?sessionId=${sessionTemplate.id}';
                    }
                });
            }
        })
        .catch(console.error);
}

// Conclude after target minutes if not manually stopped
setTimeout(() => {
    if (elapsedSeconds > 0) concludeSession();
}, (${sessionTemplate.defaultDurationMinutes} * 60 * 1000));

// Run capability verification on page load
window.addEventListener('DOMContentLoaded', runReadinessCheck);
</script>
</body>
</html>
