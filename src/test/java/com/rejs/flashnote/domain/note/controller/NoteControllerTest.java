package com.rejs.flashnote.domain.note.controller;

import com.navercorp.fixturemonkey.FixtureMonkey;
import com.rejs.flashnote.common.security.WithMockOidcMember;
import com.rejs.flashnote.common.test.TestDataBuilderGroup;
import com.rejs.flashnote.domain.note.dto.NoteDto;
import com.rejs.flashnote.domain.note.dto.NoteSummaryDto;
import com.rejs.flashnote.domain.note.dto.request.note.NoteEditRequest;
import com.rejs.flashnote.domain.note.service.NoteSearchService;
import com.rejs.flashnote.domain.note.service.NoteService;
import com.rejs.flashnote.global.controller.dto.Pagination;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static com.navercorp.fixturemonkey.api.expression.JavaGetterMethodPropertySelector.javaGetter;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(NoteController.class)
class NoteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private NoteService noteService;

    @MockitoBean
    private NoteSearchService noteSearchService;

    private final FixtureMonkey fixtureMonkey = TestDataBuilderGroup.fixtureMonkey();

    @Test
    @DisplayName("노트 생성 요청(POST): 성공 시 수정 페이지로 리다이렉트된다")
    @WithMockOidcMember
    void postNoteCreate_success() throws Exception {
        // given
        Long memberId = 1L;
        Long createdNoteId = 100L;

        given(noteService.createNote(memberId)).willReturn(createdNoteId);

        // when & then
        mockMvc.perform(post("/notes/create")
                        .with(csrf()) // POST 요청 시 CSRF 토큰 필수 (Security 설정에 따라 다름)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED) // 폼 전송 타입
                )
                .andExpect(status().is3xxRedirection()) // 리다이렉트 응답 코드 확인
                .andExpect(redirectedUrl("/notes/" + createdNoteId + "/edit")); // 리다이렉트 경로 확인

        then(noteService).should().createNote(memberId);
    }

    @Test
    @DisplayName("노트 상세 조회(GET): 뷰 이름과 모델 속성을 반환한다")
    @WithMockOidcMember
    void getNote_success() throws Exception {
        // given
        Long noteId = 1L;
        NoteDto noteDto = fixtureMonkey.giveMeBuilder(NoteDto.class)
                .set(javaGetter(NoteDto::getId), noteId)
                .sample();

        given(noteService.readById(noteId)).willReturn(noteDto);

        // when & then

        mockMvc.perform(get("/notes/{id}", noteId))
                .andExpect(status().isOk())
                .andExpect(view().name("notes/id")) // 뷰 이름 확인
                .andExpect(model().attributeExists("note")) // 모델 속성 존재 여부
                .andExpect(model().attribute("note", noteDto)); // 모델 값 검증
    }

    @Test
    @DisplayName("내 노트 목록 조회(GET)")
    @WithMockOidcMember()
    void getNotePage_success() throws Exception {
        // given
        Long memberId = 1L;

        // 1. Mock 데이터 생성
        List<NoteDto> noteDtoList = fixtureMonkey.giveMe(NoteDto.class, 3);
        Page<NoteDto> noteDtoPage = new PageImpl<>(noteDtoList, PageRequest.of(0, 10), noteDtoList.size());

        // 2. 서비스 Mocking (수정된 부분: noteSearchService를 Mocking함)
        given(noteService.readByPage(eq(memberId), any(Pageable.class)))
                .willReturn(noteDtoPage);

        // when & then
        mockMvc.perform(get("/notes")
                        .param("page", "0")
                        .param("size", "10")
                )
                .andExpect(status().isOk())
                .andExpect(view().name("notes/page"))
                .andExpect(model().attributeExists("notes"))
                .andExpect(model().attribute("notes", instanceOf(List.class)));
        
        // then: 호출 검증도 noteSearchService로 변경
        then(noteService).should().readByPage(eq(memberId), any(Pageable.class));
    }

    @Test
    @DisplayName("노트 수정 폼 조회(GET): DTO를 폼 객체로 변환하여 모델에 담는다")
    @WithMockOidcMember
    void getNoteEdit_success() throws Exception {
        // given
        Long noteId = 100L;
        NoteDto noteDto = fixtureMonkey.giveMeBuilder(NoteDto.class)
                .set("id", noteId)
                .set("title", "Old Title")
                .sample();

        given(noteService.readById(noteId)).willReturn(noteDto);

        // when & then
        mockMvc.perform(get("/notes/{id}/edit", noteId))
                .andExpect(status().isOk())
                .andExpect(view().name("notes/edit"))
                .andExpect(model().attributeExists("noteForm")) // "noteForm" 이름 확인
                .andExpect(model().attribute("noteForm",
                        NoteEditRequest.from(noteDto)
                ));
    }

    @Test
    @DisplayName("노트 수정 요청(POST): 유효성 검사 성공 시 업데이트 후 상세 페이지로 리다이렉트")
    @WithMockOidcMember
    void postNoteEdit_success() throws Exception {
        // given
        NoteEditRequest noteForm = fixtureMonkey.giveMeOne(NoteEditRequest.class);
        Long noteId = 100L;

        // when & then
        mockMvc.perform(post("/notes/{id}/edit", noteId)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .flashAttr("noteForm", noteForm)
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/notes/" + noteId));

        // 서비스가 올바른 인자로 호출되었는지 검증
        then(noteService).should().updateNote(eq(noteId), any(NoteEditRequest.class));
    }

    @Test
    @DisplayName("노트 수정 요청(POST): 유효성 검사 실패 시 다시 수정 폼으로 돌아간다")
    @WithMockOidcMember
    void postNoteEdit_validationFail() throws Exception {
        // given
        // 제목이 비어있음 -> @NotBlank 위반 가정
        NoteEditRequest noteForm = FixtureMonkey.create().giveMeBuilder(NoteEditRequest.class)
                .setNull(javaGetter(NoteEditRequest::getTitle))
                .sample();
        Long noteId = 100L;

        // when & then
        mockMvc.perform(post("/notes/{id}/edit", noteId)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .flashAttr("noteForm", noteForm)
                )
                .andExpect(status().isOk()) // 리다이렉트 안됨 (200 OK)
                .andExpect(view().name("notes/edit")) // 다시 폼 뷰 리턴
                .andExpect(model().attributeHasFieldErrors("noteForm", "title")); // title 필드 에러 확인

        // 서비스의 update 메서드는 호출되지 않아야 함
        then(noteService).shouldHaveNoInteractions();
    }

    @Test
    @DisplayName("노트 삭제 요청(POST): 삭제 후 해당 노트가 속해있던 그룹 페이지로 리다이렉트된다")
    @WithMockOidcMember
    void deleteNote_success() throws Exception {
        // given
        Long noteId = 100L;

        // when & then
        mockMvc.perform(post("/notes/{id}/delete", noteId)
                        .with(csrf())
                )
                .andExpect(status().is3xxRedirection()) // 302 Found 등 리다이렉트 응답 확인
                .andExpect(redirectedUrl("/notes")); // 리다이렉트 경로 검증

        // 서비스 메서드가 정확한 인자로 호출되었는지 검증
        then(noteService).should().deleteNote(noteId);
    }
}