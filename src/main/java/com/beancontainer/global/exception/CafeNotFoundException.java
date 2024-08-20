package com.beancontainer.global.exception;

public class CafeNotFoundException extends RuntimeException{
    public CafeNotFoundException(String message) {
        super(message);
    }
}
