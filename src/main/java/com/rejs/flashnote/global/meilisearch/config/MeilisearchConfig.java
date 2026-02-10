package com.rejs.flashnote.global.meilisearch.config;

import com.meilisearch.sdk.Client;
import com.meilisearch.sdk.Config;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(MeilisearchProperties.class)
public class MeilisearchConfig {
    @Bean
    public Client meilisearchClient(MeilisearchProperties properties) {
        Config config = new Config(
                properties.getHost(),
                properties.getApiKey()
        );
        return new Client(config);
    }

}
