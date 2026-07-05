package com.musicmaster.main.clients;

import com.musicmaster.main.models.TidalSong;
import com.musicmaster.main.exceptions.TidalApiException;
import com.musicmaster.main.pojo.LegacyTidalTracksResponse;
import com.musicmaster.main.pojo.TidalTrack;
import com.musicmaster.main.pojo.TidalTracksResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
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
        when(restTemplate.getInterceptors()).thenReturn(new ArrayList<>());
        when(restTemplate.getForObject(anyString(), eq(LegacyTidalTracksResponse.class))).thenReturn(legacyTidalTracksResponse);
        tidalMusicSource = new TidalMusicSource(restTemplateBuilder, tidalAuthInterceptor );
        ReflectionTestUtils.setField(tidalMusicSource, "API_BASEPATH", "http://testing");
    }

    @Test
    public void getPlaylistTracks_success() {
        List<TidalSong> songs = tidalMusicSource.getPlaylistTracks("test");
        Assertions.assertNotEquals(0, songs.size());
    }

    @Test
    public void getTracks_noTrackIds_returnsEmptyList() {
        List<TidalTrack> tracks = tidalMusicSource.getTracks(new ArrayList<>());

        Assertions.assertEquals(0, tracks.size());
        verify(restTemplate, never()).getForObject(anyString(), eq(TidalTracksResponse.class));
    }

    @Test
    public void getTracks_twentyTrackIds_requestsOneBatch() {
        when(restTemplate.getForObject(anyString(), eq(TidalTracksResponse.class)))
                .thenReturn(tidalTracksResponse("track-001", "track-020"));

        List<TidalTrack> tracks = tidalMusicSource.getTracks(trackIds(20));

        Assertions.assertEquals(2, tracks.size());

        ArgumentCaptor<String> uriCaptor = ArgumentCaptor.forClass(String.class);
        verify(restTemplate, times(1)).getForObject(uriCaptor.capture(), eq(TidalTracksResponse.class));
        Assertions.assertTrue(uriCaptor.getValue().contains("track-001"));
        Assertions.assertTrue(uriCaptor.getValue().contains("track-020"));
    }

    @Test
    public void getTracks_twentyOneTrackIds_requestsTwoBatches() {
        when(restTemplate.getForObject(anyString(), eq(TidalTracksResponse.class)))
                .thenReturn(tidalTracksResponse("track-001"))
                .thenReturn(tidalTracksResponse("track-021"));

        List<TidalTrack> tracks = tidalMusicSource.getTracks(trackIds(21));

        Assertions.assertEquals(2, tracks.size());

        ArgumentCaptor<String> uriCaptor = ArgumentCaptor.forClass(String.class);
        verify(restTemplate, times(2)).getForObject(uriCaptor.capture(), eq(TidalTracksResponse.class));
        Assertions.assertTrue(uriCaptor.getAllValues().get(0).contains("track-020"));
        Assertions.assertFalse(uriCaptor.getAllValues().get(0).contains("track-021"));
        Assertions.assertTrue(uriCaptor.getAllValues().get(1).contains("track-021"));
    }

    @Test
    public void getTracks_fortyFiveTrackIds_requestsThreeBatches() {
        when(restTemplate.getForObject(anyString(), eq(TidalTracksResponse.class)))
                .thenReturn(tidalTracksResponse("track-001"))
                .thenReturn(tidalTracksResponse("track-021"))
                .thenReturn(tidalTracksResponse("track-041"));

        List<TidalTrack> tracks = tidalMusicSource.getTracks(trackIds(45));

        Assertions.assertEquals(3, tracks.size());

        ArgumentCaptor<String> uriCaptor = ArgumentCaptor.forClass(String.class);
        verify(restTemplate, times(3)).getForObject(uriCaptor.capture(), eq(TidalTracksResponse.class));
        Assertions.assertTrue(uriCaptor.getAllValues().get(0).contains("track-020"));
        Assertions.assertFalse(uriCaptor.getAllValues().get(0).contains("track-021"));
        Assertions.assertTrue(uriCaptor.getAllValues().get(1).contains("track-021"));
        Assertions.assertTrue(uriCaptor.getAllValues().get(1).contains("track-040"));
        Assertions.assertFalse(uriCaptor.getAllValues().get(1).contains("track-041"));
        Assertions.assertTrue(uriCaptor.getAllValues().get(2).contains("track-041"));
        Assertions.assertTrue(uriCaptor.getAllValues().get(2).contains("track-045"));
    }

    @Test
    public void getTracks_nullResponse_throwsTidalApiException() {
        when(restTemplate.getForObject(anyString(), eq(TidalTracksResponse.class))).thenReturn(null);

        Assertions.assertThrows(TidalApiException.class, () -> tidalMusicSource.getTracks(trackIds(1)));
    }

    private List<String> trackIds(int count) {
        List<String> trackIds = new ArrayList<>();
        for (int index = 1; index <= count; index++) {
            trackIds.add(String.format("track-%03d", index));
        }
        return trackIds;
    }

    private TidalTracksResponse tidalTracksResponse(String... trackIds) {
        List<TidalTrack> tracks = new ArrayList<>();
        for (String trackId : trackIds) {
            TidalTrack track = new TidalTrack();
            track.setId(trackId);
            tracks.add(track);
        }

        TidalTracksResponse response = new TidalTracksResponse();
        response.setData(tracks);
        return response;
    }
}
