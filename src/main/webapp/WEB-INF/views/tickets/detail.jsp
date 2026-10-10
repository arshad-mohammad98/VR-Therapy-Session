<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<c:set var="pageTitle" value="Ticket #${ticket.id}" />
<jsp:include page="../layout/header.jsp" />

<div style="margin-bottom: 2rem;">
    <a href="${pageContext.request.contextPath}/tickets" style="color: #0284c7; text-decoration: none; font-size: 0.9rem; font-weight: 600;">&larr; Back to Ticket Desk</a>
    <div style="display: flex; justify-content: space-between; align-items: center; margin-top: 0.5rem;">
        <h1 style="font-size: 1.85rem; font-weight: 700; color: #0f172a;">Ticket #${ticket.id}: ${ticket.title}</h1>
        <div style="display: flex; align-items: center; gap: 0.5rem;">
            <span class="badge ${ticket.status == 'OPEN' ? 'badge-danger' : (ticket.status == 'RESOLVED' ? 'badge-success' : 'badge-info')}">
                ${ticket.status}
            </span>
            <span class="badge" style="background: #e2e8f0; color: #475569;">
                Priority: ${ticket.priority}
            </span>
        </div>
    </div>
</div>

<div style="display: grid; grid-template-columns: 2fr 1fr; gap: 2rem;">
    <!-- Conversation Thread -->
    <div style="display: flex; flex-direction: column; gap: 1.5rem;">
        <!-- Original Issue Card -->
        <div style="background: white; border-radius: 12px; border: 1px solid #e2e8f0; padding: 1.5rem; box-shadow: 0 1px 3px rgba(0,0,0,0.05);">
            <div style="display: flex; justify-content: space-between; margin-bottom: 0.75rem;">
                <span style="font-weight: 700; color: #1e293b;">Creator Details</span>
                <span style="font-size: 0.8rem; color: #64748b;">${ticket.createdAt}</span>
            </div>
            <p style="color: #334155; line-height: 1.6; font-size: 0.95rem;">${ticket.description}</p>
        </div>

        <!-- Replies Log -->
        <c:forEach var="rep" items="${replies}">
            <div style="background: white; border-radius: 12px; border: 1px solid #e2e8f0; padding: 1.5rem; box-shadow: 0 1px 3px rgba(0,0,0,0.05);">
                <div style="display: flex; justify-content: space-between; margin-bottom: 0.5rem;">
                    <span style="font-weight: 700; color: #0284c7;">${not empty rep.authorName ? rep.authorName : 'Staff Reply'}</span>
                    <span style="font-size: 0.8rem; color: #64748b;">${rep.createdAt}</span>
                </div>
                <p style="color: #334155; line-height: 1.5; font-size: 0.9rem;">${rep.message}</p>
            </div>
        </c:forEach>

        <!-- Reply Input Form -->
        <div style="background: white; border-radius: 12px; border: 1px solid #e2e8f0; padding: 1.5rem; box-shadow: 0 1px 3px rgba(0,0,0,0.05);">
            <h3 style="font-size: 1.1rem; font-weight: 700; color: #0f172a; margin-bottom: 1rem;">Post Reply</h3>
            <form action="${pageContext.request.contextPath}/tickets" method="post" style="display: flex; flex-direction: column; gap: 1rem;">
                <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}" />
                <input type="hidden" name="action" value="reply" />
                <input type="hidden" name="ticketId" value="${ticket.id}" />

                <textarea name="message" required rows="3" placeholder="Provide troubleshooting instructions or updates..."
                          style="width: 100%; padding: 0.75rem 1rem; border-radius: 8px; border: 1px solid #cbd5e1; font-size: 0.95rem;"></textarea>

                <button type="submit" style="align-self: flex-start; padding: 0.65rem 1.5rem; background: #0284c7; color: white; border: none; border-radius: 8px; font-weight: 600; cursor: pointer;">
                    Submit Reply
                </button>
            </form>
        </div>
    </div>

    <!-- Admin Status Controls -->
    <div style="background: white; border-radius: 12px; border: 1px solid #e2e8f0; padding: 1.5rem; box-shadow: 0 1px 3px rgba(0,0,0,0.05); height: fit-content;">
        <h3 style="font-size: 1.1rem; font-weight: 700; color: #0f172a; margin-bottom: 1rem;">Ticket Management</h3>

        <c:choose>
            <c:when test="${sessionScope.currentUser.role == 'ADMIN' or sessionScope.currentUser.role == 'ROLE_ADMIN'}">
                <form action="${pageContext.request.contextPath}/tickets" method="post" style="display: flex; flex-direction: column; gap: 1rem;">
                    <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}" />
                    <input type="hidden" name="action" value="updateStatus" />
                    <input type="hidden" name="ticketId" value="${ticket.id}" />

                    <div>
                        <label for="status" style="display: block; font-size: 0.85rem; font-weight: 600; color: #334155; margin-bottom: 0.35rem;">Status</label>
                        <select id="status" name="status" style="width: 100%; padding: 0.6rem 0.85rem; border-radius: 6px; border: 1px solid #cbd5e1; font-size: 0.9rem; background: white;">
                            <c:forEach var="st" items="${statuses}">
                                <option value="${st.name()}" ${st == ticket.status ? 'selected' : ''}>${st.name()}</option>
                            </c:forEach>
                        </select>
                    </div>

                    <button type="submit" style="padding: 0.6rem; background: #0d9488; color: white; border: none; border-radius: 6px; font-weight: 600; font-size: 0.875rem; cursor: pointer;">
                        Update Status
                    </button>
                </form>
            </c:when>
            <c:otherwise>
                <div style="font-size: 0.85rem; color: #64748b; line-height: 1.5;">
                    Current Status: <strong>${ticket.status}</strong><br/>
                    Replies will alert administrators and assigned support engineers.
                </div>
            </c:otherwise>
        </c:choose>
    </div>
</div>

<jsp:include page="../layout/footer.jsp" />
