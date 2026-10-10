<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<c:set var="pageTitle" value="Clinical Homework Assignments" />
<jsp:include page="../layout/header.jsp" />

<div style="margin-bottom: 2rem;">
    <h1 style="font-size: 1.85rem; font-weight: 700; color: #0f172a;">Patient Homework &amp; Cognitive Practice</h1>
    <p style="color: #64748b; margin-top: 0.25rem;">
        Prescribe between-session exercises (VR breathing repetition, cognitive restructuring, grounding practice).
    </p>
</div>

<div style="display: grid; grid-template-columns: 1.2fr 1.8fr; gap: 2rem;">
    <!-- Assignment Form -->
    <div style="background: white; border-radius: 12px; border: 1px solid #e2e8f0; padding: 2rem; box-shadow: 0 1px 3px rgba(0,0,0,0.05); height: fit-content;">
        <h2 style="font-size: 1.25rem; font-weight: 700; color: #0f172a; margin-bottom: 1.5rem;">Assign Homework</h2>

        <form action="${pageContext.request.contextPath}/therapist/homework" method="post" style="display: flex; flex-direction: column; gap: 1.25rem;">
            <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}" />
            <input type="hidden" name="action" value="assign" />

            <div>
                <label for="patientId" style="display: block; font-size: 0.875rem; font-weight: 600; color: #334155; margin-bottom: 0.35rem;">Patient</label>
                <select id="patientId" name="patientId" required style="width: 100%; padding: 0.75rem 1rem; border-radius: 8px; border: 1px solid #cbd5e1; font-size: 0.95rem; background: white;">
                    <c:forEach var="p" items="${patients}">
                        <option value="${p.id}">${p.fullName} (${p.username})</option>
                    </c:forEach>
                </select>
            </div>

            <div>
                <label for="title" style="display: block; font-size: 0.875rem; font-weight: 600; color: #334155; margin-bottom: 0.35rem;">Exercise Title</label>
                <input type="text" id="title" name="title" required placeholder="e.g. 5-Minute Box Breathing in Calm Beach"
                       style="width: 100%; padding: 0.75rem 1rem; border-radius: 8px; border: 1px solid #cbd5e1; font-size: 0.95rem;" />
            </div>

            <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 1rem;">
                <div>
                    <label for="category" style="display: block; font-size: 0.875rem; font-weight: 600; color: #334155; margin-bottom: 0.35rem;">Category</label>
                    <select id="category" name="category" required style="width: 100%; padding: 0.75rem 1rem; border-radius: 8px; border: 1px solid #cbd5e1; font-size: 0.9rem; background: white;">
                        <option value="VR Breathing">VR Breathing Practice</option>
                        <option value="Thought Record">CBT Thought Record</option>
                        <option value="Mood Tracking">Daily Mood Tracking</option>
                        <option value="Grounding">Physical Grounding Routine</option>
                    </select>
                </div>
                <div>
                    <label for="dueDate" style="display: block; font-size: 0.875rem; font-weight: 600; color: #334155; margin-bottom: 0.35rem;">Due Date</label>
                    <input type="date" id="dueDate" name="dueDate" required
                           style="width: 100%; padding: 0.75rem 1rem; border-radius: 8px; border: 1px solid #cbd5e1; font-size: 0.95rem;" />
                </div>
            </div>

            <div>
                <label for="description" style="display: block; font-size: 0.875rem; font-weight: 600; color: #334155; margin-bottom: 0.35rem;">Instructions</label>
                <textarea id="description" name="description" rows="3" placeholder="Steps for the patient to follow..."
                          style="width: 100%; padding: 0.75rem 1rem; border-radius: 8px; border: 1px solid #cbd5e1; font-size: 0.95rem;"></textarea>
            </div>

            <button type="submit" class="btn btn-primary"
                    style="width: 100%; padding: 0.85rem; font-size: 1rem; font-weight: 600; border-radius: 8px; background: #0284c7; color: white; border: none; cursor: pointer;">
                Assign to Patient
            </button>
        </form>
    </div>

    <!-- Active Assigned Homework -->
    <div style="background: white; border-radius: 12px; border: 1px solid #e2e8f0; padding: 2rem; box-shadow: 0 1px 3px rgba(0,0,0,0.05);">
        <h2 style="font-size: 1.25rem; font-weight: 700; color: #0f172a; margin-bottom: 1.5rem;">Assigned Homework (${fn:length(homeworkList)})</h2>

        <c:choose>
            <c:when test="${not empty homeworkList}">
                <div style="display: flex; flex-direction: column; gap: 1rem;">
                    <c:forEach var="hw" items="${homeworkList}">
                        <div style="border: 1px solid #e2e8f0; border-radius: 10px; padding: 1.25rem; background: ${hw.completed ? '#f0fdf4' : '#f8fafc'};">
                            <div style="display: flex; justify-content: space-between; align-items: flex-start;">
                                <div>
                                    <div style="display: flex; align-items: center; gap: 0.75rem;">
                                        <h3 style="font-size: 1.05rem; font-weight: 700; color: #1e293b;">${hw.title}</h3>
                                        <span class="badge badge-info">${hw.category}</span>
                                        <c:choose>
                                            <c:when test="${hw.completed}">
                                                <span class="badge badge-success">✓ COMPLETED</span>
                                            </c:when>
                                            <c:otherwise>
                                                <span class="badge" style="background: #fef3c7; color: #92400e;">PENDING</span>
                                            </c:otherwise>
                                        </c:choose>
                                    </div>
                                    <p style="color: #475569; font-size: 0.875rem; margin-top: 0.5rem;">${hw.description}</p>
                                    <div style="color: #64748b; font-size: 0.8rem; margin-top: 0.5rem;">
                                        <strong>Due:</strong> ${hw.dueDate}
                                    </div>
                                    <c:if test="${not empty hw.completionNotes}">
                                        <div style="margin-top: 0.5rem; background: white; padding: 0.75rem; border-radius: 6px; font-size: 0.85rem; color: #065f46; border: 1px solid #bbf7d0;">
                                            <strong>Patient Reflection:</strong> &ldquo;${hw.completionNotes}&rdquo;
                                        </div>
                                    </c:if>
                                </div>
                            </div>
                        </div>
                    </c:forEach>
                </div>
            </c:when>
            <c:otherwise>
                <p style="color: #64748b; font-size: 0.9rem; text-align: center; padding: 2rem 0;">No homework assigned yet.</p>
            </c:otherwise>
        </c:choose>
    </div>
</div>

<jsp:include page="../layout/footer.jsp" />
