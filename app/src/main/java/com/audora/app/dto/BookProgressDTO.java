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
public class BookProgressDTO {
    private String bookId;
    private String bookTitle;
    private String bookImage;
    private Integer completedChapters;
    private Integer totalChapters;
    private Integer progressPercentage;
    private Boolean completed;

    // Last chapter info for "Continue Listening"
    private String lastChapterId;
    private String lastChapterTitle;
    private Integer lastPlayedSecond;

    // Time stats
    private String totalListeningTime; // Formatted: "2h 30m"
    private String estimatedTimeRemaining; // Formatted: "1h 45m"

    // Dates
    private LocalDateTime startedAt;
    private LocalDateTime lastListenedAt;
    private LocalDateTime completedAt;
}

