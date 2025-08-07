package com.audora.app.controller;

import com.audora.app.service.BooksService;
import com.audora.app.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
public class BooksController {

    @Autowired
    private BooksService booksService;

    @Autowired
    private CategoryService categoryService;

//    @ResponseBody
//    @GetMapping("/saveBooks")
//    public String saveBooks(){
//        booksService.saveSampleAudiobooks();
//        return "Books saved";
//    }

    @ResponseBody
    @GetMapping("/addcategory")
    public String addCategory( @RequestParam String bookName,
                               @RequestParam List<String> categoryNames){

        System.out.println(bookName + "Category name are" + categoryNames);
        booksService.setCategoryandBook(bookName,categoryNames);
        return "Successfull";
    }


}
