<%@ page contentType="text/html;charset=UTF-8" language="java" isErrorPage="true" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="e" tagdir="/WEB-INF/tags/error" %>

<t:layout title="시스템 오류">
    <jsp:body>
        <div class="text-center py-5">
            <div class="display-1 fw-bold text-danger opacity-25 mb-3">${status}</div>
            <h2 class="fw-bold mb-3">잠시 후 다시 시도해 주세요</h2>
            <p class="text-secondary mb-4">내부 시스템 장애로 인해 요청을 완료하지 못했습니다.</p>

            <div class="mb-5">
                <code class="small text-muted">Trace ID: ${instance}</code>
            </div>

            <button onclick="location.reload()" class="btn btn-primary px-4">다시 시도</button>

            <e:error-docs-link type="${type}" />
        </div>
    </jsp:body>
</t:layout>