package com.audora.app.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContinueListeningDTO {
    private String bookId;
    private String bookTitle;
    private String bookImage;
    private String author;
    private Integer progressPercentage;

    // Resume info
    private String resumeChapterId;
    private String resumeChapterTitle;
    private Integer resumeSecond;

    // Time info
    private String timeLeft; // "30 minutes left"
    private LocalDateTime lastListenedAt;
}

