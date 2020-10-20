package com.musicmaster.main.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.musicmaster.main.pojo.SpotifyPlaylistRequest;

import java.util.List;

public class SpotifyPlaylist implements Playlist {

    private String id;
    private String name;
    private List<Song> songs;
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

    @Override
    public List<Song> getSongs() {
        return songs;
    }

    public void setSongs(List<Song> songs) {
        this.songs = songs;
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
