package com.musicmaster.main.controllers;

import com.musicmaster.main.clients.SpotifyMusicSource;
import com.musicmaster.main.models.Playlist;
import com.musicmaster.main.models.Song;
import com.musicmaster.main.models.SpotifyPlaylist;
import com.musicmaster.main.models.SpotifySong;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(path = "/playlists")
public class PlaylistController {

    @Autowired
    SpotifyMusicSource spotifyMusicSource;

    @GetMapping(path = "", produces = APPLICATION_JSON_VALUE)
    public List<SpotifyPlaylist> getPlaylists() {
        return spotifyMusicSource.getPlaylists();
    }

    @GetMapping(path = "/{id}", produces = APPLICATION_JSON_VALUE)
    public List<SpotifySong> getPlaylist(@PathVariable String id) {
        return spotifyMusicSource.getPlaylistTracks(id);
    }

    @PostMapping(path = "", produces = APPLICATION_JSON_VALUE)
    public Object createPlaylist() {
        return spotifyMusicSource.createPlaylist();
    }
}
