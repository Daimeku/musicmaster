package com.musicmaster.main.services;

import com.musicmaster.main.clients.SpotifyMusicSource;
import com.musicmaster.main.clients.TidalMusicSource;
import com.musicmaster.main.models.*;
import com.musicmaster.main.pojo.SpotifySearchResponse;
import com.musicmaster.main.pojo.SpotifySearchResponseTracks;
import com.musicmaster.main.pojo.TidalTrack;
import com.musicmaster.main.pojo.TidalTrackAttributes;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PlaylistTransferServiceTest {

    @Mock
    private SpotifyMusicSource spotifyMusicSource;

    @Mock
    private TidalMusicSource tidalMusicSource;

    private PlaylistTransferService playlistTransferService;

    @BeforeEach
    public void init() {
        playlistTransferService = new PlaylistTransferService(spotifyMusicSource, tidalMusicSource);
    }

    @Test
    public void copyTidalPlaylistToSpotify_success() {
        setupSpotifyMocks();
        setupTidalMocks();

        Playlist spotifyPlaylist = playlistTransferService.copyTidalPlaylistToSpotify("testId", "new-name");

        Assertions.assertEquals("test-playlist", spotifyPlaylist.getName());
        verify(tidalMusicSource).getAllPlaylistsTrackIds("testId");
        verify(tidalMusicSource).getTracks(Arrays.asList("track-1"));
        verify(tidalMusicSource, never()).getPlaylistTracks("testId");

        ArgumentCaptor<SpotifySong> searchSongCaptor = ArgumentCaptor.forClass(SpotifySong.class);
        verify(spotifyMusicSource).searchSong(searchSongCaptor.capture());
        Assertions.assertEquals("tidal-title", searchSongCaptor.getValue().getName());
        Assertions.assertEquals("USRC17607839", searchSongCaptor.getValue().getIsrc());
        Assertions.assertNull(searchSongCaptor.getValue().getAlbum());
        Assertions.assertNull(searchSongCaptor.getValue().getArtist());
    }

    private void setupTidalMocks() {
        when(tidalMusicSource.getAllPlaylistsTrackIds("testId")).thenReturn(Arrays.asList("track-1"));
        when(tidalMusicSource.getTracks(Arrays.asList("track-1"))).thenReturn(Arrays.asList(tidalTrack("tidal-title", "USRC17607839")));
    }

    private void setupSpotifyMocks() {
        List<SpotifySong> spotifySongList = new ArrayList<>();
        SpotifySong song = new SpotifySong("ajsda");
        song.setArtist(new Artist());
        song.setAlbum(new Album());
        spotifySongList.add(song);
        SpotifySearchResponseTracks spotifySearchResponseTracks = new SpotifySearchResponseTracks();
        spotifySearchResponseTracks.setItems(spotifySongList);
        SpotifySearchResponse spotifySearchResponse = new SpotifySearchResponse();
        spotifySearchResponse.setTracks(spotifySearchResponseTracks);
        when(spotifyMusicSource.searchSong(any(SpotifySong.class))).thenReturn(spotifySearchResponse);

        SpotifyPlaylist spotifyPlaylist = new SpotifyPlaylist("test-playlist");
        when(spotifyMusicSource.createPlaylistAndAddTracks(any(SpotifyPlaylist.class))).thenReturn(spotifyPlaylist);
    }

    private TidalTrack tidalTrack(String title, String isrc) {
        TidalTrackAttributes attributes = new TidalTrackAttributes();
        attributes.setTitle(title);
        attributes.setIsrc(isrc);

        TidalTrack track = new TidalTrack();
        track.setAttributes(attributes);
        return track;
    }
}
