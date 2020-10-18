package com.musicmaster.main.controllers;

import com.musicmaster.main.helpers.UriHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping(path = "/auth")
public class AuthController {

    @Autowired
    UriHelper uriHelper;

    @GetMapping(path = "/login/spotify")
    public String loginSpotify(HttpServletResponse response) {
        //redirect to spotify
        String uri = uriHelper.buildSpotifyAuthUri("129292");
        return uri;

    }
}
