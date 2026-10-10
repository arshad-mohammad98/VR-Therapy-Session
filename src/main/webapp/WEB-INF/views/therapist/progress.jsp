<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<c:set var="pageTitle" value="Clinical Progress Analytics" />
<jsp:include page="../layout/header.jsp" />

<div style="margin-bottom: 2rem; display: flex; justify-content: space-between; align-items: flex-start;">
    <div>
        <h1 style="font-size: 1.85rem; font-weight: 700; color: #0f172a;">Patient Clinical Analytics Dashboard</h1>
        <p style="color: #64748b; margin-top: 0.25rem;">
            Longitudinal biometrics, pre/post autonomic stress mitigation curves, and formal clinical reports.
        </p>
    </div>

    <c:if test="${not empty selectedPatient}">
        <div style="display: flex; gap: 0.75rem;">
            <a href="${pageContext.request.contextPath}/therapist/report?patientId=${selectedPatient.id}&format=csv"
               style="padding: 0.5rem 1rem; background: #0284c7; color: white; border-radius: 8px; text-decoration: none; font-size: 0.875rem; font-weight: 600;">
                📥 Download CSV Log
            </a>
            <a href="${pageContext.request.contextPath}/therapist/report?patientId=${selectedPatient.id}&format=txt"
               style="padding: 0.5rem 1rem; background: #0d9488; color: white; border-radius: 8px; text-decoration: none; font-size: 0.875rem; font-weight: 600;">
                📄 Download Clinical Summary
            </a>
        </div>
    </c:if>
</div>

<!-- Patient Selector Bar -->
<div style="background: white; border-radius: 12px; border: 1px solid #e2e8f0; padding: 1.25rem; margin-bottom: 2rem; display: flex; align-items: center; gap: 1rem;">
    <span style="font-size: 0.9rem; font-weight: 600; color: #334155;">Active Patient:</span>
    <form action="${pageContext.request.contextPath}/therapist/progress" method="get" style="display: flex; gap: 0.5rem; flex: 1;">
        <select name="patientId" onchange="this.form.submit()"
                style="padding: 0.5rem 1rem; border-radius: 8px; border: 1px solid #cbd5e1; font-size: 0.95rem; max-width: 320px; background: white;">
            <c:forEach var="p" items="${patients}">
                <option value="${p.id}" ${p.id == selectedPatient.id ? 'selected' : ''}>${p.fullName} (${p.username})</option>
            </c:forEach>
        </select>
    </form>
</div>

<c:choose>
    <c:when test="${not empty progress}">
        <!-- Statistical Overview Cards -->
        <div style="display: grid; grid-template-columns: repeat(auto-fit, minmax(200px, 1fr)); gap: 1.25rem; margin-bottom: 2rem;">
            <div style="background: white; border-radius: 12px; padding: 1.25rem; border: 1px solid #e2e8f0; text-align: center;">
                <div style="color: #64748b; font-size: 0.8rem; font-weight: 600; text-transform: uppercase;">Completion Rate</div>
                <div style="font-size: 2.2rem; font-weight: 700; color: #0284c7; margin-top: 0.25rem;">${progress.completionRate}%</div>
                <div style="font-size: 0.75rem; color: #64748b;">${progress.totalCompleted} / ${progress.totalAssigned} Sessions</div>
            </div>
            <div style="background: white; border-radius: 12px; padding: 1.25rem; border: 1px solid #e2e8f0; text-align: center;">
                <div style="color: #64748b; font-size: 0.8rem; font-weight: 600; text-transform: uppercase;">Average Pre-Stress</div>
                <div style="font-size: 2.2rem; font-weight: 700; color: #ef4444; margin-top: 0.25rem;">${progress.averagePreStress}</div>
                <div style="font-size: 0.75rem; color: #64748b;">Baseline Distress (/10)</div>
            </div>
            <div style="background: white; border-radius: 12px; padding: 1.25rem; border: 1px solid #e2e8f0; text-align: center;">
                <div style="color: #64748b; font-size: 0.8rem; font-weight: 600; text-transform: uppercase;">Average Post-Stress</div>
                <div style="font-size: 2.2rem; font-weight: 700; color: #10b981; margin-top: 0.25rem;">${progress.averagePostStress}</div>
                <div style="font-size: 0.75rem; color: #64748b;">Post-Session State (/10)</div>
            </div>
            <div style="background: white; border-radius: 12px; padding: 1.25rem; border: 1px solid #e2e8f0; text-align: center;">
                <div style="color: #64748b; font-size: 0.8rem; font-weight: 600; text-transform: uppercase;">Mean Improvement</div>
                <div style="font-size: 2.2rem; font-weight: 700; color: #6366f1; margin-top: 0.25rem;">${progress.improvementPercentage}%</div>
                <div style="font-size: 0.75rem; color: #64748b;">Autonomic Mitigation</div>
            </div>
            <div style="background: white; border-radius: 12px; padding: 1.25rem; border: 1px solid #e2e8f0; text-align: center;">
                <div style="color: #64748b; font-size: 0.8rem; font-weight: 600; text-transform: uppercase;">Clinical Trend</div>
                <div style="font-size: 1.1rem; font-weight: 700; color: #0d9488; margin-top: 0.75rem;">${progress.overallTrend}</div>
                <div style="font-size: 0.75rem; color: #64748b;">Algorithmic Classification</div>
            </div>
        </div>

        <!-- Chart.js Trend Visualizer -->
        <div style="background: white; border-radius: 12px; border: 1px solid #e2e8f0; padding: 2rem; margin-bottom: 2rem; box-shadow: 0 1px 3px rgba(0,0,0,0.05);">
            <h2 style="font-size: 1.2rem; font-weight: 700; color: #0f172a; margin-bottom: 1.5rem;">Pre vs Post Session Stress Trajectory</h2>
            <div style="height: 320px; position: relative;">
                <canvas id="progressChart"></canvas>
            </div>
        </div>

        <script>
        const ctxP = document.getElementById('progressChart').getContext('2d');
        new Chart(ctxP, {
            type: 'bar',
            data: {
                labels: [
                    <c:forEach var="lbl" items="${progress.chartLabels}" varStatus="st">
                        "${lbl}"<c:if test="${!st.last}">,</c:if>
                    </c:forEach>
                ],
                datasets: [
                    {
                        label: 'Pre-Session Stress (1-10)',
                        data: [
                            <c:forEach var="pt" items="${progress.preStressPoints}" varStatus="st">
                                ${pt}<c:if test="${!st.last}">,</c:if>
                            </c:forEach>
                        ],
                        backgroundColor: 'rgba(239, 68, 68, 0.6)',
                        borderColor: '#ef4444',
                        borderWidth: 1
                    },
                    {
                        label: 'Post-Session Stress (1-10)',
                        data: [
                            <c:forEach var="pt" items="${progress.postStressPoints}" varStatus="st">
                                ${pt}<c:if test="${!st.last}">,</c:if>
                            </c:forEach>
                        ],
                        backgroundColor: 'rgba(16, 185, 129, 0.6)',
                        borderColor: '#10b981',
                        borderWidth: 1
                    }
                ]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                scales: {
                    y: { min: 0, max: 10, title: { display: true, text: 'Subjective Units of Distress (SUDs)' } }
                }
            }
        });
        </script>

        <!-- Session History Log Table -->
        <div style="background: white; border-radius: 12px; border: 1px solid #e2e8f0; padding: 2rem; box-shadow: 0 1px 3px rgba(0,0,0,0.05);">
            <h2 style="font-size: 1.2rem; font-weight: 700; color: #0f172a; margin-bottom: 1.5rem;">Completed Session Log</h2>
            <c:choose>
                <c:when test="${not empty progress.results}">
                    <div style="overflow-x: auto;">
                        <table style="width: 100%; border-collapse: collapse; font-size: 0.9rem;">
                            <thead>
                                <tr style="border-bottom: 1px solid #e2e8f0; text-align: left; color: #64748b; font-size: 0.8rem; text-transform: uppercase;">
                                    <th style="padding: 0.75rem 0.5rem;">Date</th>
                                    <th style="padding: 0.75rem 0.5rem;">Duration</th>
                                    <th style="padding: 0.75rem 0.5rem;">Pre-Stress</th>
                                    <th style="padding: 0.75rem 0.5rem;">Post-Stress</th>
                                    <th style="padding: 0.75rem 0.5rem;">Delta</th>
                                    <th style="padding: 0.75rem 0.5rem;">Improvement</th>
                                    <th style="padding: 0.75rem 0.5rem;">Vitals (Avg/Peak)</th>
                                    <th style="padding: 0.75rem 0.5rem;">Status</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach var="res" items="${progress.results}">
                                    <tr style="border-bottom: 1px solid #f1f5f9;">
                                        <td style="padding: 0.85rem 0.5rem; color: #64748b;">${res.completedAt}</td>
                                        <td style="padding: 0.85rem 0.5rem; font-weight: 600;">${res.durationSeconds / 60} mins</td>
                                        <td style="padding: 0.85rem 0.5rem; color: #ef4444; font-weight: 700;">${res.preStressScore} / 10</td>
                                        <td style="padding: 0.85rem 0.5rem; color: #10b981; font-weight: 700;">${res.postStressScore} / 10</td>
                                        <td style="padding: 0.85rem 0.5rem; font-weight: 700; color: ${res.stressDelta >= 0 ? '#10b981' : '#ef4444'};">
                                            -${res.preStressScore - res.postStressScore}
                                        </td>
                                        <td style="padding: 0.85rem 0.5rem; font-weight: 700; color: #0284c7;">
                                            ${res.improvementPercentage}%
                                        </td>
                                        <td style="padding: 0.85rem 0.5rem; color: #475569;">
                                            ${res.averageHeartRate} / ${res.peakHeartRate} bpm
                                        </td>
                                        <td style="padding: 0.85rem 0.5rem;">
                                            <c:choose>
                                                <c:when test="${res.emergencyStopped}">
                                                    <span class="badge" style="background: #fee2e2; color: #991b1b;">ABORTED</span>
                                                </c:when>
                                                <c:otherwise>
                                                    <span class="badge badge-success">COMPLETED</span>
                                                </c:otherwise>
                                            </c:choose>
                                        </td>
                                    </tr>
                                </c:forEach>
                            </tbody>
                        </table>
                    </div>
                </c:when>
                <c:otherwise>
                    <p style="color: #64748b; font-size: 0.9rem; text-align: center; padding: 2rem 0;">No completed sessions recorded yet for this patient.</p>
                </c:otherwise>
            </c:choose>
        </div>
    </c:when>
    <c:otherwise>
        <p style="color: #64748b; font-size: 0.9rem; text-align: center; padding: 2rem 0;">No patient selected.</p>
    </c:otherwise>
</c:choose>

<jsp:include page="../layout/footer.jsp" />
