package com.cPlace.chzzk.dto;

import lombok.Getter;
import org.springframework.http.ResponseCookie;

@Getter
public class LoginCookies {

    private final ResponseCookie channelId;
    private final ResponseCookie accessToken;
    private final ResponseCookie refreshToken;

    public LoginCookies(String channelId, String accessToken, String refreshToken, boolean httpOnly) {
        this.channelId = ResponseCookie.from("channelId", channelId)
                .path("/")
                .httpOnly(httpOnly)
                .sameSite("None")
                .secure(true)
                .maxAge(30 * 24 * 60 * 60L) // 30 days
                .build();

        this.accessToken = ResponseCookie.from("accessToken", accessToken)
                .path("/")
                .httpOnly(httpOnly)
                .sameSite("None")
                .secure(true)
                .maxAge(24 * 60 * 60L) // 1 day
                .build();

        this.refreshToken = ResponseCookie.from("refreshToken", refreshToken)
                .path("/")
                .httpOnly(httpOnly)
                .sameSite("None")
                .secure(true)
                .maxAge(30 * 24 * 60 * 60L) // 30 days
                .build();

    }

    public static LoginCookies of(String channelId, String accessToken, String refreshToken, boolean httpOnly) {
        return new LoginCookies(channelId, accessToken, refreshToken, httpOnly);
    }
}
