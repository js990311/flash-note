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
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.utility.DockerImageName;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.services.s3.S3Client;

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
    DynamicPropertyRegistrar dynamicPropertyRegistrar(MeilisearchContainer meilisearchContainer, LocalStackContainer localStackContainer) {
        return registry -> {
            registry.add("meilisearch.host", ()->"http://"+meilisearchContainer.getHost() + ":" + meilisearchContainer.getMappedPort(7700));
            registry.add("meilisearch.api-key", meilisearchContainer::getMasterKey);
            registry.add("aws.s3.access-key", ()->localStackContainer.getAccessKey());
            registry.add("aws.s3.secret-key", ()->localStackContainer.getSecretKey());
            registry.add("aws.s3.bucket", ()->"test-bucket");
        };
    }

    @Bean
    LocalStackContainer localStackContainer() {
        return new LocalStackContainer(DockerImageName.parse("localstack/localstack:latest"))
                .withServices(LocalStackContainer.Service.S3);
    }

    @Bean
    @Primary
    public S3Client s3Client(LocalStackContainer localStack) {
        return S3Client.builder()
                .endpointOverride(localStack.getEndpointOverride(LocalStackContainer.Service.S3))
                .credentialsProvider(
                        StaticCredentialsProvider.create(
                                AwsBasicCredentials.create(localStack.getAccessKey(), localStack.getSecretKey())
                        )
                )
                .region(Region.of(localStack.getRegion()))
                .build();
    }

    @Bean
    public S3AsyncClient s3AsyncClient(LocalStackContainer localStack) {
        return S3AsyncClient.crtBuilder()
                .endpointOverride(localStack.getEndpointOverride(LocalStackContainer.Service.S3))
                .credentialsProvider(
                        StaticCredentialsProvider.create(
                                AwsBasicCredentials.create(localStack.getAccessKey(), localStack.getSecretKey())
                        )
                )
                .region(Region.of(localStack.getRegion()))
                .build();
    }

}
