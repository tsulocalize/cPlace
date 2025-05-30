package com.cPlace.admin.controller;

import com.cPlace.admin.dto.PatchMemberRequest;
import com.cPlace.chzzk.domain.ChzzkMember;
import com.cPlace.chzzk.dto.LoginCookies;
import com.cPlace.fixture.LayerTestSupport;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.http.Cookie;
import io.restassured.http.Cookies;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.web.server.LocalServerPort;

import static com.cPlace.fixture.Fixtures.CHZZK_ADMIN;
import static com.cPlace.fixture.Fixtures.CHZZK_MEMBER;

class AdminControllerTest extends LayerTestSupport {

    @LocalServerPort
    int port;

    private Cookies adminCookies;
    private PatchMemberRequest request;

    @BeforeEach
    void setCookie() {
        setAdminCookies(CHZZK_ADMIN);
        RestAssured.port = port;
        request = PatchMemberRequest.of(CHZZK_MEMBER.getChannelName(), true);
    }

    @Test
    @DisplayName("어드민은 멤버의 상태를 바꿀 수 있다.")
    void changeMemberState() {
        RestAssured.given()
                .cookies(adminCookies)
                .body(request)
                .contentType(ContentType.JSON)
                .when().patch("/admin/member")
                .then()
                .statusCode(204);
    }

    @Test
    @DisplayName("어드민이 아니면 멤버의 상태를 바꿀 수 없다.")
    void changeMemberState_notAdmin() {
        setAdminCookies(CHZZK_MEMBER);

        RestAssured.given()
                .cookies(adminCookies)
                .body(request)
                .contentType(ContentType.JSON)
                .when().patch("/admin/member")
                .then()
                .statusCode(401);
    }

    private void setAdminCookies(ChzzkMember member) {
        LoginCookies loginCookies = LoginCookies.of(member.getChannelId(), member.getAccessToken(), member.getRefreshToken(), false);

        Cookie channelId = new Cookie.Builder(
                loginCookies.getChannelId().getName(),
                loginCookies.getChannelId().getValue())
                .build();
        Cookie accessToken = new Cookie.Builder(
                loginCookies.getAccessToken().getName(),
                loginCookies.getAccessToken().getValue())
                .build();
        Cookie refreshToken = new Cookie.Builder(
                loginCookies.getRefreshToken().getName(),
                loginCookies.getRefreshToken().getValue())
                .build();
        adminCookies = Cookies.cookies(channelId, accessToken, refreshToken);
    }
}
