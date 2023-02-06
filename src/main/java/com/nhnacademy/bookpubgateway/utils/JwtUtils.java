package com.nhnacademy.bookpubgateway.utils;

import com.nhnacademy.bookpubgateway.config.KeyConfig;
import com.nhnacademy.bookpubgateway.key.dto.TokenPayLoad;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Payload 의 Claim 값 받아서 변환할때 필요한 클래스입니다.
 *
 * @author : 유호철
 * @since : 1.0
 **/
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtUtils {
    @Value("${bookpub.jwt.secret}")
    private String jwtValue;
    private final KeyConfig keyConfig;

    /**
     * Jwt 의 Payload 값을 확인하기위해서 쓰이는 메서드입니다.
     *
     * @param accessToken 엑세스 토큰 기입.
     * @return Claims 클레임 들이 반환.
     */
    public Claims getPayLoadValue(String accessToken) {
        Claims result = null;
        try {
            result = Jwts.parser()
                    .setSigningKey(keyConfig.keyStore(jwtValue).getBytes())
                    .parseClaimsJws(accessToken)
                    .getBody();

        } catch (SignatureException |
                 MalformedJwtException e) {
            log.error("잘못된 JWT 서명입니다.");
        } catch (
                ExpiredJwtException e) {
            log.error("만료된 JWT 토큰입니다.");
        } catch (
                UnsupportedJwtException e) {
            log.error("지원되지 않는 JWT 토큰입니다.");
        } catch (
                IllegalArgumentException e) {
            log.error("JWT 토큰이 잘못되었습니다.");
        }
        return result;
    }

    /**
     * Payload 의 클레임 값을 TokenPayload 로 변환시키기 위한 메서드입니다.
     *
     * @param accessToken 엑세스 토큰
     * @return 토큰 값을 클래스형태로 반환
     */
    public TokenPayLoad getPayLoad(String accessToken) {
        String memberUUID = getPayLoadValue(accessToken)
                .get("memberUUID", String.class);

        String roles = getPayLoadValue(accessToken)
                .get("roles", String.class);

        return new TokenPayLoad(memberUUID, roles);
    }
}
