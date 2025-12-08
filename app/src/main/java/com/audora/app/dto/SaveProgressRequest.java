package com.audora.app.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SaveProgressRequest {
    private String chapterId;
    private Integer currentSecond;
    private Integer chapterDurationSeconds; // Optional, for validation
}