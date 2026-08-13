package com.ruoyi.system.service.impl;

import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.system.domain.Review;
import com.ruoyi.system.mapper.ReviewMapper;
import com.ruoyi.system.service.IReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ReviewServiceImpl implements IReviewService {

    @Autowired
    private ReviewMapper reviewMapper;

    @Override
    public List<Review> selectReviewList(Review review) {
        return reviewMapper.selectReviewList(review);
    }

    @Override
    public Review selectReviewById(Long id) {
        return reviewMapper.selectReviewById(id);
    }

    @Override
    public Review selectReviewByOrderId(Long orderId) {
        return reviewMapper.selectReviewByOrderId(orderId);
    }

    @Override
    @Transactional
    public int insertReview(Review review) {
        if (reviewMapper.selectReviewByOrderId(review.getOrderId()) != null) {
            throw new ServiceException("该订单已评价");
        }
        review.setCreateTime(DateUtils.getNowDate());
        return reviewMapper.insertReview(review);
    }

    @Override
    @Transactional
    public int updateReview(Review review) {
        return reviewMapper.updateReview(review);
    }

    @Override
    @Transactional
    public int deleteReviewById(Long id) {
        return reviewMapper.deleteReviewById(id);
    }
}