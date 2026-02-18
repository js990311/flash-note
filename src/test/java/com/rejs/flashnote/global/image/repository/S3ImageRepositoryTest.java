package com.rejs.flashnote.global.image.repository;

import com.rejs.flashnote.TestcontainersConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.InputStreamResource;
import org.springframework.test.context.ActiveProfiles;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@SpringBootTest
@Import(TestcontainersConfiguration.class)
class S3ImageRepositoryTest {
    @Autowired
    private S3ImageRepository s3ImageRepository;

    @Autowired
    private S3Client s3Client;

    private final String BUCKET = "test-bucket";

    @BeforeEach
    void setup() {
        s3Client.createBucket(b -> b.bucket(BUCKET));
    }

    @Test
    @DisplayName("비동기 이미지 업로드 테스트")
    void putImageAsyncTest() throws Exception {
        // Given
        String key = "test/image.png";
        String content = "fake-image-content";
        InputStream inputStream = new ByteArrayInputStream(content.getBytes());
        long size = content.length();

        // When
        CompletableFuture<PutObjectResponse> future =
                s3ImageRepository.putImageAsync(BUCKET, key, inputStream, size, "image/png");

        // Then
        PutObjectResponse response = future.join(); // 비동기 완료 대기
        assertNotNull(response);

        // 실제 S3(LocalStack)에 파일이 있는지 검증
        ResponseInputStream<GetObjectResponse> s3Object =
                s3Client.getObject(b -> b.bucket(BUCKET).key(key));

        assertEquals(content, new String(s3Object.readAllBytes()));
    }

    @Test
    @DisplayName("이미지 리소스 조회 테스트 (동기)")
    void getImageAsResourceTest() throws Exception {
        // Given: 테스트용 데이터 업로드
        String key = "test/get-image.png";
        String content = "hello-s3-resource";
        byte[] contentBytes = content.getBytes();
        s3Client.putObject(b -> b.bucket(BUCKET).key(key),
                software.amazon.awssdk.core.sync.RequestBody.fromBytes(contentBytes));

        // When
        InputStreamResource resource = s3ImageRepository.getImageAsResource(BUCKET, key);

        // Then
        assertNotNull(resource);
        try (InputStream is = resource.getInputStream()) {
            byte[] downloadedBytes = is.readAllBytes();
            assertEquals(content, new String(downloadedBytes));
        }
    }

    @Test
    @DisplayName("존재하지 않는 키 조회 시 예외 발생 테스트")
    void getImageNotFoundTest() {
        // Given
        String invalidKey = "none-existent.png";

        // When & Then
        assertThrows(Exception.class, () -> {
            s3ImageRepository.getImageAsResource(BUCKET, invalidKey);
        });
    }
}