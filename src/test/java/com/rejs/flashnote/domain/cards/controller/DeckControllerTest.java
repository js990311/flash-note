package com.rejs.flashnote.domain.cards.controller;

import com.navercorp.fixturemonkey.FixtureMonkey;
import com.rejs.flashnote.common.security.WithMockOidcMember;
import com.rejs.flashnote.common.test.TestDataBuilderGroup;
import com.rejs.flashnote.domain.cards.dto.DeckDto;
import com.rejs.flashnote.domain.cards.dto.request.CreateDeckRequest;
import com.rejs.flashnote.domain.cards.dto.request.UpdateDeckRequest;
import com.rejs.flashnote.domain.cards.service.DeckService;
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

import static org.hamcrest.Matchers.instanceOf;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
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
    @DisplayName("덱 상세 조회(GET): ID로 조회된 덱 정보를 모델에 담는다")
    @WithMockOidcMember
    void getDeckById_success() throws Exception {
        // given
        Long deckId = 100L;
        DeckDto deckDto = fixtureMonkey.giveMeOne(DeckDto.class);
        given(deckService.readDeckById(deckId)).willReturn(deckDto);

        // when & then
        mockMvc.perform(get("/decks/{id}", deckId))
                .andExpect(status().isOk())
                .andExpect(view().name("decks/{id}")) // 컨트롤러에 작성된 경로 기준
                .andExpect(model().attribute("deck", deckDto));
    }

    @Test
    @DisplayName("덱 생성 요청(POST): 생성 후 상세 페이지로 리다이렉트한다")
    @WithMockOidcMember
    void postDeckCreate_success() throws Exception {
        // given
        Long memberId = 1L;
        Long createdDeckId = 200L;
        CreateDeckRequest request = fixtureMonkey.giveMeOne(CreateDeckRequest.class);

        given(deckService.createDeck(eq(memberId), any(CreateDeckRequest.class)))
                .willReturn(createdDeckId);

        // when & then
        mockMvc.perform(post("/decks/create")
                        .with(csrf())
                        .flashAttr("request", request))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/decks/" + createdDeckId));
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