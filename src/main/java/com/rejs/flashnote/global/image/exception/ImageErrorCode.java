package com.rejs.flashnote.global.image.exception;

import com.rejs.flashnote.global.exception.code.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ImageErrorCode implements ErrorCode {
    EMPTY_FILE("/error/images/empty-file", "IMAGE_EMPTY_FILE", HttpStatus.BAD_REQUEST, "이미지 파일이 비어있습니다."),
    UNSUPPORTED_MIME_TYPE("/error/images/unsupported-mime", "IMAGE_UNSUPPORTED_MIME_TYPE", HttpStatus.BAD_REQUEST, "허용되지 않은 이미지 형식입니다. image/png, image/jpeg, image/webp만 가능합니다."),
    FILE_TOO_LARGE("/error/images/file-too-large", "IMAGE_FILE_TOO_LARGE", HttpStatus.PAYLOAD_TOO_LARGE, "이미지 파일 크기가 최대 허용 용량을 초과했습니다."),
    INVALID_SIGNATURE("/error/images/invalid-signature", "IMAGE_INVALID_SIGNATURE", HttpStatus.BAD_REQUEST, "파일 내용이 이미지 형식과 일치하지 않습니다."),
    TEMP_DIRECTORY_INIT_FAILED("/error/images/temp-dir-init-failed", "IMAGE_TEMP_DIRECTORY_INIT_FAILED", HttpStatus.INTERNAL_SERVER_ERROR, "임시 이미지 저장소 초기화에 실패했습니다."),
    LOCAL_SAVE_FAILED("/error/images/local-save-failed", "IMAGE_LOCAL_SAVE_FAILED", HttpStatus.INTERNAL_SERVER_ERROR, "임시 이미지 저장에 실패했습니다."),
    S3_UPLOAD_FAILED("/error/images/s3-upload-failed", "IMAGE_S3_UPLOAD_FAILED", HttpStatus.INTERNAL_SERVER_ERROR, "이미지 업로드에 실패했습니다."),
    FILE_PROCESSING_FAILED("/error/images/file-processing-failed", "IMAGE_FILE_PROCESSING_FAILED", HttpStatus.INTERNAL_SERVER_ERROR, "이미지 파일 처리 중 오류가 발생했습니다.");

    private final String type;
    private final String title;
    private final HttpStatus status;
    private final String detail;
}
