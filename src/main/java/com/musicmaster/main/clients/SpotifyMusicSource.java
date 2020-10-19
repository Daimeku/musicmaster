package com.musicmaster.main.clients;

import com.musicmaster.main.exceptions.SpotifyApiException;
import com.musicmaster.main.models.SpotifyPlaylist;
import com.musicmaster.main.models.UserConfig;
import com.musicmaster.main.pojo.SpotifyProfileDetails;
import com.musicmaster.main.pojo.SpotifyTokenResponse;
import com.musicmaster.main.repositories.UserConfigRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class SpotifyMusicSource {

    private static Logger logger = LoggerFactory.getLogger(SpotifyMusicSource.class);

    @Value("${spotify.uri.api}")
    private String API_BASEPATH;

    @Value("${spotify.uri.auth}")
    private String AUTH_BASEPATH;

    @Value("${spotify.client.id}")
    private String CLIENT_ID;

    @Value("${spotify.client.secret}")
    private String CLIENT_SECRET;

    @Value("${spotify.redirect.uri}")
    private String REDIRECT_URI;

    private RestTemplate restTemplate;
    private RestTemplateBuilder restTemplateBuilder;

    @Autowired
    private UserConfigRepository userConfigRepository;

    public SpotifyMusicSource(RestTemplateBuilder restTemplateBuilder, @Value("${spotify.client.id}") String clientId, @Value("${spotify.client.secret}") String clientSecret, UserConfigRepository userConfigRepository) {
        this.restTemplate = restTemplateBuilder.basicAuthentication(clientId,clientSecret).build();
        this.restTemplateBuilder = restTemplateBuilder;
        this.userConfigRepository = userConfigRepository;
    }

    public SpotifyTokenResponse getToken(String authCode) {
        logger.info("requesting spotify token");

        MultiValueMap<String, String> spotifyTokenRequest = new LinkedMultiValueMap<>();
        spotifyTokenRequest.add("code", authCode);
        spotifyTokenRequest.add("redirect_uri", REDIRECT_URI);
        spotifyTokenRequest.add("grant_type", "authorization_code");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(spotifyTokenRequest, headers);
        SpotifyTokenResponse response;

        try {
            response = restTemplate.postForObject(AUTH_BASEPATH + "/token", request, SpotifyTokenResponse.class);
        } catch (HttpClientErrorException ex) {
            logger.error("spotify error", ex);
            throw new SpotifyApiException(ex);
        }
        logger.info("spotify token request successful");
        this.updateRequestToken(response.getAccessToken());

        return response;
    }

    public SpotifyTokenResponse getRefreshToken() {
        UserConfig config = userConfigRepository.getOne(1);
        String refreshToken = config.getSpotifyRefreshToken();

        logger.info("sending refresh token request");
        MultiValueMap<String, String> refreshTokenRequest = new LinkedMultiValueMap<>();
        refreshTokenRequest.add("grant_type", "refresh_token");
        refreshTokenRequest.add("refresh_token", refreshToken);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(refreshTokenRequest, headers);

        SpotifyTokenResponse response;
        try {
             response = restTemplate.postForObject( AUTH_BASEPATH + "/token", request, SpotifyTokenResponse.class);
        } catch (HttpClientErrorException ex) {
            throw new SpotifyApiException("Error retrieving refresh token", ex);
        }
        logger.info("successfully refreshed token");

        this.updateRequestToken(response.getAccessToken());

        return response;
    }

    public SpotifyProfileDetails getProfileDetails() {
        SpotifyProfileDetails profileDetails;
        //to get profile details I need to ensure the token is valid
        //check token expiration time, if expired request a new one
        if(tokenExpired())
            getRefreshToken();

        try {
            profileDetails = restTemplate.getForObject(API_BASEPATH + "/me", SpotifyProfileDetails.class);
        } catch(HttpClientErrorException ex) {
            logger.error("failed to get spotify profile: ", ex);
            throw new SpotifyApiException("error getting user profiles");
        }
        return profileDetails;
    }

    public List<SpotifyPlaylist> getPlaylists() {

        return null;
    }
    private boolean tokenExpired() {
        UserConfig config = userConfigRepository.getOne(1);

        if(config.getSpotifyTokenExpiration().isBefore(LocalDateTime.now()))
            return true;

        return false;
    }

    private void updateRequestToken(String token) {
//        UserConfig config = userConfigRepository.getOne(1);

        restTemplate.getInterceptors().add(new ClientHttpRequestInterceptor() {
            @Override
            public ClientHttpResponse intercept(HttpRequest httpRequest, byte[] bytes, ClientHttpRequestExecution clientHttpRequestExecution) throws IOException {
                HttpHeaders headers = httpRequest.getHeaders();
                headers.add("Authorization",  "Bearer " + token);
                return clientHttpRequestExecution.execute(httpRequest, bytes);
            }
        });
    }
}
