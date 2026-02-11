package com.rejs.flashnote;

import com.google.genai.Client;
import com.rejs.flashnote.global.meilisearch.MeilisearchContainer;
import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.test.context.DynamicPropertyRegistrar;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.MariaDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.utility.DockerImageName;

@TestConfiguration(proxyBeanMethods = false)
public class TestcontainersConfiguration {
    @Bean
    @ServiceConnection
    MariaDBContainer<?> mariaDbContainer() {
        return new MariaDBContainer<>(DockerImageName.parse("mariadb:latest"));
    }

    @Bean
    MeilisearchContainer meilisearchContainer() {
        return new MeilisearchContainer();
    }

    @Bean
    @Primary
    DynamicPropertyRegistrar dynamicPropertyRegistrar(MeilisearchContainer meilisearchContainer) {
        return registry -> {
            registry.add("meilisearch.host", ()->"http://"+meilisearchContainer.getHost() + ":" + meilisearchContainer.getMappedPort(7700));
            registry.add("meilisearch.api-key", meilisearchContainer::getMasterKey);
        };
    }
}
