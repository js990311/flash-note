package com.rejs.flashnote.global.meilisearch;

import org.testcontainers.containers.GenericContainer;

public class MeilisearchContainer extends GenericContainer<MeilisearchContainer> {
    private static final String DEFAULT_IMAGE_NAME = "getmeili/meilisearch:latest";
    private static final int DEFAULT_PORT = 7700;
    private static final String DEFAULT_MASTER_KEY = "master_key";

    public MeilisearchContainer(String dockerImageName) {
        super(dockerImageName);
        withExposedPorts(DEFAULT_PORT);
        withEnv("MEILI_MASTER_KEY", DEFAULT_MASTER_KEY);
        withEnv("MEILI_ENV", "development");
    }

    public MeilisearchContainer() {
        this(DEFAULT_IMAGE_NAME);
    }

    public String getMasterKey() {
        return getEnvMap().get("MEILI_MASTER_KEY");
    }
}
