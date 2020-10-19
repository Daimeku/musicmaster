package com.musicmaster.main.exceptions;


public class SpotifyApiException extends RuntimeException {

    public SpotifyApiException(String message) {
        super(message);
    }

    public SpotifyApiException(String message, Exception ex) {
        super(message, ex);
    }

    public SpotifyApiException(Exception ex) {
        super("an error occurred with the spotify API", ex);
    }
}
