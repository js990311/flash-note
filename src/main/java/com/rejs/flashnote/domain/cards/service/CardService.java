package com.rejs.flashnote.domain.cards.service;

import com.rejs.flashnote.domain.cards.repository.CardRepository;
import com.rejs.flashnote.domain.cards.repository.DeckRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CardService {
    private final DeckRepository deckRepository;
    private final CardRepository cardRepository;
}
