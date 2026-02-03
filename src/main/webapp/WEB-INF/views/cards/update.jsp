<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<t:layout title="카드 수정">
    <jsp:body>
        <div class="row justify-content-center">
            <div class="col-md-8">
                <div class="d-flex justify-content-between align-items-center mb-4">
                    <h2 class="h4 mb-0">카드 내용 수정</h2>
                    <a href="/decks/${deckId}" class="btn btn-outline-secondary btn-sm">
                        <i class="bi bi-arrow-left"></i> 돌아가기
                    </a>
                </div>

                <div class="card shadow-sm">
                    <div class="card-body p-4">
                        <form:form action="/cards/${updateCardRequest.id}/update" method="post" modelAttribute="updateCardRequest">
                            <%-- 덱 ID와 카드 ID는 필수 (UpdateCardRequest에 필드가 있어야 함) --%>
                            <form:hidden path="id" />

                            <div class="mb-4">
                                <label class="form-label fw-bold">앞면 (질문)</label>
                                <form:textarea path="front" class="form-control" rows="5" placeholder="질문을 입력하세요." />
                                <form:errors path="front" class="text-danger small" />
                            </div>

                            <div class="mb-4">
                                <label class="form-label fw-bold">뒷면 (정답)</label>
                                <form:textarea path="back" class="form-control" rows="5" placeholder="정답을 입력하세요." />
                                <form:errors path="back" class="text-danger small" />
                            </div>

                            <div class="d-grid gap-2 d-md-flex justify-content-md-end">
                                <button type="reset" class="btn btn-light me-md-2">초기화</button>
                                <button type="submit" class="btn btn-primary px-5">저장하기</button>
                            </div>
                        </form:form>
                    </div>
                </div>
            </div>
        </div>
    </jsp:body>
</t:layout>