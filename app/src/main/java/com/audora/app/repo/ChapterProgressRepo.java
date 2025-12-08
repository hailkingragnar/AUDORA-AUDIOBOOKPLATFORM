package com.audora.app.repo;

import com.audora.app.model.ChapterProgress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface ChapterProgressRepo extends JpaRepository<ChapterProgress, String> {

    Optional<ChapterProgress> findByUser_UserIdAndChapter_Id(String userId, String chapterId);

    List<ChapterProgress> findByUser_UserIdAndChapter_Books_AudiobookId(String userId, String bookId);

    @Query("SELECT cp.chapter.id FROM ChapterProgress cp " +
            "WHERE cp.user.id = :userId " +
            "AND cp.chapter.books.audiobookId = :bookId " +
            "AND cp.completed = true")
    Set<String> findCompletedChapterIdsByUserAndBook(
            @Param("userId") String userId,
            @Param("bookId") String bookId
    );

    Long countByUser_UserIdAndChapter_Books_AudiobookIdAndCompletedTrue(
            String userId,
            String bookId
    );

    List<ChapterProgress> findByUser_UserIdAndChapter_Books_AudiobookIdOrderByLastUpdatedAtDesc(
            String userId,
            String bookId
    );

    void deleteByUser_UserIdAndChapter_Books_AudiobookId(String userId, String bookId);

}


