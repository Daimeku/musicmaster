package com.musicmaster.main.clients;

import com.musicmaster.main.controllers.AuthController;
import com.musicmaster.main.exceptions.TidalApiException;
import com.musicmaster.main.models.TidalSong;
import com.musicmaster.main.pojo.TidalTracksResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.List;

@Service
public class TidalMusicSource {
    private static Logger logger = LoggerFactory.getLogger(TidalMusicSource.class);

    @Value("${tidal.uri.api}")
    private String API_BASEPATH;

    private final RestTemplate restTemplate;

    public TidalMusicSource(RestTemplateBuilder restTemplateBuilder, TidalAuthInterceptor tidalAuthInterceptor) {
        this.restTemplate = restTemplateBuilder.build();
        restTemplate.getInterceptors().add(tidalAuthInterceptor);
        restTemplate.getInterceptors().add((httpRequest, bytes, clientHttpRequestExecution) -> {
            HttpHeaders headers = httpRequest.getHeaders();
            headers.add("accept", "accept: application/vnd.api+json");
            return clientHttpRequestExecution.execute(httpRequest, bytes);
        });
    }

    public List<TidalSong> getPlaylistTracks(String playlistId) {
        String uri = addQueryParams(API_BASEPATH + "/playlists/" + playlistId);
        TidalTracksResponse response;
        try {
            logger.info("requesting tidal playlist uri: " + uri);
            response = restTemplate.getForObject(uri, TidalTracksResponse.class);
            if (response == null)
                throw new TidalApiException("Failed to get playlist response");
        } catch(HttpClientErrorException ex) {
            throw new TidalApiException("error getting playlist tracks", ex);
        }

        return response.getData();
    }

    private String addQueryParams(String uri) {
        UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.fromHttpUrl(uri)
                .queryParam("countryCode", "JM")
                .queryParam("limit", 1000);

        return uriComponentsBuilder.build(false).toUriString();
    }
}
