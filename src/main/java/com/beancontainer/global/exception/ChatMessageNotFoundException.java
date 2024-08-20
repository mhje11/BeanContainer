package com.beancontainer.global.exception;


public class ChatMessageNotFoundException extends RuntimeException{
    public ChatMessageNotFoundException(String message) {
        super(message);
    }
}
