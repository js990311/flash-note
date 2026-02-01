package com.rejs.flashnote.global.security.authorization;

public interface CrudMethodAuthorizationStrategy {
    /**
     * 해당하는 도메인에 대해서 인가처리가 가능하다면 자기자신을 반환할 것
     * @param domainType
     * @param methodType
     * @return 자기자신이거나 null
     */
    CrudMethodAuthorizationStrategy getStrategy(String domainType, String methodType);

    /**
     * MemberId는 PrincipalUtils에서 가져다 쓸 것
     * @param domainType
     * @param methodType
     * @param entityId
     */
    void authorize(String domainType, String methodType, Long entityId);
}
