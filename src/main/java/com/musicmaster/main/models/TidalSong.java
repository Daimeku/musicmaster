package com.musicmaster.main.models;

public class TidalSong extends Song {
    private String id;
    private String description;
    private String title;
    private TidalAlbum album;

    @Override
    public String getName() {
        return this.title;
    }

    @Override
    public void setName(String name) {
        this.title = name;
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public TidalAlbum getAlbum() {
        return album;
    }

    public void setAlbum(TidalAlbum album) {
        this.album = album;
    }
}
