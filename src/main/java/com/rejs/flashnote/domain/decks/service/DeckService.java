package com.rejs.flashnote.domain.decks.service;

import com.rejs.flashnote.domain.decks.dto.DeckDto;
import com.rejs.flashnote.domain.decks.dto.request.CreateDeckRequest;
import com.rejs.flashnote.domain.decks.dto.request.UpdateDeckRequest;
import com.rejs.flashnote.domain.decks.entity.Deck;
import com.rejs.flashnote.domain.cards.repository.CardRepository;
import com.rejs.flashnote.domain.decks.error.DeckException;
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
public class DeckService {
    private final DeckRepository deckRepository;
    private final CardRepository cardRepository;
    private final MemberRepository memberRepository;

    // # Create

    @Transactional
    public Long createDeck(Long memberId, CreateDeckRequest request){
        Member member = memberRepository.getReferenceById(memberId);
        Deck deck = Deck.create(request.getName(), member);
        deck = deckRepository.save(deck);
        return deck.getId();
    }

    // # Read
    @Transactional(readOnly = true)
    public DeckDto readDeckById(Long deckId){
        Deck deck = deckRepository.findById(deckId).orElseThrow(DeckException::notFound);
        return DeckDto.from(deck);
    }

    @Transactional(readOnly = true)
    public Page<DeckDto> readDeckPageByMemberId(Long memberId, Pageable pageable){
        Member member = memberRepository.getReferenceById(memberId);
        return deckRepository.findByMember(member, pageable).map(DeckDto::from);
    }

    // # Update
    @Transactional
    public Long updateDeck(UpdateDeckRequest request){
        Deck deck = deckRepository.findById(request.getId()).orElseThrow(DeckException::notFound);
        deck.update(request.getName());
        return deck.getId();
    }

    // # Delete
    @Transactional
    public void deleteDeck(Long deckId){
        deckRepository.deleteById(deckId);
    }
}
