<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>

<t:layout title="덱 목록 - Flashnote">
    <jsp:body>
        <div class="container py-5">
                <%-- 상단 헤더 --%>
            <div class="d-flex justify-content-between align-items-center mb-4">
                <div>
                    <h2 class="fw-bold mb-1">내 덱 목록</h2>
                    <p class="text-muted small mb-0">총 ${decks.paginationMetadata.totalElements}개의 덱이 관리되고 있습니다.</p>
                </div>
                <a href="<c:url value='/decks/create'/>" class="btn btn-primary shadow-sm">
                    <i class="bi bi-plus-lg me-1"></i> 새 덱 만들기
                </a>
            </div>

                <%-- 덱 리스트 카드 --%>
            <div class="card border-0 shadow-sm rounded-3 overflow-hidden">
                <div class="table-responsive">
                    <table class="table table-hover align-middle mb-0">
                        <thead class="table-light">
                        <tr>
                            <th scope="col" class="ps-4" style="width: 10%">ID</th>
                            <th scope="col" style="width: 45%">덱 이름</th>
                            <th scope="col" style="width: 15%">카드 수</th>
                            <th scope="col" style="width: 15%">유형</th>
                            <th scope="col" class="pe-4 text-center" style="width: 15%">관리</th>
                        </tr>
                        </thead>
                        <tbody>
                        <c:choose>
                            <c:when test="${not empty decks.contents}">
                                <c:forEach var="deck" items="${decks.contents}">
                                    <tr>
                                        <td class="ps-4 text-muted small">#${deck.id}</td>
                                        <td>
                                            <a href="<c:url value='/decks/${deck.id}'/>" class="text-decoration-none text-dark fw-semibold">
                                                <c:out value="${deck.name}"/>
                                            </a>
                                        </td>
                                        <td>
                                            <span class="badge bg-light text-dark border">${deck.cardCounts}장</span>
                                        </td>
                                        <td>
                                            <span class="small text-muted">${deck.originalType}</span>
                                        </td>
                                        <td class="pe-4 text-center">
                                            <div class="btn-group">
                                                <a href="<c:url value='/decks/${deck.id}/update'/>" class="btn btn-sm btn-outline-secondary border-0">
                                                    <i class="bi bi-pencil"></i>
                                                </a>
                                                <form action="<c:url value='/decks/${deck.id}/delete'/>" method="post" style="display:inline;" onsubmit="return confirm('이 덱을 삭제하시겠습니까?');">
                                                    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                                                    <button type="submit" class="btn btn-sm btn-outline-danger border-0">
                                                        <i class="bi bi-trash"></i>
                                                    </button>
                                                </form>
                                            </div>
                                        </td>
                                    </tr>
                                </c:forEach>
                            </c:when>
                            <c:otherwise>
                                <tr>
                                    <td colspan="5" class="text-center py-5 text-muted">생성된 덱이 없습니다.</td>
                                </tr>
                            </c:otherwise>
                        </c:choose>
                        </tbody>
                    </table>
                </div>
            </div>

                <%-- 페이징 --%>
            <div class="mt-4">
                <t:pagination meta="${decks.paginationMetadata}" />
            </div>
        </div>
    </jsp:body>
</t:layout>