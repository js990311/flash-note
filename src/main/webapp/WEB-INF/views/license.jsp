<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>

<t:layout title="홈 - Flashnote">
    <jsp:body>
        <div class="container mt-5">
            <h3>Open Source License</h3>
            <hr>
        <div class="mb-4">
            <h5>java-fsrs</h5>
            <p>Source: <a href="https://github.com/open-spaced-repetition/java-fsrs" target="_blank">https://github.com/open-spaced-repetition/java-fsrs</a></p>
            <div class="card bg-light">
                <div class="card-body">
                <pre style="white-space: pre-wrap; margin-bottom: 0;">
MIT License

Copyright (c) 2025 Open Spaced Repetition

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
                </pre>
                </div>
            </div>
        </div>
            <button onclick="history.back()" class="btn btn-secondary mt-3">돌아가기</button>
        </div>
    </jsp:body>
</t:layout>