<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>

<t:layout title="노트 그룹 목록 - Flashnote">
    <jsp:body>
        <div class="container py-4">
                <%-- 헤더 섹션 --%>
            <div class="d-flex justify-content-between align-items-center mb-4">
                <div>
                    <h2 class="fw-bold m-0 text-dark">내 노트 그룹</h2>
                    <p class="text-muted small mb-0">총 <c:out value="${noteGroups.paginationMetadata.totalElements}"/>개의 그룹이 있습니다.</p>
                </div>
                <a href="/note-groups/create" class="btn btn-primary rounded-pill px-4 shadow-sm">
                    <i class="bi bi-plus-lg me-1"></i> 새 그룹 생성
                </a>
            </div>

                <%-- 리스트 섹션 --%>
            <div class="bg-white shadow-sm rounded-4 overflow-hidden border">
                <c:choose>
                    <c:when test="${not empty noteGroups.contents}">
                        <div class="list-group list-group-flush">
                            <c:forEach var="group" items="${noteGroups.contents}">
                                <a href="/note-groups/${group.id}"
                                   class="list-group-item list-group-item-action p-3 d-flex align-items-center border-bottom justify-content-between transition">
                                    <div class="d-flex align-items-center">
                                        <div class="bg-light p-2 rounded-3 me-3">
                                            <i class="bi bi-folder2 text-primary fs-5"></i>
                                        </div>
                                        <div>
                                            <h6 class="fw-bold mb-0 text-dark">
                                                <c:out value="${group.name}" />
                                            </h6>
                                                <%-- 설명이 필요한 경우 추가 필드를 사용할 수 있습니다 --%>
                                            <small class="text-muted">노트 보러가기</small>
                                        </div>
                                    </div>
                                    <i class="bi bi-chevron-right text-muted small"></i>
                                </a>
                            </c:forEach>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <div class="text-center py-5">
                            <i class="bi bi-folder-x display-4 text-light-emphasis"></i>
                            <p class="text-muted mt-3 mb-0">생성된 노트 그룹이 없습니다.</p>
                        </div>
                    </c:otherwise>
                </c:choose>
            </div>

                <%-- 페이지네이션 --%>
            <c:if test="${noteGroups.paginationMetadata.totalPages > 1}">
                <div class="mt-4">
                    <t:pagination meta="${noteGroups.paginationMetadata}" />
                </div>
            </c:if>
        </div>
    </jsp:body>
</t:layout>