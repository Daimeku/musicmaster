package com.musicmaster.main.clients;

import com.musicmaster.main.models.SpotifySong;
import com.musicmaster.main.models.TidalSong;
import com.musicmaster.main.pojo.TidalTracksResponse;
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

    private TidalMusicSource tidalMusicSource;

    @BeforeEach
    public void init() {
        List<TidalSong> songs = new ArrayList<>();
        songs.add(new TidalSong());
        TidalTracksResponse tidalTracksResponse = new TidalTracksResponse();
        tidalTracksResponse.setItems(songs);
        when(restTemplateBuilder.build()).thenReturn(restTemplate);
        when(restTemplate.getForObject(anyString(), eq(TidalTracksResponse.class))).thenReturn(tidalTracksResponse);
        tidalMusicSource = new TidalMusicSource(restTemplateBuilder);
        ReflectionTestUtils.setField(tidalMusicSource, "API_BASEPATH", "http://testing");
        ReflectionTestUtils.setField(tidalMusicSource, "TIDAL_TOKEN", "asdfasdfa");
    }

    @Test
    public void getPlaylistTracks_success() {
        List<TidalSong> songs = tidalMusicSource.getPlaylistTracks("test");
        Assertions.assertNotEquals(0, songs.size());
    }

}
