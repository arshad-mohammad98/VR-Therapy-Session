<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<c:set var="pageTitle" value="System Administrator Cockpit" />
<jsp:include page="../layout/header.jsp" />

<div style="margin-bottom: 2rem;">
    <h1 style="font-size: 1.85rem; font-weight: 700; color: #0f172a;">Platform Administration &amp; Telemetry Cockpit</h1>
    <p style="color: #64748b; margin-top: 0.25rem;">
        Global health monitoring, multi-client hardware registry, security access logs, and runtime metrics.
    </p>
</div>

<!-- System Status Metric Cards -->
<div style="display: grid; grid-template-columns: repeat(auto-fit, minmax(220px, 1fr)); gap: 1.25rem; margin-bottom: 2rem;">
    <div style="background: white; border-radius: 12px; padding: 1.5rem; border: 1px solid #e2e8f0; box-shadow: 0 1px 3px rgba(0,0,0,0.05);">
        <div style="color: #64748b; font-size: 0.8rem; font-weight: 600; text-transform: uppercase;">System Uptime</div>
        <div style="font-size: 1.8rem; font-weight: 700; color: #10b981; margin-top: 0.5rem;">${systemStatus.uptimeFormatted}</div>
        <div style="font-size: 0.75rem; color: #64748b;">JVM Uptime Monitor</div>
    </div>
    <div style="background: white; border-radius: 12px; padding: 1.5rem; border: 1px solid #e2e8f0; box-shadow: 0 1px 3px rgba(0,0,0,0.05);">
        <div style="color: #64748b; font-size: 0.8rem; font-weight: 600; text-transform: uppercase;">Total Users</div>
        <div style="font-size: 2.2rem; font-weight: 700; color: #0284c7; margin-top: 0.5rem;">${systemStatus.totalUsers}</div>
        <div style="font-size: 0.75rem; color: #64748b;">Active RBAC accounts</div>
    </div>
    <div style="background: white; border-radius: 12px; padding: 1.5rem; border: 1px solid #e2e8f0; box-shadow: 0 1px 3px rgba(0,0,0,0.05);">
        <div style="color: #64748b; font-size: 0.8rem; font-weight: 600; text-transform: uppercase;">Active Live VR Sessions</div>
        <div style="font-size: 2.2rem; font-weight: 700; color: #ea580c; margin-top: 0.5rem;">${systemStatus.activeLiveSessions}</div>
        <div style="font-size: 0.75rem; color: #64748b;">Telemetry workers running</div>
    </div>
    <div style="background: white; border-radius: 12px; padding: 1.5rem; border: 1px solid #e2e8f0; box-shadow: 0 1px 3px rgba(0,0,0,0.05);">
        <div style="color: #64748b; font-size: 0.8rem; font-weight: 600; text-transform: uppercase;">Connected Hardware</div>
        <div style="font-size: 2.2rem; font-weight: 700; color: #6366f1; margin-top: 0.5rem;">${systemStatus.connectedDevices}</div>
        <div style="font-size: 0.75rem; color: #64748b;">Live heartbeat registry</div>
    </div>
</div>

<!-- Admin Management Grid -->
<div style="display: grid; grid-template-columns: 2fr 1fr; gap: 1.5rem; margin-bottom: 2rem;">
    <!-- Registered Users Summary -->
    <div style="background: white; border-radius: 12px; border: 1px solid #e2e8f0; padding: 1.5rem; box-shadow: 0 1px 3px rgba(0,0,0,0.05);">
        <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 1.25rem;">
            <h2 style="font-size: 1.2rem; font-weight: 700; color: #0f172a;">Platform Accounts (${fn:length(allUsers)})</h2>
            <a href="${pageContext.request.contextPath}/admin/users" style="color: #0284c7; font-size: 0.85rem; font-weight: 600; text-decoration: none;">View All &rarr;</a>
        </div>
        <div style="overflow-x: auto;">
            <table style="width: 100%; border-collapse: collapse; font-size: 0.9rem;">
                <thead>
                    <tr style="border-bottom: 1px solid #e2e8f0; text-align: left; color: #64748b; font-size: 0.8rem; text-transform: uppercase;">
                        <th style="padding: 0.5rem;">User</th>
                        <th style="padding: 0.5rem;">Role</th>
                        <th style="padding: 0.5rem;">Status</th>
                        <th style="padding: 0.5rem;">Action</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="u" items="${allUsers}" begin="0" end="4">
                        <tr style="border-bottom: 1px solid #f1f5f9;">
                            <td style="padding: 0.75rem 0.5rem; font-weight: 600; color: #1e293b;">
                                ${u.fullName} <span style="font-size: 0.8rem; color: #64748b;">(@${u.username})</span>
                            </td>
                            <td style="padding: 0.75rem 0.5rem;">
                                <span class="badge badge-info">${u.role}</span>
                            </td>
                            <td style="padding: 0.75rem 0.5rem;">
                                <span class="badge ${u.active ? 'badge-success' : 'badge-danger'}">
                                    ${u.active ? 'ACTIVE' : 'DEACTIVATED'}
                                </span>
                            </td>
                            <td style="padding: 0.75rem 0.5rem;">
                                <form action="${pageContext.request.contextPath}/admin/users" method="post" style="display: inline;">
                                    <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}" />
                                    <input type="hidden" name="action" value="toggleActive" />
                                    <input type="hidden" name="userId" value="${u.id}" />
                                    <button type="submit" style="padding: 0.25rem 0.6rem; border-radius: 4px; border: 1px solid #cbd5e1; font-size: 0.75rem; cursor: pointer; background: white;">
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

    <!-- Quick Admin Actions -->
    <div style="display: flex; flex-direction: column; gap: 1rem;">
        <div style="background: white; border-radius: 12px; border: 1px solid #e2e8f0; padding: 1.5rem; box-shadow: 0 1px 3px rgba(0,0,0,0.05);">
            <h3 style="font-size: 1.05rem; font-weight: 700; color: #0f172a; margin-bottom: 1rem;">Management Panels</h3>
            <div style="display: flex; flex-direction: column; gap: 0.75rem;">
                <a href="${pageContext.request.contextPath}/admin/users" class="btn"
                   style="display: block; text-align: center; text-decoration: none; padding: 0.75rem; font-weight: 600; border-radius: 8px; background: #0284c7; color: white;">
                    👥 User Directory &amp; RBAC
                </a>
                <a href="${pageContext.request.contextPath}/admin/devices" class="btn"
                   style="display: block; text-align: center; text-decoration: none; padding: 0.75rem; font-weight: 600; border-radius: 8px; background: #0d9488; color: white;">
                    📱 Hardware &amp; Disconnect Controls
                </a>
                <a href="${pageContext.request.contextPath}/admin/settings" class="btn"
                   style="display: block; text-align: center; text-decoration: none; padding: 0.75rem; font-weight: 600; border-radius: 8px; background: #6366f1; color: white;">
                    ⚙️ Platform Configuration
                </a>
                <a href="${pageContext.request.contextPath}/admin/status" class="btn"
                   style="display: block; text-align: center; text-decoration: none; padding: 0.75rem; font-weight: 600; border-radius: 8px; background: #10b981; color: white;">
                    📈 JVM &amp; Filter Metrics
                </a>
                <a href="${pageContext.request.contextPath}/tickets" class="btn"
                   style="display: block; text-align: center; text-decoration: none; padding: 0.75rem; font-weight: 600; border-radius: 8px; background: #f59e0b; color: white;">
                    🎫 Technical Support Desk
                </a>
            </div>
        </div>

        <div style="background: #f8fafc; border: 1px solid #e2e8f0; border-radius: 12px; padding: 1.25rem;">
            <h4 style="font-size: 0.9rem; font-weight: 700; color: #334155; margin-bottom: 0.35rem;">Security &amp; Audit</h4>
            <p style="font-size: 0.82rem; color: #64748b; line-height: 1.5;">
                Every administrative configuration change and forced hardware termination is permanently logged to JDBC Audit Logs with client IP and timestamps.
            </p>
        </div>
    </div>
</div>

<jsp:include page="../layout/footer.jsp" />
