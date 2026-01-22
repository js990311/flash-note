package com.rejs.flashnote.domain.note.repository;

import com.navercorp.fixturemonkey.FixtureMonkey;
import com.rejs.flashnote.TestcontainersConfiguration;
import com.rejs.flashnote.common.test.TestDataBuilderGroup;
import com.rejs.flashnote.domain.member.repository.MemberRepository;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

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

}