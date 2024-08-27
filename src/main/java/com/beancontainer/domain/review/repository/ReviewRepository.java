package com.beancontainer.domain.review.repository;

import com.beancontainer.domain.cafe.entity.Cafe;
import com.beancontainer.domain.member.entity.Member;
import com.beancontainer.domain.review.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Long>, CustomReviewRepository{
//    List<Review> findAllByCafeId(Long cafeId);


    @Query("select AVG (r.score) from Review r where r.cafe.id = :cafeId")
    Double calculateAverageScoreByCafeId(Long cafeId);

//    @Query("select rc.category.name, count(rc.category.name) " +
//            "from ReviewCategory rc " +
//            "where rc.review.cafe.id = :cafeId " +
//            "group by rc.category.name " +
//            "order by count(rc.category.name) desc " )
//    List<Object[]> findCategoryFrequenciesByCafeId(@Param("cafeId") Long cafeId);

    @Query(value = "SELECT c.name, COUNT(rc.category_id) " +
            "FROM review_category rc " +
            "JOIN category c ON rc.category_id = c.id " +
            "JOIN reviews r ON rc.review_id = r.id " +
            "WHERE r.cafe_id = :cafeId " +
            "GROUP BY c.name " +
            "ORDER BY COUNT(rc.category_id) DESC", nativeQuery = true)
    List<Object[]> findCategoryFrequenciesByCafeId(@Param("cafeId") Long cafeId);

    List<Review> findAllByMember(Member member);


}
