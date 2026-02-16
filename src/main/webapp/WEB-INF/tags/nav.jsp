<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<nav class="navbar navbar-expand-lg navbar-light bg-white border-bottom sticky-top">
    <div class="container">
        <%-- 로고 --%>
        <a class="navbar-brand d-flex align-items-center" href="<c:url value='/' />">
            <i class="bi bi-lightning-charge-fill me-2"></i>
            <span>Flashnote</span>
        </a>

        <%-- 모바일 토글 버튼 --%>
        <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNav">
            <span class="navbar-toggler-icon"></span>
        </button>

        <div class="collapse navbar-collapse" id="navbarNav">
            <ul class="navbar-nav me-auto">
                <li class="nav-item">
                    <a class="nav-link" href="<c:url value="/notes"/>">내 노트</a>
                </li>
                <li class="nav-item">
                    <a class="nav-link" href="<c:url value="/decks"/>">내 카드</a>
                </li>
                <li class="nav-item">
                    <a class="nav-link" href="<c:url value="/notes/search"/>">공개 노트</a> 
                </li>
            </ul>

            <ul class="navbar-nav align-items-center">
                <sec:authorize access="isAnonymous()">
                    <li class="nav-item ms-lg-2">
                        <a class="btn btn-primary btn-sm px-3" href="/login">
                            로그인
                        </a>
                    </li>
                </sec:authorize>

                <sec:authorize access="isAuthenticated()">
                    <%-- 로그인한 상태 --%>
                    <li class="nav-item me-3">
                        <form:form action="${pageContext.request.contextPath}/notes/create" method="post" style="display:inline;">
                            <button type="submit" class="btn btn-outline-primary btn-sm rounded-pill">
                                <i class="bi bi-pencil-square me-1"></i> 새 노트
                            </button>
                        </form:form>                    </li>
                    <li class="nav-item dropdown">
                        <a class="nav-link dropdown-toggle d-flex align-items-center" href="#" id="userDropdown" role="button" data-bs-toggle="dropdown">
                            <span class="fw-medium"><sec:authentication property="principal.member.name" /></span>
                        </a>
                        <ul class="dropdown-menu dropdown-menu-end shadow-sm border-0 mt-2">
                            <li><a class="dropdown-item" href="<c:url value='/profile' />">내 프로필</a></li>
                            <li><hr class="dropdown-divider"></li>
                            <li>
                                <form action="<c:url value='/logout' />" method="post" class="m-0">
                                    <sec:csrfInput/>
                                    <button type="submit" class="dropdown-item text-danger">로그아웃</button>
                                </form>
                            </li>
                        </ul>
                    </li>
                </sec:authorize>
            </ul>
        </div>
    </div>
</nav>
