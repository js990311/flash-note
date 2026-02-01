package com.rejs.flashnote.domain.cards.controller;

import com.navercorp.fixturemonkey.FixtureMonkey;
import com.rejs.flashnote.common.security.WithMockOidcMember;
import com.rejs.flashnote.common.test.TestDataBuilderGroup;
import com.rejs.flashnote.domain.cards.authorization.CardAuthorizationStrategy;
import com.rejs.flashnote.domain.cards.dto.CardDto;
import com.rejs.flashnote.domain.cards.dto.request.StudyRequest;
import com.rejs.flashnote.domain.cards.service.FlashCardService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;

import static com.navercorp.fixturemonkey.api.expression.JavaGetterMethodPropertySelector.javaGetter;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(FlashCardController.class)
@Import(ObjectMapper.class)
class FlashCardControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private FlashCardService flashCardService;

    @Autowired
    private ObjectMapper objectMapper;

    private final FixtureMonkey fixtureMonkey = TestDataBuilderGroup.fixtureMonkey();

    @Test
    @DisplayName("학습 카드 목록 조회(GET): 성공 시 JSON 배열을 반환한다")
    @WithMockOidcMember
    void getStudyCards_success() throws Exception {
        // given
        Long deckId = 1L;
        List<CardDto> mockCards = fixtureMonkey.giveMe(CardDto.class, 3);

        given(flashCardService.getTodayFlashCards(anyLong(), eq(deckId), anyInt()))
                .willReturn(mockCards);

        // when & then
        mockMvc.perform(get("/api/study/{deckId}/cards", deckId)
                        .param("limit", "30"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(3));
    }

    @Test
    @DisplayName("카드 학습 결과 업데이트(POST): 성공 시 204 No Content를 반환한다")
    @WithMockOidcMember
    void updateResource_success() throws Exception {
        // given
        Long cardId = 100L;
        StudyRequest request = fixtureMonkey.giveMeBuilder(StudyRequest.class)
                .set(javaGetter(StudyRequest::getRatingValue), 3)
                .sample();
        request.setRatingValue(3);

        // when & then
        mockMvc.perform(post("/api/study/{cardId}", cardId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .with(csrf()))
                .andExpect(status().isNoContent());

        verify(flashCardService).studyCard(eq(cardId), eq(3));
    }

    @Test
    @DisplayName("카드 학습 결과 업데이트(POST): 평점 범위를 벗어나면 400 에러가 발생한다")
    @WithMockOidcMember
    void updateResource_fail_validation() throws Exception {
        // given
        Long cardId = 100L;
        StudyRequest request = FixtureMonkey.create().giveMeBuilder(StudyRequest.class)
                .set(javaGetter(StudyRequest::getRatingValue), 5)// @Max(4) 위반
                .sample();

        // when & then
        mockMvc.perform(post("/api/study/{cardId}", cardId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .with(csrf()))
                .andExpect(status().is5xxServerError());
    }

}