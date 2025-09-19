package com.audora.app.repo;

import com.audora.app.model.Authors;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthorsRepo extends JpaRepository<Authors,String> {

}
