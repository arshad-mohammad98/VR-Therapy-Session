<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<c:set var="pageTitle" value="My Homework & Exercises" />
<jsp:include page="../layout/header.jsp" />

<div style="margin-bottom: 2rem;">
    <h1 style="font-size: 1.85rem; font-weight: 700; color: #0f172a;">Assigned Exercises &amp; Homework</h1>
    <p style="color: #64748b; margin-top: 0.25rem;">
        Complete prescribed between-session practices and submit self-reflections to your therapist.
    </p>
</div>

<div style="background: white; border-radius: 12px; border: 1px solid #e2e8f0; padding: 2rem; box-shadow: 0 1px 3px rgba(0,0,0,0.05); max-width: 800px;">
    <h2 style="font-size: 1.25rem; font-weight: 700; color: #0f172a; margin-bottom: 1.5rem;">Clinical Assignments (${fn:length(homeworkList)})</h2>

    <c:choose>
        <c:when test="${not empty homeworkList}">
            <div style="display: flex; flex-direction: column; gap: 1.5rem;">
                <c:forEach var="hw" items="${homeworkList}">
                    <div style="border: 2px solid ${hw.completed ? '#bbf7d0' : '#e2e8f0'}; border-radius: 12px; padding: 1.5rem; background: ${hw.completed ? '#f0fdf4' : '#ffffff'};">
                        <div style="display: flex; justify-content: space-between; align-items: flex-start;">
                            <div>
                                <div style="display: flex; align-items: center; gap: 0.75rem;">
                                    <h3 style="font-size: 1.15rem; font-weight: 700; color: #1e293b;">${hw.title}</h3>
                                    <span class="badge badge-info">${hw.category}</span>
                                    <c:choose>
                                        <c:when test="${hw.completed}">
                                            <span class="badge badge-success">✓ COMPLETED</span>
                                        </c:when>
                                        <c:otherwise>
                                            <span class="badge" style="background: #fef3c7; color: #92400e;">DUE: ${hw.dueDate}</span>
                                        </c:otherwise>
                                    </c:choose>
                                </div>
                                <p style="color: #475569; font-size: 0.9rem; margin-top: 0.5rem; line-height: 1.5;">${hw.description}</p>
                            </div>
                        </div>

                        <c:choose>
                            <c:when test="${hw.completed}">
                                <div style="margin-top: 1rem; background: white; padding: 1rem; border-radius: 8px; border: 1px solid #bbf7d0; font-size: 0.875rem; color: #166534;">
                                    <strong>Your Submitted Reflection:</strong>
                                    <p style="margin-top: 0.25rem;">&ldquo;${hw.completionNotes}&rdquo;</p>
                                </div>
                            </c:when>
                            <c:otherwise>
                                <form action="${pageContext.request.contextPath}/patient/homework" method="post" style="margin-top: 1rem; border-top: 1px dashed #cbd5e1; padding-top: 1rem;">
                                    <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}" />
                                    <input type="hidden" name="action" value="complete" />
                                    <input type="hidden" name="homeworkId" value="${hw.id}" />

                                    <label style="display: block; font-size: 0.85rem; font-weight: 600; color: #334155; margin-bottom: 0.35rem;">
                                        Completion Self-Reflection:
                                    </label>
                                    <textarea name="reflection" required rows="2" placeholder="Describe your experience during this exercise..."
                                              style="width: 100%; padding: 0.5rem; border: 1px solid #cbd5e1; border-radius: 6px; font-size: 0.85rem;"></textarea>
                                    <button type="submit" style="margin-top: 0.5rem; padding: 0.5rem 1rem; background: #10b981; color: white; border: none; border-radius: 6px; font-weight: 600; font-size: 0.85rem; cursor: pointer;">
                                        Mark Completed &amp; Submit Reflection
                                    </button>
                                </form>
                            </c:otherwise>
                        </c:choose>
                    </div>
                </c:forEach>
            </div>
        </c:when>
        <c:otherwise>
            <p style="color: #64748b; font-size: 0.9rem; text-align: center; padding: 2rem 0;">No homework assigned at this time.</p>
        </c:otherwise>
    </c:choose>
</div>

<jsp:include page="../layout/footer.jsp" />
