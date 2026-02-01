package com.rejs.flashnote.global.security.authorization;

public interface CrudMethodAuthorizationStrategy {
    CrudMethodAuthorizationStrategy getStrategy(String domainType, String methodType);

    /**
     * MemberId는 PrincipalUtils에서 가져다 쓸 것
     * @param domainType
     * @param methodType
     * @param entityId
     */
    void authorize(String domainType, String methodType, Long entityId);
}
