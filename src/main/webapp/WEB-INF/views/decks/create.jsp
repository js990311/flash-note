<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<t:layout title="새 덱 만들기 - Flashnote">
    <jsp:body>
        <div class="container py-5">
            <div class="row justify-content-center">
                <div class="col-md-6">
                    <div class="card border-0 shadow-sm p-4">
                        <h3 class="fw-bold mb-4">새 덱 추가</h3>

                        <form:form action="/decks/create" method="post" modelAttribute="request">
                            <div class="mb-3">
                                <label for="name" class="form-label">덱 이름</label>
                                <form:input path="name" class="form-control ${pageContext.findAttribute('org.springframework.validation.BindingResult.request').hasFieldErrors('name') ? 'is-invalid' : ''}" placeholder="이름을 입력하세요" />
                                <form:errors path="name" cssClass="invalid-feedback" />
                            </div>

                            <div class="d-grid gap-2 d-md-flex justify-content-md-end mt-4">
                                <a href="<c:url value='/decks'/>" class="btn btn-light me-md-2">취소</a>
                                <button type="submit" class="btn btn-primary">생성하기</button>
                            </div>
                        </form:form>
                    </div>
                </div>
            </div>
        </div>
    </jsp:body>
</t:layout>