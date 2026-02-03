<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>작성하기</title>

    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css">
    <link href="https://fonts.googleapis.com/css2?family=Pretendard:wght@400;600;700&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="https://uicdn.toast.com/editor/latest/toastui-editor.min.css" />
    <link rel="stylesheet" href="<c:url value='/css/global.css' />">
</head>
<body>
<form:form modelAttribute="noteForm" action="/notes/${noteForm.noteId}/edit" method="post" id="writeForm">

    <div class="mb-4">
        <form:input path="title"
                    cssClass="form-control form-control-lg border-0 border-bottom rounded-0 shadow-none"
                    id="title"
                    placeholder="제목을 입력하세요"
                    cssStyle="font-size: 2rem; font-weight: 700; background: transparent;" />
        <form:errors path="title" cssClass="text-danger" />
    </div>

    <div id="editor" class="mb-5"></div>

    <form:hidden path="content" id="content" />
    <form:hidden path="noteId" id="noteId" />

    <nav class="navbar fixed-bottom bg-white border-top py-3" style="z-index: 1000;">
        <div class="container d-flex justify-content-end">
            <button type="button" class="btn btn-primary px-4 fw-bold" id="btnSubmit">
                저장하기
            </button>
        </div>
    </nav>
    <div style="height: 60px;"></div>

</form:form>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
<script src="https://uicdn.toast.com/editor/latest/toastui-editor-all.min.js"></script>
<script src="<c:url value='/js/editor.js'/>"></script>
</body>
</html>