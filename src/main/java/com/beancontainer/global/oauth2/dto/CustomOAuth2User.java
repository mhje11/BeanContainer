package com.beancontainer.global.oauth2.dto;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
@Slf4j
public class CustomOAuth2User implements OAuth2User {

    private final OAuth2LoginDTO oAuth2LoginDTO;
    private final OAuth2Response oAuth2Response;

    public CustomOAuth2User(OAuth2LoginDTO oAuth2LoginDTO, OAuth2Response oAuth2Response) {
        this.oAuth2LoginDTO = oAuth2LoginDTO;
        this.oAuth2Response = oAuth2Response;
    }

    //제공자에게 받은 정보
    @Override
    public Map<String, Object> getAttributes() {
        log.info("getAttributes : " + oAuth2Response.getAttributes());
        return oAuth2Response.getAttributes();

    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"));
    }

    @Override
    public String getName() {
        return oAuth2LoginDTO.getName();
    }

    public String getUserId() {
        return oAuth2LoginDTO.getUserId();
    }
}
