package com.cPlace.chzzk.service;

import com.cPlace.chzzk.config.ChzzkProperties;
import com.cPlace.chzzk.config.ChzzkRestClient;
import com.cPlace.chzzk.dto.ChannelInfoApiResponse;
import com.cPlace.chzzk.dto.TokenApiRequest;
import com.cPlace.chzzk.dto.TokenApiResponse;
import com.cPlace.chzzk.dto.TokenRenewalApiRequest;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChzzkApiService {

    private static final String TOKEN_REQUEST_URL = "https://chzzk.naver.com/auth/v1/token";
    private static final String USER_SEARCH_URL = "https://openapi.chzzk.naver.com/open/v1/users/me";
    private static final Gson gson = new Gson();

    private final ChzzkRestClient chzzkRestClient;
    private final ChzzkProperties chzzkProperties;

    public TokenApiResponse getToken(String code, String state) {
        TokenApiRequest request = new TokenApiRequest(
                chzzkProperties.getClientId(),
                chzzkProperties.getClientSecret(),
                code,
                state);

        String response = chzzkRestClient.post(TOKEN_REQUEST_URL, request);
        return convert(response, TokenApiResponse.class);
    }

    public TokenApiResponse renewalAccessToken(String refreshToken) {
        TokenRenewalApiRequest request = new TokenRenewalApiRequest(
                chzzkProperties.getClientId(),
                chzzkProperties.getClientSecret(),
                refreshToken);

        String response = chzzkRestClient.post(TOKEN_REQUEST_URL, request);
        return convert(response, TokenApiResponse.class);
    }

    public ChannelInfoApiResponse getChannelInfo(String accessToken) {
        String response = chzzkRestClient.getWithToken(USER_SEARCH_URL, "Authorization", accessToken);
        return convert(response, ChannelInfoApiResponse.class);
    }

    private <T> T convert(String response, Class<T> tClass) {
        JsonObject content = JsonParser.parseString(response)
                .getAsJsonObject()
                .getAsJsonObject("content");

        return gson.fromJson(content, tClass);
    }
}
