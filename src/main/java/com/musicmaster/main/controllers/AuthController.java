package com.musicmaster.main.controllers;

import com.musicmaster.main.helpers.UriHelper;
import com.musicmaster.main.services.AuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping(path = "/auth")
public class AuthController {

    private static Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    UriHelper uriHelper;

    @Autowired
    AuthService authService;

    //update the spotify auth details for a given user
    @GetMapping(path = "/login/spotify")
    public void loginSpotify(HttpServletResponse response) throws Exception {
        String uri = authService.getSpotifyRedirectUri();
        response.sendRedirect(uri);
    }

    //callback after spotify successful login, request tokens here
    @GetMapping(path = "/login/spotifyCallback")
    @ResponseStatus(HttpStatus.OK)
    public void callbackSpotify(@RequestParam String code, @RequestParam String state, HttpServletRequest request, HttpServletResponse response) {
        logger.info("callback from successful spotify login. state: ", state);
        //@Todo - store and validate state
        authService.loadSpotifyAuthToken(code);
    }

    @GetMapping(path = "/login/tidal")
    public void loginTidal(HttpServletResponse response) throws Exception {
        String uri = authService.getTidalRedirectUri();
        response.sendRedirect(uri);
    }

    @GetMapping(path = "/login/tidalCallback")
    @ResponseStatus(HttpStatus.OK)
    public void callbackTidal(@RequestParam String code, @RequestParam String state) {

    }

}
