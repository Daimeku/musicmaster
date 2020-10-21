package com.musicmaster.main.controllers;

import com.musicmaster.main.clients.SpotifyMusicSource;
import com.musicmaster.main.models.Playlist;
import com.musicmaster.main.models.Song;
import com.musicmaster.main.models.SpotifyPlaylist;
import com.musicmaster.main.models.SpotifySong;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(path = "/playlists")
public class PlaylistController {

    @Autowired
    SpotifyMusicSource spotifyMusicSource;

    @GetMapping(path = "", produces = APPLICATION_JSON_VALUE)
    public List<Playlist> getPlaylists() {
        return spotifyMusicSource.getPlaylists();
    }

    @GetMapping(path = "/{id}", produces = APPLICATION_JSON_VALUE)
    public List<Song> getPlaylist(@PathVariable String id) {
        return spotifyMusicSource.getPlaylistTracks(id);
    }

    @PostMapping(path = "", produces = APPLICATION_JSON_VALUE)
    public String createPlaylist() {
        SpotifySong song = new SpotifySong("1q8kj0fpTfdGYgjuJd2UTM");
        song.setName("Bluffin");
        List<SpotifySong> songs = new ArrayList<>();
        songs.add(song);
        SpotifyPlaylist playlist = new SpotifyPlaylist("brand-new-playlist");
        playlist.setSpotifySongs(songs);
        playlist = spotifyMusicSource.createPlaylistAndAddTracks(playlist);
        return playlist.getId();
    }
}
