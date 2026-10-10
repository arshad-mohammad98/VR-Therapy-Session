<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<c:set var="pageTitle" value="CBT Thought Restructuring" />
<jsp:include page="../layout/header.jsp" />

<div style="margin-bottom: 2rem;">
    <h1 style="font-size: 1.85rem; font-weight: 700; color: #0f172a;">CBT Thought Restructuring Record</h1>
    <p style="color: #64748b; margin-top: 0.25rem;">
        Identify automatic distressing thoughts, recognize cognitive distortions, and construct evidence-based rational alternatives.
    </p>
</div>

<div style="display: grid; grid-template-columns: 1.3fr 1.7fr; gap: 2rem;">
    <!-- Form -->
    <div style="background: white; border-radius: 12px; border: 1px solid #e2e8f0; padding: 2rem; box-shadow: 0 1px 3px rgba(0,0,0,0.05); height: fit-content;">
        <h2 style="font-size: 1.25rem; font-weight: 700; color: #0f172a; margin-bottom: 1.5rem;">New Thought Log</h2>

        <form action="${pageContext.request.contextPath}/patient/thought-record" method="post" style="display: flex; flex-direction: column; gap: 1.25rem;">
            <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}" />

            <div>
                <label for="situation" style="display: block; font-size: 0.875rem; font-weight: 600; color: #334155; margin-bottom: 0.35rem;">1. Triggering Situation / Context</label>
                <input type="text" id="situation" name="situation" required placeholder="e.g. Stepping onto the high VR deck..."
                       style="width: 100%; padding: 0.75rem 1rem; border-radius: 8px; border: 1px solid #cbd5e1; font-size: 0.95rem;" />
            </div>

            <div>
                <label for="automaticThought" style="display: block; font-size: 0.875rem; font-weight: 600; color: #334155; margin-bottom: 0.35rem;">2. Automatic Negative Thought</label>
                <textarea id="automaticThought" name="automaticThought" required rows="2" placeholder="e.g. I am going to lose control and fall..."
                          style="width: 100%; padding: 0.75rem 1rem; border-radius: 8px; border: 1px solid #cbd5e1; font-size: 0.95rem;"></textarea>
            </div>

            <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 1rem;">
                <div>
                    <label for="emotion" style="display: block; font-size: 0.875rem; font-weight: 600; color: #334155; margin-bottom: 0.35rem;">3. Dominant Emotion</label>
                    <input type="text" id="emotion" name="emotion" required placeholder="e.g. Panic, Dread"
                           style="width: 100%; padding: 0.75rem 1rem; border-radius: 8px; border: 1px solid #cbd5e1; font-size: 0.95rem;" />
                </div>
                <div>
                    <label for="cognitiveDistortion" style="display: block; font-size: 0.875rem; font-weight: 600; color: #334155; margin-bottom: 0.35rem;">4. Cognitive Distortion</label>
                    <select id="cognitiveDistortion" name="cognitiveDistortion" required
                            style="width: 100%; padding: 0.75rem 1rem; border-radius: 8px; border: 1px solid #cbd5e1; font-size: 0.9rem; background: white;">
                        <option value="Catastrophizing">Catastrophizing</option>
                        <option value="All-or-Nothing Thinking">All-or-Nothing Thinking</option>
                        <option value="Emotional Reasoning">Emotional Reasoning</option>
                        <option value="Fortune Telling">Fortune Telling</option>
                        <option value="Overgeneralization">Overgeneralization</option>
                    </select>
                </div>
            </div>

            <div>
                <label for="rationalResponse" style="display: block; font-size: 0.875rem; font-weight: 600; color: #334155; margin-bottom: 0.35rem;">5. Rational Evidence &amp; Alternative View</label>
                <textarea id="rationalResponse" name="rationalResponse" required rows="2" placeholder="e.g. I am standing safely in a room wearing a simulator. The somatic feeling will peak and subside."
                          style="width: 100%; padding: 0.75rem 1rem; border-radius: 8px; border: 1px solid #cbd5e1; font-size: 0.95rem;"></textarea>
            </div>

            <div>
                <label style="display: block; font-size: 0.875rem; font-weight: 600; color: #334155; margin-bottom: 0.35rem;">6. Belief in Negative Thought Now (0 - 100%)</label>
                <div style="display: flex; align-items: center; gap: 1rem;">
                    <span id="beliefVal" style="font-size: 1.5rem; font-weight: 800; color: #0d9488; width: 60px;">25%</span>
                    <input type="range" name="outcomeBelief" min="0" max="100" value="25"
                           style="flex: 1; accent-color: #0d9488;"
                           oninput="document.getElementById('beliefVal').innerText = this.value + '%'" />
                </div>
            </div>

            <button type="submit" class="btn btn-primary"
                    style="width: 100%; padding: 0.85rem; font-size: 1rem; font-weight: 600; border-radius: 8px; background: #0d9488; color: white; border: none; cursor: pointer;">
                Record Thought Structure
            </button>
        </form>
    </div>

    <!-- History List -->
    <div style="background: white; border-radius: 12px; border: 1px solid #e2e8f0; padding: 2rem; box-shadow: 0 1px 3px rgba(0,0,0,0.05);">
        <h2 style="font-size: 1.25rem; font-weight: 700; color: #0f172a; margin-bottom: 1.5rem;">Saved CBT Records (${fn:length(records)})</h2>

        <c:choose>
            <c:when test="${not empty records}">
                <div style="display: flex; flex-direction: column; gap: 1.25rem; max-height: 550px; overflow-y: auto;">
                    <c:forEach var="r" items="${records}">
                        <div style="border: 1px solid #e2e8f0; border-radius: 10px; padding: 1.25rem; background: #f8fafc;">
                            <div style="display: flex; justify-content: space-between; align-items: flex-start;">
                                <div>
                                    <span class="badge" style="background: #fee2e2; color: #991b1b; font-size: 0.75rem;">${r.cognitiveDistortion}</span>
                                    <span class="badge badge-info" style="margin-left: 0.5rem; font-size: 0.75rem;">Emotion: ${r.emotion}</span>
                                </div>
                                <span style="font-size: 0.8rem; color: #64748b;">${r.recordedAt}</span>
                            </div>

                            <div style="margin-top: 0.75rem;">
                                <strong style="font-size: 0.85rem; color: #475569;">Trigger:</strong>
                                <p style="font-size: 0.9rem; color: #1e293b; margin-top: 0.15rem;">${r.situation}</p>
                            </div>

                            <div style="margin-top: 0.5rem; background: white; padding: 0.75rem; border-radius: 6px; border: 1px solid #e2e8f0;">
                                <strong style="font-size: 0.8rem; color: #ef4444;">Automatic Thought:</strong>
                                <p style="font-size: 0.85rem; color: #334155; margin-top: 0.15rem; font-style: italic;">&ldquo;${r.automaticThought}&rdquo;</p>
                            </div>

                            <div style="margin-top: 0.5rem; background: #f0fdf4; padding: 0.75rem; border-radius: 6px; border: 1px solid #bbf7d0;">
                                <strong style="font-size: 0.8rem; color: #166534;">Rational Replacement:</strong>
                                <p style="font-size: 0.85rem; color: #166534; margin-top: 0.15rem;">${r.rationalAlternative}</p>
                            </div>
                        </div>
                    </c:forEach>
                </div>
            </c:when>
            <c:otherwise>
                <p style="color: #64748b; font-size: 0.9rem; text-align: center; padding: 2rem 0;">No CBT records created yet.</p>
            </c:otherwise>
        </c:choose>
    </div>
</div>

<jsp:include page="../layout/footer.jsp" />
