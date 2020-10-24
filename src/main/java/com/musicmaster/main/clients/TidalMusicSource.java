package com.musicmaster.main.clients;

import com.musicmaster.main.exceptions.TidalApiException;
import com.musicmaster.main.models.Song;
import com.musicmaster.main.models.TidalSong;
import com.musicmaster.main.pojo.TidalTracksResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
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

    @Value("${tidal.uri.api}")
    private String API_BASEPATH;
    @Value("${tidal.token}")
    private String TIDAL_TOKEN;

    private RestTemplate restTemplate;
    private RestTemplateBuilder restTemplateBuilder;

    public TidalMusicSource(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder.build();
        this.restTemplateBuilder = restTemplateBuilder;
        restTemplate.getInterceptors().add(new ClientHttpRequestInterceptor() {
            @Override
            public ClientHttpResponse intercept(HttpRequest httpRequest, byte[] bytes, ClientHttpRequestExecution clientHttpRequestExecution) throws IOException {
                httpRequest.getHeaders().add("x-tidal-token", TIDAL_TOKEN);
                return clientHttpRequestExecution.execute(httpRequest, bytes);
            }
        });
    }

    public List<TidalSong> getPlaylistTracks(String playlistId) {
        String uri = addQueryParams(API_BASEPATH + "/playlists/" + playlistId + "/tracks");
        TidalTracksResponse response;
        try {
            response = restTemplate.getForObject(uri, TidalTracksResponse.class);
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
}
