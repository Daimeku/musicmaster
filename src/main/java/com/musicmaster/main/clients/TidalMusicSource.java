package com.musicmaster.main.clients;

import com.musicmaster.main.exceptions.TidalApiException;
import com.musicmaster.main.models.TidalSong;
import com.musicmaster.main.models.UserConfig;
import com.musicmaster.main.pojo.TidalTokenResponse;
import com.musicmaster.main.pojo.TidalTracksResponse;
import com.musicmaster.main.repositories.UserConfigRepository;
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
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class TidalMusicSource {
    @Value("${tidal.uri.api}")
    private String API_BASEPATH;

    @Value("${tidal.uri.auth}")
    private String TIDAL_AUTH_BASEPATH;

    @Value("${tidal.client.id}")
    private String CLIENT_ID;

    @Value("${tidal.uri.redirect}")
    private String REDIRECT_URI;

    private final RestTemplate restTemplate;

    @Autowired
    private UserConfigRepository userConfigRepository;

    public TidalMusicSource(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder.build();
        String authToken = getValidAuthToken();
        restTemplate.getInterceptors().add(new ClientHttpRequestInterceptor() {
            @Override
            public ClientHttpResponse intercept(HttpRequest httpRequest, byte[] bytes, ClientHttpRequestExecution clientHttpRequestExecution) throws IOException {
                HttpHeaders headers = httpRequest.getHeaders();
                headers.add("Authorization",  "Bearer " + authToken);
                return clientHttpRequestExecution.execute(httpRequest, bytes);
            }
        });
    }

    public List<TidalSong> getPlaylistTracks(String playlistId) {
        String uri = addQueryParams(API_BASEPATH + "/playlists/" + playlistId + "/tracks");
        TidalTracksResponse response;
        try {
            response = restTemplate.getForObject(uri, TidalTracksResponse.class);
            if (response == null)
                throw new TidalApiException("Failed to get playlist response");
        } catch(HttpClientErrorException ex) {
            throw new TidalApiException("error getting playlist tracks", ex);
        }

        return response.getItems();
    }

    private String addQueryParams(String uri) {
        UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.fromHttpUrl(uri)
                .queryParam("countryCode", "JM")
                .queryParam("limit", 1000);

        return uriComponentsBuilder.build(false).toUriString();
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
            throw new TidalApiException("Client error when requesting tidal auth token", ex);
        }
        return response;
    }

    public String getValidAuthToken() {
        UserConfig userConfig = userConfigRepository.getOne(1);
        if (isTokenExpired(userConfig)) {
            // TODO - update token flow
        }
        return userConfig.getTidalToken();
    }

    private boolean isTokenExpired(UserConfig userConfig) {
        if (restTemplate.getInterceptors().isEmpty())
            return true;

        if (userConfig.getTidalTokenExpiration().isBefore(LocalDateTime.now().minusSeconds(20)))
            return true;

        return false;
    }
}
