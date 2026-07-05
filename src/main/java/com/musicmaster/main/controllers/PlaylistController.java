package com.musicmaster.main.controllers;

import com.musicmaster.main.clients.SpotifyMusicSource;
import com.musicmaster.main.clients.TidalMusicSource;
import com.musicmaster.main.controllers.dto.CreateSpotifyPlaylistRequestBody;
import com.musicmaster.main.exceptions.BadRequestException;
import com.musicmaster.main.models.*;
import com.musicmaster.main.pojo.SpotifySearchResponse;
import com.musicmaster.main.pojo.TidalPlaylistData;
import com.musicmaster.main.pojo.TidalPlaylistTracksResponse;
import com.musicmaster.main.pojo.TidalTracksResponse;
import com.musicmaster.main.services.PlaylistTransferService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(path = "/playlists")
public class PlaylistController {

    @Autowired
    SpotifyMusicSource spotifyMusicSource;

    @Autowired
    TidalMusicSource tidalMusicSource;

    @Autowired
    PlaylistTransferService playlistTransferService;

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

    @GetMapping(path = "/searchSpotify")
    public SpotifySearchResponse searchSpotify(@RequestParam String songName) {
        SpotifySong song = new SpotifySong();
        song.setName(songName);
        return spotifyMusicSource.searchSong(song);
    }

    @GetMapping(path = "/tidal")
    public List<String> getTidalSongs(@RequestParam String playlistId) {
        return tidalMusicSource.getAllPlaylistsTrackIds(playlistId);
    }

    @GetMapping(path = "/tidalPlaylistTracks")
    public TidalTracksResponse getTidalPlaylistTracks(@RequestParam String playlistId) {
        List<String> trackIds = tidalMusicSource.getAllPlaylistsTrackIds(playlistId);
        return tidalMusicSource.getTracks(trackIds);
    }

    @PostMapping(path = "/tidalToSpotify")
    public Playlist copyFromTidalToSpotify(@RequestBody CreateSpotifyPlaylistRequestBody requestBody) {
        if (requestBody.getSpotifyPlaylistName() == null || requestBody.getTidalPlaylistId() == null) {
            throw new BadRequestException("Missing required request parameter");
        }
        return playlistTransferService.copyTidalPlaylistToSpotify(requestBody.getTidalPlaylistId(),
                requestBody.getSpotifyPlaylistName());
    }
}
