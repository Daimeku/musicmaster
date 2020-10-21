package com.musicmaster.main.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.musicmaster.main.pojo.SpotifyPlaylistRequest;

import java.util.ArrayList;
import java.util.List;

public class SpotifyPlaylist implements Playlist {

    private String id;
    private String name;
    private List<SpotifySong> songs;
    private String source = "SPOTIFY";
    private String description;
    @JsonProperty(value = "public")
    private boolean isPublic;
    private boolean collaborative;

    public SpotifyPlaylist() {
    }

    public SpotifyPlaylist(SpotifyPlaylistRequest request) {
        this.name = request.getName();
        this.description = request.getDescription();
        this.collaborative = request.isCollaborative();
        this.isPublic = request.isPublic();
    }

    public SpotifyPlaylist(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @JsonIgnore
    public List<SpotifySong> getSpotifySongs() {
        return songs;
    }

    public void setSpotifySongs(List<SpotifySong> songs) {
        this.songs = songs;
    }

    @Override
    public List<Song> getSongs() {
        List<Song> songs = new ArrayList<>(this.songs.size());
        this.songs.forEach(song -> {
            Song newSong = new Song();
            newSong.setName(song.getName());
            newSong.setAlbum(song.getAlbum());
            newSong.setArtist(song.getArtist());
            songs.add(newSong);
        });
        return songs;
    }

    public void setSongs(List<Song> songs) {
        this.songs = new ArrayList<>();
        songs.forEach( song-> {
            SpotifySong newSong = new SpotifySong();
            newSong.setName( song.getName());
            newSong.setArtist(song.getArtist());
            newSong.setAlbum(song.getAlbum());
            this.songs.add(newSong);
        });
    }

    @Override
    public String getSource() {
        return this.source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isPublic() {
        return isPublic;
    }

    public void setPublic(boolean aPublic) {
        isPublic = aPublic;
    }

    public boolean isCollaborative() {
        return collaborative;
    }

    public void setCollaborative(boolean collaborative) {
        this.collaborative = collaborative;
    }
}
