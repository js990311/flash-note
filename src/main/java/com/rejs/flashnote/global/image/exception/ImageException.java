package com.rejs.flashnote.global.image.exception;

import com.rejs.flashnote.global.exception.throwable.BusinessException;

public class ImageException extends BusinessException {
    public ImageException(ImageErrorCode errorCode) {
        super(errorCode);
    }

    public ImageException(String message, ImageErrorCode errorCode) {
        super(message, errorCode);
    }

    public ImageException(Throwable cause, ImageErrorCode errorCode) {
        super(cause, errorCode);
    }

    public static ImageException emptyFile() {
        return new ImageException(ImageErrorCode.EMPTY_FILE);
    }

    public static ImageException unsupportedMimeType(String mimeType) {
        return new ImageException("지원하지 않는 MIME 타입입니다: " + mimeType, ImageErrorCode.UNSUPPORTED_MIME_TYPE);
    }

    public static ImageException fileTooLarge(long actualSize, long maxSize) {
        return new ImageException("파일 크기 초과: actual=" + actualSize + " bytes, max=" + maxSize + " bytes", ImageErrorCode.FILE_TOO_LARGE);
    }

    public static ImageException invalidSignature() {
        return new ImageException(ImageErrorCode.INVALID_SIGNATURE);
    }

    public static ImageException tempDirectoryInitFailed(Throwable cause) {
        return new ImageException(cause, ImageErrorCode.TEMP_DIRECTORY_INIT_FAILED);
    }

    public static ImageException localSaveFailed(String filename, Throwable cause) {
        return new ImageException("로컬 파일 저장 실패: " + filename, ImageErrorCode.LOCAL_SAVE_FAILED);
    }

    public static ImageException s3UploadFailed(Throwable cause) {
        return new ImageException(cause, ImageErrorCode.S3_UPLOAD_FAILED);
    }

    public static ImageException fileProcessingFailed(Throwable cause) {
        return new ImageException(cause, ImageErrorCode.FILE_PROCESSING_FAILED);
    }
}
