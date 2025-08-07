package com.audora.app.controller;


import com.audora.app.dto.UserLogin;
import com.audora.app.dto.UserRegistration;
import com.audora.app.service.JWTService;
import com.audora.app.service.UsersService;
import com.audora.app.wrapper.ApiWrapper;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class UserController {
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private UsersService usersService;
    @Autowired
    private JWTService jwtService;



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
    public String register(@ModelAttribute("user") UserRegistration users , RedirectAttributes redirectAttributes){

        boolean status = usersService.registerUser(users);
        if (status) {
            redirectAttributes.addFlashAttribute("success", "Registration successful!");
            return "redirect:/login";
        } else {
            redirectAttributes.addFlashAttribute("error", "Error while registering user!");
            return "redirect:/registration";
        }


    }

    @RequestMapping("/")
    public String home(){
        return "Welcome to quizapp Home";
    }


    @RequestMapping("api/v1/authenticate")
    public String login(@ModelAttribute UserLogin user, HttpServletResponse httpServletResponse, RedirectAttributes redirectAttributes) {
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(user.getUserEmail(), user.getUserPassword()));

        if (authentication.isAuthenticated()) {
            String key = jwtService.generateToken(user.getUserEmail());

            if (key != null) {
                Cookie cookie = new Cookie("jwtToken", key);
                cookie.setHttpOnly(true);
                cookie.setPath("/");
                cookie.setMaxAge(60 * 30);
                httpServletResponse.addCookie(cookie);




                redirectAttributes.addFlashAttribute("success", "Login successful!");
                return "redirect:/user/home";

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
    public String getHomePage(){
        return "user/home";
    }


}
