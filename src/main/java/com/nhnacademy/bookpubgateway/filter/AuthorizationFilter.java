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

    /**
     * AbstractGatewayFilterFactory 에서 구현해야하는 필수 클래스
     */
    @RequiredArgsConstructor
    public static class Config {
        private final RedisTemplate<String, Object> redisTemplate;
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
     *
     * @param config Config 값 적용
     * @return gatewayfilter 반환
     */
    @Override
    public GatewayFilter apply(AuthorizationFilter.Config config) {
        return ((exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();

            if (!(request.getHeaders().containsKey(CHECK_TOKEN))) {
                return handleUnAuthorized(exchange);
            }
            String accessToken = Objects.requireNonNull(request.getHeaders()
                    .get(CHECK_TOKEN)).get(0).substring(7);

            TokenPayLoad payLoad = config.jwtUtils.getPayLoad(accessToken);

            if (Objects.isNull(config.redisTemplate.opsForValue().get(payLoad.getMemberUUID()))) {
                return handleTokenNotUsed(exchange);
            }

            String memberId = String.valueOf(config.redisTemplate.opsForValue()
                    .get(payLoad.getMemberUUID()));

            addAuthorizationHeaders(exchange, payLoad, memberId);

            return chain.filter(exchange);
        });
    }

    /**
     * redis 에서 값을 비교해서 인증된 토큰인지를 확인하기위한 메서드
     */
    private static void checkRefreshToken() {
        //TODO : auth 서버 정리후 구현예정
    }


    /**
     * Custom Header 에 정보를 담아서 보내주기위한 메서드입니다.
     *
     * @param exchange webSession 에 접근할수있게해준다.(HttpServletResponse, Request) 랑 같은역할
     * @param payLoad payload 값을 가공한 값.
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
     *  Redis 에 관련 access token 값이 없을경우 NOT_FOUND 를 발생한다.
     *
     * @param exchange webSession 에 접근할수있게해준다.(HttpServletResponse, Requset) 랑 같은역할
     * @return 404 값을 반환한다.
     */
    private Mono<Void> handleTokenNotUsed(ServerWebExchange exchange) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.NOT_FOUND);

        return response.setComplete();
    }
}
