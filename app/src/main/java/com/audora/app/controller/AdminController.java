package com.audora.app.controller;

import com.audora.app.dto.BooksDTO;
import com.audora.app.model.Books;
import com.audora.app.model.Categories;
import com.audora.app.model.Chapter;
import com.audora.app.service.BooksService;
import com.audora.app.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.Banner;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private BooksService booksService;

    @Value("${book.upload.dir}")
    private String bookUploadDir;

    @Autowired
    private CategoryService categoryService;

    @PostMapping(value = "/addbook", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public String addBooks(@ModelAttribute BooksDTO booksDTO, @RequestParam("imageFile") MultipartFile file , RedirectAttributes redirectAttributes){
        File dir = new File(bookUploadDir);
        if(!dir.exists()){
            dir.mkdirs();
        }

        System.out.println("inside add book: "+ booksDTO.getAuthor()+booksDTO.getCategoryNames()+"The chapters are : "+ booksDTO.getChapters());

        String fileName = file.getOriginalFilename();

        //Sanitize
        fileName = fileName.replaceAll("[^a-zA-Z0-9.-]", "_");

        // Set the file path to save
        String filePath = bookUploadDir + File.separator + fileName;

        Path targetLocation = Path.of(filePath);

        try {
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        boolean status = booksService.setCategoryandBook(booksDTO,fileName );
        if (status) {
            redirectAttributes.addFlashAttribute("successMessage", "Book added successfully!");
            return "redirect:/admin/addbook";
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Error while Adding Book");
            return "redirect:/admin/addbook";
        }

    }

    @GetMapping("/addbook")
    public String addBook(Model model,RedirectAttributes redirectAttributes){
        model.addAttribute("book",new BooksDTO());
        List<Categories> categories = categoryService.categoriesList();

        System.out.println("The categories list are : " + categories);
        model.addAttribute("categories",categories);
        model.addAttribute("chapter",new Chapter());

        return "admin/addbooks";
    }

    @GetMapping("/home")
    public String viewBook(Model model){
        List<Books> book = booksService.getAllBooks();
        model.addAttribute("audiobooks",book);
        return "admin/home";
    }


    @GetMapping("/addchapter/{id}")
    public String addChapter(@PathVariable("id") String audiobookId,Model model){

        Books book = booksService.findBookById(audiobookId);
        model.addAttribute("audiobooks",book);
        return "sdf";
    }

}
