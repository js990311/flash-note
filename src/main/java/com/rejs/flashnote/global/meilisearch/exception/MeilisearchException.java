package com.rejs.flashnote.global.meilisearch.exception;

public class MeilisearchException extends RuntimeException{
    public MeilisearchException() {
    }

    public MeilisearchException(String message) {
        super(message);
    }

    public MeilisearchException(String message, Throwable cause) {
        super(message, cause);
    }

    public MeilisearchException(Throwable cause) {
        super(cause);
    }

    public MeilisearchException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
