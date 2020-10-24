package com.musicmaster.main.models;

import java.util.List;

public interface MusicSource {

    boolean createPlaylist(Playlist playlist);
    List<Playlist> getPlaylists();
}
