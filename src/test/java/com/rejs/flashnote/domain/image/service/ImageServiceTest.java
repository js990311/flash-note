package com.rejs.flashnote.domain.image.service;

import com.navercorp.fixturemonkey.FixtureMonkey;
import com.rejs.flashnote.TestcontainersConfiguration;
import com.rejs.flashnote.common.test.TestDataBuilderGroup;
import com.rejs.flashnote.domain.image.entity.ImageMetadata;
import com.rejs.flashnote.domain.image.repository.ImageMetadataRepository;
import com.rejs.flashnote.domain.member.entity.Member;
import com.rejs.flashnote.domain.member.repository.MemberRepository;
import com.rejs.flashnote.global.image.S3Properties;
import com.rejs.flashnote.global.image.repository.LocalFileRepository;
import io.hypersistence.tsid.TSID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.Resource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.CompletableFuture;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ActiveProfiles("test")
@SpringBootTest
@Import(TestcontainersConfiguration.class)
class ImageServiceIntegrationTest {

    @Autowired private ImageService imageService;
    @Autowired private ImageMetadataRepository imageMetadataRepository;
    @Autowired private S3Client s3Client;
    @Autowired private S3Properties s3Properties;
    @Autowired private LocalFileRepository localFileRepository;
    @Autowired private MemberRepository memberRepository;
    private FixtureMonkey fixtureMonkey = TestDataBuilderGroup.fixtureMonkey();

    private final String BUCKET = "test-bucket";
    private Long memberId;

    @BeforeEach
    void setup() {
        // 테스트용 버킷 생성 (이미 있으면 무시)
        try {
            s3Client.createBucket(b -> b.bucket(BUCKET));
        } catch (Exception ignored) {}

        memberId = memberRepository.save(
                fixtureMonkey.giveMeOne(Member.class)
        ).getId();
    }

    @Test
    @DisplayName("이미지 업로드 전체 프로세스 통합 테스트 (Local -> S3 -> DB)")
    void fullUploadProcessTest() throws Exception {
        // 1. Given: 테스트 파일 및 회원 정보 준비
        String originalFileName = "integration-test.png";
        String contentType = "image/png";
        byte[] content = "real-file-content-binary".getBytes();

        MockMultipartFile file = new MockMultipartFile(
                "file", originalFileName, contentType, content
        );

        // 2. When: 서비스 호출
        CompletableFuture<Long> future = imageService.uploadImage(file, memberId);
        Long savedId = future.join(); // 비동기 작업 완료 대기

        // 3. Then: 결과 검증

        // A. DB 메타데이터 검증
        ImageMetadata metadata = imageMetadataRepository.findById(savedId)
                .orElseThrow(() -> new AssertionError("DB에 메타데이터가 저장되지 않았습니다."));

        assertThat(metadata.getFileName()).isEqualTo(originalFileName);
        assertTrue(metadata.isUploaded(), "최종상태가 TRUE");
        assertThat(metadata.getMemberId()).isEqualTo(memberId);

        // B. S3 실물 파일 존재 여부 검증
        assertDoesNotThrow(() ->
                        s3Client.headObject(HeadObjectRequest.builder()
                                .bucket(BUCKET)
                                .key(metadata.getS3Key())
                                .build())
                , "S3에 파일이 실제로 존재해야 합니다.");

        // C. 로컬 임시 파일 삭제 여부 검증
        // 파일 이름이 {tsid}.png 형태이므로 s3Key에서 추출
        String tempFileName = metadata.getS3Key().replace("images/", "");
        Path tempFilePath = Paths.get(s3Properties.getTempDir()).resolve(tempFileName);

        assertThat(Files.exists(tempFilePath))
                .as("업로드 완료 후 로컬 임시 파일은 삭제되어야 합니다.")
                .isFalse();
    }

    @Test
    @DisplayName("S3 실물 데이터를 Resource로 내려받는지 테스트")
    void getImageResource_Integration_Test() throws Exception {
        // 1. Given: S3에 직접 파일 업로드 및 DB 메타데이터 생성
        Long tsid = TSID.fast().toLong();
        String s3Key = "images/" + tsid + ".png";
        String content = "binary-image-data-123";

        // S3에 데이터 직접 삽입
        s3Client.putObject(b -> b.bucket(BUCKET).key(s3Key),
                software.amazon.awssdk.core.sync.RequestBody.fromString(content));

        // DB에 데이터 삽입 (imageMetadataService.create 등을 활용하거나 Repository 직접 사용)
        imageMetadataRepository.save(ImageMetadata.builder()
                .id(tsid)
                .s3Key(s3Key)
                .fileName("test.png")
                .contentType("image/png")
                .fileSize((long) content.length())
                .memberId(memberId)
                .isUploaded(true)
                .build());

        // 2. When: 서비스 호출
        Resource resource = imageService.getImageResource(tsid);

        // 3. Then: 데이터 일치 여부 확인
        assertThat(resource).isNotNull();
        try (InputStream is = resource.getInputStream()) {
            String result = new String(is.readAllBytes());
            assertThat(result).isEqualTo(content);
        }
    }
}