package com.rejs.flashnote.domain.cards.service;

import com.rejs.flashnote.domain.cards.dto.CardDto;
import com.rejs.flashnote.domain.cards.entity.Card;
import com.rejs.flashnote.domain.cards.repository.CardRepository;
import com.rejs.flashnote.domain.cards.repository.FlashCardRepository;
import io.github.openspacedrepetition.Rating;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@RequiredArgsConstructor
@Service
public class FlashCardService {
    private final FlashCardRepository flashCardRepository;
    private final CardRepository cardRepository;

    @Transactional(readOnly = true)
    public List<CardDto> getTodayFlashCards(Long memberId, Long deckId, Integer limit){
        return flashCardRepository.findCardsToStudy(memberId, deckId, Instant.now(),limit)
                .stream().map(
                        CardDto::from
                ).toList();
    }

    @Transactional
    public void studyCard(Long cardId, int ratingValue){
        Rating rating = switch (ratingValue){
            case 1 -> Rating.AGAIN;
            case 2 -> Rating.HARD;
            case 3 -> Rating.AGAIN;
            case 4 -> Rating.HARD;
            default -> throw new IllegalArgumentException();
        };
        Card card = cardRepository.findById(cardId).orElseThrow();
        card.study(rating);
    }
}
