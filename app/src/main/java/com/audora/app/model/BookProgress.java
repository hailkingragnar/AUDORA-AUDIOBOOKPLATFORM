package com.audora.app.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "book_progress",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "book_id"}))
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookProgress {

    @Id
    private String id = UUID.randomUUID().toString();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private Users user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", nullable = false)
    private Books book;

    // Last chapter user was listening to
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "last_chapter_id")
    private Chapter lastChapter;

    // Total chapters completed in this book
    @Column(name = "completed_chapters_count")
    private Integer completedChaptersCount = 0;

    // Total chapters in book (denormalized for quick access)
    @Column(name = "total_chapters_count")
    private Integer totalChaptersCount;

    // Overall progress percentage (0-100)
    @Column(name = "progress_percentage")
    private Integer progressPercentage = 0;

    // Is entire book completed
    @Column(nullable = false)
    private Boolean completed = false;

    // When book was marked complete
    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    // Total listening time for this book (seconds)
    @Column(name = "total_listening_time_seconds")
    private Integer totalListeningTimeSeconds = 0;

    // When user started this book
    @Column(name = "started_at")
    private LocalDateTime startedAt;

    // Last time user listened to this book
    @Column(name = "last_listened_at")
    private LocalDateTime lastListenedAt;

    @PrePersist
    protected void onCreate() {
        if (startedAt == null) {
            startedAt = LocalDateTime.now();
        }
        lastListenedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        lastListenedAt = LocalDateTime.now();
    }

    // Calculate progress percentage
    public void calculateProgressPercentage() {
        if (totalChaptersCount != null && totalChaptersCount > 0) {
            this.progressPercentage = (completedChaptersCount * 100) / totalChaptersCount;
        }
    }

    // Check if book is complete
    public void checkAndMarkComplete() {
        if (completedChaptersCount != null && totalChaptersCount != null
                && completedChaptersCount >= totalChaptersCount) {
            this.completed = true;
            this.completedAt = LocalDateTime.now();
            this.progressPercentage = 100;
        }
    }
}
