package com.audora.app.config;

import com.audora.app.model.Users;
import com.audora.app.repo.UsersRepo;
import com.audora.app.service.JWTService;
import com.audora.app.wrapper.JwtFilter;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;

@Component
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {


    private final UsersRepo usersRepo;
    private OAuth2SuccessHandler(UsersRepo usersRepo){
        this.usersRepo=usersRepo;
    }
    @Autowired
    private JWTService jwtService;


    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();

        String email = oAuth2User.getAttribute("email");
        String name =  oAuth2User.getAttribute("name");

        Users user = usersRepo.findByEmail(email)
                .orElseGet(()-> {
                    Users newUser = new Users();
                    newUser.setName(name);
                    newUser.setEmail(email);
                    return usersRepo.save(newUser);
                });

        String jwtToken = jwtService.generateToken(email);

        // 3️⃣ Clear any old JWT cookie
        Cookie clearCookie = new Cookie("jwtToken", null);
        clearCookie.setHttpOnly(true);
        clearCookie.setSecure(true); // if using HTTPS
        clearCookie.setPath("/");
        clearCookie.setMaxAge(0); // expires immediately
        response.addCookie(clearCookie);

        if (jwtToken != null) {
            Cookie cookie = new Cookie("jwtToken", jwtToken);
            cookie.setHttpOnly(true);
            cookie.setPath("/");
            cookie.setMaxAge(60 * 30);
            response.addCookie(cookie);
            response.sendRedirect("/user/home");

        }

    }
}
