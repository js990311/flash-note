package com.rejs.flashnote.domain.note.controller;

import com.navercorp.fixturemonkey.FixtureMonkey;
import com.rejs.flashnote.common.security.WithMockOidcMember;
import com.rejs.flashnote.common.test.TestDataBuilderGroup;
import com.rejs.flashnote.domain.note.dto.NoteDto;
import com.rejs.flashnote.domain.note.dto.request.note.NoteEditRequest;
import com.rejs.flashnote.domain.note.service.NoteService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static com.navercorp.fixturemonkey.api.expression.JavaGetterMethodPropertySelector.javaGetter;
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

    private final FixtureMonkey fixtureMonkey = TestDataBuilderGroup.fixtureMonkey();

    @Test
    @DisplayName("노트 생성 요청(POST): 성공 시 수정 페이지로 리다이렉트된다")
    @WithMockOidcMember
    void postNoteCreate_success() throws Exception {
        // given
        Long noteGroupId = 1L;
        Long createdNoteId = 100L;

        given(noteService.createNote(noteGroupId)).willReturn(createdNoteId);

        // when & then
        mockMvc.perform(post("/note/create")
                        .with(csrf()) // POST 요청 시 CSRF 토큰 필수 (Security 설정에 따라 다름)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED) // 폼 전송 타입
                        .param("noteGroupId", String.valueOf(noteGroupId))
                )
                .andExpect(status().is3xxRedirection()) // 리다이렉트 응답 코드 확인
                .andExpect(redirectedUrl("/note/" + createdNoteId + "/edit")); // 리다이렉트 경로 확인

        then(noteService).should().createNote(noteGroupId);
    }

    @Test
    @DisplayName("노트 상세 조회(GET): 뷰 이름과 모델 속성을 반환한다")
    @WithMockOidcMember
    void getNote_success() throws Exception {
        // given
        NoteDto noteDto = fixtureMonkey.giveMeOne(NoteDto.class);
        Long noteId = noteDto.getId();

        given(noteService.readById(noteId)).willReturn(noteDto);

        // when & then
        mockMvc.perform(get("/note/{id}", noteId))
                .andExpect(status().isOk())
                .andExpect(view().name("note/id")) // 뷰 이름 확인
                .andExpect(model().attributeExists("note")) // 모델 속성 존재 여부
                .andExpect(model().attribute("note", noteDto)); // 모델 값 검증
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
        mockMvc.perform(get("/note/{id}/edit", noteId))
                .andExpect(status().isOk())
                .andExpect(view().name("note/edit"))
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
        Long noteId = noteForm.getNoteId();

        // when & then
        mockMvc.perform(post("/note/{id}/edit", noteId)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .flashAttr("noteForm", noteForm)
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/note/" + noteId));

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
        Long noteId = noteForm.getNoteId();

        // when & then
        mockMvc.perform(post("/note/{id}/edit", noteId)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .flashAttr("noteForm", noteForm)
                )
                .andExpect(status().isOk()) // 리다이렉트 안됨 (200 OK)
                .andExpect(view().name("note/edit")) // 다시 폼 뷰 리턴
                .andExpect(model().attributeHasFieldErrors("noteForm", "title")); // title 필드 에러 확인

        // 서비스의 update 메서드는 호출되지 않아야 함
        then(noteService).shouldHaveNoInteractions();
    }}