package com.audora.app.controller;


import com.audora.app.dto.UserLogin;
import com.audora.app.dto.UserRegistration;
import com.audora.app.model.Authors;
import com.audora.app.model.Books;
import com.audora.app.model.Categories;
import com.audora.app.model.Users;
import com.audora.app.service.AuthorService;
import com.audora.app.service.BooksService;
import com.audora.app.service.JWTService;
import com.audora.app.service.UsersService;
import com.audora.app.utils.SecurityUtils;
import com.audora.app.wrapper.ApiWrapper;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.apache.catalina.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.Banner;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
public class UserController {
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private UsersService usersService;
    @Autowired
    private JWTService jwtService;

    @Autowired
    private SecurityUtils securityUtils;

    @Value("${upload.dir}")
    private String uploadDir;

    @Autowired
    private BooksService booksService;

    @Autowired
    private AuthorService authorService;

    @GetMapping("/login")
    public String loginPage(Model model){
      model.addAttribute("userlogin",new UserLogin());
        return "login";
    }

    @GetMapping("/register")
    public String registerPage(Model model){
        model.addAttribute("user" , new UserRegistration());
        return "registration";
    }


    @PostMapping("/register")
    public String register(@ModelAttribute("user") UserRegistration users , RedirectAttributes redirectAttributes, @RequestParam("profilePicture")MultipartFile file){

        System.out.println("The profile picture is : " + users.getProfilePicture());


        File dir = new File(uploadDir);
        if(!dir.exists()){
            dir.mkdirs();
        }
        String fileName = file.getOriginalFilename();

        //Sanitize
        fileName = fileName.replaceAll("[^a-zA-Z0-9.-]", "_");

        // Set the file path to save
        String filePath = uploadDir + File.separator + fileName;

        Path targetLocation = Path.of(filePath);

        try {
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


        boolean status = usersService.registerUser(users,fileName);
        if (status) {
            redirectAttributes.addFlashAttribute("success", "Registration successful!");
            return "redirect:/login";
        } else {
            redirectAttributes.addFlashAttribute("error", "Error while registering user!");
            return "redirect:/registration";
        }


    }

    @RequestMapping("/")
    public String home(Model model){
        List<Books> books = booksService.getAllBooks();
        List<Categories> categories = booksService.getCategory();
        List<Authors> authors = authorService.getAuthors();
        model.addAttribute("categories",categories);
        model.addAttribute("books",books);
        model.addAttribute("authors",authors);
        return "index";
    }


    @RequestMapping("api/v1/authenticate")
    public String login(@ModelAttribute UserLogin user, HttpSession session, HttpServletResponse httpServletResponse, RedirectAttributes redirectAttributes, Model model) {
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(user.getUserEmail(), user.getUserPassword()));


        if (authentication.isAuthenticated()) {
            Users existingUser = usersService.getUser(user.getUserEmail());
            List<String> roles = existingUser.getRole()
                    .stream()
                    .map(r->r.getRoles().name())
                    .toList();

            String key = jwtService.generateToken(user.getUserEmail(),roles);

            // 3️⃣ Clear any old JWT cookie
            Cookie clearCookie = new Cookie("jwtToken", null);
            clearCookie.setHttpOnly(true);
            clearCookie.setSecure(true); // if using HTTPS
            clearCookie.setPath("/");
            clearCookie.setMaxAge(0); // expires immediately
            httpServletResponse.addCookie(clearCookie);

            if (key != null) {
                Cookie cookie = new Cookie("jwtToken", key);
                cookie.setHttpOnly(true);
                cookie.setPath("/");
                cookie.setMaxAge(60 * 30);
                httpServletResponse.addCookie(cookie);
                redirectAttributes.addFlashAttribute("success", "Login successful!");
                if (roles.contains("ROLE_ADMIN")) {
                    return "redirect:/admin/home";
                } else {
                    return "redirect:/user/home";
                }

            } else {
                redirectAttributes.addFlashAttribute("error", "Invalid Key");
                return "redirect:/login";
            }
        } else {
            redirectAttributes.addFlashAttribute("error", "Invalid Credentials");
            return "redirect:/login";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpServletResponse response, RedirectAttributes redirectAttributes) {
        // Delete the JWT cookie
        Cookie cookie = new Cookie("jwtToken", null);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(0); // Expire immediately
        response.addCookie(cookie);

        redirectAttributes.addFlashAttribute("success", "You have been logged out.");
        return "redirect:/login";
    }

    @GetMapping("/user/home")
    public String getHomePage( Model model){
        String userName = SecurityUtils.getUserEmail();


        List<Books> books = booksService.getAllBooks();
        List<Categories> categories = booksService.getCategory();
        Users user1 = usersService.getUser(userName);

        List<Authors> authors = authorService.getAuthors();
        boolean isAdmin = securityUtils.isAdmin();

        System.out.println("From Home page : "+ user1);
        model.addAttribute("categories",categories);
        model.addAttribute("users",user1);
        model.addAttribute("books",books);
        model.addAttribute("isAdmin",isAdmin);
        model.addAttribute("authors",authors);

        return "user/home";
    }

    @GetMapping("/user/profile")
    public String getProfilePage(Model model){
        String userName = SecurityUtils.getUserEmail();
        Users user1 = usersService.getUser(userName);
        boolean isAdmin = securityUtils.isAdmin();
        model.addAttribute("isAdmin",isAdmin);
        model.addAttribute("users",user1);


        return "user/profile";
    }







}
