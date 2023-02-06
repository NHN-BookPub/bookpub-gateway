package com.nhnacademy.bookpubgateway.config;

import com.nhnacademy.bookpubgateway.filter.AuthorizationFilter;
import com.nhnacademy.bookpubgateway.utils.JwtUtils;
import java.util.function.Function;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.GatewayFilterSpec;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.cloud.gateway.route.builder.UriSpec;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * 라우팅 설정 파일입니다.
 *
 * @author : 임태원, 유호철
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
    private String tokenUrlPattern;



    @Bean
    public RouteLocator frontLocator(AuthorizationFilter authorizationFilter,
                                     RedisTemplate<String,String> redisTemplate,
                                     JwtUtils jwtUtils,
                                     RouteLocatorBuilder builder) {
        return builder.routes()
                .route("auth", r -> r.path(authUrlPattern)
                        .uri(authUrl))
                .route("delivery", r -> r.path(deliveryUrlPattern)
                        .uri(deliveryUrl))
                .route("shopping", r -> r.path(shoppingUrlPattern)
                        .uri(shoppingUrl))
                .route("token", r-> r.path(tokenUrlPattern)
                        .filters(tokenFilter(authorizationFilter, redisTemplate, jwtUtils))
                        .uri(shoppingUrl))
                .build();
    }

    private Function<GatewayFilterSpec, UriSpec> tokenFilter(AuthorizationFilter filter,
                                                             RedisTemplate<String, String> redisTemplate,
                                                             JwtUtils jwtUtils) {
        return f -> f.filter(
                filter.apply(
                        new AuthorizationFilter.Config(redisTemplate, jwtUtils)
                )
        );

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

    public String getTokenUrlPattern() {
        return tokenUrlPattern;
    }

    public void setTokenUrlPattern(String tokenUrlPattern) {
        this.tokenUrlPattern = tokenUrlPattern;
    }
}
