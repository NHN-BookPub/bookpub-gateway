package com.nhnacademy.bookpubgateway.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
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
@Slf4j
@ConfigurationProperties(prefix = "bookpub")
@Configuration
public class GatewayConfig {
    private String frontUrl;

    private String authUrl;

    private String deliveryUrl;

    private String shoppingUrl;

    private String authUrlPattern;

    private String deliveryUrlPattern;

    private String shoppingUrlPattern;

    private String frontUrlPattern;

    @Bean
    public RouteLocator frontLocator(RouteLocatorBuilder builder) {
        log.error("front-url : {}", frontUrl);
        log.error("shop-url : {}", shoppingUrl);
        log.error("auth-url : {}", authUrl);
        log.error("delivery-url : {}", deliveryUrl);

        log.error("front-pattern-url : {}", frontUrlPattern);
        log.error("shop-pattern-url : {}", shoppingUrlPattern);
        log.error("auth-pattern-url : {}", authUrlPattern);
        log.error("delivery-pattern-url : {}", deliveryUrlPattern);

        log.warn("frontLocator call()");
        RouteLocator build = builder.routes()
                .route("front", r -> r.path(frontUrlPattern)
                        .uri(frontUrl))
                .route("auth", r -> r.path(authUrlPattern)
                        .uri(authUrl))
                .route("delivery", r -> r.path(deliveryUrlPattern)
                        .uri(deliveryUrl))
                .route("shopping", r -> r.path(shoppingUrlPattern)
                        .uri(shoppingUrl))
                .build();
        log.warn("routes() 돔");
        return build;
    }

    public String getFrontUrl() {
        return frontUrl;
    }

    public void setFrontUrl(String frontUrl) {
        this.frontUrl = frontUrl;
    }

    public String getAuthUrl() {
        return authUrl;
    }

    public void setAuthUrl(String authUrl) {
        this.authUrl = authUrl;
    }

    public String getDeliveryUrl() {
        return deliveryUrl;
    }

    public void setDeliveryUrl(String deliveryUrl) {
        this.deliveryUrl = deliveryUrl;
    }

    public String getShoppingUrl() {
        return shoppingUrl;
    }

    public void setShoppingUrl(String shoppingUrl) {
        this.shoppingUrl = shoppingUrl;
    }

    public String getAuthUrlPattern() {
        return authUrlPattern;
    }

    public void setAuthUrlPattern(String authUrlPattern) {
        this.authUrlPattern = authUrlPattern;
    }

    public String getDeliveryUrlPattern() {
        return deliveryUrlPattern;
    }

    public void setDeliveryUrlPattern(String deliveryUrlPattern) {
        this.deliveryUrlPattern = deliveryUrlPattern;
    }

    public String getShoppingUrlPattern() {
        return shoppingUrlPattern;
    }

    public void setShoppingUrlPattern(String shoppingUrlPattern) {
        this.shoppingUrlPattern = shoppingUrlPattern;
    }

    public String getFrontUrlPattern() {
        return frontUrlPattern;
    }

    public void setFrontUrlPattern(String frontUrlPattern) {
        this.frontUrlPattern = frontUrlPattern;
    }
}
