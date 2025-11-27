package com.audora.app.repo;

import com.audora.app.model.SavedBooks;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

@Transactional
public interface SavedBookRepo extends JpaRepository<SavedBooks,String> {
    @Query(value = "SELECT CASE WHEN COUNT(*) > 0 THEN TRUE ELSE FALSE END FROM saved_books WHERE book_id = :bookId AND user_id = :userId", nativeQuery = true)
    boolean existsByBookIdAndUserId(
                                     @Param("userId") String userId,
                                     @Param("bookId") String bookId
                                    );

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM saved_books WHERE user_id = :userId AND book_id = :bookId", nativeQuery = true)
    void removeBookFromSavedList(String userId, String bookId);

    List<SavedBooks> findByUserUserId(String userId);
}
