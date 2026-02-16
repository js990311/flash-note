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
            <c:if test="${myProfile}">
                <a href="/profile/edit" class="btn btn-outline-dark btn-sm rounded-pill px-3">
                    <i class="bi bi-pencil me-1"></i> 수정하기
                </a>
            </c:if>
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

        <div class="mt-5">
            <h5 class="fw-bold mb-3">최근 노트</h5>
            <div class="d-flex flex-column gap-2">
                <c:forEach var="note" items="${notes}">
                    <div class="note-item p-3 position-relative">
                        <div class="d-flex justify-content-between align-items-center">
                            <h6 class="mb-0 fw-bold text-truncate" style="max-width: 80%;">
                                <a href="/notes/${note.id}" class="text-dark text-decoration-none stretched-link">
                                        ${note.title}
                                </a>
                                <c:if test="${not note.published}">
                                    <i class="bi bi-lock-fill ms-1 text-muted small"></i>
                                </c:if>
                            </h6>
                            <span class="text-muted text-xs">
                                    ${note.updatedAt}
                            </span>
                        </div>
                    </div>
                </c:forEach>
            </div>
        </div>
    </jsp:body>
</t:layout>