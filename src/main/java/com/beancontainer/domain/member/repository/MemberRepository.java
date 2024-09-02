package com.beancontainer.domain.member.repository;

import com.beancontainer.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByUserId(String userId);
    Optional<Member> findByName(String name);

    List<Member> findAllByDeletedAtBefore(LocalDateTime dateTime);

    boolean existsByUserId(String userId);

    @Transactional
    void deleteByUserId(String userId);


}
