<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>

<t:layout title="홈 - Flashnote">
    <jsp:body>
        <sec:authorize access="isAnonymous()">
            <%-- 로그인 전: 랜딩 섹션 --%>
            <div class="text-center py-5">
                <div class="display-4 fw-bold text-dark mb-3">생각을 번개처럼 기록하세요</div>
                <p class="lead text-secondary mb-4">
                    Flashnote는 당신의 아이디어를 가장 빠르고 깔끔하게 정리해 드립니다.
                </p>
                <a href="/oauth2/authorization/google" class="btn btn-primary btn-lg px-5 py-3 rounded-pill shadow">
                    <i class="bi bi-google me-2"></i> 구글로 무료 시작하기
                </a>
            </div>
        </sec:authorize>

        <sec:authorize access="isAuthenticated()">
            <%-- 로그인 후: 대시보드 섹션 --%>
            <div class="d-flex justify-content-between align-items-center mb-4">
                <h2 class="fw-bold m-0">최근 작성한 노트</h2>
                <a href="/notes/new" class="btn btn-link text-decoration-none">모두 보기 <i class="bi bi-chevron-right"></i></a>
            </div>

            <div class="row g-4">
                    <%-- 샘플 카드 (나중에 서버 데이터로 반복문 처리) --%>
                <div class="col-md-6">
                    <div class="card h-100 border-0 shadow-sm p-3" style="background-color: #fff9db; border-left: 5px solid #fab005 !important;">
                        <div class="card-body">
                            <h5 class="card-title fw-bold">🚀 프로젝트 아이디어</h5>
                            <p class="card-text text-secondary text-truncate">스프링 시큐리티와 OAuth2를 이용한 메모 앱 만들기...</p>
                            <small class="text-muted">방금 전</small>
                        </div>
                    </div>
                </div>
                <div class="col-md-6">
                    <div class="card h-100 border-0 shadow-sm p-3" style="background-color: #e7f5ff; border-left: 5px solid #228be6 !important;">
                        <div class="card-body">
                            <h5 class="card-title fw-bold">🛒 장보기 목록</h5>
                            <p class="card-text text-secondary text-truncate">우유, 계란, 파, 닭가슴살...</p>
                            <small class="text-muted">2시간 전</small>
                        </div>
                    </div>
                </div>
            </div>
        </sec:authorize>
    </jsp:body>
</t:layout>