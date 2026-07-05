package com.musicmaster.main.clients;

import com.musicmaster.main.exceptions.TidalApiException;
import com.musicmaster.main.models.TidalSong;
import com.musicmaster.main.pojo.TidalPlaylistTracksResponse;
import com.musicmaster.main.pojo.LegacyTidalTracksResponse;
import com.musicmaster.main.pojo.TidalTracksResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

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
        LegacyTidalTracksResponse response;
        try {
            logger.info("requesting tidal playlist uri: " + uri);
            response = restTemplate.getForObject(uri, LegacyTidalTracksResponse.class);
            if (response == null)
                throw new TidalApiException("Failed to get playlist response");
        } catch(HttpClientErrorException ex) {
            throw new TidalApiException("error getting playlist tracks", ex);
        }

        return response.getData();
    }

    public TidalPlaylistTracksResponse getPlaylistTracksResponse(String playlistId) {
        String uri = addQueryParams(API_BASEPATH + "/playlists/" + playlistId + "/relationships/items");
        TidalPlaylistTracksResponse response;
        try {
            logger.info("requesting tidal playlist uri: " + uri);
            response = restTemplate.getForObject(uri, TidalPlaylistTracksResponse.class);
            if (response == null)
                throw new TidalApiException("Failed to get playlist response");
        } catch(HttpClientErrorException ex) {
            throw new TidalApiException("error getting playlist tracks", ex);
        }

        return response;
    }


    public TidalTracksResponse getTracks(List<String> trackIds) {
        String filteredIds = String.join(",", trackIds);
        UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.fromHttpUrl(API_BASEPATH + "/tracks")
                .queryParam("countryCode", "JM")
                .queryParam("limit", 1000)
                .queryParam("filter[id]", trackIds);

        String uri = uriComponentsBuilder.build(false).toUriString();

        TidalTracksResponse response;
        try {
            logger.info("requesting tidal tracks uri: " + uri);
            response = restTemplate.getForObject(uri, TidalTracksResponse.class);
            if (response == null)
                throw new TidalApiException("Failed to get tracks response");
        } catch(HttpClientErrorException ex) {
            throw new TidalApiException("error getting tracks tracks", ex);
        }

        return response;
    }

    private String addQueryParams(String uri) {
        UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.fromHttpUrl(uri)
                .queryParam("countryCode", "JM")
                .queryParam("limit", 1000);

        return uriComponentsBuilder.build(false).toUriString();
    }
}
