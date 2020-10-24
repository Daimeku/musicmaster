package com.musicmaster.main.exceptions;

public class TidalApiException extends RuntimeException {

    public TidalApiException() {
        super();
    }

    public TidalApiException(String error) {
        super(error);
    }

    public TidalApiException(String errorMessage, Exception ex) {
        super(errorMessage, ex);
    }
}
