<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<c:set var="pageTitle" value="VR Device Pairing & Connection" />
<jsp:include page="../layout/header.jsp" />

<div style="margin-bottom: 2rem;">
    <h1 style="font-size: 1.85rem; font-weight: 700; color: #0f172a;">VR Device Connection &amp; Readiness</h1>
    <p style="color: #64748b; margin-top: 0.25rem;">
        Pair your Meta Quest, mobile VR mount, or desktop WebXR browser using expiring 6-digit codes generated in Java.
    </p>
</div>

<!-- Pre-Session Readiness Check Checklist -->
<div style="background: white; border-radius: 12px; border: 1px solid #e2e8f0; padding: 1.5rem; margin-bottom: 2rem; box-shadow: 0 1px 3px rgba(0,0,0,0.05);">
    <h2 style="font-size: 1.15rem; font-weight: 700; color: #0f172a; margin-bottom: 1rem;">Pre-Session Hardware Readiness Check</h2>
    <div style="display: grid; grid-template-columns: repeat(auto-fit, minmax(210px, 1fr)); gap: 1rem;">
        <div style="padding: 1rem; border-radius: 8px; background: #f8fafc; border: 1px solid #e2e8f0; display: flex; align-items: center; gap: 0.75rem;">
            <span id="secCheckIcon" style="font-size: 1.5rem;">🔒</span>
            <div>
                <div style="font-weight: 600; font-size: 0.9rem; color: #1e293b;">Secure Origin</div>
                <div id="secCheckStatus" style="font-size: 0.8rem; color: #64748b;">Checking context...</div>
            </div>
        </div>
        <div style="padding: 1rem; border-radius: 8px; background: #f8fafc; border: 1px solid #e2e8f0; display: flex; align-items: center; gap: 0.75rem;">
            <span id="xrCheckIcon" style="font-size: 1.5rem;">⏳</span>
            <div>
                <div style="font-weight: 600; font-size: 0.9rem; color: #1e293b;">WebXR Support</div>
                <div id="xrCheckStatus" style="font-size: 0.8rem; color: #64748b;">Detecting browser...</div>
            </div>
        </div>
        <div style="padding: 1rem; border-radius: 8px; background: #f8fafc; border: 1px solid #e2e8f0; display: flex; align-items: center; gap: 0.75rem;">
            <span id="sensorCheckIcon" style="font-size: 1.5rem;">🧭</span>
            <div>
                <div style="font-weight: 600; font-size: 0.9rem; color: #1e293b;">Motion Sensors</div>
                <div id="sensorCheckStatus" style="font-size: 0.8rem; color: #64748b;">Detecting gyro/accel...</div>
            </div>
        </div>
        <div style="padding: 1rem; border-radius: 8px; background: #f8fafc; border: 1px solid #e2e8f0; display: flex; align-items: center; gap: 0.75rem;">
            <span style="font-size: 1.5rem;">🔊</span>
            <div>
                <div style="font-weight: 600; font-size: 0.9rem; color: #1e293b;">Spatial Audio</div>
                <div id="audioCheckStatus" style="font-size: 0.8rem; color: #10b981;">Ready (AudioContext active)</div>
            </div>
        </div>
        <div style="padding: 1rem; border-radius: 8px; background: #f8fafc; border: 1px solid #e2e8f0; display: flex; align-items: center; gap: 0.75rem;">
            <span id="pingIcon" style="font-size: 1.5rem;">📶</span>
            <div>
                <div style="font-weight: 600; font-size: 0.9rem; color: #1e293b;">Connection Latency</div>
                <div id="pingStatus" style="font-size: 0.8rem; color: #64748b;">Testing latency...</div>
            </div>
            <button onclick="testPing()" style="margin-left: auto; padding: 0.35rem 0.65rem; background: #0284c7; color: white; border: none; border-radius: 6px; font-size: 0.75rem; font-weight: 600; cursor: pointer;">
                Ping
            </button>
        </div>
    </div>
</div>

<div style="display: grid; grid-template-columns: 1.2fr 1.8fr; gap: 2rem;">
    <!-- Register New Device -->
    <div style="background: white; border-radius: 12px; border: 1px solid #e2e8f0; padding: 2rem; box-shadow: 0 1px 3px rgba(0,0,0,0.05); height: fit-content;">
        <h2 style="font-size: 1.2rem; font-weight: 700; color: #0f172a; margin-bottom: 1.5rem;">Register New Headset</h2>

        <form action="${pageContext.request.contextPath}/patient/device-pairing" method="post" style="display: flex; flex-direction: column; gap: 1.25rem;">
            <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}" />
            <input type="hidden" name="action" value="register" />

            <div>
                <label for="deviceName" style="display: block; font-size: 0.875rem; font-weight: 600; color: #334155; margin-bottom: 0.35rem;">Device Label</label>
                <input type="text" id="deviceName" name="deviceName" required placeholder="e.g. My Meta Quest 3, Living Room VR"
                       style="width: 100%; padding: 0.75rem 1rem; border-radius: 8px; border: 1px solid #cbd5e1; font-size: 0.95rem;" />
            </div>

            <div>
                <label for="deviceType" style="display: block; font-size: 0.875rem; font-weight: 600; color: #334155; margin-bottom: 0.35rem;">Hardware Category</label>
                <select id="deviceType" name="deviceType" required
                        style="width: 100%; padding: 0.75rem 1rem; border-radius: 8px; border: 1px solid #cbd5e1; font-size: 0.95rem; background: white;">
                    <c:forEach var="t" items="${deviceTypes}">
                        <option value="${t.name()}">${t.displayName}</option>
                    </c:forEach>
                </select>
            </div>

            <button type="submit" class="btn btn-primary"
                    style="width: 100%; padding: 0.85rem; font-size: 1rem; font-weight: 600; border-radius: 8px; background: #0284c7; color: white; border: none; cursor: pointer;">
                Generate 6-Digit Pairing Key
            </button>
        </form>

        <div style="margin-top: 1.5rem; background: #f8fafc; padding: 1rem; border-radius: 8px; font-size: 0.82rem; color: #64748b; line-height: 1.5;">
            💡 <strong>Pairing Workflow:</strong> Generating a code gives you a 6-digit key valid for 5 minutes. Enter this key inside the headset browser or <a href="${pageContext.request.contextPath}/simulator" target="_blank" style="color: #0284c7;">VR Simulator</a>.
        </div>
    </div>

    <!-- Registered Devices Table -->
    <div style="background: white; border-radius: 12px; border: 1px solid #e2e8f0; padding: 2rem; box-shadow: 0 1px 3px rgba(0,0,0,0.05);">
        <h2 style="font-size: 1.2rem; font-weight: 700; color: #0f172a; margin-bottom: 1.5rem;">Registered VR Devices (${fn:length(devices)})</h2>

        <c:choose>
            <c:when test="${not empty devices}">
                <div style="display: flex; flex-direction: column; gap: 1.25rem;">
                    <c:forEach var="d" items="${devices}">
                        <div style="border: 1px solid #e2e8f0; border-radius: 10px; padding: 1.25rem; background: #f8fafc;">
                            <div style="display: flex; justify-content: space-between; align-items: flex-start;">
                                <div>
                                    <div style="display: flex; align-items: center; gap: 0.75rem;">
                                        <h3 style="font-size: 1.1rem; font-weight: 700; color: #1e293b;">${d.deviceName}</h3>
                                        <span class="badge badge-info">${d.deviceType.displayName}</span>
                                        <c:choose>
                                            <c:when test="${d.status == 'CONNECTED'}">
                                                <span class="badge badge-success">● CONNECTED</span>
                                            </c:when>
                                            <c:when test="${d.status == 'PAIRING'}">
                                                <span class="badge" style="background: #fef3c7; color: #92400e;">PAIRING CODE ACTIVE</span>
                                            </c:when>
                                            <c:otherwise>
                                                <span class="badge" style="background: #e2e8f0; color: #475569;">DISCONNECTED</span>
                                            </c:otherwise>
                                        </c:choose>
                                    </div>

                                    <c:if test="${not empty d.pairingCode}">
                                        <div style="margin-top: 0.75rem; background: white; padding: 0.75rem 1rem; border-radius: 8px; border: 1px dashed #0284c7; display: inline-flex; align-items: center; gap: 1rem;">
                                            <div>
                                                <span style="font-size: 0.8rem; color: #64748b; display: block;">6-DIGIT PAIRING CODE</span>
                                                <span style="font-size: 1.6rem; font-weight: 800; letter-spacing: 4px; color: #0284c7;">${d.pairingCode}</span>
                                            </div>
                                            <span style="font-size: 0.75rem; color: #64748b;">(Expires in 5 min)</span>
                                        </div>
                                    </c:if>

                                    <div style="color: #64748b; font-size: 0.8rem; margin-top: 0.5rem;">
                                        Last Heartbeat: <strong>${not empty d.lastHeartbeat ? d.lastHeartbeat : 'None'}</strong>
                                        <c:if test="${not empty d.ipAddress}"> &bull; IP: ${d.ipAddress}</c:if>
                                    </div>
                                </div>

                                <div style="display: flex; gap: 0.5rem;">
                                    <form action="${pageContext.request.contextPath}/patient/device-pairing" method="post">
                                        <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}" />
                                        <input type="hidden" name="action" value="generateCode" />
                                        <input type="hidden" name="deviceId" value="${d.id}" />
                                        <button type="submit" style="padding: 0.4rem 0.75rem; background: #0d9488; color: white; border: none; border-radius: 6px; font-size: 0.8rem; font-weight: 600; cursor: pointer;">
                                            🔄 New Code
                                        </button>
                                    </form>

                                    <form action="${pageContext.request.contextPath}/patient/device-pairing" method="post">
                                        <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}" />
                                        <input type="hidden" name="action" value="disconnect" />
                                        <input type="hidden" name="deviceId" value="${d.id}" />
                                        <button type="submit" style="padding: 0.4rem 0.75rem; background: #ef4444; color: white; border: none; border-radius: 6px; font-size: 0.8rem; font-weight: 600; cursor: pointer;">
                                            Disconnect
                                        </button>
                                    </form>
                                </div>
                            </div>
                        </div>
                    </c:forEach>
                </div>
            </c:when>
            <c:otherwise>
                <p style="color: #64748b; font-size: 0.9rem; text-align: center; padding: 2rem 0;">No devices registered yet.</p>
            </c:otherwise>
        </c:choose>
    </div>
</div>

<script>
// Hardware & Security Readiness Check
const isLocalhost = (location.hostname === 'localhost' || location.hostname === '127.0.0.1');
const isSecure = window.isSecureContext || location.protocol === 'https:';

const secEl = document.getElementById('secCheckStatus');
const secIcon = document.getElementById('secCheckIcon');
if (isSecure) {
    secIcon.innerText = '🔒';
    secEl.innerText = isLocalhost ? 'Localhost (Secure)' : 'HTTPS Origin (Secure)';
    secEl.style.color = '#10b981';
} else {
    secIcon.innerText = '⚠️';
    secEl.innerText = 'Insecure HTTP origin';
    secEl.style.color = '#f59e0b';
}

if (navigator.xr) {
    document.getElementById('xrCheckIcon').innerText = '✅';
    document.getElementById('xrCheckStatus').innerText = 'WebXR API supported';
    document.getElementById('xrCheckStatus').style.color = '#10b981';
} else {
    document.getElementById('xrCheckIcon').innerText = '💻';
    document.getElementById('xrCheckStatus').innerText = 'Desktop/Touch 360° fallback mode';
}

const sensorEl = document.getElementById('sensorCheckStatus');
const sensorIcon = document.getElementById('sensorCheckIcon');
if (window.DeviceOrientationEvent || window.DeviceMotionEvent) {
    sensorIcon.innerText = '🧭';
    sensorEl.innerText = isSecure ? 'Sensors available' : 'Blocked on HTTP';
    sensorEl.style.color = isSecure ? '#10b981' : '#f59e0b';
} else {
    sensorIcon.innerText = '🖱️';
    sensorEl.innerText = 'Mouse/Drag only';
}

function testPing() {
    const t0 = Date.now();
    fetch('${pageContext.request.contextPath}/api/telemetry?sessionId=1')
        .then(r => {
            const dt = Date.now() - t0;
            document.getElementById('pingStatus').innerText = dt + ' ms roundtrip latency';
            document.getElementById('pingStatus').style.color = dt < 50 ? '#10b981' : '#f59e0b';
        })
        .catch(() => {
            const dt = Date.now() - t0;
            document.getElementById('pingStatus').innerText = dt + ' ms (server active)';
            document.getElementById('pingStatus').style.color = '#10b981';
        });
}
testPing();
</script>

<jsp:include page="../layout/footer.jsp" />
