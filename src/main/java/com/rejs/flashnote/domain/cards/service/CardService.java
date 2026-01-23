package com.rejs.flashnote.domain.cards.service;

import com.rejs.flashnote.domain.cards.dto.CardDto;
import com.rejs.flashnote.domain.cards.dto.request.CreateCardRequest;
import com.rejs.flashnote.domain.cards.dto.request.UpdateCardRequest;
import com.rejs.flashnote.domain.cards.entity.Card;
import com.rejs.flashnote.domain.decks.entity.Deck;
import com.rejs.flashnote.domain.cards.repository.CardRepository;
import com.rejs.flashnote.domain.cards.repository.CardFetchReadRepository;
import com.rejs.flashnote.domain.decks.repository.DeckRepository;
import com.rejs.flashnote.domain.member.entity.Member;
import com.rejs.flashnote.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class CardService {
    private final DeckRepository deckRepository;
    private final CardRepository cardRepository;
    private final MemberRepository memberRepository;
    private final CardFetchReadRepository cardFetchReadRepository;

    // # Create
    @Transactional
    public Long createCard(Long memberId, CreateCardRequest request){
        Deck deck = deckRepository.findById(request.getDeckId()).orElseThrow();
        Member member = memberRepository.getReferenceById(memberId);
        Card card = Card.create(deck, member, request);
        card = cardRepository.save(card);
        return card.getId();
    }

    // # Read
    @Transactional(readOnly = true)
    public CardDto readById(Long cardId){
        Card card = cardRepository.findById(cardId).orElseThrow();
        return CardDto.from(card);
    }

    @Transactional(readOnly = true)
    public Page<CardDto> readPageByDeckId(Long deckId, Pageable pageable){
        return cardFetchReadRepository.findCardsByDeckId(deckId, pageable).map(CardDto::from);
    }

    // # Update
    @Transactional
    public Long updateCard(UpdateCardRequest request){
        Card card = cardRepository.findById(request.getId()).orElseThrow();
        card.update(request.getFront(), request.getBack());
        return card.getDeck().getId();
    }

    // # Delete
    @Transactional
    public Long deleteCard(Long cardId){
        Card card = cardFetchReadRepository.findWithDeckById(cardId);
        card.delete();
        return card.getDeck().getId();
    }
}
