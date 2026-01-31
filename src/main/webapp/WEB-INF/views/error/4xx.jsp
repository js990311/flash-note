<%@ page contentType="text/html;charset=UTF-8" language="java" isErrorPage="true" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<t:layout title="${not empty title ? title : '잘못된 요청'}">
    <jsp:body>
        <div class="text-center py-5">
            <div class="display-1 fw-bold text-muted opacity-25 mb-3">${status}</div>

            <h2 class="fw-bold mb-3">
                요청을 처리할 수 없습니다
            </h2>

            <p class="text-secondary mb-2">
                    ${not empty detail ? detail : '입력하신 정보를 다시 한번 확인해 주시기 바랍니다.'}
            </p>

            <div class="mb-5">
                <small class="text-muted opacity-50">
                    Ref: <a href="${type}" class="text-decoration-none text-muted">${type}</a> |
                    Path: ${instance}
                </small>
            </div>

            <div class="d-flex justify-content-center gap-2">
                <a href="javascript:history.back()" class="btn btn-outline-secondary px-4 py-2">
                    <i class="bi bi-arrow-left me-1"></i> 이전으로
                </a>
                <a href="/" class="btn btn-outline-primary px-4 py-2">
                    <i class="bi bi-house-door me-1"></i> 홈으로 이동
                </a>
            </div>
        </div>
    </jsp:body>
</t:layout>