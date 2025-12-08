package com.audora.app.repo;

import com.audora.app.model.BookProgress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface BookProgressRepository extends JpaRepository<BookProgress, String> {

    // ✅ Correct entity-mapped lookup
    Optional<BookProgress> findByUser_UserIdAndBook_AudiobookId(String userId, String bookId);

    // ✅ In-progress books
    @Query("SELECT bp FROM BookProgress bp " +
            "WHERE bp.user.id = :userId " +
            "AND bp.completed = false " +
            "ORDER BY bp.lastListenedAt DESC")
    List<BookProgress> findInProgressBooksByUser(@Param("userId") String userId);

    // ✅ Completed books
    @Query("SELECT bp FROM BookProgress bp " +
            "WHERE bp.user.id = :userId " +
            "AND bp.completed = true " +
            "ORDER BY bp.completedAt DESC")
    List<BookProgress> findCompletedBooksByUser(@Param("userId") String userId);

    // ✅ Recently listened
    @Query("SELECT bp FROM BookProgress bp " +
            "WHERE bp.user.id = :userId " +
            "ORDER BY bp.lastListenedAt DESC")
    List<BookProgress> findRecentlyListenedByUser(@Param("userId") String userId);

    // ✅ Count completed books
    Long countByUser_UserIdAndCompletedTrue(String userId);

    // ✅ Total listening time
    @Query("SELECT COALESCE(SUM(bp.totalListeningTimeSeconds), 0) FROM BookProgress bp " +
            "WHERE bp.user.id = :userId")
    Long getTotalListeningTimeByUser(@Param("userId") String userId);
}

