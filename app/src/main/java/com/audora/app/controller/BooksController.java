package com.audora.app.controller;

import com.audora.app.model.Books;
import com.audora.app.model.Chapter;
import com.audora.app.model.Users;
import com.audora.app.repo.ChapterRepo;
import com.audora.app.service.BooksService;
import com.audora.app.service.CategoryService;
import com.audora.app.service.ChapterService;
import com.audora.app.service.UsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class BooksController {

    @Autowired
    private BooksService booksService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private UsersService usersService;

    @Autowired
    private ChapterService chapterService;
//    @ResponseBody
//    @GetMapping("/saveBooks")
//    public String saveBooks(){
//        booksService.saveSampleAudiobooks();
//        return "Books saved";
//    }



//    @ResponseBody
//    @GetMapping("/addcategory")
//    public String addCategory( @RequestParam String bookName,
//                               @RequestParam List<String> categoryNames){
//
//        System.out.println(bookName + "Category name are" + categoryNames);
//        booksService.setCategoryandBook(bookName,categoryNames);
//        return "Successfull";
//    }



    @GetMapping("/user/viewbook/{id}")
    public String viewBook(@PathVariable("id") String audiobookId, Model model){
        String userName = SecurityContextHolder.getContext().getAuthentication().getName();
        Users user1 = usersService.getUser(userName);
        Books book = booksService.findBookById(audiobookId);
        List<Chapter> chapter = chapterService.findChapterById(audiobookId);
        model.addAttribute("book",book);
        model.addAttribute("users",user1);
        model.addAttribute("chapters",chapter);
        return "books/books";
    }


}
