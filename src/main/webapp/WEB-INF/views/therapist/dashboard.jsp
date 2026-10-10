<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<c:set var="pageTitle" value="Therapist Command Center" />
<jsp:include page="../layout/header.jsp" />

<div style="margin-bottom: 2rem;">
    <h1 style="font-size: 1.85rem; font-weight: 700; color: #0f172a;">Therapist Clinical Command Center</h1>
    <p style="color: #64748b; margin-top: 0.25rem;">Welcome back, <strong>${sessionScope.currentUser.fullName}</strong>. Manage active sessions, exposure hierarchies, and patient telemetry.</p>
</div>

<!-- Quick Stat Cards -->
<div style="display: grid; grid-template-columns: repeat(auto-fit, minmax(220px, 1fr)); gap: 1.25rem; margin-bottom: 2rem;">
    <div style="background: white; border-radius: 12px; padding: 1.5rem; border: 1px solid #e2e8f0; box-shadow: 0 1px 3px rgba(0,0,0,0.05);">
        <div style="color: #64748b; font-size: 0.85rem; font-weight: 600; text-transform: uppercase;">Active Patients</div>
        <div style="font-size: 2.2rem; font-weight: 700; color: #0284c7; margin-top: 0.5rem;">${fn:length(patients)}</div>
        <div style="font-size: 0.8rem; color: #10b981; margin-top: 0.25rem;">Enrolled in VR programs</div>
    </div>
    <div style="background: white; border-radius: 12px; padding: 1.5rem; border: 1px solid #e2e8f0; box-shadow: 0 1px 3px rgba(0,0,0,0.05);">
        <div style="color: #64748b; font-size: 0.85rem; font-weight: 600; text-transform: uppercase;">Session Templates</div>
        <div style="font-size: 2.2rem; font-weight: 700; color: #0d9488; margin-top: 0.5rem;">${fn:length(sessions)}</div>
        <div style="font-size: 0.8rem; color: #64748b; margin-top: 0.25rem;">Custom WebXR environments</div>
    </div>
    <div style="background: white; border-radius: 12px; padding: 1.5rem; border: 1px solid #e2e8f0; box-shadow: 0 1px 3px rgba(0,0,0,0.05);">
        <div style="color: #64748b; font-size: 0.85rem; font-weight: 600; text-transform: uppercase;">Active Therapy Plans</div>
        <div style="font-size: 2.2rem; font-weight: 700; color: #6366f1; margin-top: 0.5rem;">${fn:length(plans)}</div>
        <div style="font-size: 0.8rem; color: #64748b; margin-top: 0.25rem;">Graduated hierarchies</div>
    </div>
    <div style="background: white; border-radius: 12px; padding: 1.5rem; border: 1px solid #e2e8f0; box-shadow: 0 1px 3px rgba(0,0,0,0.05);">
        <div style="color: #64748b; font-size: 0.85rem; font-weight: 600; text-transform: uppercase;">Live VR Sessions</div>
        <div style="font-size: 2.2rem; font-weight: 700; color: #ea580c; margin-top: 0.5rem;">${fn:length(activeLiveSessions)}</div>
        <div style="font-size: 0.8rem; color: #ea580c; margin-top: 0.25rem;">
            <a href="${pageContext.request.contextPath}/therapist/live-control" style="color: inherit; text-decoration: underline;">Open Live Control Room &rarr;</a>
        </div>
    </div>
</div>

<!-- Primary Actions Grid -->
<div style="display: grid; grid-template-columns: 2fr 1fr; gap: 1.5rem; margin-bottom: 2rem;">
    <!-- Enrolled Patients Table -->
    <div style="background: white; border-radius: 12px; border: 1px solid #e2e8f0; padding: 1.5rem; box-shadow: 0 1px 3px rgba(0,0,0,0.05);">
        <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 1.25rem;">
            <h2 style="font-size: 1.2rem; font-weight: 700; color: #0f172a;">Assigned Patients</h2>
            <span style="font-size: 0.85rem; color: #64748b;">Clinical Monitoring</span>
        </div>

        <c:choose>
            <c:when test="${not empty patients}">
                <div style="overflow-x: auto;">
                    <table style="width: 100%; border-collapse: collapse; font-size: 0.9rem;">
                        <thead>
                            <tr style="border-bottom: 1px solid #e2e8f0; text-align: left; color: #64748b; font-size: 0.8rem; text-transform: uppercase;">
                                <th style="padding: 0.75rem 0.5rem;">Patient Name</th>
                                <th style="padding: 0.75rem 0.5rem;">Username</th>
                                <th style="padding: 0.75rem 0.5rem;">Email</th>
                                <th style="padding: 0.75rem 0.5rem;">Action</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="p" items="${patients}">
                                <tr style="border-bottom: 1px solid #f1f5f9;">
                                    <td style="padding: 0.85rem 0.5rem; font-weight: 600; color: #1e293b;">${p.fullName}</td>
                                    <td style="padding: 0.85rem 0.5rem; color: #64748b;">${p.username}</td>
                                    <td style="padding: 0.85rem 0.5rem; color: #64748b;">${p.email}</td>
                                    <td style="padding: 0.85rem 0.5rem;">
                                        <a href="${pageContext.request.contextPath}/therapist/progress?patientId=${p.id}"
                                           style="color: #0284c7; font-weight: 600; text-decoration: none; padding: 0.35rem 0.75rem; background: #e0f2fe; border-radius: 6px;">
                                            Analytics &rarr;
                                        </a>
                                    </td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </div>
            </c:when>
            <c:otherwise>
                <p style="color: #64748b; font-size: 0.9rem; padding: 1.5rem 0; text-align: center;">No patients assigned yet. Check back soon.</p>
            </c:otherwise>
        </c:choose>
    </div>

    <!-- Quick Clinical Actions -->
    <div style="display: flex; flex-direction: column; gap: 1rem;">
        <div style="background: white; border-radius: 12px; border: 1px solid #e2e8f0; padding: 1.5rem; box-shadow: 0 1px 3px rgba(0,0,0,0.05);">
            <h3 style="font-size: 1.05rem; font-weight: 700; color: #0f172a; margin-bottom: 1rem;">Clinical Actions</h3>
            <div style="display: flex; flex-direction: column; gap: 0.75rem;">
                <a href="${pageContext.request.contextPath}/therapist/session-design" class="btn btn-primary"
                   style="display: block; text-align: center; text-decoration: none; padding: 0.75rem; font-weight: 600; border-radius: 8px; background: #0284c7; color: white;">
                    ➕ Design New Session
                </a>
                <a href="${pageContext.request.contextPath}/therapist/plan" class="btn"
                   style="display: block; text-align: center; text-decoration: none; padding: 0.75rem; font-weight: 600; border-radius: 8px; background: #0d9488; color: white;">
                    📋 Build Therapy Plan
                </a>
                <a href="${pageContext.request.contextPath}/therapist/live-control" class="btn"
                   style="display: block; text-align: center; text-decoration: none; padding: 0.75rem; font-weight: 600; border-radius: 8px; background: #ea580c; color: white;">
                    ⚡ Open Live Session Room
                </a>
                <a href="${pageContext.request.contextPath}/therapist/feedback" class="btn"
                   style="display: block; text-align: center; text-decoration: none; padding: 0.75rem; font-weight: 600; border-radius: 8px; background: #6366f1; color: white;">
                    💡 Rule-Based Adjustments
                </a>
            </div>
        </div>

        <div style="background: #f8fafc; border-radius: 12px; border: 1px solid #e2e8f0; padding: 1.25rem;">
            <h4 style="font-size: 0.9rem; font-weight: 700; color: #334155; margin-bottom: 0.5rem;">Safety Monitoring</h4>
            <p style="font-size: 0.82rem; color: #64748b; line-height: 1.5;">
                Automated crisis detection triggers when heart-rate surges past 135 bpm or consecutive stress spikes occur. Emergency stop is accessible live at all times.
            </p>
        </div>
    </div>
</div>

<jsp:include page="../layout/footer.jsp" />
