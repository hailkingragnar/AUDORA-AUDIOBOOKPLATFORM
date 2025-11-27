package com.audora.app.repo;

import com.audora.app.model.Reviews;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ReviewsRepo extends JpaRepository<Reviews,String>{

    @Query(value = "SELECT * FROM reviews WHERE book_id = :id", nativeQuery = true)
    List<Reviews> findByBookId(String id);
}
