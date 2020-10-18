package com.musicmaster.main.models;

import java.util.List;

public interface MusicSource {

    void authenticate(User user);
    boolean createPlaylist(Playlist playlist);
    List<Playlist> getPlaylists();
}
