package com.musicmaster.main.helpers;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.HashMap;
import java.util.Map;

@Component
public class UriHelper {

    @Value("${spotify.uri.authorize}")
    private String SPOTIFY_BASEPATH;

    @Value("${spotify.client.id}")
    private String SPOTIFY_CLIENT_ID;

    @Value("${spotify.scope}")
    private String SPOTIFY_SCOPE;

    @Value("${spotify.uri.redirect}")
    private String SPOTIFY_REDIRECT_URI;

    @Value("${tidal.uri.authorize}")
    private String TIDAL_BASEPATH;

    @Value("${tidal.client.id}")
    private String TIDAL_CLIENT_ID;

    @Value("${tidal.scope}")
    private String TIDAL_SCOPE;

    @Value("${tidal.uri.redirect}")
    private String TIDAL_REDIRECT_URI;

    public String buildSpotifyAuthUri(String state) {
        Map<String,String> queryParams = new HashMap<>();
        queryParams.put("response_type", "code");
        queryParams.put("client_id", SPOTIFY_CLIENT_ID);
        queryParams.put("scope", SPOTIFY_SCOPE);
        queryParams.put("redirect_uri",SPOTIFY_REDIRECT_URI);
        queryParams.put("state", state);
        UriComponents uriComponents = UriComponentsBuilder.fromUriString(SPOTIFY_BASEPATH).buildAndExpand(queryParams);
        return uriComponents.toUriString();
    }

    public String buildTidalAuthUri(String state, String codeChallenge) {
        MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
        queryParams.add("response_type", "code");
        queryParams.add("client_id", TIDAL_CLIENT_ID);
        queryParams.add("scope", TIDAL_SCOPE);
        queryParams.add("redirect_uri", TIDAL_REDIRECT_URI);
        queryParams.add("state", state);
        queryParams.add("code_challenge_method", "S256");
        queryParams.add("code_challenge", codeChallenge);
        UriComponents uriComponents = UriComponentsBuilder
                .fromUriString(TIDAL_BASEPATH)
                .queryParams(queryParams)
                .buildAndExpand();
        return uriComponents.toUriString();
    }
}
