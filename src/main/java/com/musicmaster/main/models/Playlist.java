package com.musicmaster.main.models;

import java.util.List;

public interface Playlist {

    String getName();
    void setName(String name);
    List<Song> getSongs();
    void setSongs(List<Song> songs);
    String getSource();
    void setSource();
    int size();
}
