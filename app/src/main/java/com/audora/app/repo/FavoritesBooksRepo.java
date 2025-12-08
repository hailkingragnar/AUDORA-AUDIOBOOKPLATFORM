package com.audora.app.repo;

import com.audora.app.model.Favourites;
import com.audora.app.model.SavedBooks;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface FavoritesBooksRepo extends JpaRepository<Favourites,String> {

    @Query(value = "SELECT CASE WHEN COUNT(*) > 0 THEN TRUE ELSE FALSE END FROM Favourites WHERE book_id = :bookId AND user_id = :userId", nativeQuery = true)
    Boolean isFavOrNot(@Param("userId") String userId,
                       @Param("bookId") String bookId);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM favourites WHERE user_id = :userId AND book_id = :bookId", nativeQuery = true)
    void removeBookFromSavedList(@Param("userId") String userId,
                                 @Param("bookId") String bookId);


    List<Favourites> findByUserUserId(String userId);
}
