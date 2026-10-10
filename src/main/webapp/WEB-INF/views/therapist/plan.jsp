<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<c:set var="pageTitle" value="Therapy Plans" />
<jsp:include page="../layout/header.jsp" />

<div style="margin-bottom: 2rem;">
    <h1 style="font-size: 1.85rem; font-weight: 700; color: #0f172a;">Multi-Week Therapy Treatment Plans</h1>
    <p style="color: #64748b; margin-top: 0.25rem;">Orchestrate multi-week clinical trajectories with ordered exposure ladders.</p>
</div>

<div style="display: grid; grid-template-columns: 1fr 2fr; gap: 2rem;">
    <!-- Create Plan Form -->
    <div style="background: white; border-radius: 12px; border: 1px solid #e2e8f0; padding: 2rem; box-shadow: 0 1px 3px rgba(0,0,0,0.05); height: fit-content;">
        <h2 style="font-size: 1.2rem; font-weight: 700; color: #0f172a; margin-bottom: 1.5rem;">New Therapy Plan</h2>

        <form action="${pageContext.request.contextPath}/therapist/plan" method="post" style="display: flex; flex-direction: column; gap: 1.25rem;">
            <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}" />

            <div>
                <label for="patientId" style="display: block; font-size: 0.875rem; font-weight: 600; color: #334155; margin-bottom: 0.35rem;">Select Patient</label>
                <select id="patientId" name="patientId" required
                        style="width: 100%; padding: 0.75rem 1rem; border-radius: 8px; border: 1px solid #cbd5e1; font-size: 0.95rem; background: white;">
                    <c:forEach var="p" items="${patients}">
                        <option value="${p.id}">${p.fullName} (${p.username})</option>
                    </c:forEach>
                </select>
            </div>

            <div>
                <label for="title" style="display: block; font-size: 0.875rem; font-weight: 600; color: #334155; margin-bottom: 0.35rem;">Plan Name</label>
                <input type="text" id="title" name="title" required placeholder="e.g. 6-Week Acrophobia Desensitization"
                       style="width: 100%; padding: 0.75rem 1rem; border-radius: 8px; border: 1px solid #cbd5e1; font-size: 0.95rem;" />
            </div>

            <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 1rem;">
                <div>
                    <label for="conditionType" style="display: block; font-size: 0.875rem; font-weight: 600; color: #334155; margin-bottom: 0.35rem;">Condition</label>
                    <select id="conditionType" name="conditionType" required
                            style="width: 100%; padding: 0.75rem 1rem; border-radius: 8px; border: 1px solid #cbd5e1; font-size: 0.9rem; background: white;">
                        <c:forEach var="c" items="${conditions}">
                            <option value="${c.name()}">${c.displayName}</option>
                        </c:forEach>
                    </select>
                </div>
                <div>
                    <label for="totalWeeks" style="display: block; font-size: 0.875rem; font-weight: 600; color: #334155; margin-bottom: 0.35rem;">Duration (Weeks)</label>
                    <input type="number" id="totalWeeks" name="totalWeeks" min="1" max="52" value="6" required
                           style="width: 100%; padding: 0.75rem 1rem; border-radius: 8px; border: 1px solid #cbd5e1; font-size: 0.95rem;" />
                </div>
            </div>

            <div>
                <label for="goals" style="display: block; font-size: 0.875rem; font-weight: 600; color: #334155; margin-bottom: 0.35rem;">Clinical Objectives</label>
                <textarea id="goals" name="goals" rows="3" placeholder="Expected desensitization milestones..."
                          style="width: 100%; padding: 0.75rem 1rem; border-radius: 8px; border: 1px solid #cbd5e1; font-size: 0.95rem;"></textarea>
            </div>

            <button type="submit" class="btn btn-primary"
                    style="width: 100%; padding: 0.85rem; font-size: 1rem; font-weight: 600; border-radius: 8px; background: #0d9488; color: white; border: none; cursor: pointer;">
                Create Plan &amp; Configure Ladder &rarr;
            </button>
        </form>
    </div>

    <!-- Existing Treatment Plans -->
    <div style="background: white; border-radius: 12px; border: 1px solid #e2e8f0; padding: 2rem; box-shadow: 0 1px 3px rgba(0,0,0,0.05);">
        <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 1.5rem;">
            <h2 style="font-size: 1.2rem; font-weight: 700; color: #0f172a;">Active Treatment Plans (${fn:length(plans)})</h2>
        </div>

        <c:choose>
            <c:when test="${not empty plans}">
                <div style="display: flex; flex-direction: column; gap: 1.25rem;">
                    <c:forEach var="p" items="${plans}">
                        <div style="background: #f8fafc; border: 1px solid #e2e8f0; border-radius: 10px; padding: 1.5rem;">
                            <div style="display: flex; justify-content: space-between; align-items: flex-start;">
                                <div>
                                    <div style="display: flex; align-items: center; gap: 0.75rem;">
                                        <h3 style="font-size: 1.15rem; font-weight: 700; color: #1e293b;">${p.title}</h3>
                                        <span class="badge badge-info">${p.conditionType.displayName}</span>
                                        <span class="badge" style="background: #e0f2fe; color: #0369a1;">${p.totalWeeks} Weeks</span>
                                    </div>
                                    <p style="color: #64748b; font-size: 0.9rem; margin-top: 0.5rem;">
                                        <strong>Goal:</strong> ${p.goals}
                                    </p>
                                </div>
                                <div>
                                    <a href="${pageContext.request.contextPath}/therapist/hierarchy?planId=${p.id}"
                                       style="display: inline-block; padding: 0.5rem 1rem; background: #0284c7; color: white; border-radius: 8px; text-decoration: none; font-size: 0.875rem; font-weight: 600;">
                                        🪜 Manage Hierarchy Ladder &rarr;
                                    </a>
                                </div>
                            </div>
                        </div>
                    </c:forEach>
                </div>
            </c:when>
            <c:otherwise>
                <p style="color: #64748b; font-size: 0.9rem; text-align: center; padding: 2rem 0;">No plans created yet.</p>
            </c:otherwise>
        </c:choose>
    </div>
</div>

<jsp:include page="../layout/footer.jsp" />
