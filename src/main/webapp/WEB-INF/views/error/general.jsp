<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Error ${statusCode}" />
<jsp:include page="../layout/header.jsp" />

<div class="container" style="max-width: 650px; margin: 4rem auto; text-align: center;">
    <div class="card" style="padding: 3rem 2rem; border-top: 5px solid #ef4444; box-shadow: 0 10px 25px -5px rgba(0, 0, 0, 0.1);">
        <div style="font-size: 4rem; margin-bottom: 1rem;">
            <c:choose>
                <c:when test="${statusCode == 404}">🔍</c:when>
                <c:when test="${statusCode == 403}">🔒</c:when>
                <c:otherwise>⚠️</c:otherwise>
            </c:choose>
        </div>
        <h1 style="font-size: 2.2rem; color: #1e293b; margin-bottom: 0.5rem;">Error ${statusCode}</h1>
        <p style="font-size: 1.15rem; color: #64748b; margin-bottom: 2rem;">
            <c:choose>
                <c:when test="${statusCode == 404}">
                    The requested therapy page or resource could not be found.
                </c:when>
                <c:when test="${statusCode == 403}">
                    You do not have permission to access this clinical resource.
                </c:when>
                <c:otherwise>
                    ${not empty errorMessage ? errorMessage : 'An unexpected clinical server error occurred.'}
                </c:otherwise>
            </c:choose>
        </p>

        <div style="display: flex; gap: 1rem; justify-content: center;">
            <a href="${pageContext.request.contextPath}/dashboard" class="btn btn-primary" style="padding: 0.75rem 1.5rem; text-decoration: none;">
                Return to Dashboard
            </a>
            <a href="javascript:history.back()" class="btn btn-secondary" style="padding: 0.75rem 1.5rem; text-decoration: none;">
                Go Back
            </a>
        </div>
    </div>
</div>

<jsp:include page="../layout/footer.jsp" />
