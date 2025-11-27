package com.audora.app.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReviewDto {
    private float rating;
    private String review;
    private String bookId;
    private String userId;
    private LocalDate createdDate;
    private LocalDate updatedDate;
    private String userName;
}
