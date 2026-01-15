package com.rejs.flashnote.domain.note.service;

import com.navercorp.fixturemonkey.FixtureMonkey;
import com.navercorp.fixturemonkey.api.introspector.*;
import com.rejs.flashnote.domain.member.entity.Member;
import com.rejs.flashnote.domain.member.repository.MemberRepository;
import com.rejs.flashnote.domain.note.dto.CreateNoteGroupRequest;
import com.rejs.flashnote.domain.note.entity.NoteGroup;
import com.rejs.flashnote.domain.note.entity.NoteRole;
import com.rejs.flashnote.domain.note.repository.NoteGroupRepository;
import com.rejs.flashnote.domain.note.repository.NotePermissionRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Arrays;

import static com.navercorp.fixturemonkey.api.expression.JavaGetterMethodPropertySelector.javaGetter;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class NoteGroupServiceTest {
    @Mock
    private NoteGroupRepository noteGroupRepository;
    @Mock
    private NotePermissionRepository notePermissionRepository;
    @Mock
    private MemberRepository memberRepository;

    @InjectMocks
    private NoteGroupService noteGroupService;

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
    @DisplayName("Fixture Monkey를 사용하여 노트 그룹 생성 로직을 검증한다")
    void createNoteGroup_WithFixtureMonkey() {
        // 1. Given: 가상의 memberId와 Request 생성
        Long memberId = fixtureMonkey.giveMeOne(Long.class);
        CreateNoteGroupRequest request = fixtureMonkey.giveMeOne(CreateNoteGroupRequest.class);

        // 2. Mocking: getReferenceById 호출 시 반환할 Member 프록시(가짜 객체)
        // ID만 memberId로 고정하고 나머지는 랜덤하게 채운 객체 생성
        Member memberProxy = fixtureMonkey.giveMeBuilder(Member.class)
                .set(javaGetter(Member::getId), memberId)
                .sample();

        NoteGroup savedNoteGroup = fixtureMonkey.giveMeBuilder(NoteGroup.class)
                .setNotNull(javaGetter(NoteGroup::getId)) // ID가 생성된 상태를 모사
                .set(javaGetter(NoteGroup::getName), request.name())
                .sample();

        given(memberRepository.getReferenceById(memberId)).willReturn(memberProxy);
        given(noteGroupRepository.save(any(NoteGroup.class))).willReturn(savedNoteGroup);

        // 3. When
        Long resultId = noteGroupService.createNoteGroup(memberId, request);

        // 4. Then
        assertThat(resultId).isEqualTo(savedNoteGroup.getId());

        // NotePermission 저장 시 memberProxy가 제대로 전달되었는지 검증
        verify(notePermissionRepository).save(argThat(permission ->
                permission.getMember().getId().equals(memberId) &&
                        permission.getNoteGroup().getId().equals(savedNoteGroup.getId()))
        );

        verify(notePermissionRepository).save(argThat(permission ->
                permission.getRole() == NoteRole.OWNER &&
                        // 2. 전달된 프록시 객체가 그대로 쓰였는지 확인
                        permission.getMember().getId().equals(memberProxy.getId())
        ));

    }
}