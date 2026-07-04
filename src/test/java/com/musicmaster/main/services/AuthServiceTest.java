package com.musicmaster.main.services;

import com.musicmaster.main.clients.SpotifyMusicSource;
import com.musicmaster.main.clients.TidalMusicSource;
import com.musicmaster.main.helpers.UriHelper;
import com.musicmaster.main.models.UserConfig;
import com.musicmaster.main.pojo.SpotifyProfileDetails;
import com.musicmaster.main.pojo.SpotifyTokenResponse;
import com.musicmaster.main.repositories.UserConfigRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    private AuthService authService;

    @Mock
    UserConfigRepository userConfigRepository;

    @Mock
    SpotifyMusicSource spotifyMusicSource;

    @Mock
    TidalMusicSource tidalMusicSource;

    @Mock
    UriHelper uriHelper;

    @BeforeEach
    public void init() {
        authService = new AuthService(userConfigRepository, spotifyMusicSource, tidalMusicSource, uriHelper);
        SpotifyTokenResponse mockSpotifyResponse = new SpotifyTokenResponse();
        SpotifyProfileDetails mockProfileDetails = new SpotifyProfileDetails("test");

        when(spotifyMusicSource.getToken(anyString())).thenReturn(mockSpotifyResponse);
        when(spotifyMusicSource.getProfileDetails()).thenReturn(mockProfileDetails);
        when(userConfigRepository.save(any())).thenReturn(new UserConfig());
        when(userConfigRepository.getOne(anyInt())).thenReturn(new UserConfig());
    }

    @Test
    public void loadSpotifyAuthToken_success() {
        authService.loadSpotifyAuthToken("test");
        verify(userConfigRepository, times(2)).save(any());
    }
}
