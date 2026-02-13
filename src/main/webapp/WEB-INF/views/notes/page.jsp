<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="n" tagdir="/WEB-INF/tags/notes" %>

<t:layout title="내 노트 - Flashnote">
    <jsp:body>
        <div class="container py-5">
            <div class="d-flex justify-content-between align-items-center mb-4">
                <h2 class="fw-bold mb-0">내 노트</h2>
                <form action="<c:url value='/notes/create'/>" method="post">
                    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                    <button type="submit" class="btn btn-primary shadow-sm">
                        <i class="bi bi-plus-lg me-1"></i> 새 노트 작성
                    </button>
                </form>
            </div>

                <%-- 검색창 영역 --%>
            <div class="card border-0 shadow-sm mb-4">
                <div class="card-body p-3">
                    <form action="<c:url value='/notes'/>" method="get" class="row g-2">
                        <div class="col-auto">
                            <select name="searchOption" class="form-select border-0 bg-light">
                                <c:forEach var="option" items="${searchOptions}">
                                    <option value="${option}" ${option == searchOption ? 'selected' : ''}>
                                        <c:choose>
                                            <c:when test="${option == 'TITLE'}">제목</c:when>
                                            <c:when test="${option == 'CONTENT'}">내용</c:when>
                                            <c:when test="${option == 'TITLE_CONTENT'}">제목+내용</c:when>
                                        </c:choose>
                                    </option>
                                </c:forEach>
                            </select>
                        </div>
                        <div class="col">
                            <input type="text" name="keyword" class="form-control border-0 bg-light"
                                   placeholder="검색어를 입력하세요..." value="<c:out value='${keyword}'/>">
                        </div>
                        <div class="col-auto">
                            <button type="submit" class="btn btn-dark px-4">검색</button>
                            <c:if test="${not empty keyword}">
                                <a href="<c:url value='/notes'/>" class="btn btn-outline-secondary">초기화</a>
                            </c:if>
                        </div>
                    </form>
                </div>
            </div>

                <%-- 통합 목록 출력 --%>
            <n:noteList notes="${notes}" />
            <t:slicePagination
                    baseUrl="/notes/search"
                    keyword="${keyword}"
                    searchOption="${searchOption}"
                    pageNumber="${pageNumber}"
                    pageSize="${pageSize}"
                    hasPrev="${hasPrev}"
                    hasNext="${hasNext}"
            />
        </div>
    </jsp:body>
</t:layout>