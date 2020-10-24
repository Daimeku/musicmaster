package com.musicmaster.main.clients;

import com.musicmaster.main.exceptions.SpotifyApiException;
import com.musicmaster.main.models.*;
import com.musicmaster.main.pojo.*;
import com.musicmaster.main.repositories.UserConfigRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
//@RestClientTest(SpotifyMusicSource.class)
public class SpotifyMusicSourceTest {

    SpotifyMusicSource spotifyMusicSource;

    @Mock
    RestTemplateBuilder restTemplateBuilder;

    @Mock
    RestTemplate restTemplate;

    @Mock
    UserConfigRepository userConfigRepository;

    @BeforeEach
    public void init() {
        UserConfig userConfig = new UserConfig("tetet");
        userConfig.setSpotifyTokenExpiration(LocalDateTime.now().minusMinutes(4));
        SpotifyPlaylistResponse playlistResponse = new SpotifyPlaylistResponse();
        List<SpotifyPlaylist> playlists = new ArrayList<SpotifyPlaylist>();
        playlists.add(new SpotifyPlaylist("edasfa"));
        playlistResponse.setItems(playlists);
        SpotifyPlaylistTracksResponse tracksResponse = setupTracksResponse();

        when(restTemplateBuilder.basicAuthentication(anyString(), anyString())).thenReturn(restTemplateBuilder);
        when(restTemplateBuilder.build()).thenReturn(restTemplate);
        when(restTemplate.postForObject(anyString(),any(), eq(SpotifyTokenResponse.class) )).thenReturn(new SpotifyTokenResponse());
        when(restTemplate.getForObject(anyString(), eq(SpotifyProfileDetails.class))).thenReturn(new SpotifyProfileDetails("e"));
        when(restTemplate.getForEntity(anyString(), eq(SpotifyPlaylistResponse.class))).thenReturn(new ResponseEntity<SpotifyPlaylistResponse>( playlistResponse, HttpStatus.OK));
        when(restTemplate.getForObject(anyString(), eq(SpotifyPlaylistTracksResponse.class))).thenReturn(tracksResponse);
        when(userConfigRepository.getOne(anyInt())).thenReturn(userConfig);

        spotifyMusicSource = new SpotifyMusicSource(restTemplateBuilder, "", "", userConfigRepository);
        ReflectionTestUtils.setField(spotifyMusicSource, "API_BASEPATH", "http://test");
        ReflectionTestUtils.setField(spotifyMusicSource, "CLIENT_ID", "test");
        ReflectionTestUtils.setField(spotifyMusicSource, "CLIENT_SECRET", "test");
        ReflectionTestUtils.setField(spotifyMusicSource, "REDIRECT_URI", "test");

    }

    private SpotifyPlaylistTracksResponse setupTracksResponse() {
        SpotifyPlaylistItem playlistItem = new SpotifyPlaylistItem();
        playlistItem.setTrack(new SpotifySong("test"));
        List<SpotifyPlaylistItem> playlistItems = new ArrayList<>();
        playlistItems.add(playlistItem);
        SpotifyPlaylistTracksResponse playlistTracksResponse = new SpotifyPlaylistTracksResponse();
        playlistTracksResponse.setItems(playlistItems);
        return playlistTracksResponse;
    }
    @Test
    public void getToken_success() {
        SpotifyTokenResponse response = spotifyMusicSource.getToken("test");
        assertNotNull(response);
    }

    @Test
    public void getToken_failSpotifyError() {
        when(restTemplate.postForObject(anyString(), any(), eq(SpotifyTokenResponse.class))).thenThrow(new HttpClientErrorException(HttpStatus.BAD_REQUEST));
        Exception ex = assertThrows(SpotifyApiException.class, () -> spotifyMusicSource.getToken("asd"));
        assertNotNull(ex.getMessage());
    }

    @Test
    public void getRefreshToken_success() {
        SpotifyTokenResponse response = spotifyMusicSource.getRefreshToken();
        assertNotNull(response);
    }

    @Test
    public void getRefreshToken_failSpotifyError() {
        when(restTemplate.postForObject(anyString(), any(), eq(SpotifyTokenResponse.class))).thenThrow(new HttpClientErrorException(HttpStatus.BAD_REQUEST));
        Exception ex = assertThrows(SpotifyApiException.class, () -> spotifyMusicSource.getRefreshToken());
        assertEquals("Error retrieving refresh token", ex.getMessage());
    }

    @Test
    public void getProfileDetails_success() {
        SpotifyProfileDetails profileDetails = spotifyMusicSource.getProfileDetails();
        assertNotNull(profileDetails.getId());
    }

    @Test
    public void getProfileDetails_failSpotifyError() {
        when(restTemplate.getForObject(anyString(), eq(SpotifyProfileDetails.class))).thenThrow(new HttpClientErrorException(HttpStatus.BAD_REQUEST));
        Exception ex = assertThrows(SpotifyApiException.class, ()-> spotifyMusicSource.getProfileDetails());
        assertEquals("error getting user profiles", ex.getMessage());
    }

    @Test
    public void getPlaylists_success() {
        List<Playlist> playlists = spotifyMusicSource.getPlaylists();
        assertNotEquals(0, playlists.size());
    }

    @Test
    public void getPlaylists_failSpotifyError() {
        when(restTemplate.getForEntity(anyString(), eq(SpotifyPlaylistResponse.class))).thenThrow(new HttpClientErrorException(HttpStatus.BAD_REQUEST));
        Exception ex = assertThrows(SpotifyApiException.class, ()-> spotifyMusicSource.getPlaylists());
        assertTrue(ex.getMessage().contains("playlist"));
    }


    @Test
    public void getPlaylistTracks_success() {
        List<Song> tracks = spotifyMusicSource.getPlaylistTracks("test");
        assertNotEquals(0, tracks.size());
    }

    @Test
    public void getPlaylistTracks_failSpotifyError() {
        when(restTemplate.getForObject(anyString(), eq(SpotifyPlaylistTracksResponse.class))).thenThrow(new HttpClientErrorException(HttpStatus.BAD_REQUEST));
        Exception ex = assertThrows(SpotifyApiException.class, () -> spotifyMusicSource.getPlaylistTracks("asdfgads"));
        assertTrue(ex.getMessage().contains("tracks"));
    }

    @Test
    public void createPlaylist_success() {

        when(restTemplate.postForObject(anyString(), any(HttpEntity.class), eq(Map.class)))
                .thenReturn(Map.of("id", "aasdasda", "name", "test"));

        SpotifyPlaylist playlist = spotifyMusicSource.createPlaylist(new SpotifyPlaylist("testing"));
        assertEquals("test", playlist.getName());
    }

    @Test
    public void addTracksToPlaylist_success() {
        when(restTemplate.postForObject(anyString(), any(HttpEntity.class), eq(Map.class))).thenReturn(Map.of("snapshot_id", "something"));
        List<SpotifySong> tracks = new ArrayList<>();
        tracks.add(new SpotifySong("testing"));
        boolean tracksAdded = spotifyMusicSource.addTracksToPlaylist("sdasdsa", tracks);
        assertTrue(tracksAdded);
    }

    @Test
    public void searchSong_success() {
        when(restTemplate.getForObject(anyString(), eq(SpotifySearchResponse.class))).thenReturn(new SpotifySearchResponse());
        SpotifySong song = new SpotifySong("teadsad");
        song.setName("testSong");
        SpotifySearchResponse searchResponse = spotifyMusicSource.searchSong(song);
        assertNotNull(searchResponse);
    }
}
