package com.cPlace.chzzk.service;

import com.cPlace.chzzk.domain.ChzzkMember;
import com.cPlace.chzzk.dto.ChannelInfoApiResponse;
import com.cPlace.chzzk.dto.LoginCookies;
import com.cPlace.chzzk.dto.TokenApiResponse;
import com.cPlace.chzzk.exception.ChzzkException;
import com.cPlace.chzzk.exception.ChzzkExceptionCode;
import com.cPlace.chzzk.respository.ChzzkMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ChzzkService {

    private final ChzzkApiService chzzkApiService;
    private final ChzzkMemberRepository chzzkMemberRepository;

    @Value("${chzzk.http-only}")
    private boolean httpOnly;

    @Transactional
    public LoginCookies newLogin(String code, String state) {
        TokenApiResponse token = chzzkApiService.getToken(code, state);
        ChannelInfoApiResponse channelInfo = chzzkApiService.getChannelInfo(token.getAccessToken());

        ChzzkMember chzzkMember = chzzkMemberRepository.findByChannelId(channelInfo.getChannelId())
                .orElseGet(() -> registerMember(
                        channelInfo.getChannelId(),
                        channelInfo.getChannelName(),
                        token.getAccessToken(),
                        token.getRefreshToken()));
        chzzkMember.updateToken(token.getAccessToken(), token.getRefreshToken());

        return LoginCookies.of(channelInfo.getChannelId(), token.getAccessToken(), token.getRefreshToken(), httpOnly);
    }

    @Transactional
    public LoginCookies tokenLogin(String channelId, String accessToken, String refreshToken) {
        ChzzkMember chzzkMember = chzzkMemberRepository.findByChannelId(channelId)
                .orElseThrow(() -> new ChzzkException(ChzzkExceptionCode.MEMBER_NOT_EXIST));

        // 멤버의 accessToken 이 잘못되거나 만료된 경우
        if (!chzzkMember.isAccessTokenValid(accessToken)) {
            TokenApiResponse response = chzzkApiService.renewalAccessToken(refreshToken);
            chzzkMember.updateToken(response.getAccessToken(), response.getRefreshToken());
            return LoginCookies.of(channelId, response.getAccessToken(), response.getRefreshToken(), httpOnly);
        }

        return LoginCookies.of(channelId, accessToken, refreshToken, httpOnly);
    }

    @Transactional(readOnly = true)
    public ChzzkMember findMember(String channelId, String accessToken) {
        return chzzkMemberRepository.findByChannelIdAndAccessToken(channelId, accessToken)
                .orElseThrow(() -> new ChzzkException(ChzzkExceptionCode.MEMBER_NOT_EXIST));
    }

    private ChzzkMember registerMember(String channelId, String channelName, String accessToken, String refreshToken) {
        if (chzzkMemberRepository.existsByChannelId(channelId)) {
            throw new ChzzkException(ChzzkExceptionCode.MEMBER_ALREADY_EXIST);
        }

        ChzzkMember newMember = new ChzzkMember(channelId, channelName, accessToken, refreshToken);
        return chzzkMemberRepository.save(newMember);
    }
}
