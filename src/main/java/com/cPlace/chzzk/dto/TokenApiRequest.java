package com.cPlace.chzzk.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class TokenApiRequest {
    private final String grantType = "authorization_code";
    private final String clientId;
    private final String clientSecret;
    private final String code;
    private final String state;
}
