package com.musicmaster.main.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SpotifyPlaylistRequest {

    private String name;
    private String description;
    @JsonProperty(value = "public")
    private boolean isPublic;
    private boolean collaborative;

    public SpotifyPlaylistRequest() {}

    public SpotifyPlaylistRequest(String name) {
        this.name = name;
        this.description = description;
        this.isPublic = isPublic;
        this.collaborative = collaborative;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isPublic() {
        return isPublic;
    }

    public void setPublic(boolean aPublic) {
        isPublic = aPublic;
    }

    public boolean isCollaborative() {
        return collaborative;
    }

    public void setCollaborative(boolean collaborative) {
        this.collaborative = collaborative;
    }
}
