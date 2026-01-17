package com.rejs.flashnote.domain.note.repository;

import com.navercorp.fixturemonkey.FixtureMonkey;
import com.navercorp.fixturemonkey.api.introspector.*;
import com.rejs.flashnote.TestcontainersConfiguration;
import com.rejs.flashnote.common.test.TestDataBuilderGroup;
import com.rejs.flashnote.domain.member.entity.Member;
import com.rejs.flashnote.domain.member.repository.MemberRepository;
import com.rejs.flashnote.domain.note.dto.NoteGroupListDto;
import com.rejs.flashnote.domain.note.entity.NoteGroup;
import com.rejs.flashnote.domain.note.entity.NotePermission;
import com.rejs.flashnote.domain.note.entity.NoteRole;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import net.jqwik.api.Arbitraries;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.shaded.org.checkerframework.checker.units.qual.A;

import java.util.Arrays;
import java.util.stream.IntStream;

import static com.navercorp.fixturemonkey.api.expression.JavaGetterMethodPropertySelector.javaGetter;
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
    private NoteGroupRepository noteGroupRepository;

    @Autowired
    private NotePermissionRepository notePermissionRepository;

    @Autowired
    private EntityManager entityManager;

    private final FixtureMonkey fixtureMonkey = TestDataBuilderGroup.fixtureMonkey();

    @Test
    @DisplayName("내가 권한을 가진 NoteGroup 리스트만 페이징하여 조회해야 한다")
    void findByMyPage_Success() {
        // given: 1. 테스트 멤버 생성
        Member me = saveMember();
        Member others = saveMember();

        // 2. 내 그룹 15개 생성 (페이징 확인용)
        IntStream.range(0, 15).forEach(i -> {
            NoteGroup group = saveNoteGroup();
            savePermission(me, group);
        });

        // 3. 타인의 그룹 생성 (조회되면 안됨)
        NoteGroup otherGroup = saveNoteGroup();
        savePermission(others, otherGroup);

        entityManager.flush();

        PageRequest pageable = PageRequest.of(0, 10);

        // when
        Page<NoteGroupListDto> result = myNoteGroupRepository.findByMyPage(me.getId(), pageable);

        // then
        assertEquals(15, result.getTotalElements());
        assertEquals(10, result.getContent().size());
    }

    @Test
    @DisplayName("권한이 없는 멤버가 조회할 경우 빈 페이지를 반환해야 한다")
    void findByMyPage_Empty() {
        // given
        Member me = saveMember();
        PageRequest pageable = PageRequest.of(0, 10);

        // when
        Page<NoteGroupListDto> result = myNoteGroupRepository.findByMyPage(me.getId(), pageable);

        // then
        assertTrue(result.isEmpty());
        assertEquals(0, result.getTotalElements());
    }

    private Member saveMember() {
        Member m = fixtureMonkey.giveMeBuilder(Member.class)
                .setNull(javaGetter(Member::getId))
                .sample();
        return memberRepository.save(m);
    }

    private NoteGroup saveNoteGroup() {
        NoteGroup g = fixtureMonkey.giveMeBuilder(NoteGroup.class)
                .setNull(javaGetter(NoteGroup::getId))
                .sample();
        return noteGroupRepository.save(g);
    }

    private void savePermission(Member m, NoteGroup g) {
        NotePermission p = NotePermission.builder()
                .member(m)
                .noteGroup(g)
                .role(NoteRole.OWNER)
                .build();
        notePermissionRepository.save(p);
    }
}