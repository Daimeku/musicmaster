package com.musicmaster.main.clients;

import com.musicmaster.main.models.TidalSong;
import com.musicmaster.main.pojo.LegacyTidalTracksResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
public class TidalMusicSourceTest {

    @Mock
    private RestTemplateBuilder restTemplateBuilder;

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private TidalAuthInterceptor tidalAuthInterceptor;

    private TidalMusicSource tidalMusicSource;

    @BeforeEach
    public void init() {
        List<TidalSong> songs = new ArrayList<>();
        songs.add(new TidalSong());
        LegacyTidalTracksResponse legacyTidalTracksResponse = new LegacyTidalTracksResponse();
        legacyTidalTracksResponse.setData(songs);
        when(restTemplateBuilder.build()).thenReturn(restTemplate);
        when(restTemplate.getForObject(anyString(), eq(LegacyTidalTracksResponse.class))).thenReturn(legacyTidalTracksResponse);
        tidalMusicSource = new TidalMusicSource(restTemplateBuilder, tidalAuthInterceptor );
        ReflectionTestUtils.setField(tidalMusicSource, "API_BASEPATH", "http://testing");
        ReflectionTestUtils.setField(tidalMusicSource, "TIDAL_TOKEN", "asdfasdfa");
    }

    @Test
    public void getPlaylistTracks_success() {
        List<TidalSong> songs = tidalMusicSource.getPlaylistTracks("test");
        Assertions.assertNotEquals(0, songs.size());
    }

}
