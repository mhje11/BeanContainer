package com.beancontainer.global.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ExceptionCode {
    /**
     *  유저 관련
     */
    MEMBER_NOT_FOUND(404, HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."),
    EMAIL_NOT_FOUND(404, HttpStatus.NOT_FOUND, "존재하지 않는 이메일입니다."),
    EMAIL_ALREADY_EXISTS(400, HttpStatus.BAD_REQUEST, "이미 사용 중인 이메일입니다."),
    EMAIL_CODE_MISMATCH(400, HttpStatus.BAD_REQUEST, "이메일 인증번호가 일치하지 않습니다."),
    PASSWORD_MISMATCH(400, HttpStatus.BAD_REQUEST, "비밀번호가 일치하지 않습니다."),

    /**
     *  토근 관련
     */
    JWT_TOKEN_EXPIRED(401, HttpStatus.UNAUTHORIZED, "JWT 토큰이 만료되었습니다."),
    UNAUTHORIZED(401, HttpStatus.UNAUTHORIZED, "인증되지 않은 사용자입니다."),
    ACCESS_DENIED(403, HttpStatus.FORBIDDEN, "접근 권한이 없습니다."),


    /**
     *  카페, 지도 관련
     */
    CAFE_NOT_FOUND(404, HttpStatus.NOT_FOUND, "카페를 찾을 수 없습니다."),
    CATEGORY_NOT_FOUND(404, HttpStatus.NOT_FOUND, "카테고리를 찾을 수 없습니다."),
    MAP_NOT_FOUND(404, HttpStatus.NOT_FOUND, "지도를 찾을 수 없습니다."),
    REVIEW_NOT_FOUND(404, HttpStatus.NOT_FOUND, "리뷰를 찾을 수 없습니다."),

    /**
     *  게시판 관련
     */
    BOARD_NOT_FOUND(404, HttpStatus.NOT_FOUND, "게시판을 찾을 수 없습니다."),
    POST_NOT_FOUND(404, HttpStatus.NOT_FOUND, "게시글을 찾을 수 없습니다."),
    IMAGE_NOT_FOUND(404, HttpStatus.NOT_FOUND, "이미지를 찾을 수 없습니다."),
    COMMENT_NOT_FOUND(404, HttpStatus.NOT_FOUND, "댓글을 찾을 수 없습니다."),
    HISTORY_NOT_FOUND(404, HttpStatus.NOT_FOUND, "해당 글에 대한 내역을 찾을 수 없습니다."),
    LIKE_ALREADY_EXISTS(400, HttpStatus.BAD_REQUEST, "이미 좋아요를 눌렀습니다."),
    INVALID_IMAGE_FORMAT(400, HttpStatus.BAD_REQUEST, "유효하지 않은 이미지 파일 형식입니다."),
    MAX_IMAGES_COUNT(400, HttpStatus.BAD_REQUEST, "이미지는 최대 5장까지 첨부가 가능합니다."),

    /**
     *  채팅방 관련
     */
    CHAT_ROOM_NOT_FOUND(404, HttpStatus.NOT_FOUND, "채팅방을 찾을 수 없습니다."),
    CHAT_LIST_NOT_FOUND(404, HttpStatus.NOT_FOUND, "채팅 리스트를 찾을 수 없습니다."),
    CHAT_MESSAGE_NOT_FOUND(404, HttpStatus.NOT_FOUND, "채팅 메시지를 찾을 수 없습니다."),


    /**
     *  서버 관련
     */
    BAD_REQUEST(400, HttpStatus.BAD_REQUEST, "잘못된 요청입니다."),
    INVALID_PATH(404, HttpStatus.NOT_FOUND, "올바르지 않은 경로입니다."),
    INTERNAL_SERVER_ERROR(500, HttpStatus.INTERNAL_SERVER_ERROR, "서버 내부 오류가 발생했습니다.");

    private final int code;
    private final HttpStatus httpStatus;
    private final String message;

    ExceptionCode(int code, HttpStatus httpStatus, String message) {
        this.code = code;
        this.httpStatus = httpStatus;
        this.message = message;
    }
}
