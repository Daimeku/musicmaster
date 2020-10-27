package com.musicmaster.main.clients;

import com.musicmaster.main.exceptions.SpotifyApiException;
import com.musicmaster.main.models.*;
import com.musicmaster.main.pojo.*;
import com.musicmaster.main.repositories.UserConfigRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.springframework.util.MimeTypeUtils.APPLICATION_JSON_VALUE;

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
        this.updateRequestToken(response.getAccessToken(), response.getExpiresIn());

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

        this.updateRequestToken(response.getAccessToken(), response.getExpiresIn());

        return response;
    }

    public SpotifyProfileDetails getProfileDetails() {
        preRequestSetup();
        SpotifyProfileDetails profileDetails;

        try {
            profileDetails = restTemplate.getForObject(API_BASEPATH + "/me", SpotifyProfileDetails.class);
        } catch(HttpClientErrorException ex) {
            throw new SpotifyApiException("error getting user profiles", ex);
        }
        return profileDetails;
    }

    //make request to get playlists from spotify
    public List<Playlist> getPlaylists() {

        preRequestSetup();

        //make a request to get the list of playlists
        UserConfig config = userConfigRepository.getOne(1);
        ResponseEntity<SpotifyPlaylistResponse> response;

        try {
             response = restTemplate.getForEntity(API_BASEPATH + "/users/" + config.getSpotifyUserId() + "/playlists",
                    SpotifyPlaylistResponse.class);
        } catch (Exception ex) {
            throw new SpotifyApiException("failed to get playlists", ex);
        }


        List<Playlist> playlists = new ArrayList<>();
        response.getBody().getItems()
                .forEach((item) -> playlists.add(item) );

        return playlists;
    }

    public List<Song> getPlaylistTracks(String id) {

        preRequestSetup();

        String playlistUri = API_BASEPATH + "/playlists/" + id + "/tracks";
        //send the request to get playlists
        SpotifyPlaylistTracksResponse playlistReponse;
        try {
             playlistReponse = restTemplate.getForObject(playlistUri, SpotifyPlaylistTracksResponse.class);
        } catch (HttpClientErrorException ex) {
            throw new SpotifyApiException("error getting playlist tracks", ex);
        }

        List<SpotifyPlaylistItem> playlistItems = playlistReponse.getItems();

        List<Song> songs = new ArrayList<>();
        playlistItems.stream().forEach(item->songs.add(item.getTrack()));

        return songs;

    }

    public SpotifyPlaylist createPlaylist(SpotifyPlaylist sourcePlaylist) {
        preRequestSetup();

        UserConfig config = userConfigRepository.getOne(1);
        String playlistEndpoint = API_BASEPATH + "/users/" + config.getSpotifyUserId() + "/playlists";

        SpotifyPlaylistRequest playlist = new SpotifyPlaylistRequest(sourcePlaylist);
        HttpHeaders headers = new HttpHeaders(  );
        headers.add("Content-Type", APPLICATION_JSON_VALUE);
        HttpEntity<SpotifyPlaylist> request = new HttpEntity(playlist, headers);

        Map<String, Object> playlistResponse = restTemplate.postForObject(playlistEndpoint, request, Map.class);
        SpotifyPlaylist spotifyPlaylist = new SpotifyPlaylist();
        spotifyPlaylist.setId(playlistResponse.get("id").toString());
        spotifyPlaylist.setName(playlistResponse.get("name").toString());
        if(playlistResponse.get("description") != null)
            spotifyPlaylist.setDescription(playlistResponse.get("description").toString());
        return spotifyPlaylist;
    }

    public boolean addTracksToPlaylist(String playlistId, List<SpotifySong> tracks) {
        preRequestSetup();

        boolean tracksAdded = false;
        String requestUri = API_BASEPATH + "/playlists/" + playlistId + "/tracks";
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", APPLICATION_JSON_VALUE);
        List<String> trackUris = formatTrackUri(tracks);
        Map<String, Object> requestMap = new HashMap<>();
        requestMap.put("uris", trackUris);
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestMap, headers);
        Map<String, String> response;
        try {
            response = restTemplate.postForObject(requestUri, request, Map.class);
        } catch( HttpClientErrorException ex) {
            throw new SpotifyApiException("error while adding tracks to playlist ", ex);
        }

        if(response.get("snapshot_id") != null)
            tracksAdded = true;

        return tracksAdded;
    }

    public SpotifyPlaylist createPlaylistAndAddTracks(SpotifyPlaylist sourcePlaylist) {
        SpotifyPlaylist playlist = createPlaylist( sourcePlaylist);
        boolean tracksAdded = addTracksToPlaylist(playlist.getId(), sourcePlaylist.getSpotifySongs());
        return playlist;
    }

    public SpotifySearchResponse searchSong(SpotifySong song) {
        preRequestSetup();

        String searchString = song.getName();
        if(song.getArtist() != null)
            searchString += " " + song.getArtist().getName();

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(API_BASEPATH + "/search")
                .queryParam("q", searchString)
                .queryParam("type", "track");
        String url = builder.build(false).toUriString();
        logger.info("searching url: ",url);
        SpotifySearchResponse response;
        try {
            response = restTemplate.getForObject(url, SpotifySearchResponse.class);
        } catch(HttpClientErrorException ex) {
            throw new SpotifyApiException("Error searching for song", ex);
        }

        if(response == null)
            throw new SpotifyApiException("null response received from search");

        return response;
    }

    private List<String> formatTrackUri(List<SpotifySong> tracks) {
        List<String> trackUris = new ArrayList<>(tracks.size());
        tracks.forEach(track -> trackUris.add("spotify:track:" + track.getId()));
        return trackUris;
    }

    private void preRequestSetup(){
        if(tokenExpired())
            getRefreshToken();
    }

    private boolean tokenExpired() {
        UserConfig config = userConfigRepository.getOne(1);

        if(restTemplate.getInterceptors().size() < 1)
            return true;
        //offset expiration by a few seconds
        if(config.getSpotifyTokenExpiration().isBefore(LocalDateTime.now().minusSeconds(20)))
            return true;


        return false;
    }

    private void updateRequestToken(String token, int expiration) {

        UserConfig config = userConfigRepository.findById(1).orElse(new UserConfig());;
        config.setSpotifyToken(token);
        config.setSpotifyTokenExpiration(LocalDateTime.now().plusSeconds(expiration));
        userConfigRepository.save(config);
        restTemplate.getInterceptors().clear();
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
