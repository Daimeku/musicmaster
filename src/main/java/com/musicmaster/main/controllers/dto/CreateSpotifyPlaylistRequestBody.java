package com.musicmaster.main.controllers.dto;

public class CreateSpotifyPlaylistRequestBody {

    private String tidalPlaylistId;

    private String spotifyPlaylistName;

    public CreateSpotifyPlaylistRequestBody(final String tidalPlaylistId,
                                            final String spotifyPlaylistName) {
        this.tidalPlaylistId = tidalPlaylistId;
        this.spotifyPlaylistName = spotifyPlaylistName;
    }

    public String getTidalPlaylistId() {
        return tidalPlaylistId;
    }

    public void setTidalPlaylistId(String tidalPlaylistId) {
        this.tidalPlaylistId = tidalPlaylistId;
    }

    public String getSpotifyPlaylistName() {
        return spotifyPlaylistName;
    }

    public void setSpotifyPlaylistName(String spotifyPlaylistName) {
        this.spotifyPlaylistName = spotifyPlaylistName;
    }


}
