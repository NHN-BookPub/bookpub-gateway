package com.nhnacademy.bookpubgateway.filter;

import java.net.InetSocketAddress;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.support.ipresolver.XForwardedRemoteAddressResolver;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

/**
 * Logging 을 위한 GlobalFilter 입니다.
 *
 * @author : 유호철
 * @since : 1.0
 **/
@Slf4j
@Component
public class LoggingFilter implements GlobalFilter, Ordered {
    /**
     * logging 기록을 남기기 위한 filter 입니다.
     *
     * @param exchange 요청기입
     * @param chain filter chaining
     * @return 응답값 반환
     */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();
        log.info("request id : {}", request.getId());
        XForwardedRemoteAddressResolver remoteAddressResolver = XForwardedRemoteAddressResolver.maxTrustedIndex(1);
        InetSocketAddress inetSocketAddress = remoteAddressResolver.resolve(exchange);
        log.info("request ip :{}", inetSocketAddress.getAddress().getHostAddress());

        return chain.filter(exchange)
                .then(Mono.fromRunnable(() ->
                        log.info("response status :{}", response.getStatusCode())));
    }

    @Override
    public int getOrder() {
        return -1;
    }

}
