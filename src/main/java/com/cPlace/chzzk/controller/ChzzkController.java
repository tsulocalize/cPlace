package com.cPlace.chzzk.controller;

import com.cPlace.chzzk.dto.LoginCookies;
import com.cPlace.chzzk.dto.LoginRequest;
import com.cPlace.chzzk.service.ChzzkService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Log4j2
public class ChzzkController {

    private final ChzzkService chzzkService;

    @PostMapping("/token-login")
    public ResponseEntity<Void> tokenLogin(@CookieValue(value = "channelId") String channelId,
                                      @CookieValue(value = "accessToken", required = false, defaultValue = "") String accessToken,
                                      @CookieValue(value = "refreshToken") String refreshToken) {
        LoginCookies cookies = chzzkService.tokenLogin(channelId, accessToken, refreshToken);

        return ResponseEntity.ok()
                .header("Set-Cookie", cookies.getChannelId().toString())
                .header("Set-Cookie", cookies.getAccessToken().toString())
                .header("Set-Cookie", cookies.getRefreshToken().toString())
                .build();
    }

    @PostMapping("/login")
    public ResponseEntity<Void> login(@RequestBody LoginRequest loginRequest) {
        LoginCookies cookies = chzzkService.newLogin(loginRequest.code(), loginRequest.state());

        return ResponseEntity.ok()
                .header("Set-Cookie", cookies.getChannelId().toString())
                .header("Set-Cookie", cookies.getAccessToken().toString())
                .header("Set-Cookie", cookies.getRefreshToken().toString())
                .build();
    }
}
