package com.beancontainer.global.service;

import com.beancontainer.global.exception.CustomException;
import com.beancontainer.global.exception.ExceptionCode;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public class AuthorizationService {

    public void checkLogin(UserDetails userDetails) {
        if (userDetails == null) {
            throw new CustomException(ExceptionCode.NO_LOGIN);
        }
    }

    public void checkReview(UserDetails userDetails, String userId) {
        if (!userDetails.getUsername().equals(userId)) {
            throw new CustomException(ExceptionCode.ACCESS_DENIED);
        }
    }

    public void checkMap(UserDetails userDetails, String userId) {
        if (!userDetails.getUsername().equals(userId)) {
            throw new CustomException(ExceptionCode.ACCESS_DENIED);
        }
    }

}
