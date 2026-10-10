<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<c:set var="pageTitle" value="Support Tickets" />
<jsp:include page="../layout/header.jsp" />

<div style="margin-bottom: 2rem;">
    <h1 style="font-size: 1.85rem; font-weight: 700; color: #0f172a;">Technical Support &amp; Incident Desk</h1>
    <p style="color: #64748b; margin-top: 0.25rem;">
        Report hardware drift, browser WebXR glitches, or account issues directly to platform administrators.
    </p>
</div>

<div style="display: grid; grid-template-columns: 1.2fr 1.8fr; gap: 2rem;">
    <!-- Create Ticket Form -->
    <div style="background: white; border-radius: 12px; border: 1px solid #e2e8f0; padding: 2rem; box-shadow: 0 1px 3px rgba(0,0,0,0.05); height: fit-content;">
        <h2 style="font-size: 1.25rem; font-weight: 700; color: #0f172a; margin-bottom: 1.5rem;">Open New Ticket</h2>

        <form action="${pageContext.request.contextPath}/tickets" method="post" style="display: flex; flex-direction: column; gap: 1.25rem;">
            <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}" />
            <input type="hidden" name="action" value="create" />

            <div>
                <label for="title" style="display: block; font-size: 0.875rem; font-weight: 600; color: #334155; margin-bottom: 0.35rem;">Subject / Issue Summary</label>
                <input type="text" id="title" name="title" required placeholder="e.g. Controller Tracking Lost in Heights Exposure"
                       style="width: 100%; padding: 0.75rem 1rem; border-radius: 8px; border: 1px solid #cbd5e1; font-size: 0.95rem;" />
            </div>

            <div>
                <label for="priority" style="display: block; font-size: 0.875rem; font-weight: 600; color: #334155; margin-bottom: 0.35rem;">Priority Level</label>
                <select id="priority" name="priority" required
                        style="width: 100%; padding: 0.75rem 1rem; border-radius: 8px; border: 1px solid #cbd5e1; font-size: 0.95rem; background: white;">
                    <c:forEach var="pr" items="${priorities}">
                        <option value="${pr.name()}">${pr.name()}</option>
                    </c:forEach>
                </select>
            </div>

            <div>
                <label for="description" style="display: block; font-size: 0.875rem; font-weight: 600; color: #334155; margin-bottom: 0.35rem;">Detailed Incident Description</label>
                <textarea id="description" name="description" required rows="4" placeholder="Device model, browser version, and what steps reproduced the issue..."
                          style="width: 100%; padding: 0.75rem 1rem; border-radius: 8px; border: 1px solid #cbd5e1; font-size: 0.95rem;"></textarea>
            </div>

            <button type="submit" class="btn btn-primary"
                    style="width: 100%; padding: 0.85rem; font-size: 1rem; font-weight: 600; border-radius: 8px; background: #0284c7; color: white; border: none; cursor: pointer;">
                Submit Support Ticket
            </button>
        </form>
    </div>

    <!-- Ticket List Table -->
    <div style="background: white; border-radius: 12px; border: 1px solid #e2e8f0; padding: 2rem; box-shadow: 0 1px 3px rgba(0,0,0,0.05);">
        <h2 style="font-size: 1.25rem; font-weight: 700; color: #0f172a; margin-bottom: 1.5rem;">Support Tickets (${fn:length(tickets)})</h2>

        <c:choose>
            <c:when test="${not empty tickets}">
                <div style="display: flex; flex-direction: column; gap: 1rem;">
                    <c:forEach var="t" items="${tickets}">
                        <a href="${pageContext.request.contextPath}/tickets?id=${t.id}"
                           style="display: block; border: 1px solid #e2e8f0; border-radius: 10px; padding: 1.25rem; text-decoration: none; background: #f8fafc; transition: background 0.15s;">
                            <div style="display: flex; justify-content: space-between; align-items: flex-start;">
                                <div>
                                    <div style="display: flex; align-items: center; gap: 0.5rem;">
                                        <span style="font-weight: 700; color: #1e293b; font-size: 1rem;">#${t.id}: ${t.title}</span>
                                        <span class="badge ${t.status == 'OPEN' ? 'badge-danger' : (t.status == 'RESOLVED' ? 'badge-success' : 'badge-info')}">
                                            ${t.status}
                                        </span>
                                        <span class="badge" style="background: #e2e8f0; color: #475569; font-size: 0.75rem;">
                                            ${t.priority}
                                        </span>
                                    </div>
                                    <p style="color: #64748b; font-size: 0.85rem; margin-top: 0.5rem; line-height: 1.4;">
                                        ${t.description}
                                    </p>
                                </div>
                                <span style="font-size: 0.8rem; color: #94a3b8; white-space: nowrap;">${t.createdAt}</span>
                            </div>
                        </a>
                    </c:forEach>
                </div>
            </c:when>
            <c:otherwise>
                <p style="color: #64748b; font-size: 0.9rem; text-align: center; padding: 2rem 0;">No support tickets open.</p>
            </c:otherwise>
        </c:choose>
    </div>
</div>

<jsp:include page="../layout/footer.jsp" />
