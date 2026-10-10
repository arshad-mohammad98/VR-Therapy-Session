<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<c:set var="pageTitle" value="System Settings" />
<jsp:include page="../layout/header.jsp" />

<div style="margin-bottom: 2rem;">
    <h1 style="font-size: 1.85rem; font-weight: 700; color: #0f172a;">Global Platform &amp; Safety Thresholds</h1>
    <p style="color: #64748b; margin-top: 0.25rem;">
        Configure heartbeat watchdog timeouts, crisis stress limits, session durations, and maintenance flags.
    </p>
</div>

<div style="display: grid; grid-template-columns: 1.2fr 1.8fr; gap: 2rem;">
    <!-- Update Setting Form -->
    <div style="background: white; border-radius: 12px; border: 1px solid #e2e8f0; padding: 2rem; box-shadow: 0 1px 3px rgba(0,0,0,0.05); height: fit-content;">
        <h2 style="font-size: 1.25rem; font-weight: 700; color: #0f172a; margin-bottom: 1.5rem;">Modify System Parameter</h2>

        <form action="${pageContext.request.contextPath}/admin/settings" method="post" style="display: flex; flex-direction: column; gap: 1.25rem;">
            <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}" />

            <div>
                <label for="settingKey" style="display: block; font-size: 0.875rem; font-weight: 600; color: #334155; margin-bottom: 0.35rem;">Configuration Key</label>
                <select id="settingKey" name="settingKey" required style="width: 100%; padding: 0.75rem 1rem; border-radius: 8px; border: 1px solid #cbd5e1; font-size: 0.95rem; background: white;">
                    <option value="max_session_duration_minutes">max_session_duration_minutes</option>
                    <option value="maintenance_mode">maintenance_mode (true/false)</option>
                    <option value="stress_alert_threshold">stress_alert_threshold (1-10)</option>
                    <option value="heartbeat_timeout_seconds">heartbeat_timeout_seconds</option>
                </select>
            </div>

            <div>
                <label for="settingValue" style="display: block; font-size: 0.875rem; font-weight: 600; color: #334155; margin-bottom: 0.35rem;">New Parameter Value</label>
                <input type="text" id="settingValue" name="settingValue" required placeholder="e.g. 60 or false"
                       style="width: 100%; padding: 0.75rem 1rem; border-radius: 8px; border: 1px solid #cbd5e1; font-size: 0.95rem;" />
            </div>

            <div>
                <label for="description" style="display: block; font-size: 0.875rem; font-weight: 600; color: #334155; margin-bottom: 0.35rem;">Administrative Rationale</label>
                <textarea id="description" name="description" rows="2" placeholder="Reason for adjusting this threshold..."
                          style="width: 100%; padding: 0.75rem 1rem; border-radius: 8px; border: 1px solid #cbd5e1; font-size: 0.95rem;"></textarea>
            </div>

            <button type="submit" class="btn btn-primary"
                    style="width: 100%; padding: 0.85rem; font-size: 1rem; font-weight: 600; border-radius: 8px; background: #6366f1; color: white; border: none; cursor: pointer;">
                Update Setting in Database
            </button>
        </form>
    </div>

    <!-- Active Settings Table -->
    <div style="background: white; border-radius: 12px; border: 1px solid #e2e8f0; padding: 2rem; box-shadow: 0 1px 3px rgba(0,0,0,0.05);">
        <h2 style="font-size: 1.25rem; font-weight: 700; color: #0f172a; margin-bottom: 1.5rem;">Database Config Parameters (${fn:length(settings)})</h2>

        <c:choose>
            <c:when test="${not empty settings}">
                <div style="display: flex; flex-direction: column; gap: 1rem;">
                    <c:forEach var="s" items="${settings}">
                        <div style="border: 1px solid #e2e8f0; border-radius: 8px; padding: 1.25rem; background: #f8fafc;">
                            <div style="display: flex; justify-content: space-between; align-items: center;">
                                <strong style="color: #0f172a; font-family: monospace; font-size: 0.95rem;">${s.settingKey}</strong>
                                <span class="badge badge-info" style="font-family: monospace; font-size: 0.9rem;">${s.settingValue}</span>
                            </div>
                            <c:if test="${not empty s.description}">
                                <p style="color: #64748b; font-size: 0.85rem; margin-top: 0.5rem;">${s.description}</p>
                            </c:if>
                        </div>
                    </c:forEach>
                </div>
            </c:when>
            <c:otherwise>
                <p style="color: #64748b; font-size: 0.9rem; text-align: center; padding: 2rem 0;">No system settings configured in database.</p>
            </c:otherwise>
        </c:choose>
    </div>
</div>

<jsp:include page="../layout/footer.jsp" />
