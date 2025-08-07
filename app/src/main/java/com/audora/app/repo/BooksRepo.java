package com.audora.app.repo;

import com.audora.app.model.Books;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BooksRepo extends JpaRepository<Books,String> {

    Optional<Books> findByTitle(String s);
}
