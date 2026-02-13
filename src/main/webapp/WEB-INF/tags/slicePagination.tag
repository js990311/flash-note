<%@ tag language="java" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%@ attribute name="baseUrl" required="true" type="java.lang.String" %>
<%@ attribute name="keyword" required="false" type="java.lang.String" %>
<%@ attribute name="searchOption" required="false" type="java.lang.String" %>

<%@ attribute name="pageNumber" required="true" type="java.lang.Integer" %>  <%-- 0-base --%>
<%@ attribute name="pageSize" required="true" type="java.lang.Integer" %>

<%@ attribute name="hasPrev" required="true" type="java.lang.Boolean" %>
<%@ attribute name="hasNext" required="true" type="java.lang.Boolean" %>

<div class="mt-4 d-flex justify-content-center align-items-center gap-3">

    <%-- 이전 --%>
    <c:choose>
        <c:when test="${hasPrev}">
            <a class="btn btn-outline-dark"
               href="<c:url value='${baseUrl}'>
                        <c:if test='${not empty keyword}'><c:param name='keyword' value='${keyword}'/></c:if>
                        <c:if test='${not empty searchOption}'><c:param name='searchOption' value='${searchOption}'/></c:if>
                        <c:param name='page' value='${pageNumber - 1}'/>
                        <c:param name='size' value='${pageSize}'/>
                    </c:url>">
                이전
            </a>
        </c:when>
        <c:otherwise>
            <button class="btn btn-outline-secondary" disabled>이전</button>
        </c:otherwise>
    </c:choose>

    <%-- 현재 페이지(1-base 표시) --%>
    <span class="fw-bold">${pageNumber + 1} 페이지</span>

    <%-- 다음 --%>
    <c:choose>
        <c:when test="${hasNext}">
            <a class="btn btn-outline-dark"
               href="<c:url value='${baseUrl}'>
                        <c:if test='${not empty keyword}'><c:param name='keyword' value='${keyword}'/></c:if>
                        <c:if test='${not empty searchOption}'><c:param name='searchOption' value='${searchOption}'/></c:if>
                        <c:param name='page' value='${pageNumber + 1}'/>
                        <c:param name='size' value='${pageSize}'/>
                    </c:url>">
                다음
            </a>
        </c:when>
        <c:otherwise>
            <button class="btn btn-outline-secondary" disabled>다음</button>
        </c:otherwise>
    </c:choose>

</div>
