package com.beancontainer.domain.member.profileimage.repository;

import com.beancontainer.domain.member.profileimage.entity.ProfileImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProfileImageRepository extends JpaRepository<ProfileImage, Long> {
    Optional<ProfileImage> findByFileName(String fileName);
    void deleteByFileName(String fileName);
    boolean existsByFileName(String fileName);
}
