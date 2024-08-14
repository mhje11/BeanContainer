package com.beancontainer.domain.map.repository;

import com.beancontainer.domain.map.entity.Map;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MapRepository extends JpaRepository<Map, Long> {

    List<Map> findAllByUsername(String username);
}
