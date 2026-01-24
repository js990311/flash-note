<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>

<t:layout title="학습하기 - Flashnote">
    <jsp:attribute name="head">
        <meta name="_csrf" content="${_csrf.token}"/>
        <meta name="_csrf_header" content="${_csrf.headerName}"/>
    </jsp:attribute>

    <jsp:attribute name="script">
        <script src="<c:url value='/js/flash/study.js'/>"></script>
    </jsp:attribute>

    <jsp:body>
        <div class="card shadow-sm mx-auto" style="max-width: 500px;"
            id="card-container" data-deck-id="${deckId}"
        >
            <div class="card-body text-center">
                <div id="card-front" class="py-4">
                    <h5 class="text-muted small">Question</h5>
                    <h2 id="display-front" class="card-title fw-bold">질문 데이터가 로드됩니다.</h2>
                </div>

                <hr>

                <div id="card-back-container" class="py-4" style="min-height: 120px;">
                    <div id="card-back-content" style="display: none;">
                        <h5 class="text-muted small">Answer</h5>
                        <p id="display-back" class="fs-4 text-primary fw-semibold">정답 데이터</p>
                    </div>
                </div>

                <div class="mt-4">
                    <button id="btn-show" class="btn btn-secondary w-100 mb-2">정답 보기</button>

                    <div id="rating-buttons" class="btn-group w-100" style="display:none;">
                        <button type="button" class="btn btn-outline-danger" onclick="sendStudyResult(1)">AGAIN</button>
                        <button type="button" class="btn btn-outline-danger" onclick="sendStudyResult(2)">Hard</button>
                        <button type="button" class="btn btn-outline-warning" onclick="sendStudyResult(3)">Good</button>
                        <button type="button" class="btn btn-outline-success" onclick="sendStudyResult(4)">Easy</button>
                    </div>
                </div>
            </div>
        </div>
    </jsp:body>
</t:layout>