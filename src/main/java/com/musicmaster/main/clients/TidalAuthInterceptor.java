package com.musicmaster.main.clients;

import com.musicmaster.main.models.UserConfig;
import com.musicmaster.main.pojo.TidalTokenResponse;
import com.musicmaster.main.repositories.UserConfigRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;

@Component
public class TidalAuthInterceptor implements ClientHttpRequestInterceptor {
    private final UserConfigRepository userConfigRepository;
    private final TidalAuthClient tidalAuthClient;

    @Autowired
    public TidalAuthInterceptor(UserConfigRepository userConfigRepository, TidalAuthClient tidalAuthClient) {
        this.userConfigRepository = userConfigRepository;
        this.tidalAuthClient = tidalAuthClient;
    }

    @Override
    public ClientHttpResponse intercept(HttpRequest httpRequest, byte[] bytes, ClientHttpRequestExecution clientHttpRequestExecution) throws IOException {
        String authToken = getValidAuthToken();
        HttpHeaders headers = httpRequest.getHeaders();
        headers.add("Authorization", "Bearer " + authToken);
        return clientHttpRequestExecution.execute(httpRequest, bytes);
    }

    private String getValidAuthToken() {
        UserConfig userConfig = userConfigRepository.getOne(1);
        if (isTokenExpired(userConfig)) {
            TidalTokenResponse tokenResponse = tidalAuthClient.getRefreshToken(userConfig.getTidalRefreshToken());
            userConfig.setTidalToken(tokenResponse.getAccessToken());
            userConfig.updateTidalTokenExpiration(tokenResponse.getExpiresIn());
            userConfigRepository.save(userConfig);
        }
        return userConfig.getTidalToken();
    }

    private boolean isTokenExpired(UserConfig userConfig) {
        return userConfig.getTidalTokenExpiration().isBefore(LocalDateTime.now().minusSeconds(20));
    }
}
