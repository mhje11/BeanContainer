package com.beancontainer.domain.admin;


import org.springframework.security.access.prepost.PreAuthorize;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
//커스텀 어노테이션 생성
//관리자 권한을 가진 사용자만 특정 기능에 접근 할 수 있도록 함
@Target({ElementType.METHOD, ElementType.TYPE}) //적용 대상
@Retention(RetentionPolicy.RUNTIME) //정보 유지되는 대상 -> 컴파일 이후 런타임 시기에도 JVM에 의해 참조 가능
@PreAuthorize("hasAuthority('ADMIN')")
public @interface RequireAdmin {
}
