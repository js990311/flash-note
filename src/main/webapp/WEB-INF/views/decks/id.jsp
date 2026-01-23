<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<t:layout title="${deck.name}">
    <jsp:attribute name="head">
        <style>
            .card-item:hover { background-color: #f8f9fa; }
            .deck-info-badge { font-size: 0.9rem; }
        </style>
    </jsp:attribute>

    <jsp:body>
        <div class="d-flex justify-content-between align-items-center mb-4">
            <div>
                <span class="badge bg-primary deck-info-badge mb-2">총 ${deck.cardCounts}개의 카드</span>
                <p class="text-muted mb-0">학습을 시작하거나 새로운 카드를 추가해보세요.</p>
            </div>
            <div class="btn-group">
                <a href="/study/${deck.id}" class="btn btn-success">
                    <i class="bi bi-play-fill"></i> 학습하기
                </a>
            </div>
        </div>

        <div class="card shadow-sm mb-5">
            <div class="card-header bg-white">
                <h5 class="mb-0"><i class="bi bi-plus-circle"></i> 새 카드 추가</h5>
            </div>
            <div class="card-body">
                <form:form action="/cards" method="post" modelAttribute="createCardRequest" class="row g-3">
                    <%-- path를 사용하면 id와 name이 자동 생성됩니다 --%>
                    <form:hidden path="deckId" />

                    <div class="col-md-5">
                        <label class="form-label">앞면 (질문)</label>
                        <form:input path="front" class="form-control" placeholder="예: Apple" />
                        <form:errors path="front" class="text-danger small" />
                    </div>

                    <div class="col-md-5">
                        <label class="form-label">뒷면 (정답)</label>
                        <form:input path="back" class="form-control" placeholder="예: 사과" />
                        <form:errors path="back" class="text-danger small" />
                    </div>

                    <div class="col-md-2 d-flex align-items-end">
                        <button type="submit" class="btn btn-primary w-100">추가</button>
                    </div>
                </form:form>
            </div>
        </div>

        <div class="table-responsive">
            <table class="table table-hover align-middle">
                <thead class="table-light">
                <tr>
                    <th style="width: 40%">앞면</th>
                    <th style="width: 40%">뒷면</th>
                    <th style="width: 20%" class="text-center">관리</th>
                </tr>
                </thead>
                <tbody>
                <c:forEach items="${cards.contents}" var="card">
                    <tr class="card-item">
                        <td>${card.front}</td>
                        <td>${card.back}</td>
                        <td class="text-center">
                            <a href="/cards/${card.id}/update" class="btn btn-sm btn-outline-secondary"><i class="bi bi-pencil"></i></a>
                            <form action="/cards/${card.id}/delete" method="post" class="d-inline">
                                <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                                <button type="submit" class="btn btn-sm btn-outline-danger" onclick="return confirm('삭제하시겠습니까?')">
                                    <i class="bi bi-trash"></i>
                                </button>
                            </form>
                        </td>
                    </tr>
                </c:forEach>
                <c:if test="${empty cards.contents}">
                    <tr>
                        <td colspan="3" class="text-center py-4 text-muted">아직 등록된 카드가 없습니다.</td>
                    </tr>
                </c:if>
                </tbody>
            </table>
        </div>

        <div class="mt-4">
            <t:pagination meta="${cards.paginationMetadata}" />
        </div>
    </jsp:body>
</t:layout>