<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Emergency Grounding Sanctuary" />
<jsp:include page="../layout/header.jsp" />

<div style="max-width: 680px; margin: 2rem auto; background: white; border-radius: 16px; border: 1px solid #fee2e2; padding: 2.5rem; box-shadow: 0 10px 25px -5px rgba(239, 68, 68, 0.1);">
    <div style="text-align: center; margin-bottom: 2rem;">
        <div style="display: inline-flex; align-items: center; justify-content: center; width: 64px; height: 64px; border-radius: 50%; background: #fee2e2; color: #dc2626; font-size: 2rem; margin-bottom: 1rem;">
            🛑
        </div>
        <h1 style="font-size: 1.6rem; font-weight: 800; color: #991b1b;">Session Safely Halted</h1>
        <p style="color: #64748b; font-size: 0.95rem; margin-top: 0.5rem;">
            Your headset connection has been terminated. Your therapist has been immediately notified. You are completely safe in your physical environment.
        </p>
    </div>

    <!-- 5-4-3-2-1 Grounding Protocol Card -->
    <div style="background: #f0fdf4; border: 1px solid #bbf7d0; border-radius: 12px; padding: 1.5rem; margin-bottom: 2rem;">
        <h2 style="font-size: 1.15rem; font-weight: 700; color: #166534; margin-bottom: 1rem; display: flex; align-items: center; gap: 0.5rem;">
            <span>🌱</span> 5-4-3-2-1 Physical Grounding Sequence
        </h2>
        <ol style="margin-left: 1.25rem; color: #166534; line-height: 1.8; font-size: 0.95rem;">
            <li><strong>5 Things you see:</strong> Look around your physical room and name 5 distinct objects.</li>
            <li><strong>4 Things you feel:</strong> Notice the sensation of your feet flat on the floor, the texture of your chair, or your hands in your lap.</li>
            <li><strong>3 Things you hear:</strong> Listen for ambient sounds (air conditioner, distant birds, your own steady breathing).</li>
            <li><strong>2 Things you smell:</strong> Take note of ambient scents or fresh air.</li>
            <li><strong>1 Deep breath:</strong> Slowly inhale through your nose for 4 counts, hold for 4, and release for 6 counts.</li>
        </ol>
    </div>

    <!-- Crisis Helpline Resources -->
    <div style="background: #f8fafc; border: 1px solid #e2e8f0; border-radius: 12px; padding: 1.5rem; margin-bottom: 2rem;">
        <h3 style="font-size: 1rem; font-weight: 700; color: #1e293b; margin-bottom: 0.5rem;">Immediate Support Lines</h3>
        <p style="font-size: 0.9rem; color: #475569; line-height: 1.6;">
            If you are feeling overwhelmed, crisis counselors and medical professionals are available 24 hours a day, 7 days a week:
        </p>
        <div style="margin-top: 0.75rem; display: flex; flex-direction: column; gap: 0.5rem; font-size: 0.9rem;">
            <div>📞 <strong>National Crisis &amp; Suicide Lifeline:</strong> Dial <strong>988</strong> (Toll-Free, USA/Canada)</div>
            <div>💬 <strong>Crisis Text Line:</strong> Text <strong>HOME</strong> to <strong>741741</strong></div>
            <div>🚨 <strong>Emergency Services:</strong> Dial <strong>911</strong> or contact your local hospital emergency clinic.</div>
        </div>
    </div>

    <div style="display: flex; gap: 1rem; justify-content: center;">
        <a href="${pageContext.request.contextPath}/patient/mood-journal"
           style="padding: 0.75rem 1.5rem; background: #0284c7; color: white; border-radius: 8px; text-decoration: none; font-weight: 600; font-size: 0.9rem;">
            Record Feelings in Mood Journal
        </a>
        <a href="${pageContext.request.contextPath}/dashboard"
           style="padding: 0.75rem 1.5rem; background: #e2e8f0; color: #334155; border-radius: 8px; text-decoration: none; font-weight: 600; font-size: 0.9rem;">
            Return to Dashboard
        </a>
    </div>
</div>

<jsp:include page="../layout/footer.jsp" />
