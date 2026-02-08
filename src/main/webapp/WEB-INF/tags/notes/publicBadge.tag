<%@ tag language="java" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<%@ attribute name="isPublic" type="java.lang.Boolean" required="true" %>
<%@ attribute name="showText" type="java.lang.Boolean" required="false" %>

<c:if test="${empty showText}">
    <c:set var="showText" value="true"/>
</c:if>

<c:choose>
    <c:when test="${isPublic}">
        <span class="badge rounded-pill bg-success-subtle text-success border border-success-subtle px-2">
            <i class="bi bi-globe-americas me-1"></i>
            <c:if test="${showText}">공개</c:if>
        </span>
    </c:when>
    <c:otherwise>
        <span class="badge rounded-pill bg-light text-secondary border px-2">
            <i class="bi bi-lock-fill me-1"></i>
            <c:if test="${showText}">비공개</c:if>
        </span>
    </c:otherwise>
</c:choose>