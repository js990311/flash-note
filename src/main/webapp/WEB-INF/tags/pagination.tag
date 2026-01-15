<%@ tag language="java" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%-- PaginationMetadata 객체를 받습니다 --%>
<%@ attribute name="meta" type="com.rejs.flashnote.global.controller.dto.PaginationMetadata" required="true" %>

<%-- 현재 브라우저 주소창의 URI 추출 (Spring Forward 대응) --%>
<c:set var="currentUri" value="${requestScope['jakarta.servlet.forward.request_uri']}" />
<c:if test="${empty currentUri}">
    <c:set var="currentUri" value="${pageContext.request.requestURI}" />
</c:if>

<%-- 페이지 번호를 제외한 기존 쿼리 파라미터 조립 (나중에 검색 기능 추가 시 유용) --%>
<c:url var="baseUrl" value="${currentUri}">
    <c:forEach items="${param}" var="p">
        <c:if test="${p.key ne 'page'}">
            <c:param name="${p.key}" value="${p.value}"/>
        </c:if>
    </c:forEach>
</c:url>

<%-- 파라미터가 이미 있으면 &를, 없으면 ?를 붙이기 위한 처리 --%>
<c:set var="connector" value="${empty baseUrl || !baseUrl.contains('?') ? '?' : '&'}" />

<nav aria-label="Page navigation">
    <ul class="pagination justify-content-center">
        <%-- 처음으로 --%>
        <li class="page-item ${meta.currentPage == 1 ? 'disabled' : ''}">
            <a class="page-link" href="${baseUrl}${connector}page=0"><i class="bi bi-chevron-double-left"></i></a>
        </li>

        <%-- 이전 블록 --%>
        <li class="page-item ${!meta.hasPrevious ? 'disabled' : ''}">
            <a class="page-link" href="${baseUrl}${connector}page=${meta.startPage - 2}"><i class="bi bi-chevron-left"></i></a>
        </li>

        <%-- 페이지 번호 --%>
        <c:forEach begin="${meta.startPage}" end="${meta.endPage}" var="idx">
            <li class="page-item ${meta.currentPage == idx ? 'active' : ''}">
                <a class="page-link" href="${baseUrl}${connector}page=${idx - 1}">${idx}</a>
            </li>
        </c:forEach>

        <%-- 다음 블록 --%>
        <li class="page-item ${!meta.hasNext ? 'disabled' : ''}">
            <a class="page-link" href="${baseUrl}${connector}page=${meta.endPage}"><i class="bi bi-chevron-right"></i></a>
        </li>

        <%-- 마지막으로 --%>
        <li class="page-item ${meta.currentPage == meta.totalPages || meta.totalPages == 0 ? 'disabled' : ''}">
            <a class="page-link" href="${baseUrl}${connector}page=${meta.totalPages - 1}"><i class="bi bi-chevron-double-right"></i></a>
        </li>
    </ul>
</nav>