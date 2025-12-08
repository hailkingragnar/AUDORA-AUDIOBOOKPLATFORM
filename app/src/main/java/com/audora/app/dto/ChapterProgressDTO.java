package com.audora.app.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

// DTO for Chapter Progress Response
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChapterProgressDTO {
    private Long chapterId;
    private String chapterTitle;
    private Integer lastPlayedSecond;
    private Boolean completed;
    private Integer progressPercentage;
    private LocalDateTime lastUpdatedAt;
}

