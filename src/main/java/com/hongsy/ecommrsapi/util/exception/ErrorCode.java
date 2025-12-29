package com.hongsy.ecommrsapi.util.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    BAD_REQUEST(HttpStatus.BAD_REQUEST,"잘못된 요청 입니다."),
    NULL_GENDER(HttpStatus.BAD_REQUEST,"입력된 성별이 없습니다."),
    NULL_ROLE(HttpStatus.BAD_REQUEST,"입력된 역할이 없습니다."),
    INVALID_ROLE(HttpStatus.BAD_REQUEST,"유효하지 않은 역할 입니다."),
    INVALID_GENDER(HttpStatus.BAD_REQUEST,"유효하지 않은 성별 입니다."),
    EXISTING_USER(HttpStatus.BAD_REQUEST,"이미 가입된 사용자 입니다."),

    EXISTING_LIKE(HttpStatus.BAD_REQUEST,"이미 상품에 좋아요가 등록되었습니다."),
    NOT_FOUND_LIKE(HttpStatus.BAD_REQUEST,"등록된 좋아요가 없습니다."),

    NOT_FOUND_USER(HttpStatus.NOT_FOUND,"사용자를 찾을 수 없습니다."),
    NOT_FOUND_PRODUCT(HttpStatus.NOT_FOUND,"상품을 찾을 수 없습니다."),
    INCORRECT_SELLER(HttpStatus.FORBIDDEN,"판매자가 일치하지 않습니다."),
    INVALID_NUMBER(HttpStatus.BAD_REQUEST,"0 이상의 숫자를 입력해주세요."),

    INVALID_TOKEN(HttpStatus.BAD_REQUEST,"유효하지 않은 토큰입니다."),
    NOT_FOUND_TOKEN(HttpStatus.NOT_FOUND,"토큰을 찾을 수 없습니다.");

    private final HttpStatus status;
    private final String message;
}
