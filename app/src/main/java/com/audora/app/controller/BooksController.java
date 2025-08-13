package com.audora.app.controller;

import com.audora.app.model.Books;
import com.audora.app.model.Categories;
import com.audora.app.model.Chapter;
import com.audora.app.model.Users;
import com.audora.app.repo.ChapterRepo;
import com.audora.app.service.BooksService;
import com.audora.app.service.CategoryService;
import com.audora.app.service.ChapterService;
import com.audora.app.service.UsersService;
import com.audora.app.utils.SecurityUtils;
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
        String userName = SecurityUtils.getUserEmail();
        Users user1 = usersService.getUser(userName);
        Books book = booksService.findBookById(audiobookId);
        List<Chapter> chapter = chapterService.findChapterById(audiobookId);
        model.addAttribute("book",book);
        model.addAttribute("users",user1);
        model.addAttribute("chapters",chapter);
        return "books/books";
    }
    @GetMapping("/book/viewchapter/{id}")
    public String viewChapter(@PathVariable("id") String id , Model model){
        String userName = SecurityUtils.getUserEmail();
        Users user1 = usersService.getUser(userName);
        System.out.println("the Chapter id is : " + id);
          Chapter chapter = chapterService.findChapterByChapterId(id);
          System.out.println("The chapter is : " + chapter);

          Books book = chapter.getBooks();
          List<Chapter> chapters = chapterService.findChapterById(book.getAudiobookId());
        System.out.println("All the chapters are : "+ chapters);


        model.addAttribute("currentPlayingChapterId",id);
          model.addAttribute("book",book);
          model.addAttribute("users",user1);
          model.addAttribute("chapter",chapter);
        model.addAttribute("chapters",chapters);


        return "books/chapter";
    }

    @GetMapping("category/books/{id}")
    public String booksInsideCategory(@PathVariable String id, Model model){
        Categories category = categoryService.getCategoryById(id);
        String userName = SecurityUtils.getUserEmail();
        Users user1 = usersService.getUser(userName);
        List<Categories> categories = booksService.getCategory();
        model.addAttribute("categories",categories);
        model.addAttribute("users",user1);
        model.addAttribute("category",category);

        return "books/category";
    }


}
