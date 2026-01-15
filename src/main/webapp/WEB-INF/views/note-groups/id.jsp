<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>

<t:layout title="${noteGroup.name} - Flashnote">
    <jsp:body>
        <div class="container py-4">
            <div class="d-flex justify-content-between align-items-center mb-4">
                <div>
                    <nav aria-label="breadcrumb">
                        <ol class="breadcrumb mb-2">
                            <li class="breadcrumb-item"><a href="/note-groups" class="text-decoration-none">목록</a></li>
                            <li class="breadcrumb-item active">그룹 상세</li>
                        </ol>
                    </nav>
                        <%-- DTO가 클래스이므로 .name으로 접근 --%>
                    <h2 class="fw-bold m-0"><c:out value="${noteGroup.name}" /></h2>
                </div>
            </div>

            <div class="card border-0 shadow-sm p-5 bg-light text-center rounded-4">
                <i class="bi bi-journal-text display-1 text-secondary mb-3"></i>
                <h4 class="text-secondary">아직 작성된 내용이 없습니다.</h4>
                <p class="text-muted">이 그룹에 새로운 노트를 추가해 보세요.</p>
            </div>
        </div>
    </jsp:body>
</t:layout>