package com.example.thelastisme.exception;

import org.springframework.http.HttpStatus;

public enum ErrorCode {
    //Auth
    JWT_BAD_REQUEST(HttpStatus.UNAUTHORIZED, "잘못된 JWT입니다."),
    JWT_ALL_TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "토큰들이 모두 만료되었습니다"),
    JWT_DENIED(HttpStatus.UNAUTHORIZED, "토큰 해독 중 발생하였습니다."),
    INVALID_BASIC_AUTH(HttpStatus.UNAUTHORIZED, "잘못된 userId입니다."),
    JWT_SERVER_ERROR(HttpStatus.UNAUTHORIZED, "jwt 에러가 발생했습니다."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "문제가 생겼습니다. 잠시 후 다시 시도해주세요.");

    private final String message;
    private final HttpStatus status;

    ErrorCode(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }
}
