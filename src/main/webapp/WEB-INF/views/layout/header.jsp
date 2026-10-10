<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${not empty pageTitle ? pageTitle : 'VR Therapy Sessions'} - Connected VR Mental Health Platform</title>
    <!-- Google Fonts -->
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Outfit:wght@300;400;500;600;700&family=Inter:wght@400;500;600;700&display=swap" rel="stylesheet">
    <!-- Main Style -->
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/style.css">
    <!-- Chart.js for clinical vitals and analytics -->
    <script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
</head>
<body>

<header class="navbar">
    <div class="nav-container">
        <a href="${pageContext.request.contextPath}/dashboard" class="brand">
            <span class="brand-icon">🥽</span>
            <span>VR Therapy Sessions</span>
        </a>

        <nav class="nav-links">
            <c:choose>
                <c:when test="${not empty sessionScope.currentUser}">
                    <a href="${pageContext.request.contextPath}/dashboard" class="nav-item">Dashboard</a>

                    <c:if test="${sessionScope.currentUser.role == 'THERAPIST' or sessionScope.currentUser.role == 'ROLE_THERAPIST'}">
                        <a href="${pageContext.request.contextPath}/therapist/session-design" class="nav-item">Session Design</a>
                        <a href="${pageContext.request.contextPath}/therapist/plan" class="nav-item">Plans</a>
                        <a href="${pageContext.request.contextPath}/therapist/hierarchy" class="nav-item">Hierarchy</a>
                        <a href="${pageContext.request.contextPath}/therapist/live-control" class="nav-item" style="color: #0284c7; font-weight: 600;">● Live Control</a>
                        <a href="${pageContext.request.contextPath}/therapist/progress" class="nav-item">Progress</a>
                        <a href="${pageContext.request.contextPath}/therapist/feedback" class="nav-item">Feedback Rules</a>
                        <a href="${pageContext.request.contextPath}/therapist/homework" class="nav-item">Homework</a>
                    </c:if>

                    <c:if test="${sessionScope.currentUser.role == 'PATIENT' or sessionScope.currentUser.role == 'ROLE_PATIENT'}">
                        <a href="${pageContext.request.contextPath}/vr/launch" class="nav-item" style="color: #0d9488; font-weight: 600;">🥽 Enter VR</a>
                        <a href="${pageContext.request.contextPath}/patient/device-pairing" class="nav-item">Devices</a>
                        <a href="${pageContext.request.contextPath}/patient/mood-journal" class="nav-item">Mood Journal</a>
                        <a href="${pageContext.request.contextPath}/patient/thought-record" class="nav-item">CBT Records</a>
                        <a href="${pageContext.request.contextPath}/patient/homework" class="nav-item">Assignments</a>
                        <a href="${pageContext.request.contextPath}/feedback" class="nav-item">Feedback</a>
                    </c:if>

                    <c:if test="${sessionScope.currentUser.role == 'ADMIN' or sessionScope.currentUser.role == 'ROLE_ADMIN'}">
                        <a href="${pageContext.request.contextPath}/admin/users" class="nav-item">Users</a>
                        <a href="${pageContext.request.contextPath}/admin/devices" class="nav-item">Devices</a>
                        <a href="${pageContext.request.contextPath}/admin/settings" class="nav-item">Settings</a>
                        <a href="${pageContext.request.contextPath}/admin/status" class="nav-item">Health</a>
                    </c:if>

                    <a href="${pageContext.request.contextPath}/messages" class="nav-item">Messages</a>
                    <a href="${pageContext.request.contextPath}/tickets" class="nav-item">Support</a>
                    <a href="${pageContext.request.contextPath}/simulator" class="nav-item" title="Simulated Headset & Biometrics">VR Sim</a>

                    <div class="user-pill" style="display: flex; align-items: center; gap: 0.75rem; margin-left: 0.5rem; background: #f1f5f9; padding: 0.35rem 0.85rem; border-radius: 9999px;">
                        <span style="font-size: 0.85rem; font-weight: 600; color: #334155;">${sessionScope.currentUser.fullName}</span>
                        <span class="badge badge-info" style="font-size: 0.75rem; padding: 0.15rem 0.5rem;">${sessionScope.currentUser.role}</span>
                        <a href="${pageContext.request.contextPath}/logout" style="color: #ef4444; text-decoration: none; font-size: 0.85rem; font-weight: 500;">Logout</a>
                    </div>
                </c:when>
                <c:otherwise>
                    <a href="${pageContext.request.contextPath}/login" class="nav-item">Login</a>
                    <a href="${pageContext.request.contextPath}/register" class="nav-item">Register</a>
                    <a href="${pageContext.request.contextPath}/simulator" class="nav-item">VR Simulator</a>
                </c:otherwise>
            </c:choose>
        </nav>
    </div>
</header>

<main class="page-content" style="flex: 1; max-width: 1200px; width: 100%; margin: 1.5rem auto; padding: 0 1.5rem;">
    <c:if test="${not empty errorMessage}">
        <div class="alert alert-danger" style="margin-bottom: 1.5rem; padding: 1rem 1.25rem; border-radius: 8px; background: #fee2e2; color: #991b1b; border: 1px solid #f87171;">
            <strong>Notice:</strong> ${errorMessage}
        </div>
    </c:if>
    <c:if test="${not empty successMessage or param.success eq 'true'}">
        <div class="alert alert-success" style="margin-bottom: 1.5rem; padding: 1rem 1.25rem; border-radius: 8px; background: #d1fae5; color: #065f46; border: 1px solid #34d399;">
            <strong>Success:</strong> Operation completed successfully.
        </div>
    </c:if>
