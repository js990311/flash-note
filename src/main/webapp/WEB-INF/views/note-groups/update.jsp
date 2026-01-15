<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>

<t:layout title="노트 그룹 수정 - Flashnote">
    <jsp:body>
        <div class="container py-5">
            <div class="row justify-content-center">
                <div class="col-md-6">
                    <div class="d-flex align-items-center mb-4">
                            <%-- 수정 중 취소 시 상세 페이지로 이동 --%>
                        <a href="/note-groups/${id}" class="btn btn-outline-secondary btn-sm me-3">
                            <i class="bi bi-arrow-left"></i>
                        </a>
                        <h2 class="fw-bold m-0">노트 그룹 수정</h2>
                    </div>

                    <div class="card border-0 shadow-sm p-4 mb-4">
                            <%-- 컨트롤러의 @PostMapping("/{id}/update")와 매핑 --%>
                        <form:form action="/note-groups/${id}/update" method="post" modelAttribute="request">
                            <div class="mb-4">
                                <label for="name" class="form-label fw-semibold">그룹 이름 수정</label>
                                <form:input path="name"
                                            id="name"
                                            cssClass="form-control form-control-lg"
                                            cssErrorClass="form-control form-control-lg is-invalid"
                                            placeholder="수정할 그룹 이름을 입력하세요" />

                                    <%-- 에러 메시지 처리 --%>
                                <form:errors path="name" cssClass="invalid-feedback" element="div" />
                            </div>

                            <div class="d-grid gap-2">
                                <button type="submit" class="btn btn-success btn-lg rounded-pill shadow-sm">
                                    <i class="bi bi-check-lg me-1"></i> 수정 완료
                                </button>
                                <a href="/note-groups/${id}" class="btn btn-light btn-lg rounded-pill">취소</a>
                            </div>
                        </form:form>
                    </div>

                    <hr class="my-4 opacity-25">

                    <form:form action="/note-groups/${id}/delete" method="post">
                        <button type="submit" class="btn btn-outline-danger shadow-sm">
                            <i class="bi bi-trash me-1"></i> 삭제
                        </button>
                    </form:form>
                </div>
            </div>
        </div>
    </jsp:body>
</t:layout>