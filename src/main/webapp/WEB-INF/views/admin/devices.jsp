<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<c:set var="pageTitle" value="Hardware Device Registry" />
<jsp:include page="../layout/header.jsp" />

<div style="margin-bottom: 2rem;">
    <h1 style="font-size: 1.85rem; font-weight: 700; color: #0f172a;">VR Hardware Management &amp; Disconnect Controls</h1>
    <p style="color: #64748b; margin-top: 0.25rem;">
        Monitor registered WebXR headsets, inspect heartbeat latencies, and execute force-disconnects or security blocks.
    </p>
</div>

<div style="background: white; border-radius: 12px; border: 1px solid #e2e8f0; padding: 2rem; box-shadow: 0 1px 3px rgba(0,0,0,0.05);">
    <h2 style="font-size: 1.25rem; font-weight: 700; color: #0f172a; margin-bottom: 1.5rem;">Registered Devices (${fn:length(devices)})</h2>

    <div style="overflow-x: auto;">
        <table style="width: 100%; border-collapse: collapse; font-size: 0.9rem;">
            <thead>
                <tr style="border-bottom: 1px solid #e2e8f0; text-align: left; color: #64748b; font-size: 0.8rem; text-transform: uppercase;">
                    <th style="padding: 0.75rem 0.5rem;">Device ID</th>
                    <th style="padding: 0.75rem 0.5rem;">Label</th>
                    <th style="padding: 0.75rem 0.5rem;">Type</th>
                    <th style="padding: 0.75rem 0.5rem;">Owner ID</th>
                    <th style="padding: 0.75rem 0.5rem;">Status</th>
                    <th style="padding: 0.75rem 0.5rem;">Last Heartbeat</th>
                    <th style="padding: 0.75rem 0.5rem;">IP Address</th>
                    <th style="padding: 0.75rem 0.5rem;">Security Status</th>
                    <th style="padding: 0.75rem 0.5rem;">Administrative Override</th>
                </tr>
            </thead>
            <tbody>
                <c:forEach var="d" items="${devices}">
                    <tr style="border-bottom: 1px solid #f1f5f9;">
                        <td style="padding: 0.85rem 0.5rem; color: #64748b;">#${d.id}</td>
                        <td style="padding: 0.85rem 0.5rem; font-weight: 600; color: #1e293b;">${d.deviceName}</td>
                        <td style="padding: 0.85rem 0.5rem;"><span class="badge badge-info">${d.deviceType.displayName}</span></td>
                        <td style="padding: 0.85rem 0.5rem; color: #475569;">Patient #${d.patientId}</td>
                        <td style="padding: 0.85rem 0.5rem;">
                            <span class="badge ${d.status == 'CONNECTED' ? 'badge-success' : 'badge-secondary'}">
                                ${d.status}
                            </span>
                        </td>
                        <td style="padding: 0.85rem 0.5rem; color: #64748b; font-size: 0.85rem;">
                            ${not empty d.lastHeartbeat ? d.lastHeartbeat : 'None'}
                        </td>
                        <td style="padding: 0.85rem 0.5rem; color: #64748b; font-size: 0.85rem;">
                            ${not empty d.ipAddress ? d.ipAddress : 'N/A'}
                        </td>
                        <td style="padding: 0.85rem 0.5rem;">
                            <c:choose>
                                <c:when test="${d.blocked}">
                                    <span class="badge badge-danger">BLOCKED</span>
                                </c:when>
                                <c:otherwise>
                                    <span class="badge badge-success">CLEARED</span>
                                </c:otherwise>
                            </c:choose>
                        </td>
                        <td style="padding: 0.85rem 0.5rem;">
                            <div style="display: flex; gap: 0.5rem;">
                                <form action="${pageContext.request.contextPath}/admin/devices" method="post" style="display: inline;">
                                    <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}" />
                                    <input type="hidden" name="action" value="disconnect" />
                                    <input type="hidden" name="deviceId" value="${d.id}" />
                                    <button type="submit" style="padding: 0.35rem 0.65rem; background: #f59e0b; color: white; border: none; border-radius: 4px; font-size: 0.78rem; font-weight: 600; cursor: pointer;">
                                        Disconnect
                                    </button>
                                </form>
                                <form action="${pageContext.request.contextPath}/admin/devices" method="post" style="display: inline;">
                                    <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}" />
                                    <input type="hidden" name="action" value="block" />
                                    <input type="hidden" name="deviceId" value="${d.id}" />
                                    <button type="submit" style="padding: 0.35rem 0.65rem; background: #ef4444; color: white; border: none; border-radius: 4px; font-size: 0.78rem; font-weight: 600; cursor: pointer;">
                                        Block
                                    </button>
                                </form>
                            </div>
                        </td>
                    </tr>
                </c:forEach>
            </tbody>
        </table>
    </div>
</div>

<jsp:include page="../layout/footer.jsp" />
