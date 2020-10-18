package com.musicmaster.main.helpers;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.HashMap;
import java.util.Map;

@Component
public class UriHelper {

    @Value("${spotify.uri}")
    private String SPOTIFY_BASEPATH;

    @Value("${spotify.client.id")
    private String SPOTIFY_CLIENT_ID;

    @Value("${spotify.scope}")
    private String SPOTIFY_SCOPE;

    @Value("${spotify.redirect.uri}")
    private String SPOTIFY_REDIRECT_URI;

    public String buildSpotifyAuthUri(String state) {
        String uri = "";
        Map<String,String> queryParams = new HashMap<>();
        queryParams.put("response_type", "code");
        queryParams.put("client_id", SPOTIFY_CLIENT_ID);
        queryParams.put("scope", SPOTIFY_SCOPE);
        queryParams.put("redirect_uri",SPOTIFY_REDIRECT_URI);
        queryParams.put("state", state);
        UriComponents uriComponents = UriComponentsBuilder.fromUriString(SPOTIFY_BASEPATH).buildAndExpand(queryParams);
        return  uriComponents.toUri().toString();
//        UriComponents uriComponents = UriComponentsBuilder.newInstance()
//                .scheme("https").host("accounts.spotify.com").path("/authorize")
//                .query("response_type")
//                .query("client_id")
//                .query("scope")
//                .query("redirect_uri")
//                .query("state")
//                .buildAndExpand()

//        return uri;
    }

}
