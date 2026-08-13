package com.ruoyi.web.controller.system;

import com.ruoyi.common.core.domain.R;
import com.ruoyi.system.domain.Review;
import com.ruoyi.system.service.IReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/review")
public class ReviewController {

    @Autowired
    private IReviewService reviewService;

    @GetMapping("/list")
    public R getReviewList(Review review) {
        List<Review> reviews = reviewService.selectReviewList(review);
        return R.ok(reviews);
    }

    @GetMapping("/{id}")
    public R getReviewById(@PathVariable Long id) {
        Review review = reviewService.selectReviewById(id);
        return R.ok(review);
    }

    @PostMapping("/add")
    public R addReview(@RequestBody Review review) {
        reviewService.insertReview(review);
        return R.ok("评价成功");
    }

    @PutMapping("/update")
    public R updateReview(@RequestBody Review review) {
        reviewService.updateReview(review);
        return R.ok("更新成功");
    }

    @DeleteMapping("/{id}")
    public R deleteReview(@PathVariable Long id) {
        reviewService.deleteReviewById(id);
        return R.ok("删除成功");
    }
}