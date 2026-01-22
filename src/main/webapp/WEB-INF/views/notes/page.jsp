<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<t:layout title="노트 목록 - Flashnote">
    <jsp:body>
        <div class="container py-5">
                <%-- 상단 헤더 영역 --%>
            <div class="d-flex justify-content-between align-items-center mb-4">
                <div>
                    <h2 class="fw-bold mb-1">내 노트 목록</h2>
                    <p class="text-muted small mb-0">총 ${notes.paginationMetadata.totalElements}개의 노트가 있습니다.</p>
                </div>
                <form action="<c:url value='/notes/create'/>" method="post">
                    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                    <button type="submit" class="btn btn-primary shadow-sm">
                        <i class="bi bi-plus-lg me-1"></i> 새 노트 작성
                    </button>
                </form>
            </div>

                <%-- 테이블 목록 영역 --%>
            <div class="card border-0 shadow-sm rounded-3 overflow-hidden">
                <div class="table-responsive">
                    <table class="table table-hover align-middle mb-0">
                        <thead class="table-light">
                        <tr>
                            <th scope="col" class="ps-4" style="width: 10%">ID</th>
                            <th scope="col" style="width: 50%">제목</th>
                            <th scope="col" style="width: 25%">수정일</th>
                            <th scope="col" class="pe-4 text-center" style="width: 15%">관리</th>
                        </tr>
                        </thead>
                        <tbody>
                        <c:choose>
                            <c:when test="${not empty notes.contents}">
                                <c:forEach var="note" items="${notes.contents}">
                                    <tr>
                                        <td class="ps-4 text-muted small">#${note.id}</td>
                                        <td>
                                            <a href="<c:url value='/notes/${note.id}'/>" class="text-decoration-none text-dark fw-semibold d-block text-truncate" style="max-width: 400px;">
                                                <c:out value="${note.title}"/>
                                            </a>
                                        </td>
                                        <td class="text-muted small">
                                                <%-- DTO 필드명에 맞춰 updatedAt로 수정함 --%>
                                            <i class="bi bi-clock me-1"></i> ${note.updatedAt}
                                        </td>
                                        <td class="pe-4 text-center">
                                            <div class="btn-group">
                                                <a href="<c:url value='/notes/${note.id}/edit'/>" class="btn btn-sm btn-outline-secondary border-0">
                                                    <i class="bi bi-pencil"></i>
                                                </a>
                                                    <%-- 개별 삭제를 위해 간단한 form 구성 가능 --%>
                                                <form action="<c:url value='/notes/${note.id}/delete'/>" method="post" style="display:inline;" onsubmit="return confirm('정말 삭제하시겠습니까?');">
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
                                    <td colspan="4" class="text-center py-5 text-muted">
                                        작성된 노트가 없습니다.
                                    </td>
                                </tr>
                            </c:otherwise>
                        </c:choose>
                        </tbody>
                    </table>
                </div>
            </div>

                <%-- 페이징 영역 --%>
            <div class="mt-4">
                <t:pagination meta="${notes.paginationMetadata}" />
            </div>
        </div>
    </jsp:body>
</t:layout>
