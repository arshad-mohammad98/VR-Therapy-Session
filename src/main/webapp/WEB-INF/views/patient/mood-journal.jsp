<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<c:set var="pageTitle" value="Mood Journal & Affect Tracker" />
<jsp:include page="../layout/header.jsp" />

<div style="margin-bottom: 2rem;">
    <h1 style="font-size: 1.85rem; font-weight: 700; color: #0f172a;">Daily Mood &amp; Affect Journal</h1>
    <p style="color: #64748b; margin-top: 0.25rem;">
        Track valence, somatic energy, and cognitive mood patterns alongside your virtual reality therapy.
    </p>
</div>

<div style="display: grid; grid-template-columns: 1.2fr 1.8fr; gap: 2rem;">
    <!-- Log Mood Form -->
    <div style="background: white; border-radius: 12px; border: 1px solid #e2e8f0; padding: 2rem; box-shadow: 0 1px 3px rgba(0,0,0,0.05); height: fit-content;">
        <h2 style="font-size: 1.25rem; font-weight: 700; color: #0f172a; margin-bottom: 1.5rem;">Record Current Mood</h2>

        <form action="${pageContext.request.contextPath}/patient/mood-journal" method="post" style="display: flex; flex-direction: column; gap: 1.25rem;">
            <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}" />

            <div>
                <label style="display: block; font-size: 0.875rem; font-weight: 600; color: #334155; margin-bottom: 0.35rem;">Mood Rating (1 - 10)</label>
                <div style="display: flex; align-items: center; gap: 1rem;">
                    <span id="scoreVal" style="font-size: 1.75rem; font-weight: 800; color: #0284c7; width: 45px;">7</span>
                    <input type="range" name="score" min="1" max="10" value="7"
                           style="flex: 1; accent-color: #0284c7;"
                           oninput="document.getElementById('scoreVal').innerText = this.value" />
                </div>
            </div>

            <div>
                <label style="display: block; font-size: 0.875rem; font-weight: 600; color: #334155; margin-bottom: 0.35rem;">Energy Level (1 - 10)</label>
                <div style="display: flex; align-items: center; gap: 1rem;">
                    <span id="energyVal" style="font-size: 1.75rem; font-weight: 800; color: #10b981; width: 45px;">6</span>
                    <input type="range" name="energy" min="1" max="10" value="6"
                           style="flex: 1; accent-color: #10b981;"
                           oninput="document.getElementById('energyVal').innerText = this.value" />
                </div>
            </div>

            <div>
                <label for="primaryEmotion" style="display: block; font-size: 0.875rem; font-weight: 600; color: #334155; margin-bottom: 0.35rem;">Primary Affect / Emotion</label>
                <select id="primaryEmotion" name="primaryEmotion" required
                        style="width: 100%; padding: 0.75rem 1rem; border-radius: 8px; border: 1px solid #cbd5e1; font-size: 0.95rem; background: white;">
                    <option value="Calm & Grounded">Calm &amp; Grounded</option>
                    <option value="Hopeful & Optimistic">Hopeful &amp; Optimistic</option>
                    <option value="Mild Anxiety">Mild Anxiety</option>
                    <option value="Elevated Stress">Elevated Stress</option>
                    <option value="Exhausted / Low Energy">Exhausted / Low Energy</option>
                    <option value="Agitated / Tense">Agitated / Tense</option>
                </select>
            </div>

            <div>
                <label for="notes" style="display: block; font-size: 0.875rem; font-weight: 600; color: #334155; margin-bottom: 0.35rem;">Reflective Journal Notes</label>
                <textarea id="notes" name="notes" rows="3" placeholder="What factors or thoughts are influencing how you feel right now?"
                          style="width: 100%; padding: 0.75rem 1rem; border-radius: 8px; border: 1px solid #cbd5e1; font-size: 0.95rem;"></textarea>
            </div>

            <button type="submit" class="btn btn-primary"
                    style="width: 100%; padding: 0.85rem; font-size: 1rem; font-weight: 600; border-radius: 8px; background: #0284c7; color: white; border: none; cursor: pointer;">
                Save Mood Entry
            </button>
        </form>
    </div>

    <!-- Mood History & Chart -->
    <div style="display: flex; flex-direction: column; gap: 1.5rem;">
        <!-- Chart.js Mood Curve -->
        <div style="background: white; border-radius: 12px; border: 1px solid #e2e8f0; padding: 1.5rem; box-shadow: 0 1px 3px rgba(0,0,0,0.05);">
            <h3 style="font-size: 1.1rem; font-weight: 700; color: #0f172a; margin-bottom: 1rem;">Valence &amp; Energy Trends</h3>
            <div style="height: 220px; position: relative;">
                <canvas id="moodChart"></canvas>
            </div>
        </div>

        <!-- History Feed -->
        <div style="background: white; border-radius: 12px; border: 1px solid #e2e8f0; padding: 1.5rem; box-shadow: 0 1px 3px rgba(0,0,0,0.05);">
            <h3 style="font-size: 1.1rem; font-weight: 700; color: #0f172a; margin-bottom: 1rem;">Past Entries (${fn:length(entries)})</h3>
            <c:choose>
                <c:when test="${not empty entries}">
                    <div style="display: flex; flex-direction: column; gap: 0.75rem; max-height: 350px; overflow-y: auto;">
                        <c:forEach var="m" items="${entries}">
                            <div style="background: #f8fafc; border: 1px solid #e2e8f0; border-radius: 8px; padding: 1rem;">
                                <div style="display: flex; justify-content: space-between; align-items: center;">
                                    <div>
                                        <span style="font-weight: 700; color: #1e293b; font-size: 0.95rem;">${m.primaryEmotion}</span>
                                        <span class="badge badge-info" style="margin-left: 0.5rem;">Score: ${m.score}/10</span>
                                        <span class="badge" style="background: #d1fae5; color: #065f46; margin-left: 0.25rem;">Energy: ${m.energyLevel}/10</span>
                                    </div>
                                    <span style="font-size: 0.8rem; color: #64748b;">${m.recordedAt}</span>
                                </div>
                                <c:if test="${not empty m.notes}">
                                    <p style="color: #475569; font-size: 0.85rem; margin-top: 0.5rem;">${m.notes}</p>
                                </c:if>
                            </div>
                        </c:forEach>
                    </div>
                </c:when>
                <c:otherwise>
                    <p style="color: #64748b; font-size: 0.9rem; text-align: center; padding: 1.5rem 0;">No mood entries recorded yet.</p>
                </c:otherwise>
            </c:choose>
        </div>
    </div>
</div>

<script>
const ctxM = document.getElementById('moodChart').getContext('2d');
new Chart(ctxM, {
    type: 'line',
    data: {
        labels: [
            <c:forEach var="m" items="${entries}" varStatus="st">
                "${m.recordedAt}"<c:if test="${!st.last}">,</c:if>
            </c:forEach>
        ],
        datasets: [
            {
                label: 'Mood Score (1-10)',
                data: [
                    <c:forEach var="m" items="${entries}" varStatus="st">
                        ${m.score}<c:if test="${!st.last}">,</c:if>
                    </c:forEach>
                ],
                borderColor: '#0284c7',
                backgroundColor: 'rgba(2, 132, 199, 0.1)',
                tension: 0.3
            },
            {
                label: 'Energy (1-10)',
                data: [
                    <c:forEach var="m" items="${entries}" varStatus="st">
                        ${m.energyLevel}<c:if test="${!st.last}">,</c:if>
                    </c:forEach>
                ],
                borderColor: '#10b981',
                backgroundColor: 'rgba(16, 185, 129, 0.1)',
                tension: 0.3
            }
        ]
    },
    options: {
        responsive: true,
        maintainAspectRatio: false,
        scales: {
            y: { min: 0, max: 10 }
        }
    }
});
</script>

<jsp:include page="../layout/footer.jsp" />
