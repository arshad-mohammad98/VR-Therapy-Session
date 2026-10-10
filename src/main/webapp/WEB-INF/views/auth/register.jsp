<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Create Account" />
<jsp:include page="../layout/header.jsp" />

<div style="max-width: 560px; margin: 2rem auto; background: #ffffff; padding: 2.5rem; border-radius: 16px; box-shadow: 0 10px 25px -5px rgba(0,0,0,0.06), 0 8px 10px -6px rgba(0,0,0,0.04); border: 1px solid #e2e8f0;">
    <div style="text-align: center; margin-bottom: 2rem;">
        <div style="font-size: 2.5rem; margin-bottom: 0.5rem;">🌿</div>
        <h2 style="font-size: 1.5rem; font-weight: 700; color: #0f172a;">New User Registration</h2>
        <p style="color: #64748b; font-size: 0.9rem; margin-top: 0.25rem;">Join the Connected VR Mental Health Platform</p>
    </div>

    <form action="${pageContext.request.contextPath}/register" method="post" style="display: flex; flex-direction: column; gap: 1.25rem;">
        <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}" />

        <div>
            <label for="role" style="display: block; font-size: 0.875rem; font-weight: 600; color: #334155; margin-bottom: 0.35rem;">Account Type</label>
            <select id="role" name="role" onchange="toggleTherapistFields(this.value)"
                    style="width: 100%; padding: 0.75rem 1rem; border-radius: 8px; border: 1px solid #cbd5e1; font-size: 0.95rem; background: white;">
                <option value="PATIENT">Patient (Enroll in VR Sessions)</option>
                <option value="THERAPIST">Licensed Therapist (Design & Guide Protocols)</option>
            </select>
        </div>

        <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 1rem;">
            <div>
                <label for="username" style="display: block; font-size: 0.875rem; font-weight: 600; color: #334155; margin-bottom: 0.35rem;">Username</label>
                <input type="text" id="username" name="username" required
                       style="width: 100%; padding: 0.75rem 1rem; border-radius: 8px; border: 1px solid #cbd5e1; font-size: 0.95rem;" />
            </div>
            <div>
                <label for="password" style="display: block; font-size: 0.875rem; font-weight: 600; color: #334155; margin-bottom: 0.35rem;">Password</label>
                <input type="password" id="password" name="password" required
                       style="width: 100%; padding: 0.75rem 1rem; border-radius: 8px; border: 1px solid #cbd5e1; font-size: 0.95rem;" />
            </div>
        </div>

        <div>
            <label for="fullName" style="display: block; font-size: 0.875rem; font-weight: 600; color: #334155; margin-bottom: 0.35rem;">Full Name</label>
            <input type="text" id="fullName" name="fullName" required
                   style="width: 100%; padding: 0.75rem 1rem; border-radius: 8px; border: 1px solid #cbd5e1; font-size: 0.95rem;" />
        </div>

        <div>
            <label for="email" style="display: block; font-size: 0.875rem; font-weight: 600; color: #334155; margin-bottom: 0.35rem;">Email Address</label>
            <input type="email" id="email" name="email" required
                   style="width: 100%; padding: 0.75rem 1rem; border-radius: 8px; border: 1px solid #cbd5e1; font-size: 0.95rem;" />
        </div>

        <div id="therapistFields" style="display: none; border-top: 1px dashed #cbd5e1; padding-top: 1rem;">
            <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 1rem; margin-bottom: 1rem;">
                <div>
                    <label for="licenseNumber" style="display: block; font-size: 0.875rem; font-weight: 600; color: #334155; margin-bottom: 0.35rem;">License #</label>
                    <input type="text" id="licenseNumber" name="licenseNumber"
                           style="width: 100%; padding: 0.75rem 1rem; border-radius: 8px; border: 1px solid #cbd5e1; font-size: 0.95rem;" />
                </div>
                <div>
                    <label for="specialization" style="display: block; font-size: 0.875rem; font-weight: 600; color: #334155; margin-bottom: 0.35rem;">Specialization</label>
                    <input type="text" id="specialization" name="specialization" placeholder="e.g. Anxiety & PTSD"
                           style="width: 100%; padding: 0.75rem 1rem; border-radius: 8px; border: 1px solid #cbd5e1; font-size: 0.95rem;" />
                </div>
            </div>
        </div>

        <button type="submit" class="btn btn-primary" style="width: 100%; padding: 0.85rem; font-size: 1rem; font-weight: 600; border-radius: 8px; background: #0d9488; color: white; border: none; cursor: pointer; transition: background 0.2s;">
            Complete Registration
        </button>
    </form>

    <div style="text-align: center; margin-top: 1.5rem; font-size: 0.875rem; color: #64748b;">
        Already have an account? <a href="${pageContext.request.contextPath}/login" style="color: #0284c7; font-weight: 600; text-decoration: none;">Sign in here</a>
    </div>
</div>

<script>
function toggleTherapistFields(role) {
    document.getElementById('therapistFields').style.display = (role === 'THERAPIST') ? 'block' : 'none';
}
</script>

<jsp:include page="../layout/footer.jsp" />
