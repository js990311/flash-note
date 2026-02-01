package com.rejs.flashnote.global.security.authorization;

import com.rejs.flashnote.global.security.authorization.exception.NoAuthorizationStrategyException;
import com.rejs.flashnote.global.security.utils.PrincipalUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@RequiredArgsConstructor
@Component("cmAuth")
public class CrudMethodAuthorizationManager {
    private final List<CrudMethodAuthorizationStrategy> strategies;

    /**
     * 순회하면서 지원하는 전략을 찾는다
     * @param domainType 도메인 전략
     * @param methodType 메서드 전략 CRUD 등. 커스텀 가능
     * @return 해당하는 인가 전략 객체
     * @throws NoAuthorizationStrategyException 적절한 전략을 차지 못하는 경우
     */
    public CrudMethodAuthorizationStrategy getStrategy(String domainType, String methodType) {
        for(CrudMethodAuthorizationStrategy strategy : strategies){
            CrudMethodAuthorizationStrategy ret = strategy.getStrategy(domainType, methodType);
            if(ret != null){
                return ret;
            }
        }
        throw new NoAuthorizationStrategyException(domainType, methodType);
    }

    /**
     * 실제 인가 처리
     * @param domainType
     * @param methodType
     * @param entityId
     * @throws NoAuthorizationStrategyException 적절한 전략을 차지 못하는 경우
     * @throws org.springframework.security.access.AccessDeniedException 인가를 받지 못한 경우
     */
    public void authorize(String domainType, String methodType, Long entityId) {
        getStrategy(domainType, methodType).authorize(domainType, methodType, entityId);
    }
}
