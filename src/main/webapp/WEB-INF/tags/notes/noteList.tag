<%-- src/main/webapp/WEB-INF/tags/noteList.tag --%>
<%@ tag language="java" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ attribute name="notes" type="java.util.List" required="true" rtexprvalue="true" %>
<%@ attribute name="showActions" type="java.lang.Boolean" required="false" %>
<%@ attribute name="emptyMessage" type="java.lang.String" required="false" %>

<%-- 기본값 설정 --%>
<c:if test="${empty showActions}">
    <c:set var="showActions" value="true"/>
</c:if>
<c:if test="${empty emptyMessage}">
    <c:set var="emptyMessage" value="작성된 노트가 없습니다."/>
</c:if>

<div class="card border-0 shadow-sm rounded-3 overflow-hidden">
    <div class="table-responsive">
        <table class="table table-hover align-middle mb-0">
            <thead class="table-light">
            <tr>
                <th scope="col" style="width: 50%">제목</th>
                <th scope="col" style="width: 25%">수정일</th>
                <c:if test="${showActions}">
                    <th scope="col" class="pe-4 text-center" style="width: 15%">관리</th>
                </c:if>
            </tr>
            </thead>
            <tbody>
            <c:choose>
                <c:when test="${not empty notes}">
                    <c:forEach var="note" items="${notes}">
                        <tr>
                            <td>
                                <a href="<c:url value='/notes/${note.id}'/>"
                                   class="text-decoration-none text-dark fw-semibold d-block text-truncate"
                                   style="max-width: 400px;">
                                    <c:out value="${note.title}"/>
                                </a>
                                    <%-- 내용 미리보기 --%>
                                <c:if test="${not empty note.content}">
                                    <small class="text-muted d-block text-truncate" style="max-width: 500px;">
                                        <c:out value="${note.content}"/>
                                    </small>
                                </c:if>
                            </td>
                            <td class="text-muted small">
                                <i class="bi bi-clock me-1"></i> ${note.updatedAt}
                            </td>
                            <c:if test="${showActions}">
                                <td class="pe-4 text-center">
                                    <div class="btn-group">
                                        <a href="<c:url value='/notes/${note.id}/edit'/>"
                                           class="btn btn-sm btn-outline-secondary border-0"
                                           title="수정">
                                            <i class="bi bi-pencil"></i>
                                        </a>
                                        <form action="<c:url value='/notes/${note.id}/delete'/>"
                                              method="post"
                                              style="display:inline;"
                                              onsubmit="return confirm('정말 삭제하시겠습니까?');">
                                            <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                                            <button type="submit"
                                                    class="btn btn-sm btn-outline-danger border-0"
                                                    title="삭제">
                                                <i class="bi bi-trash"></i>
                                            </button>
                                        </form>
                                    </div>
                                </td>
                            </c:if>
                        </tr>
                    </c:forEach>
                </c:when>
                <c:otherwise>
                    <tr>
                        <td colspan="${showActions ? 3 : 2}" class="text-center py-5 text-muted">
                                ${emptyMessage}
                        </td>
                    </tr>
                </c:otherwise>
            </c:choose>
            </tbody>
        </table>
    </div>
</div>