<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<c:set var="pageTitle" value="Session Feedback & Rating" />
<jsp:include page="../layout/header.jsp" />

<div style="margin-bottom: 2rem;">
    <h1 style="font-size: 1.85rem; font-weight: 700; color: #0f172a;">Session Experience &amp; Simulation Feedback</h1>
    <p style="color: #64748b; margin-top: 0.25rem;">
        Provide feedback on comfort, presence, and motion sickness so your therapist can adjust protocol intensity.
    </p>
</div>

<c:if test="${param.submitted eq 'true'}">
    <div class="alert alert-success" style="margin-bottom: 1.5rem; padding: 1rem; background: #d1fae5; color: #065f46; border-radius: 8px; border: 1px solid #34d399;">
        ✓ Thank you! Your feedback has been saved and analyzed by clinical recommendation rules.
    </div>
</c:if>

<div style="display: grid; grid-template-columns: 1.2fr 1.8fr; gap: 2rem;">
    <!-- Feedback Submission Form -->
    <div style="background: white; border-radius: 12px; border: 1px solid #e2e8f0; padding: 2rem; box-shadow: 0 1px 3px rgba(0,0,0,0.05); height: fit-content;">
        <h2 style="font-size: 1.25rem; font-weight: 700; color: #0f172a; margin-bottom: 1.5rem;">Submit Experience Rating</h2>

        <form action="${pageContext.request.contextPath}/feedback" method="post" style="display: flex; flex-direction: column; gap: 1.25rem;">
            <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}" />
            <input type="hidden" name="sessionId" value="${not empty sessionId ? sessionId : 1}" />

            <div>
                <label for="rating" style="display: block; font-size: 0.875rem; font-weight: 600; color: #334155; margin-bottom: 0.35rem;">Overall Session Rating (1 to 5 Stars)</label>
                <select id="rating" name="rating" required style="width: 100%; padding: 0.75rem 1rem; border-radius: 8px; border: 1px solid #cbd5e1; font-size: 0.95rem; background: white;">
                    <option value="5" selected>★★★★★ - 5 Stars (Excellent Comfort & Flow)</option>
                    <option value="4">★★★★☆ - 4 Stars (Good Experience)</option>
                    <option value="3">★★★☆☆ - 3 Stars (Acceptable / Neutral)</option>
                    <option value="2">★★☆☆☆ - 2 Stars (Somewhat Uncomfortable / Intense)</option>
                    <option value="1">★☆☆☆☆ - 1 Star (Distressing / Too Challenging)</option>
                </select>
            </div>

            <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 1rem;">
                <div>
                    <label for="comfortScore" style="display: block; font-size: 0.875rem; font-weight: 600; color: #334155; margin-bottom: 0.35rem;">Immersion Comfort</label>
                    <select id="comfortScore" name="comfortScore" required style="width: 100%; padding: 0.75rem 1rem; border-radius: 8px; border: 1px solid #cbd5e1; font-size: 0.9rem; background: white;">
                        <option value="5" selected>5 - Completely Serene</option>
                        <option value="4">4 - High Comfort</option>
                        <option value="3">3 - Moderate</option>
                        <option value="2">2 - Low Comfort</option>
                        <option value="1">1 - Highly Uncomfortable</option>
                    </select>
                </div>
                <div>
                    <label for="nauseaScore" style="display: block; font-size: 0.875rem; font-weight: 600; color: #334155; margin-bottom: 0.35rem;">Simulation Sickness</label>
                    <select id="nauseaScore" name="nauseaScore" required style="width: 100%; padding: 0.75rem 1rem; border-radius: 8px; border: 1px solid #cbd5e1; font-size: 0.9rem; background: white;">
                        <option value="1" selected>1 - Zero Nausea / Crisp</option>
                        <option value="2">2 - Very Mild Sensation</option>
                        <option value="3">3 - Noticeable Motion Blur</option>
                        <option value="4">4 - Uncomfortable Dizziness</option>
                        <option value="5">5 - Severe Simulation Sickness</option>
                    </select>
                </div>
            </div>

            <div>
                <label for="comments" style="display: block; font-size: 0.875rem; font-weight: 600; color: #334155; margin-bottom: 0.35rem;">Notes for Therapist</label>
                <textarea id="comments" name="comments" rows="3" placeholder="Describe how the environment felt or any physical sensations you noticed..."
                          style="width: 100%; padding: 0.75rem 1rem; border-radius: 8px; border: 1px solid #cbd5e1; font-size: 0.95rem;"></textarea>
            </div>

            <button type="submit" class="btn btn-primary"
                    style="width: 100%; padding: 0.85rem; font-size: 1rem; font-weight: 600; border-radius: 8px; background: #0284c7; color: white; border: none; cursor: pointer;">
                Submit Feedback
            </button>
        </form>
    </div>

    <!-- Past Submissions History -->
    <div style="background: white; border-radius: 12px; border: 1px solid #e2e8f0; padding: 2rem; box-shadow: 0 1px 3px rgba(0,0,0,0.05);">
        <h2 style="font-size: 1.25rem; font-weight: 700; color: #0f172a; margin-bottom: 1.5rem;">Your Feedback History (${fn:length(feedbacks)})</h2>

        <c:choose>
            <c:when test="${not empty feedbacks}">
                <div style="display: flex; flex-direction: column; gap: 1rem;">
                    <c:forEach var="fb" items="${feedbacks}">
                        <div style="border: 1px solid #e2e8f0; border-radius: 10px; padding: 1.25rem; background: #f8fafc;">
                            <div style="display: flex; justify-content: space-between; align-items: center;">
                                <div>
                                    <span style="color: #f59e0b; font-weight: 700;">
                                        <c:forEach begin="1" end="${fb.rating}">★</c:forEach>
                                    </span>
                                    <span style="margin-left: 0.5rem; font-size: 0.85rem; color: #64748b;">
                                        Comfort: ${fb.comfortScore}/5 &bull; Motion Sickness: ${fb.nauseaScore}/5
                                    </span>
                                </div>
                            </div>
                            <c:if test="${not empty fb.comments}">
                                <p style="color: #334155; font-size: 0.875rem; margin-top: 0.5rem; font-style: italic;">
                                    &ldquo;${fb.comments}&rdquo;
                                </p>
                            </c:if>
                        </div>
                    </c:forEach>
                </div>
            </c:when>
            <c:otherwise>
                <p style="color: #64748b; font-size: 0.9rem; text-align: center; padding: 2rem 0;">No feedback submitted yet.</p>
            </c:otherwise>
        </c:choose>
    </div>
</div>

<jsp:include page="../layout/footer.jsp" />
