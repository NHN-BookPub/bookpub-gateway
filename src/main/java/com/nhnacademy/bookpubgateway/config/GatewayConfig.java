package com.nhnacademy.bookpubgateway.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 라우팅 설정 파일입니다.
 *
 * @author : 임태원
 * @since : 1.0
 **/
@Configuration
public class GatewayConfig {
    @Value("${bookpub.front.url}")
    private String frontUrl;

    @Value("${bookpub.auth.url}")
    private String authUrl;

    @Value("${bookpub.delivery.url}")
    private String deliveryUrl;

    @Value("${bookpub.shopping.url}")
    private String shoppingUrl;

    @Value("${bookpub.auth.url.pattern}")
    private String authUrlPattern;

    @Value("${bookpub.delivery.url.pattern}")
    private String deliveryUrlPattern;

    @Value("${bookpub.shopping.url.pattern}")
    private String shoppingUrlPattern;

    @Value("${bookpub.front.url.pattern}")
    private String frontUrlPattern;

    @Bean
    public RouteLocator frontLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("front", r -> r.path(frontUrlPattern)
                        .uri(frontUrl))
                .route("auth", r -> r.path(authUrlPattern)
                        .uri(authUrl))
                .route("delivery", r -> r.path(deliveryUrlPattern)
                        .uri(deliveryUrl))
                .route("shopping", r -> r.path(shoppingUrlPattern)
                        .uri(shoppingUrl))
                .build();
    }
}
