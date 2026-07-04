package com.musicmaster.main.services;

import com.musicmaster.main.clients.SpotifyMusicSource;
import com.musicmaster.main.helpers.UriHelper;
import com.musicmaster.main.models.UserConfig;
import com.musicmaster.main.pojo.SpotifyProfileDetails;
import com.musicmaster.main.pojo.SpotifyTokenResponse;
import com.musicmaster.main.repositories.UserConfigRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class AuthService {

    private static Logger logger = LoggerFactory.getLogger(AuthService.class);

    private UserConfigRepository userConfigRepository;

    private SpotifyMusicSource spotifyMusicSource;

    private UriHelper uriHelper;

    public AuthService() {

    }

    @Autowired
    public AuthService(UserConfigRepository userConfigRepository, SpotifyMusicSource spotifyMusicSource) {
        this.userConfigRepository = userConfigRepository;
        this.spotifyMusicSource = spotifyMusicSource;
    }

    public void loadSpotifyAuthToken(String authCode) {
        SpotifyTokenResponse spotifyResponse = spotifyMusicSource.getToken(authCode);
        //update the config details
        UserConfig config = userConfigRepository.getOne(1);
        config.setSpotifyToken(spotifyResponse.getAccessToken());
        config.setSpotifyRefreshToken(spotifyResponse.getRefreshToken());
        config.setSpotifyTokenExpiration( LocalDateTime.now().plusSeconds(spotifyResponse.getExpiresIn()) );
        config = userConfigRepository.save(config);
        SpotifyProfileDetails profileDetails = spotifyMusicSource.getProfileDetails();
        config.setSpotifyUserId(profileDetails.getId());
        userConfigRepository.save(config);
        logger.info("successfully saved spotify auth details");
    }

    public void updateTidalAuthDetails(String authCode) {

    }

    public String getSpotifyRedirectUri() {
        String state = UUID.randomUUID().toString();
        saveSpotifyAuthState(state);
        return uriHelper.buildSpotifyAuthUri(state);
    }

    public String getTidalRedirectUri() {
        String state = UUID.randomUUID().toString();
        String codeVerifier = UUID.randomUUID().toString();
        String codeChallenge = sha256(codeVerifier);
        // base 64 encode?

//        String encodedVerifierCode = Base64.getEncoder().encodeToString(verifierUUID.getBytes());
        saveTidalAuthState(state, codeChallenge, codeVerifier);
        return uriHelper.buildTidalAuthUri(state);
    }

    private void saveSpotifyAuthState(String state) {
        UserConfig config = userConfigRepository.getOne(1);
        config.setSpotifyAuthState(state);
        userConfigRepository.save(config);
    }

    private void saveTidalAuthState(String state, String codeChallenge, String codeVerifier) {
        UserConfig config = userConfigRepository.getOne(1);
        config.setTidalAuthState(state);
        config.setTidalChallengeCode(codeChallenge);
        config.setTidalChallengeVerifier(codeVerifier);
        userConfigRepository.save(config);
    }

    private String sha256(String input) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
            byte[] encoded = messageDigest.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder encodedString = new StringBuilder();
            for (byte b : encoded) {
                String hexVal = Integer.toHexString(0xff * b);
                if (hexVal.length() == 1) {
                    encodedString.append('0');
                }
                encodedString.append(hexVal);
            }
            return encodedString.toString();
        } catch (NoSuchAlgorithmException ex) {
            throw new RuntimeException("no sha256 algorithm detected");
        }
    }
}
