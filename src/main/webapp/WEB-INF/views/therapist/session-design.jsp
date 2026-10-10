<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<c:set var="pageTitle" value="VR Session Protocol Designer" />
<jsp:include page="../layout/header.jsp" />

<div style="margin-bottom: 2rem;">
    <h1 style="font-size: 1.85rem; font-weight: 700; color: #0f172a;">VR Session Protocol Designer</h1>
    <p style="color: #64748b; margin-top: 0.25rem;">Configure clinical VR parameters. Java server generates polymorphic 3D WebXR configurations stored as JSON.</p>
</div>

<div style="display: grid; grid-template-columns: 1.2fr 1.8fr; gap: 2rem;">
    <!-- Protocol Design Form -->
    <div style="background: white; border-radius: 12px; border: 1px solid #e2e8f0; padding: 2rem; box-shadow: 0 1px 3px rgba(0,0,0,0.05); height: fit-content;">
        <h2 style="font-size: 1.2rem; font-weight: 700; color: #0f172a; margin-bottom: 1.5rem;">Create New Protocol</h2>

        <form action="${pageContext.request.contextPath}/therapist/session-design" method="post" style="display: flex; flex-direction: column; gap: 1.25rem;">
            <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}" />

            <div>
                <label for="title" style="display: block; font-size: 0.875rem; font-weight: 600; color: #334155; margin-bottom: 0.35rem;">Session Title</label>
                <input type="text" id="title" name="title" required placeholder="e.g. Coastal Mindfulness Tier 1"
                       style="width: 100%; padding: 0.75rem 1rem; border-radius: 8px; border: 1px solid #cbd5e1; font-size: 0.95rem;" />
            </div>

            <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 1rem;">
                <div>
                    <label for="conditionType" style="display: block; font-size: 0.875rem; font-weight: 600; color: #334155; margin-bottom: 0.35rem;">Target Condition</label>
                    <select id="conditionType" name="conditionType" required
                            style="width: 100%; padding: 0.75rem 1rem; border-radius: 8px; border: 1px solid #cbd5e1; font-size: 0.9rem; background: white;">
                        <c:forEach var="c" items="${conditions}">
                            <option value="${c.name()}">${c.displayName}</option>
                        </c:forEach>
                    </select>
                </div>

                <div>
                    <label for="vrEnvironment" style="display: block; font-size: 0.875rem; font-weight: 600; color: #334155; margin-bottom: 0.35rem;">VR Environment</label>
                    <select id="vrEnvironment" name="vrEnvironment" required
                            style="width: 100%; padding: 0.75rem 1rem; border-radius: 8px; border: 1px solid #cbd5e1; font-size: 0.9rem; background: white;">
                        <c:forEach var="env" items="${environments}">
                            <option value="${env.name()}">${env.displayName}</option>
                        </c:forEach>
                    </select>
                </div>
            </div>

            <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 1rem;">
                <div>
                    <label for="durationMinutes" style="display: block; font-size: 0.875rem; font-weight: 600; color: #334155; margin-bottom: 0.35rem;">Duration (5 - 60 min)</label>
                    <input type="number" id="durationMinutes" name="durationMinutes" min="5" max="60" value="15" required
                           style="width: 100%; padding: 0.75rem 1rem; border-radius: 8px; border: 1px solid #cbd5e1; font-size: 0.95rem;" />
                </div>

                <div>
                    <label for="intensityLevel" style="display: block; font-size: 0.875rem; font-weight: 600; color: #334155; margin-bottom: 0.35rem;">Intensity Tier (1 - 5)</label>
                    <select id="intensityLevel" name="intensityLevel" required
                            style="width: 100%; padding: 0.75rem 1rem; border-radius: 8px; border: 1px solid #cbd5e1; font-size: 0.9rem; background: white;">
                        <option value="1">1 - Minimal (Gentle Relaxation)</option>
                        <option value="2" selected>2 - Mild (Low Exposure)</option>
                        <option value="3">3 - Moderate (Standard Desensitization)</option>
                        <option value="4">4 - High (Challenging Stimulus)</option>
                        <option value="5">5 - Maximum (Peak Distress Immersion)</option>
                    </select>
                </div>
            </div>

            <div style="display: flex; gap: 2rem; background: #f8fafc; padding: 1rem; border-radius: 8px; border: 1px solid #e2e8f0;">
                <label style="display: flex; align-items: center; gap: 0.5rem; font-size: 0.875rem; font-weight: 600; color: #334155; cursor: pointer;">
                    <input type="checkbox" name="audioEnabled" value="true" checked style="width: 18px; height: 18px;" />
                    Ambient Audio
                </label>
                <label style="display: flex; align-items: center; gap: 0.5rem; font-size: 0.875rem; font-weight: 600; color: #334155; cursor: pointer;">
                    <input type="checkbox" name="guidedBreathingEnabled" value="true" checked style="width: 18px; height: 18px;" />
                    Guided Breathing Sphere
                </label>
            </div>

            <div>
                <label for="therapistNotes" style="display: block; font-size: 0.875rem; font-weight: 600; color: #334155; margin-bottom: 0.35rem;">Therapist Clinical Notes</label>
                <textarea id="therapistNotes" name="therapistNotes" rows="3" placeholder="Clinical intent and exposure parameters..."
                          style="width: 100%; padding: 0.75rem 1rem; border-radius: 8px; border: 1px solid #cbd5e1; font-size: 0.95rem;"></textarea>
            </div>

            <button type="submit" class="btn btn-primary"
                    style="width: 100%; padding: 0.85rem; font-size: 1rem; font-weight: 600; border-radius: 8px; background: #0284c7; color: white; border: none; cursor: pointer;">
                Save Protocol &amp; Generate WebXR Config
            </button>
        </form>
    </div>

    <!-- Existing Protocols List -->
    <div style="background: white; border-radius: 12px; border: 1px solid #e2e8f0; padding: 2rem; box-shadow: 0 1px 3px rgba(0,0,0,0.05);">
        <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 1.5rem;">
            <h2 style="font-size: 1.2rem; font-weight: 700; color: #0f172a;">Configured Protocols (${fn:length(sessions)})</h2>
            <span style="font-size: 0.85rem; color: #64748b;">Java WebXR Scene Templates</span>
        </div>

        <c:choose>
            <c:when test="${not empty sessions}">
                <div style="display: flex; flex-direction: column; gap: 1rem;">
                    <c:forEach var="s" items="${sessions}">
                        <div style="background: #f8fafc; border: 1px solid #e2e8f0; border-radius: 10px; padding: 1.25rem; display: flex; justify-content: space-between; align-items: flex-start;">
                            <div>
                                <div style="display: flex; align-items: center; gap: 0.75rem;">
                                    <h3 style="font-size: 1.05rem; font-weight: 700; color: #1e293b;">${s.title}</h3>
                                    <span class="badge badge-info" style="font-size: 0.75rem;">${s.conditionType.displayName}</span>
                                    <span class="badge" style="font-size: 0.75rem; background: #e0e7ff; color: #3730a3;">Intensity ${s.intensityLevel}/5</span>
                                </div>
                                <div style="color: #64748b; font-size: 0.85rem; margin-top: 0.35rem;">
                                    <strong>Environment:</strong> ${s.vrEnvironment.displayName} &bull;
                                    <strong>Duration:</strong> ${s.defaultDurationMinutes} mins &bull;
                                    <strong>Audio:</strong> ${s.audioEnabled ? 'Yes' : 'No'} &bull;
                                    <strong>Breathing Guide:</strong> ${s.guidedBreathingEnabled ? 'Active' : 'Disabled'}
                                </div>
                                <c:if test="${not empty s.therapistNotes}">
                                    <div style="font-size: 0.82rem; color: #475569; margin-top: 0.5rem; font-style: italic;">
                                        &ldquo;${s.therapistNotes}&rdquo;
                                    </div>
                                </c:if>
                            </div>

                            <div style="display: flex; gap: 0.5rem;">
                                <a href="${pageContext.request.contextPath}/vr/launch?sessionId=${s.id}" target="_blank"
                                   style="padding: 0.4rem 0.85rem; background: #0d9488; color: white; border-radius: 6px; text-decoration: none; font-size: 0.85rem; font-weight: 600;">
                                    Preview Scene &rarr;
                                </a>
                            </div>
                        </div>
                    </c:forEach>
                </div>
            </c:when>
            <c:otherwise>
                <p style="color: #64748b; font-size: 0.9rem; text-align: center; padding: 2rem 0;">No session templates created yet.</p>
            </c:otherwise>
        </c:choose>
    </div>
</div>

<jsp:include page="../layout/footer.jsp" />
