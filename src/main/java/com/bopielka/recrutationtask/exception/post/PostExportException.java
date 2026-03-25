package com.bopielka.recrutationtask.exception.post;

public class PostExportException extends RuntimeException {

    public PostExportException(String message) {
        super(message);
    }

    public PostExportException(String message, Throwable cause) {
        super(message, cause);
    }
}
