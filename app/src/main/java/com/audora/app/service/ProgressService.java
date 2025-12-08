package com.audora.app.service;

import com.audora.app.dto.*;
import com.audora.app.model.*;
import com.audora.app.repo.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ProgressService {

    private final ChapterProgressRepo chapterProgressRepo;
    private final BookProgressRepository bookProgressRepo;
    private final ChapterRepo chapterRepo;
    private final BooksRepo bookRepo;
    private final UsersRepo userRepo;

    /**
     * Save chapter progress (called every 2-3 seconds from frontend)
     */
    public ChapterProgress saveChapterProgress(String userId, String chapterId, Integer currentSecond) {
        log.debug("Saving progress: user={}, chapter={}, second={}", userId, chapterId, currentSecond);

        // Validate inputs
        if (currentSecond < 0) {
            throw new IllegalArgumentException("Current second cannot be negative");
        }

        // Get or create progress
        ChapterProgress progress = chapterProgressRepo
                .findByUser_UserIdAndChapter_Id(userId, chapterId)
                .orElseGet(() -> {
                    ChapterProgress newProgress = new ChapterProgress();
                    newProgress.setUser(userRepo.findById(userId)
                            .orElseThrow(() -> new RuntimeException("User not found")));
                    newProgress.setChapter(chapterRepo.findById(chapterId)
                            .orElseThrow(() -> new RuntimeException("Chapter not found")));
                    newProgress.setPlayCount(1);
                    return newProgress;
                });

        // Update progress
        progress.setLastPlayedSecond(currentSecond);

        // Get chapter duration for percentage calculation
        Chapter chapter = progress.getChapter();
        if (chapter != null && chapter.getDuration() > 0) {
            progress.calculateProgressPercentage(chapter.getDuration());

            // Auto-complete if 95% or more watched
            if (progress.getProgressPercentage() >= 95 && !progress.getCompleted()) {
                markChapterComplete(userId, chapterId);
            }
        }

        progress = chapterProgressRepo.save(progress);

        // Update book progress
        updateBookProgress(userId, chapter.getBooks().getAudiobookId(), chapterId);

        return progress;
    }

    /**
     * Mark chapter as complete
     */
    public void markChapterComplete(String userId, String chapterId) {
        log.info("Marking chapter complete: user={}, chapter={}", userId, chapterId);

        ChapterProgress progress = chapterProgressRepo
                .findByUser_UserIdAndChapter_Id(userId, chapterId)
                .orElseGet(() -> {
                    ChapterProgress newProgress = new ChapterProgress();
                    newProgress.setUser(userRepo.findById(userId)
                            .orElseThrow(() -> new RuntimeException("User not found")));
                    newProgress.setChapter(chapterRepo.findById(chapterId)
                            .orElseThrow(() -> new RuntimeException("Chapter not found")));
                    return newProgress;
                });

        if (!progress.getCompleted()) {
            progress.markComplete();
            chapterProgressRepo.save(progress);

            // Update book progress
            Chapter chapter = progress.getChapter();
            updateBookProgressAfterCompletion(userId, chapter.getBooks().getAudiobookId());
        }
    }

    /**
     * Update book progress when chapter is played
     */
    private void updateBookProgress(String userId, String bookId, String lastChapterId) {
        BookProgress bookProgress = bookProgressRepo
                .findByUser_UserIdAndBook_AudiobookId(userId, bookId)
                .orElseGet(() -> {
                    BookProgress newProgress = new BookProgress();
                    newProgress.setUser(userRepo.findById(userId)
                            .orElseThrow(() -> new RuntimeException("User not found")));
                    newProgress.setBook(bookRepo.findById(bookId)
                            .orElseThrow(() -> new RuntimeException("Book not found")));

                    // Set total chapters
                    Long totalChapters = chapterRepo.countByBooks_audiobookId(bookId);
                    newProgress.setTotalChaptersCount(totalChapters.intValue());

                    return newProgress;
                });

        // Update last chapter
        Chapter lastChapter = chapterRepo.findById(lastChapterId)
                .orElseThrow(() -> new RuntimeException("Chapter not found"));
        bookProgress.setLastChapter(lastChapter);

        bookProgressRepo.save(bookProgress);
    }

    /**
     * Update book progress after chapter completion
     */
    private void updateBookProgressAfterCompletion(String userId, String bookId) {
        BookProgress bookProgress = bookProgressRepo
                .findByUser_UserIdAndBook_AudiobookId(userId, bookId)
                .orElseThrow(() -> new RuntimeException("Book progress not found"));

        // Count completed chapters
        Long completedCount = chapterProgressRepo.countByUser_UserIdAndChapter_Books_AudiobookIdAndCompletedTrue(userId, bookId);
        bookProgress.setCompletedChaptersCount(completedCount.intValue());

        // Calculate percentage
        bookProgress.calculateProgressPercentage();

        // Check if book is complete
        bookProgress.checkAndMarkComplete();

        bookProgressRepo.save(bookProgress);
    }

    /**
     * Get chapter progress for user
     */
    public Optional<ChapterProgress> getChapterProgress(String userId, String chapterId) {
        return chapterProgressRepo.findByUser_UserIdAndChapter_Id(userId, chapterId);
    }

    /**
     * Get all completed chapter IDs for a book
     */
    public Set<String> getCompletedChapterIds(String userId, String bookId) {
        return chapterProgressRepo.findCompletedChapterIdsByUserAndBook(userId, bookId);
    }

    /**
     * Get book progress with details
     */
    public BookProgressDTO getBookProgress(String userId, String bookId) {
        // Get book
        Books book = bookRepo.findById(bookId)
                .orElseThrow(() -> new RuntimeException("Book not found"));

        // Get or create book progress
        BookProgress bookProgress = bookProgressRepo
                .findByUser_UserIdAndBook_AudiobookId(userId, bookId)
                .orElse(null);

        // Get chapter count
        Long totalChapters = chapterRepo.countByBooks_audiobookId(bookId);
        Long completedChapters = chapterProgressRepo.countByUser_UserIdAndChapter_Books_AudiobookIdAndCompletedTrue(userId, bookId);

        // Get last played chapter
        ChapterProgress lastChapterProgress = null;
        List<ChapterProgress> recentProgress = chapterProgressRepo.findByUser_UserIdAndChapter_Books_AudiobookIdOrderByLastUpdatedAtDesc(userId, bookId);
        if (!recentProgress.isEmpty()) {
            lastChapterProgress = recentProgress.get(0);
        }

        // Calculate progress percentage
        int progressPercentage = totalChapters > 0
                ? (int) ((completedChapters * 100) / totalChapters)
                : 0;

        return BookProgressDTO.builder()
                .bookId(bookId)
                .bookTitle(book.getTitle())
                .bookImage(book.getImage())
                .totalChapters(totalChapters.intValue())
                .completedChapters(completedChapters.intValue())
                .progressPercentage(progressPercentage)
                .completed(bookProgress != null && bookProgress.getCompleted())
                .lastChapterId(lastChapterProgress != null ? lastChapterProgress.getChapter().getId() : null)
                .lastChapterTitle(lastChapterProgress != null ? lastChapterProgress.getChapter().getTitle() : null)
                .lastPlayedSecond(lastChapterProgress != null ? lastChapterProgress.getLastPlayedSecond() : 0)
                .totalListeningTime(formatDuration(bookProgress != null ? bookProgress.getTotalListeningTimeSeconds() : 0))
                .startedAt(bookProgress != null ? bookProgress.getStartedAt() : null)
                .lastListenedAt(bookProgress != null ? bookProgress.getLastListenedAt() : null)
                .completedAt(bookProgress != null ? bookProgress.getCompletedAt() : null)
                .build();
    }

    /**
     * Get continue listening items for homepage
     */
    public List<ContinueListeningDTO> getContinueListeningBooks(String userId, int limit) {
        List<BookProgress> recentBooks = bookProgressRepo.findRecentlyListenedByUser(userId);

        return recentBooks.stream()
                .filter(bp -> !bp.getCompleted()) // Only in-progress books
                .limit(limit)
                .map(bp -> {
                    Chapter lastChapter = bp.getLastChapter();
                    ChapterProgress chapterProgress = null;

                    if (lastChapter != null) {
                        chapterProgress = chapterProgressRepo
                                .findByUser_UserIdAndChapter_Id(userId, lastChapter.getId())
                                .orElse(null);
                    }

                    return ContinueListeningDTO.builder()
                            .bookId(bp.getBook().getAudiobookId())
                            .bookTitle(bp.getBook().getTitle())
                            .bookImage(bp.getBook().getImage())
                            .author(bp.getBook().getAuthor())
                            .progressPercentage(bp.getProgressPercentage())
                            .resumeChapterId(lastChapter != null ? lastChapter.getId() : null)
                            .resumeChapterTitle(lastChapter != null ? lastChapter.getTitle() : null)
                            .resumeSecond(chapterProgress != null ? chapterProgress.getLastPlayedSecond() : 0)
                            .lastListenedAt(bp.getLastListenedAt())
                            .build();
                })
                .collect(Collectors.toList());
    }

    /**
     * Get user listening statistics
     */
    public UserListeningStatsDTO getUserListeningStats(String userId) {
        Long totalListeningSeconds = bookProgressRepo.getTotalListeningTimeByUser(userId);
        Long completedBooksCount = bookProgressRepo.countByUser_UserIdAndCompletedTrue(userId);
        List<BookProgress> allProgress = bookProgressRepo.findRecentlyListenedByUser(userId);

        int booksStarted = allProgress.size();
        int booksInProgress = (int) allProgress.stream()
                .filter(bp -> !bp.getCompleted())
                .count();

        // Get currently listening book
        BookProgressDTO currentlyListening = null;
        if (!allProgress.isEmpty() && !allProgress.get(0).getCompleted()) {
            BookProgress bp = allProgress.get(0);
            currentlyListening = getBookProgress(userId, bp.getBook().getAudiobookId());
        }

        return UserListeningStatsDTO.builder()
                .userId(userId)
                .totalBooksStarted(booksStarted)
                .totalBooksCompleted(completedBooksCount.intValue())
                .booksInProgress(booksInProgress)
                .totalListeningTime(formatDuration(totalListeningSeconds.intValue()))
                .totalListeningTimeSeconds(totalListeningSeconds.intValue())
                .currentlyListening(currentlyListening)
                .lastActiveDate(allProgress.isEmpty() ? null : allProgress.get(0).getLastListenedAt())
                .build();
    }

    /**
     * Reset progress for a book
     */
    public void resetBookProgress(String userId, String bookId) {
        chapterProgressRepo.deleteByUser_UserIdAndChapter_Books_AudiobookId(userId, bookId);
        bookProgressRepo.findByUser_UserIdAndBook_AudiobookId(userId, bookId)
                .ifPresent(bookProgressRepo::delete);
    }

    /**
     * Format duration in seconds to readable format
     */
    private String formatDuration(Integer seconds) {
        if (seconds == null || seconds == 0) {
            return "0m";
        }

        int hours = seconds / 3600;
        int minutes = (seconds % 3600) / 60;

        if (hours > 0) {
            return String.format("%dh %dm", hours, minutes);
        } else {
            return String.format("%dm", minutes);
        }
    }
}