const editor = new toastui.Editor({
    el: document.querySelector('#editor'),
    height: '100vh',
    initialEditType: 'markdown',
    initialValue: document.getElementById('content').value,
    previewStyle: window.innerWidth < 768 ? 'tab' : 'vertical',
    hideModeSwitch:true,
    toolbarItems: [
        ['heading', 'bold', 'italic', 'strike'],
        ['hr', 'quote'],
        ['ul', 'ol', 'task', 'indent', 'outdent'],
        ['table', 'link'],
        ['code', 'codeblock']
    ]
});


document.getElementById('btnSubmit').addEventListener('click', function(e) {
    e.preventDefault(); // 혹시 모를 기본 제출 동작 방지

    // 1. 제목 확인
    const titleInput = document.getElementById('title');
    const title = titleInput.value;

    // 콘솔 확인
    console.log("제목 값:", title);

    if (!title.trim()) {
        alert('제목을 입력해주세요.');
        titleInput.focus();
        return;
    }

    // 2. 에디터 내용 가져오기
    const content = editor.getMarkdown();

    // 콘솔 확인
    console.log("내용 값:", content);

    if (!content.trim()) {
        alert('내용을 작성해주세요.');
        return;
    }

    // 3. 폼에 내용 담고 제출
    const contentInput = document.getElementById('content');
    contentInput.value = content;

    // 최종적으로 input에 값이 들어갔는지 확인
    console.log("Hidden Input 값:", contentInput.value);

    // 폼 제출
    document.getElementById('writeForm').submit();
});

