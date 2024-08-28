package com.beancontainer.global.oauth2.dto;

import java.util.Map;

public interface OAuth2Response {
    //제공자 (네이버, 카카오)
    String getProvider();
    //제공자에서 발급해주는 아이디
    String getProviderId();
    //이메일
    String getEmail();
    //사용자 실명(설정 이름)
    String getName();
    // 권한
    Map<String, Object> getAttributes();

}
