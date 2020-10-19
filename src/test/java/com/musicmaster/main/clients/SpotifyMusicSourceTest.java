package com.musicmaster.main.clients;

import com.musicmaster.main.exceptions.SpotifyApiException;
import com.musicmaster.main.models.SpotifyPlaylist;
import com.musicmaster.main.models.UserConfig;
import com.musicmaster.main.pojo.SpotifyProfileDetails;
import com.musicmaster.main.pojo.SpotifyTokenResponse;
import com.musicmaster.main.repositories.UserConfigRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.List;

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
        when(restTemplateBuilder.basicAuthentication(anyString(), anyString())).thenReturn(restTemplateBuilder);
        when(restTemplateBuilder.build()).thenReturn(restTemplate);
        when(restTemplate.postForObject(anyString(),any(), eq(SpotifyTokenResponse.class) )).thenReturn(new SpotifyTokenResponse());
        when(restTemplate.getForObject(anyString(), eq(SpotifyProfileDetails.class))).thenReturn(new SpotifyProfileDetails("e"));
        when(userConfigRepository.getOne(anyInt())).thenReturn(userConfig);

        spotifyMusicSource = new SpotifyMusicSource(restTemplateBuilder, "", "", userConfigRepository);
        ReflectionTestUtils.setField(spotifyMusicSource, "API_BASEPATH", "test");
        ReflectionTestUtils.setField(spotifyMusicSource, "CLIENT_ID", "test");
        ReflectionTestUtils.setField(spotifyMusicSource, "CLIENT_SECRET", "test");
        ReflectionTestUtils.setField(spotifyMusicSource, "REDIRECT_URI", "test");

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
        List<SpotifyPlaylist> playlists = spotifyMusicSource.getPlaylists();
        assertNotEquals(0, playlists.size());
    }
}
