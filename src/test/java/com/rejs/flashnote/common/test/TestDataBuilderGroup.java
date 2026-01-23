package com.rejs.flashnote.common.test;

import com.navercorp.fixturemonkey.FixtureMonkey;
import com.navercorp.fixturemonkey.api.introspector.BuilderArbitraryIntrospector;
import com.navercorp.fixturemonkey.buildergroup.ArbitraryBuilderGroup;
import com.navercorp.fixturemonkey.jakarta.validation.plugin.JakartaValidationPlugin;
import com.navercorp.fixturemonkey.resolver.ArbitraryBuilderCandidateFactory;
import com.navercorp.fixturemonkey.resolver.ArbitraryBuilderCandidateList;
import com.rejs.flashnote.domain.cards.entity.Deck;
import com.rejs.flashnote.domain.member.entity.Member;
import com.rejs.flashnote.domain.member.entity.MemberRole;
import com.rejs.flashnote.domain.note.entity.Note;
import net.jqwik.api.Arbitraries;

import static com.navercorp.fixturemonkey.api.expression.JavaGetterMethodPropertySelector.javaGetter;

public class TestDataBuilderGroup implements ArbitraryBuilderGroup {

    @Override
    public ArbitraryBuilderCandidateList generateCandidateList() {
        return ArbitraryBuilderCandidateList.create()
                .add(
                        ArbitraryBuilderCandidateFactory.of(Member.class)
                                .builder(
                                        builder->builder
                                                .setNull(javaGetter(Member::getId))
                                                .set(javaGetter(Member::getName), Arbitraries.strings().ofMaxLength(50))
                                                .set(javaGetter(Member::getEmail), Arbitraries.strings().alpha().ofMaxLength(50))
                                                .set(javaGetter(Member::getRole), MemberRole.ROLE_USER)
                                                .set(javaGetter(Member::getProvider), "google")
                                                .setNull(javaGetter(Member::getDeletedAt))
                                )
                )
                .add(
                        ArbitraryBuilderCandidateFactory.of(Note.class)
                                .builder(
                                        builder->builder
                                                .setNull(javaGetter(Note::getId))
                                                .set(javaGetter(Note::getTitle), Arbitraries.strings().ofMaxLength(50))
                                                .setNull(javaGetter(Note::getDeletedAt))
                                )
                )
                .add(
                        ArbitraryBuilderCandidateFactory.of(Deck.class)
                                .builder(
                                        builder->builder
                                                .setNull(javaGetter(Deck::getId))
                                                .set(javaGetter(Deck::getName), Arbitraries.strings().ofMaxLength(50))
                                                .setNull(javaGetter(Deck::getDeletedAt))
                                )
                )
                ;
    }

    public static FixtureMonkey fixtureMonkey(){
        return FixtureMonkey.builder()
                .objectIntrospector(BuilderArbitraryIntrospector.INSTANCE)
                .plugin(new JakartaValidationPlugin())
                .registerGroup(new TestDataBuilderGroup())
                .build();
    }
}
