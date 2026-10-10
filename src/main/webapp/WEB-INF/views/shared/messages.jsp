<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<c:set var="pageTitle" value="Therapeutic Messaging Thread" />
<jsp:include page="../layout/header.jsp" />

<div style="margin-bottom: 2rem;">
    <h1 style="font-size: 1.85rem; font-weight: 700; color: #0f172a;">Therapeutic Messaging &amp; Guidance</h1>
    <p style="color: #64748b; margin-top: 0.25rem;">
        Direct asynchronous messaging thread between patient and assigned therapist.
    </p>
</div>

<div style="display: grid; grid-template-columns: 1fr 2.5fr; gap: 1.5rem; background: white; border-radius: 12px; border: 1px solid #e2e8f0; overflow: hidden; min-height: 550px;">
    <!-- Contacts Sidebar -->
    <div style="border-right: 1px solid #e2e8f0; background: #f8fafc; padding: 1.25rem;">
        <h3 style="font-size: 0.95rem; font-weight: 700; color: #334155; margin-bottom: 1rem; text-transform: uppercase;">Contacts</h3>
        <div style="display: flex; flex-direction: column; gap: 0.5rem;">
            <c:forEach var="c" items="${contacts}">
                <a href="${pageContext.request.contextPath}/messages?userId=${c.id}"
                   style="display: block; padding: 0.75rem 1rem; border-radius: 8px; text-decoration: none; font-size: 0.9rem; font-weight: 600; background: ${c.id == chatPartner.id ? '#e0f2fe' : 'transparent'}; color: ${c.id == chatPartner.id ? '#0284c7' : '#334155'};">
                    ${c.fullName}
                    <span style="display: block; font-size: 0.75rem; color: #64748b; font-weight: normal;">${c.role}</span>
                </a>
            </c:forEach>
        </div>
    </div>

    <!-- Active Chat Conversation -->
    <div style="display: flex; flex-direction: column; padding: 1.5rem;">
        <c:choose>
            <c:when test="${not empty chatPartner}">
                <div style="border-bottom: 1px solid #e2e8f0; padding-bottom: 1rem; margin-bottom: 1rem; display: flex; justify-content: space-between; align-items: center;">
                    <div>
                        <h2 style="font-size: 1.2rem; font-weight: 700; color: #0f172a;">${chatPartner.fullName}</h2>
                        <span style="font-size: 0.8rem; color: #64748b;">${chatPartner.role}</span>
                    </div>
                </div>

                <!-- Messages Log -->
                <div style="flex: 1; overflow-y: auto; display: flex; flex-direction: column; gap: 1rem; padding-right: 0.5rem;">
                    <c:forEach var="msg" items="${conversation}">
                        <c:set var="isMe" value="${msg.senderId == sessionScope.currentUser.id}" />
                        <div style="display: flex; flex-direction: column; align-items: ${isMe ? 'flex-end' : 'flex-start'};">
                            <div style="max-width: 70%; padding: 0.85rem 1.15rem; border-radius: 12px; font-size: 0.9rem; background: ${isMe ? '#0284c7' : '#f1f5f9'}; color: ${isMe ? 'white' : '#1e293b'};">
                                ${msg.messageContent}
                            </div>
                            <span style="font-size: 0.7rem; color: #94a3b8; margin-top: 0.25rem;">${msg.sentAt}</span>
                        </div>
                    </c:forEach>
                </div>

                <!-- Compose Input -->
                <form action="${pageContext.request.contextPath}/messages" method="post" style="display: flex; gap: 0.75rem; margin-top: 1rem; border-top: 1px solid #e2e8f0; padding-top: 1rem;">
                    <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}" />
                    <input type="hidden" name="recipientId" value="${chatPartner.id}" />
                    <input type="text" name="content" required placeholder="Type clinical guidance or question..."
                           style="flex: 1; padding: 0.75rem 1rem; border-radius: 8px; border: 1px solid #cbd5e1; font-size: 0.95rem;" />
                    <button type="submit" style="padding: 0.75rem 1.5rem; background: #0284c7; color: white; border: none; border-radius: 8px; font-weight: 600; cursor: pointer;">
                        Send
                    </button>
                </form>
            </c:when>
            <c:otherwise>
                <div style="display: flex; align-items: center; justify-content: center; height: 100%; color: #64748b;">
                    Select a contact from the left sidebar to open the conversation thread.
                </div>
            </c:otherwise>
        </c:choose>
    </div>
</div>

<jsp:include page="../layout/footer.jsp" />
