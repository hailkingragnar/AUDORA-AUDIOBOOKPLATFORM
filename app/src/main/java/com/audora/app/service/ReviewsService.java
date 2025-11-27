package com.audora.app.service;

import com.audora.app.dto.ReviewDto;
import com.audora.app.model.Reviews;
import com.audora.app.repo.ReviewsRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ReviewsService {
    @Autowired
    private ReviewsRepo reviewsRepo;

    public List<ReviewDto> getReviews(String audioBookId) {

        List<ReviewDto> reviewDtos = reviewsRepo.findByBookId(audioBookId)
                .stream()
                .map(review -> {

                    ReviewDto reviewDto = new ReviewDto();
                    reviewDto.setRating(review.getRating());
                    reviewDto.setReview(review.getComment());
                    reviewDto.setCreatedDate(review.getCreatedAt());
                    reviewDto.setUpdatedDate(review.getUpdatedDate());
                    reviewDto.setUserName(review.getUsers().getName());
                    reviewDto.setBookId(reviewDto.getBookId());

                    return reviewDto;
                })
                .collect(Collectors.toList());

        return reviewDtos;
    }
}