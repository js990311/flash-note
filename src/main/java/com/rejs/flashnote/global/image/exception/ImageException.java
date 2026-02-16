package com.rejs.flashnote.global.image.exception;

public class ImageException extends RuntimeException{
    public ImageException(String message, Throwable cause) {
        super(message, cause);
    }

    public static ImageException putException(Throwable e){
        return new ImageException("image upload 과정에서 문제 발생", e);
    }
}
