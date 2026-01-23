package com.rejs.flashnote.domain.cards.repository;

import com.rejs.flashnote.domain.cards.entity.Card;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CardRepository extends JpaRepository<Card, Long> {
}
