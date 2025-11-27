package com.audora.app.service;

import com.audora.app.model.Books;
import com.audora.app.model.Categories;
import com.audora.app.repo.BooksRepo;
import com.audora.app.repo.CategoriesRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class CategoryService {



    @Autowired
    private CategoriesRepo categoriesRepo;

    public Categories setCategoryName(String categoryName){
        System.out.println("The catgeegory name is : "+categoryName);
        Categories categories = categoriesRepo.findByCategoryName(categoryName).orElseGet(() -> categoriesRepo.save(new Categories(UUID.randomUUID().toString(),categoryName)));
       System.out.println("The category name is : " + categories.categoryName);
        return  categories;
    }
    public List<Categories> categoriesList(){
     return categoriesRepo.findAll();
    }


    public Categories getCategoryById(String id) {
        return categoriesRepo.findById(id).get();
    }

}
