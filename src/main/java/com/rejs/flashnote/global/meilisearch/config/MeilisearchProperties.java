package com.rejs.flashnote.global.meilisearch.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Setter
@Getter
@ConfigurationProperties(prefix = "meilisearch")
public class MeilisearchProperties {
    private String host = "http://localhost:7700";
    private String apiKey;
}
