package com.musicmaster.main.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SpotifyProfileDetails {

    private String id;
    @JsonProperty(value = "display_name")
    private String displayName;
    private String email;
    private String country;
    private String uri;

    public SpotifyProfileDetails() {}

    public SpotifyProfileDetails(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }
}
