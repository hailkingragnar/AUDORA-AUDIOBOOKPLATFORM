package com.audora.app.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChapterProgress {

    @Id
    private String id = UUID.randomUUID().toString();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private Users user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chapter_id", nullable = false)
    private Chapter chapter;

    // Last second where user stopped (for resume)
    @Column(name = "last_played_second", nullable = false)
    private Integer lastPlayedSecond = 0;

    // Is chapter completed (watched 95%+ or till end)
    @Column(nullable = false)
    private Boolean completed = false;

    // When was chapter marked complete
    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    // Total time spent on this chapter (can be more than duration if replayed)
    @Column(name = "total_listening_time_seconds")
    private Integer totalListeningTimeSeconds = 0;

    // How many times user started this chapter
    @Column(name = "play_count")
    private Integer playCount = 0;

    // When user first played this chapter
    @Column(name = "first_played_at")
    private LocalDateTime firstPlayedAt;

    // Last time progress was updated
    @Column(name = "last_updated_at")
    private LocalDateTime lastUpdatedAt;

    // Percentage completed (0-100)
    @Column(name = "progress_percentage")
    private Integer progressPercentage = 0;

    @PrePersist
    protected void onCreate() {
        if (firstPlayedAt == null) {
            firstPlayedAt = LocalDateTime.now();
        }
        lastUpdatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        lastUpdatedAt = LocalDateTime.now();
    }

    // Helper method to mark as complete
    public void markComplete() {
        this.completed = true;
        this.completedAt = LocalDateTime.now();
        this.progressPercentage = 100;
    }

    // Helper method to calculate percentage
    public void calculateProgressPercentage(int chapterDurationSeconds) {
        if (chapterDurationSeconds > 0) {
            this.progressPercentage = Math.min(100,
                    (int) ((lastPlayedSecond * 100.0) / chapterDurationSeconds));
        }
    }
}

