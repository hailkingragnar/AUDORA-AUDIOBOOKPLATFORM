package com.audora.app.repo;

import com.audora.app.model.Categories;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface CategoriesRepo extends JpaRepository<Categories,String> {
    Optional<Categories> findByCategoryName(String s);


}
