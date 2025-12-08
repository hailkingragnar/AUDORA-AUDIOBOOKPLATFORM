package com.audora.app.controller;

import com.audora.app.dto.AuthorDTO;
import com.audora.app.dto.BooksDTO;
import com.audora.app.model.*;
import com.audora.app.service.AuthorService;
import com.audora.app.service.BooksService;
import com.audora.app.service.CategoryService;
import com.audora.app.service.UsersService;
import com.audora.app.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    public UsersService usersService;

    @Autowired
    public SecurityUtils securityUtils;

    @Autowired
    private BooksService booksService;

    @Value("${book.upload.dir}")
    private String bookUploadDir;

    @Value("${author.upload.dir}")
    private String authorUploadDir;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private AuthorService authorService;

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
        List<Categories> category = categoryService.categoriesList();
        String userName = SecurityUtils.getUserEmail();
        Users user1 = usersService.getUser(userName);
        boolean isAdmin = securityUtils.isAdmin();
        model.addAttribute("isAdmin",isAdmin);
        model.addAttribute("users",user1);
        model.addAttribute("category",category);
        model.addAttribute("chapter",new Chapter());

        return "admin/addbooks";
    }

    @GetMapping("/home")
    public String viewBook(Model model){
        List<Books> book = booksService.getAllBooks();
        model.addAttribute("audiobooks",book);
        String userName = SecurityUtils.getUserEmail();
        Users user1 = usersService.getUser(userName);
        boolean isAdmin = securityUtils.isAdmin();
        model.addAttribute("isAdmin",isAdmin);
        model.addAttribute("users",user1);
        return "admin/home";
    }


    @GetMapping("/addchapter/{id}")
    public String addChapter(@PathVariable("id") String audiobookId,Model model){
        String userName = SecurityUtils.getUserEmail();
        Users user1 = usersService.getUser(userName);
        boolean isAdmin = securityUtils.isAdmin();
        model.addAttribute("isAdmin",isAdmin);
        model.addAttribute("users",user1);
        Books book = booksService.findBookById(audiobookId);
        model.addAttribute("audiobooks",book);
        return "sdf";
    }

    @GetMapping("/addauthor")
    public String addAuthor(Model model){

        String userName = SecurityUtils.getUserEmail();
        Users user1 = usersService.getUser(userName);
        boolean isAdmin = securityUtils.isAdmin();
        model.addAttribute("isAdmin",isAdmin);
        model.addAttribute("users",user1);
        model.addAttribute("author",new AuthorDTO());
        return "admin/author";
    }

    @PostMapping("/addauthor")
    public String addAuthors(@ModelAttribute AuthorDTO authorDTO,  @RequestParam("image") MultipartFile file, RedirectAttributes redirectAttributes) { // "image" matches the input name
        File dir = new File(authorUploadDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }



// Generate a unique filename (e.g., timestamp + random ID + user-specific prefix)
        String fileExtension = StringUtils.getFilenameExtension(file.getOriginalFilename()); // e.g., "png"
        String customFileName = "author_" + System.currentTimeMillis() + "_" + UUID.randomUUID().toString() + "." + fileExtension;

        String filePath = authorUploadDir + File.separator + customFileName;
        Path path = Path.of(filePath);

        try {
            Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
        } catch (Exception e) {
            throw new RuntimeException("Failed to save file: " + e.getMessage());
        }

        boolean status = authorService.addAuthors(authorDTO, customFileName); // Save custom name in DB
        if (status) {
            redirectAttributes.addFlashAttribute("success", "Author added successfully!");
            return "redirect:/admin/addauthor";
        } else {
            redirectAttributes.addFlashAttribute("error", "Failed to add author!");
            return "redirect:/admin/addauthor";
        }

    }


    @GetMapping("/updatebook/{id}")
    public String updateBook(@PathVariable String id, Model model){
        String userName = SecurityUtils.getUserEmail();
        Users user1 = usersService.getUser(userName);
        boolean isAdmin = securityUtils.isAdmin();
        Books books = booksService.findBookById(id);
        BooksDTO bookDto = booksService.bookUpdateDTO(books);
        System.out.println("The chapters are : " + bookDto.getChapters());
        List<Categories> category = books.getCategoriesList();

        model.addAttribute("category",category);
        model.addAttribute("users",user1);
        model.addAttribute("isAdmin",isAdmin);
        model.addAttribute("book",bookDto);

        return "admin/updatebook";
    }

    @PostMapping(value = "/updatebook/{id}" ,  consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public String updateBook(@ModelAttribute BooksDTO booksDTO, @PathVariable String id , @RequestParam("imageFile") MultipartFile file , RedirectAttributes redirectAttributes){

       String fileName = "";

        if(!file.isEmpty()) {
            File dir = new File(bookUploadDir);
            if(!dir.exists()){
                dir.mkdirs();
            }

            System.out.println("inside add book: " + booksDTO.getAuthor() + booksDTO.getCategoryNames() + "The chapters are : " + booksDTO.getChapters());

            String originalName = file.getOriginalFilename();
            String extension = originalName.substring(originalName.lastIndexOf("."));
            fileName = "book_" + System.currentTimeMillis() + "_" + UUID.randomUUID().toString()  + extension;

            //Sanitize
            fileName = fileName.replaceAll("[^a-zA-Z0-9.-]", "_");

            // Set the file path to save
            String filePath = bookUploadDir + File.separator + fileName;

            Path targetLocation = Path.of(filePath);

            try {
                Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                System.out.println("File upload failed for bookId {}: {}" + id + e.getMessage() + e);
                redirectAttributes.addFlashAttribute("errorMessage", "File upload failed. Please try again.");
                return "redirect:/admin/home";
            }
        }
        booksDTO.setAudiobookId(id);
        boolean status = booksService.updateCategoryAndBook(booksDTO,fileName );
        if (status) {
            redirectAttributes.addFlashAttribute("successMessage", "Book added successfully!");
            return "redirect:/admin/home";
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Error while Adding Book");
            return "redirect:/admin/home";
        }

    }
    @DeleteMapping("/deletebook/{id}")
    public ResponseEntity<Map<String, Object>> deleteBook(@PathVariable String id , RedirectAttributes redirectAttributes){
        Map<String, Object> response = new HashMap<>();
        try {
            boolean status =  booksService.deleteBook(id);
            if(status) {
                response.put("success", true);
                response.put("message", "Book deleted successfully.");
                return ResponseEntity.ok(response);
            } else {
                response.put("success", false);
                response.put("message", "Failed to delete book");
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
            }
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Failed to delete book: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }

    }

    @GetMapping("/profile")
    public String getProfile(Model model){
        String userName = SecurityUtils.getUserEmail();
        Users user1 = usersService.getUser(userName);
        boolean isAdmin = securityUtils.isAdmin();
        model.addAttribute("isAdmin",isAdmin);
        model.addAttribute("users",user1);
        return "admin/adminprofile";

    }


}
