<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<c:set var="pageTitle" value="User Management" />
<jsp:include page="../layout/header.jsp" />

<div style="margin-bottom: 2rem;">
    <h1 style="font-size: 1.85rem; font-weight: 700; color: #0f172a;">Platform User Management &amp; Access Control</h1>
    <p style="color: #64748b; margin-top: 0.25rem;">
        Review registered accounts, toggle authorization status, and manage role-based permissions.
    </p>
</div>

<div style="background: white; border-radius: 12px; border: 1px solid #e2e8f0; padding: 2rem; box-shadow: 0 1px 3px rgba(0,0,0,0.05);">
    <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 1.5rem;">
        <h2 style="font-size: 1.25rem; font-weight: 700; color: #0f172a;">User Directory (${fn:length(users)})</h2>
    </div>

    <div style="overflow-x: auto;">
        <table style="width: 100%; border-collapse: collapse; font-size: 0.9rem;">
            <thead>
                <tr style="border-bottom: 1px solid #e2e8f0; text-align: left; color: #64748b; font-size: 0.8rem; text-transform: uppercase;">
                    <th style="padding: 0.75rem 0.5rem;">ID</th>
                    <th style="padding: 0.75rem 0.5rem;">Full Name</th>
                    <th style="padding: 0.75rem 0.5rem;">Username</th>
                    <th style="padding: 0.75rem 0.5rem;">Email</th>
                    <th style="padding: 0.75rem 0.5rem;">Role</th>
                    <th style="padding: 0.75rem 0.5rem;">Status</th>
                    <th style="padding: 0.75rem 0.5rem;">Action</th>
                </tr>
            </thead>
            <tbody>
                <c:forEach var="u" items="${users}">
                    <tr style="border-bottom: 1px solid #f1f5f9;">
                        <td style="padding: 0.85rem 0.5rem; color: #64748b;">#${u.id}</td>
                        <td style="padding: 0.85rem 0.5rem; font-weight: 600; color: #1e293b;">${u.fullName}</td>
                        <td style="padding: 0.85rem 0.5rem; color: #475569;">${u.username}</td>
                        <td style="padding: 0.85rem 0.5rem; color: #64748b;">${u.email}</td>
                        <td style="padding: 0.85rem 0.5rem;">
                            <span class="badge badge-info">${u.role}</span>
                        </td>
                        <td style="padding: 0.85rem 0.5rem;">
                            <span class="badge ${u.active ? 'badge-success' : 'badge-danger'}">
                                ${u.active ? 'ACTIVE' : 'DEACTIVATED'}
                            </span>
                        </td>
                        <td style="padding: 0.85rem 0.5rem;">
                            <form action="${pageContext.request.contextPath}/admin/users" method="post" style="display: inline;">
                                <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}" />
                                <input type="hidden" name="action" value="toggleActive" />
                                <input type="hidden" name="userId" value="${u.id}" />
                                <button type="submit" style="padding: 0.35rem 0.75rem; border-radius: 6px; border: 1px solid #cbd5e1; font-size: 0.8rem; font-weight: 600; cursor: pointer; background: ${u.active ? '#fee2e2' : '#d1fae5'}; color: ${u.active ? '#991b1b' : '#065f46'};">
                                    ${u.active ? 'Deactivate' : 'Activate'}
                                </button>
                            </form>
                        </td>
                    </tr>
                </c:forEach>
            </tbody>
        </table>
    </div>
</div>

<jsp:include page="../layout/footer.jsp" />
