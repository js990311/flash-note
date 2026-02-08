package com.rejs.flashnote.domain.note.authorization;

import com.rejs.flashnote.domain.note.repository.NoteAuthorizeRepository;
import com.rejs.flashnote.domain.note.repository.NoteRepository;
import com.rejs.flashnote.global.security.authorization.CrudMethodAuthorizationStrategy;
import com.rejs.flashnote.global.security.utils.PrincipalUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class NoteAuthorizationStrategy implements CrudMethodAuthorizationStrategy {
    public static final String DOMAIN_TYPE = "note";
    private final NoteAuthorizeRepository noteAuthorizeRepository;

    @Override
    public CrudMethodAuthorizationStrategy getStrategy(String domainType, String methodType) {
        return domainType.equals(DOMAIN_TYPE) ? this: null;
    }

    @Transactional(readOnly = true)
    @Override
    public void authorize(String domainType, String methodType, Long entityId) {
        Long memberId = PrincipalUtils.getMemberId();
        if(!noteAuthorizeRepository.authorize(memberId, entityId, methodType.equals("write"))){
            throw new AccessDeniedException(String.format("%s-%s-%d에 대한 접근권한 없음", domainType, methodType, entityId));
        }
    }
}
