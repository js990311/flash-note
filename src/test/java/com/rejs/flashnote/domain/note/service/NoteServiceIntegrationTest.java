package com.rejs.flashnote.domain.note.service;

import com.navercorp.fixturemonkey.FixtureMonkey;
import com.rejs.flashnote.TestcontainersConfiguration;
import com.rejs.flashnote.common.test.TestDataBuilderGroup;
import com.rejs.flashnote.domain.member.entity.Member;
import com.rejs.flashnote.domain.member.repository.MemberRepository;
import com.rejs.flashnote.domain.note.dto.NoteDto;
import com.rejs.flashnote.domain.note.dto.request.CreateNoteGroupRequest;
import com.rejs.flashnote.domain.note.dto.request.NoteEditRequest;
import com.rejs.flashnote.domain.note.entity.Note;
import com.rejs.flashnote.domain.note.entity.NoteGroup;
import com.rejs.flashnote.domain.note.entity.NotePermission;
import com.rejs.flashnote.domain.note.repository.MyNoteGroupRepository;
import com.rejs.flashnote.domain.note.repository.NoteGroupRepository;
import com.rejs.flashnote.domain.note.repository.NotePermissionRepository;
import com.rejs.flashnote.domain.note.repository.NoteRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.shaded.org.checkerframework.checker.units.qual.A;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static com.navercorp.fixturemonkey.api.expression.JavaGetterMethodPropertySelector.javaGetter;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@Import({TestcontainersConfiguration.class})
@ActiveProfiles("test")
@SpringBootTest
class NoteServiceIntegrationTest {

    @Autowired
    private NoteGroupService noteGroupService;
    @Autowired private MemberRepository memberRepository;
    @Autowired private NoteGroupRepository noteGroupRepository;
    @Autowired private NotePermissionRepository notePermissionRepository;
    @Autowired private NoteService noteService;
    @Autowired private NoteRepository noteRepository;
    private final FixtureMonkey fixtureMonkey = TestDataBuilderGroup.fixtureMonkey();

    @Test
    @DisplayName("노트 생성 통합 테스트: DB에 정상적으로 저장되어야 한다")
    void createNote_integration() {
        // given
        // 1. 부모 데이터(Member, NoteGroup) 미리 저장
        Member member = memberRepository.save(fixtureMonkey.giveMeOne(Member.class));
        Long noteGroupId = noteGroupService.createNoteGroup(member.getId(), fixtureMonkey.giveMeOne(CreateNoteGroupRequest.class));

        // when
        Long savedNoteId = noteService.createNote(noteGroupId);

        // then
        Note foundNote = noteRepository.findById(savedNoteId).orElseThrow();

        assertThat(foundNote.getId()).isNotNull();
        assertThat(foundNote.getGroup().getId()).isEqualTo(noteGroupId);
    }

    @Test
    @DisplayName("노트 조회 통합 테스트: 저장된 노트를 DTO로 변환하여 반환한다")
    void readById_integration() {
        // given
        Member member = memberRepository.save(fixtureMonkey.giveMeOne(Member.class));
        Long noteGroupId = noteGroupService.createNoteGroup(member.getId(), fixtureMonkey.giveMeOne(CreateNoteGroupRequest.class));
        NoteGroup group = noteGroupRepository.findById(noteGroupId).orElseThrow();

        Note note = fixtureMonkey.giveMeBuilder(Note.class)
                .set(javaGetter(Note::getGroup), group)
                .sample();
        Note savedNote = noteRepository.save(note);

        // when
        NoteDto result = noteService.readById(savedNote.getId());

        // then
        assertThat(result.getId()).isEqualTo(savedNote.getId());
        assertThat(result.getTitle()).isEqualTo(savedNote.getTitle());
    }

    @Test
    @DisplayName("노트 페이징 조회 통합 테스트: QueryDSL/JPQL이 정상 동작하여 페이지를 반환한다")
    void readPageByNoteGroupId_integration() {
        // given
        Member member = memberRepository.save(fixtureMonkey.giveMeOne(Member.class));
        Long noteGroupId = noteGroupService.createNoteGroup(member.getId(), fixtureMonkey.giveMeOne(CreateNoteGroupRequest.class));
        NoteGroup group = noteGroupRepository.findById(noteGroupId).orElseThrow();

        // 같은 그룹에 노트 15개 생성
        List<Note> notes = fixtureMonkey.giveMeBuilder(Note.class)
                .set(javaGetter(Note::getGroup), group)
                .sampleList(15);
        noteRepository.saveAll(notes);

        PageRequest pageRequest = PageRequest.of(0, 10);

        // when
        Page<NoteDto> result = noteService.readPageByNoteGroupId(group.getId(), pageRequest);

        // then
        assertThat(result.getTotalElements()).isEqualTo(15);
        assertThat(result.getContent()).hasSize(10);
        assertThat(result.getNumber()).isEqualTo(0);
    }

    @Test
    @DisplayName("노트 수정 통합 테스트: 더티 체킹(Dirty Checking)이 동작하여 DB 값이 변경되어야 한다")
    void updateNote_integration() {
        // given
        Member member = memberRepository.save(fixtureMonkey.giveMeOne(Member.class));
        Long noteGroupId = noteGroupService.createNoteGroup(member.getId(), fixtureMonkey.giveMeOne(CreateNoteGroupRequest.class));
        NoteGroup group = noteGroupRepository.findById(noteGroupId).orElseThrow();
        Note note = fixtureMonkey.giveMeBuilder(Note.class)
                .set(javaGetter(Note::getGroup), group)
                .sample();
        Note savedNote = noteRepository.save(note);

        NoteEditRequest request = fixtureMonkey.giveMeOne(NoteEditRequest.class);

        // when
        noteService.updateNote(savedNote.getId(), request);

        // then
        // 영속성 컨텍스트 초기화 (혹은 flush) 후 다시 조회해야 확실한 DB 반영 확인 가능
        // @Transactional 안에서는 1차 캐시 조회일 수 있으나, 값 변경 확인은 가능함
        Note updatedNote = noteRepository.findById(savedNote.getId()).orElseThrow();

        assertThat(updatedNote.getTitle()).isEqualTo(request.getTitle());
        assertThat(updatedNote.getContent()).isEqualTo(request.getContent());
    }

    @Test
    @DisplayName("노트 삭제 통합 테스트: DB에서 데이터가 사라져야 한다")
    void deleteNote_integration() {
        // given
        Member member = memberRepository.save(fixtureMonkey.giveMeOne(Member.class));
        Long noteGroupId = noteGroupService.createNoteGroup(member.getId(), fixtureMonkey.giveMeOne(CreateNoteGroupRequest.class));
        NoteGroup group = noteGroupRepository.findById(noteGroupId).orElseThrow();
        Note note = fixtureMonkey.giveMeBuilder(Note.class)
                .set(javaGetter(Note::getGroup), group)
                .sample();
        Note savedNote = noteRepository.save(note);

        Long noteId = savedNote.getId();

        // when
        noteService.deleteNote(noteId);

        // then
        assertThatThrownBy(() -> noteService.readById(noteId))
                .isInstanceOf(NoSuchElementException.class);

        assertThat(noteRepository.findById(noteId)).isEmpty();
    }}