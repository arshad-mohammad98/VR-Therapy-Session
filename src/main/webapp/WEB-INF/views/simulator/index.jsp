<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<c:set var="pageTitle" value="VR Headset Simulator & Virtual Biometrics" />
<jsp:include page="../layout/header.jsp" />

<div style="margin-bottom: 2rem;">
    <h1 style="font-size: 1.85rem; font-weight: 700; color: #0f172a;">Virtual Reality Headset &amp; Biometric Simulator</h1>
    <p style="color: #64748b; margin-top: 0.25rem;">
        Emulate physical Meta Quest / WebXR client biometrics, test 6-digit code pairing, and receive live therapist commands without physical hardware.
    </p>
</div>

<div style="display: grid; grid-template-columns: 1.2fr 1.8fr; gap: 2rem;">
    <!-- Simulator Control Console -->
    <div style="background: white; border-radius: 12px; border: 1px solid #e2e8f0; padding: 2rem; box-shadow: 0 1px 3px rgba(0,0,0,0.05); height: fit-content; display: flex; flex-direction: column; gap: 1.5rem;">
        <h2 style="font-size: 1.25rem; font-weight: 700; color: #0f172a;">Virtual Client Controller</h2>

        <!-- Step 1: Pair with 6-Digit Code -->
        <div style="background: #f8fafc; border: 1px solid #e2e8f0; border-radius: 8px; padding: 1.25rem;">
            <div style="font-weight: 700; color: #1e293b; font-size: 0.95rem; margin-bottom: 0.5rem;">Step 1: Pair Simulated Device</div>
            <div style="display: flex; gap: 0.5rem;">
                <input type="text" id="simPairCode" placeholder="6-digit code" maxlength="6"
                       style="flex: 1; padding: 0.5rem 0.75rem; border-radius: 6px; border: 1px solid #cbd5e1; font-size: 1.1rem; letter-spacing: 2px; font-weight: 700; text-align: center;" />
                <button onclick="pairSimulatedDevice()" style="padding: 0.5rem 1rem; background: #0284c7; color: white; border: none; border-radius: 6px; font-weight: 600; cursor: pointer;">
                    Pair Headset
                </button>
            </div>
            <div id="simPairStatus" style="font-size: 0.8rem; margin-top: 0.5rem; color: #64748b;">Not paired yet.</div>
        </div>

        <!-- Step 2: Live Biometric Injection -->
        <div style="background: #f8fafc; border: 1px solid #e2e8f0; border-radius: 8px; padding: 1.25rem;">
            <div style="font-weight: 700; color: #1e293b; font-size: 0.95rem; margin-bottom: 0.75rem;">Step 2: Biometric Stream Injection</div>

            <div style="margin-bottom: 1rem;">
                <label style="display: flex; justify-content: space-between; font-size: 0.85rem; font-weight: 600; color: #334155; margin-bottom: 0.25rem;">
                    <span>Heart Rate (bpm)</span>
                    <span id="simBpmVal" style="color: #ef4444; font-weight: 800;">76 bpm</span>
                </label>
                <input type="range" id="simBpm" min="50" max="150" value="76" style="width: 100%; accent-color: #ef4444;"
                       oninput="document.getElementById('simBpmVal').innerText = this.value + ' bpm'" />
            </div>

            <div style="margin-bottom: 1rem;">
                <label style="display: flex; justify-content: space-between; font-size: 0.85rem; font-weight: 600; color: #334155; margin-bottom: 0.25rem;">
                    <span>Subjective Stress (SUDs 1-10)</span>
                    <span id="simStressVal" style="color: #f59e0b; font-weight: 800;">4 / 10</span>
                </label>
                <input type="range" id="simStress" min="1" max="10" value="4" style="width: 100%; accent-color: #f59e0b;"
                       oninput="document.getElementById('simStressVal').innerText = this.value + ' / 10'" />
            </div>

            <button id="telemetryToggleBtn" onclick="toggleTelemetryStream()"
                    style="width: 100%; padding: 0.75rem; background: #10b981; color: white; border: none; border-radius: 6px; font-weight: 700; cursor: pointer;">
                ▶ Start 2s Background Heartbeat
            </button>
        </div>

        <!-- In-VR HUD Command Receiver -->
        <div style="background: #0f172a; border-radius: 8px; padding: 1.25rem; color: white;">
            <div style="font-size: 0.75rem; color: #94a3b8; font-weight: 700; text-transform: uppercase;">Simulated Headset In-VR Display</div>
            <div id="simHudDisplay" style="margin-top: 0.75rem; min-height: 50px; font-size: 1.1rem; color: #38bdf8; font-family: monospace; display: flex; align-items: center; justify-content: center; text-align: center; border: 1px dashed rgba(255,255,255,0.2); border-radius: 6px; padding: 0.5rem;">
                [VR Headset Ready - No Active Commands]
            </div>
        </div>
    </div>

    <!-- Active Live Sessions to Connect to -->
    <div style="background: white; border-radius: 12px; border: 1px solid #e2e8f0; padding: 2rem; box-shadow: 0 1px 3px rgba(0,0,0,0.05);">
        <h2 style="font-size: 1.25rem; font-weight: 700; color: #0f172a; margin-bottom: 1.5rem;">Active Therapist Live Sessions (${fn:length(activeLiveSessions)})</h2>

        <c:choose>
            <c:when test="${not empty activeLiveSessions}">
                <div style="display: flex; flex-direction: column; gap: 1rem;">
                    <c:forEach var="ls" items="${activeLiveSessions}">
                        <div style="border: 1px solid #cbd5e1; border-radius: 10px; padding: 1.25rem; background: #f8fafc;">
                            <div style="display: flex; justify-content: space-between; align-items: center;">
                                <div>
                                    <h3 style="font-size: 1.1rem; font-weight: 700; color: #1e293b;">Live Session #${ls.id}</h3>
                                    <div style="font-size: 0.85rem; color: #64748b; margin-top: 0.25rem;">
                                        State: <strong>${ls.state}</strong> &bull; Environment: <strong>${ls.currentEnvironment}</strong> &bull; Intensity: <strong>${ls.currentIntensity}/5</strong>
                                    </div>
                                </div>
                                <button onclick="bindToLiveSession(${ls.id})" style="padding: 0.5rem 1rem; background: #0284c7; color: white; border: none; border-radius: 6px; font-weight: 600; cursor: pointer;">
                                    Connect &rarr;
                                </button>
                            </div>
                        </div>
                    </c:forEach>
                </div>
            </c:when>
            <c:otherwise>
                <div style="text-align: center; padding: 2rem 0; color: #64748b;">
                    <p style="margin-bottom: 1rem;">No live sessions currently created by a therapist.</p>
                    <a href="${pageContext.request.contextPath}/therapist/live-control" style="color: #0284c7; font-weight: 600; text-decoration: underline;">
                        Go to Live Control Room to launch a live session &rarr;
                    </a>
                </div>
            </c:otherwise>
        </c:choose>

        <div style="margin-top: 2rem; background: #f1f5f9; padding: 1.25rem; border-radius: 8px; font-size: 0.85rem; color: #475569; line-height: 1.6;">
            <strong>How to Demo in Presentation:</strong>
            <ol style="margin-left: 1.25rem; margin-top: 0.5rem;">
                <li>Open this page (<code>/simulator</code>) in one browser tab or window.</li>
                <li>Log in as therapist (<code>therapist1</code>) in another window and open the <strong>Live Control Room</strong>.</li>
                <li>Send text prompts, change intensity, or press Emergency Stop in the therapist view. Watch them appear instantly inside this simulator!</li>
            </ol>
        </div>
    </div>
</div>

<script>
let boundLiveSessionId = null;
let telemetryTimer = null;

function pairSimulatedDevice() {
    const code = document.getElementById('simPairCode').value.trim();
    if (!code || code.length !== 6) {
        alert('Please enter a 6-digit pairing code.');
        return;
    }
    fetch('${pageContext.request.contextPath}/api/device/pair', {
        method: 'POST',
        headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
        body: 'pairingCode=' + encodeURIComponent(code) + '&clientName=Simulated+Headset+Client'
    })
    .then(r => r.json())
    .then(data => {
        if (data.success) {
            document.getElementById('simPairStatus').innerText = '✅ Paired successfully as ' + data.deviceName + ' (ID #' + data.deviceId + ')';
            document.getElementById('simPairStatus').style.color = '#10b981';
        } else {
            document.getElementById('simPairStatus').innerText = '❌ ' + (data.error || 'Failed to pair');
            document.getElementById('simPairStatus').style.color = '#ef4444';
        }
    })
    .catch(err => {
        document.getElementById('simPairStatus').innerText = '❌ Error contacting pairing endpoint';
        document.getElementById('simPairStatus').style.color = '#ef4444';
    });
}

function bindToLiveSession(sessionId) {
    boundLiveSessionId = sessionId;
    alert('Simulated headset bound to Live Session #' + sessionId);
    startCommandListener();
}

function toggleTelemetryStream() {
    const btn = document.getElementById('telemetryToggleBtn');
    if (telemetryTimer) {
        clearInterval(telemetryTimer);
        telemetryTimer = null;
        btn.innerText = '▶ Start 2s Background Heartbeat';
        btn.style.background = '#10b981';
    } else {
        telemetryTimer = setInterval(sendTelemetryTick, 2000);
        sendTelemetryTick();
        btn.innerText = '⏹ Stop Heartbeat Telemetry';
        btn.style.background = '#ef4444';
    }
}

function sendTelemetryTick() {
    const bpm = document.getElementById('simBpm').value;
    const stress = document.getElementById('simStress').value;
    const liveParam = boundLiveSessionId ? '&liveId=' + boundLiveSessionId : '';

    fetch('${pageContext.request.contextPath}/api/telemetry', {
        method: 'POST',
        headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
        body: 'heartRate=' + bpm + '&stressRating=' + stress + liveParam
    }).catch(console.error);
}

function startCommandListener() {
    setInterval(() => {
        if (!boundLiveSessionId) return;
        fetch('${pageContext.request.contextPath}/api/commands?sessionId=' + boundLiveSessionId)
            .then(r => r.json())
            .then(cmds => {
                if (cmds && cmds.length > 0) {
                    cmds.forEach(cmd => {
                        const display = document.getElementById('simHudDisplay');
                        if (cmd.type === 'PROMPT') {
                            display.innerText = '💬 Therapist Prompt: "' + cmd.payload + '"';
                        } else if (cmd.type === 'EMERGENCY_STOP') {
                            display.innerText = '🛑 EMERGENCY STOP ACTIVATED: ' + cmd.payload;
                            display.style.color = '#ef4444';
                        } else if (cmd.type === 'SET_INTENSITY') {
                            display.innerText = '⚡ Intensity adjusted to ' + cmd.payload + '/5';
                        }
                    });
                }
            })
            .catch(console.error);
    }, 2000);
}
</script>

<jsp:include page="../layout/footer.jsp" />
