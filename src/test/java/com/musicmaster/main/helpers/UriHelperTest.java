package com.musicmaster.main.helpers;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

public class UriHelperTest {

    private UriHelper uriHelper;

    @BeforeEach
    public void init() {
        uriHelper = new UriHelper();
        ReflectionTestUtils.setField(uriHelper, "SPOTIFY_BASEPATH", "https://accounts.spotify.com/authorize?response_type=code&client_id={client_id}&scope={scope}&redirect_uri={redirect_uri}&state={state}");
        ReflectionTestUtils.setField(uriHelper, "SPOTIFY_CLIENT_ID", "testClientId");
        ReflectionTestUtils.setField(uriHelper, "SPOTIFY_SCOPE", "user-read-private user-read-email");
        ReflectionTestUtils.setField(uriHelper, "SPOTIFY_REDIRECT_URI", "http://testredirect");
    }

    @Test
    public void buildSpotifyAuthUri_success() {
        String uri = uriHelper.buildSpotifyAuthUri("d7802658");
        String test = "https://accounts.spotify.com/authorize" +
                "?response_type=code&client_id=testClientId" +
                "&scope=user-read-private%20user-read-email" +
                "&redirect_uri=http://testredirect&state=d7802658";
        Assertions.assertEquals(test, uri);
    }

}
