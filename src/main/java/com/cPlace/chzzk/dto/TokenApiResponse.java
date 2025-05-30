package com.cPlace.chzzk.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class TokenApiResponse {

    private final String accessToken; // 1 day
    private final String refreshToken; // 30 days
    private final String tokenType;
    private final int expiresIn;
}
