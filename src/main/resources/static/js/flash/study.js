let cardQueue = [];
let currentIndex = 0;

$(document).ready(function() {
    // 1. 요소에 심어진 deckId 가져오기
    const deckId = $("#card-container").data("deck-id");

    // CSRF 설정
    const token = $("meta[name='_csrf']").attr("content");
    const header = $("meta[name='_csrf_header']").attr("content");
    $.ajaxSetup({
        beforeSend: function(xhr) {
            if (header && token) xhr.setRequestHeader(header, token);
        }
    });

    // 2. 카드 로드 시작
    if (deckId) {
        loadCards(deckId);
    } else {
        console.error("Deck ID를 찾을 수 없습니다.");
    }

    // 정답 보기 버튼 이벤트
    $("#btn-show").click(function() {
        $("#card-back-content").show();
        $(this).hide();
        $("#rating-buttons").show();
    });
});

function loadCards(deckId) {
    $.get(`/api/study/${deckId}/cards`, function(data) {
        cardQueue = data;
        if (cardQueue.length > 0) {
            renderCard();
        } else {
            alert("학습할 카드가 없습니다.");
            location.href = "/";
        }
    });
}

function renderCard() {
    const card = cardQueue[currentIndex];

    // UI 초기화
    $("#display-front").text(card.front);
    $("#display-back").text(card.back);

    $("#card-back-content").hide();
    $("#rating-buttons").hide();
    $("#btn-show").show();
}

function sendStudyResult(ratingValue) {
    const card = cardQueue[currentIndex];

    $.ajax({
        url: `/api/study/${card.id}`,
        type: "POST",
        contentType: "application/json",
        data: JSON.stringify({ ratingValue: ratingValue }),
        success: function() {
            // 성공 시 다음 카드로
            currentIndex++;
            if (currentIndex < cardQueue.length) {
                renderCard();
            } else {
                alert("오늘의 학습을 모두 마쳤습니다!");
                const deckId = $("#card-container").data("deck-id");
                location.href = `/decks/${deckId}`;
            }
        },
        error: function() {
            alert("결과 저장 중 오류가 발생했습니다.");
        }
    });
}