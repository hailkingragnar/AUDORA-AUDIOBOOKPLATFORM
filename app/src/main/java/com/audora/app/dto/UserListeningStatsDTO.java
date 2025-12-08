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
public class UserListeningStatsDTO {
    private String userId;
    private Integer totalBooksStarted;
    private Integer totalBooksCompleted;
    private Integer totalChaptersCompleted;
    private String totalListeningTime; // Formatted: "15h 30m"
    private Integer totalListeningTimeSeconds;
    private Integer booksInProgress;
    private String averageCompletionRate; // "65%"

    // Recent activity
    private BookProgressDTO currentlyListening;
    private LocalDateTime lastActiveDate;
}
