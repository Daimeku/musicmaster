package com.musicmaster.main.services;

import com.musicmaster.main.clients.SpotifyMusicSource;
import com.musicmaster.main.clients.TidalMusicSource;
import com.musicmaster.main.models.*;
import com.musicmaster.main.pojo.SpotifySearchResponse;
import com.musicmaster.main.pojo.TidalTrack;
import com.musicmaster.main.pojo.TidalTrackAttributes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class PlaylistTransferService {

    @Autowired
    private SpotifyMusicSource spotifyMusicSource;

    @Autowired
    private TidalMusicSource tidalMusicSource;

    public PlaylistTransferService(SpotifyMusicSource spotifyMusicSource, TidalMusicSource tidalMusicSource) {
        this.spotifyMusicSource = spotifyMusicSource;
        this.tidalMusicSource = tidalMusicSource;
    }

    public SpotifyPlaylist copyTidalPlaylistToSpotify(String tidalPlaylistId, String playlistName) {
        Playlist finalSpotifyPlaylist = new SpotifyPlaylist();

        //get songs from tidal
        List<String> trackIds = tidalMusicSource.getAllPlaylistsTrackIds(tidalPlaylistId);
        List<TidalTrack> tidalTracks = tidalMusicSource.getTracks(trackIds);
        List<Song> songsForSearch = convertTidalTracks(tidalTracks);

        //find songs in spotify
        List<SpotifySong> spotifySongList = findSpotifySongs(songsForSearch);
        //create playlist
        SpotifyPlaylist newPlaylist = new SpotifyPlaylist(playlistName);
        newPlaylist.setSpotifySongs(spotifySongList);
        //create playlist & add tracks in spotify
        newPlaylist = spotifyMusicSource.createPlaylistAndAddTracks(newPlaylist);

        return newPlaylist;
    }

    public List<SpotifySong> findSpotifySongs(List<Song> songs) {
        List<SpotifySong> spotifySongs = new ArrayList<>(songs.size());

        songs.forEach( song -> {
            SpotifySong spotifySong = new SpotifySong();
            spotifySong.setName(song.getName());
            spotifySong.setIsrc(song.getIsrc());

            if (song.getAlbum() != null) {
                Album album = new Album();
                album.setName(song.getAlbum().getName());
                spotifySong.setAlbum(album);
            }

            if (song.getArtist() != null) {
                Artist artist = new Artist();
                artist.setName(song.getArtist().getName());
                spotifySong.setArtist(artist);
            }

            SpotifySearchResponse searchresults = spotifyMusicSource.searchSong(spotifySong);

            if(searchresults != null
                    && searchresults.getTracks() != null
                    && searchresults.getTracks().getItems() != null
                    && !searchresults.getTracks().getItems().isEmpty()) {
                SpotifySong firstResult = searchresults.getTracks().getItems().get(0);
                spotifySong.setId(firstResult.getId());
            }

            if(spotifySong.getId() != null && !spotifySong.getId().isEmpty())
                spotifySongs.add(spotifySong);
        });

        return spotifySongs;
    }

    List<Song> convertTidalTracks(List<TidalTrack> tidalTracks) {
        List<Song> songs = new ArrayList<>(tidalTracks.size());
        tidalTracks.forEach(tidalTrack -> {
            Song song = new Song();
            TidalTrackAttributes attributes = tidalTrack.getAttributes();
            if (attributes != null) {
                song.setName(attributes.getTitle());
                song.setIsrc(attributes.getIsrc());
            }
            songs.add(song);
        });
        return songs;
    }

    List<Song> convertSongs(List<TidalSong> tidalSongs) {
        List<Song> songs = new ArrayList<>(tidalSongs.size());
        tidalSongs.forEach(tidalSong -> {
            Song song = new Song();
            song.setName(tidalSong.getName());
            Album album = new Album();
            album.setName(tidalSong.getAlbum().getTitle());
            Artist artist = new Artist();
            artist.setName(tidalSong.getArtist().getName());
            song.setAlbum(album);
            song.setArtist(artist);
            songs.add(song);
        });
        return songs;
    }
}
