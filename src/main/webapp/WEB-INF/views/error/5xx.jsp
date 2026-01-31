<%@ page contentType="text/html;charset=UTF-8" language="java" isErrorPage="true" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<t:layout title="시스템 오류가 발생했습니다">
    <jsp:body>
        <div class="text-center py-5">
            <div class="display-1 fw-bold text-danger opacity-25 mb-3">${status}</div>

            <h2 class="fw-bold mb-3">서비스 이용에 불편을 드려 죄송합니다</h2>

            <p class="text-secondary mb-2">
                현재 시스템에 일시적인 장애가 발생하여 요청을 처리하지 못했습니다.<br>
                잠시 후 다시 시도해 주시기 바랍니다.
            </p>

            <div class="mb-5">
                <div class="badge bg-light text-dark border fw-normal p-2">
                    <span class="text-muted">Error Trace:</span> ${instance}
                </div>
            </div>

            <div class="d-flex justify-content-center gap-2">
                <button onclick="location.reload()" class="btn btn-primary px-4 py-2">
                    <i class="bi bi-arrow-clockwise me-1"></i> 페이지 새로고침
                </button>
                <a href="/" class="btn btn-outline-secondary px-4 py-2">
                    <i class="bi bi-house-door me-1"></i> 메인화면으로
                </a>
            </div>
        </div>
    </jsp:body>
</t:layout>