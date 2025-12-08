package com.audora.app.repo;

import com.audora.app.dto.BookSearchDTO;
import com.audora.app.model.Books;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public interface BooksRepo extends JpaRepository<Books,String> {

    Optional<Books> findByTitle(String s);

    List<Books> findByTitleContainingIgnoreCaseOrAuthorContainingIgnoreCase(String title, String author, Pageable pageable);

    @Query(value = "SELECT * FROM BOOKS WHERE AUTHOR_ID=:id" , nativeQuery = true)
    List<Books> findByAuthor_Id(String id);

}
