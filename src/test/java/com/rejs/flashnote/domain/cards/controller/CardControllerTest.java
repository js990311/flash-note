package com.rejs.flashnote.domain.cards.controller;

import com.navercorp.fixturemonkey.FixtureMonkey;
import com.rejs.flashnote.common.security.WithMockOidcMember;
import com.rejs.flashnote.common.test.TestDataBuilderGroup;
import com.rejs.flashnote.domain.cards.dto.CardDto;
import com.rejs.flashnote.domain.cards.dto.request.CreateCardRequest;
import com.rejs.flashnote.domain.cards.dto.request.UpdateCardRequest;
import com.rejs.flashnote.domain.cards.service.CardService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.validation.BindingResult;

import static com.navercorp.fixturemonkey.api.expression.JavaGetterMethodPropertySelector.javaGetter;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CardController.class)
class CardControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CardService cardService;

    private final FixtureMonkey fixtureMonkey = TestDataBuilderGroup.fixtureMonkey();

    @Test
    @DisplayName("카드 생성(POST): 성공 시 해당 덱 상세 페이지로 리다이렉트된다")
    @WithMockOidcMember
    void postCardCreate_success() throws Exception {
        // given
        Long deckId = 1L;
        CreateCardRequest request = fixtureMonkey.giveMeBuilder(CreateCardRequest.class)
                .set(javaGetter(CreateCardRequest::getDeckId), deckId)
                .sample();
        given(cardService.createCard(anyLong(), any(CreateCardRequest.class))).willReturn(10L);

        // when & then
        mockMvc.perform(post("/cards")
                        .flashAttr("cardCreateRequest", request) // @ModelAttribute 바인딩
                        .with(csrf())) // CSRF 적용 시
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/decks/" + deckId));
    }

    @Test
    @DisplayName("카드 생성(POST): 검증 실패 시 에러를 FlashAttribute에 담고 리다이렉트한다")
    @WithMockOidcMember
    void postCardCreate_fail_validation() throws Exception {
        // given
        Long deckId = 1L;
        // 빈 내용으로 보내서 검증 에러 유도
        CreateCardRequest request = fixtureMonkey.giveMeBuilder(CreateCardRequest.class)
                .set(javaGetter(CreateCardRequest::getDeckId), deckId)
                .sample();

        // when & then
        mockMvc.perform(post("/cards")
                        .param("deckId", deckId.toString())
                        .param("front", "") // 빈 값
                        .param("back", "")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/decks/" + deckId))
                .andExpect(flash().attributeExists(BindingResult.MODEL_KEY_PREFIX + "createCardRequest"))
                .andExpect(flash().attributeExists("createCardRequest"));
    }

    @Test
    @WithMockOidcMember
    @DisplayName("카드 삭제 후 해당 덱 상세 페이지로 리다이렉트되어야 한다")
    void postCardDelete_test() throws Exception {
        // given
        Long cardId = 1L;
        Long deckId = 10L;
        given(cardService.deleteCard(cardId)).willReturn(deckId);

        // when & then
        mockMvc.perform(post("/cards/{id}/delete", cardId)
                        .with(csrf())) // 시큐리티 CSRF 토큰 추가
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/decks/" + deckId));

        // 서비스가 실제로 호출되었는지 검증
        verify(cardService).deleteCard(cardId);
    }

    @Test
    @WithMockOidcMember
    @DisplayName("수정 페이지 진입 시 기존 카드 데이터를 모델에 담아 전송해야 한다")
    void getCardUpdate_test() throws Exception {
        // given
        Long cardId = 1L;
        CardDto cardDto = CardDto.builder()
                .id(cardId)
                .front("Old Front")
                .back("Old Back")
                .deckId(10L)
                .build();

        given(cardService.readById(cardId)).willReturn(cardDto);

        // when & then
        mockMvc.perform(get("/cards/{id}/update", cardId))
                .andExpect(status().isOk())
                .andExpect(view().name("cards/update"))
                .andExpect(model().attributeExists("updateCardRequest"));
    }

    // 2. 수정 실행 테스트 (POST - 성공 시 PRG 확인)
    @Test
    @WithMockOidcMember
    @DisplayName("카드 수정 성공 시 해당 덱 상세 페이지로 리다이렉트되어야 한다")
    void postCardUpdate_success_test() throws Exception {
        // given
        Long cardId = 1L;
        Long deckId = 10L;
        given(cardService.updateCard(anyLong(), any(UpdateCardRequest.class))).willReturn(deckId);

        // when & then
        mockMvc.perform(post("/cards/{id}/update", cardId)
                        .param("id", cardId.toString())
                        .param("front", "New Front")
                        .param("back", "New Back")
                        .param("deckId", deckId.toString())
                        .with(csrf())) // CSRF 필수
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/decks/" + deckId));
    }

    // 3. 수정 실패 테스트 (POST - Validation 에러 시 페이지 유지)
    @Test
    @WithMockOidcMember
    @DisplayName("검증 에러 발생 시 리다이렉트 하지 않고 수정 폼에 머물러야 한다")
    void postCardUpdate_fail_test() throws Exception {
        Long cardId = 1L;

        // when & then
        mockMvc.perform(post("/cards/{id}/update", cardId)
                        .param("id", cardId.toString())
                        .param("front", "")
                        .param("back", "Some Back")
                        .with(csrf()))
                .andExpect(status().isOk()) // 리다이렉트(302)가 아닌 200 OK
                .andExpect(view().name("cards/update"))
                .andExpect(model().hasErrors());
    }
}