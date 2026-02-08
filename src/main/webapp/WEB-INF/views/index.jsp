<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>

<t:layout title="홈 - Flashnote">
    <jsp:body>

        <div class="text-center py-5">
            <div class="mb-4">
                <i class="bi bi-lightning-charge-fill text-primary" style="font-size: 5rem;"></i>
            </div>
            <h1 class="display-4 fw-bold mb-3">생각을 번개처럼 기록하세요</h1>
            <p class="lead text-secondary mb-4">
                AI가 당신의 노트를 자동으로 학습 카드로 변환합니다
            </p>
            <sec:authorize access="isAnonymous()">
                <a href="/oauth2/authorization/google" class="btn btn-primary btn-lg px-5 py-3 rounded-pill shadow-sm mb-5">
                    <i class="bi bi-google me-2"></i> 구글로 시작하기
                </a>
            </sec:authorize>

            <sec:authorize access="isAuthenticated()">
                <div class="mb-5">
                    <!-- 인사말 섹션 -->
                    <div class="alert alert-light border-0 shadow-sm p-4 mb-3">
                        <h4 class="fw-bold mb-1">반가워요, <sec:authentication property="principal.member.name"/>님! 👋</h4>
                        <p class="text-secondary mb-0">오늘은 어떤 지식을 번개처럼 저장해볼까요?</p>
                    </div>

                    <div class="row g-2 row-cols-1 row-cols-sm-2 row-cols-lg-4">
                        <div class="col">
                            <form action="<c:url value='/notes/create'/>" method="post" class="m-0 h-100">
                                <sec:csrfInput/>
                                <button type="submit" class="btn btn-primary rounded-pill px-3 w-100 h-100">
                                    <i class="bi bi-pencil-square me-1"></i> 새 노트
                                </button>
                            </form>
                        </div>
                        <div class="col">
                            <a href="<c:url value='/decks'/>" class="btn btn-success rounded-pill px-3 w-100 h-100 d-flex align-items-center justify-content-center">
                                <i class="bi bi-collection me-1"></i> 내 덱 보기
                            </a>
                        </div>
                        <div class="col">
                            <a href="<c:url value='/notes'/>" class="btn btn-outline-dark rounded-pill px-3 w-100 h-100 d-flex align-items-center justify-content-center">
                                <i class="bi bi-journal-text me-1"></i> 내 노트 보기
                            </a>
                        </div>
                        <div class="col">
                            <a href="<c:url value='/notes/search'/>" class="btn btn-outline-primary rounded-pill px-3 w-100 h-100 d-flex align-items-center justify-content-center">
                                <i class="bi bi-globe me-1"></i> 공개 노트 보기
                            </a>
                        </div>
                    </div>                </div>
            </sec:authorize>


        <%-- 사용법 (3단계) --%>
            <div class="row g-4 mt-5 text-start">
                <div class="col-12">
                    <h3 class="fw-bold text-center mb-4">간단한 3단계로 시작하세요</h3>
                </div>

                <div class="col-md-4">
                    <div class="card border-0 shadow-sm h-100">
                        <div class="card-body p-4">
                            <div class="d-flex align-items-center mb-3">
                                <span class="badge bg-primary rounded-circle p-3 me-3" style="width: 50px; height: 50px; display: flex; align-items: center; justify-content: center; font-size: 1.2rem;">
                                    1
                                </span>
                                <h5 class="fw-bold mb-0">노트 작성</h5>
                            </div>
                            <p class="text-secondary mb-3">
                                마크다운 에디터로 학습 내용을 자유롭게 정리하세요.
                            </p>
                            <div class="bg-light p-3 rounded">
                                <small class="text-muted">
                                    <i class="bi bi-info-circle me-1"></i>
                                    강의 노트, 책 요약, 개념 정리 등 무엇이든 좋습니다
                                </small>
                            </div>
                        </div>
                    </div>
                </div>

                <div class="col-md-4">
                    <div class="card border-0 shadow-sm h-100">
                        <div class="card-body p-4">
                            <div class="d-flex align-items-center mb-3">
                                <span class="badge bg-primary rounded-circle p-3 me-3" style="width: 50px; height: 50px; display: flex; align-items: center; justify-content: center; font-size: 1.2rem;">
                                    2
                                </span>
                                <h5 class="fw-bold mb-0">AI 카드 생성</h5>
                            </div>
                            <p class="text-secondary mb-3">
                                버튼 한 번으로 노트를 플래시카드로 자동 변환합니다.
                            </p>
                            <div class="bg-light p-3 rounded">
                                <small class="text-muted">
                                    <i class="bi bi-cpu me-1"></i>
                                    Gemini AI가 핵심 내용을 추출해 카드를 생성합니다
                                </small>
                            </div>
                        </div>
                    </div>
                </div>

                <div class="col-md-4">
                    <div class="card border-0 shadow-sm h-100">
                        <div class="card-body p-4">
                            <div class="d-flex align-items-center mb-3">
                                <span class="badge bg-primary rounded-circle p-3 me-3" style="width: 50px; height: 50px; display: flex; align-items: center; justify-content: center; font-size: 1.2rem;">
                                    3
                                </span>
                                <h5 class="fw-bold mb-0">간격 반복 학습</h5>
                            </div>
                            <p class="text-secondary mb-3">
                                과학적으로 검증된 FSRS 알고리즘으로 효율적인 복습을 하세요.
                            </p>
                            <div class="bg-light p-3 rounded">
                                <small class="text-muted">
                                    <i class="bi bi-graph-up me-1"></i>
                                    최적의 타이밍에 카드를 자동으로 제시합니다
                                </small>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
            <div class="row mt-5">
                <div class="col-12 alert alert-warning border-0 shadow-sm">
                    <div class="d-flex align-items-start text-start">
                        <i class="bi bi-exclamation-triangle-fill fs-4 me-3"></i>
                        <div>
                            <h6 class="fw-bold mb-2">AI 서비스 이용 및 개인정보 주의사항</h6>
                            <ul class="mb-0 small">
                                <li class="mb-1">
                                    <strong>AI 사용 범위:</strong> 입력하신 노트 데이터는 오직 <strong>"AI 카드 생성" 단계에서만</strong> 외부 AI 모델(Gemini)로 전송됩니다. 일반적인 노트 저장 및 편집, 카드 생성, 수정, 학습시에는 AI에게 데이터가 전달되지 않습니다.
                                </li>
                                <li class="mb-1">생성된 카드의 내용이 부정확하거나 중요한 내용이 누락될 수 있으니 <strong>학습 전 반드시 검토</strong>하세요.</li>
                                <li class="mb-1 text-danger fw-bold">무료 버전 AI 모델을 사용 중이므로, 입력하신 데이터는 AI 모델의 성능 향상을 위한 학습 데이터로 활용될 수 있습니다.</li>
                                <li>주민등록번호, 비밀번호, 기업 기밀 등 <strong>민감한 개인정보나 보안 데이터는 입력하지 마십시오.</strong></li>
                            </ul>
                        </div>
                    </div>
                </div>
            </div>

                <%-- 주요 기능 --%>
            <div class="mt-5 pt-5">
                <h4 class="fw-bold text-center mb-4">핵심 기능</h4>
                <div class="row g-4">
                    <div class="col-md-3">
                        <i class="bi bi-pencil-square text-primary fs-1 d-block mb-2"></i>
                        <h6 class="fw-bold">마크다운 지원</h6>
                        <p class="small text-secondary mb-0">구조화된 노트 작성</p>
                    </div>
                    <div class="col-md-3">
                        <i class="bi bi-robot text-primary fs-1 d-block mb-2"></i>
                        <h6 class="fw-bold">AI 자동 생성</h6>
                        <p class="small text-secondary mb-0">스마트한 카드 변환</p>
                    </div>
                    <div class="col-md-3">
                        <i class="bi bi-clock-history text-primary fs-1 d-block mb-2"></i>
                        <h6 class="fw-bold">간격 반복</h6>
                        <p class="small text-secondary mb-0">기억 최적화 알고리즘</p>
                    </div>
                    <div class="col-md-3">
                        <i class="bi bi-cloud text-primary fs-1 d-block mb-2"></i>
                        <h6 class="fw-bold">자동 동기화</h6>
                        <p class="small text-secondary mb-0">어디서나 접근 가능</p>
                    </div>
                </div>
            </div>
        </div>


    </jsp:body>
</t:layout>