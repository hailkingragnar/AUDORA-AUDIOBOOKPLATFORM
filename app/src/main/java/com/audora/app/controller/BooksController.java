package com.audora.app.controller;

import com.audora.app.dto.ReviewDto;
import com.audora.app.dto.SavedBookDTO;
import com.audora.app.model.*;
import com.audora.app.repo.ReviewsRepo;
import com.audora.app.service.*;
import com.audora.app.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class BooksController {

    @Autowired
    private ReviewsRepo reviewsRepo;

    @Autowired
    private BooksService booksService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private UsersService usersService;

    @Autowired
    private ChapterService chapterService;

    @Autowired
    private ReviewsService reviewsService;

    @Autowired
    private SecurityUtils securityUtils;
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
    public String viewBook(@PathVariable("id") String audiobookId, Model model) {
        String userName = SecurityUtils.getUserEmail();
        Users user1 = usersService.getUser(userName);
        Books book = booksService.findBookById(audiobookId);
        List<Chapter> chapter = chapterService.findChapterById(audiobookId);
        List<Categories> categories = booksService.getCategoriesById(audiobookId);
        boolean isAdmin = securityUtils.isAdmin();
        List<ReviewDto> reviews = reviewsService.getReviews(audiobookId);

        float avgReviewCount = reviews.isEmpty() ? 0 :
                Math.round(
                        (float) reviews.stream()
                                .mapToDouble(ReviewDto::getRating)
                                .average()
                                .orElse(0) * 10
                ) / 10.0f;


        model.addAttribute("isAdmin", isAdmin);
        model.addAttribute("book", book);
        model.addAttribute("users", user1);
        model.addAttribute("chapters", chapter);
        model.addAttribute("categories", categories);
        model.addAttribute("reviews", reviews);
        model.addAttribute("avgReviewCount",avgReviewCount);

        return "books/books";
    }


    @GetMapping("/book/viewchapter/{id}")
    public String viewChapter(@PathVariable("id") String id, Model model) {
        String userName = SecurityUtils.getUserEmail();
        Users user1 = usersService.getUser(userName);
        System.out.println("the Chapter id is : " + id);
        Chapter chapter = chapterService.findChapterByChapterId(id);
        System.out.println("The chapter is : " + chapter);

        Books book = chapter.getBooks();
        List<Chapter> chapters = chapterService.findChapterById(book.getAudiobookId());
        System.out.println("All the chapters are : " + chapters);
        boolean isAdmin = securityUtils.isAdmin();
        model.addAttribute("isAdmin", isAdmin);

        model.addAttribute("currentPlayingChapterId", id);
        model.addAttribute("book", book);
        model.addAttribute("users", user1);
        model.addAttribute("chapter", chapter);
        model.addAttribute("chapters", chapters);


        return "books/chapter";
    }

    @GetMapping("category/books/{id}")
    public String booksInsideCategory(@PathVariable String id, Model model) {
        Categories category = categoryService.getCategoryById(id);
        String userName = SecurityUtils.getUserEmail();
        Users user1 = usersService.getUser(userName);
        boolean isAdmin = securityUtils.isAdmin();
        model.addAttribute("isAdmin", isAdmin);
        List<Categories> categories = booksService.getCategory();
        model.addAttribute("categories", categories);
        model.addAttribute("users", user1);
        model.addAttribute("category", category);

        return "books/category";
    }

    @PostMapping("/user/viewbook/setreview")
    public String setReviews(@RequestBody ReviewDto reviewDTO) {
        try {
            System.out.println("Inside setReview");
            String userName = SecurityUtils.getUserEmail();
            Users user = usersService.getUser(userName);
            Books book = booksService.findBookById(reviewDTO.getBookId());
            Reviews reviews = new Reviews();
            reviews.setRating(reviewDTO.getRating());
            reviews.setComment(reviewDTO.getReview());
            reviews.setBooks(book);
            reviews.setUsers(user);
            reviews.setCreatedAt(LocalDate.now());
            reviewsRepo.save(reviews);

            return "Succesfullly Saved";
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "";
    }

    @GetMapping("/user/viewbook/checkbook")
    public ResponseEntity<Boolean> checkBookIsSaved(
            @RequestParam String userId,
            @RequestParam String bookId
    ) {

        Boolean isSaved = booksService.checkBookByUserIdAndBookId(userId, bookId);
        return ResponseEntity.ok(isSaved);
    }

    @DeleteMapping("/user/viewbook/deletebook")
    public ResponseEntity<String> unsaveBook(
            @RequestBody SavedBookDTO savedBooks
    ){   try {
        booksService.removeBookFromSaved(savedBooks.getUserId(), savedBooks.getBookId());
        return ResponseEntity.ok("Successfully Deleted");
    }catch (Exception e){
        throw new RuntimeException("Failed to delete");
    }
    }

    @PostMapping("/user/viewbook/savebook")
    public ResponseEntity<Map<String,String>> saveBook(@RequestBody SavedBookDTO savedBookDTO){
        try {
            booksService.saveBook(savedBookDTO.getUserId(), savedBookDTO.getBookId());
            Map<String, String> response = new HashMap<>();
            response.put("message", "Book saved successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", "Failed to save book: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }

    }
    @GetMapping("/user/library")
    public String myLibrary(Model model){
        String userName = SecurityUtils.getUserEmail();
        Users user1 = usersService.getUser(userName);
        List<SavedBooks> savedBooks = booksService.getSavedBooksByUserId(user1.getUserId());
        boolean isAdmin = securityUtils.isAdmin();
        List<Categories> categories = categoryService.categoriesList();
        model.addAttribute("categories", categories);
        model.addAttribute("isAdmin", isAdmin);
        model.addAttribute("books",savedBooks);
        model.addAttribute("users", user1);
        return "books/mylibrary";
    }




}

