package com.musicmaster.main.pojo;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.musicmaster.main.models.SpotifySong;

import java.util.List;
import java.util.Map;

public class SpotifySearchResponse {
//    @JsonDeserialize(contentAs = SpotifySearchResponseTracks.class)
    private SpotifySearchResponseTracks tracks;

    public SpotifySearchResponseTracks getTracks() {
        return tracks;
    }

    public void setTracks(SpotifySearchResponseTracks tracks) {
        this.tracks = tracks;
    }
}
