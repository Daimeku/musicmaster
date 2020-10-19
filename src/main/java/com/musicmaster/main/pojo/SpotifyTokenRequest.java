package com.musicmaster.main.pojo;

public class SpotifyTokenRequest {

    private static String grantType = "authorization_code";
    private String code;
    private String redirectUri;

    public String getGrantType() {
        return grantType;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getRedirectUri() {
        return redirectUri;
    }

    public void setRedirectUri(String redirectUri) {
        this.redirectUri = redirectUri;
    }
}
