<%@ tag language="java" pageEncoding="UTF-8" %>
<%@ attribute name="title" required="false" %>
<%@ attribute name="head" fragment="true" %>
<%@ attribute name="script" fragment="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>${not empty title ? title : 'Flashnote'}</title>

    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css">
    <link href="https://fonts.googleapis.com/css2?family=Pretendard:wght@400;600;700&display=swap" rel="stylesheet">

    <link rel="stylesheet" href="<c:url value='/css/global.css' />">
    <jsp:invoke fragment="head"/>
</head>
<body>

<jsp:include page="/WEB-INF/tags/nav.jsp" />

<main class="container note-container">
    <div class="main-card p-4 p-md-5">
        <%-- 타이틀 영역이 필요한 경우를 위해 구조화 --%>
        <c:if test="${not empty title}">
            <h1 class="h3 mb-4 fw-bold text-dark border-bottom pb-3">${title}</h1>
        </c:if>

        <jsp:doBody/>
    </div>
</main>
<footer class="container mt-5 py-3 text-center border-top">
    <p class="text-muted" style="font-size: 0.9rem;">
        © 2026 Flashnote.
        Licensed under the <a href="/license" class="text-decoration-none text-primary">MIT License</a>
        <br>
        <span style="font-size: 0.8rem;">Copyright (c) 2025 Open Spaced Repetition</span>
    </p>
</footer>
<script src="https://code.jquery.com/jquery-3.7.1.min.js"></script>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
<jsp:invoke fragment="script"/>
</body>
</html>