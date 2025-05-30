package com.cPlace.chzzk.auth;

import com.cPlace.chzzk.domain.ChzzkMember;
import com.cPlace.chzzk.exception.ChzzkException;
import com.cPlace.chzzk.service.ChzzkService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import static com.cPlace.chzzk.exception.ChzzkExceptionCode.COOKIE_NOT_FOUND;

@Component
@RequiredArgsConstructor
public class AuthInterceptor implements HandlerInterceptor {

    private final ChzzkService chzzkService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if (request.getMethod().equals("OPTIONS")) return true;

        String channelId = null;
        String accessToken = null;
        if (request.getCookies() == null) {
            throw new ChzzkException(COOKIE_NOT_FOUND);
        }

        for (Cookie cookie : request.getCookies()) {
            if (cookie.getName().equals("channelId")) {
                channelId = cookie.getValue();
            }
            if (cookie.getName().equals("accessToken")) {
                accessToken = cookie.getValue();
            }
        }

        if (channelId == null || accessToken == null) {
            throw new ChzzkException(COOKIE_NOT_FOUND);
        }

        ChzzkMember member = chzzkService.findMember(channelId, accessToken);
        if (member.isAdmin()) {
            request.setAttribute("admin", member);
        }

        request.setAttribute("member", member);
        return true;
    }
}
