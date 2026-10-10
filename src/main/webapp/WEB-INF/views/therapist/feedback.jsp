<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<c:set var="pageTitle" value="Feedback & Clinical Suggestions" />
<jsp:include page="../layout/header.jsp" />

<div style="margin-bottom: 2rem;">
    <h1 style="font-size: 1.85rem; font-weight: 700; color: #0f172a;">Session Feedback &amp; Algorithmic Suggestions</h1>
    <p style="color: #64748b; margin-top: 0.25rem;">
        Patient subjective feedback analyzed by Java clinical rules engines to suggest protocol tunings.
    </p>
</div>

<c:if test="${param.applied eq 'true'}">
    <div class="alert alert-success" style="margin-bottom: 1.5rem; padding: 1rem; background: #d1fae5; color: #065f46; border-radius: 8px; border: 1px solid #34d399;">
        ✓ Clinical suggestion adjustment successfully applied to session parameters!
    </div>
</c:if>

<div style="display: grid; grid-template-columns: 1.4fr 1.6fr; gap: 2rem;">
    <!-- Rule-Based Suggestions Panel -->
    <div style="background: white; border-radius: 12px; border: 1px solid #e2e8f0; padding: 2rem; box-shadow: 0 1px 3px rgba(0,0,0,0.05);">
        <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 1.5rem;">
            <h2 style="font-size: 1.25rem; font-weight: 700; color: #0f172a;">Java Rule-Based Suggestions</h2>
            <span class="badge" style="background: #e0f2fe; color: #0369a1; font-weight: 600;">Automated Strategy</span>
        </div>

        <c:choose>
            <c:when test="${not empty sessions}">
                <div style="display: flex; flex-direction: column; gap: 1.25rem;">
                    <c:forEach var="s" items="${sessions}">
                        <div style="border: 1px solid #e2e8f0; border-radius: 10px; padding: 1.25rem; background: #f8fafc;">
                            <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 0.75rem;">
                                <h3 style="font-size: 1.05rem; font-weight: 700; color: #1e293b;">${s.title}</h3>
                                <span class="badge badge-info" style="font-size: 0.75rem;">Current Intensity: ${s.intensityLevel}/5</span>
                            </div>

                            <c:set var="suggestions" value="${suggestionsMap[s.id]}" />
                            <c:choose>
                                <c:when test="${not empty suggestions}">
                                    <div style="display: flex; flex-direction: column; gap: 0.75rem;">
                                        <c:forEach var="sug" items="${suggestions}">
                                            <div style="background: white; border: 1px solid #cbd5e1; border-radius: 8px; padding: 1rem;">
                                                <div style="display: flex; justify-content: space-between; align-items: flex-start;">
                                                    <div>
                                                        <div style="font-weight: 700; color: #0f172a; font-size: 0.95rem;">${sug.title}</div>
                                                        <p style="color: #475569; font-size: 0.85rem; margin-top: 0.25rem;">${sug.recommendationText}</p>
                                                        <div style="color: #64748b; font-size: 0.78rem; margin-top: 0.35rem; font-style: italic;">
                                                            Rationale: ${sug.clinicalRationale}
                                                        </div>
                                                    </div>
                                                </div>

                                                <form action="${pageContext.request.contextPath}/therapist/feedback" method="post" style="margin-top: 0.75rem; text-align: right;">
                                                    <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}" />
                                                    <input type="hidden" name="action" value="applyAdjustment" />
                                                    <input type="hidden" name="sessionId" value="${s.id}" />
                                                    <input type="hidden" name="intensity" value="${sug.targetIntensity}" />
                                                    <input type="hidden" name="duration" value="${sug.targetDurationMinutes}" />
                                                    <button type="submit" style="padding: 0.4rem 0.85rem; background: #10b981; color: white; border: none; border-radius: 6px; font-size: 0.82rem; font-weight: 700; cursor: pointer;">
                                                        ⚡ Apply Adjustment
                                                    </button>
                                                </form>
                                            </div>
                                        </c:forEach>
                                    </div>
                                </c:when>
                                <c:otherwise>
                                    <p style="color: #64748b; font-size: 0.85rem; margin: 0;">No active adjustments needed. Current telemetry is within stable limits.</p>
                                </c:otherwise>
                            </c:choose>
                        </div>
                    </c:forEach>
                </div>
            </c:when>
            <c:otherwise>
                <p style="color: #64748b; font-size: 0.9rem; text-align: center; padding: 2rem 0;">No sessions available to analyze.</p>
            </c:otherwise>
        </c:choose>
    </div>

    <!-- Patient Feedback Records -->
    <div style="background: white; border-radius: 12px; border: 1px solid #e2e8f0; padding: 2rem; box-shadow: 0 1px 3px rgba(0,0,0,0.05);">
        <h2 style="font-size: 1.25rem; font-weight: 700; color: #0f172a; margin-bottom: 1.5rem;">Recent Patient Feedback (${fn:length(feedbacks)})</h2>

        <c:choose>
            <c:when test="${not empty feedbacks}">
                <div style="display: flex; flex-direction: column; gap: 1rem;">
                    <c:forEach var="fb" items="${feedbacks}">
                        <div style="border: 1px solid #e2e8f0; border-radius: 10px; padding: 1.25rem; background: #ffffff;">
                            <div style="display: flex; justify-content: space-between; align-items: center;">
                                <div style="display: flex; align-items: center; gap: 0.5rem;">
                                    <span style="font-weight: 700; color: #1e293b; font-size: 0.95rem;">Rating:</span>
                                    <span style="color: #f59e0b; font-weight: 700; font-size: 1rem;">
                                        <c:forEach begin="1" end="${fb.rating}">★</c:forEach>
                                    </span>
                                </div>
                                <div style="font-size: 0.8rem; color: #64748b;">
                                    Comfort: <strong>${fb.comfortScore}/5</strong> &bull; Nausea: <strong>${fb.nauseaScore}/5</strong>
                                </div>
                            </div>
                            <c:if test="${not empty fb.comments}">
                                <p style="color: #334155; font-size: 0.875rem; margin-top: 0.5rem; background: #f8fafc; padding: 0.75rem; border-radius: 6px;">
                                    &ldquo;${fb.comments}&rdquo;
                                </p>
                            </c:if>
                        </div>
                    </c:forEach>
                </div>
            </c:when>
            <c:otherwise>
                <p style="color: #64748b; font-size: 0.9rem; text-align: center; padding: 2rem 0;">No feedback entries submitted yet.</p>
            </c:otherwise>
        </c:choose>
    </div>
</div>

<jsp:include page="../layout/footer.jsp" />
