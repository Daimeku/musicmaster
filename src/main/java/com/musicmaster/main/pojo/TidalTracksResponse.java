package com.musicmaster.main.pojo;

import com.musicmaster.main.models.TidalSong;

import java.util.List;

public class TidalTracksResponse {

    private int limit;
    private int offset;
    private int totalNumberOfItems;
    private List<TidalSong> items;

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public int getTotalNumberOfItems() {
        return totalNumberOfItems;
    }

    public void setTotalNumberOfItems(int totalNumberOfItems) {
        this.totalNumberOfItems = totalNumberOfItems;
    }

    public List<TidalSong> getItems() {
        return items;
    }

    public void setItems(List<TidalSong> items) {
        this.items = items;
    }
}
