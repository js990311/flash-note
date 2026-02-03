<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>

<div class="table-responsive">
    <table class="table table-hover align-middle">
        <thead class="table-light">
        <tr>
            <th style="width: 40%">앞면</th>
            <th style="width: 40%">뒷면</th>
            <th style="width: 20%" class="text-center">관리</th>
        </tr>
        </thead>
        <tbody>
        <c:forEach items="${cards.contents}" var="card">
            <tr class="card-item">
                <td>${card.front}</td>
                <td>${card.back}</td>
                <td class="text-center">
                    <a href="/cards/${card.id}/update" class="btn btn-sm btn-outline-secondary"><i class="bi bi-pencil"></i></a>
                    <form action="/cards/${card.id}/delete" method="post" class="d-inline">
                        <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                        <button type="submit" class="btn btn-sm btn-outline-danger" onclick="return confirm('삭제하시겠습니까?')">
                            <i class="bi bi-trash"></i>
                        </button>
                    </form>
                </td>
            </tr>
        </c:forEach>
        <c:if test="${empty cards.contents}">
            <tr>
                <td colspan="3" class="text-center py-4 text-muted">아직 등록된 카드가 없습니다.</td>
            </tr>
        </c:if>
        </tbody>
    </table>
</div>

<div class="mt-4">
    <t:pagination meta="${cards.paginationMetadata}" />
</div>
