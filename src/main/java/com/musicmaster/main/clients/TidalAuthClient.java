package com.musicmaster.main.clients;

import com.musicmaster.main.controllers.AuthController;
import com.musicmaster.main.exceptions.TidalApiException;
import com.musicmaster.main.pojo.TidalTokenResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Service
public class TidalAuthClient {
    private static Logger logger = LoggerFactory.getLogger(TidalAuthClient.class);

    @Value("${tidal.uri.auth}")
    private String TIDAL_AUTH_BASEPATH;

    @Value("${tidal.client.id}")
    private String CLIENT_ID;

    @Value("${tidal.uri.redirect}")
    private String REDIRECT_URI;

    private final RestTemplate restTemplate;

    public TidalAuthClient(
            RestTemplateBuilder restTemplateBuilder,
            @Value("${tidal.client.id}") String clientId,
            @Value("${tidal.client.secret}") String clientSecret
    ) {
        this.restTemplate = restTemplateBuilder.basicAuthentication(clientId, clientSecret).build();
    }

    public TidalTokenResponse getRefreshToken(String refreshToken) {
        MultiValueMap<String, String> requestParams = new LinkedMultiValueMap<>();
        requestParams.add("grant_type", "refresh_token");
        requestParams.add("refresh_token", refreshToken);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<MultiValueMap<String, String >> request = new HttpEntity<>(requestParams, headers);
        TidalTokenResponse response;

        try {
            response = restTemplate.postForObject(TIDAL_AUTH_BASEPATH, request, TidalTokenResponse.class);
            if (response == null)
                throw new TidalApiException("Null response from tidal API");
        } catch (HttpClientErrorException ex) {
            throw new TidalApiException("Tidal API error getting refresh token", ex);
        }
        return response;
    }

    public TidalTokenResponse getToken(String authCode, String codeVerifier) {
        MultiValueMap<String, String> requestParams = new LinkedMultiValueMap<>();
        requestParams.add("code", authCode);
        requestParams.add("grant_type", "authorization_code");
        requestParams.add("client_id", CLIENT_ID);
        requestParams.add("redirect_uri", REDIRECT_URI);
        requestParams.add("code_verifier", codeVerifier);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(requestParams, headers);
        TidalTokenResponse response;
        try {
            response = restTemplate.postForObject(TIDAL_AUTH_BASEPATH, request, TidalTokenResponse.class);
            if (response == null)
                throw new TidalApiException("Failed to retrieve token response");
        } catch (HttpClientErrorException ex) {
            logger.error("Failed response from tidal auth: " + ex.getResponseBodyAsString());
            throw new TidalApiException("Client error when requesting tidal auth token", ex);
        }
        return response;
    }
}
