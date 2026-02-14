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

                <%-- 통합 목록 출력 --%>
            <n:noteList notes="${notes}" />
            <t:pagination
                    meta="${pagination}"
            />
        </div>
    </jsp:body>
</t:layout>