package com.musicmaster.main.pojo;

import java.util.List;

public class TidalPlaylistTracksResponse {
    private List<TidalPlaylistData> data;
    private TidalLinks links;

    public List<TidalPlaylistData> getData() {
        return data;
    }

    public void setData(List<TidalPlaylistData> data) {
        this.data = data;
    }

    public TidalLinks getLinks() {
        return links;
    }

    public void setLinks(TidalLinks links) {
        this.links = links;
    }
}
