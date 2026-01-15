package com.rejs.flashnote.domain.note.service;

import com.navercorp.fixturemonkey.FixtureMonkey;
import com.navercorp.fixturemonkey.api.introspector.*;
import com.rejs.flashnote.TestcontainersConfiguration;
import com.rejs.flashnote.domain.member.entity.Member;
import com.rejs.flashnote.domain.member.repository.MemberRepository;
import com.rejs.flashnote.domain.note.dto.CreateNoteGroupRequest;
import com.rejs.flashnote.domain.note.entity.NoteGroup;
import com.rejs.flashnote.domain.note.entity.NotePermission;
import com.rejs.flashnote.domain.note.repository.NoteGroupRepository;
import com.rejs.flashnote.domain.note.repository.NotePermissionRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.util.Arrays;
import java.util.List;

import static com.navercorp.fixturemonkey.api.expression.JavaGetterMethodPropertySelector.javaGetter;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Import({TestcontainersConfiguration.class})
@ActiveProfiles("test")
@SpringBootTest
public class NoteGroupServiceIntegrationTest {
    @Autowired
    private NoteGroupService noteGroupService;
    @Autowired private MemberRepository memberRepository;
    @Autowired private NoteGroupRepository noteGroupRepository;
    @Autowired private NotePermissionRepository notePermissionRepository;

    private final FixtureMonkey fixtureMonkey = FixtureMonkey.builder()
            .objectIntrospector(
                    new FailoverIntrospector(
                            Arrays.asList(
                                    BuilderArbitraryIntrospector.INSTANCE, // 빌더
                                    ConstructorPropertiesArbitraryIntrospector.INSTANCE, // 생성자
                                    BeanArbitraryIntrospector.INSTANCE, // setter
                                    FieldReflectionArbitraryIntrospector.INSTANCE // 리플렉션
                            ),
                            false // 로그가... 남는다... 왜?
                    )
            )
            .build();

    @Test
    @DisplayName("실제 DB와 연결하여 노트 그룹과 권한이 정상 저장되는지 확인한다")
    void createNoteGroup_Integration() {
        // given: 실제 멤버를 DB에 미리 저장
        Member member = fixtureMonkey.giveMeBuilder(Member.class).setNull(javaGetter(Member::getId)).sample();
        memberRepository.save(member);

        CreateNoteGroupRequest request = fixtureMonkey.giveMeOne(CreateNoteGroupRequest.class);

        // when
        Long groupId = noteGroupService.createNoteGroup(member.getId(), request);

        // then: DB에서 직접 조회하여 검증
        NoteGroup savedGroup = noteGroupRepository.findById(groupId).orElseThrow();
        assertThat(savedGroup.getName()).isEqualTo(request.name());

        // 연관된 권한이 실제로 DB에 인서트 되었는지 확인
        List<NotePermission> permissions = notePermissionRepository.findAll();
        assertEquals(1, permissions.size());
        assertThat(permissions.get(0).getMember().getId()).isEqualTo(member.getId());
        assertThat(permissions.get(0).getNoteGroup().getId()).isEqualTo(groupId);
    }
}
