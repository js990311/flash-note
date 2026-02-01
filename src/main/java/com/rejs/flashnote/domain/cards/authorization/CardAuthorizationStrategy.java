package com.rejs.flashnote.domain.cards.authorization;

import com.rejs.flashnote.domain.cards.repository.CardRepository;
import com.rejs.flashnote.domain.decks.repository.DeckRepository;
import com.rejs.flashnote.global.security.authorization.CrudMethodAuthorizationStrategy;
import com.rejs.flashnote.global.security.utils.PrincipalUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class CardAuthorizationStrategy implements CrudMethodAuthorizationStrategy {
    public static final String DOMAIN_TYPE = "card";
    private final CardRepository cardRepository;

    @Override
    public CrudMethodAuthorizationStrategy getStrategy(String domainType, String methodType) {
        return domainType.equals(DOMAIN_TYPE) ? this: null;
    }

    @Override
    public void authorize(String domainType, String methodType, Long entityId) {
        Long memberId = PrincipalUtils.getMemberId();
        if(!cardRepository.existsByMemberIdAndId(memberId, entityId)){
            throw new AccessDeniedException(String.format("%s-%s-%d에 대한 접근권한 없음", domainType, methodType, entityId));
        }

    }
}
