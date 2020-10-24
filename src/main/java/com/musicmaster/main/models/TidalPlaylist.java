package com.musicmaster.main.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.ArrayList;
import java.util.List;

public class TidalPlaylist implements Playlist {

    @JsonProperty(value = "title")
    private String name;
    private String description;
    private final String source = "TIDAL";
    private List<TidalSong> songs;

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public void setDescription(String description) {
        this.description = description;
    }

    public String getSource() {
        return source;
    }

    @Override
    public List<Song> getSongs() {
        List<Song> songList = new ArrayList<>(this.songs.size());
        this.songs.forEach(song -> songList.add(song));
        return songList;
    }

    public void setSongs(List<Song> songs) {
        this.songs = new ArrayList<>(songs.size());
        songs.forEach(song -> {
            TidalSong tidalSong = new TidalSong();
            tidalSong.setName(song.getName());
            tidalSong.setAlbum(song.getAlbum());
            this.songs.add(tidalSong);
        } );
    }
}
