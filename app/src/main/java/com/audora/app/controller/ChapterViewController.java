package com.audora.app.controller;

import com.audora.app.model.*;
import com.audora.app.repo.ChapterRepo;
import com.audora.app.repo.*;
import com.audora.app.service.ProgressService;
import com.audora.app.service.UsersService;
import com.audora.app.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.Set;

@Controller
@RequiredArgsConstructor
public class ChapterViewController {

    private final ChapterRepo chapterRepo;

    private final ProgressService progressService;

    @Autowired
    private UsersService usersService;

    @Autowired
    private SecurityUtils securityUtils;

    @GetMapping("/book/viewchapter/{id}")
    public String viewChapter(
            @PathVariable String id,
            Model model) {

        // Get chapter
        Chapter chapter = chapterRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Chapter not found"));

        String userName = SecurityUtils.getUserEmail();
        Users user = usersService.getUser(userName);

        // Get book
        Books book = chapter.getBooks();

        // Get all chapters for this book (ordered)
        List<Chapter> chapters = chapterRepo.findByBooks_audiobookIdOrderByChapterNumberAsc(book.getAudiobookId());

        // Check if user is admin
        boolean isAdmin = securityUtils.isAdmin();

        // Get current chapter progress
        ChapterProgress currentProgress = progressService
                .getChapterProgress(user.getUserId(), id)
                .orElse(null);

        // Get all completed chapter IDs for this book
        Set<String> playedChapterIds = progressService
                .getCompletedChapterIds(user.getUserId(), book.getAudiobookId());

        // Find current chapter index
        int currentChapterIndex = 0;
        for (int i = 0; i < chapters.size(); i++) {
            if (chapters.get(i).getId().equals(id)) {
                currentChapterIndex = i;
                break;
            }
        }

        // Check if has next/previous
        boolean hasNextChapter = currentChapterIndex < chapters.size() - 1;
        boolean hasPrevChapter = currentChapterIndex > 0;

        // Get next and previous chapters
        Chapter nextChapter = hasNextChapter ? chapters.get(currentChapterIndex + 1) : null;
        Chapter prevChapter = hasPrevChapter ? chapters.get(currentChapterIndex - 1) : null;

        // Get book progress for overall stats
        var bookProgress = progressService.getBookProgress(user.getUserId(), book.getAudiobookId());

        // Add to model
        model.addAttribute("currentPlayingChapterId", id);
        model.addAttribute("book", book);
        model.addAttribute("users", user);
        model.addAttribute("chapter", chapter);
        model.addAttribute("chapters", chapters);
        model.addAttribute("isAdmin", isAdmin);

        // Progress data
        model.addAttribute("chapterProgress", currentProgress);
        model.addAttribute("playedChapters", playedChapterIds);

        // Navigation data
        model.addAttribute("currentChapterIndex", currentChapterIndex);
        model.addAttribute("currentChapterNumber", currentChapterIndex + 1);
        model.addAttribute("totalChapters", chapters.size());
        model.addAttribute("hasNextChapter", hasNextChapter);
        model.addAttribute("hasPrevChapter", hasPrevChapter);
        model.addAttribute("nextChapter", nextChapter);
        model.addAttribute("prevChapter", prevChapter);

        // Book progress
        model.addAttribute("bookProgress", bookProgress);
        model.addAttribute("bookProgressPercentage", bookProgress.getProgressPercentage());
        model.addAttribute("completedChaptersCount", bookProgress.getCompletedChapters());

        return "books/chapter";
    }
}