package com.ruoyi.system.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import com.ruoyi.system.domain.Review;

@Mapper
public interface ReviewMapper {
    List<Review> selectReviewList(Review review);
    Review selectReviewById(Long id);
    Review selectReviewByOrderId(Long orderId);
    int insertReview(Review review);
    int updateReview(Review review);
    int deleteReviewById(Long id);
}