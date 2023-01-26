package com.nhnacademy.bookpubgateway.key.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Payload 의 값을 Dto 로 변환할때 사용되는 클래스입니다.
 *
 * @author : 유호철
 * @since : 1.0
 **/
@Getter
@AllArgsConstructor
public class TokenPayLoad {
    private String memberUUID;
    private String roles;

}
