<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<c:set var="pageTitle" value="Live Session Control Room" />
<jsp:include page="../layout/header.jsp" />

<div style="margin-bottom: 2rem; display: flex; justify-content: space-between; align-items: center;">
    <div>
        <h1 style="font-size: 1.85rem; font-weight: 700; color: #0f172a;">Live Remote-Guided Control Room</h1>
        <p style="color: #64748b; margin-top: 0.25rem;">
            Real-time biometric telemetry, procedural stimulus steering, and crisis safety override.
        </p>
    </div>
    <c:if test="${not empty activeSession}">
        <div style="display: flex; align-items: center; gap: 0.75rem;">
            <span class="badge" style="background: ${activeSession.state == 'ACTIVE' ? '#d1fae5' : '#fee2e2'}; color: ${activeSession.state == 'ACTIVE' ? '#065f46' : '#991b1b'}; font-size: 0.9rem; padding: 0.4rem 0.85rem;">
                ● STATE: ${activeSession.state}
            </span>
            <form action="${pageContext.request.contextPath}/therapist/live-control" method="post" style="display: inline;" onsubmit="return confirm('Trigger emergency abort? This will halt VR and alert safety teams.');">
                <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}" />
                <input type="hidden" name="action" value="emergencyStop" />
                <input type="hidden" name="sessionId" value="${activeSession.id}" />
                <button type="submit" style="padding: 0.5rem 1rem; background: #dc2626; color: white; border: none; border-radius: 8px; font-weight: 700; cursor: pointer; box-shadow: 0 2px 4px rgba(220,38,38,0.3);">
                    🛑 EMERGENCY STOP
                </button>
            </form>
        </div>
    </c:if>
</div>

<c:choose>
    <c:when test="${not empty activeSession}">
        <!-- Top Biometrics & Live Telemetry Stream -->
        <div style="display: grid; grid-template-columns: repeat(4, 1fr); gap: 1.25rem; margin-bottom: 1.5rem;">
            <div style="background: white; border-radius: 12px; padding: 1.25rem; border: 1px solid #e2e8f0; text-align: center;">
                <div style="color: #64748b; font-size: 0.8rem; font-weight: 600; text-transform: uppercase;">Heart Rate</div>
                <div id="liveBpm" style="font-size: 2.2rem; font-weight: 800; color: #ef4444; margin-top: 0.25rem;">-- bpm</div>
                <div style="font-size: 0.75rem; color: #64748b;">Target: 60 - 90 bpm</div>
            </div>
            <div style="background: white; border-radius: 12px; padding: 1.25rem; border: 1px solid #e2e8f0; text-align: center;">
                <div style="color: #64748b; font-size: 0.8rem; font-weight: 600; text-transform: uppercase;">Stress Index</div>
                <div id="liveStress" style="font-size: 2.2rem; font-weight: 800; color: #f59e0b; margin-top: 0.25rem;">-- / 10</div>
                <div style="font-size: 0.75rem; color: #64748b;">GSR / Subjective Gauge</div>
            </div>
            <div style="background: white; border-radius: 12px; padding: 1.25rem; border: 1px solid #e2e8f0; text-align: center;">
                <div style="color: #64748b; font-size: 0.8rem; font-weight: 600; text-transform: uppercase;">Current Intensity</div>
                <div id="liveIntensity" style="font-size: 2.2rem; font-weight: 800; color: #0284c7; margin-top: 0.25rem;">${activeSession.currentIntensity} / 5</div>
                <div style="font-size: 0.75rem; color: #64748b;">Stimulus Exposure Tier</div>
            </div>
            <div style="background: white; border-radius: 12px; padding: 1.25rem; border: 1px solid #e2e8f0; text-align: center;">
                <div style="color: #64748b; font-size: 0.8rem; font-weight: 600; text-transform: uppercase;">Elapsed Timer</div>
                <div id="liveTimer" style="font-size: 2.2rem; font-weight: 800; color: #10b981; margin-top: 0.25rem;">00:00</div>
                <div style="font-size: 0.75rem; color: #64748b;">Active Immersion Time</div>
            </div>
        </div>

        <!-- Telemetry Chart & VR Remote Control Pad -->
        <div style="display: grid; grid-template-columns: 1.6fr 1.2fr; gap: 1.5rem; margin-bottom: 2rem;">
            <!-- Real-Time Chart -->
            <div style="background: white; border-radius: 12px; border: 1px solid #e2e8f0; padding: 1.5rem; box-shadow: 0 1px 3px rgba(0,0,0,0.05);">
                <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 1rem;">
                    <h3 style="font-size: 1.1rem; font-weight: 700; color: #0f172a;">Physiological Vitals Stream (2s AJAX Polling)</h3>
                    <span id="streamStatus" style="font-size: 0.8rem; color: #10b981;">● Connected</span>
                </div>
                <div style="height: 280px; position: relative;">
                    <canvas id="telemetryChart"></canvas>
                </div>
            </div>

            <!-- Remote Stimulus Adjustments -->
            <div style="background: white; border-radius: 12px; border: 1px solid #e2e8f0; padding: 1.5rem; box-shadow: 0 1px 3px rgba(0,0,0,0.05); display: flex; flex-direction: column; gap: 1.25rem;">
                <h3 style="font-size: 1.1rem; font-weight: 700; color: #0f172a;">Live Parameter Steering</h3>

                <!-- Intensity Steering -->
                <div>
                    <label style="display: block; font-size: 0.85rem; font-weight: 600; color: #334155; margin-bottom: 0.5rem;">Adjust Intensity (Tier 1 - 5)</label>
                    <div style="display: flex; gap: 0.5rem;">
                        <c:forEach var="lvl" begin="1" end="5">
                            <form action="${pageContext.request.contextPath}/therapist/live-control" method="post" style="flex: 1;">
                                <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}" />
                                <input type="hidden" name="action" value="changeIntensity" />
                                <input type="hidden" name="sessionId" value="${activeSession.id}" />
                                <input type="hidden" name="intensity" value="${lvl}" />
                                <button type="submit" style="width: 100%; padding: 0.5rem; border-radius: 6px; font-weight: 700; border: 1px solid #cbd5e1; cursor: pointer; background: ${activeSession.currentIntensity == lvl ? '#0284c7' : '#f8fafc'}; color: ${activeSession.currentIntensity == lvl ? 'white' : '#334155'};">
                                    ${lvl}
                                </button>
                            </form>
                        </c:forEach>
                    </div>
                </div>

                <!-- Environment Switch -->
                <div>
                    <label style="display: block; font-size: 0.85rem; font-weight: 600; color: #334155; margin-bottom: 0.5rem;">Switch Environment</label>
                    <form action="${pageContext.request.contextPath}/therapist/live-control" method="post" style="display: flex; gap: 0.5rem;">
                        <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}" />
                        <input type="hidden" name="action" value="changeEnvironment" />
                        <input type="hidden" name="sessionId" value="${activeSession.id}" />
                        <select name="environment" style="flex: 1; padding: 0.5rem; border-radius: 6px; border: 1px solid #cbd5e1; font-size: 0.85rem;">
                            <c:forEach var="env" items="${environments}">
                                <option value="${env.name()}" ${activeSession.currentEnvironment == env ? 'selected' : ''}>${env.displayName}</option>
                            </c:forEach>
                        </select>
                        <button type="submit" style="padding: 0.5rem 0.85rem; background: #0d9488; color: white; border: none; border-radius: 6px; font-weight: 600; cursor: pointer;">
                            Switch
                        </button>
                    </form>
                </div>

                <!-- Guided Breathing Toggle -->
                <div style="display: flex; justify-content: space-between; align-items: center; background: #f8fafc; padding: 0.75rem 1rem; border-radius: 8px; border: 1px solid #e2e8f0;">
                    <div>
                        <div style="font-size: 0.85rem; font-weight: 600; color: #334155;">Guided Breathing Sphere</div>
                        <div style="font-size: 0.75rem; color: #64748b;">Status: ${activeSession.breathingGuideActive ? 'Active Pulsing' : 'Off'}</div>
                    </div>
                    <form action="${pageContext.request.contextPath}/therapist/live-control" method="post">
                        <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}" />
                        <input type="hidden" name="action" value="toggleBreathing" />
                        <input type="hidden" name="sessionId" value="${activeSession.id}" />
                        <input type="hidden" name="active" value="${!activeSession.breathingGuideActive}" />
                        <button type="submit" style="padding: 0.4rem 0.85rem; border-radius: 6px; font-weight: 600; font-size: 0.8rem; cursor: pointer; background: ${activeSession.breathingGuideActive ? '#f59e0b' : '#10b981'}; color: white; border: none;">
                            ${activeSession.breathingGuideActive ? 'Stop Guide' : 'Start Guide'}
                        </button>
                    </form>
                </div>

                <!-- In-VR Text Prompt -->
                <div>
                    <label style="display: block; font-size: 0.85rem; font-weight: 600; color: #334155; margin-bottom: 0.5rem;">Transmit Prompt to Patient VR HUD</label>
                    <form action="${pageContext.request.contextPath}/therapist/live-control" method="post" style="display: flex; gap: 0.5rem;">
                        <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}" />
                        <input type="hidden" name="action" value="sendPrompt" />
                        <input type="hidden" name="sessionId" value="${activeSession.id}" />
                        <input type="text" name="promptText" placeholder="e.g. Focus on your exhalation..." required
                               style="flex: 1; padding: 0.5rem 0.75rem; border-radius: 6px; border: 1px solid #cbd5e1; font-size: 0.85rem;" />
                        <button type="submit" style="padding: 0.5rem 0.85rem; background: #6366f1; color: white; border: none; border-radius: 6px; font-weight: 600; cursor: pointer;">
                            Send
                        </button>
                    </form>
                </div>

                <!-- Session State Controls -->
                <div style="display: flex; gap: 0.5rem; margin-top: auto;">
                    <c:choose>
                        <c:when test="${activeSession.state == 'ACTIVE'}">
                            <form action="${pageContext.request.contextPath}/therapist/live-control" method="post" style="flex: 1;">
                                <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}" />
                                <input type="hidden" name="action" value="pause" />
                                <input type="hidden" name="sessionId" value="${activeSession.id}" />
                                <button type="submit" style="width: 100%; padding: 0.6rem; background: #f59e0b; color: white; border: none; border-radius: 6px; font-weight: 600; cursor: pointer;">
                                    ⏸ Pause
                                </button>
                            </form>
                        </c:when>
                        <c:otherwise>
                            <form action="${pageContext.request.contextPath}/therapist/live-control" method="post" style="flex: 1;">
                                <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}" />
                                <input type="hidden" name="action" value="resume" />
                                <input type="hidden" name="sessionId" value="${activeSession.id}" />
                                <button type="submit" style="width: 100%; padding: 0.6rem; background: #10b981; color: white; border: none; border-radius: 6px; font-weight: 600; cursor: pointer;">
                                    ▶ Resume
                                </button>
                            </form>
                        </c:otherwise>
                    </c:choose>

                    <form action="${pageContext.request.contextPath}/therapist/live-control" method="post" style="flex: 1;">
                        <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}" />
                        <input type="hidden" name="action" value="complete" />
                        <input type="hidden" name="sessionId" value="${activeSession.id}" />
                        <button type="submit" style="width: 100%; padding: 0.6rem; background: #0284c7; color: white; border: none; border-radius: 6px; font-weight: 600; cursor: pointer;">
                            ✓ Conclude Session
                        </button>
                    </form>
                </div>
            </div>
        </div>

        <script>
        const ctx = document.getElementById('telemetryChart').getContext('2d');
        const telemetryChart = new Chart(ctx, {
            type: 'line',
            data: {
                labels: [],
                datasets: [
                    {
                        label: 'Heart Rate (bpm)',
                        data: [],
                        borderColor: '#ef4444',
                        backgroundColor: 'rgba(239, 68, 68, 0.1)',
                        tension: 0.3,
                        yAxisID: 'y'
                    },
                    {
                        label: 'Stress Index (1-10)',
                        data: [],
                        borderColor: '#f59e0b',
                        backgroundColor: 'rgba(245, 158, 11, 0.1)',
                        tension: 0.3,
                        yAxisID: 'y1'
                    }
                ]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                scales: {
                    y: { type: 'linear', position: 'left', min: 40, max: 160, title: { display: true, text: 'BPM' } },
                    y1: { type: 'linear', position: 'right', min: 0, max: 10, grid: { drawOnChartArea: false }, title: { display: true, text: 'Stress' } }
                }
            }
        });

        let elapsedSeconds = 0;
        setInterval(() => {
            elapsedSeconds++;
            const m = String(Math.floor(elapsedSeconds / 60)).padStart(2, '0');
            const s = String(elapsedSeconds % 60).padStart(2, '0');
            document.getElementById('liveTimer').innerText = m + ':' + s;
        }, 1000);

        // 2-second AJAX polling from Java Telemetry API
        function pollTelemetry() {
            fetch('${pageContext.request.contextPath}/api/telemetry?liveId=${activeSession.id}')
                .then(res => res.json())
                .then(data => {
                    if (data && data.heartRate) {
                        document.getElementById('liveBpm').innerText = data.heartRate + ' bpm';
                        document.getElementById('liveStress').innerText = data.stressRating + ' / 10';

                        const now = new Date().toLocaleTimeString();
                        if (telemetryChart.data.labels.length > 20) {
                            telemetryChart.data.labels.shift();
                            telemetryChart.data.datasets[0].data.shift();
                            telemetryChart.data.datasets[1].data.shift();
                        }
                        telemetryChart.data.labels.push(now);
                        telemetryChart.data.datasets[0].data.push(data.heartRate);
                        telemetryChart.data.datasets[1].data.push(data.stressRating);
                        telemetryChart.update();
                    }
                })
                .catch(err => console.error('Telemetry polling error:', err));
        }
        setInterval(pollTelemetry, 2000);
        pollTelemetry();
        </script>
    </c:when>
    <c:otherwise>
        <!-- Launch a new live session if none active -->
        <div style="background: white; border-radius: 12px; border: 1px solid #e2e8f0; padding: 2.5rem; text-align: center; max-width: 600px; margin: 3rem auto; box-shadow: 0 4px 6px rgba(0,0,0,0.05);">
            <div style="font-size: 3rem; margin-bottom: 1rem;">⚡</div>
            <h2 style="font-size: 1.4rem; font-weight: 700; color: #0f172a;">No Live Session In Progress</h2>
            <p style="color: #64748b; margin-top: 0.5rem; margin-bottom: 2rem;">
                Select a patient and a configured protocol template to initiate a remote-guided live session.
            </p>

            <form action="${pageContext.request.contextPath}/therapist/live-control" method="post" style="display: flex; flex-direction: column; gap: 1.25rem; text-align: left;">
                <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}" />
                <input type="hidden" name="action" value="startNew" />

                <div>
                    <label for="patientId" style="display: block; font-size: 0.875rem; font-weight: 600; color: #334155; margin-bottom: 0.35rem;">Patient</label>
                    <select id="patientId" name="patientId" required style="width: 100%; padding: 0.75rem 1rem; border-radius: 8px; border: 1px solid #cbd5e1; font-size: 0.95rem; background: white;">
                        <c:forEach var="p" items="${patients}">
                            <option value="${p.id}">${p.fullName} (${p.username})</option>
                        </c:forEach>
                    </select>
                </div>

                <div>
                    <label for="templateId" style="display: block; font-size: 0.875rem; font-weight: 600; color: #334155; margin-bottom: 0.35rem;">Protocol Template</label>
                    <select id="templateId" name="templateId" required style="width: 100%; padding: 0.75rem 1rem; border-radius: 8px; border: 1px solid #cbd5e1; font-size: 0.95rem; background: white;" onchange="syncTemplateDefaults(this)">
                        <c:forEach var="tpl" items="${sessionTemplates}">
                            <option value="${tpl.id}" data-env="${tpl.vrEnvironment.name()}" data-intensity="${tpl.intensityLevel}">${tpl.title} (${tpl.conditionType.displayName})</option>
                        </c:forEach>
                    </select>
                </div>

                <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 1rem;">
                    <div>
                        <label for="environment" style="display: block; font-size: 0.875rem; font-weight: 600; color: #334155; margin-bottom: 0.35rem;">Starting Environment</label>
                        <select id="environment" name="environment" required style="width: 100%; padding: 0.75rem 1rem; border-radius: 8px; border: 1px solid #cbd5e1; font-size: 0.9rem; background: white;">
                            <c:forEach var="env" items="${environments}">
                                <option value="${env.name()}">${env.displayName}</option>
                            </c:forEach>
                        </select>
                    </div>
                    <div>
                        <label for="intensity" style="display: block; font-size: 0.875rem; font-weight: 600; color: #334155; margin-bottom: 0.35rem;">Starting Intensity</label>
                        <select id="intensity" name="intensity" required style="width: 100%; padding: 0.75rem 1rem; border-radius: 8px; border: 1px solid #cbd5e1; font-size: 0.9rem; background: white;">
                            <option value="1">1 - Minimal</option>
                            <option value="2" selected>2 - Mild</option>
                            <option value="3">3 - Moderate</option>
                            <option value="4">4 - High</option>
                            <option value="5">5 - Maximum</option>
                        </select>
                    </div>
                </div>

                <button type="submit" class="btn btn-primary"
                        style="width: 100%; padding: 0.85rem; font-size: 1rem; font-weight: 600; border-radius: 8px; background: #ea580c; color: white; border: none; cursor: pointer;">
                    🚀 Launch Live Remote Session
                </button>
            </form>
            <script>
            function syncTemplateDefaults(selectElem) {
                if (!selectElem || selectElem.selectedIndex < 0) return;
                const opt = selectElem.options[selectElem.selectedIndex];
                if (opt) {
                    const env = opt.getAttribute('data-env');
                    const intensity = opt.getAttribute('data-intensity');
                    if (env) {
                        const envSelect = document.getElementById('environment');
                        if (envSelect) envSelect.value = env;
                    }
                    if (intensity) {
                        const intSelect = document.getElementById('intensity');
                        if (intSelect) intSelect.value = intensity;
                    }
                }
            }
            document.addEventListener('DOMContentLoaded', function() {
                const tplSelect = document.getElementById('templateId');
                if (tplSelect && tplSelect.options.length > 0) {
                    syncTemplateDefaults(tplSelect);
                }
            });
            </script>
        </div>
    </c:otherwise>
</c:choose>

<jsp:include page="../layout/footer.jsp" />
