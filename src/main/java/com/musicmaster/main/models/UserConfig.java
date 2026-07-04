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
    private String spotifyAuthState;

    private String tidalUserId;
    @Column(length = 500)
    private String tidalAuthCode;
    @Column(columnDefinition = "TIMESTAMP")
    private LocalDateTime tidalTokenExpiration;
    private String tidalRefreshToken;
    private String tidalChallengeCode;
    private String tidalChallengeVerifier;
    private String tidalAuthState;

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

    public String getTidalUserId() {
        return tidalUserId;
    }

    public void setTidalUserId(String tidalUserId) {
        this.tidalUserId = tidalUserId;
    }

    public String getTidalAuthCode() {
        return tidalAuthCode;
    }

    public void setTidalAuthCode(String tidalAuthCode) {
        this.tidalAuthCode = tidalAuthCode;
    }

    public LocalDateTime getTidalTokenExpiration() {
        return tidalTokenExpiration;
    }

    public void setTidalTokenExpiration(LocalDateTime tidalTokenExpiration) {
        this.tidalTokenExpiration = tidalTokenExpiration;
    }

    public String getTidalRefreshToken() {
        return tidalRefreshToken;
    }

    public void setTidalRefreshToken(String tidalRefreshToken) {
        this.tidalRefreshToken = tidalRefreshToken;
    }

    public String getSpotifyAuthState() {
        return spotifyAuthState;
    }

    public void setSpotifyAuthState(String spotifyAuthState) {
        this.spotifyAuthState = spotifyAuthState;
    }

    public String getTidalChallengeCode() {
        return tidalChallengeCode;
    }

    public void setTidalChallengeCode(String tidalChallengeCode) {
        this.tidalChallengeCode = tidalChallengeCode;
    }

    public String getTidalChallengeVerifier() {
        return tidalChallengeVerifier;
    }

    public void setTidalChallengeVerifier(String tidalChallengeVerifier) {
        this.tidalChallengeVerifier = tidalChallengeVerifier;
    }

    public String getTidalAuthState() {
        return tidalAuthState;
    }

    public void setTidalAuthState(String tidalAuthState) {
        this.tidalAuthState = tidalAuthState;
    }
}
