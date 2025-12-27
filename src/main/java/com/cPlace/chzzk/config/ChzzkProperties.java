package com.cPlace.chzzk.config;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Getter
public class ChzzkProperties {
    @Value("${chzzk.client-id}")
    private String clientId;
    @Value("${chzzk.client-secret}")
    private String clientSecret;
    @Value("${chzzk.redirect-uri}")
    private String redirectUri;
}
