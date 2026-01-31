<%@ tag language="java" pageEncoding="UTF-8" %>
<%@ attribute name="type" required="false" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<div class="mt-5 pt-4 border-top">
    <div class="d-flex align-items-center justify-content-center justify-content-md-start">
        <div class="me-3">
            <i class="bi bi-info-circle text-primary fs-4"></i>
        </div>
        <div class="text-start">
            <p class="mb-0 small text-dark fw-bold">도움이 필요하신가요?</p>
            <p class="mb-0 small text-secondary">
                발생한 오류에 대한 상세 명세는
                <a href="/error/docs${not empty type ? '#'.concat(type) : ''}" class="text-primary text-decoration-underline">
                    에러 카탈로그
                </a>에서 확인할 수 있습니다.
            </p>
        </div>
    </div>
</div>