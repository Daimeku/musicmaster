package com.musicmaster.main.pojo;

import com.musicmaster.main.models.SpotifyPlaylist;

import java.util.List;

public class SpotifyPlaylistTracksResponse {

    private String href;
    private List<SpotifyPlaylistItem> items;

    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }

    public List<SpotifyPlaylistItem> getItems() {
        return items;
    }

    public void setItems(List<SpotifyPlaylistItem> items) {
        this.items = items;
    }
}
