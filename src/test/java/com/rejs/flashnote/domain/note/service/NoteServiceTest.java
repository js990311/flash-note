package com.rejs.flashnote.domain.note.service;

import com.navercorp.fixturemonkey.FixtureMonkey;
import com.rejs.flashnote.common.test.TestDataBuilderGroup;
import com.rejs.flashnote.domain.member.entity.Member;
import com.rejs.flashnote.domain.member.repository.MemberRepository;
import com.rejs.flashnote.domain.note.dto.NoteDto;
import com.rejs.flashnote.domain.note.dto.request.note.NoteEditRequest;
import com.rejs.flashnote.domain.note.entity.Note;
import com.rejs.flashnote.domain.note.repository.MyNoteGroupRepository;
import com.rejs.flashnote.domain.note.repository.NoteRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static com.navercorp.fixturemonkey.api.expression.JavaGetterMethodPropertySelector.javaGetter;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class NoteServiceTest {

    @InjectMocks
    private NoteService noteService;

    @Mock
    private NoteRepository noteRepository;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private MyNoteGroupRepository myNoteGroupRepository;

    // 제공해주신 빌더 그룹을 등록한 픽스처 멍키 인스턴스
    private final FixtureMonkey fixtureMonkey = TestDataBuilderGroup.fixtureMonkey();

    @Test
    @DisplayName("노트 생성: 유효한 그룹 ID가 주어지면 노트가 저장되고 ID를 반환한다")
    void createNote_success() {
        // given
        Long generatedNoteId = 100L;
        Long memberId = 100L;

        Member member = fixtureMonkey.giveMeBuilder(Member.class)
                .set(javaGetter(Member::getId),memberId)
                .sample();

        // 저장 후 반환될 노트 (ID가 있어야 함)
        Note savedNote = fixtureMonkey.giveMeBuilder(Note.class)
                .set(javaGetter(Note::getId), generatedNoteId)
                .set(javaGetter(Note::getMember), member)
                .sample();

        given(memberRepository.getReferenceById(memberId)).willReturn(member);
        // any(Note.class)를 사용하여 실제 Note.newNote() 로직으로 생성된 객체가 넘어가더라도 Mock 동작 보장
        given(noteRepository.save(any(Note.class))).willReturn(savedNote);

        // when
        Long resultId = noteService.createNote(memberId);

        // then
        assertThat(resultId).isEqualTo(generatedNoteId);
        then(memberRepository).should().getReferenceById(memberId);
        then(noteRepository).should().save(any(Note.class));
    }

    @Test
    @DisplayName("노트 단건 조회: 존재하는 ID 조회 시 NoteDto를 반환한다")
    void readById_success() {
        // given
        Long noteId = 100L;
        Note note = fixtureMonkey.giveMeBuilder(Note.class)
                .set("id", noteId)
                .sample();

        given(noteRepository.findById(noteId)).willReturn(Optional.of(note));

        // when
        NoteDto result = noteService.readById(noteId);

        // then
        assertThat(result).isNotNull();
        // NoteDto.from() 로직에 따라 매핑 확인 (여기선 ID와 제목 정도만 체크)
        assertThat(result.getId()).isEqualTo(noteId);
        assertThat(result.getTitle()).isEqualTo(note.getTitle());
    }

    @Test
    @DisplayName("노트 단건 조회: 존재하지 않는 ID 조회 시 예외가 발생한다")
    void readById_notFound() {
        // given
        Long noteId = 999L;
        given(noteRepository.findById(noteId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> noteService.readById(noteId))
                .isInstanceOf(NoSuchElementException.class);
    }

    @Test
    @DisplayName("노트 수정: 제목과 내용을 수정하면 변경 사항이 반영되고 ID를 반환한다")
    void updateNote_success() {
        // given
        Long noteId = 100L;

        // FixtureMonkey로 수정 요청 객체 생성
        NoteEditRequest request = fixtureMonkey.giveMeOne(NoteEditRequest.class);
        String newTitle = request.getTitle();
        String newContent = request.getContent();

        Note note = fixtureMonkey.giveMeBuilder(Note.class)
                .set("id", noteId)
                .sample();

        given(noteRepository.findById(noteId)).willReturn(Optional.of(note));

        // when
        Long resultId = noteService.updateNote(noteId, request);

        // then
        assertThat(resultId).isEqualTo(noteId);
        // 실제 엔티티의 필드가 변경되었는지 확인 (Dirty Checking을 위한 상태 변경 검증)
        assertThat(note.getTitle()).isEqualTo(newTitle);
        assertThat(note.getContent()).isEqualTo(newContent);
    }

    @Test
    @DisplayName("노트 삭제: 리포지토리의 삭제 메서드가 호출된다")
    void deleteNote_success() {
        Long noteId = 100L;
        Long expectedGroupId = 555L;

        Member member = fixtureMonkey.giveMeOne(Member.class);

        // 2. 삭제 대상 Note 생성 (위에서 만든 그룹과 연결)
        Note mockNote = fixtureMonkey.giveMeBuilder(Note.class)
                .set(javaGetter(Note::getId), noteId)
                .set(javaGetter(Note::getMember),member)
                .sample();

        // 3. findById 호출 시 mockNote를 반환하도록 설정
        given(noteRepository.findById(noteId)).willReturn(Optional.of(mockNote));

        // when
        noteService.deleteNote(noteId);

        // then

        // deleteById(id)가 아니라 delete(entity)가 호출되었는지 검증
        then(noteRepository).should().delete(mockNote);
    }
}