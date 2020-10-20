package com.musicmaster.main.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.musicmaster.main.models.SpotifySong;

public class SpotifyPlaylistItem {

    private SpotifySong track;

    @JsonProperty(value = "added_at")
    private String addedAt;

    public SpotifySong getTrack() {
        return track;
    }

    public void setTrack(SpotifySong track) {
        this.track = track;
    }

    public String getAddedAt() {
        return addedAt;
    }

    public void setAddedAt(String addedAt) {
        this.addedAt = addedAt;
    }
}
