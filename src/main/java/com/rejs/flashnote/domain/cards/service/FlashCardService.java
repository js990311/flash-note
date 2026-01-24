package com.rejs.flashnote.domain.cards.service;

import com.rejs.flashnote.domain.cards.dto.CardDto;
import com.rejs.flashnote.domain.cards.repository.CardRepository;
import com.rejs.flashnote.domain.cards.repository.FlashCardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@RequiredArgsConstructor
@Service
public class FlashCardService {
    private final FlashCardRepository flashCardRepository;

    @Transactional(readOnly = true)
    public List<CardDto> getTodayFlashCards(Long memberId, Long deckId, Integer limit){
        return flashCardRepository.findCardsToStudy(memberId, deckId, Instant.now(),limit)
                .stream().map(
                        CardDto::from
                ).toList();
    }
}
