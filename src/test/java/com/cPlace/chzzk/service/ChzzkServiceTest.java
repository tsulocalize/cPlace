package com.cPlace.chzzk.service;

import com.cPlace.chzzk.config.ChzzkRestClient;
import com.cPlace.chzzk.dto.LoginCookies;
import com.cPlace.chzzk.exception.ChzzkException;
import com.cPlace.chzzk.exception.ChzzkExceptionCode;
import com.cPlace.chzzk.respository.ChzzkMemberRepository;
import com.cPlace.fixture.LayerTestSupport;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class ChzzkServiceTest extends LayerTestSupport {

    private static final String tokenResponse = """
            {
                "code":200,
                "message":null,
                "content":{"accessToken":"access","refreshToken":"refresh","tokenType":"Bearer","expiresIn":3600}
            }
            """;
    private static final String channelInfoResponse = """
            {
                "code":200,
                "message":null,
                "content":{"channelId":"ch_id","channelName":"ch_name"}
            }
            """;

    @Autowired
    ChzzkService chzzkService;
    @Autowired
    ChzzkMemberRepository chzzkMemberRepository;
    @MockitoBean
    ChzzkRestClient chzzkRestClient;

    @BeforeEach
    void mock() {
        when(chzzkRestClient.post(any(), any()))
                .thenReturn(tokenResponse);
        when(chzzkRestClient.getWithToken(any(), any(), any()))
                .thenReturn(channelInfoResponse);
    }

    @Test
    @DisplayName("첫 로그인 시 멤버로 등록된다")
    void newLogin() {
        // when
        LoginCookies cookies = chzzkService.newLogin("code", "state");

        // then
        assertThat(chzzkMemberRepository.findByChannelId(cookies.getChannelId().getValue()))
                .isPresent();
    }

    @Test
    @DisplayName("이미 멤버라면 토큰이 만료됐을 때, 새 로그인 할 수 있다")
    void login_expiredToken() {
        // given
        chzzkService.newLogin("code", "state");

        // when & then
        assertThatCode(() -> chzzkService.newLogin("code", "state"))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("이미 멤버라면 토큰으로 로그인 할 수 있다")
    void tokenLogin() {
        // given
        LoginCookies registerCookies = chzzkService.newLogin("code", "state");

        // when
        LoginCookies tokenLoginCookies = chzzkService.tokenLogin(
                registerCookies.getChannelId().getValue(),
                registerCookies.getAccessToken().getValue(),
                registerCookies.getRefreshToken().getValue());

        // then
        assertThat(registerCookies.getChannelId()).isEqualTo(tokenLoginCookies.getChannelId());
    }

    @Test
    @DisplayName("등록된 멤버가 아니라면 토큰으로 로그인 할 수 없다")
    void tokenLogin_notMember() {
        // when & then
        assertThatThrownBy(() ->
                chzzkService.tokenLogin("invalid-channel-id", "access", "refresh"))
                .isInstanceOf(ChzzkException.class)
                .hasMessage(ChzzkExceptionCode.MEMBER_NOT_EXIST.getMessage());
    }
}
