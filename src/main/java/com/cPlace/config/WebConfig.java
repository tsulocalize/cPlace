package com.cPlace.config;

import com.cPlace.chzzk.auth.AuthAdminArgumentResolver;
import com.cPlace.chzzk.auth.AuthInterceptor;
import com.cPlace.chzzk.auth.AuthMemberArgumentResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import java.util.List;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final AuthInterceptor authInterceptor;
    private final AuthMemberArgumentResolver authMemberArgumentResolver;
    private final AuthAdminArgumentResolver authAdminArgumentResolver;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authInterceptor)
                .addPathPatterns("/token-login", "/pixels")
                .addPathPatterns("/admin/**");
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(authMemberArgumentResolver);
        resolvers.add(authAdminArgumentResolver);
    }
}
