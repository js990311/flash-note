package com.rejs.flashnote.domain.cards.repository;

import com.rejs.flashnote.domain.cards.entity.Deck;
import com.rejs.flashnote.domain.member.entity.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeckRepository extends JpaRepository<Deck, Long> {
    Page<Deck> findByMember(Member member, Pageable pageable);
}
