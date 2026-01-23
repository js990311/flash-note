<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<t:layout title="덱 수정 - Flashnote">
    <jsp:body>
        <div class="container py-5">
            <div class="row justify-content-center">
                <div class="col-md-6">
                    <nav aria-label="breadcrumb" class="mb-4">
                        <ol class="breadcrumb small">
                            <li class="breadcrumb-item"><a href="<c:url value='/decks'/>">덱 목록</a></li>
                            <li class="breadcrumb-item"><a href="<c:url value='/decks/${request.id}'/>">덱 상세</a></li>
                            <li class="breadcrumb-item active">수정</li>
                        </ol>
                    </nav>

                    <div class="card border-0 shadow-sm p-4">
                        <div class="d-flex align-items-center mb-4">
                            <div class="bg-warning-soft p-2 rounded-3 me-3">
                                <i class="bi bi-pencil-square text-warning fs-4"></i>
                            </div>
                            <h3 class="fw-bold mb-0">덱 정보 수정</h3>
                        </div>

                            <%--
                              컨트롤러에서 model.addAttribute("request", ...)로 넘겼으므로
                              modelAttribute="request"를 사용합니다.
                            --%>
                        <form:form action="/decks/${request.id}/update" method="post" modelAttribute="request">
                            <%-- 수정을 위해 고유 ID는 반드시 hidden으로 전달 --%>
                            <form:hidden path="id" />

                            <div class="mb-4">
                                <label for="name" class="form-label fw-semibold">덱 이름</label>
                                <form:input path="name"
                                            class="form-control form-control-lg ${org.springframework.validation.BindingResult.request.hasFieldErrors('name') ? 'is-invalid' : ''}"
                                            placeholder="수정할 이름을 입력하세요" />
                                <form:errors path="name" cssClass="invalid-feedback" />
                                <div class="form-text mt-2">
                                    기존 이름: <span class="text-primary font-monospace"><c:out value="${request.name}"/></span>
                                </div>
                            </div>

                            <hr class="my-4 text-muted opacity-25">

                            <div class="d-grid gap-2 d-md-flex justify-content-md-end">
                                <a href="<c:url value='/decks/${request.id}'/>" class="btn btn-light px-4">취소</a>
                                <button type="submit" class="btn btn-primary px-4 shadow-sm">변경 사항 저장</button>
                            </div>
                        </form:form>
                    </div>

                        <%-- 위험 영역 --%>
                    <div class="mt-4 p-3 bg-danger-soft rounded-3 d-flex justify-content-between align-items-center border border-danger border-opacity-10">
                        <div class="small">
                            <strong class="text-danger">위험 영역</strong>
                            <p class="mb-0 text-muted">이 덱을 삭제하면 복구할 수 없습니다.</p>
                        </div>
                        <form action="<c:url value='/decks/${request.id}/delete'/>" method="post" onsubmit="return confirm('정말 삭제하시겠습니까?');">
                            <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                            <button type="submit" class="btn btn-outline-danger btn-sm">덱 삭제</button>
                        </form>
                    </div>
                </div>
            </div>
        </div>
    </jsp:body>
</t:layout>