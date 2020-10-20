package com.musicmaster.main.pojo;

import com.musicmaster.main.models.SpotifyPlaylist;

import java.util.List;

public class SpotifyPlaylistResponse {

    private String href;
    private List<SpotifyPlaylist> items;

    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }

    public List<SpotifyPlaylist> getItems() {
        return items;
    }

    public void setItems(List<SpotifyPlaylist> items) {
        this.items = items;
    }
}
