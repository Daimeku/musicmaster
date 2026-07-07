package com.musicmaster.main.models;

import java.util.List;

public class Song {

    protected String name;
    private String isrc;
    private Album album;
    private Artist artist;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIsrc() {
        return isrc;
    }

    public void setIsrc(String isrc) {
        this.isrc = isrc;
    }

    public Album getAlbum() {
        return album;
    }

    public void setAlbum(Album album) {
        this.album = album;
    }

    public Artist getArtist() {
        return artist;
    }

    public void setArtist(Artist artist) {
        this.artist = artist;
    }
}
