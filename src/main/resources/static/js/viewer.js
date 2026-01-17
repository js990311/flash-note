document.addEventListener('DOMContentLoaded', function () {

    // 1. 뷰어가 그려질 DOM 요소 가져오기
    const viewerEl = document.querySelector('#viewer');

    // 2. 서버에서 받은 데이터가 숨겨진 Textarea 가져오기
    const contentEl = document.querySelector('#noteContent');

    // 두 요소가 모두 존재할 때만 실행 (에러 방지)
    if (viewerEl && contentEl) {

        // 숨겨진 값(Markdown Text) 꺼내기
        const content = contentEl.value;

        // Toast UI Editor(Viewer 모드) 초기화
        const viewer = toastui.Editor.factory({
            el: viewerEl,
            viewer: true,             // [중요] 뷰어 모드 설정 (편집 불가)
            initialValue: content,    // 꺼내온 컨텐츠 주입
            height: 'auto'            // 컨텐츠 길이에 맞춰 높이 자동 조절
        });
    }
});