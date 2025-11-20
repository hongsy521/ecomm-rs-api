package com.hongsy.ecommrsapi.util.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    BAD_REQUEST(HttpStatus.BAD_REQUEST,"잘못된 요청 입니다."),
    NULL_GENDER(HttpStatus.BAD_REQUEST,"입력된 성별이 없습니다."),
    WRONG_GENDER(HttpStatus.BAD_REQUEST,"잘못된 성별이 입니다."),
    EXISTING_USER(HttpStatus.BAD_REQUEST,"이미 가입된 사용자 입니다."),
    NON_EXISTENT_USER(HttpStatus.NOT_FOUND,"존재하지 않는 사용자 입니다.");

    private final HttpStatus status;
    private final String message;
}
