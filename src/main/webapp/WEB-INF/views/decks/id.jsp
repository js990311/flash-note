<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>

<t:layout title="${deck.name} - Flashnote">
    <jsp:body>
        <div class="container py-5">
            <nav aria-label="breadcrumb" class="mb-3">
                <ol class="breadcrumb">
                    <li class="breadcrumb-item"><a href="/decks">덱 목록</a></li>
                    <li class="breadcrumb-item active">${deck.name}</li>
                </ol>
            </nav>

            <div class="card border-0 shadow-sm p-4 mb-4">
                <div class="d-flex justify-content-between align-items-start">
                    <div>
                        <h2 class="fw-bold mb-1"><c:out value="${deck.name}"/></h2>
                        <span class="badge bg-primary-soft text-primary">카드 ${deck.cardCounts}장</span>
                    </div>
                    <a href="/decks/${deck.id}/update" class="btn btn-outline-secondary btn-sm">
                        <i class="bi bi-pencil me-1"></i> 이름 수정
                    </a>
                </div>
            </div>

            <div class="py-5 text-center bg-light rounded-3 border border-dashed">
                <p class="text-muted mb-0">덱 내부 카드 목록 영역입니다. (추후 구현)</p>
            </div>
        </div>
    </jsp:body>
</t:layout>