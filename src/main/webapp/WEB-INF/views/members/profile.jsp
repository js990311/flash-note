<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>

<t:layout title="내 프로필">
    <jsp:attribute name="head">
        <link rel="stylesheet" href="<c:url value='/css/members/profile.css' />">
    </jsp:attribute>

    <jsp:body>
        <div class="d-flex align-items-center justify-content-between mb-4">
            <div class="d-flex align-items-center gap-3">
                <div class="profile-avatar-wrapper">
                    <i class="bi bi-person-fill"></i>
                </div>
                <div>
                    <h2 class="h4 fw-bold mb-0">${profile.name}</h2>
                    <span class="badge bg-light text-secondary border">Member ID: #${profile.id}</span>
                </div>
            </div>
            <a href="/profile/edit" class="btn btn-outline-dark btn-sm rounded-pill px-3">
                <i class="bi bi-pencil me-1"></i> 수정하기
            </a>
        </div>

        <hr class="my-4">

        <div class="row g-4">
                <%-- 이름 정보 --%>
            <div class="col-md-6">
                <div class="p-3 border rounded-3 bg-light-subtle">
                    <div class="info-label text-uppercase">Name</div>
                    <div class="info-value">${profile.name}</div>
                </div>
            </div>

                <%-- 계정 식별자 --%>
            <div class="col-md-6">
                <div class="p-3 border rounded-3 bg-light-subtle">
                    <div class="info-label text-uppercase">Account ID</div>
                    <div class="info-value">@user_${profile.id}</div>
                </div>
            </div>
        </div>

        <div class="mt-5 text-center">
            <p class="text-muted small">
                <i class="bi bi-info-circle me-1"></i>
                프로필 정보는 서비스 내에서 다른 사용자에게 공개될 수 있습니다.
            </p>
        </div>
    </jsp:body>
</t:layout>