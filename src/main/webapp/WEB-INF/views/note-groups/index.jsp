<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>

<t:layout title="노트 그룹 목록 - Flashnote">
    <jsp:body>
        <div class="container py-4">
            <div class="d-flex justify-content-between align-items-center mb-4">
                <h2 class="fw-bold m-0">내 노트 그룹</h2>
                <a href="/note-groups/create" class="btn btn-primary rounded-pill px-4">
                    <i class="bi bi-plus-lg me-1"></i> 새 그룹 생성
                </a>
            </div>

            <div class="row g-4">
                <c:choose>
                    <c:when test="${not empty noteGroups.content}">
                        <c:forEach var="group" items="${noteGroups.content}">
                            <div class="col-md-4">
                                    <%-- DTO에 id가 있다는 전제하에 클릭 시 상세 페이지 이동 --%>
                                <div class="card h-100 border-0 shadow-sm border-hover"
                                     onclick="location.href='/note-groups/${group.id}'"
                                     style="cursor: pointer;">
                                    <div class="card-body p-4">
                                        <div class="d-flex align-items-center">
                                            <div class="p-2 bg-light rounded-3 me-3">
                                                <i class="bi bi-folder text-primary fs-4"></i>
                                            </div>
                                            <h5 class="card-title fw-bold m-0 text-truncate">
                                                <c:out value="${group.name}" />
                                            </h5>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </c:forEach>
                    </c:when>
                    <c:otherwise>
                        <div class="col-12 text-center py-5 border rounded-4 bg-light">
                            <p class="text-muted mb-0">생성된 노트 그룹이 없습니다. 첫 번째 그룹을 만들어보세요!</p>
                        </div>
                    </c:otherwise>
                </c:choose>
            </div>

                <%-- 페이지네이션 --%>
            <c:if test="${noteGroups.totalPages > 1}">
                <nav class="mt-5">
                    <ul class="pagination justify-content-center">
                        <c:forEach begin="0" end="${noteGroups.totalPages - 1}" var="i">
                            <li class="page-item ${noteGroups.number == i ? 'active' : ''}">
                                <a class="page-link" href="/note-groups?page=${i}">${i + 1}</a>
                            </li>
                        </c:forEach>
                    </ul>
                </nav>
            </c:if>
        </div>
    </jsp:body>
</t:layout>