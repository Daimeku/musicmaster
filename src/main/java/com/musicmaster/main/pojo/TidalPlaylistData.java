package com.musicmaster.main.pojo;

public class TidalPlaylistData {
    private String id;
    private String type;
    private TidalPlaylistDataMeta meta;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public TidalPlaylistDataMeta getMeta() {
        return meta;
    }

    public void setMeta(TidalPlaylistDataMeta meta) {
        this.meta = meta;
    }
}

