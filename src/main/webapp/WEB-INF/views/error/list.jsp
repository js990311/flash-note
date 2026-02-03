<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<t:layout title="시스템 에러 명세서">
    <jsp:body>
        <div class="container py-5">
                <%-- 헤더 섹션 --%>
            <div class="mb-5">
                <h2 class="fw-bold mb-3">Error Code Index</h2>
                <p class="text-secondary">
                    시스템에서 발생 가능한 모든 비즈니스 및 표준 에러 목록입니다.<br>
                    RFC 7807 표준에 따라 에러의 유형(Type), 제목(Title), 상태(Status)를 정의합니다.
                </p>
            </div>

                <%-- 에러 코드 테이블 --%>
            <div class="card shadow-sm border-0">
                <div class="table-responsive">
                    <table class="table table-hover align-middle mb-0">
                        <thead class="table-light">
                        <tr>
                            <th class="ps-4" style="width: 15%">HTTP Status</th>
                            <th style="width: 20%">Error Code (Type)</th>
                            <th style="width: 25%">Title</th>
                            <th class="pe-4">Default Message (Detail)</th>
                        </tr>
                        </thead>
                        <tbody>
                        <c:forEach var="code" items="${errorCodes}">
                            <tr id="${code.type}">
                                <td class="ps-4">
                                        <%-- 상태 코드에 따른 색상 분기 (4xx: Warning, 5xx: Danger) --%>
                                    <c:set var="statusColor" value="${code.status.is4xxClientError() ? 'bg-warning text-dark' : 'bg-danger'}" />
                                    <span class="badge ${statusColor} px-3 py-2">
                                            <c:out value="${code.status.value()}" /> <c:out value="${code.status.reasonPhrase}" />
                                        </span>
                                </td>
                                <td>
                                    <code class="fw-bold text-primary">${code.type}</code>
                                </td>
                                <td>
                                    <span class="fw-semibold">${code.title}</span>
                                </td>
                                <td class="pe-4 text-secondary">
                                        ${code.detail}
                                </td>
                            </tr>
                        </c:forEach>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
    </jsp:body>
</t:layout>