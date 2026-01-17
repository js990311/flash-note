<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<t:layout title="${noteGroup.name} - Flashnote">
    <jsp:body>
        <div class="container py-4">
                <%-- 상단 헤더 영역 --%>
            <div class="d-flex justify-content-between align-items-center mb-4">
                <div>
                    <nav aria-label="breadcrumb">
                        <ol class="breadcrumb mb-2">
                            <li class="breadcrumb-item"><a href="/note-groups" class="text-decoration-none">목록</a></li>
                            <li class="breadcrumb-item active">그룹 상세</li>
                        </ol>
                    </nav>
                    <h2 class="fw-bold m-0"><c:out value="${noteGroup.name}" /></h2>
                </div>

                    <%-- 버튼 그룹 --%>
                <div class="d-flex gap-2">
                    <form action="/note/create" method="post">
                        <input type="hidden" name="noteGroupId" value="${noteGroup.id}"/>
                        <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
                        <button type="submit" class="btn btn-primary shadow-sm">
                            <i class="bi bi-plus-lg me-1"></i> 새 노트
                        </button>
                    </form>

                    <a href="/note-groups/${noteGroup.id}/update" class="btn btn-outline-secondary shadow-sm">
                        <i class="bi bi-pencil-square me-1"></i> 그룹 수정
                    </a>

                    <form:form action="/note-groups/${noteGroup.id}/delete" method="post">
                        <button type="submit" class="btn btn-outline-danger shadow-sm">
                            <i class="bi bi-trash me-1"></i> 그룹 삭제
                        </button>
                    </form:form>
                </div>
            </div>

                <%-- 컨텐츠 영역 (노트 목록이 비어있을 때) --%>
            <div class="card border-0 shadow-sm p-5 bg-light text-center rounded-4">
                <i class="bi bi-journal-text display-1 text-secondary mb-3"></i>
                <h4 class="text-secondary">아직 작성된 내용이 없습니다.</h4>
                <p class="text-muted mb-4">이 그룹에 새로운 노트를 추가해 보세요.</p>

                <form action="/note/create" method="post" class="d-inline-block">
                    <input type="hidden" name="noteGroupId" value="${noteGroup.id}"/>
                    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
                    <button type="submit" class="btn btn-primary btn-lg px-4 rounded-pill">
                        <i class="bi bi-pencil-fill me-2"></i>첫 번째 노트 작성하기
                    </button>
                </form>
            </div>
        </div>
    </jsp:body>
</t:layout>