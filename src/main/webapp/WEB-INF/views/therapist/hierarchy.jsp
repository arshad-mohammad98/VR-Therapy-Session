<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<c:set var="pageTitle" value="Exposure Hierarchy Ladder" />
<jsp:include page="../layout/header.jsp" />

<div style="margin-bottom: 2rem; display: flex; justify-content: space-between; align-items: flex-start;">
    <div>
        <a href="${pageContext.request.contextPath}/therapist/plan" style="color: #0284c7; text-decoration: none; font-size: 0.9rem; font-weight: 600;">&larr; Back to Treatment Plans</a>
        <h1 style="font-size: 1.85rem; font-weight: 700; color: #0f172a; margin-top: 0.5rem;">Exposure Hierarchy: ${plan.title}</h1>
        <p style="color: #64748b; margin-top: 0.25rem;">
            Graduated exposure ladder. <strong>Algorithmic safety rule:</strong> Tier N+1 unlocks only when patient post-session distress drops &le; threshold.
        </p>
    </div>
</div>

<c:if test="${not empty sessionScope.flashMessage}">
    <div class="alert alert-success" style="margin-bottom: 1.5rem; padding: 1rem; background: #d1fae5; color: #065f46; border-radius: 8px; border: 1px solid #34d399;">
        🎉 ${sessionScope.flashMessage}
    </div>
    <% session.removeAttribute("flashMessage"); %>
</c:if>
<c:if test="${not empty sessionScope.flashError}">
    <div class="alert alert-danger" style="margin-bottom: 1.5rem; padding: 1rem; background: #fee2e2; color: #991b1b; border-radius: 8px; border: 1px solid #f87171;">
        ⚠️ ${sessionScope.flashError}
    </div>
    <% session.removeAttribute("flashError"); %>
</c:if>

<div style="display: grid; grid-template-columns: 2fr 1.2fr; gap: 2rem;">
    <!-- Hierarchy Tiers Ladder -->
    <div style="background: white; border-radius: 12px; border: 1px solid #e2e8f0; padding: 2rem; box-shadow: 0 1px 3px rgba(0,0,0,0.05);">
        <h2 style="font-size: 1.25rem; font-weight: 700; color: #0f172a; margin-bottom: 1.5rem;">Graduated Exposure Hierarchy (${fn:length(levels)} Tiers)</h2>

        <div style="display: flex; flex-direction: column; gap: 1rem;">
            <c:forEach var="lvl" items="${levels}">
                <div style="border: 2px solid ${lvl.unlocked ? '#10b981' : '#cbd5e1'}; background: ${lvl.unlocked ? '#f0fdf4' : '#f8fafc'}; border-radius: 12px; padding: 1.5rem; position: relative;">
                    <div style="display: flex; justify-content: space-between; align-items: flex-start;">
                        <div>
                            <div style="display: flex; align-items: center; gap: 0.75rem;">
                                <span style="display: inline-flex; align-items: center; justify-content: center; width: 32px; height: 32px; border-radius: 50%; background: ${lvl.unlocked ? '#10b981' : '#94a3b8'}; color: white; font-weight: 700; font-size: 0.9rem;">
                                    ${lvl.levelOrder}
                                </span>
                                <h3 style="font-size: 1.15rem; font-weight: 700; color: #1e293b;">${lvl.title}</h3>
                                <c:choose>
                                    <c:when test="${lvl.unlocked}">
                                        <span class="badge" style="background: #d1fae5; color: #065f46; font-size: 0.8rem; font-weight: 600;">UNLOCKED</span>
                                    </c:when>
                                    <c:otherwise>
                                        <span class="badge" style="background: #e2e8f0; color: #475569; font-size: 0.8rem; font-weight: 600;">🔒 LOCKED</span>
                                    </c:otherwise>
                                </c:choose>
                            </div>
                            <p style="color: #475569; font-size: 0.9rem; margin-top: 0.5rem;">${lvl.description}</p>
                            <div style="color: #64748b; font-size: 0.82rem; margin-top: 0.5rem;">
                                <strong>Unlock Condition:</strong> Post-Session Distress &le; <strong>${lvl.distressRatingThreshold} / 10</strong>
                            </div>
                        </div>

                        <!-- Evaluate Unlock Form for Demonstration -->
                        <c:if test="${lvl.unlocked}">
                            <div style="background: white; border: 1px solid #e2e8f0; padding: 1rem; border-radius: 8px; width: 220px;">
                                <div style="font-size: 0.78rem; font-weight: 700; color: #334155; margin-bottom: 0.5rem;">TEST TIER EVALUATION</div>
                                <form action="${pageContext.request.contextPath}/therapist/hierarchy" method="post" style="display: flex; flex-direction: column; gap: 0.5rem;">
                                    <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}" />
                                    <input type="hidden" name="action" value="evaluateUnlock" />
                                    <input type="hidden" name="planId" value="${plan.id}" />
                                    <input type="hidden" name="currentLevelId" value="${lvl.id}" />

                                    <div style="display: flex; align-items: center; gap: 0.5rem;">
                                        <label style="font-size: 0.78rem; color: #64748b;">Post Stress:</label>
                                        <input type="number" name="postStress" min="1" max="10" value="${lvl.distressRatingThreshold}"
                                               style="width: 60px; padding: 0.25rem 0.5rem; border: 1px solid #cbd5e1; border-radius: 4px; font-size: 0.85rem;" />
                                    </div>
                                    <button type="submit" style="padding: 0.4rem; background: #0284c7; color: white; border: none; border-radius: 6px; font-size: 0.8rem; font-weight: 600; cursor: pointer;">
                                        Evaluate Unlock
                                    </button>
                                </form>
                            </div>
                        </c:if>
                    </div>
                </div>
            </c:forEach>
        </div>
    </div>

    <!-- Add Level Form -->
    <div style="background: white; border-radius: 12px; border: 1px solid #e2e8f0; padding: 2rem; box-shadow: 0 1px 3px rgba(0,0,0,0.05); height: fit-content;">
        <h2 style="font-size: 1.2rem; font-weight: 700; color: #0f172a; margin-bottom: 1.5rem;">Add Hierarchy Level</h2>

        <form action="${pageContext.request.contextPath}/therapist/hierarchy" method="post" style="display: flex; flex-direction: column; gap: 1.25rem;">
            <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}" />
            <input type="hidden" name="action" value="addLevel" />
            <input type="hidden" name="planId" value="${plan.id}" />

            <div>
                <label for="levelOrder" style="display: block; font-size: 0.875rem; font-weight: 600; color: #334155; margin-bottom: 0.35rem;">Tier Rank</label>
                <input type="number" id="levelOrder" name="levelOrder" value="${fn:length(levels) + 1}" min="1" required
                       style="width: 100%; padding: 0.75rem 1rem; border-radius: 8px; border: 1px solid #cbd5e1; font-size: 0.95rem;" />
            </div>

            <div>
                <label for="title" style="display: block; font-size: 0.875rem; font-weight: 600; color: #334155; margin-bottom: 0.35rem;">Tier Title</label>
                <input type="text" id="title" name="title" required placeholder="e.g. Tier 3: High Balcony Overlook"
                       style="width: 100%; padding: 0.75rem 1rem; border-radius: 8px; border: 1px solid #cbd5e1; font-size: 0.95rem;" />
            </div>

            <div>
                <label for="environment" style="display: block; font-size: 0.875rem; font-weight: 600; color: #334155; margin-bottom: 0.35rem;">VR Environment</label>
                <select id="environment" name="environment" required
                        style="width: 100%; padding: 0.75rem 1rem; border-radius: 8px; border: 1px solid #cbd5e1; font-size: 0.9rem; background: white;">
                    <c:forEach var="env" items="${environments}">
                        <option value="${env.name()}">${env.displayName}</option>
                    </c:forEach>
                </select>
            </div>

            <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 1rem;">
                <div>
                    <label for="intensity" style="display: block; font-size: 0.875rem; font-weight: 600; color: #334155; margin-bottom: 0.35rem;">Intensity (1-5)</label>
                    <input type="number" id="intensity" name="intensity" min="1" max="5" value="3" required
                           style="width: 100%; padding: 0.75rem 1rem; border-radius: 8px; border: 1px solid #cbd5e1; font-size: 0.95rem;" />
                </div>
                <div>
                    <label for="threshold" style="display: block; font-size: 0.875rem; font-weight: 600; color: #334155; margin-bottom: 0.35rem;">Max Distress (&le;)</label>
                    <input type="number" id="threshold" name="threshold" min="1" max="10" value="4" required
                           style="width: 100%; padding: 0.75rem 1rem; border-radius: 8px; border: 1px solid #cbd5e1; font-size: 0.95rem;" />
                </div>
            </div>

            <div>
                <label for="description" style="display: block; font-size: 0.875rem; font-weight: 600; color: #334155; margin-bottom: 0.35rem;">Clinical Description</label>
                <textarea id="description" name="description" rows="3" placeholder="Target exposure cues..."
                          style="width: 100%; padding: 0.75rem 1rem; border-radius: 8px; border: 1px solid #cbd5e1; font-size: 0.95rem;"></textarea>
            </div>

            <button type="submit" class="btn btn-primary"
                    style="width: 100%; padding: 0.85rem; font-size: 1rem; font-weight: 600; border-radius: 8px; background: #0284c7; color: white; border: none; cursor: pointer;">
                Append Tier to Ladder
            </button>
        </form>
    </div>
</div>

<jsp:include page="../layout/footer.jsp" />
