package com.rejs.flashnote.domain.cards.authorization;


import org.springframework.security.access.prepost.PreAuthorize;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@PreAuthorize("@cmAuth.authorize('card', 'all', #cardId)")
public @interface PreCardAuthorize {
}
