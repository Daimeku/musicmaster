package com.musicmaster.main.models;

import org.apache.tomcat.jni.Local;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Date;

@Entity
public class UserConfig {

    @Id
    @GeneratedValue
    private int id;

    @Column(length = 500)
    private String spotifyAuthCode;

    private String spotifyToken;

    @Column(columnDefinition = "TIMESTAMP")
    private LocalDateTime spotifyTokenExpiration;

    private String spotifyRefreshToken;

    private String spotifyUserId;

    public UserConfig() {}
    public UserConfig(String spotifyAuthCode) {
        this.spotifyAuthCode = spotifyAuthCode;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSpotifyAuthCode() {
        return spotifyAuthCode;
    }

    public void setSpotifyAuthCode(String spotifyAuthCode) {
        this.spotifyAuthCode = spotifyAuthCode;
    }

    public String getSpotifyToken() {
        return spotifyToken;
    }

    public void setSpotifyToken(String spotifyToken) {
        this.spotifyToken = spotifyToken;
    }

    public LocalDateTime getSpotifyTokenExpiration() {
        return spotifyTokenExpiration;
    }

    public void setSpotifyTokenExpiration(LocalDateTime spotifyTokenExpiration) {
        this.spotifyTokenExpiration = spotifyTokenExpiration;
    }

    public String getSpotifyRefreshToken() {
        return spotifyRefreshToken;
    }

    public void setSpotifyRefreshToken(String spotifyRefreshToken) {
        this.spotifyRefreshToken = spotifyRefreshToken;
    }

    public String getSpotifyUserId() {
        return spotifyUserId;
    }

    public void setSpotifyUserId(String spotifyUserId) {
        this.spotifyUserId = spotifyUserId;
    }
}
