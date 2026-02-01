package com.rejs.flashnote.domain.cards.repository;

import com.rejs.flashnote.domain.cards.entity.Card;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface CardRepository extends JpaRepository<Card, Long> {
    boolean existsByMemberIdAndId(Long memberId, Long id);
}
