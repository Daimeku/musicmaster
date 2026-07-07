package com.musicmaster.main.pojo;

import java.util.List;

public class TidalTracksResponse {
    List<TidalTrack> data;

    public List<TidalTrack> getData() {
        return data;
    }

    public void setData(List<TidalTrack> data) {
        this.data = data;
    }
}
