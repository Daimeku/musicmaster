package com.musicmaster.main.services;

import com.musicmaster.main.clients.SpotifyMusicSource;
import com.musicmaster.main.clients.TidalMusicSource;
import com.musicmaster.main.helpers.UriHelper;
import com.musicmaster.main.models.UserConfig;
import com.musicmaster.main.pojo.SpotifyProfileDetails;
import com.musicmaster.main.pojo.SpotifyTokenResponse;
import com.musicmaster.main.pojo.TidalTokenResponse;
import com.musicmaster.main.repositories.UserConfigRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.UUID;

@Service
public class AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    private final UserConfigRepository userConfigRepository;

    private final SpotifyMusicSource spotifyMusicSource;

    private final TidalMusicSource tidalMusicSource;

    private final UriHelper uriHelper;

    @Autowired
    public AuthService(
            UserConfigRepository userConfigRepository,
            SpotifyMusicSource spotifyMusicSource,
            TidalMusicSource tidalMusicSource,
            UriHelper uriHelper
    ) {
        this.userConfigRepository = userConfigRepository;
        this.spotifyMusicSource = spotifyMusicSource;
        this.tidalMusicSource = tidalMusicSource;
        this.uriHelper = uriHelper;
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
        logger.info("successfully saved Spotify auth details");
    }

    public void loadTidalAuthToken(String authCode) {
        UserConfig config = userConfigRepository.getOne(1);
        TidalTokenResponse tokenResponse = tidalMusicSource.getToken(authCode, config.getTidalChallengeVerifier());

        config.setTidalToken(tokenResponse.getAccessToken());
        config.setTidalRefreshToken(tokenResponse.getRefreshToken());
        config.setTidalTokenExpiration(LocalDateTime.now().plusSeconds(tokenResponse.getExpiresIn()));
        userConfigRepository.save(config);
    }

    public String getSpotifyRedirectUri() {
        String state = UUID.randomUUID().toString();
        saveSpotifyAuthState(state);
        return uriHelper.buildSpotifyAuthUri(state);
    }

    public String getTidalRedirectUri() {
        String state = UUID.randomUUID().toString();
        SecureRandom random = new SecureRandom();
        byte[] codeVerifierBytes = new byte[64];
        random.nextBytes(codeVerifierBytes);
        String codeVerifier = Base64.getEncoder().withoutPadding().encodeToString(codeVerifierBytes);
        String codeChallenge = buildCodeChallenge(codeVerifier);

        saveTidalAuthState(state, codeChallenge, codeVerifier);

        return uriHelper.buildTidalAuthUri(state, codeChallenge);
    }

    private void saveSpotifyAuthState(String state) {
        UserConfig config = userConfigRepository.findById(1).orElse(new UserConfig());
        config.setSpotifyAuthState(state);
        userConfigRepository.save(config);
    }

    private void saveTidalAuthState(String state, String codeChallenge, String codeVerifier) {
        UserConfig config = userConfigRepository.findById(1).orElse(new UserConfig());
        config.setTidalAuthState(state);
        config.setTidalChallengeCode(codeChallenge);
        config.setTidalChallengeVerifier(codeVerifier);
        userConfigRepository.save(config);
    }

    private String buildCodeChallenge(String codeVerifier) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
            byte[] encoded = messageDigest.digest(codeVerifier.getBytes());
            return Base64.getEncoder().withoutPadding().encodeToString(encoded);
        } catch (NoSuchAlgorithmException ex) {
            throw new RuntimeException("no sha256 algorithm detected");
        }
    }
}
