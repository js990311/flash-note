<%@ page contentType="text/html;charset=UTF-8" language="java" isErrorPage="true" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>

<t:layout title="페이지를 찾을 수 없습니다">
    <jsp:body>
        <div class="text-center py-5">
            <div class="display-1 fw-bold text-muted opacity-25 mb-3">404</div>
            <h2 class="fw-bold mb-3">길을 잃으셨나요?</h2>
            <p class="text-secondary mb-5">
                요청하신 페이지가 삭제되었거나 주소가 올바르지 않습니다.
            </p>
            <a href="/" class="btn btn-outline-primary px-4 py-2">
                <i class="bi bi-house-door me-1"></i> 홈으로 돌아가기
            </a>
        </div>
    </jsp:body>
</t:layout>