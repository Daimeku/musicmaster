package com.musicmaster.main.controllers;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AuthControllerTest {

    @LocalServerPort
    private String port;

    @Autowired
    private TestRestTemplate restTemplate;

    private String localUri;

    @BeforeEach
    public void init() {
        localUri = "http://localhost:" + port ;
    }

    @Test
    public void loginSpotify() {
        ResponseEntity<String> response = this.restTemplate.getForEntity( localUri + "/auth/login/spotify", String.class);
        assertEquals(HttpStatus.FOUND, response.getStatusCode());
        Assertions.assertThat(response.getHeaders().get("location").get(0)).contains("spotify");
    }

//    @Test
//    public void callbackSpotify() {
//        String callbackUri = String.format(localUri + "/auth/login/spotifyCallback?code=%s", testAuthCode);
//        ResponseEntity<String> response = this.restTemplate.getForEntity(localUri , String.class);
//
//    }
}
