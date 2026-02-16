<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<t:layout title="프로필 수정">
    <jsp:attribute name="head">
        <link rel="stylesheet" href="<c:url value='/css/members/edit.css' />">
    </jsp:attribute>

    <jsp:body>
        <div class="edit-container">
            <header class="mb-4">
                <h2 class="fw-bold text-dark">프로필 수정</h2>
                <p class="text-muted small">변경할 이름을 입력하고 저장 버튼을 눌러주세요.</p>
                <span class="badge bg-light text-secondary border current-badge">#${profile.id}</span>
            </header>

            <form action="<c:url value='/profile/edit'/>" method="post">
                <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                <div class="mb-4">
                    <label for="name" class="form-label">
                        이름
                    </label>

                    <input type="text"
                           name="name"
                           id="name"
                           class="form-control ${not empty org.springframework.validation.BindingResult.profileForm.getFieldError('name') ? 'is-invalid' : ''}"
                           value="${profileForm.name}"
                           placeholder="이름을 입력하세요"
                           autocomplete="off">

                    <form:errors path="profileForm.name" cssClass="error-message" element="div" />
                </div>

                <hr class="my-4 opacity-25">

                <div class="d-flex gap-2 justify-content-end">
                    <a href="/profile" class="btn btn-outline-secondary px-4">취소</a>
                    <button type="submit" class="btn btn-primary px-4 fw-bold">저장하기</button>
                </div>
            </form>
        </div>
    </jsp:body>
</t:layout>