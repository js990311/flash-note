<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>

<t:layout title="노트 그룹 생성 - Flashnote">
    <jsp:body>
        <div class="container py-5">
            <div class="row justify-content-center">
                <div class="col-md-6">
                    <div class="d-flex align-items-center mb-4">
                        <a href="/" class="btn btn-outline-secondary btn-sm me-3">
                            <i class="bi bi-arrow-left"></i>
                        </a>
                        <h2 class="fw-bold m-0">새 노트 그룹 생성</h2>
                    </div>

                    <div class="card border-0 shadow-sm p-4">
                            <%--
                               modelAttribute="request"는 테스트 코드의
                               model().attributeExists("request")와 대응됩니다.
                            --%>
                        <form:form action="/note-groups/create" method="post" modelAttribute="request">
                            <div class="mb-4">
                                <label for="name" class="form-label fw-semibold">그룹 이름</label>
                                    <%-- cssClass와 cssErrorClass를 활용하세요 --%>
                                <form:input path="name"
                                            id="name"
                                            cssClass="form-control form-control-lg"
                                            cssErrorClass="form-control form-control-lg is-invalid"
                                            placeholder="예: 프로젝트 아이디어, 학습 기록" />

                                    <%-- 에러 메시지 출력 (부트스트랩 피드백 클래스 적용) --%>
                                <form:errors path="name" cssClass="invalid-feedback" element="div" />
                            </div>

                            <div class="d-grid gap-2">
                                <button type="submit" class="btn btn-primary btn-lg rounded-pill shadow-sm">
                                    <i class="bi bi-plus-lg me-1"></i> 그룹 만들기
                                </button>
                                <a href="/" class="btn btn-light btn-lg rounded-pill">취소</a>
                            </div>
                        </form:form>
                    </div>

                    <div class="mt-4 p-3 bg-light rounded-3 border-start border-primary border-4">
                        <small class="text-secondary">
                            <i class="bi bi-info-circle me-1"></i>
                            노트 그룹을 만들어 관련 있는 메모들을 가이아의 품처럼 하나로 묶어 관리하세요.
                        </small>
                    </div>
                </div>
            </div>
        </div>
    </jsp:body>
</t:layout>