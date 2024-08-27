package com.beancontainer.domain.member.repository;

import com.beancontainer.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByUserId(String userId);
    Optional<Member> findByEmail(String email);
    Optional<Member> findByName(String name);

    List<Member> findAllByDeletedAtBefore(LocalDateTime dateTime);

    boolean existsByUserId(String userId);
}
