package com.rejs.flashnote.global.image.repository;

import com.rejs.flashnote.global.image.exception.ImageException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.async.AsyncRequestBody;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.transfer.s3.S3TransferManager;

import java.io.InputStream;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
@RequiredArgsConstructor
public class S3ImageRepository {
    private final S3Client s3Client;
    private final S3AsyncClient s3AsyncClient;
    @Qualifier("s3ExecutorService")
    private final ExecutorService executorService;

    public void putImage(String bucket, String key, InputStream file, Long size, String contentType) {
        try {
            RequestBody requestBody = RequestBody.fromInputStream(
                    file,
                    size
            );
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucket)
                    .key(key)
                    .contentType(contentType)
                    .build();

            s3Client.putObject(putObjectRequest, requestBody);
        } catch (S3Exception e) {
            throw ImageException.putException(e);
        }
    }

    public CompletableFuture<PutObjectResponse> putImageAsync(String bucket, String key, InputStream inputStream, long size, String contentType) {
        AsyncRequestBody asyncRequestBody = AsyncRequestBody.fromInputStream(
                inputStream,
                size,
                executorService
        );

        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .contentType(contentType)
                .build();

        return s3AsyncClient.putObject(putObjectRequest, asyncRequestBody)
                .handle((response, throwable) -> {
                    if (throwable != null) {
                        Throwable cause = throwable.getCause() != null ? throwable.getCause() : throwable;
                        throw ImageException.putException(cause);
                    }
                    return response;
                });
    }

    public InputStreamResource getImageAsResource(String bucket, String key) {
        try {
            ResponseInputStream<GetObjectResponse> s3InputStream = s3Client.getObject(
                    GetObjectRequest.builder()
                            .bucket(bucket)
                            .key(key)
                            .build()
            );
            return new InputStreamResource(s3InputStream);
        } catch (S3Exception e) {
            throw new ImageException("이미지 가져오기 실패", e);
        }
    }
}
