<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<c:set var="pageTitle" value="Patient Wellbeing Portal" />
<jsp:include page="../layout/header.jsp" />

<div style="margin-bottom: 2rem; display: flex; justify-content: space-between; align-items: center;">
    <div>
        <h1 style="font-size: 1.85rem; font-weight: 700; color: #0f172a;">Patient Wellness Hub</h1>
        <p style="color: #64748b; margin-top: 0.25rem;">
            Welcome, <strong>${sessionScope.currentUser.fullName}</strong>. Track your progress, launch VR desensitization, and view earned achievements.
        </p>
    </div>
    <div>
        <a href="${pageContext.request.contextPath}/vr/launch"
           style="display: inline-flex; align-items: center; gap: 0.5rem; padding: 0.75rem 1.5rem; background: linear-gradient(135deg, #0284c7, #0d9488); color: white; border-radius: 10px; font-weight: 700; text-decoration: none; box-shadow: 0 4px 6px -1px rgba(2,132,199,0.3); font-size: 1rem;">
            🥽 Launch VR Session
        </a>
    </div>
</div>

<!-- Quick Stat Cards -->
<div style="display: grid; grid-template-columns: repeat(auto-fit, minmax(200px, 1fr)); gap: 1.25rem; margin-bottom: 2rem;">
    <div style="background: white; border-radius: 12px; padding: 1.5rem; border: 1px solid #e2e8f0; box-shadow: 0 1px 3px rgba(0,0,0,0.05);">
        <div style="color: #64748b; font-size: 0.8rem; font-weight: 600; text-transform: uppercase;">Completed Sessions</div>
        <div style="font-size: 2.2rem; font-weight: 700; color: #0284c7; margin-top: 0.5rem;">${fn:length(completedResults)}</div>
        <div style="font-size: 0.8rem; color: #10b981;">Total VR exposures</div>
    </div>
    <div style="background: white; border-radius: 12px; padding: 1.5rem; border: 1px solid #e2e8f0; box-shadow: 0 1px 3px rgba(0,0,0,0.05);">
        <div style="color: #64748b; font-size: 0.8rem; font-weight: 600; text-transform: uppercase;">Connected Devices</div>
        <div style="font-size: 2.2rem; font-weight: 700; color: #0d9488; margin-top: 0.5rem;">${fn:length(devices)}</div>
        <div style="font-size: 0.8rem; color: #64748b;">
            <a href="${pageContext.request.contextPath}/patient/device-pairing" style="color: inherit; text-decoration: underline;">Pair New Headset &rarr;</a>
        </div>
    </div>
    <div style="background: white; border-radius: 12px; padding: 1.5rem; border: 1px solid #e2e8f0; box-shadow: 0 1px 3px rgba(0,0,0,0.05);">
        <div style="color: #64748b; font-size: 0.8rem; font-weight: 600; text-transform: uppercase;">Earned Badges</div>
        <div style="font-size: 2.2rem; font-weight: 700; color: #eab308; margin-top: 0.5rem;">${fn:length(badges)}</div>
        <div style="font-size: 0.8rem; color: #64748b;">Milestones achieved</div>
    </div>
    <div style="background: white; border-radius: 12px; padding: 1.5rem; border: 1px solid #e2e8f0; box-shadow: 0 1px 3px rgba(0,0,0,0.05);">
        <div style="color: #64748b; font-size: 0.8rem; font-weight: 600; text-transform: uppercase;">Due Homework</div>
        <div style="font-size: 2.2rem; font-weight: 700; color: #6366f1; margin-top: 0.5rem;">${fn:length(homeworkList)}</div>
        <div style="font-size: 0.8rem; color: #64748b;">Active assignments</div>
    </div>
</div>

<!-- Earned Badges Showcase -->
<div style="background: white; border-radius: 12px; border: 1px solid #e2e8f0; padding: 1.5rem; margin-bottom: 2rem; box-shadow: 0 1px 3px rgba(0,0,0,0.05);">
    <h2 style="font-size: 1.2rem; font-weight: 700; color: #0f172a; margin-bottom: 1rem;">Achievement Badges &amp; Milestones</h2>
    <c:choose>
        <c:when test="${not empty badges}">
            <div style="display: flex; gap: 1rem; flex-wrap: wrap;">
                <c:forEach var="b" items="${badges}">
                    <div style="background: #fefce8; border: 1px solid #fef08a; padding: 1rem 1.25rem; border-radius: 10px; display: flex; align-items: center; gap: 0.75rem;">
                        <span style="font-size: 1.75rem;">${not empty b.iconName ? b.iconName : '🏅'}</span>
                        <div>
                            <div style="font-weight: 700; color: #854d0e; font-size: 0.95rem;">${b.title}</div>
                            <div style="color: #a16207; font-size: 0.8rem;">${b.description}</div>
                        </div>
                    </div>
                </c:forEach>
            </div>
        </c:when>
        <c:otherwise>
            <p style="color: #64748b; font-size: 0.9rem;">Complete your first VR session to unlock your initial milestone badge!</p>
        </c:otherwise>
    </c:choose>
</div>

<!-- Main Patient Grid -->
<div style="display: grid; grid-template-columns: 2fr 1fr; gap: 1.5rem; margin-bottom: 2rem;">
    <!-- Recent Session Outcomes -->
    <div style="background: white; border-radius: 12px; border: 1px solid #e2e8f0; padding: 1.5rem; box-shadow: 0 1px 3px rgba(0,0,0,0.05);">
        <h2 style="font-size: 1.2rem; font-weight: 700; color: #0f172a; margin-bottom: 1rem;">Recent Session Log</h2>
        <c:choose>
            <c:when test="${not empty completedResults}">
                <div style="overflow-x: auto;">
                    <table style="width: 100%; border-collapse: collapse; font-size: 0.9rem;">
                        <thead>
                            <tr style="border-bottom: 1px solid #e2e8f0; text-align: left; color: #64748b; font-size: 0.8rem; text-transform: uppercase;">
                                <th style="padding: 0.75rem 0.5rem;">Date</th>
                                <th style="padding: 0.75rem 0.5rem;">Duration</th>
                                <th style="padding: 0.75rem 0.5rem;">Stress (Pre &rarr; Post)</th>
                                <th style="padding: 0.75rem 0.5rem;">Mitigation</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="res" items="${completedResults}">
                                <tr style="border-bottom: 1px solid #f1f5f9;">
                                    <td style="padding: 0.85rem 0.5rem; color: #64748b;">${res.completedAt}</td>
                                    <td style="padding: 0.85rem 0.5rem; font-weight: 600;">${res.durationSeconds / 60} min</td>
                                    <td style="padding: 0.85rem 0.5rem;">
                                        <span style="color: #ef4444; font-weight: 700;">${res.preStressScore}</span>
                                        &rarr;
                                        <span style="color: #10b981; font-weight: 700;">${res.postStressScore}</span>
                                    </td>
                                    <td style="padding: 0.85rem 0.5rem; font-weight: 700; color: #0284c7;">
                                        -${res.preStressScore - res.postStressScore} pts (${res.improvementPercentage}%)
                                    </td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </div>
            </c:when>
            <c:otherwise>
                <p style="color: #64748b; font-size: 0.9rem; text-align: center; padding: 2rem 0;">No completed sessions recorded yet.</p>
            </c:otherwise>
        </c:choose>
    </div>

    <!-- Quick Patient Tools -->
    <div style="display: flex; flex-direction: column; gap: 1rem;">
        <div style="background: white; border-radius: 12px; border: 1px solid #e2e8f0; padding: 1.5rem; box-shadow: 0 1px 3px rgba(0,0,0,0.05);">
            <h3 style="font-size: 1.05rem; font-weight: 700; color: #0f172a; margin-bottom: 1rem;">Self-Care &amp; Practice</h3>
            <div style="display: flex; flex-direction: column; gap: 0.75rem;">
                <a href="${pageContext.request.contextPath}/patient/mood-journal" class="btn"
                   style="display: block; text-align: center; text-decoration: none; padding: 0.75rem; font-weight: 600; border-radius: 8px; background: #0284c7; color: white;">
                    📝 Daily Mood Journal
                </a>
                <a href="${pageContext.request.contextPath}/patient/thought-record" class="btn"
                   style="display: block; text-align: center; text-decoration: none; padding: 0.75rem; font-weight: 600; border-radius: 8px; background: #0d9488; color: white;">
                    🧠 CBT Thought Record
                </a>
                <a href="${pageContext.request.contextPath}/patient/device-pairing" class="btn"
                   style="display: block; text-align: center; text-decoration: none; padding: 0.75rem; font-weight: 600; border-radius: 8px; background: #6366f1; color: white;">
                    📱 VR Headset Pairing
                </a>
                <a href="${pageContext.request.contextPath}/patient/emergency-grounding" class="btn"
                   style="display: block; text-align: center; text-decoration: none; padding: 0.75rem; font-weight: 600; border-radius: 8px; background: #ef4444; color: white;">
                    🛑 Emergency Grounding
                </a>
            </div>
        </div>

        <div style="background: #f0fdf4; border: 1px solid #bbf7d0; border-radius: 12px; padding: 1.25rem;">
            <h4 style="font-size: 0.9rem; font-weight: 700; color: #166534; margin-bottom: 0.25rem;">Comfort First</h4>
            <p style="font-size: 0.82rem; color: #15803d; line-height: 1.5;">
                Remember you can remove your VR headset or press the in-VR red distress button at any second. Your safety is paramount.
            </p>
        </div>
    </div>
</div>

<jsp:include page="../layout/footer.jsp" />
