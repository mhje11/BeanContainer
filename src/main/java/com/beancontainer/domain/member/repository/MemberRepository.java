package com.beancontainer.domain.member.repository;

import com.beancontainer.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByUserId(String userId); //로그인 id로 찾기

    Member findByUsername(String username);
}
