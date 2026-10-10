<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<c:set var="pageTitle" value="System Health & JVM Status" />
<jsp:include page="../layout/header.jsp" />

<div style="margin-bottom: 2rem;">
    <h1 style="font-size: 1.85rem; font-weight: 700; color: #0f172a;">System Health &amp; Runtime Metrics</h1>
    <p style="color: #64748b; margin-top: 0.25rem;">
        Real-time JVM telemetry, Filter response time metrics, and security audit log feed.
    </p>
</div>

<!-- Metrics Overview -->
<div style="display: grid; grid-template-columns: repeat(auto-fit, minmax(220px, 1fr)); gap: 1.25rem; margin-bottom: 2rem;">
    <div style="background: white; border-radius: 12px; padding: 1.25rem; border: 1px solid #e2e8f0; text-align: center;">
        <div style="color: #64748b; font-size: 0.8rem; font-weight: 600; text-transform: uppercase;">Average Filter Latency</div>
        <div style="font-size: 2.2rem; font-weight: 800; color: #0284c7; margin-top: 0.25rem;">${systemStatus.averageResponseTimeMs} ms</div>
        <div style="font-size: 0.75rem; color: #64748b;">ResponseTimeFilter calculation</div>
    </div>
    <div style="background: white; border-radius: 12px; padding: 1.25rem; border: 1px solid #e2e8f0; text-align: center;">
        <div style="color: #64748b; font-size: 0.8rem; font-weight: 600; text-transform: uppercase;">Requests Served</div>
        <div style="font-size: 2.2rem; font-weight: 800; color: #0d9488; margin-top: 0.25rem;">${systemStatus.totalRequestsServed}</div>
        <div style="font-size: 0.75rem; color: #64748b;">Throughput counter</div>
    </div>
    <div style="background: white; border-radius: 12px; padding: 1.25rem; border: 1px solid #e2e8f0; text-align: center;">
        <div style="color: #64748b; font-size: 0.8rem; font-weight: 600; text-transform: uppercase;">JVM Heap Memory</div>
        <div style="font-size: 2.2rem; font-weight: 800; color: #6366f1; margin-top: 0.25rem;">${systemStatus.heapMemoryUsedMb} MB</div>
        <div style="font-size: 0.75rem; color: #64748b;">of ${systemStatus.heapMemoryMaxMb} MB max (${systemStatus.heapMemoryUsagePercent}%)</div>
    </div>
    <div style="background: white; border-radius: 12px; padding: 1.25rem; border: 1px solid #e2e8f0; text-align: center;">
        <div style="color: #64748b; font-size: 0.8rem; font-weight: 600; text-transform: uppercase;">CPU Cores</div>
        <div style="font-size: 2.2rem; font-weight: 800; color: #10b981; margin-top: 0.25rem;">${systemStatus.availableProcessors} Cores</div>
        <div style="font-size: 0.75rem; color: #64748b;">Runtime available processors</div>
</div>

<!-- Mobile & VR Headset Network Access with QR Code -->
<div style="background: white; border-radius: 12px; border: 1px solid #e2e8f0; padding: 1.75rem; margin-bottom: 2rem; box-shadow: 0 1px 3px rgba(0,0,0,0.05); display: grid; grid-template-columns: auto 1fr; gap: 2rem; align-items: center;">
    <div style="text-align: center; background: #f8fafc; border: 1px solid #cbd5e1; border-radius: 12px; padding: 1rem;">
        <c:choose>
            <c:when test="${not empty applicationScope.QR_CODE_URL}">
                <img src="${applicationScope.QR_CODE_URL}" alt="Scan with Phone / Quest" style="width: 160px; height: 160px; border-radius: 6px; display: block;" />
            </c:when>
            <c:otherwise>
                <div style="width: 160px; height: 160px; display: flex; align-items: center; justify-content: center; font-size: 2rem;">📱</div>
            </c:otherwise>
        </c:choose>
        <div style="font-size: 0.75rem; color: #64748b; margin-top: 0.5rem; font-weight: 600;">Scan with Phone / Headset</div>
    </div>
    <div>
        <div style="display: flex; align-items: center; gap: 0.75rem; margin-bottom: 0.5rem;">
            <h2 style="font-size: 1.25rem; font-weight: 700; color: #0f172a; margin: 0;">Connected Device Access (Local Area Network)</h2>
            <span class="badge badge-success">🔒 HTTPS Ready (:8443)</span>
        </div>
        <p style="color: #64748b; font-size: 0.875rem; line-height: 1.5; margin-bottom: 1rem;">
            WebXR stereoscopic rendering and phone gyroscope/accelerometer sensors require HTTPS on local networks.
            Share these endpoints with your phone or Meta Quest headset on the same Wi-Fi:
        </p>
        <div style="background: #f8fafc; border: 1px solid #e2e8f0; border-radius: 8px; padding: 0.85rem 1rem; font-family: monospace; font-size: 0.85rem; display: flex; flex-direction: column; gap: 0.35rem;">
            <div><strong style="color: #0284c7;">Secure WebXR (Port 8443):</strong> <a href="${applicationScope.VR_LAUNCH_URL}" target="_blank" style="color: #0284c7; text-decoration: underline;">${applicationScope.VR_LAUNCH_URL}</a></div>
            <div><strong style="color: #475569;">Standard HTTP (Port 8080):</strong> <a href="${applicationScope.HTTP_URL}" target="_blank" style="color: #475569;">${applicationScope.HTTP_URL}</a></div>
        </div>
        <div style="font-size: 0.775rem; color: #94a3b8; margin-top: 0.75rem;">
            💡 Self-signed certificate notice: On your phone or Quest browser, tap <strong>"Advanced" &rarr; "Proceed"</strong> to accept the local certificate.
        </div>
    </div>
</div>

<!-- Audit Log Feed -->
<div style="background: white; border-radius: 12px; border: 1px solid #e2e8f0; padding: 2rem; box-shadow: 0 1px 3px rgba(0,0,0,0.05);">
    <h2 style="font-size: 1.25rem; font-weight: 700; color: #0f172a; margin-bottom: 1.5rem;">Recent Security &amp; Clinical Audit Logs (${fn:length(recentAuditLogs)})</h2>

    <div style="overflow-x: auto;">
        <table style="width: 100%; border-collapse: collapse; font-size: 0.875rem;">
            <thead>
                <tr style="border-bottom: 1px solid #e2e8f0; text-align: left; color: #64748b; font-size: 0.8rem; text-transform: uppercase;">
                    <th style="padding: 0.5rem;">Timestamp</th>
                    <th style="padding: 0.5rem;">Actor</th>
                    <th style="padding: 0.5rem;">Action</th>
                    <th style="padding: 0.5rem;">Details</th>
                    <th style="padding: 0.5rem;">Client IP</th>
                </tr>
            </thead>
            <tbody>
                <c:forEach var="log" items="${recentAuditLogs}">
                    <tr style="border-bottom: 1px solid #f1f5f9;">
                        <td style="padding: 0.75rem 0.5rem; color: #64748b; font-family: monospace;">${log.timestamp}</td>
                        <td style="padding: 0.75rem 0.5rem; font-weight: 600; color: #1e293b;">${log.actorUsername}</td>
                        <td style="padding: 0.75rem 0.5rem;"><span class="badge badge-info">${log.action}</span></td>
                        <td style="padding: 0.75rem 0.5rem; color: #475569;">${log.details}</td>
                        <td style="padding: 0.75rem 0.5rem; color: #64748b; font-family: monospace;">${log.ipAddress}</td>
                    </tr>
                </c:forEach>
            </tbody>
        </table>
    </div>
</div>

<jsp:include page="../layout/footer.jsp" />
