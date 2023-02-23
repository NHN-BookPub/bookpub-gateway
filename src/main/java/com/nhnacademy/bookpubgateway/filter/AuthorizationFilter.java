package com.nhnacademy.bookpubgateway.filter;

import com.nhnacademy.bookpubgateway.key.dto.TokenPayLoad;
import com.nhnacademy.bookpubgateway.utils.JwtUtils;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

/**
 * Gateway 에서 token 값 검증 필터
 *
 * @author : 유호철
 * @since : 1.0
 **/
@Slf4j
@Component
public class AuthorizationFilter extends AbstractGatewayFilterFactory<AuthorizationFilter.Config> {
    private static final String CHECK_TOKEN = "Authorization";
    private static final String REFRESH_TOKEN = "refresh-token";
    private static final String BLACK_LIST = "black_list";

    /**
     * AbstractGatewayFilterFactory 에서 구현해야하는 필수 클래스
     */
    @RequiredArgsConstructor
    public static class Config {
        private final RedisTemplate<String, String> redisTemplate;
        private final JwtUtils jwtUtils;

    }

    /**
     * 인증 필터 생성자
     */
    public AuthorizationFilter() {
        super(Config.class);
    }

    /**
     * apply 를 통해 gateway config 에서 function 으로 동작
     * Flow :
     * 1. header 의 token 값 유무 확인
     * 2. payload 의 Claim 값을 변환시켜서 받음
     * 3. Redis 에 refresh 토큰에 accessToken 의 존재유무 확인
     * 4. Redis 에 header uuid 를 통해 userNo 의 존재 유무 확인
     * 5. header 에 id, memberNo 값 넣어서 보냄
     *
     * @param config Config 값 적용
     * @return gatewayfilter 반환
     */
    @Override
    public GatewayFilter apply(AuthorizationFilter.Config config) {
        return ((exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();

            if (checkHeaderAccessToken(request)) {
                log.error("header 가 없는 사용자");
                return handleUnAuthorized(exchange);
            }
            String accessToken = Objects.requireNonNull(request.getHeaders()
                    .get(CHECK_TOKEN)).get(0).substring(7);

            if (checkBlackList(config, accessToken)) {
                return handleBlackListToken(exchange);
            }
            TokenPayLoad payLoad = config.jwtUtils.getPayLoad(accessToken);

            if (checkMemberInfo(config, payLoad)) {
                log.error("redis 에 회원의 정보가 없음");
                return handleTokenNotUsed(exchange);
            }

            if (checkRefreshToken(config, accessToken)) {
                log.error("refresh 토큰이 없음");
                return handleUnAuthorized(exchange);
            }

            String memberId = String.valueOf(config.redisTemplate.opsForValue()
                    .get(payLoad.getMemberUUID()));

            addAuthorizationHeaders(exchange, payLoad, memberId);

            return chain.filter(exchange);
        });
    }

    /**
     * Redis 안에 member 의 uuid 값이 있는지 확인하기위한 메서드입니다.
     *
     * @param config  config 값 기입
     * @param payLoad payload 값을 확인한다.
     * @return boolean
     */
    private static boolean checkMemberInfo(Config config, TokenPayLoad payLoad) {
        return Objects.isNull(config.redisTemplate.opsForValue().get(payLoad.getMemberUUID()));
    }

    /**
     *
     * @param config config 값 기입
     * @param accessToken accessToken 기입
     * @return boolean
     */
    private static boolean checkBlackList(Config config, String accessToken) {
        return Objects.nonNull((config.redisTemplate.opsForHash().get(BLACK_LIST, accessToken)));
    }

    /**
     * Request Header 에 accessToken 이 있는지 확인하는 메서드입니다.
     *
     * @param request 현재의 요청이 기입
     * @return boolean
     */
    private static boolean checkHeaderAccessToken(ServerHttpRequest request) {
        return !(request.getHeaders().containsKey(CHECK_TOKEN));
    }

    /**
     * Redis 에 refresh token 값이 있는지 검증하는 메서드입니다.
     *
     * @param config      config 값 기입
     * @param accessToken accessToken 이 기입된다.
     * @return boolean
     */
    private static boolean checkRefreshToken(Config config, String accessToken) {
        return Objects.isNull(config.redisTemplate.opsForHash().get(REFRESH_TOKEN, accessToken));
    }

    /**
     * Custom Header 에 정보를 담아서 보내주기위한 메서드입니다.
     *
     * @param exchange webSession 에 접근할수있게해준다.(HttpServletResponse, Request) 랑 같은역할
     * @param payLoad  payload 값을 가공한 값.
     * @param memberId 회원의 no
     */
    private static void addAuthorizationHeaders(ServerWebExchange exchange, TokenPayLoad payLoad, String memberId) {
        exchange.getRequest()
                .mutate()
                .header("X-Authorization-Id", memberId)
                .header("X-Authorization-Roles", payLoad.getRoles())
                .build();
    }


    /**
     * Header 에 Authorization 가 없으면 예외를 발생한다.
     * 401 에러 발생
     *
     * @param exchange webSession 에 접근할수있게해준다.(HttpServletResponse, Requset) 랑 같은역할
     * @return 401 에러 반환
     */
    private Mono<Void> handleUnAuthorized(ServerWebExchange exchange) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.UNAUTHORIZED);

        return response.setComplete();
    }

    /**
     * Redis 에 관련 access token 값이 없을경우 NOT_FOUND 를 발생한다.
     *
     * @param exchange webSession 에 접근할수있게해준다.(HttpServletResponse, Requset) 랑 같은역할
     * @return 404 값을 반환한다.
     */
    private Mono<Void> handleTokenNotUsed(ServerWebExchange exchange) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.UNAUTHORIZED);

        return response.setComplete();
    }

    /**
     * Redis 에 관련 accessToken 이 Black List 에 올라간 경우 400 을 뱉는다.
     *
     * @param exchange 에 접근할수있게해준다.(HttpServletResponse, Requset) 랑 같은역할
     * @return 400 을 반환한다
     */
    private Mono<Void> handleBlackListToken(ServerWebExchange exchange) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.UNAUTHORIZED);

        return response.setComplete();
    }
}
