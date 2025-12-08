package com.audora.app.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Chapter {
    @Id
    private String id = UUID.randomUUID().toString();
    private String title;

    @Override
    public String toString() {
        return "Chapter{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", audioFileKey='" + audioFileKey + '\'' +
                ", duration='" + duration + '\'' +
                '}';
    }

    @Column(name = "duration_int")
    private int duration; // in seconds
    private String audioFileKey; // S3 object key
    private Double lastPlayedSecond; // resume point
    private Boolean completed;       // played glow

    private Integer chapterNumber;

    @ManyToOne
    @JoinColumn(name = "audiobookId")
    @JsonIgnore   // ✅ STOPS infinite recursion
    private Books books;
}
