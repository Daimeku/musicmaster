package com.musicmaster.main.pojo;

public class TidalLinks {
    private String self;
    private String next;
    private TidalLinksMeta meta;

    public String getSelf() {
        return self;
    }

    public void setSelf(String self) {
        this.self = self;
    }

    public String getNext() {
        return next;
    }

    public void setNext(String next) {
        this.next = next;
    }

    public TidalLinksMeta getMeta() {
        return meta;
    }

    public void setMeta(TidalLinksMeta meta) {
        this.meta = meta;
    }
}

