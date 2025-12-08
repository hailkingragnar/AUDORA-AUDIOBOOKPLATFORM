package com.audora.app.controller;

import com.audora.app.dto.*;
import com.audora.app.model.ChapterProgress;
import com.audora.app.model.Users;
import com.audora.app.service.ProgressService;
import com.audora.app.service.UsersService;
import com.audora.app.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Slf4j
public class ProgressApiController {

    private final ProgressService progressService;
    @Autowired
    private UsersService usersService;

    /**
     * Save chapter progress (called every 2-3 seconds)
     * POST /api/chapters/progress?chapterId=1&second=120
     */
    @PostMapping("/chapters/progress")
    public ResponseEntity<?> saveProgress(
            @RequestParam String chapterId,
            @RequestParam Integer second
           ) {

        try {
            // Validate

            String userName = SecurityUtils.getUserEmail();
            Users user = usersService.getUser(userName);

            if (second < 0) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Invalid second value"));
            }

            // Save progress
            ChapterProgress progress = progressService.saveChapterProgress(
                    user.getUserId(), chapterId, second);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "chapterId", chapterId,
                    "second", second,
                    "progressPercentage", progress.getProgressPercentage()
            ));

        } catch (Exception e) {
            log.error("Error saving progress", e);
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Mark chapter as played/completed
     * POST /api/chapters/mark-played/1
     */
    @PostMapping("/chapters/mark-played/{chapterId}")
    public ResponseEntity<?> markChapterPlayed(
            @PathVariable String chapterId
            ) {

        try {
            String userName = SecurityUtils.getUserEmail();
            Users user = usersService.getUser(userName);
            progressService.markChapterComplete(user.getUserId(), chapterId);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "chapterId", chapterId,
                    "message", "Chapter marked as complete"
            ));

        } catch (Exception e) {
            log.error("Error marking chapter as played", e);
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Get chapter progress
     * GET /api/chapters/1/progress
     */
    @GetMapping("/chapters/{chapterId}/progress")
    public ResponseEntity<?> getChapterProgress(
            @PathVariable String chapterId
           ) {

        String userName = SecurityUtils.getUserEmail();
        Users user = usersService.getUser(userName);
        return progressService.getChapterProgress(user.getUserId(), chapterId)
                .map(progress -> ResponseEntity.ok(Map.of(
                        "chapterId", chapterId,
                        "lastPlayedSecond", progress.getLastPlayedSecond(),
                        "completed", progress.getCompleted(),
                        "progressPercentage", progress.getProgressPercentage(),
                        "lastUpdatedAt", progress.getLastUpdatedAt()
                )))
                .orElse(ResponseEntity.ok(Map.of(
                        "chapterId", chapterId,
                        "lastPlayedSecond", 0,
                        "completed", false,
                        "progressPercentage", 0
                )));
    }

    /**
     * Get completed chapter IDs for a book
     * GET /api/books/1/completed-chapters
     */
    @GetMapping("/books/{bookId}/completed-chapters")
    public ResponseEntity<Set<String>> getCompletedChapters(
            @PathVariable String bookId
          ) {
        String userName = SecurityUtils.getUserEmail();
        Users user = usersService.getUser(userName);
        Set<String> completedIds = progressService.getCompletedChapterIds(
                user.getUserId(), bookId);

        return ResponseEntity.ok(completedIds);
    }

    /**
     * Get book progress with details
     * GET /api/books/1/progress
     */
    @GetMapping("/books/{bookId}/progress")
    public ResponseEntity<BookProgressDTO> getBookProgress(
            @PathVariable String bookId
           ) {
        String userName = SecurityUtils.getUserEmail();
        Users user = usersService.getUser(userName);
        BookProgressDTO progress = progressService.getBookProgress(
                user.getUserId(), bookId);

        return ResponseEntity.ok(progress);
    }

    /**
     * Get continue listening items for homepage
     * GET /api/user/continue-listening?limit=5
     */
    @GetMapping("/user/continue-listening")
    public ResponseEntity<List<ContinueListeningDTO>> getContinueListening(
            @RequestParam(defaultValue = "5") int limit
           ) {

        String userName = SecurityUtils.getUserEmail();
        Users user = usersService.getUser(userName);

        List<ContinueListeningDTO> items = progressService.getContinueListeningBooks(
                user.getUserId(), limit);

        return ResponseEntity.ok(items);
    }

    /**
     * Get user listening statistics
     * GET /api/user/listening-stats
     */
    @GetMapping("/user/listening-stats")
    public ResponseEntity<UserListeningStatsDTO> getListeningStats(
           ) {

        String userName = SecurityUtils.getUserEmail();
        Users user = usersService.getUser(userName);

        UserListeningStatsDTO stats = progressService.getUserListeningStats(
                user.getUserId());

        return ResponseEntity.ok(stats);
    }

    /**
     * Reset book progress (delete all progress)
     * DELETE /api/books/1/progress
     */
    @DeleteMapping("/books/{bookId}/progress")
    public ResponseEntity<?> resetBookProgress(
            @PathVariable String bookId
           ) {

        try {

            String userName = SecurityUtils.getUserEmail();
            Users user = usersService.getUser(userName);
            progressService.resetBookProgress(user.getUserId(), bookId);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Book progress reset successfully"
            ));

        } catch (Exception e) {
            log.error("Error resetting book progress", e);
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", e.getMessage()));
        }
    }
}