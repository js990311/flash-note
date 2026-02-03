package com.rejs.flashnote.domain.cards.controller;

import com.navercorp.fixturemonkey.FixtureMonkey;
import com.rejs.flashnote.common.security.WithMockOidcMember;
import com.rejs.flashnote.common.test.TestDataBuilderGroup;
import com.rejs.flashnote.domain.cards.dto.CardDto;
import com.rejs.flashnote.domain.cards.service.CardService;
import com.rejs.flashnote.domain.decks.controller.DeckController;
import com.rejs.flashnote.domain.decks.dto.DeckDto;
import com.rejs.flashnote.domain.decks.dto.request.CreateDeckRequest;
import com.rejs.flashnote.domain.decks.dto.request.UpdateDeckRequest;
import com.rejs.flashnote.domain.decks.service.DeckService;
import com.rejs.flashnote.global.controller.dto.Pagination;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static com.navercorp.fixturemonkey.api.expression.JavaGetterMethodPropertySelector.javaGetter;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(DeckController.class)
class DeckControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private DeckService deckService;

    @MockitoBean
    private CardService cardService;

    private final FixtureMonkey fixtureMonkey = TestDataBuilderGroup.fixtureMonkey();

    @Test
    @DisplayName("덱 목록 조회(GET): 페이징된 데이터와 뷰를 반환한다")
    @WithMockOidcMember // memberId=1L 가정
    void getPageDeckDto_success() throws Exception {
        // given
        Long memberId = 1L;
        List<DeckDto> deckDtos = fixtureMonkey.giveMe(DeckDto.class, 3);
        Page<DeckDto> deckPage = new PageImpl<>(deckDtos, PageRequest.of(0, 10), deckDtos.size());

        given(deckService.readDeckPageByMemberId(eq(memberId), any(Pageable.class)))
                .willReturn(deckPage);

        // when & then
        mockMvc.perform(get("/decks"))
                .andExpect(status().isOk())
                .andExpect(view().name("decks/page"))
                .andExpect(model().attributeExists("decks"))
                .andExpect(model().attribute("decks", instanceOf(Pagination.class)));
    }

    @Test
    @DisplayName("덱 상세 조회(GET): 덱 정보, 페이징된 카드, 생성 폼 객체를 모델에 담는다")
    @WithMockOidcMember
    void getDeckById_with_pagination_and_form() throws Exception {
        // given
        Long deckId = 100L;
        DeckDto deckDto = fixtureMonkey.giveMeBuilder(DeckDto.class)
                .set(javaGetter(DeckDto::getId), deckId)
                .sample();

        List<CardDto> cardList = fixtureMonkey.giveMe(CardDto.class, 5);
        Page<CardDto> cardPage = new PageImpl<>(cardList, PageRequest.of(0, 30), 5);

        given(deckService.readDeckById(deckId)).willReturn(deckDto);
        given(cardService.readPageByDeckId(eq(deckId), any(Pageable.class))).willReturn(cardPage);

        // when & then
        mockMvc.perform(get("/decks/{id}", deckId))
                .andExpect(status().isOk())
                .andExpect(view().name("decks/id"))
                .andExpect(model().attribute("deck", deckDto))
                .andExpect(model().attributeExists("cards")) // Pagination.from() 결과 확인
                .andExpect(model().attributeExists("createCardRequest")) // 빈 폼 확인
                .andExpect(model().attribute("createCardRequest",
                        hasProperty("deckId", is(deckId)))); // DTO의 deckId가 일치하는지 확인
    }

    @Test
    @DisplayName("덱 수정 폼 조회(GET): 존재하는 ID 조회 시 기존 정보를 모델에 담아 수정 페이지를 반환한다")
    @WithMockOidcMember
    void getDeckUpdate_success() throws Exception {
        // given
        Long deckId = 100L;
        String deckName = "덱원래이름";
        DeckDto deckDto = fixtureMonkey.giveMeBuilder(DeckDto.class)
                .set(javaGetter(DeckDto::getId), deckId)
                .set(javaGetter(DeckDto::getName), deckName)
                .sample();

        // 서비스에서 해당 덱을 찾아 반환하도록 설정
        given(deckService.readDeckById(deckId)).willReturn(deckDto);

        // when & then
        mockMvc.perform(get("/decks/{id}/update", deckId))
                .andExpect(status().isOk())
                .andExpect(view().name("decks/update"))
                .andExpect(model().attributeExists("request"))
                .andExpect(result -> {
                    // 모델에 담긴 request가 UpdateDeckRequest 타입인지, 데이터가 복사되었는지 검증
                    Object attribute = result.getModelAndView().getModel().get("request");
                    assertEquals(UpdateDeckRequest.class, attribute.getClass());

                    // UpdateDeckRequest.from(deckDto)가 제대로 작동했는지 확인
                    UpdateDeckRequest request = (UpdateDeckRequest) attribute;
                    assertEquals(deckName, request.getName());
                });

        then(deckService).should().readDeckById(deckId);
    }

    @Test
    @DisplayName("덱 수정 요청(POST): 수정 후 상세 페이지로 리다이렉트한다")
    @WithMockOidcMember
    void postDeckUpdate_success() throws Exception {
        // given
        Long deckId = 100L;
        UpdateDeckRequest request = fixtureMonkey.giveMeOne(UpdateDeckRequest.class);
        given(deckService.updateDeck(any(UpdateDeckRequest.class))).willReturn(deckId);

        // when & then
        mockMvc.perform(post("/decks/{id}/update", deckId)
                        .with(csrf())
                        .flashAttr("updateDeckRequest", request)) // @ModelAttribute 이름 확인 필요
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/decks/" + deckId));
    }

    @Test
    @DisplayName("덱 삭제 요청(POST): 현재는 삭제 후 해당 경로로 다시 리다이렉트한다")
    @WithMockOidcMember
    void postDeckDelete_success() throws Exception {
        // given
        Long deckId = 100L;

        // when & then
        mockMvc.perform(post("/decks/{id}/delete", deckId)
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/decks"));
    }
}