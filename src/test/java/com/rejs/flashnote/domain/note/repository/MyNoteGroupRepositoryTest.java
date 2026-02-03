package com.rejs.flashnote.domain.note.repository;

import com.navercorp.fixturemonkey.FixtureMonkey;
import com.rejs.flashnote.TestcontainersConfiguration;
import com.rejs.flashnote.common.test.TestDataBuilderGroup;
import com.rejs.flashnote.domain.member.entity.Member;
import com.rejs.flashnote.domain.member.repository.MemberRepository;
import com.rejs.flashnote.domain.note.dto.NoteDto;
import com.rejs.flashnote.domain.note.entity.Note;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Import({TestcontainersConfiguration.class})
@ActiveProfiles("test")
@SpringBootTest
@Transactional
class MyNoteGroupRepositoryTest {

    @Autowired
    private MyNoteGroupRepository myNoteGroupRepository;

    @Autowired
    private MemberRepository memberRepository;


    @Autowired
    private EntityManager entityManager;

    private final FixtureMonkey fixtureMonkey = TestDataBuilderGroup.fixtureMonkey();

    @Test
    @DisplayName("사용자 ID로 노트 목록을 페이징하여 조회한다")
    void findByMemberId_success() {
        // given
        // 1. 테스트용 회원 생성 및 저장
        Member member = fixtureMonkey.giveMeOne(Member.class);
        memberRepository.save(member);

        Member otherMember = fixtureMonkey.giveMeOne(Member.class);
        memberRepository.save(otherMember);

        Integer noteCounts = 15;
        Integer pageSize = 10;

        // 2. 해당 회원의 노트 15개 생성 및 저장
        List<Note> myNotes = fixtureMonkey.giveMeBuilder(Note.class)
                .set("member", member)
                .sampleList(noteCounts);

        // 3. 다른 회원의 노트 생성 (조회 결과에 섞이지 않아야 함)
        List<Note> otherNotes = fixtureMonkey.giveMeBuilder(Note.class)
                .set("member", otherMember)
                .sampleList(5);

        myNotes.forEach(entityManager::persist);
        otherNotes.forEach(entityManager::persist);

        entityManager.flush();
        entityManager.clear();

        // 첫 번째 페이지, 사이즈 10으로 요청
        Pageable pageable = PageRequest.of(0, 10);

        // when
        Page<NoteDto> result = myNoteGroupRepository.findByMemberId(member.getId(), pageable);

        // then
        assertEquals( Long.valueOf(noteCounts), (Long) result.getTotalElements()); // 전체 개수 검증 (Count Query)
        assertEquals(pageSize, result.getContent().size());        // 페이징 사이즈 검증 (Limit)

        // 다른 사용자의 노트가 포함되지 않았는지 논리적으로 확인
        // (Querydsl where 절에서 필터링이 잘 되었는지 확인)
    }

    @Test
    @DisplayName("노트가 하나도 없는 경우 빈 페이지를 반환한다")
    void findByMemberId_empty() {
        // given
        Member member = fixtureMonkey.giveMeOne(Member.class);
        memberRepository.save(member);

        Pageable pageable = PageRequest.of(0, 10);

        // when
        Page<NoteDto> result = myNoteGroupRepository.findByMemberId(member.getId(), pageable);

        // then

        assertTrue(result.isEmpty());
        assertEquals(0, result.getTotalElements());
    }
}