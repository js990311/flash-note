package com.rejs.flashnote.domain.decks.service.generate;

import com.rejs.flashnote.domain.cards.entity.Card;
import com.rejs.flashnote.domain.cards.repository.CardRepository;
import com.rejs.flashnote.domain.decks.dto.generate.GenerateDeckFromNoteDto;
import com.rejs.flashnote.domain.decks.entity.Deck;
import com.rejs.flashnote.domain.decks.error.DeckException;
import com.rejs.flashnote.domain.decks.repository.DeckRepository;
import com.rejs.flashnote.domain.member.entity.Member;
import com.rejs.flashnote.domain.member.repository.MemberRepository;
import com.rejs.flashnote.domain.note.entity.Note;
import com.rejs.flashnote.domain.note.error.NoteException;
import com.rejs.flashnote.domain.note.repository.NoteRepository;
import com.rejs.flashnote.global.gemini.dto.GeneratedDeckDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GenerateDeckService {
    private final NoteRepository noteRepository;
    private final DeckRepository deckRepository;
    private final MemberRepository memberRepository;
    private final CardRepository cardRepository;

    @Transactional
    public GenerateDeckFromNoteDto generateDeckFromNote(Long noteId, Long memberId){
        Note note = noteRepository.findById(noteId).orElseThrow(NoteException::notFound);
        Member member = memberRepository.findById(memberId).orElseThrow();
        Deck deck = Deck.from(note, member);
        deck = deckRepository.save(deck);
        return GenerateDeckFromNoteDto.from(deck, note);
    }

    @Transactional
    public void generateCard(Long deckId, Long memberId, GeneratedDeckDto generatedDeckDto){
        Deck deck = deckRepository.findById(deckId).orElseThrow(DeckException::notFound);
        Member member = memberRepository.findById(memberId).orElseThrow();
        List<Card> cards = generatedDeckDto.getCards().stream().map(generatedCardDto -> Card.from(deck, member, generatedCardDto)).toList();
        cardRepository.saveAll(cards);
        deck.complete();
    }

    @Transactional
    public void generateFail(Long deckId){
        Deck deck = deckRepository.findById(deckId).orElseThrow(DeckException::notFound);
        deck.fail();
    }
}
