<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<t:layout title="${note.title} - Flashnote">
    <jsp:attribute name="head">
            <link rel="stylesheet" href="https://uicdn.toast.com/editor/latest/toastui-editor.min.css" />
    </jsp:attribute>
    <jsp:attribute name="script">
        <script src="https://uicdn.toast.com/editor/latest/toastui-editor-all.min.js"></script>
        <script src="<c:url value='/js/viewer.js'/>"></script>
    </jsp:attribute>

    <jsp:body>
        <div class="container py-5">

                <%-- 1. 상단 헤더 영역 (네비게이션 + 제목 + 버튼) --%>
            <div class="mb-4 pb-3 border-bottom">
                <div class="d-flex justify-content-between align-items-start">

                        <%-- 왼쪽: 네비게이션 및 제목 --%>
                    <div class="col-8">
                        <nav aria-label="breadcrumb">
                            <ol class="breadcrumb mb-2 text-muted small">
                                <li class="breadcrumb-item"><a href="/notes" class="text-decoration-none">노트 목록</a></li>
                                <li class="breadcrumb-item active">노트 상세</li>
                            </ol>
                        </nav>
                        <h1 class="fw-bold text-dark mb-0 text-break">
                            <c:out value="${note.title}" />
                        </h1>
                    </div>

                    <%-- 오른쪽: 수정/삭제 버튼 그룹 --%>
                    <div class="col-4 d-flex justify-content-end gap-2">
                        <form id="generateForm" action="/notes/${note.id}/generate" method="post">
                            <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
                            <button type="submit" class="btn btn-success" id="generateBtn">
                                <i class="bi bi-cpu me-1"></i> AI 카드 생성
                            </button>
                        </form>

                        <a href="/notes/${note.id}/edit" class="btn btn-outline-primary">
                            <i class="bi bi-pencil-square me-1"></i> 수정
                        </a>

                        <form action="/notes/${note.id}/delete" method="post">
                            <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
                            <button type="submit" class="btn btn-outline-danger">
                                <i class="bi bi-trash me-1"></i> 삭제
                            </button>
                        </form>
                    </div>
                </div>
            </div>

                <%-- 2. 뷰어 영역 (종이 질감 카드 UI) --%>
            <div class="card border-0 shadow-sm rounded-3">
                <div class="card-body p-4 p-md-5" style="min-height: 500px;">
                    <div id="viewer"></div>
                </div>
            </div>

            <textarea hidden="hidden" id="noteContent" style="display:none;"><c:out value="${note.content}" /></textarea>
        </div>
    </jsp:body>

</t:layout>