package com.beancontainer.global.handler;

import com.beancontainer.global.exception.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler2 {

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ExceptionResponse> handleUserNotFoundException(UserNotFoundException e) {
        return createErrorResponse(ExceptionCode.USER_NOT_FOUND);
    }

    @ExceptionHandler(EmailNotFoundException.class)
    public ResponseEntity<ExceptionResponse> handleEmailNotFoundException(EmailNotFoundException e) {
        return createErrorResponse(ExceptionCode.EMAIL_NOT_FOUND);
    }

    @ExceptionHandler(EmailExistException.class)
    public ResponseEntity<ExceptionResponse> handleEmailExistException(EmailExistException e) {
        return createErrorResponse(ExceptionCode.EMAIL_ALREADY_EXISTS);
    }

    @ExceptionHandler(EmailNotMatchCodeException.class)
    public ResponseEntity<ExceptionResponse> handleEmailNotMatchCodeException(EmailNotMatchCodeException e) {
        return createErrorResponse(ExceptionCode.EMAIL_CODE_MISMATCH);
    }

    @ExceptionHandler(JwtTokenExpiredException.class)
    public ResponseEntity<ExceptionResponse> handleJwtTokenExpiredException(JwtTokenExpiredException e) {
        return createErrorResponse(ExceptionCode.JWT_TOKEN_EXPIRED);
    }

    @ExceptionHandler(InvalidPathException.class)
    public ResponseEntity<ExceptionResponse> handleInvalidPathException(InvalidPathException e) {
        return createErrorResponse(ExceptionCode.INVALID_PATH);
    }

    @ExceptionHandler(PasswordMismatchException.class)
    public ResponseEntity<ExceptionResponse> handlePasswordMismatchException(PasswordMismatchException e) {
        return createErrorResponse(ExceptionCode.PASSWORD_MISMATCH);
    }

    @ExceptionHandler(UnAuthorizedException.class)
    public ResponseEntity<ExceptionResponse> handleUnauthorizedException(UnAuthorizedException e) {
        return createErrorResponse(ExceptionCode.UNAUTHORIZED);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ExceptionResponse> handleAccessDeniedException(AccessDeniedException e) {
        return createErrorResponse(ExceptionCode.ACCESS_DENIED);
    }

    @ExceptionHandler(CafeNotFoundException.class)
    public ResponseEntity<ExceptionResponse> handleCafeNotFoundException(CafeNotFoundException e) {
        return createErrorResponse(ExceptionCode.CAFE_NOT_FOUND);
    }

    @ExceptionHandler(CategoryNotFoundException.class)
    public ResponseEntity<ExceptionResponse> handleCategoryNotFoundException(CategoryNotFoundException e) {
        return createErrorResponse(ExceptionCode.CATEGORY_NOT_FOUND);
    }

    @ExceptionHandler(MapNotFoundException.class)
    public ResponseEntity<ExceptionResponse> handleMapNotFoundException(MapNotFoundException e) {
        return createErrorResponse(ExceptionCode.MAP_NOT_FOUND);
    }

    @ExceptionHandler(ReviewNotFoundException.class)
    public ResponseEntity<ExceptionResponse> handleReviewNotFoundException(ReviewNotFoundException e) {
        return createErrorResponse(ExceptionCode.REVIEW_NOT_FOUND);
    }

    @ExceptionHandler(BoardNotFoundException.class)
    public ResponseEntity<ExceptionResponse> handleBoardNotFoundException(BoardNotFoundException e) {
        return createErrorResponse(ExceptionCode.BOARD_NOT_FOUND);
    }

    @ExceptionHandler(PostNotFoundException.class)
    public ResponseEntity<ExceptionResponse> handlePostNotFoundException(PostNotFoundException e) {
        return createErrorResponse(ExceptionCode.POST_NOT_FOUND);
    }

    @ExceptionHandler(ImageNotFoundException.class)
    public ResponseEntity<ExceptionResponse> handleImageNotFoundException(ImageNotFoundException e) {
        return createErrorResponse(ExceptionCode.IMAGE_NOT_FOUND);
    }

    @ExceptionHandler(CommentNotFoundException.class)
    public ResponseEntity<ExceptionResponse> handleCommentNotFoundException(CommentNotFoundException e) {
        return createErrorResponse(ExceptionCode.COMMENT_NOT_FOUND);
    }

    @ExceptionHandler(LikeExistException.class)
    public ResponseEntity<ExceptionResponse> handleLikeExistException(LikeExistException e) {
        return createErrorResponse(ExceptionCode.LIKE_ALREADY_EXISTS);
    }

    @ExceptionHandler(HistoryNotFoundException.class)
    public ResponseEntity<ExceptionResponse> handleHistoryNotFoundException(HistoryNotFoundException e) {
        return createErrorResponse(ExceptionCode.HISTORY_NOT_FOUND);
    }

    @ExceptionHandler(ChatRoomNotFoundException.class)
    public ResponseEntity<ExceptionResponse> handleChatRoomNotFoundException(ChatRoomNotFoundException e) {
        return createErrorResponse(ExceptionCode.CHAT_ROOM_NOT_FOUND);
    }

    @ExceptionHandler(ChatListNotFoundException.class)
    public ResponseEntity<ExceptionResponse> handleChatListNotFoundException(ChatListNotFoundException e) {
        return createErrorResponse(ExceptionCode.CHAT_LIST_NOT_FOUND);
    }

    @ExceptionHandler(ChatMessageNotFoundException.class)
    public ResponseEntity<ExceptionResponse> handleChatMessageNotFoundException(ChatMessageNotFoundException e) {
        return createErrorResponse(ExceptionCode.CHAT_MESSAGE_NOT_FOUND);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ExceptionResponse> handleGeneralException(Exception e) {
        log.error("Unexpected error occurred", e);
        return createErrorResponse(ExceptionCode.INTERNAL_SERVER_ERROR);
    }

    private ResponseEntity<ExceptionResponse> createErrorResponse(ExceptionCode exceptionCode) {
        ExceptionResponse response = new ExceptionResponse(exceptionCode.getCode(), exceptionCode.getMessage());
        return new ResponseEntity<>(response, exceptionCode.getHttpStatus());
    }

    private static class ExceptionResponse {
        private final int code;
        private final String message;

        public ExceptionResponse(int code, String message) {
            this.code = code;
            this.message = message;
        }

        public int getCode() {
            return code;
        }

        public String getMessage() {
            return message;
        }
    }
}