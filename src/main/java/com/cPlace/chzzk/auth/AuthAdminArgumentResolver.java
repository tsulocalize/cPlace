package com.cPlace.chzzk.auth;

import com.cPlace.chzzk.exception.ChzzkException;
import com.cPlace.chzzk.exception.ChzzkExceptionCode;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@Component
public class AuthAdminArgumentResolver implements HandlerMethodArgumentResolver {
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(AuthAdmin.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter,
                                  ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest,
                                  WebDataBinderFactory binderFactory) {
        HttpServletRequest nativeRequest = (HttpServletRequest) webRequest.getNativeRequest();
        Object admin = nativeRequest.getAttribute("admin");
        if (admin == null) {
            throw new ChzzkException(ChzzkExceptionCode.UNAUTHORIZED);
        }

        return admin;
    }
}
