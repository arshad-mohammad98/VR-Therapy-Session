<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Sign In" />
<jsp:include page="../layout/header.jsp" />

<div style="max-width: 440px; margin: 3rem auto; background: #ffffff; padding: 2.5rem; border-radius: 16px; box-shadow: 0 10px 25px -5px rgba(0,0,0,0.06), 0 8px 10px -6px rgba(0,0,0,0.04); border: 1px solid #e2e8f0;">
    <div style="text-align: center; margin-bottom: 2rem;">
        <div style="font-size: 2.5rem; margin-bottom: 0.5rem;">🥽</div>
        <h2 style="font-size: 1.5rem; font-weight: 700; color: #0f172a;">Sign In to VR Therapy</h2>
        <p style="color: #64748b; font-size: 0.9rem; margin-top: 0.25rem;">Connected WebXR Mental Health Platform</p>
    </div>

    <form action="${pageContext.request.contextPath}/login" method="post" style="display: flex; flex-direction: column; gap: 1.25rem;">
        <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}" />

        <div>
            <label for="username" style="display: block; font-size: 0.875rem; font-weight: 600; color: #334155; margin-bottom: 0.35rem;">Username</label>
            <input type="text" id="username" name="username" required autocomplete="username"
                   style="width: 100%; padding: 0.75rem 1rem; border-radius: 8px; border: 1px solid #cbd5e1; font-size: 0.95rem; outline: none; transition: border-color 0.2s;"
                   placeholder="e.g. therapist1, patient1, admin" />
        </div>

        <div>
            <label for="password" style="display: block; font-size: 0.875rem; font-weight: 600; color: #334155; margin-bottom: 0.35rem;">Password</label>
            <input type="password" id="password" name="password" required autocomplete="current-password"
                   style="width: 100%; padding: 0.75rem 1rem; border-radius: 8px; border: 1px solid #cbd5e1; font-size: 0.95rem; outline: none; transition: border-color 0.2s;"
                   placeholder="Enter password" />
        </div>

        <button type="submit" class="btn btn-primary" style="width: 100%; padding: 0.85rem; font-size: 1rem; font-weight: 600; border-radius: 8px; background: #0284c7; color: white; border: none; cursor: pointer; transition: background 0.2s;">
            Sign In
        </button>
    </form>

    <div style="margin-top: 2rem; padding: 1rem; background: #f8fafc; border-radius: 8px; border: 1px solid #e2e8f0; font-size: 0.82rem; color: #475569;">
        <strong style="color: #0f172a; display: block; margin-bottom: 0.35rem;">Quick Demo Accounts:</strong>
        <ul style="padding-left: 1.25rem; margin: 0; line-height: 1.6;">
            <li><strong>Therapist:</strong> <code>therapist1</code> / <code>password123</code></li>
            <li><strong>Patient:</strong> <code>patient1</code> / <code>password123</code></li>
            <li><strong>Admin:</strong> <code>admin</code> / <code>admin123</code></li>
        </ul>
    </div>

    <div style="text-align: center; margin-top: 1.5rem; font-size: 0.875rem; color: #64748b;">
        Need an account? <a href="${pageContext.request.contextPath}/register" style="color: #0284c7; font-weight: 600; text-decoration: none;">Create one here</a>
    </div>
</div>

<jsp:include page="../layout/footer.jsp" />
