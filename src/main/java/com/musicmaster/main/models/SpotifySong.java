package com.musicmaster.main.models;

public class SpotifySong extends Song {
    private String id;

    public SpotifySong(){}

    public SpotifySong(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

}
