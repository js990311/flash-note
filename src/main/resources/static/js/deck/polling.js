$(document).ready(function() {
    const deckId = window.location.pathname.split('/')[2];
    const $statusContainer = $('#ai-gen-status');
    const initialState = $statusContainer.data('state');

    // CSRF 토큰 가져오기
    const csrfToken = $('meta[name="_csrf"]').attr('content');
    const csrfHeader = $('meta[name="_csrf_header"]').attr('content');

    let pollingInterval = null;

    // AJAX 요청 전역 설정 (CSRF 토큰 자동 포함)
    $.ajaxSetup({
        beforeSend: function(xhr) {
            if (csrfToken && csrfHeader) {
                xhr.setRequestHeader(csrfHeader, csrfToken);
            }
        }
    });

    // AI 생성 중인 경우에만 폴링 시작
    if (initialState === 'AI_GENERATING' || $statusContainer.find('.alert-info').length > 0) {
        startPolling();
    }

    function startPolling() {
        // 3초마다 상태 체크
        pollingInterval = setInterval(function() {
            checkDeckState();
        }, 3000);
    }

    function stopPolling() {
        if (pollingInterval) {
            clearInterval(pollingInterval);
            pollingInterval = null;
        }
    }

    function checkDeckState() {
        $.ajax({
            url: `/decks/${deckId}/state`,
            method: 'GET',
            dataType: 'json',
            success: function(response) {
                const currentState = response.state;

                if (currentState === 'COMPLETED') {
                    // AI 생성 완료 - 카드 목록 새로고침
                    stopPolling();
                    refreshCards();
                    removeStatusAlert();

                } else if (currentState === 'AI_GEN_FAILED') {
                    // AI 생성 실패 - 실패 메시지 표시
                    stopPolling();
                    loadFailureFragment();
                }
                // AI_GENERATING 상태면 계속 폴링
            },
            error: function(xhr, status, error) {
                console.error('상태 확인 실패:', error);
                // 에러 발생 시에도 폴링 계속 (일시적 네트워크 오류 대응)
            }
        });
    }

    function refreshCards() {
        $.ajax({
            url: `/decks/${deckId}/cards`,
            method: 'GET',
            success: function(html) {
                $('#cards-container').html(html);
                },
            error: function(xhr, status, error) {
                console.error('카드 목록 로드 실패:', error);
            }
        });
    }

    function loadFailureFragment() {
        $.ajax({
            url: `/decks/${deckId}/fail-fragment`,
            method: 'GET',
            success: function(html) {
                $statusContainer.html(html);
            },
            error: function(xhr, status, error) {
                console.error('실패 메시지 로드 실패:', error);
                // 폴백: 기본 에러 메시지 표시
                $statusContainer.html(`
                    <div class="alert alert-danger">
                        <h4 class="alert-heading">AI 카드 생성 실패</h4>
                        <p>카드 생성 중 오류가 발생했습니다. 다시 시도해주세요.</p>
                    </div>
                `);
            }
        });
    }

    function removeStatusAlert() {
        $statusContainer.fadeOut(300, function() {
            $(this).html('').show();
        });
    }

    // 페이지를 떠날 때 폴링 정리
    $(window).on('beforeunload', function() {
        stopPolling();
    });
});