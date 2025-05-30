package com.cPlace.chzzk.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class TokenRenewalApiRequest {
    private final String grantType = "authorization_code";
    private final String clientId;
    private final String clientSecret;
    private final String refreshToken;
}
