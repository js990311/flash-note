package com.rejs.flashnote.domain.note.controller;

import com.navercorp.fixturemonkey.FixtureMonkey;
import com.rejs.flashnote.common.security.WithMockOidcMember;
import com.rejs.flashnote.common.test.TestDataBuilderGroup;
import com.rejs.flashnote.domain.note.dto.request.note.NoteEditRequest;
import com.rejs.flashnote.domain.note.error.NoteException;
import com.rejs.flashnote.domain.note.service.NoteService;
import com.rejs.flashnote.global.exception.code.CommonErrorCode;
import com.rejs.flashnote.global.exception.throwable.BusinessException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import static com.navercorp.fixturemonkey.api.expression.JavaGetterMethodPropertySelector.javaGetter;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(NoteRestController.class)
@Import(ObjectMapper.class)
class NoteRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private NoteService noteService;

    @Autowired
    private ObjectMapper objectMapper;

    // FixtureMonkey 설정 (TestDataBuilderGroup 라이브러리 활용)
    private final FixtureMonkey fixtureMonkey = TestDataBuilderGroup.fixtureMonkey();

    @Test
    @WithMockOidcMember
    @DisplayName("노트 수정(POST): 성공 시 리다이렉트 경로 문자열을 반환한다")
    void postNoteEdit_success() throws Exception {
        // given
        Long noteId = 1L;
        NoteEditRequest request = fixtureMonkey.giveMeBuilder(NoteEditRequest.class)
                .set(javaGetter(NoteEditRequest::getTitle), "정상 제목")
                .set(javaGetter(NoteEditRequest::getContent), "정상 내용")
                .sample();

        // void 메서드 서비스 모킹
        given(noteService.updateNote(eq(noteId), any(NoteEditRequest.class)))
                .willReturn(noteId);

        // when & then
        mockMvc.perform(post("/api/note/{id}/edit", noteId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON) // 어드바이스 AJAX 판별용
                        .content(objectMapper.writeValueAsString(request))
                        .with(csrf())) // Security 환경일 경우 추가
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.redirectUrl").value("/notes/" + noteId));

        verify(noteService).updateNote(eq(noteId), any(NoteEditRequest.class));
    }

    @Test
    @WithMockOidcMember
    @DisplayName("노트 수정(POST): 유효성 검사 실패 시 400 에러와 에러 상세(Object)를 반환한다")
    void postNoteEdit_fail_validation() throws Exception {
        // given
        Long noteId = 1L;
        // @NotEmpty 위반을 위해 빈 문자열 설정
        NoteEditRequest request = FixtureMonkey.create().giveMeBuilder(NoteEditRequest.class)
                .set(javaGetter(NoteEditRequest::getTitle), "")
                .set(javaGetter(NoteEditRequest::getContent), "내용만 있음")
                .sample();

        // when & then
        mockMvc.perform(post("/api/note/{id}/edit", noteId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .with(csrf()))
                .andExpect(status().isBadRequest()) // InvalidParameterException -> 400
                .andExpect(jsonPath("$.type").exists())
                .andExpect(jsonPath("$.title").exists())
                .andExpect(jsonPath("$.detail").isArray()) // detail이 리스트(Object)인지 검증
                .andExpect(jsonPath("$.detail[0].field").value("title"))
                .andExpect(jsonPath("$.instance").value("/api/note/" + noteId + "/edit"));
    }

    @Test
    @WithMockOidcMember
    @DisplayName("노트 수정(POST): 비즈니스 예외 발생 시 해당 ErrorCode의 상태코드를 반환한다")
    void postNoteEdit_fail_business_exception() throws Exception {
        // given
        Long noteId = 999L;
        NoteEditRequest request = fixtureMonkey.giveMeOne(NoteEditRequest.class);

        // 존재하지 않는 노트 예외 상황 모킹
        doThrow(NoteException.notFound()) // 상황에 맞는 에러코드 사용
                .when(noteService).updateNote(eq(noteId), any(NoteEditRequest.class));

        // when & then
        mockMvc.perform(post("/api/note/{id}/edit", noteId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .with(csrf()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }
}