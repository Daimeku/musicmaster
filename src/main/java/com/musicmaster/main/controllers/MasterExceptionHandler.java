package com.musicmaster.main.controllers;

import com.musicmaster.main.exceptions.SpotifyApiException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class MasterExceptionHandler extends ResponseEntityExceptionHandler {

    private static Logger logger = LoggerFactory.getLogger(ResponseEntityExceptionHandler.class);

    @ExceptionHandler(SpotifyApiException.class)
    @ResponseBody
    public ResponseEntity<String> handleSpotifyApiException(SpotifyApiException ex) {
        logger.error(ex.getMessage(), ex);
        return new ResponseEntity<String>( ex.getMessage(), HttpStatus.BAD_REQUEST);
    }
}
