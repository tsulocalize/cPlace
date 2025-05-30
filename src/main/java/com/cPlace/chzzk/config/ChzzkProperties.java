package com.cPlace.chzzk.config;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

@Component
@RequiredArgsConstructor
@Getter
public class ChzzkProperties {

    private static final String authorizationCodeUrl = "https://chzzk.naver.com/account-interlock";

    private final ChzzkRestClient chzzkRestClient;

    @Value("${chzzk.client-id}")
    private String clientId;

    @Value("${chzzk.client-secret}")
    private String clientSecret;

    @Value("${chzzk.redirect-uri}")
    private String redirectUri;

    private String code; // authorization_code

    private String state;


    @PostConstruct
    private void post() {
        getAuthorizationCode();
    }

    private void getAuthorizationCode() {
        String uri = UriComponentsBuilder.fromUriString(authorizationCodeUrl)
                .queryParam("clientId", clientId)
                .queryParam("redirectUri",redirectUri)
                .toUriString();

        JsonObject response = JsonParser.parseString(chzzkRestClient.get(uri))
                .getAsJsonObject();

        this.code = response
                .getAsJsonObject("content")
                .getAsJsonObject("code")
                .getAsString();
        this.state = response
                .getAsJsonObject("content")
                .getAsJsonObject("state")
                .getAsString();
    }
}
