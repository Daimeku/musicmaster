package com.musicmaster.main.services;

import com.musicmaster.main.clients.SpotifyMusicSource;
import com.musicmaster.main.clients.TidalMusicSource;
import com.musicmaster.main.models.*;
import com.musicmaster.main.pojo.SpotifySearchResponse;
import com.musicmaster.main.pojo.SpotifySearchResponseTracks;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
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
        setupSpotifyMocks();
        setupTidalMocks();
        playlistTransferService = new PlaylistTransferService(spotifyMusicSource, tidalMusicSource);
    }

    @Test
    public void copyTidalPlaylistToSpotify_success() {
        Playlist spotifyPlaylist = playlistTransferService.copyTidalPlaylistToSpotify("testId", "new-name");
        Assertions.assertTrue(spotifyPlaylist.getName().length() > 0);
    }

    private void setupTidalMocks() {
        List<TidalSong> tidalSongs = new ArrayList<>();
        TidalSong tidalSong = new TidalSong();
        TidalAlbum album = new TidalAlbum();
        album.setName("test");
        album.setTitle("test");
        Artist artist = new Artist();
        tidalSong.setName("test");
        tidalSong.setAlbum(album);
        tidalSong.setArtist(artist);
        tidalSongs.add(tidalSong);
        when(tidalMusicSource.getPlaylistTracks(anyString())).thenReturn(tidalSongs);
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
}
