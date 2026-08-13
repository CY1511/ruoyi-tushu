package com.ruoyi.system.service;

import com.ruoyi.system.domain.Review;

import java.util.List;

public interface IReviewService {

    List<Review> selectReviewList(Review review);

    Review selectReviewById(Long id);

    Review selectReviewByOrderId(Long orderId);

    int insertReview(Review review);

    int updateReview(Review review);

    int deleteReviewById(Long id);
}