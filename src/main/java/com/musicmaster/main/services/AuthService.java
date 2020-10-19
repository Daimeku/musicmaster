package com.musicmaster.main.services;

import com.musicmaster.main.clients.SpotifyMusicSource;
import com.musicmaster.main.models.UserConfig;
import com.musicmaster.main.pojo.SpotifyProfileDetails;
import com.musicmaster.main.pojo.SpotifyTokenResponse;
import com.musicmaster.main.repositories.UserConfigRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class AuthService {

    private static Logger logger = LoggerFactory.getLogger(AuthService.class);

    private UserConfigRepository userConfigRepository;

    private SpotifyMusicSource spotifyMusicSource;

    public AuthService() {

    }

    @Autowired
    public AuthService(UserConfigRepository userConfigRepository, SpotifyMusicSource spotifyMusicSource) {
        this.userConfigRepository = userConfigRepository;
        this.spotifyMusicSource = spotifyMusicSource;
    }

    public String updateSpotifyAuthDetails(String authCode) {
        SpotifyTokenResponse spotifyResponse = spotifyMusicSource.getToken(authCode);
        //update the config details
        UserConfig config = userConfigRepository.findById(1).orElse(new UserConfig(authCode));
        config.setSpotifyToken(spotifyResponse.getAccessToken());
        config.setSpotifyRefreshToken(spotifyResponse.getRefreshToken());
        config.setSpotifyTokenExpiration( LocalDateTime.now().plusSeconds(spotifyResponse.getExpiresIn()) );
        config = userConfigRepository.save(config);
        SpotifyProfileDetails profileDetails = spotifyMusicSource.getProfileDetails();
        config.setSpotifyUserId(profileDetails.getId());
        userConfigRepository.save(config);
        logger.info("successfully saved spotify auth details");
        return "successfully updated credentials";
    }
}
