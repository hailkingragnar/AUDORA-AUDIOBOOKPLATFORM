package com.audora.app.repo;

import com.audora.app.model.Reviews;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewsRepo extends JpaRepository<Reviews,String>{
}
