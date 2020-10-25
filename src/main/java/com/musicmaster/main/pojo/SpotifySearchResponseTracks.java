package com.musicmaster.main.pojo;

import com.musicmaster.main.models.SpotifySong;

import java.util.List;

public class SpotifySearchResponseTracks {

    private int total;
    private String href;
    private List<SpotifySong> items;

    public List<SpotifySong> getItems() {
        return items;
    }

    public void setItems(List<SpotifySong> items) {
        this.items = items;
    }

    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }
}
