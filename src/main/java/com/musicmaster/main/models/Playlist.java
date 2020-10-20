package com.musicmaster.main.models;

import java.util.List;

public interface Playlist {

    String getName();
    void setName(String name);
    List<Song> getSongs();
    void setSongs(List<Song> songs);
    String getSource();
    String getDescription();
    void setDescription(String description);
//    void setSource();
//    int size();
}
