package com.rejs.flashnote.domain.note.service;

import com.navercorp.fixturemonkey.FixtureMonkey;
import com.navercorp.fixturemonkey.api.introspector.*;
import com.rejs.flashnote.domain.member.entity.Member;
import com.rejs.flashnote.domain.member.repository.MemberRepository;
import com.rejs.flashnote.domain.note.dto.NoteGroupListDto;
import com.rejs.flashnote.domain.note.dto.request.CreateNoteGroupRequest;
import com.rejs.flashnote.domain.note.dto.NoteGroupDto;
import com.rejs.flashnote.domain.note.dto.request.UpdateNoteGroupRequest;
import com.rejs.flashnote.domain.note.entity.NoteGroup;
import com.rejs.flashnote.domain.note.entity.NoteRole;
import com.rejs.flashnote.domain.note.repository.MyNoteGroupRepository;
import com.rejs.flashnote.domain.note.repository.NoteGroupRepository;
import com.rejs.flashnote.domain.note.repository.NotePermissionRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static com.navercorp.fixturemonkey.api.expression.JavaGetterMethodPropertySelector.javaGetter;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class NoteGroupServiceTest {
    @Mock
    private NoteGroupRepository noteGroupRepository;
    @Mock
    private NotePermissionRepository notePermissionRepository;
    @Mock
    private MemberRepository memberRepository;

    @Mock
    private MyNoteGroupRepository myNoteGroupRepository;

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

    @Test
    @DisplayName("노트 그룹 단건 조회 테스트")
    void readById_Test() {
        // Given
        Long noteGroupId = 1L;
        NoteGroup noteGroup = fixtureMonkey.giveMeBuilder(NoteGroup.class)
                .set(javaGetter(NoteGroup::getId), noteGroupId)
                .sample();

        given(noteGroupRepository.findById(noteGroupId)).willReturn(Optional.of(noteGroup));

        // When
        NoteGroupDto result = noteGroupService.readById(noteGroupId);

        // Then
        assertThat(result.getId()).isEqualTo(noteGroupId);
        assertThat(result.getName()).isEqualTo(noteGroup.getName());
    }

    @Test
    @DisplayName("노트 그룹 페이징 조회 테스트")
    void readByPage_Test() {
        // Given
        int size = 10;
        Pageable pageable = PageRequest.of(0, size);
        List<NoteGroup> noteGroups = fixtureMonkey.giveMeBuilder(NoteGroup.class).sampleList(size);
        Page<NoteGroup> page = new PageImpl<>(noteGroups, pageable, noteGroups.size());

        given(noteGroupRepository.findAll(pageable)).willReturn(page);

        // When
        Page<NoteGroupDto> result = noteGroupService.readByPage(pageable);

        // Then
        assertEquals(noteGroups.size(), result.getContent().size());
        assertThat(result.getContent().get(0).getName()).isEqualTo(noteGroups.get(0).getName());
    }

    @Test
    @DisplayName("노트 그룹 이름 수정 테스트")
    void updateName_Test() {
        // Given
        Long noteGroupId = 1L;
        UpdateNoteGroupRequest request = fixtureMonkey.giveMeOne(UpdateNoteGroupRequest.class);
        NoteGroup noteGroup = fixtureMonkey.giveMeBuilder(NoteGroup.class)
                .set(javaGetter(NoteGroup::getId), noteGroupId)
                .set(javaGetter(NoteGroup::getName), "Old Name")
                .sample();

        given(noteGroupRepository.findById(noteGroupId)).willReturn(Optional.of(noteGroup));

        // When
        noteGroupService.updateName(noteGroupId, request);

        // Then
        assertThat(noteGroup.getName()).isEqualTo(request.getName());
    }

    @Test
    @DisplayName("노트 그룹 삭제 시 자식을 먼저 지우고 부모를 지운다")
    void deleteNoteGroup_Test() {
        // Given
        Long noteGroupId = 1L;
        // getReferenceById는 실제 DB를 안 찌르므로 ID만 박힌 프록시 객체 모사
        NoteGroup noteGroupProxy = fixtureMonkey.giveMeBuilder(NoteGroup.class)
                .set(javaGetter(NoteGroup::getId), noteGroupId)
                .sample();

        given(noteGroupRepository.getReferenceById(noteGroupId)).willReturn(noteGroupProxy);

        // When
        noteGroupService.deleteNoteGroup(noteGroupId);

        // Then
        // 1. 순서 검증을 위한 InOrder 생성
        InOrder inOrder = inOrder(notePermissionRepository, noteGroupRepository);

        // 2. 자식(Permission) 벌크 삭제가 먼저 일어났는가?
        inOrder.verify(notePermissionRepository).deleteByNoteGroup(noteGroupProxy);

        // 3. 그 다음 부모(NoteGroup)가 삭제되었는가?
        inOrder.verify(noteGroupRepository).delete(noteGroupProxy);
    }

    @Test
    @DisplayName("사용자 ID를 기반으로 내가 속한 노트 그룹 목록을 페이징 조회한다")
    void readMyNoteGroupsByPage_Test() {
        // Given
        Long memberId = 1L;
        Pageable pageable = PageRequest.of(0, 10);

        // MyNoteGroupRepository가 반환할 DTO 리스트 생성
        List<NoteGroupListDto> dtoList = fixtureMonkey.giveMeBuilder(NoteGroupListDto.class)
                .sampleList(3);
        Page<NoteGroupListDto> expectedPage = new PageImpl<>(dtoList, pageable, dtoList.size());

        // Repository Mocking (Service가 호출하는 대상)
        given(myNoteGroupRepository.findByMyPage(memberId, pageable)).willReturn(expectedPage);

        // When
        Page<NoteGroupListDto> result = noteGroupService.readMyNoteGroupsByPage(memberId, pageable);

        // Then
        assertAll(
                () -> assertThat(result).isNotNull(),
                () -> assertThat(result.getTotalElements()).isEqualTo(3),
                () -> verify(myNoteGroupRepository).findByMyPage(memberId, pageable)
        );
    }
}