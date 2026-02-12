package com.rejs.flashnote.global.meilisearch.exception;

public class MeilisearchSyncException extends MeilisearchException{
    public MeilisearchSyncException() {
    }

    public MeilisearchSyncException(String message) {
        super(message);
    }

    public MeilisearchSyncException(String message, Throwable cause) {
        super(message, cause);
    }

    public MeilisearchSyncException(Throwable cause) {
        super(cause);
    }

    public MeilisearchSyncException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
