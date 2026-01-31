<%@ page contentType="text/html;charset=UTF-8" language="java" isErrorPage="true" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="e" tagdir="/WEB-INF/tags/error" %>

<t:layout title="${title}">
    <jsp:body>
        <div class="text-center py-5">
            <div class="display-1 fw-bold text-muted opacity-25 mb-3">${status}</div>
            <h2 class="fw-bold mb-3">${title}</h2>
            <p class="text-secondary mb-5">${detail}</p>

            <a href="/" class="btn btn-outline-primary px-4">홈으로 돌아가기</a>
            <e:error-docs-link type="${type}" />
        </div>
    </jsp:body>
</t:layout>